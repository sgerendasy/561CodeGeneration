# 541CodeGeneration

Lexer, parser,AST and TypeChecker, and Codegeneration built by Sam Gerendasy & Cristian Ramirez. 
Starter code created by Michal Young.


# To Build
./CompileAndRun.sh [input filename]

'make' just build

'make clean' removes sym table, lexer, parser, and all .class files.

'make dust' removes all .class files only.


# To Run compiler
./run.sh [input fileName]


#To compile & execute C code
Each Quack input file will create one header and one .c file with the same file name.
For example, running './run.sh SampleTest.qk' will create 'SampleTest.c' & 'SampleTest.h'.

To execute the generated C code:
./runC.sh [name of test] 
this script will complie the c code and run it

#What we can compile correctly:
Aything can be typechecked and most quack programs can be code generated with the exceptions of some cases that fail to implement dynamic dispatch. 

-Methods get inherrited correctly
-Methods get overriden correctly 


#What we don't compile correctly:
-We did not codegenerate typecase.
-Ask we explained to you our intital desing for code genration did not allow us to fully implement dynamic dispatch. Sucha as an inherited method calling an overriding method 
-We did not implement goto for if and while. we just write if or while statements in c
-While loops don't work complety right. 

#Our testcases don't do anything interesting when run but the code is generated and executed correctly 
-DyDispatchExecInherMethod.qk
-DyDispatchExecOverrMethod.qk
-Arith.qk
-LogicAndExpressions.qk 

Note: There might be a bug with cup and the empty case. If this error occurs:
java.lang.NoSuchMethodError: java_cup.runtime.SymbolFactory

it can be fixed by editing the generated parser.java code. At the end of the file you will find case 71. Remove the text ", RESULT"
case 71: // empty ::= 
            {
              Empty RESULT =null;
		 
              CUP$parser$result = parser.getSymbolFactory().newSymbol("empty",20, ((java_cup.runtime.Symbol)CUP$parser$stack.peek()), RESULT);
            }