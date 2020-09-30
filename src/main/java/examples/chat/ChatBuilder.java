package examples.chat;

import dcb.core.component.ComponentCore;
import dcb.core.component.ComponentCoreBuilder;
import dcb.core.utils.Copyable;

public class ChatBuilder implements ComponentCoreBuilder {
    private final String[] routes = {"velocidade", "numero_de_furos"};

    @Override
    public <State extends Copyable<State>> ComponentCore<State> createNew() {
        return null;
    }

    @Override
    public String getName() {
        return "DISCO_SENSOR";
    }

    @Override
    public String[] getRoutes() {
        return routes;
    }
}
