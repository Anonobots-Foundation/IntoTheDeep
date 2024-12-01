package OpModes;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;


import java.util.Locale;

import Drivers.GoBildaPinpointDriver;
import Utilities.Location;
import Utilities.RobotPosition;


public abstract class AutoBase extends Robot {
    public int currentStep;
    public boolean newStep = false;
    public double timeRemaining;

    public Location redLocation;
    public Location blueLocation;

    public GoBildaPinpointDriver odo;
    public Pose2D pos;



    public void init() {
        super.init();
        Robot.alliance = AllianceColor.RED;
        odo = hardwareMap.get(GoBildaPinpointDriver.class,"Odometry");
        odo.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        odo.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.REVERSED, GoBildaPinpointDriver.EncoderDirection.REVERSED);

    }
//test
    public void init_loop() {
        super.init_loop();

        if(gameController1.aPressed) {
            if(Robot.alliance == null || Robot.alliance == AllianceColor.BLUE)
                Robot.alliance = AllianceColor.RED;
            else
                Robot.alliance = AllianceColor.BLUE;
        }
      /*  if(gameController1.dpad_leftPressed)
            startingStackSize = Enumerations.StartingStackSize.ZERO;
        if(gameController1.dpad_upPressed)
            startingStackSize = Enumerations.StartingStackSize.ONE;
        if(gameController1.dpad_rightPressed)
            startingStackSize = Enumerations.StartingStackSize.FOUR; */
        //buildPaths();
        telemetry.addLine("Pres A to Toggle Alliance");
        telemetry.addLine("Press B to reset IMU");
        if(gameController1.bPressed)
            odo.resetPosAndIMU();

        telemetry.addData("Odo Status", "Initialized");
        telemetry.addData("X offset", odo.getXOffset());
        telemetry.addData("Y offset", odo.getYOffset());
        telemetry.update();

    }

    public void start() {
        //setRobotLocation(RobotPosition.toLocation()); //set this before start because we start the camera in robot start
        //resetInitialHeading(RobotPosition.currentHeading);
        super.start();
    }

    public void loop() {
        super.loop();
        odo.bulkUpdate();
        timeRemaining = 30 - opModeTimer.elapsedTime() / 1000;
        telemetry.addData("Active Auto Step", getCurrentStep());
         pos = odo.getPosition();
        RobotPosition.currentX = pos.getY(DistanceUnit.INCH);
        RobotPosition.currentY = pos.getX(DistanceUnit.INCH);
        double myAngle = pos.getHeading(AngleUnit.DEGREES);
        if(myAngle <= 0)
            myAngle = -myAngle;
        else
            myAngle = 360-myAngle;
        RobotPosition.currentHeading = myAngle;
        String data = String.format(Locale.US, "{X: %.3f, Y: %.3f, H: %.3f}", pos.getX(DistanceUnit.INCH), pos.getY(DistanceUnit.INCH), pos.getHeading(AngleUnit.DEGREES));
        telemetry.addData("Position", data);
        telemetry.addData("RobotX",RobotPosition.currentX);
        telemetry.addData("RobotY",RobotPosition.currentY);
        telemetry.addData("RobotHeading",RobotPosition.currentHeading);

    }

    public void stop() {
        super.stop();
    }
    public void nextStep() {

        currentStep += 1;
        newStep = true;
    }

    public void setCurrentStep(int nextStep)
    {
        currentStep = nextStep;
        newStep = true;
    }

    public void buildPaths() {
        //This exists only to be overridden in the child classes and called from the base.
    }

    public abstract String getCurrentStep();

    public abstract void BuildPaths();
}
