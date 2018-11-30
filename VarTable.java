import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;



public class VarTable
{

    // Var is class that basically acts as a struct to store data necessary to the variable table
    LinkedList<Var> varTable;
    LinkedList<Var> constructorTable;
    LinkedList<Var> methodTable;
    HashMap<String, LinkedList<Var>> methodVars;
    HashMap<String, LinkedList<Var>> methodArgs;
    LinkedList<Var> classArgTypes;
    public String className;


    public VarTable(String className)
    {
        varTable = new LinkedList<>();
        constructorTable = new LinkedList<>();
        methodTable = new LinkedList<>();
        methodVars = new HashMap<>();
        classArgTypes = new LinkedList<>();
        methodArgs = new HashMap<>();
        this.className = className;
    }

    public void addClassArgs(LinkedList<Var> theArgTypes)
    {
        this.classArgTypes = theArgTypes;
    }

    public void addMethodArgs(String methodIdent, LinkedList<Var> theMethodArgTypes)
    {
        if (!methodArgs.containsKey(methodIdent))
        {
            methodArgs.put(methodIdent, theMethodArgTypes);
        }
        else
        {
            methodArgs.get(methodIdent).clear();
            methodArgs.put(methodIdent, theMethodArgTypes);
        }
    }

    public void checkClassArgs(ArrayList<String> givenArgTypes) throws Exception
    {
        if (givenArgTypes.size() != this.classArgTypes.size())
            throw new Exception("Number of args for " + this.className + " don't match");
        for (int i = 0; i < givenArgTypes.size(); i++)
        {
            if (!TypeChecker.checkSubtype(this.classArgTypes.get(i).type, givenArgTypes.get(i)))
                throw new Exception(this.classArgTypes.get(i).type + " not a subtype of " + givenArgTypes.get(i));
        }
    }

    public void checkMethodArgs(String methodIdent, ArrayList<String> givenMethodArgTypes) throws Exception
    {
        if (!ExistsInMethodTable(methodIdent))
            throw new Exception("Method " + methodIdent + " doesn't exist");
        LinkedList<Var> methodArgs = GetMethodArgs(methodIdent);
        if (givenMethodArgTypes.size() != methodArgs.size())
        {
            throw new Exception("Number of args for " + methodIdent + " don't match");
        }
        for (int i = 0; i < givenMethodArgTypes.size(); i++)
        {
            if (!TypeChecker.checkSubtype(methodArgs.get(i).type, givenMethodArgTypes.get(i)))
            {
                throw new Exception(this.methodArgs.get(methodIdent).get(i) + " not a subtype of " + givenMethodArgTypes.get(i));
            }
        }
    }

    public LinkedList<Var> GetMethodArgs(String methodIdent)
    {
        boolean changed = true;
        String classIdent = this.className;
        while (changed)
        {
            for (Var v1 : VarTableSingleton.getTableByClassName(classIdent).methodTable)
            {
                if (methodIdent.equals(v1.ident))
                    return VarTableSingleton.getTableByClassName(classIdent).methodArgs.get(methodIdent);
            }
            String newClassIdent = ClassesTable.getInstance().getParentClass(classIdent);
            if (newClassIdent.equals(classIdent))
            {
                changed = false;
            }
            classIdent = newClassIdent;
        }
        return null;
    }

    public boolean VarIdentExists(String ident)
    {
        for (Var v : varTable)
        {
            if (v.ident.equals(ident))
                return true;
        }
        return false;
    }

    public boolean ConstructorIdentExists(String ident)
    {
        for (Var v : constructorTable)
        {
            if (v.ident.equals(ident) || v.ident.replace("this.", "").equals(ident))
                return true;
        }
        return false;
    }

    public boolean MethodIdentExists(String ident)
    {
        for (Var v : methodTable)
        {
            if (v.ident.equals(ident))
                return true;
        }
        return false;
    }

    public Var GetVarFromVarTable(String ident)
    {
        for (Var v : varTable)
        {
            if (v.ident.equals(ident))
                return v;
        }
        return null;
    }

    public Var GetVarFromMethodVars(String ident, String methodIdent)
    {
        boolean changed = true;
        String classIdent = this.className;
        while (changed)
        {
            for (Var v1 : VarTableSingleton.getTableByClassName(classIdent).methodVars.get(methodIdent))
            {
                if (ident.equals(v1.ident))
                    return v1;
            }
            String newClassIdent = ClassesTable.getInstance().getParentClass(classIdent);
            if (newClassIdent.equals(classIdent))
            {
                changed = false;
            }
            classIdent = newClassIdent;
        }
        return null;
    }

    public Var GetVarFromMethodArgs(String ident, String methodIdent)
    {
        boolean changed = true;
        String classIdent = this.className;
        while (changed)
        {
            for (Var v1 : VarTableSingleton.getTableByClassName(classIdent).methodArgs.get(methodIdent))
            {
                if (ident.equals(v1.ident))
                    return v1;
            }
            String newClassIdent = ClassesTable.getInstance().getParentClass(classIdent);
            if (newClassIdent.equals(classIdent))
            {
                changed = false;
            }
            classIdent = newClassIdent;
        }
        return null;
    }

