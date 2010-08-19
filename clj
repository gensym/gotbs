#!/bin/bash

LIBS=`printf "%s:" lib/*.jar | sed s/:$//`
if [ $# -eq 0 ]; then
   java -cp $LIBS:src:resources clojure.main
else
    java -cp $LIBS:src:resources clojure.main $1 - $@
fi	