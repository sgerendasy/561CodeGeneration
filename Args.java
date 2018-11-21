import java.util.ArrayList;
import java.util.LinkedList;

public abstract class Args
{

    public LinkedList<Args.Arg> _args;
	public Args() { }
    public void addArg(String ident, String type) {}
    public void addArg(Expression e) {}
    abstract ArrayList<String> getArgTypes() throws Exception;
    abstract void visit2(String s) throws Exception;
	abstract void visit2(String s, String methodIdent) throws Exception;

    public static class Formal_Args extends Args
    {
        public Formal_Args()
        {
            this._args = new LinkedList<>();
        }
        public Formal_Args(String ident, String type)
        {
            this._args = new LinkedList<>();
            this._args.add(new Arg(ident, type));
        }

        public void addArg(String ident, String type)
        {
            this._args.add(new Arg(ident, type));
        }

        public void visit2(String classIdent) throws Exception
        {
            // only called for formal args in class definition
        	VarTable varTable;
        	Var var;
        	for (Arg a : this._args)
            {
        		var = new Var(a._ident, a._type);
        		varTable = VarTableSingleton.getTableByClassName(classIdent);
                varTable.AddVarToVarTable(var);
            }
            LinkedList<String> listOfTypes = new LinkedList<>();
            for (Arg e : this._args)
            {
                listOfTypes.add(e._type);
            }
            VarTableSingleton.getTableByClassName(classIdent).addClassArgs(listOfTypes);
        }

        public void visit2(String classIdent, String methodIdent) throws Exception
        {
            // only called for formal args in method definition
            for (Arg a : this._args)
            {
                Var var = new Var(a._ident,a._type);
                if (a._ident.contains("this."))
                {
                    VarTable varTable = VarTableSingleton.getTableByClassName(classIdent);
                    varTable.AddVarToConstructorTable(var);
                }
                else
                {
                    VarTable varTable = VarTableSingleton.getTableByClassName(classIdent);
                    varTable.AddVarToMethodVarTable(methodIdent, var);
                    
                }
            }
            LinkedList<String> listOfTypes = new LinkedList<>();
            for (Arg e : this._args)
            {
                listOfTypes.add(e._type);
            }

            VarTableSingleton.getTableByClassName(classIdent).addMethodArgs(methodIdent, listOfTypes);
        }

        public ArrayList<String> getArgTypes()
        {
            ArrayList<String> argTypes = new ArrayList<>();
            for (int i = 0; i < this._args.size(); i++)
            {
                argTypes.add(this._args.get(i)._type);
            }
            return argTypes;
        }

        public String toString()
        {
            StringBuilder argsResult = new StringBuilder();
            argsResult.append("(");
            for (Arg a: _args)
            {
                argsResult.append(a).append(",");
            }
            if (_args.size() > 0)
                argsResult.deleteCharAt(argsResult.length() - 1);
            argsResult.append(")");
            return argsResult.toString();
        }
    }
    public static Args.Formal_Args formalArgs()
    {
        return new Args.Formal_Args();
    }
    public static Args.Formal_Args formalArgs(String ident, String type)
    {
        return new Args.Formal_Args(ident, type);
    }


    public static class Informal_Args extends Args
    {
        LinkedList<Expression> _args;
        public Informal_Args()
        {
            this._args = new LinkedList<>();
        }
        public Informal_Args(Expression e)
        {
            this._args = new LinkedList<>();
            this._args.add(e);
        }
        public void addArg(Expression e)
        {
            this._args.add(e);
        }
        public LinkedList<Expression> getArgs()
        {
			return this._args;
        }

        public ArrayList<String> getArgTypes() throws Exception
        {
            ArrayList<String> argTypes = new ArrayList<>();
            for (int i = 0; i < this._args.size(); i++)
            {
                argTypes.add(this._args.get(i).getType());
            }
            return argTypes;
        }
        


        public void visit2(String classIdent) throws Exception
        {
            // check to make sure each arg is the correct type for class constructor
            VarTable t = VarTableSingleton.getTableByClassName(classIdent);
            t.checkClassArgs(this.getArgTypes());
        }

        public void visit2(String classIdent, String methodIdent) throws Exception
        {
            // check to make sure each arg is the correct type for method in classIdent
            ArrayList<String> listOfTypes = new ArrayList<>();
            for (Expression e : this._args)
            {
                e.SetClassIdent(classIdent);
                e.SetMethodIdent(methodIdent);
                listOfTypes.add(e.getType());
            }
            VarTableSingleton.getTableByClassName(classIdent).checkMethodArgs(methodIdent, listOfTypes);
        }

        public String toString()
        {
            StringBuilder argsResult = new StringBuilder();
            argsResult.append("(");
            for (Expression e: _args)
            {
                argsResult.append(e).append(",");
            }
            if (_args.size() > 0)
                argsResult.deleteCharAt(argsResult.length() - 1);
            argsResult.append(")");
            return argsResult.toString();
        }
    }
    public static Args.Informal_Args informalArgs(Expression e)
    {
        return new Args.Informal_Args(e);
    }
    public static Args.Informal_Args informalArgs()
    {
        return new Args.Informal_Args();
    }


    public static class Arg extends Args
    {
        public String _ident;
        public String _type;
        public Arg(String ident, String type)
        {
            this._ident = ident;
            this._type = type;
        }

        public ArrayList<String> getArgTypes()
        {
            ArrayList<String> argTypes = new ArrayList<>();
            argTypes.add(this._type);
            return argTypes;
        }

        public void visit2(String _classIdent) throws Exception
        {
            Var var = new Var(this._ident, this._type);
            VarTableSingleton.getTableByClassName(_classIdent).AddVarToVarTable(var);
        }

        public void visit2(String classIdent, String methodIdent) throws Exception
        {

        }

        public String toString()
        {
            return this._ident + " : " + this._type;
        }
    }
    public LinkedList<Expression> getArgs()
    {
		return null;
    }
	
}
