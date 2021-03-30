executor.registerCommand("myscript", (sender, context) => {
    let data = executor.run("entity query TheMode911")
    let entities = data.entities
    console.log("entities: "+entities[0].username)
})