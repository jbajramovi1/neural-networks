package com.company;

public class Matrix {
    private double data[];
    private int num_rows,num_cols;

    public Matrix(double[] data, int num_rows, int num_cols){
        if (data.length != num_rows * num_cols) throw new RuntimeException("Invalid matrix sizes!");

        this.data = data;
        this.num_rows = num_rows;
        this.num_cols = num_cols;
    }

    public Matrix(int num_rows, int num_cols) {
        this.data = new double[num_rows*num_cols];
        this.num_rows = num_rows;
        this.num_cols = num_cols;
    }
    public double[] getData() {
        return data;
    }

    public int getNum_rows() {
        return num_rows;
    }

    public int getNum_cols() {
        return num_cols;
    }

    public boolean withinRange(int row, int column) {
        return row < num_rows && row >= 0 && column < num_cols && column >= 0;
    }

    public void setValue(int row, int column, double value) {
        if (withinRange(row, column))
            data[row*num_cols + column] = value;
        else
            throw new RuntimeException("Out of bound exception");
    }

    public double getValue(int row, int column) {
        if (withinRange(row, column))
            return data[row*num_cols + column] ;
        else
            throw new RuntimeException("Out of bound exception");
    }

    public void setAll(double value) {
        for (int i = 0; i < num_rows; i++)
            for (int j = 0; j < num_cols ; j++)
                setValue(i,j,value);
    }

    /*Transpose*/

    public Matrix transpose() {

        Matrix transposed = new Matrix(num_cols, num_rows);
        transpose(this, transposed);

        return transposed;
    }

    public static void transpose(Matrix m, Matrix dest) {
        if (m.getNum_rows() != m.getNum_cols())
            throw new RuntimeException("Matrix transpose: Matrix size mismatch");

        for(int i = 0; i < m.getNum_rows(); i++) {
            for(int j = 0; j < m.getNum_cols(); j++) {
                dest.setValue(j, i, m.getValue(i, j));
            }
        }
    }

    /*Add*/

    public Matrix add (Matrix m) {
        Matrix out = new Matrix(num_rows,num_cols);
        add (this, m, out);

        return out;
    }

    public static void add(Matrix a, Matrix b, Matrix dest) {
        if (a.getNum_rows() != b.getNum_rows() || a.getNum_cols()!=b.getNum_cols() || a.getNum_rows()!=dest.getNum_rows() || a.getNum_cols()!=dest.getNum_cols())
            throw new RuntimeException("Matrix add: Matrix size mismatch");

        for (int i = 0; i < a.getNum_rows(); i++)
            for (int j = 0; j < a.getNum_cols(); j++ )
                dest.setValue(i, j, a.getValue(i, j) + b.getValue(i , j));
    }

    /*Substract*/

    public Matrix substract(Matrix m) {
        Matrix out = new Matrix(num_rows,num_cols);
        substract (this, m, out);

        return out;
    }

    public static void substract(Matrix a, Matrix b, Matrix dest) {
        if (a.getNum_rows() != b.getNum_rows() || a.getNum_cols()!=b.getNum_cols() || a.getNum_rows()!=dest.getNum_rows() || a.getNum_cols()!=dest.getNum_cols())
            throw new RuntimeException("Matrix substract: Matrix size mismatch");

        for (int i = 0; i < a.getNum_rows(); i++)
            for (int j = 0; j < a.getNum_cols(); j++ )
                dest.setValue(i, j, a.getValue(i, j) - b.getValue(i , j));
    }

    /*Multiplicate*/

    public double getMultipliedEntry(Matrix m, int row, int column) {
        return getMultipliedEntry(this, m, row, column);
    }

    public static double getMultipliedEntry(Matrix a, Matrix b, int row, int column) {
        if(!(row < a.getNum_rows() && row >= 0 && column < b.getNum_cols() && column >= 0))
            throw new RuntimeException("Matrix multiplicate: Out of matrix bounds");

        double sum = 0;

        for(int i = 0; i < a.getNum_cols(); i++) {
            sum += a.getValue(row, i) * b.getValue(i, column);
        }

        return sum;
    }

