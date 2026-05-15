package it.univr.pl;

import it.univr.pl.type.ErrType;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class TypedImpCastIntpTest {
    private static final String PROG =
            """
            int v = 9;
            string s;
            dec x;
            int i = 0;
            while (i < 5) {
              v = v + 1;
              i = i + 1
            };
            if (false) {
              int j = 0;
              j = j + 1;
              i = i + 11
            } else {
              int f = 0;
              f = f - 1;
              print toStr(f);
              i = i + 9
            };
            print toStr(v);
            print toStr("hello \\\\ world");
            s = "3";
            print s : "  " : toStr(8.7);
            x = 3.1;
            print toStr(4 + x); // implicit cast
            int k;
            k = (int) x // downcast
            """;

    public static void main(String[] args) {
        ParseTree tree = runTS(PROG);
        if (tree != null) {
            Mem res = runIntp(tree);
            if (res != null) {
                System.out.println(res);
            }
        } else
            System.out.println("Typing error(s).");
    }

    private static ParseTree runTS(String prog) {
        CharStream cs = CharStreams.fromString(prog);
        TypedImpCastLexer lexer = new TypedImpCastLexer(cs);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        TypedImpCastParser parser = new TypedImpCastParser(tokens);
        ParseTree tree = parser.main();
        TypedImpCastTS typeSystem = new TypedImpCastTS();
        return typeSystem.visit(tree) != ErrType.INSTANCE ? tree : null;
    }

    private static Mem runIntp(ParseTree tree) {
        TypedImpCastIntp interpreter = new TypedImpCastIntp();
        try {
            interpreter.visit(tree);
            return interpreter.getMem();
        } catch (RuntimeException re) {
            System.out.println(re.getMessage());
            return null;
        }
    }
}
