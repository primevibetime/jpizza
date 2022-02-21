package lemon.jpizza.nodes.values;

import lemon.jpizza.JPType;
import lemon.jpizza.Position;
import lemon.jpizza.Token;
import lemon.jpizza.TokenType;
import lemon.jpizza.nodes.Node;
import org.jetbrains.annotations.NotNull;

public class NumberNode extends ValueNode {
    public final double val;
    public final boolean hex;

    public NumberNode(Token tok) {
        super(tok);
        val = (double) tok.value;
        hex = false;
        jptype = JPType.Number;
    }

    public NumberNode(Token tok, boolean hex) {
        super(tok);
        val = (double) tok.value;
        this.hex = hex;
        jptype = JPType.Number;
    }

    public NumberNode(int v, @NotNull Position pos_start, @NotNull Position pos_end) {
        super(new Token(TokenType.Identifier, "null", pos_start, pos_end));
        val = v;
        hex = true;
        jptype = JPType.Number;
    }

    public NumberNode(double v, @NotNull Position pos_start, @NotNull Position pos_end) {
        super(new Token(TokenType.Identifier, "null", pos_start, pos_end));
        val = v;
        hex = true;
        jptype = JPType.Number;
    }

    @Override
    public double asNumber() {
        return val;
    }

    @Override
    public boolean asBoolean() {
        return val != 0;
    }

    @Override
    public String asString() {
        return String.valueOf(val);
    }

    @Override
    public boolean equals(Node other) {
        if (other instanceof NumberNode) {
            return val == ((NumberNode) other).val;
        }
        return false;
    }

    @Override
    public String visualize() {
        return String.valueOf(val);
    }
}
