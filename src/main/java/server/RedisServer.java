package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import handler.ClientHandler;

public class RedisServer {
  private final int port;

  public RedisServer(int port) {
    this.port = port;
  }

  public void start() {
    try (ServerSocket serverSocket = new ServerSocket(port)) {
      serverSocket.setReuseAddress(true);
      
      while (true) {
        Socket clientSocket = serverSocket.accept();
        new Thread(() -> new ClientHandler(clientSocket));
      }
    } catch (IOException e) {
      System.err.println("Server error: " + e.getMessage());
    }
  }
}
