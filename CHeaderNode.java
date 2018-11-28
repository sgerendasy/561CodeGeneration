import java.util.HashMap;
import java.util.LinkedList;

public class CHeaderNode {
    public String classStructName;
    public String classInstanceName;
    public String objectInstanceName;
    public String classInstanceSingletonName;
    HashMap<String, String> QuackMethodToCMethod = new HashMap<>();
    HashMap<String, String> CMethodToReturnType = new HashMap<>();

    // a list of <variable name, type> for each struct
    public LinkedList<String> objectInstanceStructVariables = new LinkedList<>();

    public CHeaderNode()
    {
    }
    public  CHeaderNode(String classTypeName, String instanceName)
    {
        this.classStructName = classTypeName;
        this.classInstanceName = instanceName;
    }
}
