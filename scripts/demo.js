console.log("debug from javascript!")

executor.registerFunction("test", test)

function test(properties) {
    console.log("function executed " + properties.value)
}

executor.onSignal("entity_interact", onSignal)

function onSignal(properties) {
    console.log("test! " + properties.player.username)
}