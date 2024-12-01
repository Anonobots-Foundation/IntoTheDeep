package OpModes;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;


import Drivers.GoBildaPinpointDriver;
import Subsystems.DeliveryManager;
import Subsystems.DriveTrainManager;
import Subsystems.IntakeManager;
import Utilities.GameController;
import Utilities.Timer;

public abstract class Robot extends OpMode {
    public enum AllianceColor {
        RED,
        BLUE
    }

    //region public properties
    public Timer opModeTimer;

    public GameController gameController1;
    public GameController gameController2;

    public static AllianceColor alliance;

    //motors
    public DcMotorEx frontLeftMotor;
    public DcMotorEx backLeftMotor;
    public DcMotorEx frontRightMotor;
    public DcMotorEx backRightMotor;
    public DcMotorEx intakeExtensionMotor;
    public DcMotorEx deliveryLiftMotor;
    public DcMotorEx hangingMotor;
    //servos
    public Servo intakeGrabberServo;
    public Servo intakeDeploymentServo;
    public Servo intakeRotationServo;
    public Servo deliveryGrabberServo;
    public Servo deliveryBucketDeploymentServo;
    public Servo deliveryBucketGateServo;
    public Servo hangingRatchetServo;

    //sensors

    public RevColorSensorV3 intakeObjectDetectionSensor;
    public Rev2mDistanceSensor rearDrivetrainDistanceSensor;

    //manager classes

    public DriveTrainManager driveTrainManager;
    public IntakeManager intakeManager;
    public DeliveryManager deliveryManager;
    //endregion
    public String sampleInView = "None";
    //region private properties
    private long lastLoopTimestamp;
    //endregion

    public void init() {
        lastLoopTimestamp = System.currentTimeMillis();
        opModeTimer = new Timer();

        if(alliance == null)
            alliance = AllianceColor.RED;
        //declare all game controllers
        gameController1 = new GameController();
        gameController2 = new GameController();

        //Drivetrain Components
        frontLeftMotor = hardwareMap.get(DcMotorEx.class, "FrontLeftMotor");
        backLeftMotor = hardwareMap.get(DcMotorEx.class,"BackLeftMotor");
        frontRightMotor = hardwareMap.get(DcMotorEx.class,"FrontRightMotor");
        backRightMotor = hardwareMap.get(DcMotorEx.class,"BackRightMotor");

        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        rearDrivetrainDistanceSensor = hardwareMap.get(Rev2mDistanceSensor.class,"RearDistanceSensor");
        //set up odometry
        //pinpointOdometry = hardwareMap.get(GoBildaPinpointDriver.class,"PinpointOdometry");
        //TODO: configure odometry

        //Intake Components

        intakeExtensionMotor = hardwareMap.get(DcMotorEx.class,"IntakeExtensionMotor");
        intakeExtensionMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeExtensionMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        intakeExtensionMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        intakeGrabberServo = hardwareMap.get(Servo.class,"IntakeGrabberServo");
        intakeDeploymentServo = hardwareMap.get(Servo.class,"IntakeDeploymentServo");
        intakeRotationServo = hardwareMap.get(Servo.class,"IntakeRotationServo");
        intakeObjectDetectionSensor = hardwareMap.get(RevColorSensorV3.class,"IntakeObjectDetectionSensor");

        //delivery components
        deliveryLiftMotor = hardwareMap.get(DcMotorEx.class,"DeliveryLiftMotor");
        deliveryLiftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        deliveryLiftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        deliveryGrabberServo = hardwareMap.get(Servo.class, "DeliveryGrabberServo");
        deliveryBucketDeploymentServo = hardwareMap.get(Servo.class, "DeliveryBucketDeploymentServo");
        deliveryBucketGateServo = hardwareMap.get(Servo.class,"DeliveryBucketGateServo");

        //hanging
        hangingMotor = hardwareMap.get(DcMotorEx.class, "HangingMotor");
        hangingRatchetServo = hardwareMap.get(Servo.class,"HangingRatchetServo");

        //create subsystem manager classes
        driveTrainManager = new DriveTrainManager(this);
        intakeManager = new IntakeManager(this);
        deliveryManager = new DeliveryManager(this);

    }

    public void init_loop() {
        setLoopTimestamp();
        handleButtonInputs();
        telemetry.addData("Alliance Color", Robot.alliance);
        telemetry.addData("extension", intakeExtensionMotor.getCurrentPosition());
        /*
        telemetry.addData("Robot X", RobotPosition.currentX);
        telemetry.addData("Robot Y", RobotPosition.currentY);
        telemetry.addData("Current Heading", RobotPosition.currentHeading);
         */
    }

    public void start() {
        lastLoopTimestamp = System.currentTimeMillis();
        opModeTimer.start();
    }

    public void loop() {
        handleButtonInputs();

        driveTrainManager.update();
        intakeManager.update();
        deliveryManager.update();

        telemetry.addData("Alliance Color", Robot.alliance);
        telemetry.addData("Sample", sampleInView);
    }

    //region private methods
    private void handleButtonInputs() {
        gameController1.update(gamepad1.a, gamepad1.b, gamepad1.back, gamepad1.dpad_down, gamepad1.dpad_left,
                gamepad1.dpad_right, gamepad1.dpad_up, gamepad1.guide, gamepad1.left_bumper, gamepad1.left_stick_button,
                gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.left_trigger, gamepad1.right_bumper,
                gamepad1.right_stick_button, gamepad1.right_stick_x, gamepad1.right_stick_y, gamepad1.right_trigger, gamepad1.start, gamepad1.x, gamepad1.y);
        gameController2.update(gamepad2.a, gamepad2.b, gamepad2.back, gamepad2.dpad_down, gamepad2.dpad_left,
                gamepad2.dpad_right, gamepad2.dpad_up, gamepad2.guide, gamepad2.left_bumper, gamepad2.left_stick_button,
                gamepad2.left_stick_x, gamepad2.left_stick_y, gamepad2.left_trigger, gamepad2.right_bumper,
                gamepad2.right_stick_button, gamepad2.right_stick_x, gamepad2.right_stick_y, gamepad2.right_trigger, gamepad2.start, gamepad2.x, gamepad2.y);
    }

    private void setLoopTimestamp() {
        long currentTimestamp = System.currentTimeMillis();
        long lastLoopTime = currentTimestamp - lastLoopTimestamp;
        lastLoopTimestamp = currentTimestamp;
        telemetry.addData("loop time", lastLoopTime + "ms");

    }
    //endregion


}
