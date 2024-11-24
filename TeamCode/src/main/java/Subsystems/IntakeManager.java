package Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import OpModes.Robot;
import Utilities.MathUtilities;
import Utilities.Timer;

public class IntakeManager {
    public enum IntakeExtensionState{
        HOME, //ext retracted,
        MOVING, //ext is moving
        MOVING_TO_POSITION, //ext is moving to specific position
        STOPPED //ext is stopped in a non HOME position
    }

    public enum IntakeDeploymentState {
        HOME, //mech in home position
        DEPLOYING, //mech is in the process of deploying
        READY, //mech is ready for grabbing
        RETRACTING //mech is retracting back to home
    }
    public enum IntakeGrabberState {
        OPEN, //grabber is in open state
        MOVING, //grabber is moving
        CLOSED //grabber is closed
    }
    public enum IntakeExtensionPositions {
        HOME,
        AUTO_SAMPLE_LEFT,
        AUTO_SAMPLE_MIDDLE,
        AUTO_SAMPLE_RIGHT,
        FULL
    }
    private Robot robot;

    private final int extensionMaxPosition = 1400;
    private final int extensionHomePosition = 0;
    private final int extensionAutoLeftPosition = 500;
    private final int extensionAutoMiddlePosition = 500;
    private final int extensionAutoRightPosition = 500;
    private  double deploymentServoIntakePosition = 0.76;
    private final double deploymentServoRestingPosition = 0.3;
    private final double intakeRotationServoIntakePosition = 0.25;
    private final double intakeRotationServoRestingPosition = 0.65;
    private final double grabbingServoClosedPosition = 0.30;
    private final double grabbingServoOpenPosition = 0.47;

    private final int deploymentTimeMs = 1000;
    private final int grabberTimerMs = 300;
    private final int foundObjectMs = 1000;
    private Timer deploymentTimer;
    private Timer grabberTimer;
    private Timer foundObjectTimer;
    private boolean autoSampleRetracting = false;
    private double targetPower = 0;
    public IntakeExtensionState intakeState;
    public IntakeDeploymentState deploymentState;
    public IntakeGrabberState grabberState;

    public IntakeManager(Robot robot) {
        //assign the global robot to the local property to access in this class
        this.robot = robot;

        intakeState = IntakeExtensionState.HOME;
    }
    public void moveToHome() {
        moveIntakeToPosition(IntakeExtensionPositions.HOME);
        retractIntake();
    }
    public void update() {

        double currentExtensionPosition = robot.intakeExtensionMotor.getCurrentPosition();
        if(autoSampleRetracting) {
            if(grabberState == IntakeGrabberState.CLOSED) {
                if(deploymentState != IntakeDeploymentState.RETRACTING) {
                    retractIntake();
                }
                if(deploymentState == IntakeDeploymentState.HOME ||
                        (deploymentState == IntakeDeploymentState.RETRACTING &&
                                deploymentTimer.elapsedTime()/2 > deploymentTimeMs)) {
                    //begin retracting extension
                    moveIntakeToPosition(IntakeExtensionPositions.HOME);
                }
            }
            if(intakeState == IntakeExtensionState.HOME)
                autoSampleRetracting = false;
        }
        if(intakeState == null)
            intakeState = IntakeExtensionState.HOME;
        if(grabberState == null)
            openGrabber();
        if(deploymentState == null)
            retractIntake();
        switch (intakeState) {
            case MOVING_TO_POSITION:
                if(Math.abs(currentExtensionPosition- robot.intakeExtensionMotor.getTargetPosition()) <= 20) {
                    if(robot.intakeExtensionMotor.getTargetPosition() == extensionHomePosition)
                        intakeState = IntakeExtensionState.HOME;
                    else
                        intakeState = IntakeExtensionState.STOPPED;
                }
                break;
            case HOME:

                if(deploymentState == IntakeDeploymentState.HOME &&
                        robot.deliveryManager.deliveryState == DeliveryManager.DeliveryState.HOME) {
                    openGrabber();
                }
                break;
            case MOVING:
                double power =0;
                robot.intakeExtensionMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                robot.intakeExtensionMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                //if(targetPower < 0.4 && targetPower > -0.4)
                //    power = 0;
                power = targetPower;
                double currentPos = robot.intakeExtensionMotor.getCurrentPosition();
                if((power > 0 && currentPos >= extensionMaxPosition) ||
                        (power < 0 && currentPos <= extensionHomePosition)) {
                    power = 0;
                }

                robot.intakeExtensionMotor.setPower(power);
                if (currentPos <= 10 && intakeState == IntakeExtensionState.MOVING) {
                    robot.intakeExtensionMotor.setPower(0);
                    intakeState = IntakeExtensionState.HOME;
                } else if (targetPower == 0) {
                    intakeState = IntakeExtensionState.STOPPED;
                } else
                    intakeState = IntakeExtensionState.MOVING;
                break;
            default:
                break;
        }
        if(grabberState == IntakeGrabberState.MOVING && grabberTimer.elapsedTime() >= grabberTimerMs){
            if(robot.intakeGrabberServo.getPosition() == grabbingServoOpenPosition)
                grabberState = IntakeGrabberState.OPEN;
            else
                grabberState = IntakeGrabberState.CLOSED;
            grabberTimer = null;
        }

        switch(deploymentState) {
            case DEPLOYING:
                if(deploymentTimer.elapsedTime() >= deploymentTimeMs)
                    deploymentState = IntakeDeploymentState.READY;
                break;
            case RETRACTING:
                if(deploymentTimer.elapsedTime() >= deploymentTimeMs)
                    deploymentState = IntakeDeploymentState.HOME;
                break;
            default:
                break;
        }

        if(intakeState == IntakeExtensionState.HOME &&
            deploymentState == IntakeDeploymentState.HOME &&
            robot.deliveryManager.bucketState == DeliveryManager.BucketState.HOME) {
            openGrabber();
        }
        if(deploymentState == IntakeDeploymentState.READY) {
            boolean foundSample = false;

            if(robot.intakeObjectDetectionSensor.getDistance(DistanceUnit.INCH) < 2.0) {
                int b = robot.intakeObjectDetectionSensor.blue();
                int r = robot.intakeObjectDetectionSensor.red();
                int g = robot.intakeObjectDetectionSensor.green();

                if(r > 100 || b > 200) {
                    if (b > r && b > g) {
                        //found blue
                        if(Robot.alliance == Robot.AllianceColor.BLUE)
                            foundSample = true;
                    }
                    else if( g > r)
                        //neutral sample
                        foundSample = true;
                    else
                    {
                        //found red
                        if(Robot.alliance == Robot.AllianceColor.RED)
                            foundSample = true;
                    }
                }
            }
            if(foundSample) {
                if(foundObjectTimer == null) {
                    foundObjectTimer = new Timer();
                    foundObjectTimer.start();
                }
                if(foundObjectTimer != null && foundObjectTimer.elapsedTime() > foundObjectMs) {
                    foundObjectTimer = null;
                    closeGrabber();
                    autoSampleRetracting = true;
                }
            }
        }

        targetPower = 0;
    }

