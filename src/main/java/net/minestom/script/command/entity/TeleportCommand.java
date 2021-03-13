package net.minestom.script.command.entity;

import net.minestom.script.command.RichCommand;
import net.minestom.server.chat.ChatColor;
import net.minestom.server.chat.ColoredText;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.command.builder.arguments.relative.ArgumentRelativeVec2;
import net.minestom.server.command.builder.arguments.relative.ArgumentRelativeVec3;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.Position;
import net.minestom.server.utils.entity.EntityFinder;
import net.minestom.server.utils.location.RelativeVec;

import java.util.List;

public class TeleportCommand extends RichCommand {

    private final ArgumentRelativeVec3 location = ArgumentType.RelativeVec3("location");
    private final ArgumentEntity destination = ArgumentType.Entity("destination").singleEntity(true);
    private final ArgumentEntity targets = ArgumentType.Entity("targets");
    private final ArgumentRelativeVec2 direction = ArgumentType.RelativeVec2("direction");

    public TeleportCommand() {
        super("tp");

        setDefaultExecutor(this::usage);

        addSyntax(this::targetToDestinationWithDir, targets, destination, direction);
        addSyntax(this::targetToLocationWithDir, targets, location, direction);

        addSyntax(this::targetToDestination, targets, destination);
        addSyntax(this::targetToLocation, targets, location);

        addSyntax(this::selfToEntity, destination);
        addSyntax(this::selfToPosition, location);
    }

    private void usage(CommandSender sender, CommandContext context) {
        if (sender.isPlayer()) {
            sender.sendMessage("Usage: /tp <location>");
            sender.sendMessage("Usage: /tp <destination>");
        }
        sender.sendMessage("Usage: /tp <targets> (<destination>|<location>)");
    }

    private void selfToEntity(CommandSender sender, CommandContext context) {
        if (sender.isConsole()) {
            usage(sender, context);
            return;
        }

        EntityFinder entityFinder = context.get(destination);

        List<Entity> entities = entityFinder.find(sender);

        if (entities.size() > 0) {
            teleport(sender.asPlayer(), entities.get(0).getPosition());
            sender.sendMessage("Teleport to entity");
        } else {
            sender.sendMessage(ColoredText.of(ChatColor.RED, "No destination found"));
        }
    }

    private void selfToPosition(CommandSender sender, CommandContext context) {
        if (sender.isConsole()) {
            usage(sender, context);
            return;
        }

        Player player = sender.asPlayer();

        RelativeVec relativeVec = context.get(location);
        Position position = relativeVec.from(player).toPosition();

        teleport(player, position);
        player.sendMessage("Teleported " + player.getUsername() + " to " +
                position.getX() + ", " + position.getY() + ", " + position.getZ());
    }

    public void targetToLocation(CommandSender sender, CommandContext context) {
        RelativeVec relativeVec = context.get(location);
        EntityFinder entityTarget = context.get(targets);

        List<Entity> targetsEntity = entityTarget.find(sender);

        if (targetsEntity.size() > 0) {
            targetsEntity.stream()
                    .filter(entity -> !(entity instanceof Player) ||
                            !sender.isPlayer() ||
                            !sender.asPlayer().getUuid().equals(entity.getUuid()))
                    .forEach(entity -> {
                        Position position = relativeVec.from(entity).toPosition();
                        teleport(entity, position);
                        String entityName = entity.getCustomName() == null ?
                                entity.getEntityType().name() : entity.getCustomName().getRawMessage();
                        sender.sendMessage("Teleported " + entityName + " to " +
                                position.getX() + ", " + position.getY() + ", " + position.getZ());
                    });
            sender.sendMessage("Teleported target(s)");
        } else {
            sender.sendMessage(ColoredText.of(ChatColor.RED, "No target found"));
        }
    }

    public void targetToLocationWithDir(CommandSender sender, CommandContext context) {
        RelativeVec relativeVec = context.get(location);
        RelativeVec relativeDirection = context.get(direction);

        EntityFinder entityTarget = context.get(targets);
        List<Entity> targetsEntity = entityTarget.find(sender);

        if (targetsEntity.size() > 0) {
            targetsEntity.stream()
                    .filter(entity -> !(entity instanceof Player) ||
                            !sender.isPlayer() ||
                            !sender.asPlayer().getUuid().equals(entity.getUuid()))
                    .forEach(entity -> {
                        Position position = relativeVec.from(entity).toPosition().setDirection(relativeDirection.from(entity));
                        teleport(entity, position);
                        String entityName = entity.getCustomName() == null ?
                                entity.getEntityType().name() : entity.getCustomName().getRawMessage();
                        sender.sendMessage("Teleported " + entityName + " to " +
                                position.getX() + ", " + position.getY() + ", " + position.getZ());
                    });
            sender.sendMessage("Teleported target(s)");
        } else {
            sender.sendMessage(ColoredText.of(ChatColor.RED, "No target found"));
        }

    }

    public void targetToDestination(CommandSender sender, CommandContext context) {
        EntityFinder destinationFinder = context.get(destination);
        EntityFinder entityTarget = context.get(targets);

        List<Entity> targetsEntity = entityTarget.find(sender);
        Entity destination = destinationFinder.find(sender).get(0);

        if (targetsEntity.size() > 0) {
            targetsEntity.stream()
                    .filter(entity -> !(entity instanceof Player) ||
                            !sender.isPlayer() ||
                            !sender.asPlayer().getUuid().equals(entity.getUuid()))
                    .forEach(entity -> {
                        Position position = destination.getPosition();
                        teleport(entity, position);
                        String entityName = entity.getCustomName() == null ?
                                entity.getEntityType().name() : entity.getCustomName().getRawMessage();
                        sender.sendMessage("Teleported " + entityName + " to " +
                                position.getX() + ", " + position.getY() + ", " + position.getZ());
                    });
            sender.sendMessage("Teleported target(s)");
        } else {
            sender.sendMessage(ColoredText.of(ChatColor.RED, "No target found"));
        }
    }

    public void targetToDestinationWithDir(CommandSender sender, CommandContext context) {
        RelativeVec relativeVec = context.get(location);
        RelativeVec relativeDirection = context.get(direction);

        EntityFinder entityTarget = context.get(targets);
        List<Entity> targetsEntity = entityTarget.find(sender);

        if (targetsEntity.size() > 0) {
            targetsEntity.stream()
                    .filter(entity -> !(entity instanceof Player) ||
                            !sender.isPlayer() ||
                            !sender.asPlayer().getUuid().equals(entity.getUuid()))
                    .forEach(entity -> {
                        Position position = relativeVec.from(entity).toPosition().setDirection(relativeDirection.from(entity));
                        teleport(entity, position);
                        String entityName = entity.getCustomName() == null ?
                                entity.getEntityType().name() : entity.getCustomName().getRawMessage();
                        sender.sendMessage("Teleported " + entityName + " to " +
                                position.getX() + ", " + position.getY() + ", " + position.getZ());
                    });
            sender.sendMessage("Teleported target(s)");
        } else {
            sender.sendMessage(ColoredText.of(ChatColor.RED, "No target found"));
        }
    }

    public void teleport(Entity target, Position result) {
        target.teleport(result);
    }
}
