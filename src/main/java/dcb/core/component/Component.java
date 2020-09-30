package dcb.core.component;

import dcb.core.utils.Copyable;

public abstract class Component<State extends Copyable<State>> implements Runnable {
    protected final ComponentData<State> data;

    protected Component(ComponentData<State> data) {
        this.data = data;
    }
}
