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
        System.out.println(explanatoryMessage);
        throw new CompilationException(explanatoryMessage);
    }

}
