package AutoCoureur;

import java.io.IOException;

import CarSimulator.Car;
import CarSimulator.Properties;
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
                        train(args[1], args[2]);
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
        // convert dataset to format for neuralnet
        // get startvalues of edges if necessary
        // create neural net
        // train
        // save edges to (new) file
        
    }

    /**
     * Control the car used a trained neural net to test its performance
     * @param edgesFile A file where the weights of the edges are saved
     */
    public static void test(String edgesFile){
        try {
            NeuralNet neuralNet = NeuralNet.fromFile(edgesFile);
            Car car = new Car();

            while (true) {
                car.recvProperties();
                Properties carData = car.getProperties();
 
                double[] filteredData = {1,1,1,1,1,1,1,1}; // PLACEHOLDER

                double steeringAngle = neuralNet.predict(filteredData)[0][0];
                double targetVelocity = 0.9;    // default velocity, to be replaced by the neuralnet

                car.sendControls(steeringAngle, targetVelocity);
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
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
            if(car.getProperties().getLapTime() > 2){
                System.out.println(car.getProperties().getProgress());
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
