package dcb.components.rdtlgc;

import dcb.core.component.ComponentData;
import dcb.core.component.Component;
import dcb.core.utils.Copyable;

public class RdtLgcComponent<State extends Copyable<State>> extends Component<State> {
    public RdtLgcComponent(ComponentData<State> data) {
        super(data);
    }

    @Override
    public void run() {

    }
}
