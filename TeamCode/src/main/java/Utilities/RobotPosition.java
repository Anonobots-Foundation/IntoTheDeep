package Utilities;

import java.util.logging.XMLFormatter;

import Utilities.Point;

public class RobotPosition {
    public static double currentHeading;
    public static double currentX;
    public static double currentY;
    public static double speedometer;
    public static double cummulativeAngle;
    public static String confidence;

    public static void initialize(double x, double y, double heading)
    {
        currentX = x;
        currentY = y;
        currentHeading = heading;
        cummulativeAngle = 0;

    }

    public static void setCurrentHeading(double heading)
    {
        currentHeading = heading;
    }

    public static void initialize(Location location) {
        currentX = location.x;
        currentY = location.y;
        currentHeading = location.heading;
    }

    public static Location toLocation() {
        return new Location(currentX, currentY, currentHeading);
    }
    public static Point toPoint() {
        return new Point(currentX, currentY);
    }
    public static void setConfidence(String currentConfidence) {
        confidence = currentConfidence;
    }
}
