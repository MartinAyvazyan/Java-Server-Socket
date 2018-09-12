import java.net.ServerSocket;
import java.net.Socket;

import java.util.Map;
import java.util.HashMap;

import java.io.*;

public class Server {

    public static void main(String... args) throws IOException {
        ServerSocket ss = new ServerSocket(6666);

        while(true) {
            Socket socket = ss.accept();
            new Thread(new ClientProcessor(socket)).start();
        }
    }

}

class ClientRegistry {
    public static Map<String, PrintWriter> clients = new HashMap<>();
}

class ClientProcessor implements Runnable {

    private final Socket socket;

    public ClientProcessor(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        PrintWriter writer = null;

        try {
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(is)
            );

            writer = new PrintWriter(os, true);


            String name = reader.readLine();

            ClientRegistry.clients.put(name, writer);

            System.out.println(name + " connected");

            while(true) {
                String message = reader.readLine();
                String output = name + ": " + message;

                for(String cw : ClientRegistry.clients.keySet()) {
                    if(ClientRegistry.clients.get(cw) == writer) continue;
                    if (message.indexOf(":") != -1 && ClientRegistry.clients.get(message.substring(0,message.indexOf(":")))!=null) {
                      System.out.println("cw " + cw);
                      System.out.println("second " +  message.substring(0,message.indexOf(":")));
                      System.out.println(cw.equals(message.substring(0,message.indexOf(":"))));
                      if (cw.equals(message.substring(0,message.indexOf(":")))){
                        ClientRegistry.clients.get(cw).println(output);
                      }
                    }else ClientRegistry.clients.get(cw).println(output);
                }
            }
        } catch(IOException e) {
            ClientRegistry.clients.remove(writer);
        }
    }

}
