package NeuralNetTrainer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

import CarSimulator.Car;
import CarSimulator.Properties;
import NeuralNetwork.NeuralNet;

class WorkThread implements Callable<Data> {
    private double[][][] edges;
    private int genId;
    private int poolId;
    

    public WorkThread(double[][][] edges, int genId ,int poolId) {
        this.edges = edges;
        this.genId = genId;
        this.poolId = poolId;
    }

    @Override
    public Data call() throws Exception {
        int amountOfCars = 1;
        Car cars[] = new Car[amountOfCars];

        for(int i = 0; i < amountOfCars; i++){
            cars[i] = new Car(poolId);
        }
        Properties carProperties =null;
        while(true) { 

            carProperties = cars[0].recvProperties();

            if(carProperties.getCollided()||!carProperties.getIsOnTrack()){
                break;
            }

            double[] ray = carProperties.getRay();
            double[][] carinput = NeuralNet.predict(edges, ray);
            
            cars[0].sendControls(carinput[0][0],carinput[1][0]);
        }


        for (int i = 0; i < amountOfCars; i++) {
            cars[i].close();
        }
       
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        System.out.println(genId + " "+ poolId + " " + timeStamp + " "+ carProperties.getProgress());

        return  new Data(carProperties.getProgress(),carProperties.getLapTime(),edges);
    }
}
