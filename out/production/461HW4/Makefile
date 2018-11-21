#
# Makefile for Quack compiler components 
#

LIB = ./lib
CUP = java -cp ${LIB}/java-cup-11b.jar java_cup.Main 
CUPLIB = ${LIB}/java-cup-11b-runtime.jar
JFLEX = ${LIB}/jflex-full-1.7.0/bin/jflex
CLI = ${LIB}/commons-cli-1.4.jar

## Eventually we'll use all of these 
LIBS = $(CUPLIB):$(CLI)

# JAVACOPT =  -Xlint:unchecked 
JAVACOPT =  

all:	Main.class 

Main.class:	Main.java Lexer.class parser.class
	javac -classpath .:$(LIBS) $< 

%.class:	%.java
	javac -classpath .:$(LIBS) $< 

sym.java parser.java: 
	$(CUP) Quack.cup

Lexer.java:	Quack.jflex  sym.java
	$(JFLEX) Quack.jflex

parser: parser.class Quack.class

#=================

clean: ; rm *.class parser.java sym.java Lexer.java

dust: ; rm *.class


