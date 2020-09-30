package dcb.core.synchronization;

import dcb.core.exceptions.InsufficientCheckpointsException;
import dcb.core.exceptions.InvalidMessageException;
import dcb.core.exceptions.TimeViolationException;
import dcb.core.models.Checkpoint;
import dcb.core.models.Message;
import dcb.core.utils.Copyable;

import java.util.*;

public class RollbackManager<State extends Copyable<State>> {
    private static final int INITIAL_CAPACITY = 16;
    private State state;
    private long lvt = 0L;
    private final int id;
    private final List<Checkpoint<State>> checkpoints = new ArrayList<>(INITIAL_CAPACITY);
    private final List<Message> receivedMessages = new ArrayList<>(INITIAL_CAPACITY);
    private final List<Message> sentMessages = new ArrayList<>(INITIAL_CAPACITY);

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
        return list.get(list.size() - 1);
    }

    private static <T> void removeLast(List<T> list) {
        list.remove(list.size() - 1);
    }

    public RollbackManager(int newId, State initialState) {
        state = initialState;
        id = newId;
        checkpoints.add(new Checkpoint<>(0, initialState.copy()));
    }


    public void saveMessage(Message message) throws InvalidMessageException, TimeViolationException {
        if (message.from != id && message.to != id || message.isAnti) {
            throw new InvalidMessageException();
        }

        if (message.from == id) {
            Message lastMessage = last(sentMessages);
            if (lastMessage != null && lastMessage.sentTs > message.sentTs) {
                throw new TimeViolationException();
            }
            sentMessages.add(message);
        } else {
            Message lastMessage = last(receivedMessages);
            if (lastMessage != null && lastMessage.execTs > message.execTs) {
                throw new TimeViolationException();
            }
            receivedMessages.add(message);
        }
    }

    public Set<Message> rollback(long timestamp) throws TimeViolationException, InsufficientCheckpointsException, InvalidMessageException {
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
                lvt = lastCheckpoint.timestamp;
                state = lastCheckpoint.state;
                break;
            }
        }

        while (!receivedMessages.isEmpty()) {
            Message lastMessage = last(receivedMessages);
            assert lastMessage != null;
            if (lastMessage.execTs > timestamp) {
                break;
            }
            removeLast(receivedMessages);
            toBeSent.add(lastMessage);
        }

        while (!sentMessages.isEmpty()) {
            final Message lastMessage = last(sentMessages);
            assert lastMessage != null;
            if (lastMessage.sentTs > timestamp) {
                break;
            }
            toBeSent.add(lastMessage.getAnti());
            removeLast(sentMessages);
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
        lvt += 1L;
        checkpoints.add(new Checkpoint<>(lvt, state.copy()));
    }

    public void update(State newState, long newLvt) throws TimeViolationException {
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
