console.log("debug from javascript!")

// Register signals and api functions
executor.registerFunction("test", test)
executor.onSignal(signals.USE_ITEM_BLOCK, onUse)
executor.onSignal("entity_interact", onInteract)


function test(properties) {
    console.log("function executed " + properties.value)
}

function onUse(properties) {
    let id = Math.floor(Math.random() * 1000)
    let entity = "minecraft:skeleton"

    let blockPosition = properties.block.position
    blockPosition.y = blockPosition.y + 1
    blockPosition.x = blockPosition.x + 0.5
    blockPosition.z = blockPosition.z + 0.5

    let data = executor.run("entity editor init " + id + " " + entity + " " + blockPosition)

    if (data.success) {
        console.log("success!")
    } else {
        console.log("failure")
    }
}

function onInteract(properties) {
    let targetUuid = properties.target.uuid

    executor.run("entity kill " + targetUuid)
    console.log("entity killed")
}

executor.onSignal("move", (properties, output) => {
    output.cancel = false
})