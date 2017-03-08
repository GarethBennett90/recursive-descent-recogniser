import java.io.IOException;

/**
 * @author JamesDavies
 * @date 28/02/2017
 * RecursiveDescentRecogniser
 */
public class SyntaxAnalyser extends AbstractSyntaxAnalyser {

    public SyntaxAnalyser(String filename) {

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
    private void handleStatementList() throws IOException, CompilationException {

        // Check not at the end of the list
        myGenerate.commenceNonterminal("<statement list>");

        handleStatement();

        // If you've reached a semicolon, then you've finished the statement
        while (nextToken.symbol == Token.semicolonSymbol) {
            acceptTerminal(Token.semicolonSymbol);

            // Handle the next statement again
            handleStatement();
        }

        myGenerate.finishNonterminal("<statement list>");
        myGenerate.reportSuccess();
    }

    /**
     * Checks the first Set for the terminal
     */
    private void handleStatement() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("<statement>");

        switch (nextToken.symbol) {
            case Token.callSymbol:
                handleProcedure();
                break;
            case Token.becomesSymbol:
                handleAssignment();
                break;
            case Token.whileSymbol:
                handleWhile();
                break;
        }

        myGenerate.finishNonterminal("<statement>");
    }

    private void handleAssignment() throws IOException, CompilationException {
        // :=
        acceptTerminal(Token.becomesSymbol);

        // Check if the next token is a string constant
        if (lex.getNextToken().symbol != Token.stringSymbol) {
            acceptTerminal(Token.stringSymbol);
            return;
        }

        // If it's not a string constant, then handle the expression
        handleExpression();
    }

    private void handleProcedure() throws IOException, CompilationException {
        // call
        acceptTerminal(Token.callSymbol);
        // get
        acceptTerminal(Token.identifier);
        // (
        acceptTerminal(Token.leftParenthesis);
        // Arguments inside the 'get'
        handleArgumentList();
        // )
        acceptTerminal(Token.rightParenthesis);
    }

    private void handleExpression() {
        myGenerate.commenceNonterminal("<expression>");


        myGenerate.finishNonterminal("<expression>");
    }

    private void handleTerm() {

    }

    private void handleArgumentList() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("<argument list>");

        // Check if there is a comma, identifiers are automatically processed
        while (nextToken.symbol == Token.commaSymbol) {
            acceptTerminal(Token.commaSymbol);
            handleArgumentList();
        }

        // Accept the identifier
        acceptTerminal(Token.identifier);

        myGenerate.finishNonterminal("<argument list>");
    }

    private void handleCondition() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("<condition>");
        handleConditionalOperator();
        myGenerate.finishNonterminal("<condition>");
    }

    private void handleConditionalOperator() throws IOException, CompilationException {
        switch (nextToken.symbol) {
            case Token.lessThanSymbol:
                acceptTerminal(Token.lessThanSymbol);
                break;
            case Token.greaterThanSymbol:
                acceptTerminal(Token.greaterThanSymbol);
                break;
            case Token.lessEqualSymbol:
                acceptTerminal(Token.lessEqualSymbol);
                break;
            case Token.greaterEqualSymbol:
                acceptTerminal(Token.greaterEqualSymbol);
                break;
            case Token.equalSymbol:
                acceptTerminal(Token.equalSymbol);
                break;
            case Token.notEqualSymbol:
                acceptTerminal(Token.notEqualSymbol);
                break;
        }
    }

    private void handleIf() {
        System.out.println("Handle the if statement");
    }

    private void handleWhile() throws IOException, CompilationException {
        acceptTerminal(Token.whileSymbol);
        handleCondition();
        acceptTerminal(Token.loopSymbol);
        handleStatementList();
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.loopSymbol);
    }

    private void handleVariables() {

    }

    @Override
    public void acceptTerminal(int symbol) throws IOException, CompilationException {

        Token actual = nextToken;
        // Check if the symbol was expected
        if (symbol == actual.symbol) {
            myGenerate.insertTerminal(nextToken);
            nextToken = lex.getNextToken();
            return;
        }

        myGenerate.reportError(nextToken, "Error on line " + nextToken.lineNumber + ": Invalid symbol found, expected '" + Token.getName(symbol) + "' and found '" + Token.getName(actual.symbol) + "'");
    }

}
