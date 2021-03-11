package net.minestom.script.component;

import net.minestom.script.Executor;
import org.jetbrains.annotations.NotNull;

public class ScriptAPI {

    private final Executor executor = new Executor();
    private final RegionComponent regionComponent = new RegionComponent();

    @NotNull
    public Executor getExecutor() {
        return executor;
    }

    @NotNull
    public RegionComponent getRegionHandler() {
        return regionComponent;
    }
}
