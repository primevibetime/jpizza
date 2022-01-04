package lemon.jpizza.compiler;

public class OpCode {
    public static final int Return = 0x00;
    public static final int Constant = 0x01;
    public static final int Negate = 0x02;
    public static final int Increment = 0x03;
    public static final int Decrement = 0x04;
    public static final int Add = 0x05;
    public static final int Subtract = 0x06;
    public static final int Multiply = 0x07;
    public static final int Divide = 0x08;
    public static final int Modulo = 0x09;
    public static final int Power = 0x0A;
    public static final int Equal = 0x0B;
    public static final int LessThan = 0x0C;
    public static final int GreaterThan = 0x0D;
    public static final int Not = 0x0E;
    public static final int Pop = 0x0F;
    public static final int SetGlobal = 0x10;
    public static final int DefineGlobal = 0x11;
    public static final int GetGlobal = 0x12;
    public static final int GetLocal = 0x13;
    public static final int SetLocal = 0x14;
    public static final int Jump = 0x16;
    public static final int JumpIfFalse = 0x17;
    public static final int JumpIfTrue = 0x18;
    public static final int Loop = 0x19;
    public static final int For = 0x1A;
    public static final int StartCache = 0x1B;
    public static final int CollectLoop = 0x1C;
    public static final int FlushLoop = 0x1D;
    public static final int DefineLocal = 0x1E;
    public static final int Pattern = 0x1F;
    public static final int Call = 0x20;
    public static final int Closure = 0x21;
    public static final int GetUpvalue = 0x22;
    public static final int SetUpvalue = 0x23;
    public static final int MakeArray = 0x24;
    public static final int MakeMap = 0x25;
    public static final int Class = 0x26;
    public static final int Access = 0x27;
    public static final int Method = 0x28;
    public static final int MakeVar = 0x29;
    public static final int SetAttr = 0x2A;
    public static final int GetAttr = 0x2B;
    public static final int Index = 0x2C;
    public static final int Get = 0x2D;
    public static final int Null = 0x2E;
    public static final int Assert = 0x2F;
    public static final int Throw = 0x30;
    public static final int Import = 0x31;
    public static final int Enum = 0x32;
    public static final int BitAnd = 0x33;
    public static final int BitOr = 0x34;
    public static final int BitXor = 0x35;
    public static final int BitCompl = 0x36;
    public static final int LeftShift = 0x37;
    public static final int RightShift = 0x38;
    public static final int SignRightShift = 0x39;
    public static final int Copy = 0x3A;
    public static final int Iter = 0x3B;
    public static final int Spread = 0x3C;
    public static final int Ref = 0x3D;
    public static final int Deref = 0x3E;
    public static final int SetRef = 0x3F;
    public static final int ToBytes = 0x40;
    public static final int FromBytes = 0x41;
    public static final int NullErr = 0x42;
    public static final int Chain = 0x43;
    public static final int DropLocal = 0x44;
    public static final int DropGlobal = 0x45;
    public static final int DropUpvalue = 0x46;
    public static final int Header = 0x47;
    public static final int Destruct = 0x48;
    public static final int PatternVars = 0x49;
    public static final int IncrNullErr = 0x4A;
    public static final int Extend = 0x4B;
}
