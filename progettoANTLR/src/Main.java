import ast.Types.ErrorType;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import parser.SimpLanPlusLexer;
import parser.SimpLanPlusParser;
import semanticanalysis.SemanticError;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import ast.Node;
import semanticanalysis.SymbolTable;

public class Main {

    static String INPUT_PATH = "progettoANTLR/src/input.simplanplus";
    static String OUTPUT_PATH = "progettoANTLR/out/errors.txt";

    public static void main(String[] args) throws IOException {
        String input = new String(Files.readAllBytes(Paths.get(INPUT_PATH).toAbsolutePath()));

        CharStream stream = CharStreams.fromString(input);
        SimpLanPlusLexer lexer = new SimpLanPlusLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SimpLanPlusParser parser = new SimpLanPlusParser(tokens);
        Visitor visitor = new Visitor();
        List<String> parserErrors = new ArrayList<>();
        CustomErrorListener errorListener = new CustomErrorListener(parserErrors);

        // Aggiungi un listener personalizzato per l'analisi dell'albero del parser
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        // Esegui l'analisi del programma
        Node AST = visitor.visit(parser.prog());
        System.out.println("Parsing started...");
        //Errori di sintassi
        if (!parserErrors.isEmpty()) {
            System.out.println("Error: Ci sono errori di sintassi nel programma.");
            errorListener.writeOnFile(OUTPUT_PATH);
            return;
        }
        System.out.println("Parse completed with no errors!\nStarting semantic check");
        // Creazione della tabella dei simboli
        SymbolTable ST = new SymbolTable();
        ArrayList<SemanticError> errors = AST.checkSemantics(ST, 0);
        //Errori semantici sugli identificatori
        if(!errors.isEmpty()) {
            System.out.println("The semantic check found "+ errors.size()+" errors.");
            String semanticErrors ="";
            for (SemanticError e: errors) {
                semanticErrors += "[X] Semantic error: " + e + "\n";
                BufferedWriter wr = new BufferedWriter(new FileWriter(OUTPUT_PATH));
                wr.write(semanticErrors);
                wr.close();
            }
        }

        System.out.println("Checking type errors...");
        Node type = AST.typeCheck();
        if (type instanceof ErrorType)
            System.out.println("Type checking is WRONG!" + ((ErrorType) type).getMessage());
        else
            System.out.println(type.toPrint("Type checking ok! Type of the program is: "));

/*
        // Verifica degli identificatori non dichiarati
        List<Symbol> undeclaredIdentifiers = ST.getUndeclaredIdentifiers();
        if (!undeclaredIdentifiers.isEmpty()) {
            for (Symbol identifier : undeclaredIdentifiers) {
                parserErrors.add("Errore - Identificatore non dichiarato: " + identifier);
            }
        }

        // Verifica degli identificatori duplicati
        List<Symbol> duplicateIdentifiers = ST.getDuplicateIdentifiers();
        if (!duplicateIdentifiers.isEmpty()) {
            for (Symbol identifier : duplicateIdentifiers) {
                parserErrors.add("Error - Identificatore dichiarati più volte nello stesso ambiente:" + identifier);
            }
        }


        printSymbolTable(ST);
*/
        ST.toPrint("", 0);

        if (!parserErrors.isEmpty()) {
            File f = new File(OUTPUT_PATH);
            if (!f.exists()) {
                f.createNewFile();
            } else {
                f.delete();
                f.createNewFile();
            }

            System.out.println("Parser errors: " + parserErrors.size());

            for (String error : parserErrors) {
                Files.write(Paths.get(OUTPUT_PATH), (error + "\n").getBytes(), StandardOpenOption.APPEND);
            }
        } else {
            Files.deleteIfExists(Paths.get(OUTPUT_PATH));
            Files.write(Paths.get(OUTPUT_PATH), ("").getBytes(), StandardOpenOption.CREATE_NEW);
        }


    }
/*
    private static void printSymbolTable(SymbolTable symbolTable) {
        // Visualizzazione della tabella dei simboli
        //Simboli dichiarati
        System.out.println("Identificatori dichiarati:");
        for (Symbol identifier : symbolTable.getIdentifiers()) {
            System.out.println(identifier.toString());
        }

        //Simboli referenziati
        System.out.println("Identificatori referenziati:");
        for (Symbol identifier : symbolTable.getReferences()) {
            System.out.println(identifier.toString());
        }

        //Simboli non dichiarati
        System.out.println("Identificatori non dichiarati:");
        for (Symbol identifier : symbolTable.getUndeclaredIdentifiers()) {
            System.out.println(identifier.toString());
        }

        //Simboli duplicati
        System.out.println("Identificatori duplicati:");
        for (Symbol identifier : symbolTable.getDuplicateIdentifiers()) {
            System.out.println(identifier.toString());
        }
    }

 */
}