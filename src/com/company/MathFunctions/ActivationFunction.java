package com.company.MathFunctions;

public abstract class ActivationFunction extends Function_Pom{
    public static final ActivationFunction SIGMOID = new ActivationFunction() {
        @Override
        public double f(double x) {
            return 1.0 / (1.0 + Math.exp(-x));
        }

        @Override
        public Function_Pom derivative() {
            Function_Pom self = this;
            return new Function_Pom() {
                @Override
                public double f(double x) {
                    double sigmoid = self.f(x);
                    return sigmoid*(1 - sigmoid);
                }
            };
        }
    };
}
