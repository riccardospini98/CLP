import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.List;

public class CustomErrorListener extends BaseErrorListener {
    private final List<String> parserErrors;

    public CustomErrorListener(List<String> parserErrors) {
        this.parserErrors = parserErrors;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String message, RecognitionException e) {
        String errorMessage = "Syntax error at line " + line + ":" + charPositionInLine + " " + message;
        parserErrors.add(errorMessage);
    }
}