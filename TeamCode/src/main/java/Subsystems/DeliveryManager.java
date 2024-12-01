package Subsystems;

import static Subsystems.IntakeManager.IntakeExtensionState.HOME;

import com.qualcomm.robotcore.hardware.DcMotor;

import OpModes.Robot;
import Utilities.Timer;

public class DeliveryManager {
    public enum DeliveryPositions {
        HOME,
        DROP_SAMPLE,
        LOW_CHAMBER,
        HIGH_CHAMBER,
        LOW_BASKET,
        HIGH_BASKET,
        LAST_BASKET,
        LAST_CHAMBER,
        LEVEL_1_CLIMB
    }

    public enum DeliveryState {
        HOME,
        MOVING,
        READY_TO_SCORE,
        READY_TO_DROP
    }

    public enum BucketState {
        HOME,
        LOW,
        HIGH,
        MOVING
    }

    public enum GrabberState {
        OPEN,
        CLOSED,
        MOVING
    }

    public enum BucketGateState {
        OPEN,
        CLOSED,
        MOVING
    }


    private Robot robot;

    private final int deliveryHomePosition = 0;
    private final int deliveryDropPosition = 0;
    private final int deliveryLowChamberPosition = 600;
    private final int deliveryHighChamberPosition = 1100;
    private final int level1ClimbPosition = 500;
    private  int deliveryLowBasketPosition = 700;
    private  int deliveryHighBasketPosition = 1200;
    private final int deliveryMinBucketDeploymentScoringHeight = 0;
    private final int deliveryMinBucketDeploymentDumpHeight = 0;

    private final double bucketHomePosition = 0.28;
    private final double bucketScorePosition = 0.79;
    private final double bucketDumpPosition = 0.18;

    private final double grabberOpenPosition = 0.53;
    private final double grabberClosedPosition = 0.66;

    private final double bucketGateOpenPosition = 0.29;
    private final double bucketGateClosedPosition = 0.55;

    private final int bucketDeployMs = 1000;
    private final int bucketGateMs = 200;
    private final int grabberMs = 200;

    private Timer bucketTimer;
    private Timer grabberTimer;
    private Timer bucketGateTimer;
    private Timer intakeDeploymentTimer;

    private boolean liftMovementStarted = false;

    private DeliveryPositions targetDeliveryPosition;
    private BucketState targetBucketState;
    public DeliveryState deliveryState = DeliveryState.HOME;
    public BucketState bucketState;
    public GrabberState grabberState;
    public BucketGateState bucketGateState;

    private DeliveryPositions lastBasketPosition = DeliveryPositions.HIGH_BASKET;
    private DeliveryPositions lastChamberPosition = DeliveryPositions.HIGH_CHAMBER;


    public DeliveryManager(Robot robot) {
        //assign the global robot to the local property to access in this class
        this.robot = robot;
        closeBucketGate();

    }

    public void moveToHome() {
        moveBucket(BucketState.HOME);
        closeBucketGate();
        openGrabber();
        robot.intakeManager.deployIntake();
        intakeDeploymentTimer = new Timer();
        intakeDeploymentTimer.start();
        moveDeliveryToPosition(DeliveryPositions.HOME);

    }
    public void update() {
        if(intakeDeploymentTimer != null && intakeDeploymentTimer.elapsedTime() > 1000) {
            robot.intakeManager.retractIntake();
            intakeDeploymentTimer = null;
        }
        //these will set initial states if they are not already set
        if(deliveryState == null)
            deliveryState = DeliveryState.HOME;
        if(bucketState == null)
            moveBucket(BucketState.HOME);
        if(bucketGateState == null)
            closeBucketGate();
        if(deliveryState == DeliveryState.MOVING)
            handleMovement();


        if(bucketState == BucketState.MOVING && bucketTimer.elapsedTime() >= bucketDeployMs) {
            bucketState = targetBucketState;
            bucketTimer = null;

        }
        if(grabberState == GrabberState.MOVING && grabberTimer.elapsedTime() >= grabberMs){
            if(robot.deliveryGrabberServo.getPosition() == grabberOpenPosition)
                grabberState = GrabberState.OPEN;
            else
                grabberState = GrabberState.CLOSED;
            grabberTimer = null;
        }
        if(bucketGateState == BucketGateState.MOVING && bucketGateTimer.elapsedTime() >= bucketGateMs){
            if(robot.deliveryBucketGateServo.getPosition() == bucketGateOpenPosition)
                bucketGateState = BucketGateState.OPEN;
            else
                bucketGateState = BucketGateState.CLOSED;
            bucketGateTimer = null;
        }


    }

    public void toggleBasketAndHome() {
        if(deliveryState != DeliveryState.HOME)
            moveDeliveryToPosition(DeliveryPositions.HOME);
        else
            moveDeliveryToPosition(lastBasketPosition);
    }

    public void toggleChamberAndHome() {
        if(deliveryState != DeliveryState.HOME)
            moveDeliveryToPosition(DeliveryPositions.HOME);
        else
            moveDeliveryToPosition(lastChamberPosition);
    }

