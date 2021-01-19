package dcb.core.network;

import dcb.core.MessengerImpl;
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
    private final MessengerImpl messenger;

    public Server(int port, MessengerImpl messenger) {
        this.port = port;
        this.messenger = messenger;
    }


    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket client = serverSocket.accept();
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
