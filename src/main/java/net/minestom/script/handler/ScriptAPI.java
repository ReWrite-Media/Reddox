package net.minestom.script.handler;

import org.jetbrains.annotations.NotNull;

public class ScriptAPI {

    private final RegionHandler regionHandler = new RegionHandler();

    @NotNull
    public RegionHandler getRegionHandler() {
        return regionHandler;
    }
}
