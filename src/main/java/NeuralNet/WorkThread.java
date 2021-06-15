package NeuralNet;

import java.util.concurrent.Callable;

import NeuralNet.ActivationFunction.ActivationFunction;
import NeuralNet.ActivationFunction.FastSigmoid;

class WorkThread implements Callable<NNdata> {



    private double[][][] edges;
    private Data[] dataSet;
    private ActivationFunction activationFunction = new FastSigmoid() ;
    private int[] edge;

    public WorkThread(double[][][] edges, Data[] dataSet, int[] edge ) {
        this.edges = edges;
        this.dataSet = dataSet;
        this.edge = edge;
    }
    /**
     * Passes the input values through the neural net
     * @param inputValues double input vector 
     * @return what the computer thinks is right 
     */
    public double[][] predict(double[][] input) {
        double[][] output = input;

        for (int layer = 0; layer < edges.length; layer++) {
            input = output;
            output = MatMath.multiplyAndActivate(edges[layer], input, activationFunction);
        }
        return output;
    }


    /**
     * calculate the error of eache datapoint 
     * @param dataSet Data[]
     * @return returns the average error
     */
    private double calculateAverageError(Data[] dataSet) {
        double errorSum = 0;
        for (Data data : dataSet) {
            errorSum += calculateError(data);
        }

        return errorSum / dataSet.length;
    }
    /**
     *  calculate error of the vector 
     * @param data Data
     * @return error as double 
     */
    private double calculateError(Data data) {
        double[][] target = data.getDesiredValue();
        double[][] output = predict(data.getMatrix());

        return MatMath.sumSquaredErrors(target, output);
    }


    @Override
    public NNdata call() throws Exception {
        double[] avgError = new double[1]; 
        avgError[0] = calculateAverageError(dataSet);
        return new NNdata(edges,edge ,avgError[0]);
    }
    
}
