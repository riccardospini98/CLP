import antlr.SimpLanPlusLexer;
import org.antlr.v4.runtime.*;

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

        //Esercizio 1
        CharStream stream = CharStreams.fromString(input);
        antlr.SimpLanPlusLexer lexer = new antlr.SimpLanPlusLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        antlr.SimpLanPlusParser parser = new antlr.SimpLanPlusParser(tokens);
        parser.removeErrorListeners(); // Remove the default error listeners
        List<String> parserErrors = new ArrayList<>();

        // Custom error listener to collect parser errors
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                parserErrors.add("Errore: Linea " + line + ", carattere numero " + charPositionInLine + " -> " + msg);
            }
        });

        // Start parsing from the 'prog' rule
        antlr.SimpLanPlusParser.ProgContext prog = parser.prog();

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
                System.out.println("Writing in file " + error);
                Files.write(Paths.get(OUTPUT_PATH), (error + "\n").getBytes(), StandardOpenOption.APPEND);
            }
        } else {
            Files.deleteIfExists(Paths.get(OUTPUT_PATH));
            Files.write(Paths.get(OUTPUT_PATH), ("").getBytes(), StandardOpenOption.CREATE_NEW);
        }

        //Esercizio 2

    }
}