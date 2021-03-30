package net.minestom.script.component;

import net.minestom.script.GlobalExecutor;
import org.jetbrains.annotations.NotNull;

public class ScriptAPI {

    private final GlobalExecutor globalExecutor = new GlobalExecutor();
    private final RegionComponent regionComponent = new RegionComponent();

    @NotNull
    public GlobalExecutor getExecutor() {
        return globalExecutor;
    }

    @NotNull
    public RegionComponent getRegionHandler() {
        return regionComponent;
    }
}
