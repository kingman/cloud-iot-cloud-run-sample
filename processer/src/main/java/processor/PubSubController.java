package processor;

import java.io.StringReader;
import java.util.Base64;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import legacy.jaxb.Measurements;

@RestController
public class PubSubController {

    @Resource(name = "measurementsUnMarshaller")
    private Unmarshaller measurementsUnMarshaller;

    @Resource
    private Transformer transformer;

    @Resource
    private PubSubPublisher publisher;

    // @Resource
    // private PersistenceService persistenceService;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<String> receiveMessage(@RequestBody Body body) {
        String invalidFormatMsg = "Bad Request: invalid Pub/Sub message format";
        Body.Message message = body.getMessage();
        if (message == null) {
          System.out.println(invalidFormatMsg);
          return new ResponseEntity<>(invalidFormatMsg, HttpStatus.BAD_REQUEST);
        }

        Measurements measurements = null;
        try {
            measurements = (Measurements) measurementsUnMarshaller
                    .unmarshal(new StringReader(decodeBase64String(message.getData())));
            // persistenceService.persist(message.getDeviceId(), transformer.transform(measurements));
        } catch (JAXBException e) {
            System.out.println("Jaxb unmarshall fail");
            return new ResponseEntity<>("Jaxb unmarshall fail", HttpStatus.BAD_REQUEST);
        }

        try {
            publisher.publish(message.getDeviceId(), measurements);
        } catch (Exception e) {
            System.out.println("Publish to normalized topic failed");
        }
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    private static String decodeBase64String(String input) {
        byte[] byteArray = Base64.getDecoder().decode(input);
        return new String(byteArray);
    }
}