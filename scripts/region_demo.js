/*let regionName = "test_region"

executor.registerFunction("init_region", () => {
    executor.run("world region create " + regionName + " -2 -100 -2 2 100 2")
})

executor.onSignal("move", (properties) => {
    let data = executor.runAs(properties.player, "world region function is_inside " + regionName + " ~ ~ ~")
    console.log("debug console: " + data.inside)
})*/