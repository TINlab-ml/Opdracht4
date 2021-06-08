/*
Authors
- Stefan Beenen 0963586
- Ruben Hiemstra 0924010
- Jordy Weijgertse 0974347
*/
package NeuralNetwork;

import java.util.ArrayList;
import java.util.Random;

public class NeuralNet {

    private double[][][] edges; 
    private ArrayList<double[][][]> neuralNets ;

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

    void setEdgeWeight(int layer, double initEdgeWeight ){

        for (int row = 0; row < edges[layer].length; row++) {
            for (int col = 0; col < edges[layer][row].length; col++) {
                Random r = new Random();

                int low = 500;
                int high = 1000;

                edges[layer][row][col] = (double)  r.nextInt(high-low) + low;
            }
        }
    }

    public double[][] predict(double[] inputValues) {
        double[][] input = MatMath.fromList(inputValues);
        double[][] output = input;

        for (int layer = 0; layer < edges.length; layer++) {
            input = output;
            output = MatMath.multiply(edges[layer], input);
            output = MatMath.sigmoid(output);
        }
        return MatMath.norm(output);
    }

    public static double[][] predict(double[][][] nn, double[] inputValues) {
        double[][] input = MatMath.fromList(inputValues);
        double[][] output = input;

        for (int layer = 0; layer < nn.length; layer++) {
            input = output;
            output = MatMath.multiply(nn[layer], input);
            output = MatMath.sigmoid(output);
        }
        return MatMath.norm(output);
    }


    public void makeNN(double weightChange) {
        this.neuralNets = new ArrayList<double[][][]>(); // Create an ArrayList object
        for (int layer = 0; layer < edges.length; layer++) {
            for (int row = 0; row < edges[layer].length; row++) {
                for (int col = 0; col < edges[layer][row].length; col++) {
                    int[] currentIndex = {layer, row, col};
                    
                    this.neuralNets.add(changeEdge( edges,currentIndex,  weightChange));
                    this.neuralNets.add(changeEdge( edges,currentIndex,  -weightChange));

                    }
                }
            }
    }

    private double[][][] changeEdge( double[][][] edges ,int[] edgeIndex,double weightChange) {
        edges[edgeIndex[0]][edgeIndex[1]][edgeIndex[2]] += weightChange;

        double[][][] newEdges = new double[edges.length][][];


        for (int layer = 0; layer < edges.length ;layer++) {
            for (int row = 0; row < edges[layer].length; row++) {
                newEdges[layer] = new double[edges[layer].length][edges[layer][0].length];
            }
        }

        newEdges = copyOf3Dim(edges, newEdges);

        edges[edgeIndex[0]][edgeIndex[1]][edgeIndex[2]] -= weightChange;
        return newEdges;
    } 

    private double[][][] copyOf3Dim(double[][][] array, double[][][]copy) {

        for (int x = 0; x < array.length; x++) {  
            for (int y = 0; y < array[x].length; y++) {  
                for (int z = 0; z < array[x][y].length; z++) {
                    copy[x][y][z] = array[x][y][z];  
                }  
            }  
        } 
        return copy;
    }


    public ArrayList<double[][][]> getNeuralNets() {
        return neuralNets;
    }
    public void setEdges(double[][][] edges) {
        this.edges = edges;
    }

    public double[][][] getEdges() {
        return edges;
    }
}
