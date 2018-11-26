
//Starter code from Cool 

import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class Main {

    // Command line options
    String sourceFile = "";

    // Internal state
    ErrorReport report;

    boolean DebugMode = false; // True => parse in debug mode 
    Program builtinAST = null;
    Program ast = null;
    HashMap<String, CHeaderNode> classHeaderDictionary = new HashMap<>();

    static public void main(String args[])
    {
        Main q = new Main();
        q.go(args);
    }

    public void go(String[] args)
    {
        report = new ErrorReport();
        parseCommandLine(args);
        TypeChecker typeChecker = parseProgram();
        BuildHFile(typeChecker);
    }

    void BuildHFile(TypeChecker typeChecker)
    {
        // use typeChecker.tree, typechecker.classesTable, & VarTableSingleton.TheTable to generate output
        String[] tempSource = this.sourceFile.split("/");
        String outputFileName = tempSource[tempSource.length - 1].replace("qk", "h");
        try
        {
            FileWriter outputStream = new FileWriter(outputFileName);
            outputStream.write("#ifndef " + outputFileName.replace(".", "_") + '\n');
            outputStream.write("#define " + outputFileName.replace(".", "_") + "\n\n\n");
            // fill in .h stuff here

            // adds 'struct class_[className]_struct' in breadth-first search manner from tree
            ArrayList<Node> childToAdd = new ArrayList<>();
            childToAdd.add(typeChecker.tree.getRoot());

            // write declarations
            while(childToAdd.size() > 0)
            {
                String className = childToAdd.get(0).getId();
                String structName = "class_" + className + "_struct";
                outputStream.write("struct " + structName + ";\n");
                String instanceName = "class_" + className;
                CHeaderNode newNode = new CHeaderNode(structName, instanceName);
                classHeaderDictionary.put(className, newNode);
                outputStream.write("typedef struct " + structName + "*" + " " + instanceName + ";\n\n");
                childToAdd.addAll(childToAdd.get(0).getChildren());
                childToAdd.remove(0);
            }

            // create typedef instances
            for (VarTable vt : VarTableSingleton.TheTable)
            {
                String typeDefString = "typedef struct obj_" + vt.className + "_struct {\n";
                outputStream.write(typeDefString);

                String classIdent = "clazz";
                String instanceName = classHeaderDictionary.get(vt.className).classInstanceName + " " + classIdent + ";\n";
                outputStream.write("\t" + instanceName);
                classHeaderDictionary.get(vt.className).objectInstanceStructVariables.add(classIdent);

                if(vt.className.equals("String"))
                {
                    classHeaderDictionary.get(vt.className).objectInstanceStructVariables.add("value");
                    String value = "char* value;\n";
                    outputStream.write("\t" + value);
                }
                else if (vt.className.equals("Int") || vt.className.equals("Boolean"))
                {
                    classHeaderDictionary.get(vt.className).objectInstanceStructVariables.add("value");
                    String value = "int value;\n";
                    outputStream.write("\t" + value);
                }
                else
                {
                    for (Var v : vt.constructorTable)
                    {
                        String type = classHeaderDictionary.get(v.type).objectInstanceName;
                        String varName = v.ident.replace("this.", "");
                        classHeaderDictionary.get(vt.className).objectInstanceStructVariables.add(varName);
                        outputStream.write("\t" + type + " " + varName + ";\n");
                    }
                }

                String objectInstanceName = "obj_" + vt.className;
                classHeaderDictionary.get(vt.className).objectInstanceName = objectInstanceName;
                outputStream.write("} *" + objectInstanceName + ";\n\n");
            }

            // write definitions
            for (VarTable vt : VarTableSingleton.TheTable)
            {
                String structDef = "struct " + classHeaderDictionary.get(vt.className).classTypeName + " {\n";
                outputStream.write(structDef);

                // write constructor
                String constructorType = classHeaderDictionary.get(vt.className).objectInstanceName;
                String constructorArgs = "( ";
                int constructArgLength = vt.classArgTypes.size();
                if (constructArgLength == 0)
                {
                    constructorArgs += "void );\n";
                }
                else
                {
                    constructorArgs += classHeaderDictionary.get(vt.classArgTypes.get(0)).objectInstanceName;
                    for (int i = 1; i < constructArgLength; i++)
                    {
                        constructorArgs += ", " + classHeaderDictionary.get(vt.classArgTypes.get(i)).objectInstanceName;
                    }
                    constructorArgs += ");\n";
                }
                outputStream.write("\t" + constructorType + " (*constructor) " + constructorArgs);

                // write methods
                LinkedList<Var> completedMethodTable = new LinkedList<>();
                HashMap<String, LinkedList<Var>> completedMethodArgs = new HashMap<>();
                String currentClassName = vt.className;

                Stack<String> classInheritanceStack = new Stack<>();
                while (!currentClassName.equals("Obj"))
                {
                    classInheritanceStack.push(currentClassName);
                    currentClassName = ClassesTable.getInstance().getParentClass(currentClassName);
                }
                classInheritanceStack.push(currentClassName);

                while(!classInheritanceStack.empty())
                {
                    currentClassName = classInheritanceStack.pop();
                    for (Var v : VarTableSingleton.getTableByClassName(currentClassName).methodTable)
                    {
                        if (!completedMethodTable.contains(v))
                        {
                            completedMethodTable.add(v);
                            String methodReturnType = classHeaderDictionary.get(v.type).objectInstanceName;

                            // add 'self' as first method arg
                            String methodArgs = "( " + classHeaderDictionary.get(vt.className).objectInstanceName;

                            completedMethodArgs.put(v.ident, VarTableSingleton.getTableByClassName(currentClassName).methodVars.get(v.ident));
                            LinkedList<Var> methodArgList = completedMethodArgs.get(v.ident);
                            int methodArgsLenth = methodArgList.size();
                            for (int i = 0; i < methodArgsLenth; i++) {
                                methodArgs += ", " + classHeaderDictionary.get(methodArgList.get(i).type).objectInstanceName;
                            }
                            methodArgs += " );\n";
                            String methodName = "(*" + v.ident + ") ";
                            outputStream.write("\t" + methodReturnType + " " + methodName + " " + methodArgs);
                        }
                    }
//                    if (currentClassName.equals("Obj"))
//                        break;
//                    currentClassName = ClassesTable.getInstance().getParentClass(currentClassName);
                }




                outputStream.write("};\n\n");
            }

            outputStream.write("#endif" + '\n');
            outputStream.flush();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }

    }

    HashMap<String, LinkedList<Var>> GetCompleteMethodArgs(String className)
    {
        HashMap<String, LinkedList<Var>> classMethodArgs = VarTableSingleton.getTableByClassName(className).methodVars;
        String currentClassName = className;
        while (!currentClassName.equals("Obj"))
        {
            currentClassName = ClassesTable.getInstance().getParentClass(currentClassName);
            HashMap<String, LinkedList<Var>> parentMethodArgs = VarTableSingleton.getTableByClassName(currentClassName).methodVars;
            for (Map.Entry<String, LinkedList<Var>> entry : parentMethodArgs.entrySet())
            {
                if (!classMethodArgs.containsKey(entry.getKey()))
                {
                    classMethodArgs.put(entry.getKey(), entry.getValue());
                }
            }
        }

        return classMethodArgs;
    }

    // gets methods from class plus inherited methods
    LinkedList<Var> GetCompleteMethodTable(String className)
    {
        LinkedList<Var> classMethods = VarTableSingleton.getTableByClassName(className).methodTable;
        String currentClassName = className;
        while(!currentClassName.equals("Obj"))
        {
            currentClassName = ClassesTable.getInstance().getParentClass(currentClassName);
            LinkedList<Var> parentMethodTable = VarTableSingleton.getTableByClassName(currentClassName).methodTable;
            for (Var v : parentMethodTable)
            {
                if (!classMethods.contains(v))
                {
                    classMethods.add(v);
                }
            }
        }
        return classMethods;
    }

    void parseCommandLine(String args[])
    {
	    try
        {
            // Command line parsing
            Options options = new Options();
            options.addOption("d", false, "debug mode (trace parse states)");
            CommandLineParser  cliParser = new GnuParser();
            CommandLine cmd = cliParser.parse( options, args);
            DebugMode = cmd.hasOption("d");
            String[] remaining = cmd.getArgs();
            int argc = remaining.length;
            if (argc == 0)
            {
                report.err("Input file name required");
                System.exit(1);
            }
            else if (argc == 1)
            {
                sourceFile = remaining[0];
            }
            else
            {
                report.err("Only 1 input file name can be given;"+ " ignoring other(s)");
            }
        }
        catch (Exception e)
        {
            System.err.println("Argument parsing problem");
            System.err.println(e.toString());
            System.exit(1);
        }
    }

    TypeChecker parseProgram()
    {
        System.out.println("Beginning parse ...");
        try
        {


            Symbol result;
            ComplexSymbolFactory symbolFactory = new ComplexSymbolFactory();
            Lexer scanner = new Lexer (new FileReader( "built-ins.qk" ), symbolFactory);
                parser p = new parser( scanner, symbolFactory);
                result = p.parse();
            //ast of built in clasess
            builtinAST = (Program) result.value;

            System.out.println("Built in classes parsed, built-in ast built");

            scanner = new Lexer (new FileReader ( sourceFile), symbolFactory);
                p = new parser( scanner, symbolFactory);
                p.setErrorReport(report);

            if (DebugMode) { result =  p.debug_parse(); }
            else
            {
                result = p.parse();
            }

            ast = (Program) result.value;
        }
        catch (Exception e)
        {
            System.err.println("Yuck, blew up in parse phase");
            e.printStackTrace();
	        System.exit(1);
        }

        final TypeChecker typeChecker = new TypeChecker(builtinAST,ast);
        try
        {
            if(typeChecker.TypeCheck())
            {
                System.out.println("Done TypeChecking");
            }
        }
        catch (Exception e)
        {
            System.err.println("Yuck, blew up in typecheck phase");
            e.printStackTrace();
            System.exit(1);
        }
        return typeChecker;

    }
}

