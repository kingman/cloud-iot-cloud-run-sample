package processor;

import java.util.Map;

public class Body {

  private Message message;

  public Body() {}

  public Message getMessage() {
    return message;
  }

  public void setMessage(Message message) {
    this.message = message;
  }

  public class Message {

    private String messageId;
    private String publishTime;
    private String data;
    private Map<String, String> attributes;

    public Message() {}

    public Message(String messageId, String publishTime, String data, Map<String, String> attributes) {
      this.messageId = messageId;
      this.publishTime = publishTime;
      this.data = data;
      this.attributes = attributes;
    }

    public String getMessageId() {
      return messageId;
    }

    public void setMessageId(String messageId) {
      this.messageId = messageId;
    }

    public String getPublishTime() {
      return publishTime;
    }

    public void setPublishTime(String publishTime) {
      this.publishTime = publishTime;
    }

    public String getData() {
      return data;
    }

    public void setData(String data) {
      this.data = data;
    }

    public Map<String, String> getAttributes() {
      return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
      this.attributes = attributes;
    }
    
    public String getDeviceId() {
      return attributes.get("deviceId");
    }
    
    public String getDeviceNumId() {
      return attributes.get("deviceNumId");
    }

    public String getDeviceRegistryLocation() {
      return attributes.get("deviceRegistryLocation");
    }

    public String getDeviceRegistryId() {
      return attributes.get("deviceRegistryId");
    }

    public String getProjectId() {
      return attributes.get("projectId");
    }
    
    public String getSubFolder() {
      return attributes.get("subFolder");
    }
  }
}