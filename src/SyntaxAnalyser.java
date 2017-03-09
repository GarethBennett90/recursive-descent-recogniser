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

    /**
     * @throws IOException
     * @throws CompilationException
     */
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

    /**
     * @throws IOException
     * @throws CompilationException
     */
    private void handleAssignment() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("<assignment statement>");
        // :=
        acceptTerminal(Token.becomesSymbol);

        // Check if the next token is a string constant
        if (lex.getNextToken().symbol != Token.stringSymbol) {
            acceptTerminal(Token.stringSymbol);
            myGenerate.finishNonterminal("<assignment statement>");
            return;
        }

        // If it's not a string constant, then handle the expression
        handleExpression();
        myGenerate.finishNonterminal("<assignment statement>");
    }

    /**
     * @throws IOException
     * @throws CompilationException
     */
    private void handleProcedure() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("<procedure statement>");
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
        myGenerate.finishNonterminal("<procedure statement>");
    }

    /**
     * Return boolean based on whether the next token is part of an expression
     *
     * @return boolean
     */
    private boolean isExpression(Token token) {
        return token.symbol == Token.plusSymbol
                || token.symbol == Token.minusSymbol;
    }

    /**
     * Return boolean based on whether the next token is part of a factor
     *
     * @return boolean
     */
    private boolean isFactor(Token token) {
        return token.symbol == Token.divideSymbol
                || token.symbol == Token.timesSymbol;
    }

    /**
     * @throws IOException
     * @throws CompilationException
     */
    private void handleExpression() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("<expression>");

        handleTerm();

        while (isExpression(nextToken)) {
            // If we know that the next token is either plus or minus, we can simply add it
            acceptTerminal(nextToken.symbol);
            handleTerm();
        }

        myGenerate.finishNonterminal("<expression>");
    }


    private void handleTerm() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("<term>");

        handleFactor();

        while (isFactor(nextToken)) {
            // If we know that the next token is either plus or minus, we can simply add it
            acceptTerminal(nextToken.symbol);
            handleTerm();
        }

        myGenerate.finishNonterminal("<term>");
    }

    private void handleFactor() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("<factor>");

        switch (nextToken.symbol) {
            case Token.identifier:
                acceptTerminal(Token.identifier);
                break;
            case Token.numberConstant:
                acceptTerminal(Token.numberConstant);
                break;
            case Token.leftParenthesis:
                acceptTerminal(Token.leftParenthesis);
                handleExpression();
                acceptTerminal(Token.rightParenthesis);
            default:
                myGenerate.reportError(nextToken, "Error on factor, expected IDENTIFIER, NUMBER or (<expression), but found " + Token.getName(nextToken.symbol));
        }

        myGenerate.finishNonterminal("<factor>");
    }

    /**
     * @throws IOException
     * @throws CompilationException
     */
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

    /**
     * @throws IOException
     * @throws CompilationException
     */
    private void handleCondition() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("<condition>");
        handleConditionalOperator();
        myGenerate.finishNonterminal("<condition>");
    }

    /**
     * @throws IOException
     * @throws CompilationException
     */
    private void handleConditionalOperator() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("<conditional operator>");
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
        myGenerate.finishNonterminal("<conditional operator>");
    }

    private void handleIf() {
        System.out.println("Handle the if statement");
    }

    /**
     * @throws IOException
     * @throws CompilationException
     */
    private void handleWhile() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("<while statement>");
        // while
        acceptTerminal(Token.whileSymbol);
        // condition for the loop
        handleCondition();
        // loop
        acceptTerminal(Token.loopSymbol);
        // The body of the loop
        handleStatementList();
        // end loop
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.loopSymbol);

        myGenerate.finishNonterminal("<while statement>");
    }

    private void handleVariables() {

    }

    /**
     * @param symbol
     * @throws IOException
     * @throws CompilationException
     */
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
