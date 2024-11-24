package OpModes.TestOpModes;

import OpModes.AutoBase;

public class BasketAuto extends AutoBase {
    public enum AutoSteps {
        ExtendDelivery, //always swerve around stack
        DriveToBasket,
        RetractDelivery,
        ExtendDeliveryToRight,


        DriveToAcquireRings,
        PeckForRings,
        DriveToFireRings,
        FireOtherRings,
        TurnToFirstDropOff,
        DriveToFirstDropOff,
        DropOffFirstWobble,
        IntakeSecondWobble,
        TurnToSecondWobbleDropOff,
        DriveToSecondDropOff,
        DropOffSecondWobble,
        DriveToParkedPosition
    }
    public void init() {
        super.init();
    }
    public void init_loop() {
        super.init_loop();
    }
    public void start() {
        super.start();
    }
    public void loop() {
        super.loop();
    }

    @Override
    public String getCurrentStep() {
        return "";
    }
}
