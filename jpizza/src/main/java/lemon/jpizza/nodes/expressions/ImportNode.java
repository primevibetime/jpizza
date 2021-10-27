package lemon.jpizza.nodes.expressions;

import lemon.jpizza.Constants;
import lemon.jpizza.contextuals.Context;
import lemon.jpizza.errors.Error;
import lemon.jpizza.errors.RTError;
import lemon.jpizza.generators.Interpreter;
import lemon.jpizza.objects.Obj;
import lemon.jpizza.objects.executables.ClassInstance;
import lemon.jpizza.results.RTResult;
import lemon.jpizza.Shell;
import lemon.jpizza.Token;
import lemon.jpizza.nodes.Node;
import lemon.jpizza.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImportNode extends Node {
    public final Token file_name_tok;
    public final Token as_tok;

    public ImportNode(Token file_name_tok) {
        this.file_name_tok = file_name_tok;
        this.as_tok = null;

        pos_start = file_name_tok.pos_start.copy(); pos_end = file_name_tok.pos_end.copy();
        jptype = Constants.JPType.Import;
    }

    public ImportNode(Token file_name_tok, Token as_tok) {
        this.file_name_tok = file_name_tok;
        this.as_tok = as_tok;

        pos_start = file_name_tok.pos_start.copy(); pos_end = as_tok.pos_end.copy();
        jptype = Constants.JPType.Import;
    }

    public RTResult vis(Context context) throws IOException {
        RTResult res = new RTResult();

        String fn = (String) file_name_tok.value;
        String file_name = System.getProperty("user.dir") + "/" + fn + ".devp";

        String modPath = Shell.root + "/modules/" + fn;
        String modFilePath = modPath + "/" + fn + ".devp";

        //noinspection ResultOfMethodCallIgnored
        new File(Shell.root + "/modules").mkdirs();

        Obj imp = null;

        String userDataDir = System.getProperty("user.dir");
        if (Constants.LIBRARIES.containsKey(fn)) {
            imp = new ClassInstance(Constants.LIBRARIES.get(fn), fn)
                    .set_pos(pos_start, pos_end)
                    .set_context(context);
        } else if (Constants.STANDLIBS.containsKey(fn)) {
            Pair<ClassInstance, Error> pair = Shell.imprt(fn, Constants.STANDLIBS.get(fn), context, pos_start);
            if (pair.b != null) return res.failure(pair.b);
            imp = pair.a;
        } else {
            if (Files.exists(Paths.get(modFilePath))) {
                System.setProperty("user.dir", modPath);

                imp = res.register(Interpreter.getImprt(modFilePath, fn, context, pos_start, pos_end));

                System.setProperty("user.dir", userDataDir);
            }
            else if (Files.exists(Paths.get(file_name)))
                imp = res.register(Interpreter.getImprt(file_name, fn, context, pos_start, pos_end));
            
            if (res.error != null) 
                return res;
        }
        if (imp == null) return res.failure(RTError.FileNotFound(
                pos_start, pos_end,
                "Module does not exist",
                context
        ));

        String as = fn;
        if (as_tok != null)
            as = as_tok.value.toString();

        context.symbolTable.define(as, imp);
        return res.success(imp);
    }

    public RTResult visit(Interpreter inter, Context context) {
        try {
            return vis(context);
        } catch (IOException e) {
            return new RTResult().failure(RTError.Internal(
                    pos_start, pos_end,
                    e.toString(),
                    context
            ));
        }
    }

}
