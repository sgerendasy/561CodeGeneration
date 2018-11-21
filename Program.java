//import java.util.List;
//


import java.util.LinkedList;
import java.util.List;

public class Program
{
    List<Class_Block.Clazz_Block> _cbs;
    List<Statement> _stmts;
    public Program(List<Class_Block.Clazz_Block> cb, List<Statement> stmts)
    {
        this._cbs = cb;
        this._stmts = stmts;
        //System.out.println(this.toString());
    }

    public void visit()throws Exception
    {
    	//first visit to make sure syntax is correct
    	//only visit class
        System.out.println("visiting program");
        for (Class_Block.Clazz_Block cb: _cbs)
        {
            TypeChecker.currentClass = cb._classIdent;
            VarTableSingleton.getCurrentInstance().addTable(new VarTable(cb._classIdent));
            cb.visit();
        }
        //add statements to a dummy class to type checked
        if(!_stmts.isEmpty()) {
        	Class_Block.Clazz_Block cb = new Class_Block.Clazz_Block ("$statementsDummyClass", Args.formalArgs(), _stmts, new LinkedList<Methods.Method>());
            this._cbs.add(cb);
            TypeChecker.currentClass = "$statementsDummyClass";
            VarTableSingleton.getCurrentInstance().addTable(new VarTable(cb._classIdent));
        	cb.visit();
        }
    }

    public void methodVisit() throws Exception
    {
        for (Class_Block.Clazz_Block cb: _cbs)
        {
            TypeChecker.currentClass = cb._classIdent;
            cb.methodVisit();
        }
    }

    public void visit2()throws Exception
    {
    	//second visit to typeCheck
        for (Class_Block.Clazz_Block cb : _cbs)
        {
            TypeChecker.currentClass = cb._classIdent;
            cb.visit2();
        }
    }

    public List<Class_Block.Clazz_Block> get_cbs()
    {
        return this._cbs;
    }
    public Class_Block.Clazz_Block get_cb(String classIdent)
    {
    	
    	for (Class_Block.Clazz_Block c: _cbs)
        {
            if(c._classIdent.matches(classIdent)) {
            	return c;
            }
        }
    	
        return null;
    }

    public String toString(){
        StringBuilder classesString = new StringBuilder();
        StringBuilder statementsString = new StringBuilder();

        if (_cbs != null)
        {
            for (Class_Block.Clazz_Block c: _cbs) classesString.append(c.toString());
        }
        if (_stmts != null)
        {
            for (Statement s: _stmts)
            {
                statementsString.append("\t").append(s.toString()).append("\n");
            }
        }

        return "Classes:\n" + classesString + "\nStatements:\n" + statementsString;
    }


}
