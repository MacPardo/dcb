package dcb.components;

import dcb.components.optimistic.OptimisticComponent;
import dcb.components.probcheckpoints.ProbabilisticCheckpointComponent;
import dcb.components.rdtlgc.RdtLgcComponent;
import dcb.core.component.Component;
import dcb.core.component.ComponentData;
import dcb.core.exceptions.DcbException;
import dcb.core.utils.Copyable;

public class ComponentFactory {
    public static <State extends Copyable<State>> Component<State> createComponent(
            ComponentType componentType,
            ComponentData<State> componentData) throws DcbException {
        switch (componentType) {
            case OPTIMISTIC:
                return new OptimisticComponent<>(componentData);
            case PROBABILISTIC_CHECKPOINTS:
                return new ProbabilisticCheckpointComponent<>(componentData);
            case RDT_LGC:
                return new RdtLgcComponent<>(componentData);
            default:
                throw new DcbException();
        }
    }
}
