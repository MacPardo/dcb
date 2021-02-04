package dcb.components.utils;

import dcb.core.State;
import dcb.exceptions.InsufficientCheckpointsException;
import dcb.exceptions.InvalidMessageException;
import dcb.exceptions.TimeViolationException;
import dcb.core.models.Message;

import java.util.*;

public class RollbackManager {
    public static final int INITIAL_CAPACITY = 16;
    public State state;
    public long lvt = 0L;
    public final int id;
    public final List<Checkpoint<State>> checkpoints = new ArrayList<>(INITIAL_CAPACITY);
    public final List<Message> receivedMessages = new ArrayList<>(INITIAL_CAPACITY);
    public final List<Message> sentMessages = new ArrayList<>(INITIAL_CAPACITY);

    public State getState() {
        return state.copy();
    }

    public long getLvt() {
        return lvt;
    }

    public List<Checkpoint<State>> getCheckpoints() {
        return Collections.unmodifiableList(checkpoints);
    }

    public List<Message> getReceivedMessages() {
        return Collections.unmodifiableList(receivedMessages);
    }

    public List<Message> getSentMessages() {
        return Collections.unmodifiableList(sentMessages);
    }

    private static <T> T last(List<T> list) {
        if (list.isEmpty()) //noinspection ReturnOfNull
            return null;
        return list.get(list.size() - 1);
    }
    private static <T> void removeLast(List<T> list) {
        list.remove(list.size() - 1);
    }

    public RollbackManager(int newId, State initialState) {
        state = initialState;
        id = newId;
        checkpoints.add(new Checkpoint<>(0L, initialState.copy()));
    }

    private void printPrefix() {
        if (id == 1) System.out.print("RB(" + id + ") - ");
    }

    public void saveMessage(Message message) throws InvalidMessageException, TimeViolationException {
        printPrefix();
        if (id == 1) System.out.println("saving message: " +  message);

        if (message.from != id && message.to != id || message.isAnti) {
            throw new InvalidMessageException();
        }

        if (message.from == id) {
            Message lastMessage = last(sentMessages);
            if (lastMessage != null && lastMessage.sentTs > message.sentTs) {
                throw new TimeViolationException();
            }
            lvt = Math.max(lvt, message.sentTs);
            sentMessages.add(message);
        } else {
            Message lastMessage = last(receivedMessages);
            if (lastMessage != null && lastMessage.execTs > message.execTs) {
                printPrefix();
                if (id == 1) {
                    System.out.println("is throwing a TimeViolationException");
                    System.out.println("last_exec_ts = " + lastMessage.execTs);
                    System.out.println("recv_exec_ts = " + message.execTs);
                }
                throw new TimeViolationException();
            }
            lvt = Math.max(lvt, message.execTs);
            receivedMessages.add(message);
        }
    }

    @SuppressWarnings("OverlyLongMethod")
    public Set<Message> rollback(long timestamp) throws TimeViolationException, InsufficientCheckpointsException {
        printPrefix();
        if (id == 1) System.out.println("rolling back: " + timestamp);

        Set<Message> toBeSent = new HashSet<>(INITIAL_CAPACITY);

        if (timestamp > lvt) {
            throw new TimeViolationException();
        }

        Checkpoint<State> firstCheckpoint = checkpoints.get(0);
        if (firstCheckpoint == null || firstCheckpoint.timestamp > timestamp) {
            throw new InsufficientCheckpointsException();
        }

        while (!checkpoints.isEmpty()) {
            Checkpoint<State> lastCheckpoint = last(checkpoints);
            assert lastCheckpoint != null;
            if (lastCheckpoint.timestamp > timestamp) {
                removeLast(checkpoints);
            } else {
                this.lvt = lastCheckpoint.timestamp;
                this.state = lastCheckpoint.state;
                break;
            }
        }

        while (!receivedMessages.isEmpty()) {
            Message lastMessage = last(receivedMessages);
            printPrefix();
            System.out.println("last rec msg is " + lastMessage);

            assert lastMessage != null;
            if (lastMessage.execTs > lvt) {
                System.out.println("removing...");
                removeLast(receivedMessages);
                toBeSent.add(lastMessage);
            } else {
                break;
            }
        }

        while (!sentMessages.isEmpty()) {
            final Message lastMessage = last(sentMessages);
            assert lastMessage != null;

            printPrefix();
            System.out.println("last sent msg is " + lastMessage);
            if (lastMessage.sentTs > lvt) {
                System.out.println("removing...");
                toBeSent.add(lastMessage.getInverse());
                removeLast(sentMessages);
            } else {
                break;
            }

        }

        return toBeSent;
    }

    public void free(long timestamp) {
        while (!checkpoints.isEmpty()) {
            Checkpoint<State> firstCheckpoint = checkpoints.get(0);
            if (firstCheckpoint.timestamp > timestamp) {
                break;
            }
            checkpoints.remove(0);
        }

        while (!receivedMessages.isEmpty()) {
            Message firstMessage = receivedMessages.get(0);
            if (firstMessage.execTs > timestamp) {
                break;
            }
            receivedMessages.remove(0);
        }

        while (!sentMessages.isEmpty()) {
            Message firstMessage = sentMessages.get(0);
            if (firstMessage.sentTs > timestamp) {
                break;
            }
            sentMessages.remove(0);
        }
    }

    public void takeCheckpoint() {
        printPrefix();
        if (id == 1) System.out.println("taking checkpoint at " + lvt);

        lvt += 1L;
        checkpoints.add(new Checkpoint<>(lvt, state.copy()));
    }

    public void update(State newState, long newLvt) throws TimeViolationException {
        printPrefix();
        if (id == 1) System.out.println("updating from (state1, " + lvt + ") to (state2, " + newLvt + ")");

        if (newLvt < lvt) {
            throw new TimeViolationException();
        }
        state = newState;
        lvt = newLvt;
    }

    @Override
    public String toString() {
        return "RollbackManager{" +
                "state=" + state +
                ", lvt=" + lvt +
                ", id=" + id +
                ", checkpoints=" + checkpoints +
                ", receivedMessages=" + receivedMessages +
                ", sentMessages=" + sentMessages +
                '}';
    }
}
