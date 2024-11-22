package Subsystems;

import OpModes.Robot;
import Utilities.Timer;

public class IntakeManager {
    public enum IntakeExtensinoState{
        HOME, //ext retracted, deploy retracted, rotate retracted, grabber open
        MOVING, //
        MOVING_TO_POSITION,
        STOPPED
    }

    public enum IntakeMechanismState {
        HOME,
        DEPLOYING,
        READY,
        RETRACTING
    }
    public enum IntkaeGrabberState {
        OPEN,
        MOVING,
        CLOSED
    }

    private Robot robot;

    private final int extensionMaxPosition = 1000;
    private final int extensionRetractedPosition = 0;
    private final double deploymentServoIntakePosition = 0.5;
    private final double deploymentServoRestingPosition = 0.5;
    private final double intakeRotationServoIntakePosition = 0.5;
    private final double intakeRotationServoRestingPosition = 0.5;
    private final double grabbingServoClosedPosition = 0.5;
    private final double grabbingServoOpenPosition = 0.5;


    private final double intakeFieldSamplesPosition = 50;

    private int lastIntakeExtensionPosition;
    private Timer deploymentTimer;
    private Timer grabberTimer;

    public IntakeState intakeState = IntakeState.RESTING;

    public IntakeManager(Robot robot) {
        //assign the global robot to the local property to access in this class
        this.robot = robot;
        lastIntakeExtensionPosition = robot.intakeExtensionMotor.getCurrentPosition();
    }

    public void update() {
        double currentExtensionPosition = robot.intakeExtensionMotor.getCurrentPosition();
        switch (intakeState) {
            case RunningToPosition:
                if(Math.abs(currentExtensionPosition-robot.intakeExtensionMotor.getTargetPositionTolerance()) <
                        robot.intakeExtensionMotor.getTargetPositionTolerance() ) {
                    intakeState = IntakeState.Ready;
                }
                break;
        }

    }

    public void moveIntakeExtension(double power) {
        double currentPos = robot.intakeExtensionMotor.getCurrentPosition();
        if((power > 0 && currentPos >= fullExtensionPosition) ||
            power < 0 && currentPos <= extensionRetractedPosition) {
            robot.intakeExtensionMotor.setPower(0);
        }
        robot.intakeExtensionMotor.setPower(power);
        intakeState = IntakeState.MovingExtension;
    }

    public void moveExtensionToSampleIntake(int targetPosition) {
        robot.intakeExtensionMotor.setTargetPositionTolerance(10);
        robot.intakeExtensionMotor.setTargetPosition(targetPosition);
        robot.intakeExtensionMotor.setPower(1);
        intakeState = IntakeState.RunningToPosition;
    }
}
