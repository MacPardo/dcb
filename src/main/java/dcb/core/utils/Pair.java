package dcb.core.utils;

public class Pair<First ,Second> {
    public final First first;
    public final Second second;

    public Pair(First first, Second second) {
        this.first = first;
        this.second = second;
    }
}

/*
public record Pair<First, Second>(First first, Second second) {}
*/
