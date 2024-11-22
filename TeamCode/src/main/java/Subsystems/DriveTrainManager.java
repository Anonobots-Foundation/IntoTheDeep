package Subsystems;

import OpModes.Robot;
import Utilities.MathUtilities;
import Utilities.MovementVars;

public class DriveTrainManager {
    private Robot robot;

    private int NUMBEROFMOTORS = 4;
    private int Left_Front = 0;
    private int Right_Front = 1;
    private int Left_Back = 2;
    private int Right_Back = 3;


    private double lastWheelSpeeds[];
    private double wheelSpeeds[];
    private double speedStep = 0.3;
    private double speedStepDown = 0.6;

    public DriveTrainManager(Robot parentRobot) {
        robot = parentRobot;
        lastWheelSpeeds = new double[]{0,0,0,0};
        wheelSpeeds = new double[] {0,0,0,0};
    }

    public void update() {
        double robotMovementX = MovementVars.movementX;
        double robotMovementY = MovementVars.movementY;
        /*
        if(fieldOriented)
        {
            double theta = Math.toRadians(RobotPosition.currentHeading);

            Point newRobotMovement = translateRobotMovement(robotMovementX, robotMovementY, theta);

            robotMovementX = newRobotMovement.x;
            robotMovementY = newRobotMovement.y;
        }
*/
        double v = Math.hypot(robotMovementX, robotMovementY);
        //double robotAngle = Math.atan2(robotMovementY, robotMovementX) - Math.PI / 4;

        //double rotationSpeed = MathUtilities.rangeClip(MovementVars.movementRotate,maxRotationSpeed,-maxRotationSpeed);
        double rotationSpeed = MovementVars.movementRotate;
        wheelSpeeds[Left_Front] = robotMovementY + robotMovementX + rotationSpeed;
        wheelSpeeds[Right_Front] = robotMovementY - robotMovementX - rotationSpeed;
        wheelSpeeds[Left_Back] = robotMovementY - robotMovementX + rotationSpeed;
        wheelSpeeds[Right_Back] = robotMovementY + robotMovementX - rotationSpeed;

        normalize(wheelSpeeds, v);
        robot.frontLeftMotor.setPower(wheelSpeeds[Left_Front]);
        robot.frontRightMotor.setPower(wheelSpeeds[Right_Front]);
        robot.backLeftMotor.setPower(wheelSpeeds[Left_Back]);
        robot.backRightMotor.setPower(wheelSpeeds[Right_Back]);
    }

    private void normalize(double wheelSpeeds[], double targetSpeed)
    {
        double maxMagnitude = Math.abs(wheelSpeeds[0]);
        for (int i = 1; i < NUMBEROFMOTORS; i++)
        {
            double temp = Math.abs(wheelSpeeds[i]);
            if (maxMagnitude < temp)
                maxMagnitude = temp;
        }

        if (maxMagnitude > 1.0)
        {
            for (int i = 0; i < NUMBEROFMOTORS; i++)
            {
                wheelSpeeds[i] = (wheelSpeeds[i] / maxMagnitude);
            }
        }
        else if (maxMagnitude < targetSpeed)
        {
            for (int i = 0; i < NUMBEROFMOTORS; i++)
            {
                wheelSpeeds[i] = (wheelSpeeds[i] * targetSpeed / maxMagnitude);
            }
        }


        for(int i=0; i < NUMBEROFMOTORS; i++)
        {
            wheelSpeeds[i] = wheelSpeeds[i]*MovementVars.powerMultiplier;
            if(MovementVars.useRamp)
            {
                if(lastWheelSpeeds[i] !=0 && (Math.signum(wheelSpeeds[i]) != Math.signum(lastWheelSpeeds[i])))
                    wheelSpeeds[i] = 0;
                if (wheelSpeeds[i] != 0 && Math.abs(wheelSpeeds[i]) > Math.abs(lastWheelSpeeds[i]))
                {
                    if(wheelSpeeds[i] > 0)
                        wheelSpeeds[i] = Math.min(wheelSpeeds[i],lastWheelSpeeds[i] + speedStep);
                    else
                        wheelSpeeds[i] = Math.max(wheelSpeeds[i],lastWheelSpeeds[i] - speedStepDown);
                }
                lastWheelSpeeds[i] = wheelSpeeds[i];
            }

        }
    }
}
