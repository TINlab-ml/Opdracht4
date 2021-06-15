package AutoCoureur;

import java.io.IOException;
import java.util.Arrays;

import CarSimulator.Car;
import CarSimulator.Controls;
import CarSimulator.Properties;
import CarSimulator.DataSet;
import NeuralNet.Data;
import NeuralNet.MatMath;
import NeuralNet.NeuralNet;

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
    // For testing purposes to see if the right function is called, might be removed later
    public static String currentFunction = null;


    public static void main(String[] args){

        if(args.length > 0){
            currentFunction = args[0];
            switch(currentFunction){
                case "data":
                    if(args.length == 2){
                        getData(args[1]);
                    }
                    break;
                    
                case "train":
                    if(args.length == 2){
                        train(args[1], null);
                    } else if(args.length == 3){
                        long start = System.currentTimeMillis();
                        train(args[1], args[2]);
                        System.out.println(System.currentTimeMillis() -start);
                    }
                    break;
                    
                case "test":
                    if(args.length == 2){
                        test(args[1]);
                    }
                    break;
                    
                default:
                    break;
            }

        } else{
            basicControlLoop();
        }
    }

    /**
     * Get a raw dataset from the car by controlling it and saving all the input(properties)-output(controls)
     * @param dataSetFile The file where the raw dataset needs to be saved
     */
    public static void getData(String dataSetFile){
        // start car
        // control car
        // save dataset to specified file

    }

    /**
     * Train the neural net
     * @param dataSetFile A file with a raw dataset from the car
     * @param edgesFile A file where the weights of the edges are saved
     */
    public static void train(String dataSetFile, String edgesFile){
        // get dataset out of file
        DataSet carDataSet = DataSet.loadFromJsonFile(dataSetFile);
        
        // convert dataset to format for neuralnet

        int end = carDataSet.getPropertiesList().size()-1;
        Data[] dataSet = new Data[end];

        for(int indexDataset = 0; indexDataset < end; indexDataset++){
            Properties properties = carDataSet.getFirstProperties();
            Controls control = carDataSet.getFirstControls();
            
            dataSet[indexDataset] = new Data(
                properties.getRay(120,8), 
                new double[]{
                    control.getSteeringAngle()
                }
            );
        }


        
        // create neural net
        int[] layers = {8,6,4,1 };
        NeuralNet nn = null;
        
        // get startvalues of edges if necessary
        try {
            nn = NeuralNet.loadFromJsonFile(edgesFile);
        } catch (Exception e) {
            System.out.println("No file to init edges");
            nn = new NeuralNet(layers);
        }
        UserInputControls uic = UserInputControls.getInstance();
        System.out.println("NeuralNet.cores "+ NeuralNet.cores);
        System.out.println("press ESC to save the NN and exit the program");        
        // train
        int count = 0;
        double newImprovement = 0;
        double oldImprovement = 0;
        double weights = 10000;
        while(!uic.getQuitingStatus()){

            newImprovement =  nn.fit(dataSet, weights, 10,count);
            count++; 

            double diff = (oldImprovement - newImprovement );
            System.out.println("\t Improvement "+  diff + "\n");
            oldImprovement = newImprovement;
            if(diff ==0){
                weights/=10;
                System.out.println("weights has been changed to "+ weights);
            }
        }
        nn.stopExecutor();

        // save
        if(edgesFile != null){
            nn.saveToJsonFile(edgesFile);
        }
        System.out.println(Arrays.deepToString(nn.getEdges()).replace("[", "{").replace("]", "}"));
        System.exit(0);
    }

    /**
     * Control the car used a trained neural net to test its performance
     * @param edgesFile A file where the weights of the edges are saved
     */
    public static void test(String edgesFile){
        // initialize objects
        NeuralNet neuralNet = null;
        try {
            neuralNet = NeuralNet.loadFromJsonFile(edgesFile);
        } catch (IOException e) {
            System.out.println("No file to init edges");
        }
        Car car = new Car();

        // test main loop
        while (true) {
            car.recvProperties();
            Properties carData = car.getProperties();

            double[][] neuralNetInput = MatMath.fromList(carData.getRay(120, 8));
            System.out.println(neuralNetInput[0][0]);
            double steeringAngle = neuralNet.predict(neuralNetInput)[0][0];
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {}
            double targetVelocity = 0.5;    // default velocity, to be replaced by the neuralnet

            car.sendControls(steeringAngle, targetVelocity);
        }
    }

    /**
     * basic loop to run Jacques code to control a car
     */
    public static void basicControlLoop(){
        while(true){
            int amountOfCars = 1;
            Car car[] = new Car[amountOfCars];
            for (int i = 0; i < car.length; i++) {
                car[i] = new Car();
            }

            for (int i = 0; i < car.length; i++) {
                control(car[i]);
            }
        }
    }

    /**
     * Control a car for testing purposes
     * @param car
     */
    public static void control(Car car){
        while(true){
            car.recvProperties();
            if(car.getProperties().getProgress() >= 100){
                System.out.println(car.getProperties().getLapTime());
                car.close();
                break;
            }
            double[] lidarDistances = car.getProperties().getLidarDistances();
            long lidarHalfApertureAngle = car.getProperties().getLidarHalfApertureAngle();
            long lidarApertureAngle = 2 * lidarHalfApertureAngle;
    
            // ====== BEGIN of control algorithm
    
            double nearestObstacleDistance = 1e20;
            double nearestObstacleAngle = 0.;
            
            double nextObstacleDistance = 1e20;
            double nextObstacleAngle = 0.;
    
            for (long lidarAngle = -lidarHalfApertureAngle; lidarAngle < lidarHalfApertureAngle; lidarAngle++) {
                long distanceIndex = lidarAngle < 0 ? lidarAngle + lidarApertureAngle : lidarAngle;
                double lidarDistance = lidarDistances [(int) distanceIndex];
                
                if (lidarDistance < nearestObstacleDistance) {
                    nextObstacleDistance = nearestObstacleDistance;
                    nextObstacleAngle = nearestObstacleAngle;
                    
                    nearestObstacleDistance = lidarDistance;
                    nearestObstacleAngle = lidarAngle;
                }
                else if (lidarDistance < nextObstacleDistance) {
                    nextObstacleDistance = lidarDistance;
                    nextObstacleAngle = lidarAngle;
                }
            }
            
            double targetObstacleAngle = (nearestObstacleAngle + nextObstacleAngle) / 2;
    
            double steeringAngle = targetObstacleAngle;
            double targetVelocity = (90 - Math.abs (steeringAngle)) / 60;
    
            // ====== END of control algorithm
    
            car.sendControls(steeringAngle, targetVelocity);
        }
    }
}
