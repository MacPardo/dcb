package dcb.core;

import dcb.core.models.Message;
import dcb.core.models.BehaviorMessage;
import dcb.exceptions.DcbException;

public interface Translator {
    Message translate(BehaviorMessage behaviorMessage, long sentTs) throws DcbException;
}
