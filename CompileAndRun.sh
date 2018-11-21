#! /bin/sh 
#
HERE=$(dirname "$0")
CUP=${HERE}/lib/java-cup-11b-runtime.jar
JFLEX=${HERE}/lib/jflex-full-1.7.0.jar
CLI=${HERE}/lib/commons-cli-1.4.jar

CLASSPATH=.:${CUP}:${CLI}:${HERE}:${JFLEX}

make clean
make
java -cp $CLASSPATH Main $*
