package com.company;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DataLoader {

    public static Matrix[] loadImageSet(String fileName) {
        BufferedReader br_1 = null;
        BufferedReader br_2, br_3;
        //double[][] result_pom = new double[1][1];
        String line = "";

        Matrix[] images;

        int lines_num = 0;
        int pixels_num = 0;

        try {
            br_2 = new BufferedReader(new FileReader(fileName));
            while ((br_2.readLine()) != null)
                lines_num++;

            br_3 = new BufferedReader(new FileReader(fileName));
            line = br_3.readLine();
            String[] temp = line.split(",");
            pixels_num = temp.length;

            //double[][] result = new double[lines_num][pixels_num];
            images = new Matrix[lines_num];

            br_1 = new BufferedReader(new FileReader(fileName));
            int counter = 0;
            while ((line = br_1.readLine()) != null) {
                //Matrix image = new Matrix(28, 28);
                String[] temp2 = line.split(",");
                double[] result = new double[pixels_num];
                for(int i = 0; i < temp2.length; i++){
                    result[i] = Double.parseDouble(temp2[i]) / 255d;
                }
                Matrix image = new Matrix(result, pixels_num, 1);
                images[counter] = image;
                counter++;
            }

            br_1.close();
            br_2.close();
            br_3.close();

            return images;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br_1 != null)
                try {
                    br_1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return new Matrix[lines_num];
    }

    public static Matrix[] loadLabels(String fileName){
        BufferedReader br_1 = null;
        BufferedReader br_2 = null;
        String line = "";
        //double[][] result_pom = new double[1][10];

        int lines_num = 0;
        int counter = 0;

        Matrix[] labels;

        try{
            br_2 = new BufferedReader(new FileReader(fileName));
            while((br_2.readLine()) != null)
                lines_num++;


            labels = new Matrix[lines_num];

            //double[][] result = new double[lines_num][10];

            br_1 = new BufferedReader(new FileReader(fileName));
            while((line = br_1.readLine()) != null){

                String[] temp2 = line.split(",");
                labels[counter] = new Matrix(getResultArray(Integer.parseInt(temp2[0])), 10, 1);
                counter++;
            }

            br_1.close();
            br_2.close();

            return labels;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br_1 != null)
                try {
                    br_1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return new Matrix[lines_num];
    }

    public static double[] getResultArray(int n){
        double[] result = new double[10];

        for(int i = 0; i < result.length; i++){
            result[i] = 0.0;
            if(i == n)
                result[i] = 1.0;
        }

        return result;
    }
}