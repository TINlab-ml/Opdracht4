// Author: Stefan Beenen

package NeuralNet;

import java.util.Arrays;

import NeuralNet.ActivationFunction.ActivationFunction;

import java.lang.IllegalArgumentException;


public final class MatMath {

    private MatMath(){} // private constructor; no initialization, only static methods


    public static double[][] multiplyAndActivate(double[][] mA, double[][] mB, ActivationFunction af) throws IllegalArgumentException {
        
        if (!areMultiplyable(mA, mB)) throw new IllegalArgumentException("Matrix sizes are not compatible");

        int rows = mA.length;
        int cols = mB[0].length;
        int numOfCalcs = mA[0].length;

        double[][] resultMatrix = new double[rows][cols];

        for (int r=0; r<rows; r++){
            for (int c=0; c<cols; c++){

                double sum = 0;
                for (int i=0; i<numOfCalcs; i++){
                    sum += mA[r][i] * mB[i][c];
                }
                resultMatrix[r][c] = af.calculateActivation(sum);
            }
        }

        return resultMatrix;
    }

                double sum = 0;
                for (int i=0; i<numOfCalcs; i++){
                    sum += mA[r][i] * mB[i][c];
                }
                resultMatrix[r][c] = af.calculateActivation(sum);
            }
        }

    public static double sumSquaredErrors(double[][] mA, double[][] mB) {
        int rows = mA.length;
        int cols = mA[0].length;

        double sse = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double error = mA[r][c] - mB[r][c];
                sse += error*error;
            }
        }

        return sse;
    }


    public static double[][] sub(double[][] mA, double[][] mB) {
        int rows = mA.length;
        int cols = mA[0].length;

        double[][] resultMatrix = new double[cols][rows];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                resultMatrix[c][r] = mA[r][c] - mB[r][c];
            }
        }

        return resultMatrix;
    }
    

    public static double[][] fromList(double[] list){
        double[][] newVector = new double[list.length][1];
        for (int i=0; i<list.length; i++) {
            newVector[i][0] = list[i];
        }
        return newVector;
    }


    public static void print(double[][] mA) {
        System.out.println(Arrays.deepToString(mA));
    }


    public static boolean areMultiplyable(double mA[][], double mB[][]) {
        return mA[0].length == mB.length; 
    }


    public static boolean isRowVector(double vA[][]) {
        return vA.length == 1;
    }


    public static boolean isColVector(double vA[][]) {
        return vA[0].length == 1;
    }

    
    public static boolean matricesEqualSize(double mA[][], double mB[][]) {
        return mA.length == mB.length && mA[0].length == mB[0].length;
    }
}