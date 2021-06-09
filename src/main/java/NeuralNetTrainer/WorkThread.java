package NeuralNetTrainer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

import CarSimulator.Car;
import CarSimulator.Properties;
import NeuralNetwork.NeuralNet;

class WorkThread implements Callable<Data> {
    private double[][][] edges;
    private int id;
    

    public WorkThread(double[][][] edges, int id) {
        this.edges = edges;
        this.id = id;
    }

    @Override
    public Data call() throws Exception {
        int amountOfCars = 1;
        Car cars[] = new Car[amountOfCars];

        for(int i = 0; i < amountOfCars; i++){
            cars[i] = new Car(id);
        }
        Properties carProperties =null;
        while(true) { 

            carProperties = cars[0].recvProperties();

            if(carProperties.getCollided()||!carProperties.getIsOnTrack()){
                break;
            }

            double[] nearestConesVector = carProperties.getNearestCones();
            double[][] carinput = NeuralNet.predict(edges, nearestConesVector);
            cars[0].sendControls(carinput[0][0],carinput[1][0]);
        }


 

        for (int i = 0; i < amountOfCars; i++) {
            cars[i].close();
        }
       
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        System.out.println( id + " " + timeStamp);

        return  new Data(carProperties.getProgress(),carProperties.getLapTime(),edges);
    }
}
