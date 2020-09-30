package dcb.core.synchronization;

import dcb.core.models.Checkpoint;
import dcb.core.models.Message;
import dcb.core.utils.Copyable;

import java.util.List;

public class RollbackManagerContainer<State extends Copyable<State>> {
    private final RollbackManager<State> rollbackManager;

    public RollbackManagerContainer(RollbackManager<State> rollbackManager) {
        this.rollbackManager = rollbackManager;
    }

    public State getState() {
        return rollbackManager.getState();
    }

    public long getLvt() {
        return rollbackManager.getLvt();
    }

    public List<Checkpoint<State>> getCheckpoints() {
        return rollbackManager.getCheckpoints();
    }

    public List<Message> getReceivedMessages() {
        return rollbackManager.getReceivedMessages();
    }

    public List<Message> getSentMessages() {
        return rollbackManager.getSentMessages();
    }
}
