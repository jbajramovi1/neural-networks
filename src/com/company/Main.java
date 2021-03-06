package com.company;

public class Main {

    public static void main(String[] args) {

        Matrix[] trainDataSet = DataLoader.loadImageSet("src/com/company/MNIST_DATA/mnist_train_vectors.csv");
        Matrix[] trainLabels = DataLoader.loadLabels("src/com/company/MNIST_DATA/mnist_train_labels.csv");

        Matrix[] testDataSet = DataLoader.loadImageSet("src/com/company/MNIST_DATA/mnist_test_vectors.csv");
        Matrix[] testLabels = DataLoader.loadLabels("src/com/company/MNIST_DATA/mnist_test_labels.csv");

        Network n = new Network(new int[] {784,500,300,10});

        n.train(trainDataSet, trainLabels, testDataSet, testLabels);

    }
}
