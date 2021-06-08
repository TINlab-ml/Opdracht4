package AutoCoureur;

import CarSimulator.Car;
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

    public static void main(String[] args) {

        int[] layers = { 8,6,4,2 };
        NeuralNet nn = new NeuralNet(layers);

        // train the Auto Coureur
        

        
        // int amountOfCars = 5;
        // Car cars[] = new Car[amountOfCars];

        // for (int i = 0; i < amountOfCars; i++) {
        //     cars[i] = new Car();
        // }

        
        // for (int i = 0; i < amountOfCars; i++) {
        //     String s = cars[i].control(cars[i].recvProperties());
        //     // cars[i].sendControls();
        //     System.out.println(s);
        // }


        // for (int i = 0; i < amountOfCars; i++) {
        //     cars[i].close();
        // }


    }
}