    public Var GetVarFromConstructorTable(String ident)
    {
        for (Var v : constructorTable)
        {
            if (v.ident.equals(ident))
                return v;
        }
        return null;
    }

    public Var GetVarFromMethodTable(String ident)
    {
        for (Var v : methodTable)
        {
            if (v.ident.equals(ident))
                return v;
        }
        return null;
    }

    public Boolean ExistsInVarTable(Var v)
    {
        for (Var v1 : varTable)
        {
            if (v.ident.equals(v1.ident))
                return true;
        }
        return false;
    }
    public Boolean ExistsInVarTable(String s)
    {
        for (Var v1 : varTable)
        {
            if (s.equals(v1.ident))
                return true;
        }
        return false;
    }

    public String ExistsInConstructorTable(Var v)
    {
        for (Var v1 : constructorTable)
        {
            if (v.ident.equals(v1.ident))
                return v1.type;
        }
        return null;
    }
    public String ExistsInConstructorTable(String s)
    {
        for (Var v1 : constructorTable)
        {
            if (s.equals(v1.ident))
                return v1.type;
        }
        return null;
    }

    public Boolean ExistsInMethodTable(Var v)
    {
        boolean changed = true;
        String classIdent = this.className;
        while (changed)
        {
            for (Var v1 : VarTableSingleton.getTableByClassName(classIdent).methodTable)
            {
                if (v.ident.equals(v1.ident))
                    return true;
            }
            String newClassIdent = ClassesTable.getInstance().getParentClass(classIdent);
            if (newClassIdent.equals(classIdent))
            {
                changed = false;
            }
            classIdent = newClassIdent;
        }
        return false;
    }
    public Boolean ExistsInMethodTable(String ident)
    {
        boolean changed = true;
        String classIdent = this.className;
        while (changed)
        {
            for (Var v1 : VarTableSingleton.getTableByClassName(classIdent).methodTable)
            {
                if (ident.equals(v1.ident))
                    return true;
            }
            String newClassIdent = ClassesTable.getInstance().getParentClass(classIdent);
            if (newClassIdent.equals(classIdent))
            {
                changed = false;
            }
            classIdent = newClassIdent;
        }
        return false;
    }

    public Boolean ExistsInMethodArgs(String ident, String methodIdent)
    {
        boolean changed = true;
        String classIdent = this.className;
        while (changed)
        {
            if (VarTableSingleton.getTableByClassName(classIdent).methodArgs.containsKey(methodIdent))
            {
                for (Var v1 : VarTableSingleton.getTableByClassName(classIdent).methodArgs.get(methodIdent)) {
                    if (ident.equals(v1.ident))
                        return true;
                }
            }
            String newClassIdent = ClassesTable.getInstance().getParentClass(classIdent);
            if (newClassIdent.equals(classIdent))
            {
                changed = false;
            }
            classIdent = newClassIdent;
        }
        return false;
    }

    public Boolean ExistsInMethodVars(String ident, String methodIdent)
    {
        boolean changed = true;
        String classIdent = this.className;
        while (changed)
        {
            for (Var v1 : VarTableSingleton.getTableByClassName(classIdent).methodVars.get(methodIdent))
            {
                if (ident.equals(v1.ident))
                    return true;
            }
            String newClassIdent = ClassesTable.getInstance().getParentClass(classIdent);
            if (newClassIdent.equals(classIdent))
            {
                changed = false;
            }
            classIdent = newClassIdent;
        }
        return false;
    }


    public void AddVarToVarTable(Var v) throws Exception
    {
    	//make sure the varname is not same as a class name
        if(ClassesTable.getInstance().classTable.containsKey(v.ident))
         	throw new Exception("Var " + v.ident + " has same name as class ");
        //make sure var type exists
    	if(!ClassesTable.getInstance().classTable.containsKey(v.type))
    		throw new Exception("Var " + v.ident + " has invalid type "+v.type);
        boolean changed = false;
        for (Var v2: varTable)
        {
            if (v2.ident.equals(v.ident))
            {
                changed = true;
                v2.UpdateType(v.type);
                break;
            }
        }
        if (!changed)
        {
            varTable.add(v);
        }
    }


    public void AddVarToConstructorTable(Var v) throws Exception
    {
        //make sure the varname is not same as a class name
    	if(v.ident.contains("this."))
    		if(ClassesTable.getInstance().classTable.containsKey(v.ident.substring(5)))
    			throw new Exception("Var " + v.ident + " has same name as class ");
        if(ClassesTable.getInstance().classTable.containsKey(v.ident))
            throw new Exception("Var " + v.ident + " has same name as class ");
        //make sure var type exists
        if(!ClassesTable.getInstance().classTable.containsKey(v.type))
            throw new Exception("Var " + v.ident + " has invalid type "+v.type);
        boolean changed = false;
        for (Var v2: constructorTable)
        {
            if (v2.ident.equals(v.ident))
            {
                changed = true;
                v2.UpdateType(v.type);
                break;
            }
        }
        if (!changed)
        {
            constructorTable.add(v);
        }
    }


