// Should stop intellij from getting mad about executor methods
const executor = {
    onSignal() {
    },
    registerCommand() {
    },
    alias() {
    },
    make() {
    },
    run() {
    }
}
// Minescript interacts with the server using various commands and signals.
// Commands allow you to take action, and signal to listen to certain events.

// Each of those relies on properties. Commands and signals provide you context
// as properties.

// Run command with a single method, and retrieve its returned data
let data = executor.run("health add Notch 5")
console.log("The player has now " + data.health + " health!")

executor.onSignal("move", (properties) => {
    let player = properties.player

    // Commands can be run as another sender
    // TODO: options to ignore player permission & prevent from being sent
    executor.runAs(player, "say I am moving!")
    console.log("the player " + player.username + " moved to " + properties.position)
});

executor.onSignal("move", (properties, output) => {
    // Each signal has an optional "output" properties which
    // will be forwarded to following listeners and the signal caller.
    output.cancel = false
});

// Commands

// Commands are defined by a syntax and an executor, the syntax consists
// of a single string, and the executor is a callback.
executor.registerCommand('test [name: String] [value: Integer]', (context) => {
    // The context gives us data relative to the command execution.
    // In this case, the argument values.
    let name = context.name
    let value = context.value
    console.log("Command 'test' with the name " + name + " and value " + value)
});

// Functions can be made from commands,
// they allow you to define your own syntax
const healPlayer = executor.make('heal {0} {1}') // -> /heal <player> <value>
const success = healPlayer("Notch", 5).success; // -> /heal Notch 5
console.log("heal status: " + success)


// Filtering
// TODO

//////////////////////
// Text command use //
//////////////////////

const somePlayer = executor.run("entity players get TheMode").player

// Titles & Actionbars
const sendTitle = executor.make("text title send title {0} 5s {1}")
const sendActionbar = executor.make("text title send actionbar {0} 2s {1}")
sendTitle(somePlayer, "Title text") // Will display for 5 seconds
sendActionbar(somePlayer, "Actionbar text") // Will display for 2 seconds

// Bossbars

// Create a bossbar called "exampleBossbar"
executor.run("text bossbar modify create", "exampleBossbar", "Bossbar Title")

// Add a player named "TheMode" to the Bossbar
executor.run("text bossbar modify addplayer", "exampleBossbar", "TheMode")

// Make a function to easily edit the bossbar's features
const modifyBossbar = executor.make("text bossbar modify set exampleBossbar {0} {1}")

// Set the bossbars features
modifyBossbar("color", "red")
modifyBossbar("max value", 10)
modifyBossbar("value", 6)
modifyBossbar("segment amount", 12)

// Schedule the bossbar to be deleted in 5 seconds
setTimeout(() => {
    executor.run("text bossbar modify removeplayer", "exampleBossbar", "TheMode")
    executor.run("text bossbar remove", "exampleBossbar")
}, 5000);
