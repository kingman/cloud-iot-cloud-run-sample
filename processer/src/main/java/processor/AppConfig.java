package processor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import legacy.jaxb.Measurements;

@Configuration
public class AppConfig {
    
    @Bean
    public static Unmarshaller measurementsUnMarshaller() throws JAXBException{
        JAXBContext jaxbContext = JAXBContext.newInstance(Measurements.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return unmarshaller;
    }

    @Bean
    public static BigQuery bigQueryService() {
        return BigQueryOptions.getDefaultInstance().getService();
    }
}