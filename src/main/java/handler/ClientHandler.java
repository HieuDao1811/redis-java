package handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import protocol.RespEncoder;
import storage.DataStore;

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
        // *2\r\n$4\r\nECHO\r\n$3\r\nhey\r\n
        String request = new String(buffer, 0, bytesRead); 
        String[] parts = request.split("\r\n");

        for (int i=0; i<parts.length; i++) {
          String part = parts[i];
          if (part.equalsIgnoreCase("PING")) {
            output.write(RespEncoder.simpleString("PONG").getBytes());
            output.flush();
            break;
          }
          else if (part.equalsIgnoreCase("ECHO") && i+2 < parts.length) {
            String echoMessage = parts[i+2];
            String response = "$" + echoMessage.length() + "\r\n" + echoMessage + "\r\n";
            output.write(RespEncoder.bulkString(response).getBytes());
            output.flush();
            break;
          } 
          else if (part.equalsIgnoreCase("SET") && i+4 < parts.length) {
            String key = parts[i+2];
            String value = parts[i+4];

            DataStore.set(key, value);

            output.write(RespEncoder.simpleString("OK").getBytes()); 
            output.flush();
            break;
          }
          else if (part.equalsIgnoreCase("GET") && i+2 < parts.length) {
            String key = parts[i+2];
            String value = DataStore.get(key);

            if (value != null) {
              String response = "$" + value.length() + "\r\n" + value + "\r\n";
              output.write(RespEncoder.bulkString(response).getBytes());
            }
            else {
              String response = "$-1\r\n";
              output.write(RespEncoder.nullBulkString().getBytes());
            }
            output.flush();
            break;
          }
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
