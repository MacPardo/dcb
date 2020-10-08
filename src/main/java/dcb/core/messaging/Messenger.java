package dcb.core.messaging;

import dcb.core.exceptions.DcbException;
import dcb.core.models.Message;
import dcb.core.models.NetworkAddress;
import dcb.core.utils.BlockingQueueSender;
import dcb.core.utils.Pair;

import java.util.Map;

public class Messenger {
    private final Map<Integer, BlockingQueueSender<Message>> localSenders;
    private final Map<Integer, NetworkAddress> remoteAddrs;
    private final BlockingQueueSender<Pair<Message, NetworkAddress>> networkSender;

    public Messenger(Map<Integer, BlockingQueueSender<Message>> localSenders, Map<Integer, NetworkAddress> remoteAddrs, BlockingQueueSender<Pair<Message, NetworkAddress>> networkSender) {
        this.localSenders = localSenders;
        this.remoteAddrs = remoteAddrs;
        this.networkSender = networkSender;
    }

    private void sendWithOption(Message message, boolean localOnly) throws DcbException, InterruptedException {
        BlockingQueueSender<Message> sender = localSenders.getOrDefault(message.to, null);
        if (sender != null) {
            sender.put(message);
            return;
        }

        if (localOnly) throw new DcbException();

        NetworkAddress address = remoteAddrs.getOrDefault(message.to, null);
        if (address == null) {
            throw new DcbException();
        }

        networkSender.put(new Pair<>(message, address));
    }

    public void send(Message message) throws DcbException, InterruptedException {
        sendWithOption(message, false);
    }

    public void sendLocally(Message message) throws DcbException, InterruptedException {
        sendWithOption(message, true);
    }
}
