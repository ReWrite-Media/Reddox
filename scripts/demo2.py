print("python init")

def callback(properties):
    print("The player "+properties.player.username+" moved!")

executor.onSignal("move", callback)