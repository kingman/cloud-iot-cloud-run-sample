package processor;

import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;

import com.google.api.client.util.DateTime;

import org.springframework.stereotype.Component;

import legacy.jaxb.Measurement;
import legacy.jaxb.Measurements;

@Component
public class Transformer {

    public Map<String, Object> transform(Measurements measurements) {
        List<Measurement> measurementList = measurements.getMeasurements();

        Map<String, Object> row = new HashMap<>();
        
        Date intervalStart = measurementList.stream().map(m->m.getTimestamp()).min(Date::compareTo).get();
        Date intervalEnd = measurementList.stream().map(m->m.getTimestamp()).max(Date::compareTo).get();
        
        row.put("interval_start", new DateTime(intervalStart));
        row.put("interval_end", new DateTime(intervalEnd));
        row.put("temperature", extractSummaryStatisticsFor(measurements, m->m.getTemperature()));
        row.put("pressure", extractSummaryStatisticsFor(measurements, m->m.getPressure()));
        row.put("light_level", extractSummaryStatisticsFor(measurements, m->m.getLightLevel()));
        row.put("humidity", extractSummaryStatisticsFor(measurements, m->m.getHumidity()));

        return row;
    }

    private Map<String, Object> extractSummaryStatisticsFor(Measurements measurements, ToDoubleFunction<Measurement> mapper) {
        DoubleSummaryStatistics summary = measurements.getMeasurements().stream().mapToDouble(mapper).summaryStatistics();
        Map<String, Object> summaryMap = new HashMap<>();
        summaryMap.put("max", summary.getMax());
        summaryMap.put("min", summary.getMin());
        summaryMap.put("avg", summary.getAverage());
        return summaryMap;
    }
}