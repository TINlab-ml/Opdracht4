/** 
 * Authors:
 *  Jordy Weijgertse  0974347
 *  Ruben Hiemstra    0924010
 *  Stefan Beenen     0963586
 * 
 * Requirements:
 *  This program needs to be able to:
 *      - Start and stop the SimPyLC car simulation
 *      - Connect through sockets with the SimPyLC car simulation.
 *      - Send and receive the appropiate commands with JSON to drive and steer the vehicle 
 *        and to receive the lidar array data
 *      - Generate data and format it properly for the machine learning algorithm
 *      - Learn to manoeuvre the car through the default track with a machine learning technique (without hitting the cones and going off track) 
 * 
 * Design:
 *    The program is split up in the following parts:
 *      - CarSimulator
 *          - Socket connection to the SimPyLC Python simulation
 *          - JSON object translation to control the car
 *          - API interface for controlling the car and receiving the relevant information
 *      - NeuralNet
 *          - A neural net with its nodes and edges
 *          - Mathematical methods for prediction and training (matrix multiplication, sum of least squares, activation function)
 *          - Training algorithm
 *              - Supervised learning by changing each weight and testing them  
 * Testing:
 *      Testing of methods where it is possible will be done by unit tests
 *      and testing of the methods where unit tests are not possible, together with the integration tests, will be done by running the program
 *      reviewing its behaviour in the simulation
 *      The program is done when all the unit tests are succesfull and the car is driving the whole track without going off track or hitting cones
 */




package AutoCoureur;

import java.io.IOException;
import java.util.Arrays;

import CarSimulator.Car;
import CarSimulator.Controls;
import CarSimulator.Properties;
import CarSimulator.CarData;
import NeuralNet.Data;
import NeuralNet.MatMath;
import NeuralNet.NeuralNet;

public class App {
    private static int amountOfRays = 6;
    private static double minDistance = 0.4;
    private static double maxDistance = 4;
    private static double maxSteeringAngle = 45;
    private static int[] layers = {amountOfRays,12,3,1}; 
    private static double facSteeringAngle = 1.4;


    public static void main(String[] args){
        switch (args.length) {
            case 0:
                test("NN.json");
                break;
            case 2:
                switch (args[0]) {
                    case "data":
                        getData(args[1]);
                        break;
                    
                    case "train":
                        train(args[1], null);
                        break;
                    
                    case "test":
                        test(args[1]);
                        break;

                    default:
                        System.out.println("Invalid argument specified!");
                        displayOptions();
                        break;
                }
                break;
        
            case 3:
                if(args[0].equals("train")){
                    train(args[1], args[2]);
                    break;
                }
        
            default:
                System.out.println("Invalid number of arguments specified!");
                displayOptions();
                break;
        }
    }

    /**
     * Get car data from the car by controlling it and saving all the input(properties)-output(controls)
     * @param dataSetFile The file where the car data needs to be saved
     */
    private static void getData(String dataSetFile){
        Car car = new Car();
        CarData carData = new CarData();
        UserInputControls uic = UserInputControls.getInstance();

        System.out.println("Use mouse to steer, press R to start/stop recording and press PAGE_DOWN to save the cardata and exit the program");

        boolean record = false;
        boolean previousRecordStatus = record;
        while(!uic.getQuitingStatus()){
            record = uic.getRecordStatus();
            if(!previousRecordStatus && record){
                System.out.println("Started recording");
            } else if(previousRecordStatus && !record){
                System.out.println("Stopped recording");
            }
            Properties properties = car.recvProperties();
            
            double steeringAngle = uic.getSteeringAngle();
            double targetVelocity = uic.getTargetVelocity();
            Controls controls = car.sendControls(steeringAngle, targetVelocity);
            if(record){
                carData.addProperties(properties);
                carData.addControls(controls);
            }
            previousRecordStatus = record;
        }

        if(carData.getPropertiesList().isEmpty()){
            System.out.println("No data to save");
        } else{
            System.out.println("Saving data to file");
            carData.saveToJsonFile(dataSetFile);
        }

        car.close();
        System.exit(0);
    }

