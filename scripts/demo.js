console.log("debug from javascript!")

executor.registerFunction("test", test)

function test(properties) {
    console.log("function executed " + properties.value)
}

executor.onSignal("use_item", onSignal)

function onSignal(properties) {
    let item = properties.item
    console.log("Use item! " + item + " " + item.material)
}