import java.io.IOException;
/**
 * @author JamesDavies
 * @date 28/02/2017
 * RecursiveDescentRecogniser
 */
public class SyntaxAnalyser extends AbstractSyntaxAnalyser {

    SyntaxAnalyser(String filename) {
        try {
            lex = new LexicalAnalyser(filename);

        } catch (Exception e) {
            System.err.println("Failed to load lexical analyser.");
        }

    }

    @Override
    public void _statementPart_() throws IOException, CompilationException {

        // Begin the parsing, find the 'begin' statement
        myGenerate.commenceNonterminal("<statement part>");

        // Begining of the statement list
        acceptTerminal(Token.beginSymbol);

        parseStatementList();

        // Ending of the parsing
        acceptTerminal(Token.endSymbol);
        myGenerate.finishNonterminal("<statement part>");

    }

    /**
     * Start processing the statement list
     */
    private void parseStatementList() {

        // Loop until we find a semicolon
    }

    @Override
    public void acceptTerminal(int symbol) throws IOException, CompilationException {

        String sybl = Token.getName(symbol);


        switch (symbol) {
            case Token.eofSymbol:
                acceptTerminal(lex.getNextToken().symbol);
                break;
            case Token.callSymbol:
                break;
        }
    }

    public void expression() {

    }

}
