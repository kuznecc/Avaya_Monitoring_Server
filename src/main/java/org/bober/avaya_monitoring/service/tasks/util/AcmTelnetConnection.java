package org.bober.avaya_monitoring.service.tasks.util;

import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* Логика работы :
* При создании нового экземпляра класса устанавливается телнет-соединение с сервером ACM
* write(cmd) отправляет команду на сервер, а readUntil(pattern) возвращает ответ, принятый от сервера
* с начала выполнения команды, до прихода завершающей последовательности (pattern).
*       ответ в виде строки с удалеными спецсимволами.
*       Конец ответа определяется в аргументах, т.е. до первого ее вхождения в ответ.
* Попытки поиска "шаблона кода" завершающего страницу при многостраничном ответе, чтобы
*       можно было отправлять команды листания страниц с привязкой к нему, успехом не увенчались.
* Единственный найденный вариант результативного листания многостраничного ответа - заранее посмотреть
* какие слова на странице должны быть последними.
*       Если определить заранее последнюю строку нет возможности, то можно использовать тамаут на принятие
*       ответа. Желательно  в связке с наиболее близкой к концу страницы строкой.
*       Фраза "press NEXT PAGE to continue" содержится не в конце ответа и привязываться
*       к ней нельзя.
* Парсинг каждого многостраничного запроса происходит в несколько этапов.
*       выполнениекоманды(write()), прием ответа до указанного фрагмента (readUntil()),
*       запрос следующей страницы(write(NEXT_PAGE)), опять readUntil()
* После завершения работы с телнет-соединением, его нужно закрыть методом disconnect().
*
*/

/**
 * Use this class to behaviour with ACM telnet.
 * Class have public method for make telnet connection and execute prepared AcmTelnet script.
 * Public methods readUntil(...), write(...), nextPage() can be used in AcmTelnetScript logic.
 */
public class AcmTelnetConnection {

    private static final Logger logger = LoggerFactory.getLogger("avayaMonTask");
    private void logAnError(String message, Exception e) {
        logger.error(String.format("%s Error - %s - %s",
                this.getClass().getSimpleName(),
                message,
                e.getClass().getName()
        ));
        System.err.println(e);
    }

    private String telnetServerIp;
    private int telnetServerPort;
    private String telnetLogin;
    private String telnetPassword;
    public void setTelnetServerIp(String telnetServerIp) {
        this.telnetServerIp = telnetServerIp;
    }
    public void setTelnetServerPort(int telnetPort) {
        this.telnetServerPort = telnetPort;
    }
    public void setTelnetLogin(String telnetUser) {
        this.telnetLogin = telnetUser;
    }
    public void setTelnetPassword(String telnetPassword) {
        this.telnetPassword = telnetPassword;
    }

    /**
     * Synchronized method that provide possibility execute only one acmTelnetScripts at the time
     *
     * @param script - AcmTelnetScript than must me executed
     * @param delayAfterDisconnect - delay in milliseconds after script execution finished
     * @return - list of telnet output strings
     */
    private synchronized List<String> executeAvayaTelnetCmd(AcmTelnetScript script, int delayAfterDisconnect)  {
        List<String> result = null;

        connect();

        try {
            if (script.connection==null)
                script.setConnection(this);
            result = script.call();
        } catch (Exception e) {
            logAnError("Can't execute acmTelnetScript("+script.getCmd()+").",e);
        }

        disconnect();

        try {
            Thread.sleep(delayAfterDisconnect);
        } catch (InterruptedException ignore) {}

        return result;
    }

    public synchronized List<String> executeAvayaTelnetCmd(AcmTelnetScript script) {
        return executeAvayaTelnetCmd(script, 500);
    }




    private TelnetClient telnet = new TelnetClient();
    private InputStream in;
    private PrintStream out;

    /* ACM terminal controls */
    private String NEXT_PAGE = (char)27+"[U";
    private String CANCEL = (char)27+"Ow";
    private String SAVE = (char)27+"SB";


    public AcmTelnetConnection(){}

    /**
     * Constructor create new telnet connection with received credentials
     */
    private void connect() {
        try {
            // Connect to the specified server
            telnet.connect( telnetServerIp, telnetServerPort );

            // Get input and output stream references
            in = telnet.getInputStream();
            out = new PrintStream( telnet.getOutputStream() );

            // Logon the user
            readUntil( "login: " );
            write( telnetLogin );
            readUntil( "Password: " );
            write( telnetPassword );
            readUntil( "Terminal Type (513, 715, 4410, 4425, VT220, NTT, W2KTT, SUNT): [513]" );
            write( "513" );

            // Advance to a prompt
            readUntil( "Command: " );
        }
        catch( Exception e ) {
            logAnError("Can't connect to ACM server("+telnetServerIp+").",e);
            disconnect();
        }
    }

    /**
     * This method reading all responses from telnet session and waiting for specified pattern.
     * After this return received response without ESC-symbols
     */
    public String readUntil( String pattern ) {
        try {
            char lastChar = pattern.charAt( pattern.length() - 1 );
            StringBuffer sb = new StringBuffer();

            char ch = ( char )in.read();
            while( true ) {
                //System.out.print(ch);
                sb.append( ch );
                if( ch == lastChar ) {
                    if( sb.toString().endsWith( pattern ) ) {
                        return terminalFilteredOutput(sb);
                    }
                }
                ch = ( char )in.read();
            }
        }
        catch( Exception e ) {
            logAnError("Error during reading server response.(readUntil('"+pattern+"'))",e);
        }
        return null;
    }

    /**
     * This method wait for specified delay (milliseconds) and read all chars from telnet output
     * After this return received response without ESC-symbols
     */
    public String readUntil( int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
        }

        try {
            StringBuffer sb = new StringBuffer();

            while (true){
                int available = in.available();
                if (available == 0) break;
                for (int i=0;i<available;i++){
                    sb.append((char)in.read());
                }
            }
            return terminalFilteredOutput(sb);
        }
        catch( Exception e ) {
            logAnError("Error during reading server response.(readUntil('" + delay + "'))", e);
        }
        return null;
    }
    /* All ASC-characters in the telnet output are will be replaced to '`' */
    private String terminalFilteredOutput( StringBuffer rawOutput){
        List<String> escapeRegExp = new ArrayList<String>(){{
            add("[^\\d\\w\\[\\]\\;\\s\\-`\\:./&?]+");
            add("\\[\\d{1,2};\\d{1,2}[Hm]{0,1}");
            add("\\[\\d{1,2}[bGmJr]");
        }};

        String result = rawOutput.toString();

        for (String regExp : escapeRegExp) {
            result = terminalFilterReplacer(regExp, "`", result);
        }

        result = terminalFilterReplacer("\\s+", " ", result);
        result = terminalFilterReplacer("` `", "`", result);
        result = terminalFilterReplacer("`+", "`", result);

        return result;
    }
    private String terminalFilterReplacer(String regExp, String newValue, String raw){
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(raw);
        return matcher.replaceAll(newValue); // строка с результатом (замена всего найденного на newValue)
    }

    /**
     * Send any command to the current telnet session
     */
    public void write( String value) {
        try {
            out.println( value );
            out.flush();
        }
        catch( Exception e ) {
            logAnError("Error during sending request to server.(write('" + value + "'))", e);
        }
    }

    public void nextPage(){
        write(NEXT_PAGE);
    }

    private void disconnect() {
        try {
            telnet.disconnect();
        }
        catch( Exception e ) {
            logAnError("Error during disconnecting from server("+telnetServerIp+").", e);
        }
    }


}

