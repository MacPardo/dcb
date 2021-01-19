package dcb.core.network;

import dcb.core.models.Message;
import dcb.core.models.NetworkAddress;
import dcb.utils.Receiver;
import dcb.utils.Pair;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"InfiniteLoopStatement", "IOResourceOpenedButNotSafelyClosed", "SocketOpenedButNotSafelyClosed"})
public class Client implements Runnable {
    private final Receiver<Pair<Message, NetworkAddress>> receiver;

    public Client(Receiver<Pair<Message, NetworkAddress>> receiver) {
        this.receiver = receiver;
    }

    @Override
    public void run() {
        try {
            while (true) {
                final var pair = receiver.poll(1L, TimeUnit.DAYS);
                final var message = pair.first;
                final var address = pair.second;
                final var client = new Socket(address.host, address.port);
                final var outputStream = new ObjectOutputStream(client.getOutputStream());
                outputStream.writeObject(message);
                outputStream.close();
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
