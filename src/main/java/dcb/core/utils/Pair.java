package dcb.core.utils;

public class Pair<First, Second> {
    public final First first;
    public final Second second;

    public Pair(First first, Second second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
