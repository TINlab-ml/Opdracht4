package NeuralNetTrainer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import CarSimulator.Car;
import CarSimulator.Properties;
import NeuralNetwork.NeuralNet;


public class NeuralNetTrainer {


    public static double[][][] train(ArrayList<double[][][]> listOfnn ) {
        ArrayList<Data> data = new ArrayList<Data>();



        for (int indexOfnn = 0; indexOfnn < listOfnn.size(); indexOfnn++) {

            int amountOfCars = 1;
            Car cars[] = new Car[amountOfCars];

            for(int i = 0; i < amountOfCars; i++){
                cars[i] = new Car();
            }
            Properties carProperties =null;
            while(true) { 

                carProperties = cars[0].recvProperties();
                if(carProperties.getCollided()||!carProperties.getIsOnTrack()){
                    break;
                }

                double[] nearestConesVector = carProperties.getNearestCones();
                double[][] carinput = NeuralNet.predict(listOfnn.get(indexOfnn), nearestConesVector);
                cars[0].sendControls(carinput[0][0],carinput[1][0]);
            }

            for (int i = 0; i < amountOfCars; i++) {
                cars[i].close();
            }
           
            data.add( new Data(carProperties.getProgress(),carProperties.getLapTime(),listOfnn.get(indexOfnn)));
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            System.out.println( indexOfnn + " " + timeStamp);
        }

        Collections.sort(data, new Comparator<Data>() {
            @Override
            public int compare(Data d1, Data d2) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return d1.progress > d2.progress ? -1 : (d1.progress < d2.progress) ? 1 : 0;
            }
        });

        // double[][][] TheBestNN = sort op the best fitnes;
        return data.get(0).nn ; 
    }

    

    
}
