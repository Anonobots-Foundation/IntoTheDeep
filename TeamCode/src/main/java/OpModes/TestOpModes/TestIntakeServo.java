package OpModes.TestOpModes;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
@TeleOp(name = "Test Intake")
public class TestIntakeServo extends OpMode {

    enum AllianceColor {
        RED,
        BLUE
    }
    enum IntakeState {
        RESTING,
        EJECTING,
        INTAKING,
        HOLDING
    }
    CRServo intakeServo;
    RevColorSensorV3 intakeColorSensor;
    AllianceColor allianceColor = AllianceColor.RED;
    IntakeState intakeState = IntakeState.RESTING;
    boolean intakeActive = false;


    public void init() {
        intakeServo = hardwareMap.get(CRServo.class,"IntakeServo");
        //intakeColorSensor = hardwareMap.get(RevColorSensorV3.class,"ColorSensor");

    }

    public void init_loop() {
        if(gamepad1.dpad_up)
            allianceColor = AllianceColor.RED;
        if(gamepad1.dpad_down)
            allianceColor = AllianceColor.BLUE;
        telemetry.addData("Alliance Color", allianceColor.toString());
    }

    public void start() {

    }

    public void loop() {
        if(gamepad1.a)
            intakeServo.setPower(1);
        if(gamepad1.b)
            intakeServo.setPower(-1);
        if(gamepad1.x)
            intakeServo.setPower(0);
/*
        double objectgDistance = intakeColorSensor.getDistance(DistanceUnit.INCH);
        telemetry.addData("distance",objectgDistance);
        if(intakeActive) {
            int b = intakeColorSensor.blue();
            int r = intakeColorSensor.red();
            int g = intakeColorSensor.green();
            if(r > 100 || b > 200) {
                if (b > r && b > g) {
                    color = "blue"; //found blue
                }

                else if( g > r)
                    color = "yellow"; //found gold
                else
                    color = "red"; //found red
            }

        }
        */

    }
}
