package NeuralNetTrainer;

import java.util.ArrayList;

import CarSimulator.Car;

import NeuralNetwork.NeuralNet;

public class NeuralNetTrainer {


    public static double[][][] train(ArrayList<double[][][]> listOfnn ) {


        for (double[][][] nn : listOfnn) {
            int amountOfCars = 1;
            Car cars[] = new Car[amountOfCars];

            boolean botsing = false, autoVanDeBaan = false, rondjeCompleet = false;

            while(true) { // is de auto van de baan of is er een botsing of de auto heeft het rondje gedaan

                // Properties rec = cars[0].recvProperties();

                if(botsing || autoVanDeBaan || rondjeCompleet){
                    break;
                }

                // double[] vec = new cars[0]; 
                //lidar de 8 dichtbezijde afstand en hoek er uit
                /*
                [
                    afstand
                    hoek
                    afstand
                    hoek
                    afstand
                    hoek
                    afstand
                    hoek
                ]
                */
                // nn.predict(vec);

                String jsonControlString = " "; // double targetVelocity; double steeringAngle;

                // cars[0].sendControls(targetVelocity,targetVelocity);
            }
            for (int i = 0; i < amountOfCars; i++) {
                cars[i].close();
            }


        }
        // geeft the beste edge treug met waarde verandering. 
        
        // double[][][] TheBestNN = sort op the best fitnes;
        return  null; 
    }

    
    public void fit() {

    }
    
}
