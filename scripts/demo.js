console.log("debug from javascript!")

executor.registerFunction("test", test)

function test(properties) {
    console.log("function executed " + properties.value)
}

executor.registerListener("move", onSignal)

function onSignal(properties) {
    //console.log("Signal called! "+properties.position.x)
}