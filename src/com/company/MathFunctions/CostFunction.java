package com.company.MathFunctions;

import com.company.Matrix;

/* TODO: Matrix vectorize missing */

public abstract class CostFunction {
    public static final CostFunction CROSS_ENTROPY = new CostFunction() {
        @Override
        public Matrix derivative(Matrix calculatedResult, Matrix expectedResult, Matrix outputZ, ActivationFunction activationFunction) {
            return calculatedResult.subtract(expectedResult);
        }
    };

    public abstract Matrix derivative(Matrix calculatedResult, Matrix expectedResult, Matrix outputZ, ActivationFunction activationFunction);
}
