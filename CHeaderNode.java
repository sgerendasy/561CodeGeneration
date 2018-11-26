import java.util.LinkedList;

public class CHeaderNode {
    public String classTypeName;
    public String classInstanceName;
    public String objectInstanceName;

    // a list of <variable name, type> for each struct
    public LinkedList<String> objectInstanceStructVariables = new LinkedList<>();

    public CHeaderNode()
    {
    }
    public  CHeaderNode(String classTypeName, String instanceName)
    {
        this.classTypeName = classTypeName;
        this.classInstanceName = instanceName;
    }
}
