package com.company;

public class Main {

    public static void main(String[] args) {
	// write your code here
        Matrix trainDataSet = DataLoader.loadImageSet("src/com/company/MNIST_DATA/mnist_train_vectors.csv");
        Matrix trainLabels = DataLoader.loadLabels("src/com/company/MNIST_DATA/mnist_train_labels.csv");

        Matrix testDataSet = DataLoader.loadImageSet("src/com/company/MNIST_DATA/mnist_test_vectors.csv");
        Matrix testLabels = DataLoader.loadLabels("src/com/company/MNIST_DATA/mnist_test_vectors.csv");
    }
}
