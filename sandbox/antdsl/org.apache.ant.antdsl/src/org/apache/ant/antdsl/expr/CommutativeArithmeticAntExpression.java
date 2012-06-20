package org.apache.ant.antdsl.expr;

public abstract class CommutativeArithmeticAntExpression extends CommutativeAntExpression {

    public CommutativeArithmeticAntExpression(String name) {
        super(name);
    }

    @Override
    protected Object eval(String v1, String v2) {
        throw buildIncompatibleTypeException("String", "String");
    }

    @Override
    protected Object eval(String v1, int v2) {
        throw buildIncompatibleTypeException("String", "int");
    }

    @Override
    protected Object eval(String v1, long v2) {
        throw buildIncompatibleTypeException("String", "long");
    }

    @Override
    protected Object eval(String v1, float v2) {
        throw buildIncompatibleTypeException("String", "float");
    }

    @Override
    protected Object eval(String v1, double v2) {
        throw buildIncompatibleTypeException("String", "double");
    }

}
