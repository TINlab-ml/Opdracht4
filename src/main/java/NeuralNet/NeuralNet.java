/*
Authors
- Stefan Beenen 0963586
- Ruben Hiemstra 0924010
- Jordy Weijgertse 0974347
*/
package NeuralNet;

import NeuralNet.ActivationFunction.*;


public class NeuralNet {

    private double[][][] edges;
    private ActivationFunction activationFunction = new FastSigmoid();

    /**
     * @return list of matrices containing the weights
     */
    public double[][][] getEdges() {
        return edges;
    }

    /**
      * initializes neuralnet with given layer sizes from input to output with optional hidden layers
      * @params layers is a list with the amount of nodes in each layer
      */
      
    public NeuralNet(int[] layers) {

        edges = new double[layers.length - 1][][];
        double initEdgeWeight = 1;

        for (int i = 0; i < layers.length - 1; i++) {
            int input = layers[i];
            int output = layers[i + 1];

            edges[i] = new double[output][input];

            initEdgeWeights(i, initEdgeWeight);
        }
    }

    /**
     * if you have already the weights of the edges  
     * @param edges 
     */
    public NeuralNet(double[][][] edges) {
        this.edges = edges;
    }

    private void initEdgeWeights(int layer, double initEdgeWeight ){
        for (int row = 0; row < edges[layer].length; row++) {
            for (int col = 0; col < edges[layer][row].length; col++) {
                edges[layer][row][col] = initEdgeWeight;
            }
        }
    }

    /**
     * Passes the input values through the neural net
     * @param inputValues double input vector 
     * @return what the computer thinks is right 
     */
    public double[][] predict(double[] inputValues) {
        double[][] input = MatMath.fromList(inputValues);
        double[][] output = input;

        for (int layer = 0; layer < edges.length; layer++) {
            input = output;
            output = MatMath.multiplyAndActivate(edges[layer], input, activationFunction);
        }
        return output;
    }


    private void train(Data[] dataSet, double weightChange) {
        int[] bestEdgeIndex = new int[3];
        double bestEdgeWeightChange =0;
        double lowestAvgError = calculateAverageError(dataSet);

        for (int layer = 0; layer < edges.length; layer++) {
            for (int row = 0; row < edges[layer].length; row++) {
                for (int col = 0; col < edges[layer][row].length; col++) {

                        int[] currentIndex = {layer, row, col};

                        double avgErrorPlus = calculateErrorEdgeChange(dataSet, currentIndex, weightChange);
                        double avgErrorMinus = calculateErrorEdgeChange(dataSet, currentIndex, -weightChange);
                
                        if (lowestAvgError < avgErrorPlus && lowestAvgError < avgErrorMinus ){
                            continue;
                        }

                        if (avgErrorMinus < avgErrorPlus){
                            lowestAvgError = avgErrorMinus ;  
                            bestEdgeWeightChange = -weightChange;
                        } else {
                            lowestAvgError = avgErrorPlus;
                            bestEdgeWeightChange = weightChange;
                        }

                        bestEdgeIndex = currentIndex;
                    }
                }
            }
        edges[bestEdgeIndex[0]][bestEdgeIndex[1]][bestEdgeIndex[2]] += bestEdgeWeightChange;
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
     * chance the edge and chance them back
     * @param dataSet Data[]
     * @param edgeIndex int[]
     * @param weightChange double
     * @return the avrrage error of the dataset with the edge chance  
     */
    private double calculateErrorEdgeChange(Data[] dataSet, int[] edgeIndex, double weightChange) {
        edges[edgeIndex[0]][edgeIndex[1]][edgeIndex[2]] += weightChange;
        double avgError = calculateAverageError(dataSet);
        edges[edgeIndex[0]][edgeIndex[1]][edgeIndex[2]] -= weightChange;
        
        return avgError;
    }

    public void fit(Data[] dataSet, double weightChange, int epochs) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            System.out.println(epoch);
            train(dataSet, weightChange);
        }
    }

}
