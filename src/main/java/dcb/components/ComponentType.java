package dcb.components;

public enum ComponentType {
    OPTIMISTIC,
    SILENT_OPTIMISTIC,
    PROBABILISTIC_CHECKPOINTS,
    RDT_LGC;

    public boolean canSendMessagesTo(ComponentType other) {
        return true;
    }
}
