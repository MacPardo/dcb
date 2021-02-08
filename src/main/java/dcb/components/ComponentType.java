package dcb.components;

public enum ComponentType {
    OPTIMISTIC,
    PROBABILISTIC_CHECKPOINTS,
    RDT_LGC,
    SPACED_CHECKPOINTS;

    public boolean canSendMessagesTo(ComponentType other) {
        return true;
    }
}
