package AutoCoureur;

import java.io.IOException;

import NeuralNetTrainer.NeuralNetTrainer;
import NeuralNetwork.NeuralNet;

/**
 * Requirements:
 * 
 * 
 * Design:
 * 
 * 
 * Testing:
 * 
 * 
 */
public class App {

    public static boolean stopTheProgram = false;  
    public static boolean cmd = false;  

    public static void main(String[] args) {
        
        int[] layers = { 8,6,4,2 };

        NeuralNet nn =null;

         try {
            nn = NeuralNet.fromFile("NN.txt");
        } catch (IOException e1) {
            nn = new NeuralNet(layers);
            System.out.println("No File");
        }



        for (int i = 0; i < 10; i++) {
            nn.makeNN(10.0);
            nn.setEdges(NeuralNetTrainer.train(nn.getNeuralNets())); 

            try {
                nn.writeToFile("NN.txt");
            } catch (IOException e) {}

        }
    }
}