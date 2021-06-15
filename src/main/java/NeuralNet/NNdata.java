package NeuralNet;

public class NNdata {
    public double[][][] nn;
    public double error;
    private int[] edge;

    public NNdata(double[][][] neuralNet,int[] edge, double error){
        this.nn = neuralNet;
        this.error = error;
        this.edge = edge;
    }

}
