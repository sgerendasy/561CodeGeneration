import java.util.LinkedList;

public class VarTableSingleton
{
    static LinkedList<VarTable> TheTable = new LinkedList<>();
//    static LinkedList<VarTable> constructorVarTables = new LinkedList<>();
    private static VarTableSingleton varTableInstance;

    public static VarTableSingleton getCurrentInstance()
    {
        if (varTableInstance == null)
            varTableInstance = new VarTableSingleton();
        return varTableInstance;
    }

    public void addTable(VarTable varTable)
    {
        TheTable.add(varTable);
    }

    public static VarTable getTableByClassName(String className)
    {
        for (VarTable vt : TheTable)
        {
            if (vt.className.equals(className))
                return vt;
        }
        return null;
    }

   
}
