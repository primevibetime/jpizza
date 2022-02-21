package lemon.jpizza.compiler.values.functions;

import lemon.jpizza.compiler.values.Value;

import java.util.ArrayList;
import java.util.List;

public class JNative {

    final String name;
    final Method method;
    final int argc;
    final List<String> types;
    public JNative(String name, Method method, int argc, List<String> types) {
        this.name = name;
        this.method = method;
        this.argc = argc;
        this.types = types;
    }

    public JNative(String name, Method method, int argc) {
        this(name, method, argc, new ArrayList<>());
        for (int i = 0; i < argc; i++)
            types.add("any");
    }

    public NativeResult call(Value[] args) {
        if (args.length != argc)
            return NativeResult.Err("Argument Count", "Expected " + argc + " arguments, got " + args.length);

        for (int i = 0; i < argc; i++) {
            String t = args[i].type();
            if (!types.get(i).equals("any") && !t.equals(types.get(i)))
                return NativeResult.Err("Type", "Expected " + types.get(i) + " for argument " + i + ", got " + t);
        }

        return method.call(args);
    }

    public String toString() {
        return "<function-" + name + ">";
    }

    public interface Method {
        NativeResult call(Value[] stack);
    }
}
