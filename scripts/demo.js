console.log("debug from javascript!")

executor.registerFunction("test", test)

function test(properties) {
    console.log("function executed " + properties.value)
}

executor.registerListener("move", onSignal)

function onSignal(properties) {
    let player = properties.player
    executor.runAs(player, "entity give TheMode911 minecraft:diamond 1")
    console.log("Player moved! " + player.username)
}