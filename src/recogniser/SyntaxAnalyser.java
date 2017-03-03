package recogniser;

import lib.AbstractSyntaxAnalyser;
import lib.CompilationException;
import lib.LexicalAnalyser;

import java.io.IOException;

/**
 * @author JamesDavies
 * @date 28/02/2017
 * RecursiveDescentRecogniser
 */
public class SyntaxAnalyser extends AbstractSyntaxAnalyser {

    public SyntaxAnalyser(String filename) {
        try{
            this.lex = new LexicalAnalyser(filename);
            this.nextToken = lex.getNextToken();
        } catch(Exception e) {
            System.err.println("Failed to load lexical analyser.");
        }

    }

    @Override
    public void _statementPart_() throws IOException, CompilationException {

    }

    @Override
    public void acceptTerminal(int symbol) throws IOException, CompilationException {

    }

}
