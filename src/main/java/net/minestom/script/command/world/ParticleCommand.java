package net.minestom.script.command.world;

import net.kyori.adventure.text.Component;
import net.minestom.script.command.RichCommand;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.particle.ParticleCreator;
import net.minestom.server.utils.location.RelativeVec;

import static net.minestom.server.command.builder.arguments.ArgumentType.Float;
import static net.minestom.server.command.builder.arguments.ArgumentType.Integer;
import static net.minestom.server.command.builder.arguments.ArgumentType.*;

public class ParticleCommand extends RichCommand {
    public ParticleCommand() {
        super("particle");

        setDefaultExecutor((sender, context) ->
                sender.sendMessage(Component.text("Usage: /particle <type> <position> <delta> <speed> <count>")));

        addSyntax((sender, context) -> {
                    final Particle particle = context.get("particle");
                    final RelativeVec relativePosition = context.get("position");
                    final RelativeVec relativeDelta = context.get("delta");
                    final float speed = context.get("speed");
                    final int count = context.get("count");

                    final Vec position = relativePosition.fromSender(sender);
                    final Vec delta = relativeDelta.fromSender(sender);

                    ParticlePacket particlePacket = ParticleCreator.createParticlePacket(
                            particle, false, position.x(), position.y(), position.z(),
                            (float) delta.x(), (float) delta.y(), (float) delta.z(), speed, count, null);

                    if (sender.isPlayer()) {
                        sender.asPlayer().sendPacketToViewersAndSelf(particlePacket);
                    }

                    sender.sendMessage(Component.text("Particle(s) sent!"));

                }, Particle("particle"), RelativeVec3("position"),
                RelativeVec3("delta"), Float("speed"),
                Integer("count"));
    }
}
