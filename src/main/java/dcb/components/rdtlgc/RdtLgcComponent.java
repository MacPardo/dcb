package dcb.components.rdtlgc;

import dcb.components.utils.MessageQueueBase;
import dcb.components.utils.RollbackManager;
import dcb.core.Component;
import dcb.components.ComponentFactoryArgs;
import dcb.core.Gateway;
import dcb.core.State;
import dcb.core.TranslatorGateway;
import dcb.core.models.Message;
import dcb.exceptions.DcbException;
import dcb.utils.Pair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/*
This component implements the NRAS (No-Receive-After-Send) checkpointing protocol.
1. Whenever a checkpoint is saved, the AFTER_FIRST_SEND flag must be set to FALSE.
2. Whenever sending a message to SOMEONE ELSE, set AFTER_FIRST_SEND=TRUE.

3. If a message is received && AFTER_FIRST_SEND == TRUE, then a checkpoint must be saved
BEFORE (!!) the message is processed.

Item 3 needs some adjustments, because the protocol originally deals with real time,
whereas this component deals with virtual time (LVT). This effectively means that
messages with the same timestamp must be treated by the protocol as if they were the same message.

Also, because the checkpoint must precede the message, the rollback manager must be asked
to take a checkpoint for the timestamp that precedes the messages' timestamp.

So, whenever a message with timestamp t is received, if AFTER_FIRST_SEND == TRUE, this component should
    - try to make the rollback manager take a checkpoint with timestamp less than the message's timestamp
 */
@SuppressWarnings({"InfiniteLoopStatement", "MagicNumber", "UnsecureRandomNumberGeneration"})
public class RdtLgcComponent extends Component {
    private final MessageQueueBase messageQueue = new MessageQueueBase();
    private final Gateway gateway;
    private RollbackManager rollbackManager = null;
    private boolean afterFirstSend = false;
    private int counter = 0;
    private int selfId;
    private BufferedWriter writer;
    private Set<UUID> regularSent = new HashSet<>();
//    private Map<Integer, CCB> UC = new HashMap<>();

    public RdtLgcComponent(ComponentFactoryArgs args) {
        super(args);
        selfId = args.id;
        gateway = new TranslatorGateway(args.translator, args.core);
        try {
            writer = new BufferedWriter(new FileWriter(args.outputFilename, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void print(String message) {
        if (selfId != 1) return;
        System.out.println("<" + selfId + "> - " + message);
    }

    private void log(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            runThrows();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runThrows() throws Exception {
        Pair<State, List<Message>> initialValues = gateway.init();
        State initialState = initialValues.first;
        List<Message> initialMessages = initialValues.second;

        rollbackManager = new RollbackManager(args.id, initialState);

        for (Message msg : initialMessages) {
            sendMessage(msg);
        }

        while (true) {
            Message message = args.receiver.poll(1L, TimeUnit.DAYS);
//            print(" -----> received " + message);
//            print("__RB-stat (recv=" + rollbackManager.receivedMessages.size() + ", sent=" + rollbackManager.sentMessages.size() + ", chck=" + rollbackManager.checkpoints.size() + ")");

            messageQueue.push(message);

            if (messageQueue.canPeekTimestamp()) {
                boolean violates = isThereLCCViolation();
                if (violates) {
                    if (!messageQueue.canPop()) {
                        print("violation caused by anti message");
                    }
//                    print("rolling back to " + messageQueue.peekTimestamp());
                    Collection<Message> messages = rollbackManager.rollback(messageQueue.peekTimestamp());
//                    print("rolled back to " + rollbackManager.getLvt());
                    for (Message msg : messages) {
                        sendMessage(msg);
                    }
                }
                print("before pop " + messageQueue.regularQueue.size() + " - " + messageQueue.antiQueue.size());
                if (messageQueue.canPop()) {
                    onMessage(messageQueue.pop());
                } else {
                    print("did not pop");
                }
            }
        }
    }

    boolean isThereLCCViolation() {
        return messageQueue.peekTimestamp() < rollbackManager.getLvt();
    }

    private void sendMessage(Message message) throws DcbException, InterruptedException {
        message.componentPayload = String.valueOf(counter);
        if (message.to != selfId) {
            afterFirstSend = true;
        }
        if (message.isAnti) {
            boolean already = regularSent.contains(message.uuid);
            print("sending anti message " + (already ? "ALREADY" : "FIRST"));
        } else {
            regularSent.add(message.uuid);
        }
        args.messenger.send(message);
    }

    private void takeCheckpoint() {
        afterFirstSend = false;
        rollbackManager.takeCheckpoint();
    }

    void onMessage(Message message) throws DcbException, InterruptedException {
        if (counter > 20) {
            print("Ending execution");
            System.exit(0);
        }
        print(messageQueue.regularQueue.size() + " regular messages on queue");
        for (Message m : messageQueue.regularQueue) {
            print("regular: " + m);
        }
        print(messageQueue.antiQueue.size() + " anti messages on queue");
        for (Message m : messageQueue.antiQueue) {
            print("anti: " + m);
        }

        counter++;
        print(String.valueOf(counter));
        log(rollbackManager.lvt + "," + rollbackManager.checkpoints.size());

        // criar checkpoint com timestamp = 200
        rollbackManager.update(rollbackManager.state.copy(), 200);
        rollbackManager.takeCheckpoint();
        // aqui, LVT = 201

        // TODO: why is this never executing???
        if (rollbackManager.lvt < message.execTs && afterFirstSend && message.from != selfId) {
            print("NRAS CHECKPOINT");
            takeCheckpoint();
        } else {
            print("NRAS DID NOT [" + rollbackManager.lvt + "] (" +
                    (rollbackManager.lvt < message.execTs) + ") - (" +
                    (afterFirstSend) + ") - (" +
                    (message.from != selfId) + ")");
        }

        rollbackManager.saveMessage(message);
        long timestamp = message.execTs;
        Pair<State, List<Message>> values = gateway.onMessage(rollbackManager.getState(), message);
        State newState = values.first;
        List<Message> messages = values.second;
        rollbackManager.update(newState, timestamp);

        if (shouldTakeCheckpoint(message)) {
            takeCheckpoint();
        }

        for (Message m : messages) {
            rollbackManager.saveMessage(m);
            sendMessage(m);
        }
    }

    // decides when to take a non-forced checkpoint
    @SuppressWarnings("MethodMayBeStatic")
    private boolean shouldTakeCheckpoint(Message message) {
        return false;
    }
}
