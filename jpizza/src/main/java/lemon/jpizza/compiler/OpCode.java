package lemon.jpizza.compiler;

public class OpCode {
    public static final int Return = 0;
    public static final int Constant = 1;
    public static final int Negate = 2;
    public static final int Increment = 3;
    public static final int Decrement = 4;
    public static final int Add = 5;
    public static final int Subtract = 6;
    public static final int Multiply = 7;
    public static final int Divide = 8;
    public static final int Modulo = 9;
    public static final int Power = 10;
    public static final int Equal = 11;
    public static final int LessThan = 12;
    public static final int GreaterThan = 13;
    public static final int Not = 14;
    public static final int Pop = 15;
    public static final int SetGlobal = 16;
    public static final int DefineGlobal = 17;
    public static final int GetGlobal = 18;
    public static final int GetLocal = 19;
    public static final int SetLocal = 20;
    public static final int PushTraceback = 21;
    public static final int PopTraceback = 22;
    public static final int Jump = 23;
    public static final int JumpIfFalse = 24;
    public static final int JumpIfTrue = 25;
    public static final int Loop = 26;
    public static final int For = 27;
    public static final int StartCache = 28;
    public static final int CollectLoop = 29;
    public static final int FlushLoop = 30;
    public static final int DefineLocal = 31;
    public static final int GetGeneric = 32;
}
