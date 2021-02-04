package dcb.core.synchronization;

import dcb.components.utils.Checkpoint;
import dcb.components.utils.RollbackManager;
import dcb.core.State;
import dcb.core.models.Message;
import dcb.exceptions.InsufficientCheckpointsException;
import dcb.exceptions.InvalidMessageException;
import dcb.exceptions.TimeViolationException;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TestState implements State {
    @Override
    public State copy() {
        return new TestState();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TestState;
    }
}

class RollbackManagerTest {

    @Test
    void saveReceivedMessage() throws TimeViolationException, InvalidMessageException {
        RollbackManager rb = new RollbackManager(1, new TestState());
        Message message = new Message(1, 2, 2, 1, "payload", "route", UUID.randomUUID(), false);
        rb.saveMessage(message);
        ArrayList<Message> aux = new ArrayList<>();
        aux.add(message);
        assertEquals(aux, rb.receivedMessages);
        assertEquals(message.execTs, rb.lvt);
    }

    @Test
    void saveSentMessage() throws TimeViolationException, InvalidMessageException {
        RollbackManager rb = new RollbackManager(1, new TestState());
        Message message = new Message(1, 2, 1, 2, "payload", "route", UUID.randomUUID(), false);
        rb.saveMessage(message);
        List<Message> aux = new ArrayList<>();
        aux.add(message);
        assertEquals(aux, rb.sentMessages);
        assertEquals(message.sentTs, rb.lvt);
    }

    @Test
    void throwsTimeViolationExceptionIfNewReceivedMessageIsYoungerThanLastReceivedMessage() throws TimeViolationException, InvalidMessageException {
        RollbackManager rb = new RollbackManager(1, new TestState());
        Message m1 = new Message(10, 30, 2, 1,  "payload", "route", UUID.randomUUID(), false);
        rb.saveMessage(m1);
        Message m2 = new Message(10, 20, 2, 1,  "payload", "route", UUID.randomUUID(), false);
        assertThrows(TimeViolationException.class, () -> {
            rb.saveMessage(m2);
        });
    }

    @Test
    void throwsTimeViolationExceptionIfNewSentMessageIsYoungerThanLastSentMessage() throws TimeViolationException, InvalidMessageException {
        RollbackManager rb = new RollbackManager(1, new TestState());
        Message m1 = new Message(10, 30, 1, 2,  "payload", "route", UUID.randomUUID(), false);
        rb.saveMessage(m1);
        Message m2 = new Message(5, 40, 1, 2,  "payload", "route", UUID.randomUUID(), false);
        assertThrows(TimeViolationException.class, () -> {
            rb.saveMessage(m2);
        });
    }

    @Test
    void saveMultipleMessages() {

    }

    @Test
    void rollback() throws TimeViolationException, InvalidMessageException, InsufficientCheckpointsException {
        RollbackManager rb = new RollbackManager(1, new TestState());
        Message rec1 = new Message(1 ,10, 2, 1, "rec1", "route", UUID.randomUUID(), false);
        Message rec2 = new Message(2 ,20, 2, 1, "rec2", "route", UUID.randomUUID(), false);
        Message rec3 = new Message(3 ,30, 2, 1, "rec3", "route", UUID.randomUUID(), false);
        Message sent1 = new Message(10 ,100, 1, 2, "sent1", "route", UUID.randomUUID(), false);
        Message sent2 = new Message(20 ,200, 1, 2, "sent2", "route", UUID.randomUUID(), false);
        Message sent3 = new Message(30 ,300, 1, 2, "sent3", "route", UUID.randomUUID(), false);

        rb.takeCheckpoint();
        rb.saveMessage(rec1);
        rb.saveMessage(sent1);
        rb.takeCheckpoint();
        rb.saveMessage(rec2);
        rb.saveMessage(sent2);
        rb.takeCheckpoint();
        rb.saveMessage(rec3);
        rb.saveMessage(sent3);
        rb.takeCheckpoint();

        Set<Message> expectedMustSend = new HashSet<>();
        expectedMustSend.add(sent3);
        expectedMustSend.add(rec3);
        expectedMustSend.add(rec2);
        expectedMustSend.add(sent2);

        List<Checkpoint<TestState>> expectedCheckpointsBefore = new ArrayList<>();
        expectedCheckpointsBefore.add(new Checkpoint<>(0, new TestState()));
        expectedCheckpointsBefore.add(new Checkpoint<>(1, new TestState()));
        expectedCheckpointsBefore.add(new Checkpoint<>(11, new TestState()));
        expectedCheckpointsBefore.add(new Checkpoint<>(21, new TestState()));
        expectedCheckpointsBefore.add(new Checkpoint<>(31, new TestState()));

        List<Checkpoint<TestState>> expectedCheckpointsAfter = new ArrayList<>();
        expectedCheckpointsAfter.add(new Checkpoint<>(0, new TestState()));
        expectedCheckpointsAfter.add(new Checkpoint<>(1, new TestState()));
        expectedCheckpointsAfter.add(new Checkpoint<>(11, new TestState()));

        assertEquals(expectedCheckpointsBefore, rb.checkpoints);

        Set<Message> mustSend = rb.rollback(20);

        assertEquals(11, rb.lvt);
        assertEquals(expectedCheckpointsAfter, rb.checkpoints);
        assertEquals(4, mustSend.size());
        assertTrue(mustSend.contains(sent3.getInverse()));
        assertTrue(mustSend.contains(sent2.getInverse()));
        assertTrue(mustSend.contains(rec3));
        assertTrue(mustSend.contains(rec2));
    }

    @Test
    void free() {
    }

    @Test
    void takeCheckpoint() {
    }

    @Test
    void update() {
    }
}