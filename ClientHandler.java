import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

//Client Handler is a class that is used to make sure that clients can run concurrently
public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final MessageManager messageManager;

    public ClientHandler(Socket socket, MessageManager messageManager) {
        this.clientSocket = socket;
        this.messageManager = messageManager;
    }

    //This is the run method, this does the actual processing that the server should do for running with the client, 
    //the server calls
    //the start() method for threads inside it.
    @Override
    public void run() {
        try (
                BufferedReader bfr = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String inputFromClient;

            while ((inputFromClient = bfr.readLine()) != null) {
                if ("exit".equals(inputFromClient)) {
                    break;
                }

                String outputToClient = MainServer.processCommand(inputFromClient, messageManager);
                pw.println(outputToClient);
                pw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
