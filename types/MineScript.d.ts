/*
* MineScript language-inspecific concept definition v0
*/

/**
 * Represents something which is capable of executing commands.
 *
 * The specific options of the executor are unknown. For example,
 * the command may be executed by the console or by a specific player.
 */
declare interface Executor {

    /**
     * Execute the given command with the given arguments.
     *
     * ```js
     * executor.run('tell', 'Notch', '<green>Hello, World');
     * ```
     *
     * @param args The command arguments
     */
    run(...args: any[]): any;

    /**
     * Create an alias for the given command with placeholder arguments.
     *
     * The return value may be executed the same as {@link Executor#run}
     *
     * ```js
     * const tell = executor.make('tell {0} {1}');
     * tell('Notch', '<green>Hello, World');
     *
     * const getPlayer = executor.make('entity get {0}', data => data.entity);
     * console.log("Fetched player: " + getPlayer('Notch'));
     * ```
     *
     * @param alias The format of the command to alias
     * @param mapper A function to map from the input to some new output. The default is a direct mapping.
     * @return an executable command
     */
    make(alias: string, mapper?: (any) => any): (...args: any[]) => any;
}

/**
 * Configuration options for command executions.
 *
 * Applied with {@link GlobalExecutor#with}.
 */
declare interface ExecutionOptions {
    /** A proxy sender of the command. The default is console. */
    as?: any;
    /** If true, no `sendMessage` calls will be executed. The default is false. */
    silent?: boolean;
}

/**
 * Represents the "handler" for a command. It will be called
 * whenever the command is executed.
 *
 * @param sender The source of the command
 * @param context Command metadata such as argument values
 */
declare type CommandCallback = (sender: any, context: any) => void;

/**
 * Executed whenever a signal is triggered.
 *
 * @param properties The signal properties, specific to each signal
 * @param output An output object, can be arbitrary data
 */
declare type SignalCallback = (properties: any, output?: any) => void;

declare class GlobalExecutor implements Executor {

    /**
     * @inheritDoc
     */
    run(...args: any[]): any;

    /**
     * @inheritDoc
     */
    make(alias: string, mapper?: (any) => any): (...args: any[]) => any;

    /**
     * Generates an executor with the given options.
     *
     * @param options The options to use for command execution
     */
    with(options: ExecutionOptions): Executor;

    /**
     * Registers a new command to the server.
     *
     * todo explanation of syntax
     *
     * @param syntax The command syntax (see format)
     * @param callback The execution callback
     */
    registerCommand(syntax: string, callback: CommandCallback): void;

    /**
     * Adds a handler to the given signal. The signal can be a pre defined game event,
     * or a custom signal from this script or another.
     *
     * @param type The signal to listen for
     * @param callback The function to execute when the signal is triggered
     */
    onSignal(type: signals.SignalType, callback: SignalCallback): void;

    /**
     * Triggers the given signal type with the given properties.
     *
     * @param type The signal to trigger
     * @param properties The data associated with the signal
     */
    signal(type: signals.SignalType, properties: any): any;
}

declare namespace signals {
    export const PLAYER_USE_ITEM_ON_BLOCK = 'player_use_item_on_block';
    export const PLAYER_ENTITY_INTERACT = 'player_entity_interact';

    // Just `string` in reality, however it is left here to be changed in the future with less breaking
    type SignalType = typeof PLAYER_USE_ITEM_ON_BLOCK;
}

/**
 * Global executor reference for use in scripts.
 */
declare const executor: GlobalExecutor;