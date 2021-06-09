package CarSimulator;

import java.io.IOException;
import java.net.SocketTimeoutException;

import com.google.gson.Gson;

public class Car{
    private static final long waitingTimeBeforeConnect = 500;
    private int socketPortCounter = 50000;

    private Gson gson;

    private Process pythonWorld;
    private Client client;

    private Properties properties;
    private Controls controls;

    /**
     * Set up a client connection to be able to use the car
     */
    public Car(int id){
        gson = new Gson();
        properties = new Properties();
        controls = new Controls();

        socketPortCounter += id;

        try{
            pythonWorld = Runtime.getRuntime().exec("cmd /c activate Tinlab_opdracht_4 && start pythonServer.bat " + socketPortCounter);
            Thread.sleep(waitingTimeBeforeConnect);
        } catch(Exception e){
            e.printStackTrace();
        }
        client = new Client(socketPortCounter);

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
     * Reconnect when a SocketTimeoutException is thrown
     * Close the program when an IOException is thrown
     * @return properties object
     */
    public Properties recvProperties(){
        String incomingString = "";
        boolean received = false;
        while(!received){
            try{
                incomingString = client.recv();
                received = true;
            } catch(SocketTimeoutException e){
                client.connect();
            } catch(IOException e){
                System.out.println("Car: recvProperties ERROR");   
                close();

            }
        }
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