package net.minestom.script.command.utils;

import net.kyori.adventure.text.Component;
import net.minestom.script.command.RichCommand;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandData;
import net.minestom.server.command.builder.CommandResult;
import net.minestom.server.command.builder.ParsedCommand;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskBuilder;
import net.minestom.server.utils.time.TimeUnit;
import net.minestom.server.utils.time.UpdateOption;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.String;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import static net.minestom.server.command.builder.arguments.ArgumentType.Integer;
import static net.minestom.server.command.builder.arguments.ArgumentType.*;

public class ScheduleCommand extends RichCommand {
    private static final CommandManager COMMAND_MANAGER = MinecraftServer.getCommandManager();
    private static final SchedulerManager SCHEDULER_MANAGER = MinecraftServer.getSchedulerManager();

    private static final Map<Task, String> SCHEDULED_TASKS_MAP = new ConcurrentHashMap<>();

    public ScheduleCommand() {
        super("schedule");

        // /schedule list
        {
            addSyntax((sender, context) -> {
                sender.sendMessage(Component.text("Task count: " + SCHEDULED_TASKS_MAP.size()));
                for (Map.Entry<Task, String> entry : SCHEDULED_TASKS_MAP.entrySet()) {
                    final Task task = entry.getKey();
                    final String command = entry.getValue();
                    sender.sendMessage(Component.text("Task id '" + task.getId() + "' with cmd " + command));
                }
            }, Literal("list"));
        }

        // /schedule remove <id>
        {

            addSyntax((sender, context) -> {
                final int id = context.get("task_id");

                final boolean removed = SCHEDULED_TASKS_MAP.keySet().removeIf(task -> {
                    if (task.getId() == id) {
                        SCHEDULER_MANAGER.removeTask(task);

                        sender.sendMessage(Component.text("You removed the task " + id + " successfully"));
                        return true;
                    }
                    return false;
                });

                if (!removed) {
                    sender.sendMessage(Component.text("The task " + id + " does not exist"));
                }
            }, Literal("remove"), Integer("task_id"));
        }

        // /schedule removeall
        {
            addSyntax((sender, context) -> {
                SCHEDULED_TASKS_MAP.keySet().removeIf(task -> {
                    SCHEDULER_MANAGER.removeTask(task);
                    return true;
                });
                sender.sendMessage(Component.text("All tasks have been removed!"));
            }, Literal("removeall"));
        }

        // /schedule delayed <delay> <command>
        {
            addSyntax((sender, context) -> {
                final UpdateOption delay = context.get("delay");
                final CommandResult commandResult = context.get("command");

                scheduleTask(sender, context, commandResult, delay, null);
            }, Literal("delayed"), Time("delay"), Command("command"));
        }

        // /schedule delayed_repeat <delay> <repeat> <command>
        {
            addSyntax((sender, context) -> {
                final UpdateOption delay = context.get("delay");
                final UpdateOption repeat = context.get("repeat");
                final CommandResult commandResult = context.get("command");

                scheduleTask(sender, context, commandResult, delay, repeat);
            }, Literal("delayed_repeat"), Time("delay"), Time("repeat"), Command("command"));
        }

        // /schedule repeat <repeat> <command>
        {
            addSyntax((sender, context) -> {
                final UpdateOption repeat = context.get("repeat");
                final CommandResult commandResult = context.get("command");

                scheduleTask(sender, context, commandResult, null, repeat);
            }, Literal("repeat"), Time("repeat"), Command("command"));
        }

        // /schedule gmt <time> <command>
        {
            addSyntax((sender, context) -> {
                final String timeString = context.get("utc_time"); // // eg: 2021-02-07T19:16:19Z
                final CommandResult commandResult = context.get("command");

                final Instant now = Instant.now();
                final Instant time = Instant.parse(timeString);

                final UpdateOption delay;
                {
                    final Duration duration = Duration.between(now, time);
                    final long seconds = duration.get(ChronoUnit.SECONDS);
                    delay = new UpdateOption(seconds, TimeUnit.SECOND);
                }

                scheduleTask(sender, context, commandResult, delay, null);
            }, Literal("gmt"), Word("utc_time"), Command("command"));
        }

    }

    private static void scheduleTask(@NotNull CommandSender sender, CommandContext context, @NotNull CommandResult commandResult,
                                     @Nullable UpdateOption delay, @Nullable UpdateOption repeat) {
        final ParsedCommand parsedCommand = commandResult.getParsedCommand();
        if (parsedCommand == null) {
            sender.sendMessage(Component.text("Invalid command"));
            return;
        }

        final String input = commandResult.getInput();
        AtomicReference<Task> taskReference = new AtomicReference<>();
        TaskBuilder taskBuilder = SCHEDULER_MANAGER.buildTask(() -> {
            parsedCommand.execute(COMMAND_MANAGER.getConsoleSender());
            if (repeat == null) {
                final Task task = taskReference.get();
                if (task != null) {
                    SCHEDULED_TASKS_MAP.remove(task);
                }
            }
        });

        if (delay != null) {
            taskBuilder.delay(delay.getValue(), delay.getTimeUnit());
        }

        if (repeat != null) {
            taskBuilder.repeat(repeat.getValue(), repeat.getTimeUnit());
        }

        final Task task = taskBuilder.schedule();
        taskReference.set(task);
        SCHEDULED_TASKS_MAP.put(task, input);

        CommandData commandData = new CommandData();
        commandData.set("taskId", task.getId());
        context.setReturnData(commandData);

        sender.sendMessage(Component.text("You created the task " + task.getId() + " successfully (" + input + ")"));
    }
}
