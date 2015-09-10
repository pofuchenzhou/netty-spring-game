package cpgame.demo.domain; 

public enum ERequestType
{
  SOCKET("socket"), HTTP("http"), WEBSOCKET_TEXT("websocket_text"), WEBSOCKET_BINARY("websocket_binary");

  String value;

  private ERequestType(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public static ERequestType parse(String value) {
    for (ERequestType o : values()) {
      if (o.getValue().equals(value)) {
        return o;
      }
    }
    throw new IllegalArgumentException();
  }
}
