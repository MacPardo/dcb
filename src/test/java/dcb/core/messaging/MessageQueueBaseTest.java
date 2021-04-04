package dcb.core.messaging;

import dcb.components.utils.MessageQueueBase;
import dcb.core.models.Message;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MessageQueueBaseTest {

    @Test
    void pushRegularMessageWithoutAntiMessages() {
        MessageQueueBase q = new MessageQueueBase();
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        q.push(new Message(1L, 2L, 1, 2, "", "", uuid1, false));
        assertEquals(q.regularQueue.size(), 1);
        assertEquals(q.antiQueue.size(), 0);
        q.push(new Message(1L, 2L, 1, 2, "", "", uuid2, false));
        assertEquals(q.regularQueue.size(), 2);
        assertEquals(q.antiQueue.size(), 0);
    }

    @Test
    void pushRegularMessageWithUnrelatedAntiMessages() {
        MessageQueueBase q = new MessageQueueBase();
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        UUID uuid3 = UUID.randomUUID();
        q.push(new Message(1L, 2L, 1, 2, "", "", uuid1, true));
        assertEquals(q.antiQueue.size(), 1);
        assertEquals(q.regularQueue.size(), 0);
        q.push(new Message(1L, 2L, 1, 2, "", "", uuid2, false));
        assertEquals(q.antiQueue.size(), 1);
        assertEquals(q.regularQueue.size(), 1);
        q.push(new Message(1L, 2L, 1, 2, "", "", uuid3, false));
        assertEquals(q.antiQueue.size(), 1);
        assertEquals(q.regularQueue.size(), 2);
    }

    @Test
    void pushRegularMessageWithTargetAntiMessage() {
        MessageQueueBase q = new MessageQueueBase();
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        q.push(new Message(1L, 2L, 1, 2, "", "", uuid1, true));
        assertEquals(q.antiQueue.size(), 1);
        assertEquals(q.regularQueue.size(), 0);
        q.push(new Message(1L, 2L, 1, 2, "", "", uuid1, false));
        assertEquals(q.antiQueue.size(), 0);
        assertEquals(q.regularQueue.size(), 0);
        q.push(new Message(1L, 2L, 1, 2, "", "", uuid2, false));
        assertEquals(q.antiQueue.size(), 0);
        assertEquals(q.regularQueue.size(), 1);
    }

    @Test
    void pushAntiMessageWithoutRegularMessages() {
        MessageQueueBase q = new MessageQueueBase();
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        q.push(new Message(1L, 2L, 1, 2, "", "", uuid1, true));
        assertEquals(q.regularQueue.size(), 0);
        assertEquals(q.antiQueue.size(), 1);
        q.push(new Message(1L, 2L, 1, 2, "", "", uuid2, true));
        assertEquals(q.regularQueue.size(), 0);
        assertEquals(q.antiQueue.size(), 2);
    }

    @Test
    void pushAntiMessageWithUnrelatedRegularMessages() {
        MessageQueueBase q = new MessageQueueBase();
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        UUID uuid3 = UUID.randomUUID();
        q.push(new Message(1L, 2L, 1, 2, "", "", uuid1, false));
        assertEquals(q.regularQueue.size(), 1);
        assertEquals(q.antiQueue.size(), 0);
        q.push(new Message(1L, 2L, 1, 2, "", "", uuid2, true));
        assertEquals(q.regularQueue.size(), 1);
        assertEquals(q.antiQueue.size(), 1);
        q.push(new Message(1L, 2L, 1, 2, "", "", uuid3, true));
        assertEquals(q.regularQueue.size(), 1);
        assertEquals(q.antiQueue.size(), 2);
    }

    @Test
    void pushAntiMessageWithTargetRegularMessage() {
        MessageQueueBase q = new MessageQueueBase();
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        q.push(new Message(1L, 2L, 1, 2, "", "", uuid1, false));
        assertEquals(q.regularQueue.size(), 1);
        assertEquals(q.antiQueue.size(), 0);
        q.push(new Message(1L, 2L, 1, 2, "", "", uuid2, true));
        assertEquals(q.regularQueue.size(), 1);
        assertEquals(q.antiQueue.size(), 1);
        q.push(new Message(1L, 2L, 1, 2, "", "", uuid1, true));
        assertEquals(q.regularQueue.size(), 0);
        assertEquals(q.antiQueue.size(), 1);
    }

    @Test
    void peekTimestampWithoutRegularOrAntiMessages() {
        MessageQueueBase q = new MessageQueueBase();
        assertNull(q.peekTimestamp());
    }

    @Test
    void peekTimestampWithOnlyRegularMessages() {
        MessageQueueBase q = new MessageQueueBase();
        q.push(new Message(5L, 20L, 1, 2, "", "", UUID.randomUUID(), false));
        q.push(new Message(8L, 15L, 1, 2, "", "", UUID.randomUUID(), false));
        q.push(new Message(8L, 25L, 1, 2, "", "", UUID.randomUUID(), false));
        assertEquals(15L, q.peekTimestamp());
    }

    @Test
    void peekTimestampWithOnlyAntiMessages() {
        MessageQueueBase q = new MessageQueueBase();
        q.push(new Message(5L, 20L, 1, 2, "", "", UUID.randomUUID(), true));
        q.push(new Message(8L, 15L, 1, 2, "", "", UUID.randomUUID(), true));
        q.push(new Message(8L, 25L, 1, 2, "", "", UUID.randomUUID(), true));
        assertEquals(15L, q.peekTimestamp());
    }

    @Test
    void peekTimestampWithRegularAndAntiMessages() {
        MessageQueueBase q = new MessageQueueBase();
        q.push(new Message(5L, 20L, 1, 2, "", "", UUID.randomUUID(), false));
        q.push(new Message(8L, 15L, 1, 2, "", "", UUID.randomUUID(), true));
        q.push(new Message(8L, 25L, 1, 2, "", "", UUID.randomUUID(), true));
        assertEquals(15L, q.peekTimestamp());

        MessageQueueBase q2 = new MessageQueueBase();
        q2.push(new Message(5L, 20L, 1, 2, "", "", UUID.randomUUID(), true));
        q2.push(new Message(8L, 15L, 1, 2, "", "", UUID.randomUUID(), false));
        q2.push(new Message(8L, 25L, 1, 2, "", "", UUID.randomUUID(), false));
        assertEquals(15L, q2.peekTimestamp());
    }
}