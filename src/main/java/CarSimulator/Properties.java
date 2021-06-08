package CarSimulator;

public class Properties{
    private double[] lidarDistances;
    private long lidarHalfApertureAngle;
    private boolean isOnTrack;
    private double progress;
    private double lapTime;
    private boolean collided;

    Properties(){}

    /**
     * 
     * @return Array of lidardistances
     */
    public double[] getLidarDistances(){
        return lidarDistances;
    }
    
    /**
     * 
     * @return Lidar half aperture angle
     */
    public long getLidarHalfApertureAngle(){
        return lidarHalfApertureAngle;
    }

    /**
     * 
     * @return whether the car is on the track or not
     */
    public boolean getIsOnTrack(){
        return isOnTrack;
    }

    /**
     * 
     * @return the progress of the car on the track in percentage
     */
    public double getProgress() {
        return progress;
    }

    /**
     * 
     * @return elapsed simulation time
     */
    public double getLapTime() {
        return lapTime;
    }

    /**
     * 
     * @return if the car is colliding
     */
    public boolean getCollided() {
        return collided;
    }

    /**
     * 
     * @return Array with the distance and angle to the 4 nearest cones sorted per cone
     */
    public double[] getNearestCones(){
        double[] nearestCones = new double[8];
        int amountOfLidars = lidarDistances.length;

        double[] distanceOfNearestCones = {1e20, 1e20, 1e20, 1e20};
        double[] angleOfNearestCones = new double[4];

        // Get the 4 nearest cone distances and angles
        for(int i = 0; i < amountOfLidars; i++){
            double currentLidarDistance = lidarDistances[i];
            if(currentLidarDistance < distanceOfNearestCones[0]){
                distanceOfNearestCones[3] = distanceOfNearestCones[2];
                distanceOfNearestCones[2] = distanceOfNearestCones[1];
                distanceOfNearestCones[1] = distanceOfNearestCones[0];
                distanceOfNearestCones[0] = currentLidarDistance;
                angleOfNearestCones[3] = angleOfNearestCones[2];
                angleOfNearestCones[2] = angleOfNearestCones[1];
                angleOfNearestCones[1] = angleOfNearestCones[0];
                angleOfNearestCones[0] = (i + 1) * ((lidarHalfApertureAngle * 2) / amountOfLidars) - lidarHalfApertureAngle;
            } else if(currentLidarDistance < distanceOfNearestCones[1]){
                distanceOfNearestCones[3] = distanceOfNearestCones[2];
                distanceOfNearestCones[2] = distanceOfNearestCones[1];
                distanceOfNearestCones[1] = currentLidarDistance;
                angleOfNearestCones[3] = angleOfNearestCones[2];
                angleOfNearestCones[2] = angleOfNearestCones[1];
                angleOfNearestCones[1] = (i + 1) * ((lidarHalfApertureAngle * 2) / amountOfLidars) - lidarHalfApertureAngle;
            } else if(currentLidarDistance < distanceOfNearestCones[2]){
                distanceOfNearestCones[3] = distanceOfNearestCones[2];
                distanceOfNearestCones[2] = currentLidarDistance;
                angleOfNearestCones[3] = angleOfNearestCones[2];
                angleOfNearestCones[2] = (i + 1) * ((lidarHalfApertureAngle * 2) / amountOfLidars) - lidarHalfApertureAngle;
            } else if(currentLidarDistance < distanceOfNearestCones[3]){
                distanceOfNearestCones[3] = currentLidarDistance;
                angleOfNearestCones[3] = (i + 1) * ((lidarHalfApertureAngle * 2) / amountOfLidars) - lidarHalfApertureAngle;
            }
        }

        // Sort the distance and angles arrays on the angle from left to right
        for (int i = 0; i < angleOfNearestCones.length; i++){
            for (int j = 0; j < angleOfNearestCones.length; j++){
                double temp = 0;
                if(angleOfNearestCones[i] < angleOfNearestCones[j]){
                    temp = angleOfNearestCones[i];
                    angleOfNearestCones[i] = angleOfNearestCones[j];
                    angleOfNearestCones[j] = temp;
                    temp = distanceOfNearestCones[i];
                    distanceOfNearestCones[i] = distanceOfNearestCones[j];
                    distanceOfNearestCones[j] = temp;
                }
            }
        }

        // Combine the distance and angle arrays
        for (int i = 0; i < nearestCones.length; i++) {
            if(i % 2 == 0){
                nearestCones[i] = distanceOfNearestCones[i/2];
            } else{
                nearestCones[i] = angleOfNearestCones[i/2];
            }
        }
        return nearestCones;
    }
}
