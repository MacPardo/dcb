package dcb.core.models;

import java.util.Objects;

public class Checkpoint<State> {
    public final long timestamp;
    public final State state;

    public Checkpoint(long timestamp, State state) {
        this.timestamp = timestamp;
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Checkpoint<?> that = (Checkpoint<?>) o;
        return timestamp == that.timestamp &&
                Objects.equals(state, that.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, state);
    }

    @Override
    public String toString() {
        return "Checkpoint{" +
                "timestamp=" + timestamp +
                ", state=" + state +
                '}';
    }
}
