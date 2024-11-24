package OpModes;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;


import Utilities.Location;


public abstract class AutoBase extends Robot {
    public int currentStep;
    public boolean newStep = false;
    public double timeRemaining;

    public Location redLocation;
    public Location blueLocation;





    public void init() {
        super.init();
        Robot.alliance = AllianceColor.RED;

        //Set robot zero position on init
        //updateFieldLocationPoints();
        //RobotPosition.initialize(startingLocation);
        //setRobotLocation(startingLocation);
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

    }

    public void start() {
        //setRobotLocation(RobotPosition.toLocation()); //set this before start because we start the camera in robot start
        //resetInitialHeading(RobotPosition.currentHeading);
        super.start();
    }

    public void loop() {
        super.loop();
        timeRemaining = 30 - opModeTimer.elapsedTime() / 1000;
        telemetry.addData("Active Auto Step", getCurrentStep());
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
}
