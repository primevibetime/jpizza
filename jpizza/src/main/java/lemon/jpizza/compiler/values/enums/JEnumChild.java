package lemon.jpizza.compiler.values.enums;

import lemon.jpizza.compiler.ChunkCode;
import lemon.jpizza.compiler.values.Value;
import lemon.jpizza.compiler.values.classes.ClassAttr;
import lemon.jpizza.compiler.values.classes.Instance;
import lemon.jpizza.compiler.vm.VM;

import java.util.*;

public class JEnumChild {
    // For Enum Props
    public final List<String> props;
    public final List<List<String>> propTypes;
    public final List<String> generics;
    public final List<Integer> genericSlots;
    public final int arity;
    public final int genericArity;
    final int value;
    private final Value asValue;
    JEnum parent;

    public JEnumChild(int value, List<String> props, List<List<String>> propTypes, List<String> generics, List<Integer> genericSlots) {
        this.value = value;
        this.props = props;
        this.propTypes = propTypes;
        this.generics = generics;
        this.genericSlots = genericSlots;

        this.arity = props.size();
        this.genericArity = generics.size();

        this.asValue = new Value(this);
    }

    public int getValue() {
        return value;
    }

    public boolean equals(JEnumChild other) {
        return value == other.value;
    }

    public String type() {
        return parent.name();
    }

    public JEnum getParent() {
        return parent;
    }

    public void setParent(JEnum jEnum) {
        parent = jEnum;
    }

    public Value create(Value[] args, String[] types, String[] resolvedGenerics, VM vm) {
        Map<String, ClassAttr> fields = new HashMap<>();
        for (int i = 0; i < props.size(); i++) {
            fields.put(
                    props.get(i),
                    new ClassAttr(args[i], types[i])
            );
        }

        fields.put("$child", new ClassAttr(new Value(value)));
        fields.put("$parent", new ClassAttr(new Value(parent)));

        StringBuilder type = new StringBuilder(parent.name());
        if (generics.size() > 0) {
            type.append("(");
            for (int i = 0; i < generics.size(); i++) {
                type.append("(").append(resolvedGenerics[i]).append(")");
            }
            type.append(")");
        }
        // Normal: EnumChild
        // Generic: EnumChild((Type1)(Type2)(etc))
        return new Value(new Instance(type.toString(), fields, vm));
    }

    public Value asValue() {
        return asValue;
    }

    public int[] dump() {
        List<Integer> dump = new ArrayList<>(Arrays.asList(ChunkCode.EnumChild, value, genericSlots.size()));
        dump.addAll(genericSlots);
        dump.add(props.size());
        for (String prop : props) {
            Value.addAllString(dump, prop);
        }
        dump.add(propTypes.size());
        for (List<String> propType : propTypes) {
            dump.add(ChunkCode.Type);
            dump.add(propType.size());
            for (String propTypePart : propType) {
                Value.addAllString(dump, propTypePart);
            }
        }
        dump.add(generics.size());
        for (String generic : generics) {
            Value.addAllString(dump, generic);
        }
        return dump.stream().mapToInt(i -> i).toArray();
    }
}
