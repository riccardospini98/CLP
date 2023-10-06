import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.util.List;

public class CustomErrorListener extends BaseErrorListener {
    private final List<String> parserErrors;

    public CustomErrorListener(List<String> parserErrors) {
        this.parserErrors = parserErrors;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String message, RecognitionException e) {
        String errorMessage = "Errore di sintassi riga" + line + ":" + charPositionInLine + " " + message;

        //Basandosi sul param message possiamo decidere che messaggi stampare nel file per ogni caso specifico.
        parserErrors.add(errorMessage);
    }

    public void writeOnFile(String path) {
        String errors = "";
        for (String error: this.parserErrors) {
            errors += error + '\n';
        }
        try {
            BufferedWriter wr = new BufferedWriter(new FileWriter(path));
            wr.write(errors);
            wr.close();
        } catch (IOException e) {

        }
    }

}