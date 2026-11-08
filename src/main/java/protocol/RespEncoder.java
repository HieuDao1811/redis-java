package protocol;

public class RespEncoder {
  public static String bulkString(String content) {
    if (content == null) {
      return nullBulkString();
    }
    return "$" + content.length() + "\r\n" + content + "\r\n";
  }

  public static String nullBulkString() {
    return "$-1\r\n";
  }

  public static String simpleString(String message) {
    return "+" + message + "\r\n";
  }
}
