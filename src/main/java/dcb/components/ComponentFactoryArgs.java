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

    public ComponentFactoryArgs(int id, Behavior core, Receiver<Message> receiver, MessengerImpl messenger, TranslatorImpl translator) {
        this.id = id;
        this.core = core;
        this.receiver = receiver;
        this.messenger = messenger;
        this.translator = translator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComponentFactoryArgs that = (ComponentFactoryArgs) o;
        return id == that.id &&
                Objects.equals(core, that.core) &&
                Objects.equals(receiver, that.receiver) &&
                Objects.equals(messenger, that.messenger) &&
                Objects.equals(translator, that.translator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, core, receiver, messenger, translator);
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
