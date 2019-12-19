package legacy.jaxb;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
 
@XmlRootElement(name = "bundle-001")
@XmlAccessorType(XmlAccessType.NONE)
public class Measurement {

    @XmlElement(name = "ezkjx-001")
    private Double temperature;

    @XmlElement(name = "iwzyw-012")
    private Double humidity;

    @XmlElement(name = "lyint-006")
    private Double lightLevel;
    
    @XmlElement(name = "wvsas-014")
    private Double pressure;

    @XmlElement(name = "yeicw-007")
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date timestamp;

    public Double getTemperature() {
        return temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public Double getLightLevel() {
        return lightLevel;
    }

    public Double getPressure() {
        return pressure;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public void setLightLevel(Double lightLevel) {
        this.lightLevel = lightLevel;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}