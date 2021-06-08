package NeuralNetTrainer;

public class Data {
    public double progress;
    public double lapTime;
    public double[][][] nn;
    
    Data(double progress,double lapTime, double[][][] neuralNet){
        this.progress = progress;
        this.lapTime = lapTime;
        this.nn = neuralNet;
    }

}
