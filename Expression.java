
import java.util.List;

public abstract class Expression
{
    public Expression() { }
    abstract void visit2(String classIdent) throws Exception;
    abstract String getType() throws Exception;
    abstract String getType(String methodIdent) throws Exception;
    protected abstract String getIdent() throws Exception;
    protected abstract void visit2(String classIdent, String methodIdent);
    abstract void SetClassIdent(String ci);
    abstract void SetMethodIdent(String mi);
    
    public static class Priority extends Expression
    {
        public Expression e;
        public int _left, _right;

        public Priority(Expression e, int left, int right)
        {
            this.e = e;
            this._left = left;
            this._right = right;
        }

        public void SetClassIdent(String ci)
        {
            this.e.SetClassIdent(ci);
        }

        public void SetMethodIdent(String mi)
        {
            this.e.SetMethodIdent(mi);
        }

        public String getType() throws Exception
        {
            return e.getType();
        }
        public String getType(String methodIdent) throws Exception
        {
            return e.getType(methodIdent);
        }

        public void visit2(String classIdent) throws Exception
        {
            e.visit2(classIdent);
        }

        public String toString()
        {
            return "(" + e + ")";
        }


		protected String getIdent() throws Exception
        {
            return e.getIdent();
		}

		@Override
		protected void setsetType(String type) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void visit2(String classIdent, String methodIdent) {
			// TODO Auto-generated method stub
			
		}

    }
    public static Expression.Priority priority(Expression e, int left, int right)
    {
        return new Expression.Priority(e, left, right);
    }



    public static class Binex extends Expression
    {
        public Expression e1, e2;
        public String op;
        public String classIdent;
        public String methodIdent;

        public Binex(Expression e1, String op, Expression e2)
        {
            this.e1 = e1;
            this.e2 = e2;
            this.op = op;
        }

        public void SetClassIdent(String ci)
        {
            this.e1.SetClassIdent(ci);
            this.e2.SetClassIdent(ci);
        }

        public void SetMethodIdent(String mi)
        {
            this.e1.SetMethodIdent(mi);
            this.e2.SetMethodIdent(mi);
        }

        public String getType() throws Exception
        {
            String e1Type = e1.getType();
            String e2Type = e2.getType();
            String operatorString = OperatorToString.getOperatorDict().get(this.op);
            
            String rtype = null;
            if(operatorString.equals("$AND")||operatorString.equals("$OR")) {
            	
            	if(e1Type.equals("Boolean")&&e2Type.equals("Boolean"))
            		rtype="Boolean";
            }
            else {
            rtype = TypeChecker.typeCheckOperator(e1Type, operatorString, e2Type);
            
            while(rtype ==null) {
            	e1Type= TypeChecker.getParent(e1Type);
            	e2Type= TypeChecker.getParent(e2Type);
            	if(e2Type==null||e2Type==null)
            		throw new Exception(operatorString + "(" + e1Type + ", " + e2Type + ") ");
            	if(rtype ==null)
            			rtype= TypeChecker.typeCheckOperator(e1Type, operatorString, e2Type);
            	if(rtype==null&& e1Type=="Obj"&& e1Type=="Obj")
            		break;
            }
            if (rtype == null)
               throw new Exception(operatorString + "(" + e1Type + ", " + e2Type + ") not defined");
        	}
            return rtype;
        }

        public String getType(String methodIdent) throws Exception
        {
        	//if(e1.getType().equals("Identifier"))
            String e1Type = e1.getType(methodIdent);
            String e2Type = e2.getType(methodIdent);
            String operatorString = OperatorToString.getOperatorDict().get(this.op);
            String rtype = null;
            if(operatorString.equals("$AND")||operatorString.equals("$OR")) {
            	
            	if(e1Type.equals("Boolean")&&e2Type.equals("Boolean"))
            		rtype="Boolean";
            }
            else {
            rtype = TypeChecker.typeCheckOperator(e1Type, operatorString, e2Type);
            
            while(rtype ==null) {
            	e1Type= TypeChecker.getParent(e1Type);
            	e2Type= TypeChecker.getParent(e2Type);
            	if(e2Type==null||e2Type==null)
            		throw new Exception(operatorString + "(" + e1Type + ", " + e2Type + ") ");
            	if(rtype ==null)
            			rtype= TypeChecker.typeCheckOperator(e1Type, operatorString, e2Type);
            	if(rtype==null&& e1Type=="Obj"&& e1Type=="Obj")
            		break;
            }
            if (rtype == null)
               throw new Exception(operatorString + "(" + e1Type + ", " + e2Type + ") not defined");
        	}
            return rtype;
        }

