package dcb.core.component;

import dcb.core.utils.Copyable;

public interface ComponentCoreBuilder {
    <State extends Copyable<State>> ComponentCore<State> createNew();

    String getName();

    String[] getRoutes();
}