    public void moveIntakeExtension(double power) {
        targetPower = power;
        intakeState = IntakeExtensionState.MOVING;
    }

    public void adjustDeploymentHeight(double adjustment) {
        deploymentServoIntakePosition += adjustment;
        robot.intakeDeploymentServo.setPosition(deploymentServoIntakePosition);
    }

    public void moveIntakeToPosition(IntakeExtensionPositions position) {
        int targetPosition = 0;
        switch (position) {
            case HOME:
                targetPosition = extensionHomePosition;
                break;
            case AUTO_SAMPLE_LEFT:
                targetPosition = extensionAutoLeftPosition;
                break;
            case AUTO_SAMPLE_MIDDLE:
                targetPosition = extensionAutoMiddlePosition;
                break;
            case AUTO_SAMPLE_RIGHT:
                targetPosition = extensionAutoRightPosition;
                break;
            case FULL:
                targetPosition = extensionMaxPosition;
                break;
        }
        moveExtensionToPosition(targetPosition);
        intakeState = IntakeExtensionState.MOVING_TO_POSITION;
    }

    public void deployIntake() {
        robot.intakeRotationServo.setPosition(intakeRotationServoIntakePosition);
        robot.intakeDeploymentServo.setPosition(deploymentServoIntakePosition);
        openGrabber();
        deploymentTimer = new Timer();
        deploymentTimer.start();
        deploymentState = IntakeDeploymentState.DEPLOYING;
    }
    public void retractIntake() {
        robot.intakeRotationServo.setPosition(intakeRotationServoRestingPosition);
        robot.intakeDeploymentServo.setPosition(deploymentServoRestingPosition);
        deploymentTimer = new Timer();
        deploymentTimer.start();
        deploymentState = IntakeDeploymentState.RETRACTING;
    }
    public void openGrabber() {
        robot.intakeGrabberServo.setPosition(grabbingServoOpenPosition);
        grabberTimer = new Timer();
        grabberTimer.start();
        grabberState = IntakeGrabberState.MOVING;
    }
    public void closeGrabber() {
        robot.intakeGrabberServo.setPosition(grabbingServoClosedPosition);
        grabberTimer = new Timer();
        grabberTimer.start();
        grabberState = IntakeGrabberState.MOVING;
    }
    public void toggleGrabber() {
        if(grabberState == IntakeGrabberState.OPEN)
            closeGrabber();
        else if(grabberState == IntakeGrabberState.CLOSED)
            openGrabber();
    }
    private void moveExtensionToPosition(int targetPosition) {
        robot.intakeExtensionMotor.setTargetPosition(targetPosition);
        robot.intakeExtensionMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.intakeExtensionMotor.setPower(1);

    }


}