        public void visit2(String classIdent) throws Exception
        {
            e1.visit2(classIdent);
            e2.visit2(classIdent);
        }

        public String toString()
        {
            return e1 + " " + op + " " + e2;
        }

		protected String getIdent() {
			// there is no identifier for a binary expression
            return null;
		}

		@Override
		protected void setsetType(String type) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void visit2(String classIdent, String methodIdent) {
			this.classIdent=classIdent;
			this.methodIdent=methodIdent;
			
		}

    }
    public static Expression.Binex binop(Expression e1, String op, Expression e2)
    {
        return new Expression.Binex(e1, op, e2);
    }



    public static class Unex extends Expression
    {
        public String op;
        public Expression e1;
        public int _left, _right;
		public String classIdent;
		public String methodIdent;
        public Unex(String op, Expression e1, int left, int right)
        {
            this.e1 = e1;
            this.op = op;
            this._left = left;
            this._right = right;
        }

        public void SetClassIdent(String ci)
        {
            this.e1.SetClassIdent(ci);
        }

        public void SetMethodIdent(String mi)
        {
            this.e1.SetMethodIdent(mi);
        }

        public String getType() throws Exception
        {
            String eType = this.e1.getType();
            String opString = OperatorToString.getUnaryOperatorDict().get(this.op);
            if (this.op.equals("!") && eType.equals("Boolean"))
            {
                return eType;
            }
            String unopType = TypeChecker.typeCheckUnaryOperator(eType, opString);
            return unopType;
        }

        public String getType(String methodIdent) throws Exception
        {
            String eType = this.e1.getType(methodIdent);
            String opString = OperatorToString.getUnaryOperatorDict().get(this.op);
            String unopType = TypeChecker.typeCheckUnaryOperator(eType, opString);
            if (unopType == null)
            {
                if (this.op.equals("!"))
                    unopType = "Boolean";
            }
            return unopType;
        }

        public void visit2(String classIdent) throws Exception
        {
            e1.visit2(classIdent);
        }

        public String toString()
        {
            return op + e1.toString();
        }


		protected String getIdent() {
            // there is no identifier for a unary expression
			return null;
		}

		@Override
		protected void setsetType(String type) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void visit2(String classIdent, String methodIdent) {
			this.classIdent=classIdent;
			this.methodIdent=methodIdent;
			
			
		}

    }
    public static Expression.Unex unop(String op, Expression e, int left, int right)
    {
        return new Expression.Unex(op, e, left, right);
    }



    public static class StringLit extends Expression
    {
        public String _s;
        public int _left, _right;
        public String classIdent;
		public String methodIdent;
        public StringLit(String s, int left, int right)
        {
            this._s = s;
            this._left = left;
            this._right = right;
        }

        public String getType()
        {
            return ClassesTable.getInstance().getClass("String");
        }

        public String getType(String methodIdent)
        {
            return ClassesTable.getInstance().getClass("String");
        }

        public void SetClassIdent(String ci)
        {
            this.classIdent = ci;
        }

        public void SetMethodIdent(String mi)
        {
            this.methodIdent = mi;
        }

        public void visit2(String classIdent)
        {
            // TODO
        }

        public String toString()
        {
            return _s;
        }

		protected String getIdent() {
            // there is no identifier for a string lit
			return null;
		}

		@Override
		protected void setsetType(String type) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void visit2(String classIdent, String methodIdent) {
			this.classIdent=classIdent;
			this.methodIdent=methodIdent;
			
			
		}

    }
    public static Expression.StringLit stringLit(String s, int left, int right)
    {
        return new Expression.StringLit(s, left, right);
    }


    public static class IntConst extends Expression
    {
        public int i;
        public int _left, _right;
        public String classIdent;
		public String methodIdent;
        public IntConst(int i, int left, int right)
        {
            this.i = i;
            this._left = left;
            this._right = right;
        }

        public String getType()
        {
            return ClassesTable.getInstance().getClass("Int");
        }

        public String getType(String methodIdent)
        {
            return ClassesTable.getInstance().getClass("Int");
        }

        public void visit2(String classIdent)
        {
            // TODO
        }

        public void SetClassIdent(String ci)
        {
            this.classIdent = ci;
        }

        public void SetMethodIdent(String mi)
        {
            this.methodIdent = mi;
        }

        public String toString()
        {
            return i + "";
        }

		protected String getIdent() {
            // there is no identifier for an int const
			return null;
		}

