package net.minestom.script.utils;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class TypeScriptTranspiler {

    private static final Value TRANSPILE_MODULE_FUNCTION;

    static {
        URL url;
        try {
            url = new URL("https://cdnjs.cloudflare.com/ajax/libs/typescript/3.4.3/typescript.js");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new IllegalStateException("typescript transpiler link is not working");
        }
        Context context = Context.create("js");
        Source source = Source.newBuilder("js", url).buildLiteral();
        context.eval(source);
        TRANSPILE_MODULE_FUNCTION = context.eval("js", "ts.transpileModule");
    }

    @NotNull
    public static String transpile(@NotNull String source) {
        assert TRANSPILE_MODULE_FUNCTION != null;
        var result = TRANSPILE_MODULE_FUNCTION.execute(source, ProxyObject.fromMap(Map.of("lib", "es2020")));
        var outputText = result.getMember("outputText").asString();
        assert result.getMember("diagnostics").getArraySize() == 0;
        return outputText;
    }


}
