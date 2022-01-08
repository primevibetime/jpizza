package lemon.jpizza;

import lemon.jpizza.compiler.ChunkBuilder;
import lemon.jpizza.compiler.Compiler;
import lemon.jpizza.compiler.FunctionType;
import lemon.jpizza.compiler.values.Var;
import lemon.jpizza.compiler.values.functions.JFunc;
import lemon.jpizza.compiler.vm.VM;
import lemon.jpizza.compiler.vm.VMResult;
import lemon.jpizza.errors.Error;
import lemon.jpizza.generators.Lexer;
import lemon.jpizza.generators.Optimizer;
import lemon.jpizza.generators.Parser;
import lemon.jpizza.nodes.Node;
import lemon.jpizza.nodes.expressions.BodyNode;
import lemon.jpizza.results.ParseResult;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Shell {

    public static final Logger logger = new Logger();
    public static String root;
    public static VM vm;
    public static final Map<String, Var> globals = new HashMap<>();
    public static final String fileEncoding = System.getProperty("file.encoding");

    static class Flags {
        public static final int COMPILE   = 0b0001;
        public static final int RUN       = 0b0010;
        public static final int REFACTOR  = 0b0100;
        public static final int SHELL     = 0b1000;
    }

    public static String[] getFNDirs(String dir) {
        int ind = dir.lastIndexOf('\\');
        if (ind == -1)
            return new String[]{dir, "."};
        return new String[]{
                dir.substring(ind),
                dir.substring(0, ind)
        };
    }

    static boolean hasFlag(int target, int flag) {
        return (target & flag) == flag;
    }

    @SuppressWarnings("DuplicatedCode")
    public static void main(String[] args) throws IOException {
        root = System.getenv("JPIZZA_DATA_DIR") == null ? System.getProperty("user.home") + "/.jpizza" : System.getenv("JPIZZA_DATA_DIR");

        int flags = Flags.SHELL;
        String to = null;
        String target = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-c":
                    flags |= Flags.COMPILE;
                    break;
                case "-rf":
                    flags |= Flags.REFACTOR;
                    break;
                case "-r":
                    flags |= Flags.RUN;
                    break;
                case "-o":
                    if (i + 1 < args.length) {
                        to = args[i + 1];
                        i++;
                    }
                    else {
                        Shell.logger.fail("-o requires an argument");
                    }
                    break;
                default:
                    if (args[i].startsWith("-")) {
                        Shell.logger.fail("Unknown option: " + args[i]);
                    }
                    else {
                        target = args[i];
                    }
                    break;
            }
            flags &= ~Flags.SHELL;
        }

        if (flags == Flags.SHELL) repl();

        if (hasFlag(flags, Flags.COMPILE) && hasFlag(flags, Flags.RUN)) {
            Shell.logger.fail("Cannot compile and run at the same time");
        }

        if (hasFlag(flags, Flags.REFACTOR)) {
            Shell.logger.enableTips();
        }

        if (Objects.equals(target, "help")) {
            Shell.logger.outln("Usage: jpizza [options] [target]");
            Shell.logger.outln("Options:");
            Shell.logger.outln("  -c\t\tCompile target");
            Shell.logger.outln("  -r\t\tRun target");
            Shell.logger.outln("  -o [target]\tOutput target to file");
            Shell.logger.outln("  -rf\t\tRefactor target");
            Shell.logger.outln("Targets:");
            Shell.logger.outln("  help\t\tPrint this help message");
            Shell.logger.outln("  <file>\tCompile and/or run file");
            Shell.logger.outln("  v\t\tPrint version");
            Shell.logger.outln("  docs\t\tPrint documentation");
        }
        else if (Objects.equals(target, "version")) {
            Shell.logger.outln("jpizza version " + VM.VERSION);
        }
        else if (Objects.equals(target, "docs")) {
            Shell.logger.outln("Documentation can be found at https://jpizza.readthedocs.io/en/latest/");
        }
        else if (target == null && !(flags == Flags.SHELL)) {
            Shell.logger.fail("No target specified");
        }
        else {
            // .devp is the raw file format
            // It contants the source code
            Path path = Paths.get(args[0]);
            if (target.endsWith(".devp")) {
                if (Files.exists(path)) {
                    String scrpt = Files.lines(path).collect(Collectors.joining("\n"));

                    String dir = path.toString();

                    String[] dsfn = getFNDirs(dir);
                    String fn = dsfn[0];
                    String newDir = dsfn[1];

                    System.setProperty("user.dir", newDir);
                    if (hasFlag(flags, Flags.RUN)) {
                        Pair<JFunc, Error> res = compile(fn, scrpt);
                        if (res.b != null)
                            Shell.logger.fail(res.b.asString());
                        runCompiled(fn, res.a, args);
                    }
                    else if (hasFlag(flags, Flags.COMPILE)) {
                        to = to == null ? newDir + "\\" + fn.substring(0, fn.length() - 5) + ".jbox" : to + ".jbox";
                        Error e = compile(fn, scrpt, to);
                        if (e != null)
                            Shell.logger.fail(e.asString());
                    }
                }
                else {
                    Shell.logger.fail("File does not exist.");
                }
            }
            // .jbox is the compiled file format
            // It contains the bytecode
            else if (target.endsWith(".jbox")) {
                if (hasFlag(flags, Flags.COMPILE)) {
                    Shell.logger.fail("Cannot compile a compiled file");
                }
                else if (hasFlag(flags, Flags.REFACTOR)) {
                    Shell.logger.fail("Cannot refactor a compiled file");
                }
                else if (to != null) {
                    Shell.logger.fail("Cannot output to a compiled file");
                }
                else if (hasFlag(flags, Flags.RUN)) {
                    // Run the compiled file
                    if (Files.exists(path)) {
                        String dir = path.toString();

                        String[] dsfn = getFNDirs(dir);
                        String fn = dsfn[0];
                        String newDir = dsfn[1];

                        System.setProperty("user.dir", newDir);
                        runCompiled(fn, args[0], args);
                    }
                    else {
                        Shell.logger.fail("File does not exist.");
                    }
                }
            }
        }

    }

    public static void repl() {
        Scanner in = new Scanner(System.in);

        Shell.logger.outln("Exit with 'quit'");
        Shell.logger.enableTips();

        while (true) {
            Shell.logger.out("-> ");
            String input = in.nextLine() + ";";

            if (input.equals("quit;"))
                break;
            //  compile("<shell>", input, "shell.jbox");
            Pair<JFunc, Error> a = compile("<shell>", input);
            if (a.b != null) {
                Shell.logger.fail(a.b.asString());
            }
            else {
                runCompiled("<shell>", a.a, new String[]{"<shell>"}, globals);
            }
        }
        in.close();
        System.exit(0);
    }

    public static Pair<List<Node>, Error> getAst(String fn, String text) {
        Lexer lexer = new Lexer(fn, text);
        Pair<List<Token>, Error> x = lexer.make_tokens();
        List<Token> tokens = x.a;
        Error error = x.b;
        if (error != null)
            return new Pair<>(null, error);
        Parser parser = new Parser(tokens);
        ParseResult<Node> ast = parser.parse();
        if (ast.error != null)
            return new Pair<>(null, ast.error);
//        if (Shell.logger.debug)
//            Shell.logger.debug(TreePrinter.print(ast.node));
        BodyNode body = (BodyNode) Optimizer.optimize(ast.node);
        return new Pair<>(body.statements, null);
    }

    public static Pair<JFunc, Error> compile(String fn, String text) {
        return compile(fn, text, false);
    }

    public static Pair<JFunc, Error> compile(String fn, String text, boolean scope) {
        Pair<List<Node>, Error> ast = getAst(fn, text);
        if (ast.b != null) return new Pair<>(null, ast.b);
        List<Node> outNode = ast.a;

        Compiler compiler = new Compiler(FunctionType.Script, text);

        if (scope)
            compiler.beginScope();
        JFunc func = compiler.compileBlock(outNode);
        if (scope)
            compiler.endScope(ast.a.get(0).pos_start, ast.a.get(ast.a.size() - 1).pos_end);

        return new Pair<>(func, null);
    }

    public static Error compile(String fn, String text, String outpath) {
        Pair<JFunc, Error> res = compile(fn, text);
        if (res.b != null) return res.b;
        JFunc func = res.a;

        try {
            FileOutputStream fout;
            fout = new FileOutputStream(outpath);
            fout.write(func.dumpBytes());
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new Error(
                    null, null,
                    "Internal",
                    "Could not write to file"
            );
        }

        return null;
    }

    public static JFunc load(String inpath) {
        try {
            Path path = Paths.get(inpath);
            if (!Files.exists(path)) {
                Shell.logger.fail("File does not exist!");
            }
            byte[] arr = Files.readAllBytes(path);
            return ChunkBuilder.Build(arr);
        } catch (IOException e) {
            Shell.logger.fail("File is not readable!");
        }
        return null;
    }

    public static void runCompiled(String fn, JFunc func, String[] args) {
        runCompiled(fn, func, args, new HashMap<>());
    }

    public static void runCompiled(String fn, JFunc func, String[] args, Map<String, Var> globals) {
        vm = new VM(func, globals).trace(fn);
        VMResult res = vm.run();
        if (res == VMResult.ERROR) return;
        vm.finish(args);
    }

    public static void runCompiled(String fn, String inpath, String[] args) {
        try {
            Path path = Paths.get(inpath);
            if (!Files.exists(path)) {
                Shell.logger.fail("File does not exist!");
            }
            byte[] arr = Files.readAllBytes(path);

            JFunc func = ChunkBuilder.Build(arr);
            vm = new VM(func).trace(fn);

            VMResult res = vm.run();
            if (res == VMResult.ERROR) return;
            vm.finish(args);

        } catch (IOException e) {
            Shell.logger.fail("File is not readable!");
        }
    }

}