    public void AddMethodToMethodTable(Var v) throws Exception
    {
        //make sure the varname is not same as a class name
        if(ClassesTable.getInstance().classTable.containsKey(v.ident))
            throw new Exception("Var " + v.ident + " has same name as class ");
        //make sure var type exists
        if(!ClassesTable.getInstance().classTable.containsKey(v.type))
            throw new Exception("Var " + v.ident + " has invalid type " + v.type);
        boolean changed = false;
        for (Var v2: methodTable)
        {
            if (v2.ident.equals(v.ident))
            {
                changed = true;
                v2.UpdateType(v.type);
                break;
            }
        }
        if (!changed)
        {
            methodTable.add(v);
        }
        methodVars.put(v.ident, new LinkedList<>());
    }

    public void AddVarToMethodVarTable(String methodIdent, Var v) throws Exception
    {
        //make sure the varname is not same as a class name
        if(ClassesTable.getInstance().classTable.containsKey(v.ident))
            throw new Exception("Var " + v.ident + " has same name as class ");
        //make sure var type exists
        if(!ClassesTable.getInstance().classTable.containsKey(v.type))
            throw new Exception("Var " + v.ident + " has invalid type "+v.type);
        boolean changed = false;
        for (Var v2: methodVars.get(methodIdent))
        {
            if (v2.ident.equals(v.ident))
            {
                changed = true;
                v2.UpdateType(v.type);
                break;
            }
        }
        if (!changed)
        {
            methodVars.get(methodIdent).add(v);
        }
    }

    public void RemoveVarFromVarTable(Var v)
    {
        varTable.remove(v);
    }


    public void RemoveVarFromConstructorTable(Var v)
    {
        constructorTable.remove(v);
    }


    public void RemoveVarFromMethodTable(Var v)
    {
        methodTable.remove(v);
    }

    public String GetTypeFromVarTable(Var v)
    {
        for (Var v2 : varTable)
        {
            if (v2.ident.equals(v.ident))
            {
                return v.type;
            }
        }
        return null;
    }

    public String GetTypeFromVarTable(String identifier)
    {
        for (Var v : varTable)
        {
            if (v.ident.equals(identifier))
            {
                return v.type;
            }
        }
        return null;
    }

    public String GetTypeFromConstructorTable(Var v)
    {
        for (Var v2 : constructorTable)
        {
            if (v2.ident.equals(v.ident))
            {
                return v.type;
            }
        }
        return null;
    }

    public String GetTypeFromConstructorTable(String identifier)
    {
        for (Var v : constructorTable)
        {
            if (v.ident.equals(identifier))
            {
                return v.type;
            }
        }
        return null;
    }

    public String GetTypeFromMethodTable(Var v)
    {
        for (Var v2 : methodTable)
        {
            if (v2.ident.equals(v.ident))
            {
                return v.type;
            }
        }
        return null;
    }

    public String GetTypeFromMethodTable(String ident)
    {
        boolean changed = true;
        String classIdent = this.className;
        while (changed)
        {
            for (Var v1 : VarTableSingleton.getTableByClassName(classIdent).methodTable)
            {
                if (ident.equals(v1.ident))
                    return v1.type;
            }
            String newClassIdent = ClassesTable.getInstance().getParentClass(classIdent);
            if (newClassIdent.equals(classIdent))
            {
                changed = false;
            }
            classIdent = newClassIdent;
        }
        return null;
    }


    public String GetTypeFromMethodVarTable(String identifier, String methodIdent)
    {
        if (methodVars.containsKey(methodIdent))
        {
            LinkedList<Var> vars = methodVars.get(methodIdent);
            for (Var v : vars)
            {
                if (v.ident.equals(identifier))
                    return v.type;
            }
        }
        return null;
    }

    public String GetTypeFromMethodArgs(String ident, String methodIdent)
    {
        if (methodArgs.containsKey(methodIdent))
        {
            for (Var v : methodArgs.get(methodIdent))
            {
                if (v.ident.equals(ident))
                    return v.type;
            }
        }
        return null;
    }

    public HashMap<String, LinkedList<Var>> getMethodVarTable(){
    	
    	return this.methodVars;
    }
    public LinkedList<Var> getVarTable(){
    	
    	return this.varTable;
    }
    public void setMethodVarTable(HashMap<String, LinkedList<Var>> table){
    	
    	this.methodVars= table;
    }
    public void setVarTable(LinkedList<Var> table){
    	
    	this.varTable= table;
    }
    
    
}

