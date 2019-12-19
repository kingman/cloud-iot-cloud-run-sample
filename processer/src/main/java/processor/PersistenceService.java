package processor;

import java.util.Map;

import javax.annotation.Resource;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.InsertAllRequest;
import com.google.cloud.bigquery.InsertAllResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PersistenceService {

    @Resource(name = "bigQueryService")
    private BigQuery bigQueryService;

    @Value("${DATASET}")
    private String dataset;

    @Value("${TABLE}")
    private String table;

    public void persist(String deviceId, Map<String, Object> row) {
        row.put("device", deviceId);
        InsertAllRequest insertRequest = InsertAllRequest.newBuilder(dataset, table)
        .addRow(row)
        .build();
        InsertAllResponse response = bigQueryService.insertAll(insertRequest);
        if (response.hasErrors()) {
            System.out.println("Fail to insert to BigQuery");
        }
    }
}