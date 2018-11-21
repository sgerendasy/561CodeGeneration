import java.util.*;
import java.util.Map.Entry;


public class TypeChecker {
	
	static Program builtinAST;
	static Program ast;
	public static String currentClass;
	public static String currentMethod;
	ClassesTable classesTable;
	Tree tree;
	
	public TypeChecker(Program builtinAST, Program ast){
		TypeChecker.ast = ast;
		TypeChecker.builtinAST = builtinAST;
		classesTable = ClassesTable.getInstance();
		tree = Tree.getInstance();
	}
	
	public boolean TypeCheck() throws Exception
    {
        //first visit only classes
        ast.visit();
        ast.methodVisit();
        checkForUndefined();
        System.out.println("Passed check for undefined class inheritance");
        checkForCycles();
        createLattice();
        System.out.println("Passed check for class cycles");

        //Second Visit to typeCheck everything
        ast.visit2();
        checkOverriden();
        checkConstructor();
        // System.out.println(" : "+tree.LCA(tree.getRoot(), "D","X"));
        PrintTables();
        return true;
    }

    private void PrintTables()
    {
        System.out.println("\n ========== PRINTING TYPE TABLES ==========\n");
        for (VarTable vt : VarTableSingleton.TheTable)
        {
            String className = vt.className;
            System.out.println("Class: " + className);
            System.out.println("\tExtends: " + ClassesTable.getInstance().getParentClass(className));
            System.out.println("\tInstance Variables: ");
            for (Var v : vt.constructorTable)
            {
                System.out.println("\t\t" + v.ident + ": " + v.type);
            }
            if (vt.constructorTable.size() == 0)
            {
                System.out.println("\t\t[none]");
            }
            System.out.println("\tMethods:");
            for(Var v : vt.methodTable)
            {
                System.out.println("\t\t" + v.ident + " returns: " + v.type);
            }
            if (vt.methodTable.size() == 0)
            {
                System.out.println("\t\t[none]");
            }
            System.out.println();
        }
    }

	private void createLattice() throws Exception {
	    Tree tree = Tree.getInstance();
	    ClassesTable ct = ClassesTable.getInstance();
	    HashMap<String, String> c = new HashMap<>();
	    c.putAll(ct.classTable);
	    Node root = new Node("Obj");
     	tree.setRoot(root);
     		 
     	c.remove("Obj", "Obj");
     	

     	Entry<String,String> entry = null;
     	String key = "Obj";

     	while(true) {
     		
     		Node n =tree.findNode(root, key);
     		while(n==null) {
     			for (Entry<String,String> e : c.entrySet()) {
     				key = e.getKey();
     				n =tree.findNode(root, key);
     				if(n!=null)
     					break;
     				key = e.getValue();
     				n =tree.findNode(root, key);
     				if(n!=null)
         				break;
     				
     			
     			}
     		}
     		entry =getElement(c,key);
     		while(entry!=null) {

     			n.addChild(new Node(entry.getKey()));
     			c.remove(entry.getKey());
     			entry =getElement(c,key);
     		
     		}
     		
     		if(c.isEmpty())
     			break;
     		key = c.entrySet().iterator().next().getValue();

     	}
     	//System.out.println("Latice");
		
	}
	public Entry<String, String> getElement(HashMap<String, String> t, String v) {
		for(Entry<String, String> entry: t.entrySet()){
            if(v.equals(entry.getValue())){
                return entry;
            }
        }
		return null;
	}
	public static String getParent(String clazz) {
		if(clazz.equals("Obj"))
			return clazz;
		Tree tree = Tree.getInstance();
		Node n =tree.findNode(tree.getRoot(), clazz);	
		return n.getParent().getId();
		
	}
	

	private void checkOverriden() throws Exception {
		//check each classblock
		//check to see if super class has any method
		String currentSuper="";
		Class_Block.Clazz_Block parentClass =null;
		Methods.Method m;

		//check each classblock
        for (Class_Block.Clazz_Block cb : ast.get_cbs())
        {
            if(!(cb._extendsIdent.equals(currentSuper)))
            {
                parentClass = builtinAST.get_cb(cb._extendsIdent);
                if(parentClass == null) parentClass = ast.get_cb(cb._extendsIdent);
            }
            //typecheck instance variables
            if(parentClass != null && !parentClass._stmtList.isEmpty())
            {
                VarTable parentClassConstructorTable = VarTableSingleton.getTableByClassName(parentClass._classIdent);
                VarTable classConstructorTable = VarTableSingleton.getTableByClassName(cb._classIdent);
                for (Var v : parentClassConstructorTable.constructorTable)
                {
                    String matchingType = classConstructorTable.ExistsInConstructorTable(v);
                    if (matchingType == null)
                        throw new Exception("Super instance variable " + v.ident + " not defined");
                    if (!checkSubtype(matchingType, v.type))
                        throw new Exception("Problem with type of inherrited instance variable " + v.ident + " of type " + matchingType + " is not a subtype of " + v.type);
                }
            }
            //check to see if superclass has methods
            if(!parentClass._methods.isEmpty())
            {
                //check each method
                for(Methods.Method mSuper : parentClass.getMethods())
                {
                    m =cb.getMethod(mSuper._methodIdent);
                    if(m!=null)
                    {
                        checkOverridenMethod(m, mSuper);
                    }
                }
            }
        }
	}
	
