#!/bin/bash

corretor=$PWD/testes/corretor-automatico.jar
executavel=$PWD/trabalho1/alguma-lexico/target/alguma-lexico-1.0-SNAPSHOT-jar-with-dependencies.jar
pastaTemp=$PWD/temp
casosTeste=$PWD/testes/casos-de-teste
ras="791067, 790004, 790034"

if [ ! -d $pastaTemp ]
then
    mkdir $pastaTemp
fi

java -jar $corretor "java -jar $executavel" gcc $pastaTemp $casosTeste "$ras" "$1"