		@Override
		protected void setsetType(String type) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void visit2(String classIdent, String methodIdent) {
			this.classIdent=classIdent;
			this.methodIdent=methodIdent;
			
			
		}

    }
    public static Expression.IntConst intconst(int i, int left, int right)
    {
         return new Expression.IntConst(i, left, right);
    }




    public static class Identifier extends Expression
    {
        //        public Location left, right;
        public int _left, _right;
        public String ident;
        public String type;
        public String classIdent;
		public String methodIdent;

        public Identifier(String i, int left, int right)
        {
            this.ident = i;
            this._left = left;
            this._right = right;
        }

        public void SetClassIdent(String classIdent)
        {
            this.classIdent = classIdent;
        }

        public void SetMethodIdent(String methodIdent)
        {
            this.methodIdent = methodIdent;
        }

        public String getType()
        {
            if (ident.equals("true") || ident.equals("false"))
            {
                return "Boolean";
            }
            if (ident.equals("none"))
            {
                return "Nothing";
            }
            if (ident.equals("this"))
            {
                this.type = TypeChecker.currentClass;
                return type;
            }
            // TODO: fix so it looks for the type in the correct table
            String  iType = "";
            if (classIdent != null && methodIdent != null)
                iType = VarTableSingleton.getTableByClassName(classIdent).GetTypeFromMethodVarTable(this.ident, methodIdent);
            else
                iType = VarTableSingleton.getTableByClassName(TypeChecker.currentClass).GetTypeFromVarTable(this.ident);
            if (iType == null)
                iType = VarTableSingleton.getTableByClassName(TypeChecker.currentClass).GetTypeFromMethodVarTable(this.ident, TypeChecker.currentMethod);
            return iType;
//            return type;
        }

        public String getType(String methodIdent) throws Exception
        {
            if (ident.equals("true") || ident.equals("false"))
            {
                return "Boolean";
            }
            if (ident.equals("none"))
            {
                return "Nothing";
            }
            if (ident.equals("this"))
            {
                this.type = TypeChecker.currentClass;
                return type;
            }
            // TODO: fix so it looks for the type in the correct table
            VarTable t = VarTableSingleton.getTableByClassName(TypeChecker.currentClass);
            String iType = VarTableSingleton.getTableByClassName(TypeChecker.currentClass).GetTypeFromMethodVarTable(this.ident, methodIdent);
            
            
            if (iType == null)
            	iType = VarTableSingleton.getTableByClassName(TypeChecker.currentClass).GetTypeFromVarTable(this.ident);
            if(iType ==null)
            	throw new Exception(this.ident + " not defined");
            return iType;
//            return type;
        }


        public void visit2(String classIdent) throws Exception
        {
        	 
        	 ClassesTable ct = ClassesTable.getInstance();
             if(ct.classTable.containsKey(ident))
             	throw new Exception("Var "+ ident + " (" + _left + ", " + _right + ") has same name as class ");

			VarTable varTable = VarTableSingleton.getTableByClassName(classIdent);
			
			String type = varTable.ExistsInVarTable(ident);
			if(type != null)
			{
                Var var = new Var(ident, type);
                varTable.AddVarToVarTable(var);
			}

        }

        public String toString()
        {
            return ident;
        }
        public void setType(String type)
        {
            this.type=type;
        }

		protected String getIdent() {
			
			return ident;
		}

		@Override
		protected void setsetType(String type) {
			this.type=type;
			
		}

		@Override
		protected void visit2(String classIdent, String methodIdent) {
			this.classIdent=classIdent;
			this.methodIdent=methodIdent;
			
			
		}

    }
    public static Expression.Identifier ident(String s, int left, int right)
    {
        return new Expression.Identifier(s, left, right);
    }


    public static class Method_Call extends Expression
    {
        Expression _e;
        public String _ident;
        public String _varIdent;
        public String methodName;
        Args _optionalArgs;
        public int _left, _right;

        public boolean isMethod;
        public String classIdent;
		public String methodIdent;

        public Method_Call(Expression e, String ident, int left, int right) throws Exception
        {
            this._e = e;

            if (e != null && e.getIdent().equals("this"))
                this._varIdent = e + "." + ident;
            else
                this._varIdent = e.getIdent();
            methodName = ident;

            this._ident = ident;
            this._left = left;
            this._right = right;
            isMethod = false;
        }

        public Method_Call(Expression e, String ident, Args args, int left, int right) throws Exception
        {
            this._e = e;

            this._varIdent = e.getIdent();
            this.methodName = ident;

            this._ident = ident;
            this._optionalArgs = args;
            this._left = left;
            this._right = right;
            isMethod = true;
        }

        public void SetClassIdent(String ci)
        {
            this._e.SetClassIdent(ci);
        }

