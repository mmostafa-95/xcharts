import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Xchart {
    public List<TitanicPassenger> getPassengersFromJsonFile() {
        List<TitanicPassenger> allPassengers = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try (InputStream input = new FileInputStream("src/main/resources/titanic_csv.json")) {
            allPassengers = objectMapper.readValue(input, new TypeReference<List<TitanicPassenger>>() {
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return allPassengers;
    }

    public void graphPassengerSurvived(List<TitanicPassenger> passengerList) {
        Map<String, Long> result = passengerList.stream().collect(
                Collectors.groupingBy(
                        TitanicPassenger::getSurvived, Collectors.counting()
                )
        );
        PieChart chart = new PieChartBuilder().width(800).height(600).title(getClass().getSimpleName()).build();
        Color[] sliceColors = new Color[]{new Color(250, 0, 0), new Color(0, 250, 0)};
        chart.getStyler().setSeriesColors(sliceColors);
        chart.addSeries("Survived", result.get("1"));
        chart.addSeries("Unsurvived", result.get("0"));
        new SwingWrapper(chart).displayChart();
    }
    public void graphPassengerAges(List<TitanicPassenger> passengerList) {
        //filter to get an array of passenger ages
        List<Float> pAges = passengerList.stream().map(TitanicPassenger::getAge).limit(8).collect(Collectors.toList());
        List<String> pNames = passengerList.stream().map(TitanicPassenger::getName).limit(8).collect(Collectors.toList());
        String[] names = new String[pNames.size()];
        Float[] ages = new Float[pAges.size()];
        ages = pAges.toArray(ages);
        names = pNames.toArray(names);
        //Using XCart to graph the Ages
        // Create Chart
        CategoryChart chart = new CategoryChartBuilder().width(1024).height(768).title("Age Histogram").xAxisTitle("Names").yAxisTitle("Age").build();
        // Customize Chart
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setHasAnnotations(true);
        chart.getStyler().setStacked(true);
        // Series
        chart.addSeries("Passenger's Ages", pNames, pAges);
        // Show it
        new SwingWrapper(chart).displayChart();
    }
    public void graphPassengerClass(List<TitanicPassenger> passengerList) {
        //filter to get a map of passenger class and total number of passengers in each class
        Map<String, Long> result =
                passengerList.stream().collect(
                        Collectors.groupingBy(
                                TitanicPassenger::getPclass, Collectors.counting()
                        )
                );
        // Create Chart
        PieChart chart = new PieChartBuilder().width(800).height(600).title(getClass().getSimpleName()).build();
        // Customize Chart
        Color[] sliceColors = new Color[]{new Color(180, 68, 50), new Color(130, 105, 120), new Color(80, 143, 160)};
        chart.getStyler().setSeriesColors(sliceColors);
        // Series
        chart.addSeries("First Class", result.get("1"));
        chart.addSeries("Second Class", result.get("2"));
        chart.addSeries("Third Class", result.get("3"));
        // Show it
        new SwingWrapper(chart).displayChart();
    }
    public void graphPassengerGender(List<TitanicPassenger> passengerList) {
        Map<String, Long> result = passengerList.stream().collect(
                Collectors.groupingBy(
                        TitanicPassenger::getSex, Collectors.counting()
                )
        );
        PieChart chart = new PieChartBuilder().width(800).height(600).title(getClass().getSimpleName()).build();
        Color[] sliceColors = new Color[]{new Color(200, 190, 70), new Color(130, 50, 120)};
        chart.getStyler().setSeriesColors(sliceColors);
        chart.addSeries("male", result.get("male"));
        chart.addSeries("female", result.get("female"));
        new SwingWrapper(chart).displayChart();


    }
        public static void main(String[] args) {
            Xchart xchart = new Xchart();
            List<TitanicPassenger> allPassengers = xchart.getPassengersFromJsonFile();
            xchart.graphPassengerAges(allPassengers);
            xchart.graphPassengerClass(allPassengers);
            try {
                System.in.read();
            } catch (IOException ex) {
                Logger.getLogger(Xchart.class.getName()).log(Level.SEVERE, null, ex);
            }
            xchart.graphPassengerGender(allPassengers);
            xchart.graphPassengerSurvived(allPassengers);

        }
}
