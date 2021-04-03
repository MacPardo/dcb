package dcb.core;

import dcb.exceptions.DcbException;
import dcb.core.models.Message;
import dcb.core.models.NetworkAddress;
import dcb.utils.Sender;
import dcb.utils.Pair;

import java.util.Map;

public class MessengerImpl implements Messenger {
    private final Map<Integer, Sender<Message>> localSenders;
    private final Map<Integer, NetworkAddress> remoteAddrs;
    private final Sender<Pair<Message, NetworkAddress>> networkSender;

    public MessengerImpl(Map<Integer, Sender<Message>> localSenders, Map<Integer, NetworkAddress> remoteAddrs, Sender<Pair<Message, NetworkAddress>> networkSender) {
        this.localSenders = localSenders;
        this.remoteAddrs = remoteAddrs;
        this.networkSender = networkSender;
    }

    private void sendWithOption(Message message, boolean localOnly) throws DcbException, InterruptedException {
//        System.out.println("{{Messenger (from=" + message.from + ", to=" + message.to + ", sentTs=" + message.sentTs + ", execTs=" + message.execTs + ")}}");

        Sender<Message> sender = localSenders.getOrDefault(message.to, null);
        if (sender != null) {
//            System.out.println("{{__sender (from=" + message.from + ", to=" + message.to + ", sentTs=" + message.sentTs + ", execTs=" + message.execTs + ")}}");
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
