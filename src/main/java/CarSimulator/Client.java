package CarSimulator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class Client{
    /**
     * The max length of the buffer which is used to send and receive to and from the server
     */
    private static final int maxMessageLength = 2048;
    private Socket socket;
    private DataInputStream inStream;
    private DataOutputStream outStream;

    public Client(int socketPort){
        try{
            connect(socketPort);
        } catch(Exception e){
            System.out.println("There is no server running at socket port: " + socketPort);
        }
    }

    /**
     * Connect the socket and the corresponding streams
     * @throws IOException
     */
    public void connect(int socketPort) throws IOException{
        socket = new Socket("localhost", socketPort);
        inStream = new DataInputStream(socket.getInputStream());
        outStream = new DataOutputStream(socket.getOutputStream());
    }

    /**
     * Receive a message from the socket server
     * @return The received string without leading of trailing whitespace
     */
    public String recv(){
        byte[] inBytes = new byte [maxMessageLength];
        String inString = "";
        try{
            inStream.readFully(inBytes, 0, maxMessageLength);
            inString = new String(inBytes, "ASCII");
        } catch(Exception e){
            e.printStackTrace();
        }
        return inString.trim();
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
