#! /bin/sh 
#
var=`gcc -Wno-incompatible-pointer-types $*.c -o $*`
echo $var

./$*