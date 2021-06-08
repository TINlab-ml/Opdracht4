// Author: Stefan Beenen

package NeuralNetwork;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.lang.IllegalArgumentException;


public final class MatMath {

    private MatMath(){} // private constructor; no initialization, only static methods


    public static double[][] norm(double[][] vA) {
        return map(vA, a -> a/magnitude(vA));
    }


    public static double magnitude(double[][] vA) {
        return Math.sqrt( sumOfSquares(vA) );
    }


    public static double[][] pow(double[][] mA, double n) {
        return map(mA, a -> Math.pow(a,n));
    }


    public static double[][] sub(double[][] mA, double[][] mB) {
        return map(mA, mB, (a, b) -> a - b);
    }


    public static double sigmoid(double x) { // sigmoid on single value
        return 1 / (1 + Math.exp(-x));
    }


    public static double[][] sigmoid(double[][] mA) { // sigmoid on matrix
        return map(mA, (x) -> sigmoid(x));
    }
    

    public static double derivedSigmoid(double x) {
        double sigx = sigmoid(x);
        return sigx * (1 - sigx);
    }


    public static double[][] derivedSigmoid(double[][] mA) { // sigmoid on matrix
        return map(mA, (x) -> derivedSigmoid(x));
    }


    public static double sum(double[][] mA){
        return reduce(mA, (x,a) -> x+a, 0);
    }


    public static double sumOfSquares(double[][] mA){
        return reduce(mA, (x,a) -> x+Math.pow(a,2), 0);
    }
    

    public static double[][] transpose(double[][] mA) {
        int rows = mA.length;
        int cols = mA[0].length;

        double[][] resultMatrix = new double[cols][rows];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                resultMatrix[c][r] = mA[r][c];
            }
        }

        return resultMatrix;
    }


    public static double reduce(double[][] mA, BiFunction<Double, Double, Double> fn, double initialValue) {
        int rows = mA.length;
        int cols = mA[0].length;

        double result = initialValue;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                result = fn.apply(result, mA[r][c]);
            }
        }

        return result;
    }
    

    public static double[][] fromList(double[] list){
        double[][] newVector = new double[list.length][1];
        for (int i=0; i<list.length; i++) {
            newVector[i][0] = list[i];
        }
        return newVector;
    }


    public static double[][] map(double[][] mA, double[][] mB, BiFunction<Double, Double, Double> fn) 
        throws IllegalArgumentException { 
        
        if (!matricesEqualSize(mA, mB)) throw new IllegalArgumentException("Matrices must be equal in size");

        int rows = mA.length;
        int cols = mA[0].length;

        double[][] resultMatrix = new double[rows][cols];

        for (int r=0; r<rows; r++){
            for (int c=0; c<cols; c++){
                resultMatrix[r][c] = fn.apply(mA[r][c], mB[r][c]);
            }
        }

        return resultMatrix;
    }


    public static double[][] map(double[][] mA, Function<Double, Double> fn) { 
        int rows = mA.length;
        int cols = mA[0].length;

        double[][] resultMatrix = new double[rows][cols];

        for (int r=0; r<rows; r++){
            for (int c=0; c<cols; c++){
                resultMatrix[r][c] = fn.apply(mA[r][c]);
            }
        }

        return resultMatrix;
    }


    public static String toString(double[][] mat) {
        String matrixStr = "";

        for (double[] row : mat){
            matrixStr += Arrays.toString(row) + '\n';
        }

        return matrixStr;
    }


    public static void print(double[][] mA) {
        System.out.print(toString(mA));
    }
    
    
    public static double[][] multiply(double[][] mA, double[][] mB) 
        throws IllegalArgumentException {
        
        if (!areMultiplyable(mA, mB)) throw new IllegalArgumentException("Matrix sizes are not compatible");

        int rows = mA.length;
        int cols = mB[0].length;
        int numOfCalcs = mA[0].length;

        double[][] resultMatrix = new double[rows][cols];

        for (int r=0; r<rows; r++){
            for (int c=0; c<cols; c++){

                for (int i=0; i<numOfCalcs; i++){
                    resultMatrix[r][c] += mA[r][i] * mB[i][c];
                }

            }
        }

        return resultMatrix;
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

