package OpModes.TestOpModes;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import OpModes.Robot;

public class TestMotorPosition extends Robot {

    DcMotorEx currentMotor;
    String currentMotorName = "Intake Motor";
    public void init() {
        super.init();
        currentMotor = intakeExtensionMotor;


    }

    public void loop() {
        super.loop();
        telemetry.addLine("X=IntakeMotor");
        telemetry.addLine("Y=DeliveryLiftMotor");
        telemetry.addLine("B=HangingMotor");
        telemetry.addLine("DPad Down to Set Zero");
        if(gameController1.xPressed) {
            currentMotor = intakeExtensionMotor;
            currentMotorName = "IntakeMotor";
        }
        if(gameController1.yPressed) {
            currentMotor = deliveryLiftMotor;
            currentMotorName = "DeliveryLiftMotor";
        }
        if(gameController1.bPressed) {
            currentMotor = hangingMotor;
            currentMotorName = "HangingMotor";
        }
        if(gameController1.dpad_down) {
            currentMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            currentMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        //set power to game controller left stick
        currentMotor.setPower(gameController1.left_stick_y);
        telemetry.addData("Current Motor",currentMotorName);
        telemetry.addData("Motor Position",currentMotor.getCurrentPosition());
    }

}
