package com.company;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DataLoader {

    public static Matrix[] loadImageSet(String fileName) {
        BufferedReader br_1 = null;
        BufferedReader br_2, br_3;
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

            images = new Matrix[lines_num];

            br_1 = new BufferedReader(new FileReader(fileName));
            int counter = 0;
            while ((line = br_1.readLine()) != null) {
                String[] temp2 = line.split(",");
                double[] result = new double[pixels_num];

                for(int i = 0; i < temp2.length; i++)
                    result[i] = Double.parseDouble(temp2[i]);

                double[] normalized = normalizeFeatures(result);
                Matrix image = new Matrix(normalized, pixels_num, 1);

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
        BufferedReader br_2;
        String line = "";

        int lines_num = 0;
        int counter = 0;

        Matrix[] labels;

        try{
            br_2 = new BufferedReader(new FileReader(fileName));
            while((br_2.readLine()) != null)
                lines_num++;

            labels = new Matrix[lines_num];
            br_1 = new BufferedReader(new FileReader(fileName));
            while((line = br_1.readLine()) != null){
                labels[counter] = new Matrix(getResultArray(Integer.parseInt(line)), 10, 1);
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

    private static double[] normalizeFeatures(double[] pixels) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        double sum = 0;

        for (double pixel: pixels) {
            sum = sum + pixel;

            if (pixel > max)
                max = pixel;

            if (pixel < min)
                min = pixel;
        }

        double mean = sum / pixels.length;
        double[] pixelsNorm = new double[pixels.length];

        for (int i = 0; i< pixels.length; i++)
            pixelsNorm[i] = (pixels[i] - mean) / (max - min);

        return pixelsNorm;
    }
}