    /**
     * Train the neural net
     * @param dataSetFile A file with a raw dataset from the car
     * @param edgesFile A file where the weights of the edges are saved
     */
    private static void train(String dataSetFile, String edgesFile){
        CarData carData = CarData.loadFromJsonFile(dataSetFile);
        int datasetSize = carData.getPropertiesList().size()-1;
        Data[] dataSet = new Data[datasetSize];
        for(int indexDataset = 0; indexDataset < datasetSize; indexDataset++){
            Properties properties = carData.getFirstProperties();
            Controls controls = carData.getFirstControls();
            dataSet[indexDataset] = new Data(
                MatMath.normalize(properties.getRay(amountOfRays), minDistance, maxDistance), 
                new double[]{
                    MatMath.normalize(controls.getSteeringAngle(), -maxSteeringAngle, maxSteeringAngle)
                }
            );
        }

        NeuralNet nn = null;
        try {
            nn = NeuralNet.loadFromJsonFile(edgesFile);
        } catch (Exception e) {
            System.out.println("No file to init edges");
            nn = new NeuralNet(layers);
        }

        UserInputControls uic = UserInputControls.getInstance();
        System.out.println("NeuralNet.cores "+ NeuralNet.cores);
        System.out.println("press PAGE_DOWN to save the NN and exit the program");
        
        int count = 0;
        double oldLowestError =0;
        while(!uic.getQuitingStatus()){   
            double weights = 0.1;
            while(true){
                double LowestError = nn.fit(dataSet, weights, 100,count);
                double errorDiff = oldLowestError - LowestError;
                System.out.println("LowestError "+ Math.sqrt(LowestError) );

                if(errorDiff ==0){
                    weights/=10; 
                    System.out.println("\n weights has been changed to "+ weights);
                    if(weights  == 0.0001){
                        System.out.println("\n start over with training ");
                        break;
                    }
                }
                if(uic.getQuitingStatus()){
                    System.out.println("\n Stop with training ");
                    break;
                }

                oldLowestError =  LowestError;
                count++;
            }
        }

        nn.stopExecutor();

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
    private static void test(String edgesFile){
        NeuralNet neuralNet = null;
        double lastTime = 0;
        try {
            neuralNet = NeuralNet.loadFromJsonFile(edgesFile);
        } catch (IOException e) {
            System.out.println("No file to init edges");
            System.exit(1);
        }
        Car car = new Car();

        while (true) {
            Properties properties = car.recvProperties();

            double[][] neuralNetInput = MatMath.fromList(MatMath.normalize(properties.getRay(amountOfRays), minDistance, maxDistance));

            double steeringAngle = MatMath.denormalize(NeuralNet.predict( neuralNet.getEdges(),neuralNetInput)[0][0], -maxSteeringAngle, maxSteeringAngle);

            double targetVelocity = 1.0 + ( 2.0 / Math.abs(steeringAngle) );
            
            if (properties.getProgress() >= 100){
                double time = properties.getLapTime();
                
                System.out.println(time - lastTime);
                lastTime = time;
                
            }

            car.sendControls(steeringAngle*(facSteeringAngle ), targetVelocity);
        }
    }


    /**
     * Display the possible arguments to run the app with
     */
    private static void displayOptions(){
        System.out.println("Possible options:");
        System.out.println("- data <dataset.json>");
        System.out.println("\t To gather data and save it to a file specified at <dataset.json>");
        System.out.println("- train <dataset.json>");
        System.out.println("\t To train a new neural net with a dataset specified at <dataset.json>");
        System.out.println("- train <dataset.json> <edges.json>");
        System.out.println("\t To train an existing neural net with a dataset specified at <dataset.json> and the edges specified at <edges.json>");
        System.out.println("- test <edges.json>");
        System.out.println("\t To test the neural net with edges specified at <edges.json>");
    }
}
