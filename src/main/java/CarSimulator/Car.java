package CarSimulator;

import java.io.IOException;

import com.google.gson.Gson;

public class Car{
    private  int socketPort = 50000;
    private  int socketPortCounter = 50000;

    private Gson gson;

    private Process pythonWorld;
    private Client client;

    private Properties properties;
    private Controls controls;

    /**
     * Set up a client connection to be able to use the car
     */
    public Car(int carId){
        synchronized (this) {
        gson = new Gson();
        properties = new Properties();
        controls = new Controls();

            socketPortCounter += carId;

            System.out.println();
            try{
                pythonWorld = Runtime.getRuntime().exec("cmd /c activate Tinlab_opdracht_4 && start pythonServer.bat " + socketPortCounter);
                Thread.sleep(2000);
            } catch(Exception e){
                e.printStackTrace();
            }

            

            client = new Client(socketPortCounter);

            socketPortCounter++;
        }
    }

    synchronized void incrementSync() {
        socketPortCounter +=  1;
    }

    synchronized void resetSocketPortCounter() {
        socketPortCounter  = socketPort;
    }


    /**
     * 
     * @return An object with all the properties of the car
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Receive the properties of the car from the world and set them in the properties
     */
    public Properties recvProperties(){
        String incomingString = client.recv();
        // System.out.println(incomingString);
        properties = gson.fromJson(incomingString, Properties.class);
        return properties;
    }

    /**
     * 
     * @param steeringAngle The angle to where the car has to steer
     * @param targetVelocity The target velocity of the car
     */
    public void sendControls(double steeringAngle, double targetVelocity){
        controls = new Controls(steeringAngle, targetVelocity);
        client.send(gson.toJson(controls));
    }

    /**
     * Close the socketconnection and kill the server
     */
    public void close(){
        client.close();
        pythonWorld.descendants().forEach(s -> {
            try {
                Runtime.getRuntime().exec("taskkill /F /PID " + s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
}
