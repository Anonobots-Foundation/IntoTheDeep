package Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import OpModes.Robot;
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

    private final int extensionMaxPosition = 1800;
    private final int extensionHomePosition = 0;
    private final int extensionAutoLeftPosition = 500;
    private final int extensionAutoMiddlePosition = 500;
    private final int extensionAutoRightPosition = 500;
    private final double deploymentServoIntakePosition = 0.73;
    private final double deploymentServoRestingPosition = 0.25;
    private final double intakeRotationServoIntakePosition = 0.15;
    private final double intakeRotationServoRestingPosition = 0.65;
    private final double grabbingServoClosedPosition = 0.30;
    private final double grabbingServoOpenPosition = 0.47;

    private final int deploymentTimeMs = 1000;
    private final int grabberTimerMs = 300;

    private Timer deploymentTimer;
    private Timer grabberTimer;
    private boolean autoSampleRetracting = false;

    public IntakeExtensionState intakeState;
    public IntakeDeploymentState deploymentState;
    public IntakeGrabberState grabberState;

    public IntakeManager(Robot robot) {
        //assign the global robot to the local property to access in this class
        this.robot = robot;
        intakeState = IntakeExtensionState.HOME;
    }

    public void update() {
        double currentExtensionPosition = robot.intakeExtensionMotor.getCurrentPosition();
        if(autoSampleRetracting) {
            if(grabberState == IntakeGrabberState.CLOSED) {
                if(deploymentState == IntakeDeploymentState.READY) {
                    retractIntake();
                }
                if(deploymentState == IntakeDeploymentState.RETRACTING && deploymentTimer.elapsedTime()/2 > deploymentTimeMs) {
                    //begin retracting extension
                    moveIntakeToPosition(IntakeExtensionPositions.HOME);
                }
            }
            if(intakeState == IntakeExtensionState.HOME)
                autoSampleRetracting = false;
        }
        switch (intakeState) {
            case MOVING_TO_POSITION:
                if(Math.abs( currentExtensionPosition - robot.intakeExtensionMotor.getTargetPosition()) <= robot.intakeExtensionMotor.getTargetPositionTolerance()) {
                    if(robot.intakeExtensionMotor.getTargetPosition() == extensionHomePosition)
                        intakeState = IntakeExtensionState.HOME;
                    else
                        intakeState = IntakeExtensionState.STOPPED;
                }
                break;
            case HOME:
                //TODO: add check to see if delivery manager is in a home state
                if(deploymentState == IntakeDeploymentState.HOME) {
                    openGrabber();
                }
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
        //TODO: add check to see if delivery manager is in a home state
        if(intakeState == IntakeExtensionState.HOME &&
            deploymentState == IntakeDeploymentState.HOME) {
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
                closeGrabber();
                autoSampleRetracting = true;
            }
        }


    }

    public void moveIntakeExtension(double power) {
        robot.intakeExtensionMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        double currentPos = robot.intakeExtensionMotor.getCurrentPosition();
        if((power > 0 && currentPos >= extensionMaxPosition) ||
            power < 0 && currentPos <= extensionHomePosition) {
            robot.intakeExtensionMotor.setPower(0);
        }
        robot.intakeExtensionMotor.setPower(power);
        if(power == 0) {
            intakeState = IntakeExtensionState.STOPPED;
        }
        else
         intakeState = IntakeExtensionState.MOVING;
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
        robot.intakeExtensionMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.intakeExtensionMotor.setTargetPositionTolerance(10);
        robot.intakeExtensionMotor.setTargetPosition(targetPosition);
        robot.intakeExtensionMotor.setPower(1);

    }


}
