import java.util.HashMap;

public class ClassesTable
{
    private static ClassesTable classTableInstance = null;

    HashMap<String, String> classTable;

    private ClassesTable()
    {
        classTable = new HashMap<>();
    }

    public static ClassesTable getInstance()
    {
        if (classTableInstance == null)
        {
            classTableInstance = new ClassesTable();
        }
        return classTableInstance;
    }

    public boolean addClass(String clazz, String extendsClazz)
    {
        // return true if clazz ident is unique to the HashMap, false otherwise
        if (classTable.containsKey(clazz))
            return false;
        classTable.put(clazz, extendsClazz);
        return true;
    }

    public boolean removeClass(String clazz)
    {
        // returns true if the clazz indent was found in the HashMap, false otherwise
        if (classTable.containsKey(clazz))
        {
            classTable.remove(clazz);
            return true;
        }
        return false;
    }

    public String getClass(String className) {
        if (classTable.containsKey(className))
        {
            return className;
        }
//        throw new Exception(String.format("Class %s does not exist", className));
        return null;
    }

    public String getParentClass(String className) {
        if (classTable.containsKey(className))
        {
            return classTable.get(className);
        }
//        throw new Exception(String.format("Class %s does not exist", className));
        return null;
    }

    public HashMap<String, String> getClassTable()
    {
        return classTable;
    }


}
