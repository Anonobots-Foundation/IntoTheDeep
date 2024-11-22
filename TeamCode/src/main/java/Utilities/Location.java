package Utilities;

import java.util.logging.XMLFormatter;

public class Location
{
    public double x;
    public double y;
    public double heading;

    public Location(double x, double y, double heading)
    {
        this.x = x;
        this.y = y;
        this.heading = heading;
    }
    public void setLocation(double x, double y, double heading) {
        this.x = x;
        this.y = y;
        this.heading = heading;
    }

}