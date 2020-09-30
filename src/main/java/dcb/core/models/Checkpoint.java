package dcb.core.models;

public record Checkpoint<State>(long timestamp, State state) {}
