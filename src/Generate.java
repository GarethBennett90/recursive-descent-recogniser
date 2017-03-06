/**
 * @author JamesDavies
 * @date 28/02/2017
 * RecursiveDescentRecogniser
 */
public class Generate extends AbstractGenerate {

    public Generate() {
        // Constructor
    }

    @Override
    public void reportError(Token token, String explanatoryMessage) throws CompilationException {
        System.err.println("Error on line " + token.lineNumber + ": " + explanatoryMessage);
    }

}
