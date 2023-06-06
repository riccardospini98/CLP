import antlr.SimpLanPlusLexer;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;


public class Main {

    static String INPUT_PATH = "progettoANTLR/src/input.txt";
    static String OUTPUT_PATH = "progettoANTLR/out/errors.txt";
    public static void main(String[] args) throws IOException{
        String input = new String(Files.readAllBytes(Paths.get(INPUT_PATH).toAbsolutePath()));

        CharStream stream = CharStreams.fromString(input);
        antlr.SimpLanPlusLexer lexer = new antlr.SimpLanPlusLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        antlr.SimpLanPlusParser parser = new antlr.SimpLanPlusParser(tokens);

        List<String> parserErrors = new ArrayList<>();
        CustomErrorListener errorListener = new CustomErrorListener(parserErrors);
        parser.addErrorListener(errorListener);

        // Creazione della tabella dei simboli
        SymbolTable symbolTable = new SymbolTable();

        // Aggiungi un listener personalizzato per l'analisi dell'albero del parser
        ParseTreeListener listener = new CustomListener(symbolTable);

        ParseTreeWalker walker = new ParseTreeWalker();


        // Esegui l'analisi del programma
        ParseTree parseTree = parser.prog();

        //Errori di sintassi
        if (!parserErrors.isEmpty()) {
            System.out.println("Error: Ci sono errori di sintassi nel programma.");
        }

        //Errori semantici sugli identificatori
        else {
            // Continua con l'analisi dell'albero
            walker.walk(listener, parseTree);

            // Verifica degli identificatori non dichiarati
            List<Symbol> undeclaredIdentifiers = symbolTable.getUndeclaredIdentifiers();
            if (!undeclaredIdentifiers.isEmpty()) {
                for (Symbol identifier : undeclaredIdentifiers) {
                    parserErrors.add("Errore - Identificatore non dichiarato: " + identifier);
                }
            }

            // Verifica degli identificatori duplicati
            List<Symbol> duplicateIdentifiers = symbolTable.getDuplicateIdentifiers();
            if (!duplicateIdentifiers.isEmpty()) {
                for (Symbol identifier : duplicateIdentifiers) {
                    parserErrors.add("Error - Identificatore dichiarati più volte nello stesso ambiente:" + identifier);
                }
            }

            printSymbolTable(symbolTable);
        }



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

    private static void printSymbolTable(SymbolTable symbolTable){
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
}