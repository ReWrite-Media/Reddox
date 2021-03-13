package net.minestom.script.command.utils;

import net.minestom.script.command.RichCommand;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandResult;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskBuilder;
import net.minestom.server.utils.time.TimeUnit;
import net.minestom.server.utils.time.UpdateOption;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.minestom.server.command.builder.arguments.ArgumentType.Integer;
import static net.minestom.server.command.builder.arguments.ArgumentType.*;

import java.lang.String;

public class ScheduleCommand extends RichCommand {
    private static final CommandManager COMMAND_MANAGER = MinecraftServer.getCommandManager();
    private static final SchedulerManager SCHEDULER_MANAGER = MinecraftServer.getSchedulerManager();

    private static final Map<Task, String> SCHEDULED_TASKS_MAP = new ConcurrentHashMap<>();

    public ScheduleCommand() {
        super("schedule");

        // /schedule list
        {
            addSyntax((sender, context) -> {
                for (Map.Entry<Task, String> entry : SCHEDULED_TASKS_MAP.entrySet()) {
                    final Task task = entry.getKey();
                    final String command = entry.getValue();
                    sender.sendMessage("Task id '" + task.getId() + "' with cmd " + command);
                }
            }, Literal("list"));
        }

        // /schedule remove <id>
        {

            addSyntax((sender, args) -> {
                final int id = args.get("task_id");

                final boolean removed = SCHEDULED_TASKS_MAP.keySet().removeIf(task -> {
                    if (task.getId() == id) {
                        SCHEDULER_MANAGER.removeTask(task);

                        sender.sendMessage("You removed the task " + id + " successfully");
                        return true;
                    }
                    return false;
                });

                if (!removed) {
                    sender.sendMessage("The task " + id + " does not exist");
                }
            }, Literal("remove"), Integer("task_id"));
        }

        // /schedule delayed <delay> <command>
        {
            addSyntax((sender, args) -> {
                final UpdateOption delay = args.get("delay");
                final CommandResult commandResult = args.get("command");

                scheduleTask(sender, commandResult, delay, null);
            }, Literal("delayed"), Time("delay"), Command("command"));
        }

        // /schedule delayed_repeat <delay> <repeat> <command>
        {
            addSyntax((sender, args) -> {
                final UpdateOption delay = args.get("delay");
                final UpdateOption repeat = args.get("repeat");
                final CommandResult commandResult = args.get("command");

                scheduleTask(sender, commandResult, delay, repeat);
            }, Literal("delayed_repeat"), Time("delay"), Time("repeat"), Command("command"));
        }

        // /schedule repeat <repeat> <command>
        {
            addSyntax((sender, args) -> {
                final UpdateOption repeat = args.get("repeat");
                final CommandResult commandResult = args.get("command");

                scheduleTask(sender, commandResult, null, repeat);
            }, Literal("repeat"), Time("repeat"), Command("command"));
        }

        // /schedule gmt <time> <command>
        {
            addSyntax((sender, args) -> {
                final String timeString = args.get("utc_time"); // // eg: 2021-02-07T19:16:19Z
                final CommandResult commandResult = args.get("command");

                final Instant now = Instant.now();
                final Instant time = Instant.parse(timeString);

                final UpdateOption delay;
                {
                    final Duration duration = Duration.between(now, time);
                    final long seconds = duration.get(ChronoUnit.SECONDS);
                    delay = new UpdateOption(seconds, TimeUnit.SECOND);
                }

                scheduleTask(sender, commandResult, delay, null);
            }, Literal("gmt"), Word("utc_time"), Command("command"));
        }

    }

    private static void scheduleTask(CommandSender sender, CommandResult commandResult,
                                     UpdateOption delay, UpdateOption repeat) {
        final String input = commandResult.getInput();
        // TODO remove task from list automatically if not repeated
        TaskBuilder taskBuilder = SCHEDULER_MANAGER.buildTask(() ->
                commandResult.getParsedCommand().execute(COMMAND_MANAGER.getConsoleSender(), input));

        if (delay != null) {
            taskBuilder.delay(delay.getValue(), delay.getTimeUnit());
        }

        if (repeat != null) {
            taskBuilder.repeat(repeat.getValue(), repeat.getTimeUnit());
        }

        final Task task = taskBuilder.schedule();
        SCHEDULED_TASKS_MAP.put(task, input);

        sender.sendMessage("You created the task " + task.getId() + " successfully (" + input + ")");
    }
}
