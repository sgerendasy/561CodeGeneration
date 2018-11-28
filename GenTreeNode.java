import java.util.LinkedList;

public class GenTreeNode
{
    public String registerName;
    public String registerType;
    public String rightHandExpression;
    LinkedList<GenTreeNode> children;

    public GenTreeNode() {}

    public GenTreeNode(String registerName, String registerType)
    {
        this.registerName = registerName;
        this.registerType = registerType;
        children = new LinkedList<>();
    }

    public GenTreeNode(String registerName, String registerType, String rightHandExpression)
    {
        this.registerName = registerName;
        this.registerType = registerType;
        this.rightHandExpression = rightHandExpression;
        children = new LinkedList<>();
    }
}