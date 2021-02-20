package net.minestom.script;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScriptProperties implements ProxyObject {

    private final Map<String, Value> properties = new ConcurrentHashMap<>();

    @Override
    public Object getMember(String key) {
        return properties.get(key);
    }

    @Override
    public Object getMemberKeys() {
        return properties.keySet().toArray(new String[0]);
    }

    @Override
    public boolean hasMember(String key) {
        return properties.containsKey(key);
    }

    @Override
    public void putMember(String key, Value value) {
        this.properties.put(key, value);
    }
}
