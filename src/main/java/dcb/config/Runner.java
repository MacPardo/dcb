package dcb.config;

import dcb.config.models.ComponentConfig;
import dcb.config.models.FullConfig;
import dcb.config.models.ProcessConfig;
import dcb.core.component.ComponentCoreBuilder;

import java.util.Arrays;

public class Runner implements Runnable {
    private final FullConfig config;
    private final ComponentCoreBuilder[] builders;
    private final String processName;

    public Runner(FullConfig config, ComponentCoreBuilder[] builders, String processName) {
        this.config = config;
        this.builders = builders;
        this.processName = processName;
    }

    @Override
    public void run() {
        int id = 0;
        ProcessConfig processConfig = config.processes
                .stream()
                .filter(cfg -> cfg.name == processName)
                .findFirst()
                .get();

        for (ComponentConfig component : processConfig.components) {
            ComponentCoreBuilder builder = Arrays.stream(builders)
                    .filter(g -> g.getName() == component.core)
                    .findFirst()
                    .get();
        }
    }
}
