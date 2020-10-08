package dcb.components;

import dcb.core.component.ComponentCore;
import dcb.core.gateway.Translator;
import dcb.core.messaging.Messenger;
import dcb.core.models.Message;
import dcb.core.utils.BlockingQueueReceiver;

import java.util.Objects;


public class ComponentFactoryArgs {
    public final int id;
    public final ComponentCore core;
    public final BlockingQueueReceiver<Message> receiver;
    public final Messenger messenger;
    public final Translator translator;

    public ComponentFactoryArgs(int id, ComponentCore core, BlockingQueueReceiver<Message> receiver, Messenger messenger, Translator translator) {
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
