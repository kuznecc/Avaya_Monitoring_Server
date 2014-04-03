package org.bober.avaya_monitoring.service.tasks.util;


import java.util.ArrayList;
import java.util.List;

public class ParserHelper {

    /* parsing int in string like : '"31"', '23 KBytes' */
    public static int parseStringToInt(String s) {
        if (s.endsWith("KBytes")) {
            return Integer.parseInt(
                    s.split(" " )[0].trim()
            );
        }
        if (s.startsWith("\"") || s.endsWith("\"")){
            return Integer.parseInt(
                    s.split("\"" )[1].trim()
            );
        }

        return Integer.parseInt( s.trim() );
    }

    public static List<Integer> parseStringListToInt(List<String> strings) {
        List<Integer> result = new ArrayList<>();

        for (String s : strings) {

            try{
                result.add(parseStringToInt(s));
            } catch (Exception e){
                System.out.println( "val:" + s + " - " + e );
            }
        }

        return result;
    }

}
