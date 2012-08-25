#!/bin/sh

wget "http://repo1.maven.org/maven2/org/apache/ant/ant-antunit/1.2/ant-antunit-1.2.jar"
bnd wrap -output org.apache.ant.antunit-1.2.jar -properties ant-antunit-1.2.bnd  ant-antunit-1.2.jar
