package CarSimulator;

public class Controls{
    public static final double maxSteeringAngle = 45;
    public static final double minTargetVelocity = 0;
    public static final double maxTargetVelocity = 10;

    private double steeringAngle;
    private double targetVelocity;

    Controls(){}

    Controls(double steeringAngle, double targetVelocity){
        setNormalizedSteeringAngle(steeringAngle);
        setTargetVelocity(targetVelocity);
    }

    public void setNormalizedSteeringAngle(double normalizedSteeringAngle) {
        double temPormalizedSteeringAngle = (normalizedSteeringAngle - .5) * 2 * maxSteeringAngle;
        // System.out.println(temPormalizedSteeringAngle);
        setSteeringAngle(temPormalizedSteeringAngle);

    }

    /**
     * 
     * @param steeringAngle Steering angle to set in degrees.
     * Angles higher than {@value #maxSteeringAngle}, will be set to {@value #maxSteeringAngle}.
     * Angles lower than -{@value #maxSteeringAngle}, will be set to -{@value #maxSteeringAngle}.
     */
    public void setSteeringAngle(double steeringAngle){
        if(steeringAngle > maxSteeringAngle){
            this.steeringAngle = maxSteeringAngle;
        } else if(steeringAngle < -maxSteeringAngle){
            this.steeringAngle = -maxSteeringAngle;
        } else{
            this.steeringAngle = steeringAngle;
        }
    }

    /**
     * 
     * @return Current steering angle in degrees
     */
    public double getSteeringAngle(){
        return steeringAngle;
    }

    /**
     * 
     * @param targetVelocity Target velocity of the car
     */
    public void setTargetVelocity(double targetVelocity){
        if(targetVelocity > maxTargetVelocity){
            this.targetVelocity = maxTargetVelocity;
        } else if(targetVelocity < minTargetVelocity){
            this.targetVelocity = minTargetVelocity;
        } else{
            this.targetVelocity = targetVelocity;
        }
    }
    

    /**
     * 
     * @return Current target velocity
     */
    public double getTargetVelocity(){
        return targetVelocity;
    }
}
