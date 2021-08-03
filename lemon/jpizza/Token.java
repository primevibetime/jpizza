package lemon.jpizza;

import java.io.Serializable;

public class Token implements Serializable {
    public Tokens.TT type;
    public Object value;
    public Position pos_start;
    public Position pos_end;

    public Token(Tokens.TT type, Object value, Position pos_start, Position pos_end) {
        this.type = type;
        this.value = value;

        if (pos_start != null) {
            this.pos_start = pos_start.copy();
            this.pos_end = pos_end != null ? pos_end.copy() : pos_start.copy().advance();
        }
    }

    public Token(Tokens.TT type) {
        this.type = type;
        this.value = null;

        this.pos_start = null;
        this.pos_end = null;
    }

    public Token(Tokens.TT type, Object value) {
        this.type = type;
        this.value = value;

        this.pos_start = null;
        this.pos_end = null;
    }

    public Token(Tokens.TT type, Position start_pos) {
        this.type = type;
        this.value = null;

        this.pos_start = start_pos.copy();
        this.pos_end = start_pos.copy().advance();
    }

    public Token(Tokens.TT type, Position start_pos, Position end_pos) {
        this.type = type;
        this.value = null;

        this.pos_start = start_pos.copy();
        this.pos_end = end_pos.copy();
    }

    public boolean matches(Tokens.TT type, Object value) {
        return this.type.equals(type) && this.value.equals(value);
    }

    public String toString() {
        return value != null ? String.format(
                "%s:%s",
                type, value
        ) : String.valueOf(type);
    }
}
