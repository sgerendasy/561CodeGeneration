import java.util.HashMap;

public class GenTreeAndRegisterTables
{
    public GenTreeNode genTreeNode;
    public HashMap<String, Var> theRegisterTable;

    public GenTreeAndRegisterTables()
    {
        this.theRegisterTable = new HashMap<>();
    }

    public GenTreeAndRegisterTables(GenTreeNode root, HashMap<String, Var> registerTable)
    {
        this.genTreeNode = root;
        this.theRegisterTable = registerTable;
    }
}