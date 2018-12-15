package com.company;


import com.company.MathFunctions.ActivationFunction;

import java.io.FileWriter;
import java.util.Random;

public class Network {
    private static final double learningRate = 0.05;
    private static final double regularizationLambda = 5.0;
    private static final double  momentum = 0.3;
    private static final ActivationFunction activationFunction = ActivationFunction.SIGMOID;

    private int num_of_layers;
    private Matrix[] biases;
    private Matrix[] weights;
    private Matrix[] velocities;

    public Network(int[] sizes){
        this.num_of_layers = sizes.length;
        this.biases = new Matrix[num_of_layers - 1];
        this.weights = new Matrix[num_of_layers - 1];
        this.velocities = new Matrix[num_of_layers - 1];

        Random r = new Random();

        for (int i = 0; i < num_of_layers - 1; i++) {
            biases[i] = new Matrix(sizes[i+1], 1);
            weights[i] = new Matrix(sizes[i+1], sizes[i]);
            velocities[i] = new Matrix(sizes[i+1], sizes[i]);

            for (int j = 1; j < sizes[i+1]; j++) {
                biases[i].setValue(j, 0, r.nextGaussian());

                for (int k = 1; k < sizes[i]; k++)
                    weights[i].setValue(j, k, r.nextGaussian()/Math.sqrt(sizes[i]));
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

    public int evaluate_epoch(Matrix[] dataset, Matrix[] expected_results) {
        int correct_num = 0;

        for (int j = 0; j < dataset.length; j++) {
            Matrix result = evaluate(dataset[j]);
            getPrediction(result);

            if (result.equals(expected_results[j]))
                correct_num++;
        }

        if ((correct_num * 1.0 / dataset.length)>0.96){ ;
            return  1;
        }
        else {
            return 0;
        }
    }

    public void evaluate_dataset(Matrix[] dataset, Matrix[] expected_results,String fileName){
        int correct_num = 0;

        try {
            FileWriter fileWriter = null;
            fileWriter = new FileWriter((fileName));

            for (int j = 0; j < dataset.length; j++) {
                Matrix result = evaluate(dataset[j]);
                getPrediction(result);
                int result_int = convert_to_int(result);

                fileWriter.append(String.valueOf(result_int));
                fileWriter.append("\n");

                if (result.equals(expected_results[j]))
                    correct_num++;
            }
        }
        catch (Exception e){
            System.out.println("CSV error");
        }

        System.out.println("Accuracy (" + fileName + "): " + (correct_num * 100.0 / dataset.length) + "%");
    }

    public int convert_to_int(Matrix input) {
        double[] input_array = input.getData();

        for (int i = 0; i < input_array.length; i++)
            if (input_array[i] == 1)
                return i;

        return -1;
    }

    public void getPrediction(Matrix m) {
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
        for (int i = 0; i < 10; i++) {
            int length = trainingData.length;

            Matrix[] gradientWeights = new Matrix[num_of_layers - 1];
            Matrix[] gradientBiases = new Matrix[num_of_layers - 1];

            for (int j = 0; j < gradientWeights.length; j++) {
                gradientWeights[j] = new Matrix(weights[j].getNum_rows(), weights[j].getNum_cols());
                gradientBiases[j] = new Matrix(biases[j].getNum_rows(), biases[j].getNum_cols());
            }

            for (int j = 0; j < trainingData.length; j++) {
                Matrix[][] deltaGradients = backpropagation(trainingData[j], trainingResults[j]);

                for (int k = 0; k < num_of_layers - 1; k++) {
                    gradientWeights[k].addSelf(deltaGradients[0][k]);
                    gradientBiases[k].addSelf(deltaGradients[1][k]);
                }

                if (j > 0 && j % 5.0 == 0 || j == trainingData.length - 1) {
                    for (int k = 0; k < num_of_layers - 1; k++) {
                        gradientWeights[k].multiplySelf(learningRate);
                        velocities[k].multiplySelf(momentum);
                        velocities[k].subtractSelf(gradientWeights[k]);
                        weights[k].multiplySelf(1 - learningRate * regularizationLambda / length);
                        weights[k].addSelf(velocities[k]);

                        gradientBiases[k].multiplySelf(learningRate);
                        biases[k].subtractSelf(gradientBiases[k]);

                        gradientWeights[k].setAll(0);
                        gradientBiases[k].setAll(0);
                    }
                }
            }

            if (evaluate_epoch(testData,testResults) == 1)
                break;
        }

        evaluate_dataset(trainingData,trainingResults, "trainPredictions.csv");
        evaluate_dataset(testData,testResults,"actualTestPredictions.csv");
    }

    public Matrix[][] backpropagation(Matrix trainingData, Matrix trainingResult) {
        Matrix[] deltaGradientWeights = new Matrix[num_of_layers - 1];
        Matrix[] deltaGradientBiases = new Matrix[num_of_layers - 1];
        Matrix[] zs = new Matrix[num_of_layers - 1];
        Matrix[] activations = new Matrix[num_of_layers];
        Matrix previousActivation = trainingData;

        activations[0] = previousActivation;

        for (int i = 0; i < num_of_layers - 1; i++) {
            Matrix z = weights[i].multiply(previousActivation);
            z.addSelf(biases[i]);
            zs[i] = z;
            previousActivation = z.vectorize(activationFunction);
            activations[i + 1] = previousActivation;
        }

        Matrix error = activations[activations.length - 1].subtract(trainingResult);
        deltaGradientBiases[deltaGradientBiases.length - 1] = error;
        deltaGradientWeights[deltaGradientWeights.length - 1] = error.multiplyTransposeM(activations[activations.length - 2]);

        for (int i = num_of_layers - 3; i >= 0; i--) {
            Matrix zsp = zs[i].vectorize(activationFunction.derivative());
            error = weights[i + 1].multiplyTransposeSelf(error);
            error.hadamardProductSelf(zsp);

            deltaGradientBiases[i] = error;
            deltaGradientWeights[i] = error.multiplyTransposeM(activations[i]);
        }

        return new Matrix[][] {deltaGradientWeights, deltaGradientBiases};
    }
}