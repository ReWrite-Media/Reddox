executor.registerCommand("myscript", (sender, context) => {
    let getPlayer = executor.make("entity query {0}", data => data.entities[0])
    console.log("random player: "+getPlayer("@p"))
})