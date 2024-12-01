package Utilities;

import Utilities.Location;

public class PathPoint
{
    public double x;
    public double y;
    public double heading;
    public double power;
    public double xMultiple;
    public double slowdownDistance;
    public double endPower;
    public double allowableDistance;
    public boolean isDone = false;
    public boolean proportionalRotate;
    public double preferredOrientation = 0;
    public double turnDistance = 0;

    public PathPoint(double x, double y, double heading, double power, double xMultiple, double slowdownDistance, double endPower, double allowableDistance, boolean proportionalRotate, double preferredOrientation, double turnDistance )
    {
        this.x = x;
        this.y = y;
        this.heading = heading;
        this.power = power;
        this.xMultiple = xMultiple;
        this.slowdownDistance = slowdownDistance;
        this.endPower = endPower;
        this.allowableDistance = allowableDistance;
        this.proportionalRotate = proportionalRotate;
        this.preferredOrientation = preferredOrientation;
        this.turnDistance = turnDistance;
    }


    public PathPoint(double x, double y, double heading, double power, double xMultiple, double slowdownDistance, double endPower, double allowableDistance, boolean proportionalRotate )
    {
        this.x = x;
        this.y = y;
        this.heading = heading;
        this.power = power;
        this.xMultiple = xMultiple;
        this.slowdownDistance = slowdownDistance;
        this.endPower = endPower;
        this.allowableDistance = allowableDistance;
        this.proportionalRotate = proportionalRotate;
    }

    public PathPoint(Location location, double power, double xMultiple, double slowdownDistance, double endPower, double allowableDistance, boolean proportionalRotate) {
        this.x = location.x;
        this.y = location.y;
        this.heading = location.heading;
        this.power = power;
        this.xMultiple = xMultiple;
        this.slowdownDistance = slowdownDistance;
        this.endPower = endPower;
        this.allowableDistance = allowableDistance;
        this.proportionalRotate = proportionalRotate;
    }

    
}
