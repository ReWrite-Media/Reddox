executor.registerCommand("myscript Integer<value>", (sender, context) => {
    executor.run("display tellraw", sender, "<green>You typed " + context.value)
})