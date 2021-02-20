console.log("debug from javascript!")

executor.registerFunction("test", test)

function test(properties) {
    console.log("function executed " + properties.value.key)
}