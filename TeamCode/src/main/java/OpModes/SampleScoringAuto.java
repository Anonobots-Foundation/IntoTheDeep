package OpModes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.util.ArrayList;
import java.util.List;

import Subsystems.DeliveryManager;
import Utilities.Enumerations;
import Utilities.HolonomicDriveCommands;
import Utilities.PathPoint;
import Utilities.Timer;

@Autonomous(name="Sample Scoring Auto")
public class SampleScoringAuto extends AutoBase {

    public enum AutoSteps {
        TurnToBasket,
        DropSample

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
        if (currentStep == AutoSteps.TurnToBasket.ordinal()) {
            if (HolonomicDriveCommands.turnToHeading(45, .70, 30, 5, Enumerations.RotationDirection.CW))
                nextStep();



        }
        if(currentStep == AutoSteps.DropSample.ordinal()){
            if(newStep) {
                deliveryManager.moveDeliveryToPosition(DeliveryManager.DeliveryPositions.DROP_SAMPLE);
                delayTimer = new Timer();
                delayTimer.start();
                newStep = false;
            }
            if(delayTimer != null && delayTimer.elapsedTime() > 1000) {
                deliveryManager.openBucketGate();

            }
        }
    }
    @Override
    public void BuildPaths() {
        pathToBasket = new ArrayList<PathPoint>();
        pathToBasket.add(new PathPoint( 19, 4, 45, .5, 1.5,0,0, 5, true));
    }

}
