package com.company.MathFunctions;

public abstract class Function_Pom {
    public static final double H = 1e-5;

    public abstract double f(double x);

    public Function_Pom derivative() {
        return new Function_Pom() {
            @Override
            public double f(double x) {
                return (f(x + H) - f(x - H)) / (2 * H);
            }
        };
    }
}
