package lemon.jpizza.nodes.definitions;

import lemon.jpizza.JPType;
import lemon.jpizza.Token;
import lemon.jpizza.nodes.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FuncDefNode extends Node {
    public final Token var_name_tok;
    public final List<Token> arg_name_toks;
    public final Node body_node;
    public final boolean autoreturn;
    public final boolean async;
    public final List<Token> arg_type_toks;
    public final List<Token> generic_toks;
    public final List<String> returnType;
    public final List<Node> defaults;
    public final int defaultCount;
    public final String argname;
    public final String kwargname;
    public boolean catcher = false;

    public FuncDefNode(Token var_name_tok, List<Token> arg_name_toks, List<Token> arg_type_toks, Node body_node,
                       boolean autoreturn, boolean async, List<String> returnType, List<Node> defaults, int defaultCount,
                       List<Token> generic_toks, String argname, String kwargname) {
        this.var_name_tok = var_name_tok;
        this.argname = argname;
        this.kwargname = kwargname;
        this.generic_toks = generic_toks;
        this.async = async;
        this.arg_name_toks = arg_name_toks;
        this.arg_type_toks = arg_type_toks;
        this.body_node = body_node;
        this.autoreturn = autoreturn;
        this.returnType = returnType;
        this.defaults = defaults;
        this.defaultCount = defaultCount;

        pos_start = var_name_tok != null ? var_name_tok.pos_start : (
                arg_name_toks != null && arg_name_toks.size() > 0 ? arg_name_toks.get(0).pos_start : body_node.pos_start
        );
        pos_end = body_node.pos_end;
        jptype = JPType.FuncDef;
    }

    public FuncDefNode setCatcher(boolean c) {
        this.catcher = c;
        return this;
    }

    @Override
    public Node optimize() {
        Node body = body_node.optimize();
        List<Node> optimizedDefaults = new ArrayList<>();
        for (Node n : defaults) {
            optimizedDefaults.add(n.optimize());
        }
        return new FuncDefNode(var_name_tok, arg_name_toks, arg_type_toks, body, autoreturn, async, returnType,
                optimizedDefaults, defaultCount, generic_toks, argname, kwargname).setCatcher(catcher);
    }

    @Override
    public List<Node> getChildren() {
        return new ArrayList<>(Collections.singletonList(body_node));
    }

    @Override
    public String visualize() {
        return "fn" + (
                var_name_tok != null ?
                        " " + var_name_tok.value : ""
        ) + "(" + arg_name_toks.stream().map(x -> x.value.toString()).collect(Collectors.joining(", ")) + ")";
    }
}
