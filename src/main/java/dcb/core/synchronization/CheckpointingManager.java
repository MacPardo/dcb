package dcb.core.synchronization;

import dcb.core.utils.Copyable;

public interface CheckpointingManager<State extends Copyable<State>> {
    boolean shouldTakeCheckpoint(RollbackManagerContainer<State> container);
}
