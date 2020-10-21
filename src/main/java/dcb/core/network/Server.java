package dcb.core.network;

import dcb.core.Messenger;
import dcb.core.models.Message;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

@SuppressWarnings({
        "IOResourceOpenedButNotSafelyClosed",
        "InfiniteLoopStatement",
        "SocketOpenedButNotSafelyClosed",
        "CastToConcreteClass"
})
public class Server implements Runnable {
    private final int port;
    private final Messenger messenger;

    public Server(int port, Messenger messenger) {
        this.port = port;
        this.messenger = messenger;
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
                messenger.sendLocally(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
