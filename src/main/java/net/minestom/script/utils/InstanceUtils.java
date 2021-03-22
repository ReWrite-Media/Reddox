package net.minestom.script.utils;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.instance.Instance;

import java.util.function.Consumer;

public class InstanceUtils {

    public static void processInstances(CommandSender sender, Consumer<Instance> instanceConsumer) {
        if (sender.isPlayer()) {
            instanceConsumer.accept(sender.asPlayer().getInstance());
        } else {
            for (Instance instance : MinecraftServer.getInstanceManager().getInstances()) {
                instanceConsumer.accept(instance);
            }
        }
    }

}
