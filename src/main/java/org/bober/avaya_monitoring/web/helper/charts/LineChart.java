package org.bober.avaya_monitoring.web.helper.charts;

import java.awt.*;
import java.util.*;
import java.util.List;

import com.googlecode.wickedcharts.highcharts.jackson.JsonRenderer;
import com.googlecode.wickedcharts.highcharts.options.*;
import com.googlecode.wickedcharts.highcharts.options.button.Button;
import com.googlecode.wickedcharts.highcharts.options.button.ButtonTheme;
import com.googlecode.wickedcharts.highcharts.options.button.Position;
import com.googlecode.wickedcharts.highcharts.options.color.SimpleColor;
import com.googlecode.wickedcharts.highcharts.options.series.Coordinate;
import com.googlecode.wickedcharts.highcharts.options.series.CoordinatesSeries;
import org.bober.avaya_monitoring.model.entity.CheckResult;
import org.bober.avaya_monitoring.model.entity.Server;

public class LineChart {

    /*
       Using example :
        myOldChartOptions = <%= getChart("myOldChart","oldChart",getTestData()) %>;
        new Highcharts.Chart( myOldChartOptions );
    */

    public static List<CheckResult> getTestData(){
        List<CheckResult> result = new ArrayList<>();

        Server server1 = new Server("Server1","10.0.0.1"),
                server2 = new Server("Server2","10.0.0.2"),
                server3 = new Server("Server3","10.0.0.3");
        List<Integer> s1Values = Arrays.asList(1, 2, 3, 4, 5, 7, null, 5, 4, 3, 2, 1);
        List<Integer> s2Values = Arrays.asList(6, 5, 4, 3, 2, 1, 2, 3, null, 4, 5, 6);
        List<Integer> s3Values = Arrays.asList(3, 4, 5, null, 4, 3, 2, 3, 4, 5, 4, 3);

        long now = new Date().getTime();

        for (int i=0; i<s1Values.size(); i++) {
            CheckResult r1 = new CheckResult();
            r1.setEntity(server1);
            r1.setDate(new Date(now + (i * 10000000)));
            r1.setValue(s1Values.get(i));

            CheckResult r2 = new CheckResult();
            r2.setEntity(server2);
            r2.setDate(new Date(now + (i * 9000000)));
            r2.setValue(s2Values.get(i));

            CheckResult r3 = new CheckResult();
            r3.setEntity(server3);
            r3.setDate(new Date(now + (i * 8000000)));
            r3.setValue(s3Values.get(i));

            result.add( r1 );
            result.add( r2 );
            result.add( r3 );
        }

        return  result;
    }

