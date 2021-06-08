package CarSimulator;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;

import org.junit.BeforeClass;
import org.junit.Test;

public class PropertiesTest {
    static Properties properties;

    @BeforeClass
    public static void initializeProperties(){
        String incomingString = "{\"lidarDistances\": [1e+20, 7.1029, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 6.5159, 1e+20, 1e+20, 1e+20, 8.4964, 1e+20, 1e+20, 1e+20, 1e+20, 10.304, 8.0743, 12.3452, 1e+20, 1e+20, 9.9589, 12.0171, 1e+20, 1e+20, 1e+20, 12.8513, 1e+20, 1e+20, 1e+20, 12.6555, 13.6439, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 12.0074, 1e+20, 10.9972, 1e+20, 1e+20, 9.5106, 7.5145, 5.5212, 1e+20, 1e+20, 1e+20, 9.6156, 1e+20, 7.6468, 1e+20, 1e+20, 1e+20, 5.7, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 5.0243, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 2.5013, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 4.0302, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 5.2181, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1.1235, 4.47, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 6.1002, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 5.3122, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20], \"lidarHalfApertureAngle\": 60, \"isOnTrack\": true, \"progress\": \"0.0\", \"lapTime\": \"1.49\", \"collided\": false}";
        Gson gson = new Gson();
        properties = gson.fromJson(incomingString, Properties.class);
    }

    @Test
    public void getLidarDistancesTest(){
        double[] expected = {1e+20, 7.1029, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 6.5159, 1e+20, 1e+20, 1e+20, 8.4964, 1e+20, 1e+20, 1e+20, 1e+20, 10.304, 8.0743, 12.3452, 1e+20, 1e+20, 9.9589, 12.0171, 1e+20, 1e+20, 1e+20, 12.8513, 1e+20, 1e+20, 1e+20, 12.6555, 13.6439, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 12.0074, 1e+20, 10.9972, 1e+20, 1e+20, 9.5106, 7.5145, 5.5212, 1e+20, 1e+20, 1e+20, 9.6156, 1e+20, 7.6468, 1e+20, 1e+20, 1e+20, 5.7, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 5.0243, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 2.5013, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 4.0302, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 5.2181, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1.1235, 4.47, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 6.1002, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 5.3122, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20, 1e+20};
        double[] actual = properties.getLidarDistances();
        assertArrayEquals(expected, actual, 0.001);
    }

    @Test
    public void getLidarHalfApertureAngleTest(){
        long expected = 60;
        long actual = properties.getLidarHalfApertureAngle();
        assertEquals(expected, actual);
    }

    @Test
    public void getIsOnTrackTest(){
        boolean expected = true;
        boolean actual = properties.getIsOnTrack();
        assertEquals(expected, actual);
    }

    @Test
    public void getProgressTest(){
        double expected = 0.0;
        double actual = properties.getProgress();
        assertEquals(expected, actual, 0.001);
    }

    @Test
    public void getLapTime(){
        double expected = 1.49;
        double actual = properties.getLapTime();
        assertEquals(expected, actual, 0.001);
    }

    @Test
    public void getCollidedTest(){
        boolean expected = false;
        boolean actual = properties.getCollided();
        assertEquals(expected, actual);
    }

    @Test
    public void getNearestCones(){
        double[] expected = {2.5013, 11, 4.0302, 18, 1.1235, 37, 4.47, 38};
        double[] actual = properties.getNearestCones();
        assertArrayEquals(expected, actual, 0.001);
    }
}
