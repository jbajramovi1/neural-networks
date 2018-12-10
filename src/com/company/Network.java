package com.company;


import com.company.MathFunctions.ActivationFunction;
import com.company.MathFunctions.CostFunction;

import java.util.Random;

public class Network {

    private static final int epochs= 1;
    private static final int batchsize = 10;
    private static final double learningRate = 0.05;
    private static final double regularizationLambda = 5.0;
    private static final double  momentum = 0.3;
    private static final CostFunction costFunction = CostFunction.CROSS_ENTROPY;
    private static final ActivationFunction activationFunction = ActivationFunction.SIGMOID;

    //public double sigmoid(double x) {
        //return 1.0 / (1.0 + Math.exp(-x));
    //}

    private int num_of_layers;
    private int[] sizes;
    private Matrix[] biases;
    private Matrix[] weights;
    private Matrix[] velocities;

    public Network(int[] sizes){
        this.num_of_layers = sizes.length;
        this.sizes = sizes;
        this.biases = new Matrix[num_of_layers - 1];
        this.weights = new Matrix[num_of_layers - 1];
        this.velocities = new Matrix[num_of_layers - 1]; //momentum based

        Random r = new Random();
        for (int i = 0; i < num_of_layers - 1; i++) {
            biases[i] = new Matrix(sizes[i+1], 1);
            weights[i] = new Matrix(sizes[i+1], sizes[i]);
            velocities[i] = new Matrix(sizes[i+1], sizes[i]);

            for (int j = 0; j < sizes[i+1]; j++) {
                biases[i].setValue(j, 0, r.nextGaussian());

                for (int k = 0; k < sizes[i]; k++) {
                    weights[i].setValue(j, k, r.nextGaussian()/Math.sqrt(sizes[i]));
                }
            }
        }
    }

    public Matrix evaluate (Matrix a) {
        for (int i = 0; i < num_of_layers - 1; i++ ){
            Matrix z = weights[i].multiply(a);
            z.addSelf(biases[i]);

            a = z.vectorize(activationFunction);
        }

        return a;
    }

    public void roundUpMax(Matrix m) {
        double max = Double.NEGATIVE_INFINITY;
        int[] maxIndex = new int[] {0, 0};
        for (int i = 0; i < m.getNum_rows(); i++) {
            for (int j = 0; j < m.getNum_cols(); j++) {
                if (m.getValue(i, j) > max) {
                    max = m.getValue(i, j);
                    m.setValue(maxIndex[0], maxIndex[1], 0);
                    m.setValue(i, j, 1);
                    maxIndex = new int[] {i, j};
                }
                else {
                    m.setValue(i, j, 0);
                }
            }
        }
    }


    public void train(Matrix[] trainingData, Matrix[] trainingResults, Matrix[] testData, Matrix[] testResults) {

        for (int i = 0; i < epochs; i++) { // i the epoch

            //long startTime = System.nanoTime();

            int length = trainingData.length;

            //Shuffler.shuffle(trainingData, trainingResults);

            Matrix[] gradientWeights = new Matrix[num_of_layers - 1];
            Matrix[] gradientBiases = new Matrix[num_of_layers - 1];

            // Initialize the gradients matrices
            for (int j = 0; j < gradientWeights.length; j++) {
                gradientWeights[j] = new Matrix(weights[j].getNum_rows(), weights[j].getNum_cols());
                gradientBiases[j] = new Matrix(biases[j].getNum_rows(), biases[j].getNum_cols());
            }

            for (int j = 0; j < trainingData.length; j++) { // j is the current training point

                Matrix[][] deltaGradients = backpropagate(trainingData[j], trainingResults[j]);

                // Update the gradients for this batch
                for (int k = 0; k < num_of_layers - 1; k++) {
                    gradientWeights[k].addSelf(deltaGradients[0][k]);
                    gradientBiases[k].addSelf(deltaGradients[1][k]);
                }

                // Check if the batch is done
                if (j > 0 && j % batchsize == 0 || j == trainingData.length - 1) {

                    for (int k = 0; k < num_of_layers - 1; k++) {

                        gradientWeights[k].multiplySelf(learningRate);
                        velocities[k].multiplySelf(momentum);
                        velocities[k].subtractSelf(gradientWeights[k]);
                        weights[k].multiplySelf(1 - learningRate * regularizationLambda / length); // L2 regularization
                        weights[k].addSelf(velocities[k]);

                        gradientBiases[k].multiplySelf(learningRate);
                        biases[k].subtractSelf(gradientBiases[k]);

                        // Reset the gradient matrices
                        gradientWeights[k].setAll(0);
                        gradientBiases[k].setAll(0);
                    }
                }
            }

            int correct_num = 0;
            for (int j = 0; j < testData.length; j++) {
                Matrix result = evaluate(testData[j]);
                roundUpMax(result);
                if (result.equals(testResults[j]))
                    correct_num++;
            }
            System.out.println("Accuracy: " + (correct_num * 100.0 / testData.length) + "%");
        }

    }

    public Matrix[][] backpropagate(Matrix trainingData, Matrix trainingResult) { // returns Matrix[][0] -> weights, [1] is biases

        Matrix[] deltaGradientWeights = new Matrix[num_of_layers - 1];
        Matrix[] deltaGradientBiases = new Matrix[num_of_layers - 1];
        Matrix[] zs = new Matrix[num_of_layers - 1];
        Matrix[] activations = new Matrix[num_of_layers];
        Matrix previousActivation = trainingData;

        activations[0] = previousActivation;

        // Feed forward layer by layer and store all the z's
        for (int i = 0; i < num_of_layers - 1; i++) {
            Matrix z = weights[i].multiply(previousActivation);
            z.addSelf(biases[i]);
            zs[i] = z;
            previousActivation = z.vectorize(activationFunction);
            activations[i + 1] = previousActivation;
        }

        // Move backwards to calculate the errors
        // Error in the final layer (Output error):
        Matrix error = costFunction.derivative(activations[activations.length - 1], trainingResult, zs[zs.length - 1], activationFunction);
        deltaGradientBiases[deltaGradientBiases.length - 1] = error;
        deltaGradientWeights[deltaGradientWeights.length - 1] = error.multiplyTransposeM(activations[activations.length - 2]);

        for (int i = num_of_layers - 3; i >= 0; i--) {
            Matrix zsp = zs[i].vectorize(activationFunction.derivative());
            error = weights[i + 1].multiplyTransposeSelf(error); // Equation BP2, continued on following line
            error.hadamardProductSelf(zsp);

            deltaGradientBiases[i] = error; // Equation BP3
            deltaGradientWeights[i] = error.multiplyTransposeM(activations[i]); // Equation BP4
        }

        return new Matrix[][] {deltaGradientWeights, deltaGradientBiases};
    }

    public int getNum_of_layers() {
        return num_of_layers;
    }

    public int[] getSizes() {
        return sizes;
    }
}