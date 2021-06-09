package CarSimulator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

class Client{
    /**
     * The max length of the buffer which is used to send and receive to and from the server
     */
    private static final int maxMessageLength = 2048;
    private static final int socketTimeout = 100;

    private int socketPort;
    private Socket socket;
    private DataInputStream inStream;
    private DataOutputStream outStream;

    public Client(int socketPort){
        this.socketPort = socketPort;
        boolean connected = false;
        long startTime = System.currentTimeMillis();
        while(!connected){
            connected = connect();
        }
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Connected to " + socket.getInetAddress() + ":" + socketPort + " in " + duration);
    }

    /**
     * Connect the socket and the corresponding streams
     * @return true when connected, false when an IOException occured
     */
    public boolean connect(){
        try{
            socket = new Socket("localhost", socketPort);
            socket.setSoTimeout(socketTimeout);
            inStream = new DataInputStream(socket.getInputStream());
            outStream = new DataOutputStream(socket.getOutputStream());
            return true;
        } catch (IOException e){
            // System.out.println("Not able to connect to socket on port " + socketPort + "...");
            return false;
        }
    }

    /**
     * Receive a message from the socket server
     * @return The received string without leading of trailing whitespace
     * @throws SocketTimeoutException When the socket is not connected right to the server
     * @throws IOException When there is no working server anymore
     */
    public String recv() throws SocketTimeoutException, IOException{
        byte[] inBytes = new byte [maxMessageLength];
        inStream.readFully(inBytes, 0, maxMessageLength);
        return (new String(inBytes, "ASCII")).trim();
    }

    /**
     * Send the given message to the socket server
     * @param message The message which is sent to the socket server with a maximum of {@value #maxMessageLength} bytes
     */
    public void send(String message){
        while (message.length() < maxMessageLength) {
            message += " ";
        }
        try{
            byte[] sendBytes = message.getBytes("ASCII");
            outStream.write(sendBytes);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Close all connections to the server
     */
    public void close(){
        try{
            outStream.close();
            inStream.close();
            socket.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}