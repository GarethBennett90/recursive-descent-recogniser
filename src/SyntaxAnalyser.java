import java.io.IOException;

/**
 * @author JamesDavies
 * @date 28/02/2017
 * RecursiveDescentRecogniser
 */
public class SyntaxAnalyser extends AbstractSyntaxAnalyser {

    SyntaxAnalyser(String filename) {

        // Initalise the lexical analyser
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

        // Enter into the statement list
        handleStatementList();

        // Ending of the parsing
        acceptTerminal(Token.endSymbol);
        myGenerate.finishNonterminal("<statement part>");

    }

    /**
     * Start processing the statement list
     */
    private void handleStatementList() throws IOException {

        // Check not at the end of the list
        Token currToken;
        while ((currToken = lex.getNextToken()).symbol != Token.endSymbol) {
            System.out.println(Token.getName(currToken.symbol));

            try {
                acceptTerminal(currToken.symbol);
            } catch (CompilationException e) {
            }

            if (currToken.symbol == Token.semicolonSymbol) {
                // End of the current line
                System.out.println("End of line");
            }
        }
    }

    private void handleIf() {

    }

    private void handleWhile() {

    }

    private void handleVariables() {

    }

    private void handleProcedure() {

    }


    @Override
    public void acceptTerminal(int symbol) throws IOException, CompilationException {

        switch (symbol) {
            case Token.callSymbol:
                handleProcedure();
                break;
            case Token.ifSymbol:
                handleIf();
                break;
            case Token.whileSymbol:
                handleWhile();
                break;
        }
    }

}