    /*Scalar multiplication*/

    public Matrix multiply(double scalar) {
        Matrix out = new Matrix(num_rows,num_cols);
        multiply(this, scalar, out);

        return out;
    }

    public void multiplySelf(double scalar) {
        multiply(this, scalar, this);
    }

    public static void multiply(Matrix a, double scalar, Matrix dest) {
        if (a.getNum_rows() != dest.getNum_rows() || a.getNum_cols() != dest.getNum_cols())
            throw new RuntimeException("Matric scalar multiplicate: Matrix size mismatch");

        for(int i = 0; i < a.getNum_rows(); i++) {
            for(int j = 0; j < a.getNum_cols(); j++) {
                dest.setValue(i, j, scalar * a.getValue(i, j));
            }
        }
    }

    public Matrix multiply(Matrix m) {
        Matrix out = new Matrix(num_rows, m.getNum_cols());
        multiply(this, m, out);

        return out;
    }

    public static void multiply(Matrix a, Matrix b, Matrix dest) {
        if(a.getNum_cols() != b.getNum_rows() || dest.getNum_rows() != a.getNum_rows() || dest.getNum_cols() != b.getNum_cols())
            throw new RuntimeException("Matrix multiplicate: Matrix size mismatch");

        for(int i = 0; i < a.getNum_rows(); i++) {
            for(int j = 0; j < b.getNum_cols(); j++) {
                dest.setValue(i, j, getMultipliedEntry(a, b, i, j));
            }
        }
    }

    /*Hadamard product*/

    public Matrix hadamardProduct(Matrix m) {
        Matrix out = new Matrix(num_rows, num_cols);
        hadamardProduct(this, m, out);

        return out;
    }

    public void hadamardProductSelf(Matrix m) {
        hadamardProduct(this, m, this);
    }

    public static void hadamardProduct(Matrix a, Matrix b, Matrix dest) {
        if(a.getNum_rows() != b.getNum_rows() || a.getNum_cols() != b.getNum_cols() || a.getNum_rows() != dest.getNum_rows() || a.getNum_cols() != dest.getNum_cols())
            throw new RuntimeException("Matrix Hadamard product: Matrix size mismatch");

        for(int i = 0; i < a.getNum_rows(); i++) {
            for(int j = 0; j < a.getNum_cols(); j++) {
                dest.setValue(i, j, a.getValue(i, j) * b.getValue(i, j));
            }
        }
    }

    /*Hadamard division*/

    public Matrix hadamardDivision(Matrix m) {
        Matrix out = new Matrix(num_rows, num_cols);
        hadamardDivision(this, m, out);

        return out;
    }

    public void hadamardDivisionSelf(Matrix m) {
        hadamardDivision(this, m, this);
    }

    public static void hadamardDivision(Matrix a, Matrix b, Matrix dest) {
        if(a.getNum_rows() != b.getNum_rows() || a.getNum_cols() != b.getNum_cols() || a.getNum_rows() != dest.getNum_rows() || a.getNum_cols() != dest.getNum_cols())
            throw new RuntimeException("Matrix Hadamard division: Matrix size mismatch");

        for(int i = 0; i < a.getNum_rows(); i++) {
            for(int j = 0; j < a.getNum_cols(); j++) {
                dest.setValue(i, j, a.getValue(i, j) / b.getValue(i, j));
            }
        }
    }

    public boolean equals(Matrix m) {
        if(num_rows != m.getNum_rows() || num_cols != m.getNum_cols())
            return false;

        for(int i = 0; i < num_rows; i++) {
            for(int j = 0; j < num_cols; j++) {
                if(getValue(i, j) != m.getValue(i, j))
                    return false;
            }
        }

        return true;
    }

}