    public static String getChart(String targetDiv,
                                  String chartName,
                                  List<CheckResult> chartData){
        /* Create options for HighChart instance */
        Options options = new Options();

        options.setTitle(new Title( chartName ));

        Button resetZoomButton = new Button()
                .setPosition(new Position().setX(-80).setY(8))
                .setRelativeTo("chart")
                .setTheme(new ButtonTheme()
                        .setFill(new SimpleColor(Color.WHITE))
                        .setStroke(new SimpleColor(Color.GRAY))
                        .setR(0)
                        .setStates(new StatesChoice()
                                .setHover(new State()
                                        .setFillColor(Color.decode("#69999"))
                                        .setColor(new SimpleColor(Color.WHITE))
                                )
                        )
                );

        options.setChartOptions(
                new ChartOptions()
                        .setType(SeriesType.LINE)
                        .setRenderTo(targetDiv)
                        .setZoomType(ZoomType.X)
                        .setResetZoomButton( resetZoomButton )
                        .setBackgroundColor(Color.decode("#f3f3fa"))
                        .setPlotBackgroundColor( Color.decode("#eaeaef"))
                        .setBorderColor( Color.decode("#330066") )
                        .setBorderRadius( 12 )
                        .setBorderWidth( 1 )
                        .setSpacingRight( 20 )
                        .setSpacingLeft( 5 )
                        .setSpacingTop( 5 )
                        .setSpacingBottom( 5 )
        );

        options.setCredits(new CreditOptions().setEnabled(false));


//        options.setSubtitle(new Title("(tooltip show max/min on current period view)")
//                .setFloating(true)
//                .setY(25)
//                .setStyle(new CssStyle()
//                        .setProperty("color", "#660000")
//                        .setProperty("fontSize", "80%")
//                )
//        );

        PlotLine plotLine = new PlotLine()
                .setValue(Float.valueOf(getChartMaxValue(chartData).getX()))
                .setzIndex(5)
                .setColor(Color.decode("#cc3300"))
                .setDashStyle(GridLineDashStyle.DASH)
                .setWidth(1)
                .setLabel(new Labels("max")
                        .setTextAlign(HorizontalAlignment.RIGHT)
                        .setY(0)
                        .setVerticalAlign(VerticalAlignment.BOTTOM)
                        .setStyle(new CssStyle()
                                .setProperty("color", "#cc3300")
                                .setProperty("fontSize", "80%"))
                );
        List<PlotLine> plotLines = new ArrayList<>();
        plotLines.add(plotLine);

        options.setxAxis(new Axis(AxisType.DATETIME)
                .setMinRange(3600000)
                .setTickPixelInterval(60)
                .setTitle(null)
                .setPlotLines(plotLines)
        );

        options.setyAxis(new Axis()
                .setTitle(null)
                .setLineWidth(1)
                .setTickPixelInterval(40)
                .setMin(0)
                .setShowFirstLabel(false)
                .setEndOnTick(false)
        );


        options.setTooltip(new Tooltip()
                .setxDateFormat("%Y-%m-%d %H:%M:%S")
                .setShared(true)
        );

        options.setLegend(new Legend()
                .setLayout(LegendLayout.HORIZONTAL)
                .setAlign(HorizontalAlignment.CENTER)
                .setVerticalAlign(VerticalAlignment.BOTTOM)
                .setBorderWidth(0)
        );

        options.setPlotOptions( new PlotOptionsChoice()
                .setLine(new PlotOptions()
                        .setLineWidth(2)
                        .setMarker(new Marker(false)
                                .setStates(new StatesChoice()
                                        .setHover(new State()
                                                .setEnabled(true)
                                                .setRadius(4)
                                        ))
                        )
                )
        );

        Map<String, List<Coordinate<Number, Number>>> seriesMap = getSeriesMap(chartData);

        for (String s : seriesMap.keySet()) {
            options.addSeries(new CoordinatesSeries()
                    .setType(SeriesType.LINE)
                    .setName(s)
                    .setData( seriesMap.get( s ) )
            );
        }

        return new JsonRenderer().toJson(options);
    }

    private static Coordinate<Long, Integer> getChartMaxValue
            (List<CheckResult> chartData){
        long maxDate = 0;
        int maxVal = 0;

        for (CheckResult r: chartData) {
            if (r.getValue()!=null && r.getValue()>maxVal){
                maxVal = r.getValue();
                maxDate = getDateInMillisInUtc(r.getDate());
            }
        }

        return new Coordinate<>(maxDate, maxVal);
    }

    private static Map<String,List<Coordinate<Number,Number>>> getSeriesMap
            (List<CheckResult> checkResultList){

        Map<String,List<Coordinate<Number,Number>>> result = new HashMap<>();

        for (CheckResult r : checkResultList) {
            String chartName = (r.getAttributes()==null||r.getAttributes().isEmpty())
                    ? r.getEntity().getName()
                    : r.getEntity().getName() + r.getAttributes() ;

            if (!result.containsKey(chartName)){
                result.put(chartName,new ArrayList<Coordinate<Number, Number>>());
            }

            result.get(chartName).add( new Coordinate<Number, Number>(getDateInMillisInUtc(r.getDate()), r.getValue()) );
        }

        return result;
    }

    /*
     *  This method prepare date in millis for HighChart which operates with datetime values in UTC.
     *
     *  So if we send to chart date in GMT format (default) then chart show date without Timezone offset.
     *
     *  For fix this issue we prepare date in millis, that include a timezone offset.
     *
     */
    private static long getDateInMillisInUtc(final Date date){
        final int timezoneOffsetInMillis = Math.abs(date.getTimezoneOffset())*60*1000;
        return date.getTime() + timezoneOffsetInMillis;
    }

}
