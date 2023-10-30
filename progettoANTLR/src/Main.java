import SVM.SVMLexer;
import SVM.SVMParser;
import ast.Types.ErrorType;
import ast.Node;

import evaluator.AssemblyClass;
import evaluator.ExecuteVM;
import org.antlr.v4.runtime.*;
import parser.SimpLanPlusLexer;
import parser.SimpLanPlusParser;
import semanticanalysis.SemanticError;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        // Vogliamo solo gli errori lessicali in output
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);

        // Se volessimo anche quelli sintattici, basterebbe scommentare queste righe
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        // Esegui l'analisi del programma
        Node AST = visitor.visit(parser.prog());
        System.out.println("Parsing started...");
        if (!parserErrors.isEmpty()) {
            //Errori di sintassi
            System.err.println("[X] ERROR: Syntax error(s) found in program.\n\tCheck output file for additional information.");
            errorListener.writeOnFile(OUTPUT_PATH);
            return;
        }
        System.out.println("Parse completed with no errors!\nStarting semantic check...");

        // Creazione della tabella dei simboli
        ArrayList<SemanticError> STErrors = new ArrayList<>();
        SymbolTable ST = new SymbolTable(STErrors);
        ArrayList<SemanticError> SErrors = AST.checkSemantics(ST, 0);
        ArrayList<SemanticError> errors = new ArrayList<>();
        if (!SErrors.isEmpty()) {
            errors.addAll(SErrors);
        }
        if (!STErrors.isEmpty()) {
            errors.addAll(STErrors);
        }

        //Errori semantici sugli identificatori
        if(!errors.isEmpty()) {
            System.err.println("The semantic check found "+ errors.size() +" errors.");
            String semanticErrors ="";

            for (SemanticError err: errors) {
                semanticErrors += err + "\n";
                System.err.println(err + "\n");

                try {
                    BufferedWriter wr = new BufferedWriter(new FileWriter(OUTPUT_PATH));
                    wr.write(semanticErrors);
                    wr.flush();
                    wr.close();
                } catch (IOException e) {
                    System.err.println("Exception while writing on output file: " + e);
                }
                System.exit(1);
            }
        }

        System.out.println("Checking type errors...");
        Node type = AST.typeCheck();
        if (type instanceof ErrorType) {
            System.err.println("[X] ERROR: Type checking is WRONG!\n\t" + ((ErrorType) type).getMessage());
            return;
        }
        else
            System.out.println(type.toPrint("Type checking ok! Type of the program is: "));

        if (!parserErrors.isEmpty()) {
            File f = new File(OUTPUT_PATH);
            if (!f.exists()) {
                f.createNewFile();
            } else {
                f.delete();
                f.createNewFile();
            }

            System.err.println("[X] ERROR: Found"+ parserErrors.size() +" parser errors.\n\tCheck output file for additional information. ");

            for (String error : parserErrors) {
                Files.write(Paths.get(OUTPUT_PATH), (error + "\n").getBytes(), StandardOpenOption.APPEND);
            }
        } else {
            Files.deleteIfExists(Paths.get(OUTPUT_PATH));
            Files.write(Paths.get(OUTPUT_PATH), ("").getBytes(), StandardOpenOption.CREATE_NEW);
        }

        // CODE GENERATION  input.simplanplus.asm
        String code=AST.codeGeneration();
        BufferedWriter out = new BufferedWriter(new FileWriter(INPUT_PATH+".asm"));
        out.write(code);
        out.close();
        System.out.println("Code generated! Assembling and running generated code.");

        FileInputStream isASM = new FileInputStream(INPUT_PATH+".asm");
        ANTLRInputStream inputASM = new ANTLRInputStream(isASM);
        SVMLexer lexerASM = new SVMLexer(inputASM);
        CommonTokenStream tokensASM = new CommonTokenStream(lexerASM);
        SVMParser parserASM = new SVMParser(tokensASM);

        SVMVisitor visitorSVM = new SVMVisitor();
        visitorSVM.visit(parserASM.assembly());

        System.out.println("Starting Virtual Machine...");
        ExecuteVM vm = new ExecuteVM(visitorSVM.code);
        vm.cpu();

    }
}