	private void checkOverridenMethod(Methods.Method m, Methods.Method mSuper) throws Exception {
		//make sure it has same number of arguments,
		//each argument is of correct type
		//return type is subtype of the type r inherited of the superclass method
		if(m._formalArgs._args.size()!=mSuper._formalArgs._args.size())
			throw new Exception("Overriden Method does not have same number of arguments");
		for (int i=0; i<m._formalArgs._args.size();i++)
	    {
			//check arguments to see if type matches
			//must be a inherited <: a (the overriding
			//System.out.println(m._formalArgs._args.get(i)._type);
			//System.out.println(mSuper._formalArgs._args.get(i)._type);
			
			if(!checkSubtype(m._formalArgs._args.get(i)._type,mSuper._formalArgs._args.get(i)._type))
				//if(type is super type)
				throw new Exception("Problem with arguments "+m._formalArgs._args.get(i)._type+" is not a subtype of "+mSuper._formalArgs._args.get(i)._type);
			
	    }
		
		//typecheck return statement declared type
		if(!checkSubtype(m._methodType, mSuper._methodType)) {
			//if(type is subtype)
			throw new Exception("Problem with return: "+m._methodType+ " is not a subtype of "+ mSuper._methodType);
		}
		
	}

	public static boolean checkSubtype(String typeInherited, String typeSuper) throws Exception {
		//check that types are valid
		//???THIS MAYBE not needed since now variables are checked before they get added
		if(!Tree.getInstance().exists(typeInherited))
			throw new Exception("Problem: " + typeInherited + " is not a valid type");
		if (!Tree.getInstance().exists(typeSuper))
        {
            throw new Exception("Problem: " + typeSuper + " is not a valid type");
        }
		
		Node n= Tree.getInstance().LCA(Tree.getInstance().getRoot(),typeInherited,typeSuper);
		
		//returns false if typeInherited is not a subtype of SuperType
		if(n==null||!n.toString().equals(typeSuper))
			return false;
		
		return true;
	}
	public static String getCommonAncestor(String type1, String type2) throws Exception {
		
		return Tree.getInstance().LCA(Tree.getInstance().getRoot(),type1,type2).getId();
	}

	public static String typeCheckOperator(String classType, String operation, String argumentType)
    {
        for (Class_Block.Clazz_Block cb : builtinAST.get_cbs())
        {
            if (cb._classIdent.equals(classType))
            {
                for (Methods.Method m : cb.getMethods())
                {
                    if (m._methodIdent.equals(operation))
                    {
                        ArrayList<String> argTypes = ((Args.Formal_Args) m._formalArgs).getArgTypes();
                        if (argTypes.size() == 1)
                        {
                            if (argTypes.get(0).equals(argumentType))
                                return m._methodType;
                        }
                    }
                }
            }
        }
        // again, but for ast
        for (Class_Block.Clazz_Block cb : ast.get_cbs())
        {
            if (cb._classIdent.equals(classType))
            {
                for (Methods.Method m : cb.getMethods())
                {
                    if (m._methodIdent.equals(operation))
                    {
                        ArrayList<String> argTypes = ((Args.Formal_Args) m._formalArgs).getArgTypes();
                        if (argTypes.size() == 1)
                        {
                            if (argTypes.get(0).equals(argumentType))
                                return m._methodType;
                        }
                    }
                }
            }
        }

        return null;
    }

    public static String typeCheckUnaryOperator(String classType, String operation)
    {
        for (Class_Block.Clazz_Block cb : builtinAST.get_cbs())
        {
            if (cb._classIdent.equals(classType))
            {
                for (Methods.Method m : cb.getMethods())
                {
                    if (m._methodIdent.equals(operation))
                    {
                        return m._methodType;
                    }
                }
            }
        }
        // again, but for ast
        for (Class_Block.Clazz_Block cb : ast.get_cbs())
        {
            if (cb._classIdent.equals(classType))
            {
                for (Methods.Method m : cb.getMethods())
                {
                    if (m._methodIdent.equals(operation))
                    {
                        return m._methodType;
                    }
                }
            }
        }

        return null;
    }

	void checkForUndefined() throws Exception
    {
        HashMap<String, String> clazzTable = classesTable.getClassTable();
        for (String ident : clazzTable.values())
        {
            String currExtends = clazzTable.get(ident);
            if (currExtends == null)
            {
            	throw new Exception("Undefined class "+ident);
            }
        }

    }

    void checkForCycles() throws Exception
    {
        HashMap<String, String> clazzTable = classesTable.getClassTable();
        for (String ident : clazzTable.values())
        {
            String startingIdent = ident;
            String currIdent = ident;
            String currExtends = clazzTable.get(currIdent);
            while (!currExtends.equals("Obj"))
            {
                if (currExtends.equals(startingIdent))
                {
                	throw new Exception("Class "+currIdent+" and Class "+currExtends +" have cycles");
                }
                currIdent = currExtends;
                currExtends = clazzTable.get(currIdent);
            }
        }
    }

    boolean checkConstructor()
    {
        List<Class_Block.Clazz_Block> class_blocks = ast.get_cbs();
        for (Class_Block.Clazz_Block class_block : class_blocks)
        {
            VarTable vt = class_block.checkConstructor();
        }
        return false;
    }
}
