package NeuralNet;

public class NNdata {
    public double[][][] nn;
    public double error;

    public NNdata(double[][][] neuralNet, double error){
        this.nn = neuralNet;
        this.error = error;
    }

}
