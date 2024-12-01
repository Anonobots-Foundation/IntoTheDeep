package OpModes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.util.ArrayList;
import java.util.List;

import Subsystems.DeliveryManager;
import Utilities.Enumerations;
import Utilities.HolonomicDriveCommands;
import Utilities.PathPoint;
import Utilities.Timer;

@Autonomous(name="Park Auto")
public class ParkAuto extends AutoBase {

    public enum AutoSteps {
        DeployIntake

    }
    private Timer delayTimer;
    private Timer delayTimer2;
    private List<PathPoint> pathToBasket;

    @Override
    public String getCurrentStep() {
        return AutoSteps.values()[currentStep].toString();
    }

    public void init() {
        super.init();

    }
    public void init_loop() {
        super.init_loop();
        newStep = true;
    }
    public void start() {

        super.start();
        BuildPaths();
        newStep = true;
    }
    public void loop() {
        super.loop();

        manageMachineState();

    }
    private void manageMachineState() {
        if (currentStep == AutoSteps.DeployIntake.ordinal()) {
            if(opModeTimer.elapsedTime() > 27000) {
                intakeManager.deployIntake();
            }



        }
    }
    @Override
    public void BuildPaths() {
        pathToBasket = new ArrayList<PathPoint>();
        pathToBasket.add(new PathPoint( 19, 4, 45, .5, 1.5,0,0, 5, true));
    }

}
