package dcb.components;

import dcb.components.optimistic.OptimisticComponent;
import dcb.components.probcheckpoints.ProbabilisticCheckpointComponent;
import dcb.components.rdtlgc.RdtLgcComponent;
import dcb.core.component.Component;

public class ComponentFactory {
    public static Component createComponent(
            ComponentType componentType,
            ComponentFactoryArgs componentFactoryArgs
    ) {
        return switch (componentType) {
            case OPTIMISTIC -> new OptimisticComponent(componentFactoryArgs);
            case PROBABILISTIC_CHECKPOINTS -> new ProbabilisticCheckpointComponent(componentFactoryArgs);
            case RDT_LGC -> new RdtLgcComponent(componentFactoryArgs);
        };
    }
}
