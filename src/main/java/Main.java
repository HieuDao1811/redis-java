import java.io.IOException;


import server.RedisServer;

public class Main {
  public static void main(String[] args) throws IOException {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    int port = 6379;
    RedisServer server = new RedisServer(port);
    server.start();
  }
}
