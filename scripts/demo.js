console.log("debug from javascript!")

executor.registerFunction("test", test)

function test(properties) {
    console.log("function executed " + properties.value)
}

executor.onSignal("move", onSignal)

function onSignal(properties) {
    let player = properties.player
    console.log("Player moved! " + player.username+" "+player.type)
}