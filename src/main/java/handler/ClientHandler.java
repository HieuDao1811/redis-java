package handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
  private final Socket clientSocket;

  public ClientHandler(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  @Override 
  public void run() {
    try (InputStream input = clientSocket.getInputStream()) {
      OutputStream output = clientSocket.getOutputStream();

      byte[] buffer = new byte[1024];
      int bytesRead;

      while ((bytesRead = input.read(buffer)) != -1) {
        String request = new String(buffer, 0, bytesRead);

        // *2\r\n$4\r\nECHO\r\n$3\r\nhey\r\n 
        String[] parts = request.split("\r\n");

        boolean responded = false;

        for (int i=0; i<parts.length; i++) {
          String part = parts[i];
          if (part.equalsIgnoreCase("PING")) {
            output.write("+PONG\r\n".getBytes());
            output.flush();
            responded = true;
            break;
          }
          else if (part.equalsIgnoreCase("ECHO") && i+2 < parts.length) {
            String echoMessage = part[i+2];
            String response = "$" + echoMessage.length() + "\r\n" + echoMessage + "\r\n";
            output.write(response.getBytes());
            output.flush();
            responded = true;
            break;
          }
        }

        if (!responded) {
          output.write("+PONG\r\n".getBytes());
          output.flush();
        }
      }
    } catch (IOException e) {
      System.err.println("Client handler error: " + e.getMessage());
    }
    finally {
      try {
        clientSocket.close();
      } catch (IOException e) {
        System.err.println("Client handler error: " + e.getMessage());
      }
    }
  }
}
