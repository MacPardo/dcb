package dcb.components;

import dcb.core.Behavior;
import dcb.core.TranslatorImpl;
import dcb.core.MessengerImpl;
import dcb.core.models.Message;
import dcb.utils.Receiver;

import java.util.Objects;


public class ComponentFactoryArgs {
    public final int id;
    public final Behavior core;
    public final Receiver<Message> receiver;
    public final MessengerImpl messenger;
    public final TranslatorImpl translator;
    public final String outputFilename;

    public ComponentFactoryArgs(int id, Behavior core, Receiver<Message> receiver, MessengerImpl messenger, TranslatorImpl translator, String outputFilename) {
        this.id = id;
        this.core = core;
        this.receiver = receiver;
        this.messenger = messenger;
        this.translator = translator;
        this.outputFilename = outputFilename;
    }

    @Override
    public String toString() {
        return "ComponentData{" +
                "id=" + id +
                ", core=" + core +
                ", receiver=" + receiver +
                ", messenger=" + messenger +
                ", translator=" + translator +
                '}';
    }
}