    public void moveDeliveryToPosition(DeliveryPositions position) {
        if(position == DeliveryPositions.LOW_BASKET || position == DeliveryPositions.HIGH_BASKET)
            lastBasketPosition = position;
        if(position == DeliveryPositions.LOW_CHAMBER || position == DeliveryPositions.HIGH_CHAMBER)
            lastChamberPosition = position;
        targetDeliveryPosition = position;
        if(position == DeliveryPositions.HOME) {
            openGrabber();
            closeBucketGate();
        }
        liftMovementStarted = false;
        deliveryState = DeliveryState.MOVING;

    }
    public void toggleGrabber() {
        if(grabberState == GrabberState.CLOSED)
            openGrabber();
        else
            closeGrabber();
    }
    public void openGrabber() {
        robot.deliveryGrabberServo.setPosition(grabberOpenPosition);
        grabberTimer = new Timer();
        grabberTimer.start();
        grabberState = GrabberState.MOVING;
    }
    public void closeGrabber() {
        robot.deliveryGrabberServo.setPosition(grabberClosedPosition);
        grabberTimer = new Timer();
        grabberTimer.start();
        grabberState = GrabberState.MOVING;
    }
    public void toggleBucketGate() {
        if(bucketGateState == BucketGateState.CLOSED)
            openBucketGate();
        else
            closeBucketGate();
    }
    public void openBucketGate() {
        robot.deliveryBucketGateServo.setPosition(bucketGateOpenPosition);
        bucketGateTimer = new Timer();
        bucketGateTimer.start();
        bucketGateState = BucketGateState.MOVING;
    }
    public void closeBucketGate() {
        robot.deliveryBucketGateServo.setPosition(bucketGateClosedPosition);
        bucketGateTimer = new Timer();
        bucketGateTimer.start();
        bucketGateState = BucketGateState.MOVING;
    }
    private void moveDeliveryToPosition(int targetPosition, double power) {
        robot.deliveryLiftMotor.setTargetPosition(targetPosition);
        robot.deliveryLiftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        robot.deliveryLiftMotor.setPower(power);

    }

    private void handleMovement() {
        //ismoving
        //targetposition
        //if targetposition is a chamber, move lift
        //if targetposition is low, move lift then move bucket when high enough - min dump height
        //if target position is basket move lift then move bucket when high enough - min bucket height
        //if target position is home move bucket then move lift
        int currentPosition = robot.deliveryLiftMotor.getCurrentPosition();
        switch(targetDeliveryPosition) {

            case LOW_CHAMBER:
            case HIGH_CHAMBER:
                if(!liftMovementStarted) {
                    int targetPosition = deliveryHighChamberPosition;
                    if(targetDeliveryPosition == DeliveryPositions.LOW_CHAMBER)
                        targetPosition = deliveryLowChamberPosition;
                    moveDeliveryToPosition(targetPosition, 1.0);
                    liftMovementStarted = true;
                }
                else {
                    //we are in position
                    if(Math.abs( robot.deliveryLiftMotor.getTargetPosition() - currentPosition) <= 30) {
                        deliveryState = DeliveryState.READY_TO_SCORE;
                        liftMovementStarted = false;
                    }
                }
                break;
            case LOW_BASKET:
            case HIGH_BASKET:

                    int targetPosition = deliveryHighBasketPosition;
                    if(targetDeliveryPosition == DeliveryPositions.LOW_BASKET)
                        targetPosition = deliveryLowBasketPosition;
                    moveBucket(BucketState.HIGH);
                    moveDeliveryToPosition(targetPosition, 1.0);

               if(Math.abs( robot.deliveryLiftMotor.getTargetPosition() - currentPosition) <= robot.deliveryLiftMotor.getTargetPositionTolerance()) {
                        deliveryState = DeliveryState.READY_TO_SCORE;



                }
                if(bucketTimer == null ) {
                    if( currentPosition >= deliveryMinBucketDeploymentScoringHeight) {
                        moveBucket(BucketState.HIGH);

                    }
                }
                break;
            case DROP_SAMPLE:
                deliveryState = DeliveryState.READY_TO_DROP;
                moveBucket(BucketState.LOW);
                break;
            case LEVEL_1_CLIMB:
                moveBucket(BucketState.HOME);
                moveDeliveryToPosition(level1ClimbPosition, 1.0);
                break;
            case HOME:
                if(bucketState != BucketState.HOME ){
                    moveBucket(BucketState.HOME);
                    bucketState = BucketState.HOME;
                    liftMovementStarted = false;
                }
                else {
                    moveDeliveryToPosition(deliveryHomePosition, 0.5);
                    deliveryState = DeliveryState.HOME;
                }
            break;


        }
    }
    public void adjustDeliveryHeight(int adjustment) {
        deliveryLowBasketPosition += adjustment;
        deliveryHighBasketPosition += adjustment;
        int currentPosition = robot.deliveryLiftMotor.getTargetPosition();
        moveDeliveryToPosition(currentPosition += adjustment, 1.0);
    }
    private void moveBucket(BucketState targetBucketState) {
        double targetBucketPosition;
        switch (targetBucketState) {
            case HIGH:
                targetBucketPosition = bucketScorePosition;
            break;
            case LOW:
                targetBucketPosition = bucketDumpPosition;
            break;
            default:
                targetBucketPosition = bucketHomePosition;
            break;
        }
        bucketState = BucketState.MOVING;
        this.targetBucketState = targetBucketState;
        robot.deliveryBucketDeploymentServo.setPosition(targetBucketPosition);
        bucketTimer = new Timer();
        bucketTimer.start();
    }

}
