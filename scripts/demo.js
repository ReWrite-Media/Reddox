console.log("debug from javascript!")

// Register signals and api functions
executor.onSignal(signals.PLAYER_USE_ITEM_ON_BLOCK, onUse)
executor.onSignal("player_entity_interact", onInteract)

const createEntity = executor.make("entity editor create {0}")
const killEntity = executor.make("entity kill {0}")

function onUse(properties) {
    let blockPosition = properties.block.position
    blockPosition.x += 0.5
    blockPosition.y += 1
    blockPosition.z += 0.5

    let data = createEntity("minecraft:skeleton " + blockPosition)

    if (data.success) {
        console.log(`You created the entity ${data.entity.uuid} successfully!`)
    } else {
        console.log("failure")
    }
}

function onInteract(properties) {
    let targetUuid = properties.target.uuid

    killEntity(targetUuid)
    console.log("entity killed")
}

const object = {
    id: "c001",
    name: "Hello Test",
    array: [2, 3],
    float: 3.5,
    number: {
        num: 5
    }
};
executor.run("utils map set Test:test",object)
let data = executor.run("utils map get Test:test")
console.log("debug: "+data.value.array[1])

executor.run("display bossbar create test <red>name")

executor.onSignal("player_join", (properties) => {
    console.log("join "+properties.player.username)
});

let get = executor.make("utils map get {0}");
data = get("Test:test");
console.log("debug: " + data.value.id);

let getMapped = executor.make("utils map get {0}", data => data.value);
data = getMapped("Test:test");
console.log("debug: " + data.float);

let objArrayMoreThan2 = executor.make("utils map get test:Test",
        data => data.value.array.filter(value => value > 2));
data = objArrayMoreThan2();
console.log("debug: " + data);