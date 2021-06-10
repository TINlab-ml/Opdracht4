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


        int c = 0;
        while (true) {

            nn.makeNN(1);
            nn.setEdges(NeuralNetTrainer.train(nn.getNeuralNets(), c)); 

            try {
                nn.writeToFile("NN.txt");
            } catch (IOException e) {
                System.out.println("Write to file failed");
            }

            c++;
        }

    }
}