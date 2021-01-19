package dcb.core;

import dcb.core.models.Message;
import dcb.exceptions.DcbException;

public interface Messenger {
    void send(Message message) throws DcbException, InterruptedException;

    void sendLocally(Message message) throws DcbException, InterruptedException;
}
