package dcb.components.probcheckpoints;

import dcb.components.rdtlgc.CCB;
import dcb.components.utils.DependencyVector;
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

import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"InfiniteLoopStatement", "MagicNumber", "UnsecureRandomNumberGeneration"})
public class ProbabilisticCheckpointComponent extends Component {
    private final MessageQueueBase messageQueue = new MessageQueueBase();
    private final Gateway gateway;
    private RollbackManager rollbackManager = null;
    private int totalRollbackCount = 0;
    private int totalEventCount = 0;
    private int totalCheckpointAttemptCount = 0;
    private int totalUselessCheckpointCount = 0;
    private long totalRollbackTime = 0L;
    private Random randomGenerator = new Random();
    Map<Integer, CCB> UC = new HashMap<>();
    DependencyVector dependencyVector;


    public ProbabilisticCheckpointComponent(ComponentFactoryArgs args) {
        super(args);
        gateway = new TranslatorGateway(args.translator, args.core);
        dependencyVector = DependencyVector.createNew(args.id);
    }

    private void print(String message) {
        System.out.println("<" + rollbackManager.id + "> - " + message);
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

        for (Message message : initialMessages) {
            args.messenger.send(message);
        }

        while (true) {
            Message message = args.receiver.poll(1L, TimeUnit.DAYS);
            print(" -----> received " + message);
            print("__RB-stat (recv=" + rollbackManager.receivedMessages.size() + ", sent=" + rollbackManager.sentMessages.size() + ", chck=" + rollbackManager.checkpoints.size() + ")");

            messageQueue.push(message);

            if (messageQueue.canPeekTimestamp()) {
                boolean violates = isThereLCCViolation();
                if (violates) {
                    print("rolling back to " + messageQueue.peekTimestamp());
                    totalRollbackCount++;

                    long ts = messageQueue.peekTimestamp();
                    totalUselessCheckpointCount += getUselessCheckpointCount(ts);
                    totalRollbackTime += getRollbackTime(ts);
                    Collection<Message> messages = rollbackManager.rollback(ts);
                    print("rolled back to " + rollbackManager.getLvt());
                    for (Message msg : messages) {
                        args.messenger.send(msg);
                    }
                }
                onMessage(messageQueue.pop());
            }
        }
    }

    boolean isThereLCCViolation() {
        return messageQueue.peekTimestamp() < rollbackManager.getLvt();
    }

    void onMessage(Message message) throws DcbException, InterruptedException {
        totalEventCount++;
        print("got message " + message);
        rollbackManager.saveMessage(message);
        long timestamp = message.execTs;
        Pair<State, List<Message>> values = gateway.onMessage(rollbackManager.getState(), message);
        State newState = values.first;
        List<Message> messages = values.second;
        rollbackManager.update(newState, timestamp);

        totalCheckpointAttemptCount++;
        if (shouldTakeCheckpoint()) {
            rollbackManager.takeCheckpoint();
        }

        for (Message m : messages) {
            rollbackManager.saveMessage(m);
            args.messenger.send(m);
        }
    }

    @SuppressWarnings("MethodMayBeStatic")
    public boolean shouldTakeCheckpoint() {
        return true;
    }

    private int getUselessCheckpointCount(long timestamp) {
        int i = rollbackManager.checkpoints.size() - 1;
        int count = 0;

        while (timestamp < rollbackManager.checkpoints.get(i).timestamp) {
            rollbackManager.checkpoints.remove(i);
            i--;
            count++;
        }

        return count;
    }

    double getRollbackProbability() {
        double averageEventsPerRollback = 1L;
        double rollbackProbability = 0;

        if (totalRollbackCount > 0 && totalEventCount > 0) {
            averageEventsPerRollback = ((double) totalEventCount / totalRollbackCount);
        }

//        if ()

        return 0.5;
    }

    private long getRollbackTime(long timestamp) {
        double alpha = randomGenerator.nextDouble();
        return rollbackManager.lvt - timestamp;
    }
}
