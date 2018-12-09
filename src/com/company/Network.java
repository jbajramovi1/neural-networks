package com.company;


import java.util.Random;

public class Network {

    private static final int epochs= 30;
    private static final int batchsize = 10;
    private static final double learningRate = 0.05;
    private static final double regularizationLambda = 0;
    private static final double  momentum = 0;

    public double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

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
        for (int i = 0; i < num_of_layers; i++) {
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
        for (int i = 0; i < num_of_layers -1;i++ ){
            Matrix z = weights[i].multiply(a);
            z = z.add(biases[i]);

            for (int j = 0; j < z.getNum_rows(); j++)
                for (int k = 0; k < z.getNum_cols(); k++) {
                    a.setValue(j, k, sigmoid(z.getValue(j, k)));
                }
        }

        return a;
    }



    public int getNum_of_layers() {
        return num_of_layers;
    }

    public int[] getSizes() {
        return sizes;
    }
}
