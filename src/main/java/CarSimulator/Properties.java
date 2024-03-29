package CarSimulator;

import java.util.Arrays;

public class Properties{
    private static final double finity = 1e20;
    private double[] lidarDistances;
    private long lidarHalfApertureAngle;
    private boolean isOnTrack;
    private double progress;
    private double lapTime;
    private boolean collided;

    Properties(){}

    /**
     * @return Array of lidardistances
     */
    public double[] getLidarDistances(){
        return lidarDistances;
    }
    
    /**
     * @return Lidar half aperture angle
     */
    public long getLidarHalfApertureAngle(){
        return lidarHalfApertureAngle;
    }

    /**
     * @return Whether the car is on the track or not
     */
    public boolean getIsOnTrack(){
        return isOnTrack;
    }

    /**
     * @return The progress of the car on the track in percentage
     */
    public double getProgress() {
        return progress;
    }

    /**
     * @return Elapsed simulation time
     */
    public double getLapTime() {
        return lapTime;
    }

    /**
     * @return If the car is colliding
     */
    public boolean getCollided() {
        return collided;
    }

    /**
     * Get the distance of the nearest cones within rays of equal size
     * @param outputVector The amount of rays
     * @return Array with the distance of the nearest cones per ray
     */
    public double[] getRay(int outputVector){
        int lidarAngle = 2 * (int)lidarHalfApertureAngle;
        int sublist = lidarAngle / outputVector;
        double[][] rays = new double[outputVector][sublist]; 

        for (int i = 0; i < outputVector; i++) {
            for (int j = 0; j < sublist; j++) {
                rays[i][j] = lidarDistances[(i*sublist)+j];
            }
        }

        for (double[] ds : rays) {
            Arrays.sort(ds);
        }

        double[] tempRays = new double[outputVector];
        for (int k = 0; k < rays.length; k++) {
            tempRays[k] = rays[k][0];
        }

        return tempRays;
    }
    
    /**
     * @return Array with the distance and angle to the 4 nearest cones sorted per cone
     */
    public double[] getNearestCones(int amountOfNearestCones){
        double[] nearestCones = new double[amountOfNearestCones * 2];
        int amountOfLidars = lidarDistances.length;

        double[] distanceOfNearestCones = new double[amountOfNearestCones];
        for(int i = 0; i < amountOfNearestCones; i++){
            distanceOfNearestCones[i] = finity;
        }
        double[] angleOfNearestCones = new double[amountOfNearestCones];

        // Get the nearest cone distances and angles
        for(int i = 0; i < amountOfLidars; i++){
            double currentLidarDistance = lidarDistances[i];
            for(int j = 0; j < amountOfNearestCones; j++){
                if(currentLidarDistance < distanceOfNearestCones[j]){
                    for(int k = amountOfNearestCones - 1; k > j; k--){
                        distanceOfNearestCones[k] = distanceOfNearestCones[k-1];
                        angleOfNearestCones[k] = angleOfNearestCones[k-1];
                    }
                    distanceOfNearestCones[j] = currentLidarDistance;
                    angleOfNearestCones[j] = (i + 1) * ((lidarHalfApertureAngle * 2) / amountOfLidars) - lidarHalfApertureAngle;
                    break;
                }
            }
        }

        // Sort the distance and angles arrays on the angle from left to right
        for(int i = 0; i < angleOfNearestCones.length; i++){
            for(int j = 0; j < angleOfNearestCones.length; j++){
                double tempAngle = 0;
                double tempDistance = 0;
                if(angleOfNearestCones[i] < angleOfNearestCones[j]){
                    tempAngle = angleOfNearestCones[i];
                    angleOfNearestCones[i] = angleOfNearestCones[j];
                    angleOfNearestCones[j] = tempAngle;
                    tempDistance = distanceOfNearestCones[i];
                    distanceOfNearestCones[i] = distanceOfNearestCones[j];
                    distanceOfNearestCones[j] = tempDistance;
                }
            }
        }

        // Combine the distance and angle arrays
        for(int i = 0; i < nearestCones.length; i++){
            if(i % 2 == 0){
                nearestCones[i] = distanceOfNearestCones[i/2];
            } else{
                nearestCones[i] = angleOfNearestCones[i/2];
            }
        }
        return nearestCones;
    }
}
