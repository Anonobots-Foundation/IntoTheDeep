package Utilities;

import java.security.PublicKey;
import java.util.List;

import Utilities.Enumerations;
import Utilities.PathPoint;
import Utilities.Point;

public class HolonomicDriveCommands {
    public static boolean followPath(List<PathPoint> pathPoints, boolean stopAtEnd)
    {
        boolean atEnd = true;
        for(PathPoint pathPoint : pathPoints)
        {
            if(!pathPoint.isDone)
            {
                driveToPoint(pathPoint);
                atEnd = false;
                break;
            }
        }
        if(atEnd)
        {
            if(stopAtEnd)
            {
                MovementVars.movementX = 0;
                MovementVars.movementY = 0;
                MovementVars.movementRotate = 0;
            }
            return true;
        }
        else
            return false;

    }
    public static void stopDrive()
    {
        MovementVars.movementY = 0;
        MovementVars.movementY = 0;
        MovementVars.movementRotate = 0;
    }

    private static void driveToPoint(PathPoint pathPoint)
    {

        double xDistance = pathPoint.x - RobotPosition.currentX;
        double yDistance = pathPoint.y - RobotPosition.currentY;

        double distanceToTarget = Math.hypot(xDistance,yDistance);
        double currentHeading = RobotPosition.currentHeading;
        if(distanceToTarget > pathPoint.allowableDistance)
        {
            double power;
            if(pathPoint.slowdownDistance != 0 && distanceToTarget < pathPoint.slowdownDistance)
            {
                power = pathPoint.endPower + (pathPoint.power - pathPoint.endPower)*(distanceToTarget/pathPoint.slowdownDistance);
            }
            else
                power = pathPoint.power;

            double movementAngle = Math.atan2(xDistance,yDistance);

            double currentHeadingRadians = Math.toRadians(currentHeading);
            double x1 = Math.sin(movementAngle); //B9*COS(B2)-B10*SIN(B2)
            double y1 = Math.cos(movementAngle); //B10*COS(B2)+B9*SIN(B2)

            double xPower = (x1*Math.cos(currentHeadingRadians) - y1*Math.sin(currentHeadingRadians)) * power;
            double yPower = (y1*Math.cos(currentHeadingRadians) + x1*Math.sin(currentHeadingRadians)) * power;


            double rotatePower = 0;
            double targetAngle = pathPoint.heading;
            //if a turn distance is set then we want to drive at the preferred angle unless we are withing the turn distance
            if(pathPoint.turnDistance != 0 && distanceToTarget <= pathPoint.turnDistance) {
                targetAngle = AngleUtilities.angleWrapDegrees(movementAngle + pathPoint.preferredOrientation);
            }

            if(Math.abs(AngleUtilities.degreesFrom(currentHeading, targetAngle))> 0.2)
            {
                double degreesFromTarget = AngleUtilities.degreesFrom(RobotPosition.currentHeading, targetAngle);
                if(Math.abs(degreesFromTarget) > 60 || !pathPoint.proportionalRotate )
                    rotatePower = Math.signum(degreesFromTarget);
                else
                    rotatePower = Math.max(Math.abs(degreesFromTarget) / 180, 0.11) * Math.signum(degreesFromTarget);
            }
            xPower *= pathPoint.xMultiple; //Give strafing more power

            MovementVars.movementX = xPower;
            MovementVars.movementY = yPower;
            MovementVars.movementRotate = rotatePower;
        }
        else
            pathPoint.isDone = true;

    }
    public static boolean turnToHeading(double targetHeading, double power, double slowdownDegrees, double allowableDegrees)
    {

        boolean atHeading = false;
        double currentHeading = RobotPosition.currentHeading;
        double rotatePower = 0;
        if(Math.abs(currentHeading- targetHeading) > allowableDegrees)
        {
            double degreesFromTarget = AngleUtilities.degreesFrom(RobotPosition.currentHeading, targetHeading);
            if(slowdownDegrees == 0 || Math.abs(degreesFromTarget) > slowdownDegrees  )
                rotatePower = power * Math.signum(degreesFromTarget);
            else
                rotatePower = Math.max(Math.abs(degreesFromTarget) / 180, 0.5) * Math.signum(degreesFromTarget)* power;
            if(Math.abs(rotatePower) < 0.2)
                rotatePower = 0.2*Math.signum(rotatePower);
            MovementVars.movementRotate = rotatePower;
        }
        else {
            atHeading = true;
            MovementVars.movementRotate = 0;
        }
        return atHeading;


    }

    public static boolean turnToHeading(double targetHeading, double power, double slowdownDegrees, double allowableDegrees, Enumerations.RotationDirection rotationDirection) {
        boolean atHeading = false;
        double currentHeading = RobotPosition.currentHeading;
        double rotatePower = 0;
        double rotationDirectionMultiple = 1;

        if(Math.abs(currentHeading- targetHeading) > allowableDegrees)
        {
            double degreesFromTarget = AngleUtilities.degreesFrom(RobotPosition.currentHeading, targetHeading);
            if(degreesFromTarget >= 90 ) {
                if(rotationDirection == Enumerations.RotationDirection.CCW)
                    rotationDirectionMultiple = -1;
            }
            else
                rotationDirectionMultiple = Math.signum(degreesFromTarget);

            if(slowdownDegrees == 0 || Math.abs(degreesFromTarget) > slowdownDegrees  )
                rotatePower = power * rotationDirectionMultiple;
            else
                rotatePower = Math.max(Math.abs(degreesFromTarget) / 180, 0.5) * rotationDirectionMultiple* power;
            if(Math.abs(rotatePower) < 0.2)
                rotatePower = 0.2*rotationDirectionMultiple;
            MovementVars.movementRotate = rotatePower;
        }
        else {
            atHeading = true;
            MovementVars.movementRotate = 0;
        }
        return atHeading;
    }

    public static double pathDistanceToEnd(List<PathPoint> pathPoints) {
        PathPoint pathPoint = pathPoints.get(pathPoints.size()-1);
        return distanceToPoint(new Point(pathPoint.x, pathPoint.y) );

    }

    public static double distanceToPoint(Point point) {
        double xDistance = point.x - RobotPosition.currentX;
        double yDistance = point.y - RobotPosition.currentY;

        return Math.hypot(xDistance,yDistance);
    }

    public static Location pathLocationPoint(Point point, double inchesFromEndPoint) {

        double xDistance = point.x - RobotPosition.currentX;
        double yDistance = point.y - RobotPosition.currentY;

        double scaleFactor = Math.hypot(xDistance, yDistance)/inchesFromEndPoint;

        double targetX = Math.abs(xDistance)/scaleFactor + point.x;
        double targetY = Math.abs(yDistance)/scaleFactor + point.y;
        double heading = Math.toDegrees(Math.atan2(xDistance,yDistance));
        if(heading < 0)
            heading += 360;
        return new Location(targetX,  targetY, heading);
    }

    public static Location pathLocationFromEnd(List<PathPoint> pathPoints, double inchesFromEndPoint) {
        PathPoint pathPoint = pathPoints.get(pathPoints.size() -1);
        return pathLocationPoint(new Point(pathPoint.x, pathPoint.y), inchesFromEndPoint);

    }
}
