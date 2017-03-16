import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @author JamesDavies
 * @date 28/02/2017
 * RecursiveDescentRecogniser
 */
public class SyntaxAnalyser extends AbstractSyntaxAnalyser {

    private String filename;

    public SyntaxAnalyser(String filename) {

        this.filename = filename;

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

        try {
            // Enter into the statement list
            handleStatementList();
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("<statement list>", nextToken), e);
        }

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

        try {
            handleStatement();
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("<statement>", nextToken), e);
        }

        // If you've reached a semicolon, then you've finished the statement
        while (nextToken.symbol == Token.semicolonSymbol) {
            acceptTerminal(Token.semicolonSymbol);

            try {
                // Handle the next statement again
                handleStatement();
            } catch (CompilationException e) {
                throw new CompilationException(generateErrorString("<statement>", nextToken), e);
            }
        }

        myGenerate.finishNonterminal("<statement list>");
    }

    /**
     * Checks the first Set for the terminal
     */
    private void handleStatement() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("<statement>");

        try {
            switch (nextToken.symbol) {
                case Token.callSymbol:
                    handleProcedure();
                    break;
                case Token.identifier:
                    handleAssignment();
                    break;
                case Token.whileSymbol:
                    handleWhile();
                    break;
                case Token.ifSymbol:
                    handleIf();
                    break;
                case Token.untilSymbol:
                    handleUntil();
                    break;
                default:
                    myGenerate.reportError(nextToken, "Expected <if>, <assignment>, <until>, <while> or <procedure>");
                    break;
                    // Handle error
            }
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("<if>, <assignment>, <until>, <while> or <procedure>", nextToken), e);
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
        acceptTerminal(Token.identifier);
        acceptTerminal(Token.becomesSymbol);

        // Check if the next token is a string constant
        if (nextToken.symbol == Token.stringConstant) {
            acceptTerminal(Token.stringConstant);
            myGenerate.finishNonterminal("<assignment statement>");
            return;
        }

        // If it's not a string constant, then handle the expression
        try {
            handleExpression();
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("<expression>", nextToken), e);
        }
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

        try {
            // Arguments inside the 'get'
            handleArgumentList();
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("<argument list>", nextToken), e);
        }

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

        try {
            handleTerm();


            while (isExpression(nextToken)) {
                // If we know that the next token is either plus or minus, we can simply add it
                acceptTerminal(nextToken.symbol);

                handleTerm();
            }

        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("<term>", nextToken), e);
        }

        myGenerate.finishNonterminal("<expression>");
    }


    private void handleTerm() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("<term>");

        try {

            handleFactor();

            while (isFactor(nextToken)) {
                // If we know that the next token is either plus or minus, we can simply add it
                acceptTerminal(nextToken.symbol);
                handleFactor();
            }

        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("<factor>", nextToken), e);
        }

        myGenerate.finishNonterminal("<term>");
    }

    private void handleFactor() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("<factor>");

        try {

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
                    break;
            }

        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("<identifier>, <number constant> or <( <expression> )>", nextToken), e);
        }

        myGenerate.finishNonterminal("<factor>");
    }

    /**
     * @throws IOException
     * @throws CompilationException
     */
    private void handleArgumentList() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("<argument list>");

        // Accept the identifier
        acceptTerminal(Token.identifier);

        try {
            // Check if there is a comma, identifiers are automatically processed
            while (nextToken.symbol == Token.commaSymbol) {
                acceptTerminal(Token.commaSymbol);
                acceptTerminal(Token.identifier);
                handleArgumentList();
            }

        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("<argument list>", nextToken), e);
        }

        myGenerate.finishNonterminal("<argument list>");
    }

    /**
     * @throws IOException
     * @throws CompilationException
     */
    private void handleCondition() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("<condition>");
        acceptTerminal(Token.identifier);

        try {
            handleConditionalOperator();

            switch (nextToken.symbol) {
                case Token.identifier:
                    acceptTerminal(Token.identifier);
                    break;
                case Token.numberConstant:
                    acceptTerminal(Token.numberConstant);
                    break;
                case Token.stringConstant:
                    acceptTerminal(Token.stringConstant);
                    break;
                default:
            }

        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("<condition operator>", nextToken), e);
        }

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
            default:
        }
        myGenerate.finishNonterminal("<conditional operator>");
    }

    private void handleIf() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("<if statement>");

        // if
        acceptTerminal(Token.ifSymbol);

        try {
            // If condition
            handleCondition();
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("<condition>", nextToken), e);
        }
        // then
        acceptTerminal(Token.thenSymbol);

        try {
            // FIRST()
            handleStatementList();

            // Check if an else exists, then we want to handle that
            if (nextToken.symbol == Token.elseSymbol) {
                // else
                acceptTerminal(Token.elseSymbol);
                // Handle the FIRST()
                handleStatementList();
            }

        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("<statement list>", nextToken), e);
        }

        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.ifSymbol);

        myGenerate.finishNonterminal("<if statement>");
    }

    /**
     * @throws IOException
     * @throws CompilationException
     */
    private void handleWhile() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("<while statement>");
        // while
        acceptTerminal(Token.whileSymbol);

        try {
            // condition for the loop
            handleCondition();
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("<condition>", nextToken), e);
        }
        // loop
        acceptTerminal(Token.loopSymbol);

        try {
            // The body of the loop
            handleStatementList();
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("<statement list>", nextToken), e);
        }
        // end loop
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.loopSymbol);

        myGenerate.finishNonterminal("<while statement>");
    }

    /**
     *
     * @throws IOException
     * @throws CompilationException
     */
    private void handleUntil() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("<until statement>");

        // do
        acceptTerminal(Token.doSymbol);

        try {
            // Handle FIRST() set
            handleStatementList();
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("<statement list>", nextToken), e);
        }

        // until
        acceptTerminal(Token.untilSymbol);

        try {
            // Handle the condition
            handleCondition();
        } catch (CompilationException e) {
            throw new CompilationException(generateErrorString("<condition>", nextToken), e);
        }

        myGenerate.finishNonterminal("<until statement>");
    }

    /**
     * @param expected The list of expected terminals.
     * @param next     The next token
     * @return String
     */
    private String generateErrorString(String expected, Token next) {
        return "line " + next.lineNumber + " in " + this.filename + ":\n\t\t\t- Expected token(s) " + expected + " but found (" + Token.getName(next.symbol) + ").\n";
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

        myGenerate.reportError(nextToken, generateErrorString("<" + Token.getName(symbol) + ">", nextToken));
    }

}
