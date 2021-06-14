/*
Authors
- Stefan Beenen 0963586
- Ruben Hiemstra 0924010
- Jordy Weijgertse 0974347
*/
package NeuralNet;

public class NeuralNet {

    private double[][][] edges;

    /**
     * @return list of matrices containing the weights
     */
    public double[][][] getEdges() {
        return edges;
    }

    public NeuralNet(int[] layers) {

        edges = new double[layers.length - 1][][];
        double initEdgeWeight = 1;

        for (int i = 0; i < layers.length - 1; i++) {
            int input = layers[i];
            int output = layers[i + 1];

            edges[i] = new double[output][input];

            setEdgeWeight(i, initEdgeWeight);
        }
    }

    /**
     * if you have already the weights of the edges  
     * @param edges 
     */
    public NeuralNet(double[][][] edges) {
        this.edges = edges;
    }

    /**
     * init the layers with a static value
     * @param layer
     * @param initEdgeWeight
     */
    private void setEdgeWeight(int layer, double initEdgeWeight ){
        for (int row = 0; row < edges[layer].length; row++) {
            for (int col = 0; col < edges[layer][row].length; col++) {
                edges[layer][row][col] = initEdgeWeight;
            }
        }
    }

    /**
     * This will predict the the neural net 
     * @param inputValues double[]
     * @return
     */
    public double[][] predict(double[] inputValues) {
        double[][] input = MatMath.fromList(inputValues);
        double[][] output = input;

        for (int layer = 0; layer < edges.length; layer++) {
            input = output;
            output = MatMath.multiply(edges[layer], input);
            output = MatMath.sigmoid(output);
        }
        return output;
    }

    /**
     * 
     * @param dataSet Data[]
     * @param weightChange double
     */

    public void train(Data[] dataSet, double weightChange) {
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
     * 
     * @param data Data
     * @return
     */
    private double calculateError(Data data) {
        double[][] target = data.getDesiredValue();
        double[][] output = predict(data.getMatrix());

        return MatMath.sumOfSquares(MatMath.sub(target, output));
    }

    /**
     * 
     * @param dataSet Data[]
     * @return
     */
    public double calculateAverageError(Data[] dataSet) {
        double errorSum = 0;
        for (Data data : dataSet) {
            errorSum += calculateError(data);
        }

        return errorSum / dataSet.length;
    }

    /**
     * 
     * @param dataSet Data[]
     * @param edgeIndex int[]
     * @param weightChange double
     * @return
     */
    private double calculateErrorEdgeChange(Data[] dataSet, int[] edgeIndex, double weightChange) {
        edges[edgeIndex[0]][edgeIndex[1]][edgeIndex[2]] += weightChange;
        double avgError = calculateAverageError(dataSet);
        edges[edgeIndex[0]][edgeIndex[1]][edgeIndex[2]] -= weightChange;
        
        return avgError;
    }

    /**
     * 
     * @param dataSet Data[]
     * @param weightChange double
     * @param epochs int
     */
    public void fit(Data[] dataSet, double weightChange, int epochs) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            System.out.println(epoch);
            train(dataSet, weightChange);
        }
    }

}
