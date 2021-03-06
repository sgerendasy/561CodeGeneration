
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
import java.util.Map.Entry;


public class Main {

    // Command line options
    String sourceFile = "";
    static FileWriter outputStream;
    public static boolean amSam = false;

    // Internal state
    ErrorReport report;

    boolean DebugMode = false; // True => parse in debug mode 
    Program builtinAST = null;
    Program ast = null;
    public static HashMap<String, CHeaderNode> classHeaderDictionary = new HashMap<>();
    // used to name register variables in each class
    public static int nodeIndex = 0;

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
        BuildCFile(typeChecker);
        System.out.println("\nC CodeGenerated");
    }

    // returns -1 if method doesn't exist. Otherwise returns the index of the method
    private int methodTableContains(LinkedList<Var> methodTable, Var var)
    {
        for (int i = 0; i < methodTable.size(); i++)
        {
            if (methodTable.get(i).ident.equals(var.ident))
                return i;
        }
        return -1;
    }

    void BuildHFile(TypeChecker typeChecker)
    {
        // use typeChecker.tree, typechecker.classesTable, & VarTableSingleton.TheTable to generate output
        String[] tempSource = this.sourceFile.split("/");
        LinkedList<String> publicMethodList = new LinkedList<>();
        String outputFileName = tempSource[tempSource.length - 1].replace("qk", "h");
        try
        {
            FileWriter outputStream;
            if (amSam)
                outputStream = new FileWriter("/Users/fringeclass/ClionProjects/untitled/" + outputFileName);
            else
                outputStream = new FileWriter(outputFileName);


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
                String structDef = "struct " + classHeaderDictionary.get(vt.className).classStructName + " {\n";
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
                    constructorArgs += classHeaderDictionary.get(vt.classArgTypes.get(0).type).objectInstanceName;
                    for (int i = 1; i < constructArgLength; i++)
                    {
                        constructorArgs += ", " + classHeaderDictionary.get(vt.classArgTypes.get(i).type).objectInstanceName;
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
                        // -1 if doesn't exist in method table
                        int existsRes = methodTableContains(completedMethodTable, v);
                        if (existsRes == -1)
                        {
                            String methodReturnName = VarTableSingleton.getTableByClassName(currentClassName).GetTypeFromMethodTable(v.ident);
                            String publicMethodTemp = classHeaderDictionary.get(methodReturnName).objectInstanceName + " " + vt.className + "_method_" + v.ident + "(";
                            completedMethodTable.add(v);
                            String methodReturnType = classHeaderDictionary.get(v.type).objectInstanceName;

                            // add 'self' as first method arg
                            String methodArgs = "( " + classHeaderDictionary.get(vt.className).objectInstanceName;

                            completedMethodArgs.put(v.ident, VarTableSingleton.getTableByClassName(currentClassName).methodArgs.get(v.ident));
                            publicMethodTemp += classHeaderDictionary.get(vt.className).objectInstanceName + " this";
                            LinkedList<Var> methodArgList = completedMethodArgs.get(v.ident);
                            int methodArgsLenth = methodArgList.size();
                            for (int i = 0; i < methodArgsLenth; i++) {
                                methodArgs += ", " + classHeaderDictionary.get(methodArgList.get(i).type).objectInstanceName;
                                publicMethodTemp += ", " + classHeaderDictionary.get(methodArgList.get(i).type).objectInstanceName + " " + methodArgList.get(i).ident;
                            }
                            methodArgs += " );\n";
                            publicMethodTemp += " );\n";
                            publicMethodList.add(publicMethodTemp);
                            String methodName = "(*" + v.ident + ") ";
                            outputStream.write("\t" + methodReturnType + " " + methodName + " " + methodArgs);
                        }
                    }
                }
                outputStream.write("};\n\n");
            }

            // write externs
            String strLitExtern = "extern " + classHeaderDictionary.get("String").objectInstanceName + " str_lit(char *s);\n";
            outputStream.write(strLitExtern);

            String intLitExtern = "extern " + classHeaderDictionary.get("Int").objectInstanceName + " int_lit(int n);\n";
            outputStream.write(intLitExtern);

            // write externs for class instances
            for (Map.Entry<String, String> clazzes : ClassesTable.getInstance().classTable.entrySet())
            {
                if(!clazzes.getKey().equals("$statementsDummyClass"))
                {
                    if (!ClassIsBuiltIn(clazzes.getKey()))
                    {
                        String classConstructor = "extern obj_" + clazzes.getKey() + " new_" + clazzes.getKey() + "(";
                        for (Var v : VarTableSingleton.getTableByClassName(clazzes.getKey()).classArgTypes)
                        {
                            classConstructor += classHeaderDictionary.get(v.type).objectInstanceName + " " + v.ident + ", ";
                        }
                        if (VarTableSingleton.getTableByClassName(clazzes.getKey()).classArgTypes.size() > 0)
                        {
                            classConstructor = classConstructor.substring(0, classConstructor.length() - 2);
                        }
                        classConstructor += ");\n";
                        outputStream.write(classConstructor);
                    }
                    String objExtern = "extern " + classHeaderDictionary.get(clazzes.getKey()).classInstanceName + " class_" + clazzes.getKey() + "_Instance;\n";
                    classHeaderDictionary.get(clazzes.getKey()).classInstanceSingletonName = "class_" + clazzes.getKey() + "_Instance";
                    outputStream.write(objExtern);
                }
            }

            String lit_trueExtern = "extern " + classHeaderDictionary.get("Boolean").objectInstanceName + " lit_true;\n";
            outputStream.write(lit_trueExtern);
            String lit_falseExtern = "extern " + classHeaderDictionary.get("Boolean").objectInstanceName + " lit_false;\n";
            outputStream.write(lit_falseExtern);

            String nothingObjExtern = "extern " + classHeaderDictionary.get("Nothing").objectInstanceName + " nothing;\n";
            outputStream.write(nothingObjExtern);

            outputStream.write("\n\n");

            // write method declaration
            for (String s : publicMethodList)
            {
                outputStream.write(s);
            }
            outputStream.write("\n\n");


            outputStream.write("#endif" + '\n');
            outputStream.flush();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }

    }

    private boolean ClassIsBuiltIn(String className)
    {
        for (Class_Block.Clazz_Block cb : builtinAST._cbs)
        {
            if (cb._classIdent.equals(className))
                return true;
        }
        return false;
    }

    LinkedList<VarTable> GetAllClassesInBFSOrder()
    {
        LinkedList<VarTable> theClasses = new LinkedList<>();
        Node currentNode = null;
        LinkedList<Node> nodeBuffer = new LinkedList<>();
        nodeBuffer.add(Tree.getInstance().getRoot());
        int builtInCount = 0;
        while(!nodeBuffer.isEmpty())
        {
            currentNode = nodeBuffer.pop();
            if (ClassIsBuiltIn(currentNode.getId()) || builtInCount >= 5)
            {
                builtInCount++;
                for (Node child : currentNode.getChildren())
                {
                    nodeBuffer.add(child);
                }
                theClasses.add(VarTableSingleton.getTableByClassName(currentNode.getId()));
            }
            else
            {
                nodeBuffer.add(currentNode);
            }

        }
        theClasses.remove(VarTableSingleton.getTableByClassName("$statementsDummyClass"));
        theClasses.add(VarTableSingleton.getTableByClassName("$statementsDummyClass"));

        return theClasses;
    }

    void BuildCFile(TypeChecker typeChecker) 
    {
      
        String[] tempSource = this.sourceFile.split("/");
        String outputFileName;
        if (amSam)
            outputFileName = "/Users/fringeclass/ClionProjects/untitled/" + tempSource[tempSource.length - 1].replace("qk", "c");
        else
            outputFileName = tempSource[tempSource.length - 1].replace("qk", "c");

        String headerFileName = tempSource[tempSource.length - 1].replace("qk", "h");
        try {
			outputStream= new FileWriter(outputFileName);
            outputStream.write("#include <stdio.h>\n");
            outputStream.write("#include <stdlib.h>\n");
            outputStream.write("#include <string.h>\n");
            outputStream.write("#include \"" + headerFileName+"\"\n\n\n");
            
            VarTable t = VarTableSingleton.getTableByClassName("$statementsDummyClass");
            if(!(t==null))
            {
                outputStream.write("void quackmain(); \n\n");
            }

            outputStream.write("int main(int argc, char** argv) {\n");
            outputStream.write("  printf(\"--- Begin: %s ---" + "\\" + "n\", argv[0]);\n\n");
            if(!(t==null))
            {
                outputStream.write("  quackmain();\n");
            }

            outputStream.write("  printf(\"" + "\\" + "n--- Terminated successfully ---\\" + "n\");\n\n");
            outputStream.write("  exit(0);\n");
            outputStream.write("}\n\n\n");

            LinkedList<VarTable> allClassesInBFSOrder = GetAllClassesInBFSOrder();

            // c file code generation
            for(VarTable c : allClassesInBFSOrder)
            {
            	int i=0;
            	int size = GetCompleteMethodTable(c.className).size() - 1;

            	// check to see if the class is a built in one
            	if (ClassIsBuiltIn(c.className))
            	{
                    for (MethodNode m : GetCompleteMethodTable(c.className)) {
                        if (c.className.equals("Obj"))
                        {
                            if (i == 0)
                            {
                                outputStream.write("class_Obj class_Obj_Instance; \n");
                                /* Constructor */
                                outputStream.write("obj_Obj new_Obj(  ) { \n");
                                outputStream.write("obj_Obj new_thing = (obj_Obj) malloc(sizeof(struct obj_Obj_struct));\n");
                                outputStream.write("new_thing->clazz = class_Obj_Instance;\n");
                                outputStream.write("return new_thing; \n}\n\n");
                            }
                            if (m.ident.equals("STR"))
                            {
                                outputStream.write("obj_String Obj_method_STR(obj_Obj self) {\n");
                                outputStream.write("long addr = (long) self;\n");
                                outputStream.write("char *rep;\n");
                                outputStream.write("asprintf(&rep, \"<Object at %ld>\", addr);\n");
                                outputStream.write("obj_String str = str_lit(rep); \n");
                                outputStream.write("return str; \n}\n\n");
                                classHeaderDictionary.get("Obj").CMethodToReturnType.put("Obj_method_STR", "obj_String");
                            }
                            else if (m.ident.equals("PRINT"))
                            {
                                outputStream.write("obj_Nothing Obj_method_PRINT(obj_Obj self) {\n");
                                outputStream.write("  obj_String str = self->clazz->STR(self);\n");
                                outputStream.write("  fprintf(stdout, \"%s\", str->value);\n");
                                outputStream.write("  return nothing; \n}\n\n");
                                classHeaderDictionary.get("Obj").CMethodToReturnType.put("Obj_method_PRINT", "obj_Nothing");
                            }
                            else if (m.ident.equals("EQUALS"))
                            {
                                outputStream.write("obj_Boolean Obj_method_EQUALS(obj_Obj self, obj_Obj other) {\n");
                                outputStream.write("  if (self == other) {\n");
                                outputStream.write("    return lit_true;\n");
                                outputStream.write("  } else {\n");
                                outputStream.write("    return lit_false; \n} \n}\n\n");
                                classHeaderDictionary.get("Obj").CMethodToReturnType.put("Obj_method_EQUALS", "obj_Boolean");
                            }

                            if (i == size) {
                                /* The Obj Class (a singleton) */
                                outputStream.write(" struct  class_Obj_struct  the_class_Obj_struct = {\n");
                                outputStream.write("  new_Obj,     \n");
                                classHeaderDictionary.get("Obj").QuackMethodToCMethod.put("CONSTRUCTOR", "new_Obj");
                                outputStream.write("  Obj_method_PRINT, \n");
                                classHeaderDictionary.get("Obj").QuackMethodToCMethod.put("PRINT", "Obj_method_PRINT");
                                outputStream.write("  Obj_method_STR, \n");
                                classHeaderDictionary.get("Obj").QuackMethodToCMethod.put("STR", "Obj_method_STR");
                                outputStream.write("  Obj_method_EQUALS \n};\n");
                                classHeaderDictionary.get("Obj").QuackMethodToCMethod.put("EQUALS", "Obj_method_EQUALS");

                                outputStream.write("class_Obj class_Obj_Instance = &the_class_Obj_struct;\n\n");
                            }
                            i++;
                        }

                        else if (c.className.equals("String"))
                        {
                            if (i == 0)
                            {
                                outputStream.write("obj_String new_String(  ) {\n");
                                outputStream.write("  obj_String new_thing = (obj_String) malloc(sizeof(struct obj_String_struct));\n");
                                outputStream.write("  new_thing->clazz = class_String_Instance;\n");
                                outputStream.write("  return new_thing; \n}\n\n");
                            }
                            if (m.ident.equals("STR"))
                            {
                                outputStream.write("obj_String String_method_STR(obj_String self) {\n");
                                outputStream.write("  return self;\n}\n\n");
                                classHeaderDictionary.get("String").CMethodToReturnType.put("String_method_STR", "obj_String");
                            }
                            else if (m.ident.equals("PRINT"))
                            {
                                outputStream.write("obj_Nothing String_method_PRINT(obj_String self) {\n");
                                outputStream.write("  fprintf(stdout, \"%s\", self->value);\n");
                                outputStream.write("  return nothing;\n}\n\n");
                                classHeaderDictionary.get("String").CMethodToReturnType.put("String_method_PRINT", "obj_Nothing");
                            }
                            else if (m.ident.equals("EQUALS"))
                            {
                                outputStream.write("obj_Boolean String_method_EQUALS(obj_String self, obj_Obj other) {\n");
                                outputStream.write("  obj_String other_str = (obj_String) other;\n");
                                outputStream.write("  if (other_str->clazz != class_String_Instance) {\n");
                                outputStream.write("    return lit_false;\n");
                                outputStream.write("  }\n");
                                outputStream.write("  if (strcmp(self->value, other_str->value) == 0) {\n");
                                outputStream.write("    return lit_true;\n");
                                outputStream.write("  } else {\n");
                                outputStream.write("    return lit_false;\n");
                                outputStream.write("  }\n}\n\n");
                                classHeaderDictionary.get("String").CMethodToReturnType.put("String_method_EQUALS", "obj_Boolean");
                            }
                            else if (m.ident.equals("PLUS"))
                            {
                                ////??????? needs to get fixed
                                outputStream.write("obj_String String_method_PLUS(obj_String self, obj_String other) {\n");
                                outputStream.write("char* selfString = self->value;\n");
                                outputStream.write("char* otherString = other->value;\n");
                                outputStream.write("char* combinedStrings = malloc(strlen(selfString) + strlen(otherString) + 1);\n");
                                outputStream.write("strcat(combinedStrings, selfString);\n");
                                outputStream.write("strcat(combinedStrings, otherString);\n");
                                outputStream.write("return str_lit(combinedStrings);\n}\n\n");
                                classHeaderDictionary.get("String").CMethodToReturnType.put("String_method_PLUS", "obj_String");
                            }
                            else if (m.ident.equals("ATMOST"))
                            {
                                outputStream.write("obj_Boolean String_method_ATMOST(obj_String self, obj_String other) {\n");
                                outputStream.write("  if (strcmp(self->value, other->value) <= 0) {\n");
                                outputStream.write("    return lit_true;\n");
                                outputStream.write("  } \n");
                                outputStream.write("  else {\n");
                                outputStream.write("    return lit_false;\n}\n}\n\n");
                                classHeaderDictionary.get("String").CMethodToReturnType.put("String_method_ATMOST", "obj_Boolean");
                            }
                            else if (m.ident.equals("LESS"))
                            {
                                outputStream.write("obj_Boolean String_method_LESS(obj_String self, obj_String other) {\n");
                                outputStream.write("  if (strcmp(self->value, other->value) < 0) {\n");
                                outputStream.write("    return lit_true;\n");
                                outputStream.write("  } else {\n");
                                outputStream.write("    return lit_false;\n}\n}\n\n");
                                classHeaderDictionary.get("String").CMethodToReturnType.put("String_method_LESS", "obj_Boolean");
                            }
                            else if (m.ident.equals("ATLEAST"))
                            {
                                outputStream.write("obj_Boolean String_method_ATLEAST(obj_String self, obj_String other) {\n");
                                outputStream.write("   if (strcmp(self->value, other->value) >= 0) {\n");
                                outputStream.write("      return lit_true;\n");
                                outputStream.write("    } else {\n");
                                outputStream.write("      return lit_false;\n}\n}\n\n");
                                classHeaderDictionary.get("String").CMethodToReturnType.put("String_method_ATLEAST", "obj_Boolean");
                            }
                            else if (m.ident.equals("MORE"))
                            {
                                outputStream.write("obj_Boolean String_method_MORE(obj_String self, obj_String other) {\n");
                                outputStream.write("  if (strcmp(self->value, other->value) > 0) {\n");
                                outputStream.write("    return lit_true;\n");
                                outputStream.write("  } else {\n");
                                outputStream.write("    return lit_false;\n}\n}\n\n");
                                classHeaderDictionary.get("String").CMethodToReturnType.put("String_method_MORE", "obj_Boolean");
                            }

                            if (i == size) {
                                outputStream.write("struct  class_String_struct  the_class_String_struct = {\n");
                                outputStream.write("  new_String,   \n");
                                classHeaderDictionary.get("String").QuackMethodToCMethod.put("CONSTRUCTOR", "new_String");
                                outputStream.write("  String_method_PRINT, \n");
                                classHeaderDictionary.get("String").QuackMethodToCMethod.put("PRINT", "String_method_PRINT");
                                outputStream.write("  String_method_STR, \n");
                                classHeaderDictionary.get("String").QuackMethodToCMethod.put("STR", "String_method_STR");
                                outputStream.write("  String_method_EQUALS,\n");
                                classHeaderDictionary.get("String").QuackMethodToCMethod.put("EQUALS", "String_method_EQUALS");
                                outputStream.write("  String_method_PLUS,\n");
                                classHeaderDictionary.get("String").QuackMethodToCMethod.put("PLUS", "String_method_PLUS");
                                outputStream.write("  String_method_ATMOST,\n");
                                classHeaderDictionary.get("String").QuackMethodToCMethod.put("ATMOST", "String_method_ATMOST");
                                outputStream.write("  String_method_LESS,\n");
                                classHeaderDictionary.get("String").QuackMethodToCMethod.put("LESS", "String_method_LESS");
                                outputStream.write("  String_method_ATLEAST,\n");
                                classHeaderDictionary.get("String").QuackMethodToCMethod.put("ATLEAST", "String_method_ATLEAST");
                                outputStream.write("  String_method_MORE\n };\n\n");
                                classHeaderDictionary.get("String").QuackMethodToCMethod.put("MORE", "String_method_MORE");

                                outputStream.write("class_String class_String_Instance = &the_class_String_struct; \n\n");
                                outputStream.write("obj_String str_lit(char *s) {\n");
                                outputStream.write("  char *rep;\n");
                                outputStream.write("  obj_String str = class_String_Instance->constructor(); \n");
                                outputStream.write("  str->value = s;\n");
                                outputStream.write("  return str;\n}\n\n");
                            }
                            // increment method count
                            i++;
                        }

                        else if (c.className.equals("Boolean"))
                        {
                            if (i == 0)
                            {
                                outputStream.write("obj_Boolean new_Boolean(  ) {\n");
                                outputStream.write("  obj_Boolean new_thing = (obj_Boolean)\n");
                                outputStream.write("    malloc(sizeof(struct obj_Boolean_struct));\n");
                                outputStream.write("  new_thing->clazz = class_Boolean_Instance;\n");
                                outputStream.write("  return new_thing; \n}\n\n");
                            }
                            if (m.ident.equals("PRINT"))
                            {
                                outputStream.write("obj_Nothing Boolean_method_PRINT(obj_Boolean self) {\n");
                                outputStream.write("  obj_String str = self->clazz->STR(self);\n");
                                outputStream.write("  fprintf(stdout, \"%s\", str->value);\n");
                                outputStream.write("  return nothing; \n}\n\n");
                                classHeaderDictionary.get("Boolean").CMethodToReturnType.put("Boolean_method_PRINT", "obj_Nothing");
                            }
                            else if (m.ident.equals("STR"))
                            {
                                outputStream.write("obj_String Boolean_method_STR(obj_Boolean self) {\n");
                                outputStream.write("  if (self == lit_true) {\n");
                                outputStream.write("    return str_lit(\"true\");\n");
                                outputStream.write("  } else if (self == lit_false) {\n");
                                outputStream.write("    return str_lit(\"false\");\n");
                                outputStream.write("  } else {\n");
                                outputStream.write("    return str_lit(\"!!!BOGUS BOOLEAN\");\n\n");
                                classHeaderDictionary.get("Boolean").CMethodToReturnType.put("Boolean_method_STR", "obj_String");
                                outputStream.write("  }\n}\n");
                            }
                            else if (m.ident.equals("EQUALS"))
                            {
                                outputStream.write("obj_Boolean Boolean_method_EQUALS(obj_Boolean self, obj_Obj other) {\n");
                                outputStream.write("obj_Boolean other_bool = (obj_Boolean) other;\n");
                                outputStream.write("  if (self->value == other_bool->value) {\n");
                                outputStream.write("    return lit_true;\n");
                                outputStream.write("  } else {\n");
                                outputStream.write("    return lit_false; \n} \n}\n\n");
                                classHeaderDictionary.get("Boolean").CMethodToReturnType.put("Boolean_method_EQUALS", "obj_Boolean");
                            }
                            if (i == size) {
                                outputStream.write("struct  class_Boolean_struct  the_class_Boolean_struct = {\n");
                                outputStream.write("  new_Boolean,     \n");
                                classHeaderDictionary.get("Boolean").QuackMethodToCMethod.put("CONSTRUCTOR", "new_Boolean");
                                outputStream.write("  Boolean_method_PRINT, \n");
                                classHeaderDictionary.get("Boolean").QuackMethodToCMethod.put("PRINT", "Boolean_method_PRINT");
                                outputStream.write("  Boolean_method_STR, \n");
                                classHeaderDictionary.get("Boolean").QuackMethodToCMethod.put("STR", "Boolean_method_STR");
                                outputStream.write("  Boolean_method_EQUALS\n};\n\n");
                                classHeaderDictionary.get("Boolean").QuackMethodToCMethod.put("EQUALS", "Boolean_method_EQUALS");

                                outputStream.write("class_Boolean class_Boolean_Instance = &the_class_Boolean_struct; \n\n");
                                outputStream.write("struct obj_Boolean_struct lit_false_struct =\n");
                                outputStream.write("  { &the_class_Boolean_struct, 0 };\n\n");
                                outputStream.write("obj_Boolean lit_false = &lit_false_struct;\n\n");
                                outputStream.write("struct obj_Boolean_struct lit_true_struct =\n");
                                outputStream.write("  { &the_class_Boolean_struct, 1 };\n\n");
                                outputStream.write("obj_Boolean lit_true = &lit_true_struct;\n\n");
                            }
                            // increment method count
                            i++;
                        }

                        else if (c.className.equals("Nothing"))
                        {
                            if (i == 0)
                            {
                                outputStream.write("obj_Nothing new_Nothing(  ) {\n");
                                outputStream.write("  return nothing; \n}\n\n");
                            }
                            if (m.ident.equals("PRINT"))
                            {
                                outputStream.write("obj_Nothing Nothing_method_PRINT(obj_Nothing self) {\n");
                                outputStream.write("  obj_String str = self->clazz->STR(self);\n");
                                outputStream.write("  fprintf(stdout, \"%s\", str->value);\n");
                                outputStream.write("  return nothing; \n}\n\n");
                                classHeaderDictionary.get("Nothing").CMethodToReturnType.put("Nothing_method_PRINT", "obj_Nothing");
                            }
                            else if (m.ident.equals("STR"))
                            {
                                outputStream.write("obj_String Nothing_method_STR(obj_Nothing self) {\n");
                                outputStream.write("    return str_lit(\"<nothing>\");\n}\n\n");
                                classHeaderDictionary.get("Nothing").CMethodToReturnType.put("Nothing_method_STR", "obj_String");
                            }
                            else if (m.ident.equals("EQUALS"))
                            {
                                outputStream.write("obj_Boolean Nothing_method_EQUALS(obj_Nothing self, obj_Obj other) {\n");
                                outputStream.write("obj_Nothing other_nothing = (obj_Nothing) other;\n");
                                outputStream.write("  if (self == other_nothing) {\n");
                                outputStream.write("    return lit_true;\n");
                                outputStream.write("  } else {\n");
                                outputStream.write("    return lit_false; \n} \n}\n\n");
                                classHeaderDictionary.get("Nothing").CMethodToReturnType.put("Nothing_method_EQUALS", "obj_Boolean");
                            }
                            if (i == size) {
                                outputStream.write("struct  class_Nothing_struct  the_class_Nothing_struct = {\n");
                                outputStream.write("  new_Nothing,     \n");
                                classHeaderDictionary.get("Nothing").QuackMethodToCMethod.put("CONSTRUCTOR", "new_Nothing");
                                outputStream.write("  Nothing_method_PRINT, \n");
                                classHeaderDictionary.get("Nothing").QuackMethodToCMethod.put("PRINT", "Nothing_method_PRINT");
                                outputStream.write("  Nothing_method_STR, \n");
                                classHeaderDictionary.get("Nothing").QuackMethodToCMethod.put("STR", "Nothing_method_STR");
                                outputStream.write("  Nothing_method_EQUALS\n};\n\n");
                                classHeaderDictionary.get("Nothing").QuackMethodToCMethod.put("EQUALS", "Nothing_method_EQUALS");

                                outputStream.write("class_Nothing class_Nothing_Instance = &the_class_Nothing_struct; \n\n");
                                outputStream.write("struct obj_Nothing_struct nothing_struct =\n");
                                outputStream.write("  { &the_class_Nothing_struct };\n\n");
                                outputStream.write("obj_Nothing nothing = &nothing_struct; \n\n");
                            }
                            // increment method count
                            i++;
                        }
                        else if (c.className.equals("Int"))
                        {
                            if (i == 0)
                            {
                                outputStream.write("obj_Int new_Int(  ) {\n");
                                outputStream.write("  obj_Int new_thing = (obj_Int)\n");
                                outputStream.write("    malloc(sizeof(struct obj_Int_struct));\n");
                                outputStream.write("  new_thing->clazz = class_Int_Instance;\n");
                                outputStream.write("  new_thing->value = 0;\n");
                                outputStream.write("  return new_thing; \n}\n");
                            }
                            if (m.ident.equals("PRINT"))
                            {
                                outputStream.write("obj_Nothing Int_method_PRINT(obj_Int self) {\n");
                                outputStream.write("  obj_String str = self->clazz->STR(self);\n");
                                outputStream.write("  fprintf(stdout, \"%s\", str->value);\n");
                                outputStream.write("  return nothing; \n}\n\n");
                                classHeaderDictionary.get("Int").CMethodToReturnType.put("Int_method_PRINT", "obj_Nothing");
                            }
                            else if (m.ident.equals("STR"))
                            {
                                outputStream.write("obj_String Int_method_STR(obj_Int self) {\n");
                                outputStream.write("  char *rep;\n");
                                outputStream.write("  asprintf(&rep, \"%d\", self->value);\n");
                                outputStream.write("  return str_lit(rep); \n}\n\n");
                                classHeaderDictionary.get("Int").CMethodToReturnType.put("Int_method_STR", "obj_String");
                            }
                            else if (m.ident.equals("EQUALS"))
                            {
                                outputStream.write("obj_Boolean Int_method_EQUALS(obj_Int self, obj_Obj other) {\n");
                                outputStream.write("  obj_Int other_int = (obj_Int) other; \n");
                                outputStream.write("  \n");
                                outputStream.write("  if (other_int->clazz != self->clazz) {\n");
                                outputStream.write("    return lit_false;\n");
                                outputStream.write("  }\n");
                                outputStream.write("  if (self->value != other_int->value) {\n");
                                outputStream.write("    return lit_false;\n");
                                outputStream.write("  }\n");
                                outputStream.write("  return lit_true;\n}\n\n");
                                classHeaderDictionary.get("Int").CMethodToReturnType.put("Int_method_EQUALS", "obj_Boolean");
                            }
                            else if (m.ident.equals("PLUS"))
                            {
                                outputStream.write("obj_Int Int_method_PLUS(obj_Int self, obj_Int other) {\n");
                                outputStream.write("  return int_lit(self->value + other->value);\n}\n\n");
                                classHeaderDictionary.get("Int").CMethodToReturnType.put("Int_method_PLUS", "obj_Int");
                            }
                            else if (m.ident.equals("MINUS"))
                            {
                                outputStream.write("obj_Int Int_method_MINUS(obj_Int self, obj_Int other) {\n");
                                outputStream.write("  return int_lit(self->value - other->value);\n}\n\n");
                                classHeaderDictionary.get("Int").CMethodToReturnType.put("Int_method_MINUS", "obj_Int");
                            }
                            else if (m.ident.equals("TIMES"))
                            {
                                outputStream.write("obj_Int Int_method_TIMES(obj_Int self, obj_Int other) {\n");
                                outputStream.write("  return int_lit(self->value * other->value);\n}\n\n");
                                classHeaderDictionary.get("Int").CMethodToReturnType.put("Int_method_TIMES", "obj_Int");
                            }
                            else if (m.ident.equals("DIVIDE"))
                            {
                                outputStream.write("obj_Int Int_method_DIVIDE(obj_Int self, obj_Int other) {\n");
                                outputStream.write("  return int_lit(self->value / other->value);\n}\n\n");
                                classHeaderDictionary.get("Int").CMethodToReturnType.put("Int_method_DIVIDE", "obj_Int");
                            }
                            else if (m.ident.equals("ATMOST"))
                            {
                                outputStream.write("obj_Boolean Int_method_ATMOST(obj_Int self, obj_Int other) {\n");
                                outputStream.write("  if (self->value <= other->value) {\n");
                                outputStream.write("    return lit_true;\n}\n");
                                outputStream.write("  return lit_false;\n}\n\n");
                                classHeaderDictionary.get("Int").CMethodToReturnType.put("Int_method_ATMOST", "obj_Boolean");
                            }
                            else if (m.ident.equals("LESS"))
                            {
                                outputStream.write("obj_Boolean Int_method_LESS(obj_Int self, obj_Int other) {\n");
                                outputStream.write("  if (self->value < other->value) {\n");
                                outputStream.write("    return lit_true;\n}\n");
                                outputStream.write("  return lit_false;\n}\n\n");
                                classHeaderDictionary.get("Int").CMethodToReturnType.put("Int_method_LESS", "obj_Boolean");
                            }
                            else if (m.ident.equals("ATLEAST"))
                            {
                                outputStream.write("obj_Boolean Int_method_ATLEAST(obj_Int self, obj_Int other) {\n");
                                outputStream.write("  if (self->value >= other->value) {\n");
                                outputStream.write("    return lit_true;\n}\n");
                                outputStream.write("  return lit_false;\n}\n\n");
                                classHeaderDictionary.get("Int").CMethodToReturnType.put("Int_method_ATLEAST", "obj_Boolean");
                            }
                            else if (m.ident.equals("MORE"))
                            {
                                outputStream.write("obj_Boolean Int_method_MORE(obj_Int self, obj_Int other) {\n");
                                outputStream.write("  if (self->value > other->value) {\n");
                                outputStream.write("    return lit_true;\n}\n");
                                outputStream.write("  return lit_false;\n}\n\n");
                                classHeaderDictionary.get("Int").CMethodToReturnType.put("Int_method_MORE", "obj_Boolean");
                            }
                            else if (m.ident.equals("NEG"))
                            {
                                outputStream.write("obj_Int Int_method_NEG(obj_Int self) {\n");
                                outputStream.write("  return int_lit(- self->value);\n}\n\n");
                                classHeaderDictionary.get("Int").CMethodToReturnType.put("Int_method_NEG", "obj_Int");
                            }
                            if (i == size) {
                                outputStream.write("struct  class_Int_struct  the_class_Int_struct = {\n");
                                outputStream.write("  new_Int,     /* Constructor */\n");
                                classHeaderDictionary.get("Int").QuackMethodToCMethod.put("CONSTRUCTOR", "new_Int");
                                outputStream.write("  Int_method_PRINT, \n");
                                classHeaderDictionary.get("Int").QuackMethodToCMethod.put("PRINT", "Int_method_PRINT");
                                outputStream.write("  Int_method_STR, \n");
                                classHeaderDictionary.get("Int").QuackMethodToCMethod.put("STR", "Int_method_STR");
                                outputStream.write("  Int_method_EQUALS,\n");
                                classHeaderDictionary.get("Int").QuackMethodToCMethod.put("EQUALS", "Int_method_EQUALS");
                                outputStream.write("  Int_method_PLUS,\n");
                                classHeaderDictionary.get("Int").QuackMethodToCMethod.put("PLUS", "Int_method_PLUS");
                                outputStream.write("  Int_method_TIMES,\n");
                                classHeaderDictionary.get("Int").QuackMethodToCMethod.put("TIMES", "Int_method_TIMES");
                                outputStream.write("  Int_method_MINUS,\n");
                                classHeaderDictionary.get("Int").QuackMethodToCMethod.put("MINUS", "Int_method_MINUS");
                                outputStream.write("  Int_method_DIVIDE,\n");
                                classHeaderDictionary.get("Int").QuackMethodToCMethod.put("DIVIDE", "Int_method_DIVIDE");
                                outputStream.write("  Int_method_ATMOST,\n");
                                classHeaderDictionary.get("Int").QuackMethodToCMethod.put("ATMOST", "Int_method_ATMOST");
                                outputStream.write("  Int_method_LESS,\n");
                                classHeaderDictionary.get("Int").QuackMethodToCMethod.put("LESS", "Int_method_LESS");
                                outputStream.write("  Int_method_ATLEAST,\n");
                                classHeaderDictionary.get("Int").QuackMethodToCMethod.put("ATLEAST", "Int_method_ATLEAST");
                                outputStream.write("  Int_method_MORE,\n");
                                classHeaderDictionary.get("Int").QuackMethodToCMethod.put("MORE", "Int_method_MORE");
                                outputStream.write("  Int_method_NEG\n};\n\n");
                                classHeaderDictionary.get("Int").QuackMethodToCMethod.put("NEG", "Int_method_NEG");

                                outputStream.write("class_Int class_Int_Instance = &the_class_Int_struct; \n\n");
                                outputStream.write("obj_Int int_lit(int n) {\n");
                                outputStream.write("  obj_Int boxed = new_Int();\n");
                                outputStream.write("  boxed->value = n;\n");
                                outputStream.write("  return boxed;\n}\n\n");
                            }
                            // increment method count
                            i++;
                        }
                    }
                }
                // START DUMMY STATEMENTS "CLASS"
                else if (c.className.equals("$statementsDummyClass"))
                {
                    TypeChecker.currentClass = "$statementsDummyClass";
                    nodeIndex = 0;
                    String mainDecl = "void quackmain() {\n";
                    outputStream.write(mainDecl);
                    Class_Block.Clazz_Block theClassBlock = GetClassBlock(c.className);

                    GenTreeAndRegisterTables genTreeAndRegisterTables = new GenTreeAndRegisterTables();
                    for (Statement s : theClassBlock._stmtList)
                    {
                        try
                        {
                            genTreeAndRegisterTables = s.CreateGenTree(genTreeAndRegisterTables.theRegisterTable);
                            WriteCFromGenTree(genTreeAndRegisterTables.genTreeNode, outputStream);
                        }
                        catch (Exception e)
                        {
                            System.out.println(e.getMessage());
                        }

                    }
                    String endMain = "}\n";
                    outputStream.write(endMain);
                }

                // START CUSTOM CLASS
                else
                {
                    TypeChecker.currentClass = c.className;
                    nodeIndex = 0;
                    // do regular code generation for all other classes here
                    for (MethodNode m : GetCompleteMethodTable(c.className)) {
                        TypeChecker.currentMethod = m.ident;
                        if (i == 0) {
                            //Create struct for class
                        	Class_Block.Clazz_Block theClassBlock = GetClassBlock(c.className);
                        	String args = "";
                        	for(Args.Arg a : theClassBlock._argList._args)
                        	{
                        		args += "obj_"+a._type+" "+a._ident+" ,";
                        	}
                        	if(!(args.equals("")))
                            {
                                args = args.substring(0,args.length() - 1);
                            }
                            outputStream.write("obj_" + c.className + " new_" + c.className + "(" + args + ") {\n");
                            classHeaderDictionary.get(c.className).QuackMethodToCMethod.put("CONSTRUCTOR", "new_"+c.className);
                            outputStream.write("  obj_" + c.className + " new_thing = (obj_" + c.className + ") malloc(sizeof(struct obj_" + c.className + "_struct));\n");
                            outputStream.write("  new_thing->clazz = class_" + c.className + "_Instance;\n");
                            
                            for (Statement s: theClassBlock._stmtList)
                            {
                            	if(s.StatementType().equals("ASSIGNMENT"))
                            		if(s.getLexpr().getIdent().contains("this."))
                            		{
                            			String str= s.getLexpr().getIdent().replace("this.","new_thing->");
                            			outputStream.write("  " + str + " = " + s.getRexpr() + ";\n");
                            		}
                            		
                            }
                            outputStream.write("  return new_thing; \n}\n\n");

                            String parent=typeChecker.tree.findNode(typeChecker.tree.getRoot(),c.className).getParent().getId();
                            
                            for(Entry<String, String> Super : classHeaderDictionary.get(parent).QuackMethodToCMethod.entrySet()) {
                            	if(c.MethodIdentExists(Super.getKey()))
                            	{
                            		classHeaderDictionary.get(c.className).QuackMethodToCMethod.put(Super.getKey(), c.className +"_method_"+Super.getKey());
                            	}
                            	else
                                {
                            		if(!(Super.getKey().equals("CONSTRUCTOR")))
                            		classHeaderDictionary.get(c.className).QuackMethodToCMethod.put(Super.getKey(), Super.getValue());
                            	}
                            }
                            CHeaderNode x = classHeaderDictionary.get(parent);
                            for (MethodNode meth : GetCompleteMethodTable(c.className))
                            {
                            	if(!x.QuackMethodToCMethod.containsKey(meth.ident))
                            	{
                            	    classHeaderDictionary.get(c.className).QuackMethodToCMethod.put(meth.ident, c.className+"_method_"+meth.ident);
                            	}
                            }
                        }
                        //create method object for each method
                        List<Statement> statements = null;
                        GenTreeAndRegisterTables genTreeAndRegisterTables = new GenTreeAndRegisterTables();
                        if(c.MethodIdentExists(m.ident))
                        {
                            LinkedList<Var> args = VarTableSingleton.getTableByClassName(c.className).GetMethodArgs(m.ident);
                            LinkedList<Args.Arg> methArgs = null;
                            Class_Block.Clazz_Block theClassBlock = GetClassBlock(c.className);
                            for(Methods.Method x:theClassBlock._methods)
                            {
                                if(x._methodIdent.equals(m.ident))
                                {
                                    methArgs= x._formalArgs._args;
                                    statements = x._statements;
                                }
                            }
                            if(args.isEmpty())
                            {
                                outputStream.write("obj_" + m.returnType + " " + classHeaderDictionary.get(c.className).QuackMethodToCMethod.get(m.ident) + "(obj_" + c.className + " self) {\n");
                            }
                            else
                            {
                                String methArg = "obj_" + c.className+" self";
                                for(Args.Arg a : methArgs)
                                {
//                                    Var tempVar = new Var("temp_" + nodeIndex, Main.classHeaderDictionary.get(a._type).objectInstanceName);
//                                    nodeIndex++;
//                                    genTreeAndRegisterTables.theRegisterTable.put(a._ident, tempVar);
                                    methArg += ", obj_" + a._type + " " + a._ident;
                                }
                                outputStream.write("obj_" + m.returnType + " " + classHeaderDictionary.get(c.className).QuackMethodToCMethod.get(m.ident) + "(" + methArg + ") {\n");
                            }
                        }

                        if(!(statements==null))
                        {
                        	for(Statement st: statements)
                        	{
                        		try
                                {
                                    genTreeAndRegisterTables = st.CreateGenTree(genTreeAndRegisterTables.theRegisterTable);
                                    if(!(genTreeAndRegisterTables==null))
                                    WriteCFromGenTree(genTreeAndRegisterTables.genTreeNode, outputStream);
                                }
                                catch (Exception e)
                                {
                                    System.out.println("Failed in class:" + TypeChecker.currentClass + " on statement: " + st.toString() + ". with error: " + e.getMessage());
                                }
                        	}
                            outputStream.write("\n}\n");
                            classHeaderDictionary.get(c.className).CMethodToReturnType.put(classHeaderDictionary.get(c.className).QuackMethodToCMethod.get(m.ident), "obj_" + m.returnType);
                        }

                        if (i == size)
                        {
                            outputStream.write("struct  class_" + c.className + "_struct  the_class_" + c.className + "_struct = {\n");
                            classHeaderDictionary.get(c.className).QuackMethodToCMethod.put("CONSTRUCTOR", "new_" + c.className);
                            if(size>1)
                            {

								for(Entry<String, String> e : classHeaderDictionary.get(c.className).QuackMethodToCMethod.entrySet())
								{
                                	outputStream.write(e.getValue()+",\n");
                                }
                            }
                            outputStream.write("};\n");
                            outputStream.write("class_"+c.className+" class_"+c.className+"_Instance = &the_class_"+c.className+"_struct; \n");
                        }
                        // increment method count
                        i++;
                    }
                }
            }
            outputStream.flush();  
        } catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        
    }

    public static String GetCMethodReturnTypeWithParents(String className, String methodName)
    {
        String cType = null;
        String currentClass = className;
        while (cType == null)
        {
            cType = Main.classHeaderDictionary.get(currentClass).CMethodToReturnType.get(methodName);
            if (currentClass.equals("Obj"))
                break;
            currentClass = ClassesTable.getInstance().getParentClass(className);
        }
        return cType;
    }

    static void WriteCFromGenTree(GenTreeNode root, FileWriter outputStream)
    {
        for (GenTreeNode node : root.children)
        {
            WriteCFromGenTree(node, outputStream);
        }
        try
        {
            root.completeCOutput = root.completeCOutput.replace(" this", " self");
            outputStream.write(root.completeCOutput);
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage() + ": " + root.registerName);
        }
    }

    private Class_Block.Clazz_Block GetClassBlock(String className)
    {
        for (Class_Block.Clazz_Block cb : ast._cbs)
        {
            if (cb._classIdent.equals(className))
                return cb;
        }
        return null;
    }

    // this method returns methods of provided class, including ones inherited from its parents
    LinkedList<MethodNode> GetCompleteMethodTable(String className)
    {
        LinkedList<MethodNode> completeMethods = new LinkedList<>();
        String currentClassName = className;

        while(true)
        {
            LinkedList<Var> classMethods = VarTableSingleton.getTableByClassName(currentClassName).methodTable;
            for (Var v : classMethods)
            {
                boolean exists = false;
                for (MethodNode m : completeMethods)
                {
                    if (m.ident.equals(v.ident))
                        exists = true;
                }
                if (!exists)
                {
                    MethodNode methodNode = new MethodNode(v.ident, v.type);
                    methodNode.classIdent = currentClassName;
                    completeMethods.add(methodNode);
                }
            }
            if (currentClassName.equals("Obj"))
                break;
            currentClassName = ClassesTable.getInstance().getParentClass(currentClassName);
        }
        return completeMethods;
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
            //ast of built in classes
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
