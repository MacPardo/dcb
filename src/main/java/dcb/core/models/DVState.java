package dcb.core.models;

import dcb.components.utils.DependencyVector;
import dcb.core.State;

public class DVState implements State {
    public final State actualState;
    public final DependencyVector dependencyVector;

    protected DVState(State actualState, DependencyVector dependencyVector) {
        this.actualState = actualState;
        this.dependencyVector = dependencyVector;
    }

    @Override
    public State copy() {
        return new DVState(actualState.copy(), dependencyVector.copy());
    }
}
