package CarSimulator;

class Controls{
    private static final double maxSteeringAngle = 90;
    private static final double maxTargetVelocity = 10;

    private double steeringAngle;
    private double targetVelocity;

    Controls(){}

    Controls(double steeringAngle, double targetVelocity){
        setSteeringAngle(steeringAngle);
        setTargetVelocity(targetVelocity);
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
        } else if(targetVelocity < 0){
            this.targetVelocity = 0;
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
