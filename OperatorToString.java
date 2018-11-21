import java.util.HashMap;

public class OperatorToString
{
    private static HashMap<String, String> operatorDict;
    private static HashMap<String, String> unaryOperatorDict;

    public static HashMap<String ,String> getOperatorDict()
    {
        if (operatorDict == null)
        {
            operatorDict = new HashMap<>();
            operatorDict.put("+", "PLUS");
            operatorDict.put("==", "EQUALS");
            operatorDict.put("<=", "ATMOST");
            operatorDict.put("<", "LESS");
            operatorDict.put(">=", "ATLEAST");
            operatorDict.put(">", "MORE");
            operatorDict.put("*", "TIMES");
            operatorDict.put("-", "MINUS");
            operatorDict.put("/", "DIVIDE");
            operatorDict.put("and", "$AND");
            operatorDict.put("or", "$OR");
        }

        return operatorDict;
    }

    public static HashMap<String ,String> getUnaryOperatorDict()
    {
        if (unaryOperatorDict == null)
        {
            unaryOperatorDict = new HashMap<>();
            unaryOperatorDict.put("-", "NEG");
            unaryOperatorDict.put("!", "$NOT");
        }

        return unaryOperatorDict;
    }
}

