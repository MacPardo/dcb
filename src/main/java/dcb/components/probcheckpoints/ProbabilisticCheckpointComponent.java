package dcb.components.probcheckpoints;

import dcb.core.component.ComponentData;
import dcb.core.component.Component;
import dcb.core.utils.Copyable;

public class ProbabilisticCheckpointComponent<State extends Copyable<State>> extends Component<State> {
    public ProbabilisticCheckpointComponent(ComponentData<State> data) {
        super(data);
    }

    @Override
    public void run() {

    }
}
