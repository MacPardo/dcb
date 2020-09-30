package dcb.core.network;

import dcb.core.models.Message;
import dcb.core.utils.BlockingQueueSender;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    private final int port;
    private final BlockingQueueSender<Message> queue;

    public Server(int port, BlockingQueueSender<Message> queue) {
        this.port = port;
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Client connected: " + client.getInetAddress().getHostAddress());
                ObjectInputStream inputStream = new ObjectInputStream(client.getInputStream());
                Message message = (Message) inputStream.readObject();
                inputStream.close();
                queue.put(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