        public void SetMethodIdent(String mi)
        {
            this._e.SetMethodIdent(mi);
        }

        public String getType() throws Exception
        {
            String identifierType = this._e.getType();
            String type = "";
            if (_optionalArgs == null)
            {
                // if optional args are null, I'm accessing a variable
                type = VarTableSingleton.getTableByClassName(identifierType).GetTypeFromConstructorTable("this." + _ident);
            }
            else
            {
                type = VarTableSingleton.getTableByClassName(identifierType).ExistsInMethodTable(this._ident);
                VarTableSingleton.getTableByClassName(identifierType).checkMethodArgs(this._ident, this._optionalArgs.getArgTypes());
            }
            return type;
        }

        public String getType(String methodIdent) throws Exception
    {

        String identifierType = this._e.getType();
        String type = "";
        if (_optionalArgs == null)
        {
            // if optional args are null, I'm accessing a variable
            type = VarTableSingleton.getTableByClassName(identifierType).GetTypeFromConstructorTable("this." + _ident);
        }
        else
        {
            type = VarTableSingleton.getTableByClassName(identifierType).ExistsInMethodTable(this._ident);
            VarTableSingleton.getTableByClassName(identifierType).checkMethodArgs(this._ident, this._optionalArgs.getArgTypes());

        }
        return type;
    }

        public String getIdent() throws Exception
        {
            if (this._e != null)
            {
//                if (!this.toString().contains("this."))
//                    throw new Exception("Cannot access private variables in class " + this.toString());
                return _e.getIdent() + "." + _ident;
            }
        	return _ident;
        }

        public void visit2(String classIdent) throws Exception
        {

        	_e.visit2(classIdent);
        	ClassesTable ct = ClassesTable.getInstance();
            if(ct.classTable.containsKey(_ident))
            	throw new Exception("Var "+ _ident + " has same name as class ");
        }

        public String toString()
        {
            StringBuilder args = new StringBuilder();
            if (this._optionalArgs != null)
            {
                args.append(this._optionalArgs.toString());
            }
            return _e + "." + _ident + args;
        }

		@Override
		protected void setsetType(String type) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void visit2(String classIdent, String methodIdent) {
			this.classIdent=classIdent;
			this.methodIdent=methodIdent;
			
			
		}

    }
    public static Expression.Method_Call methodCall(Expression e, String ident, int left, int right) throws Exception
    {
        return new Expression.Method_Call(e, ident, left, right);
    }
    public static Expression.Method_Call methodCall(Expression e, String ident, Args args, int left, int right) throws Exception
    {
        return new Expression.Method_Call(e, ident, args, left, right);
    }

    // Constructor class is for when a class gets instantiated: C1(4, "example");
    public static class Constructor extends Expression
    {
        String _ident;
        Args.Informal_Args _args;
        public int _left, _right;
        public String classIdent;
		public String methodIdent;
        public Constructor(String ident, Args.Informal_Args args, int left, int right)
        {
            this._ident = ident;
            this._args = args;
            this._left = left;
            this._right = right;
            
        }

        public void SetClassIdent(String ci)
        {
            this.classIdent = ci;
        }

        public void SetMethodIdent(String mi)
        {
            this.methodIdent = mi;
        }

        public String getType() throws Exception
        {
            // the class identifier is the type, right?
            this._args.visit2(this._ident);
            return this._ident;
        }

        public String getType(String methodIdent) throws Exception
        {
            // the class identifier is the type, right?
            this._args.visit2(this._ident);
            return this._ident;
        }

        public void visit2(String classIdent) throws Exception
        {
        	//check constructor
        	List<Class_Block.Clazz_Block> class_blocks = TypeChecker.ast.get_cbs();
            for (Class_Block.Clazz_Block class_block : class_blocks)
            {
                if(class_block._classIdent.equals(_ident)) {
                	
                	if(!(class_block._argList._args.size()==_args.getArgs().size()))
                		throw new Exception("Class " + class_block._classIdent + "is getting instantiated with the wrong number of arguments");
                }
                	
            }
        }

        public String toString()
        {
            return _ident + _args;
        }
		protected String getIdent() {
			
			return _ident;
		}

		@Override
		protected void setsetType(String type) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void visit2(String classIdent, String methodIdent) {
			this.classIdent=classIdent;
			this.methodIdent=methodIdent;
			
		}

    }
    public static Expression.Constructor constructor(String ident, Args.Informal_Args args, int left, int right)
    {
        return new Expression.Constructor(ident, args, left, right);
    }
	protected abstract void setsetType(String type);
	

}