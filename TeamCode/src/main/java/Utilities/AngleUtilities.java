package Utilities;

public class AngleUtilities {

    public static double angleWrapDegrees(double degrees) {
        degrees = degrees % 360;
        if (degrees < 0)
            degrees += 360;
        return degrees;
    }

    public static double angleWrapDegreesInverted(double degrees) {
        degrees *= -1;
        return angleWrapDegrees(degrees);
    }
    public static double degreesFrom(double currentAngle, double targetAngle) {
        double degreesRight = angleWrapDegrees(targetAngle - currentAngle);
        double degreesLeft = angleWrapDegrees(currentAngle - targetAngle);
        double degreesFromTarget = 0;
        if(degreesLeft < degreesRight) {
            degreesFromTarget = degreesLeft * -1;
        }
        else
            degreesFromTarget = degreesRight;

        return degreesFromTarget;
    }
}
