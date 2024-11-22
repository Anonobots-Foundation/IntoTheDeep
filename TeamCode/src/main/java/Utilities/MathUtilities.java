package Utilities;

public class MathUtilities
{
    public static double rangeClip(double val, double max, double min)
    {
        if(val > max)
            val = max;
        if (val < min)
            val = min;
        return val;
    }

    public static double calcRotate(double currentDegrees, double targetDegrees, double speedOfCorrection)
    {
        if(Math.abs(currentDegrees-targetDegrees) < 2)
            return 0;
        if(speedOfCorrection > 1)
            speedOfCorrection = 1;
        if(speedOfCorrection < 0)
            speedOfCorrection = 0;

        double degreeRight = targetDegrees - currentDegrees;
        if(degreeRight < 0)
        {
            degreeRight = 360 + degreeRight;
        }

        double degreeLeft = currentDegrees - targetDegrees;
        if(degreeLeft < 0)
        {
            degreeLeft = 360 + degreeLeft;
        }

        double rotatePower = 0;

        if(degreeRight < degreeLeft)
        {
            rotatePower = MathUtilities.rangeClip(Math.max(0.5, degreeRight/180 * speedOfCorrection),1,0.1);
        }
        else
        {
            rotatePower = MathUtilities.rangeClip(Math.max(0.5, degreeLeft/180 * speedOfCorrection),1,0.1) * -1;
        }

        return rotatePower;
    }

    public static double degreesFrom(double currentDegrees, double targetDegrees)
    {


        double degreeRight = targetDegrees - currentDegrees;
        if(degreeRight < 0)
        {
            degreeRight = 360 + degreeRight;
        }

        double degreeLeft = currentDegrees - targetDegrees;
        if(degreeLeft < 0)
        {
            degreeLeft = 360 + degreeLeft;
        }

        return Math.min(degreeLeft, degreeRight);
    }

    public static double degreesFromWithDirection(double currentDegrees, double targetDegrees)
    {
        double degreeRight = targetDegrees - currentDegrees;
        if(degreeRight < 0)
        {
            degreeRight = 360 + degreeRight;
        }

        double degreeLeft = currentDegrees - targetDegrees;
        if(degreeLeft < 0)
        {
            degreeLeft = 360 + degreeLeft;
        }

        if(degreeLeft < degreeRight)
            return degreeLeft*-1;
        else
            return degreeRight;
    }
}
