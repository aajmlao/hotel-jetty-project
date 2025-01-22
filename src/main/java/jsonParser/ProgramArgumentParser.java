package jsonParser;
import java.util.HashMap;
import java.util.Map;

/***
 * this class uses to parse the arguments for the inputs.
 */
public class ProgramArgumentParser {
    private final Map<String, String> argMap;

    public ProgramArgumentParser() {
        argMap = new HashMap<>();
        argMap.put("-hotels", "");
        argMap.put("-reviews","");
        argMap.put("-threads", "3");
    }

    /***
     * parse arguments to store in the argMap.
     * @param args
     */
    public void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i+= 2) {
            if (argMap.containsKey(args[i])) {
                argMap.replace(args[i], args[i + 1]);
            } else {
                throw new RuntimeException("Invalid Argument");
            }
        }
    }

    /***
     * find the argument with query.
     * @param argumentFlag
     * @return
     */
    public String getArgumentValue(String argumentFlag) {
        if (argMap.containsKey(argumentFlag)) {
            return argMap.get(argumentFlag);
        } else {
            throw new IllegalArgumentException(argumentFlag + " does not exist. Ending the program...");
        }
    }
}
