package net.minestom.script.component;

import org.jetbrains.annotations.NotNull;

public class ScriptAPI {

    private final RegionComponent regionComponent = new RegionComponent();

    @NotNull
    public RegionComponent getRegionHandler() {
        return regionComponent;
    }
}
