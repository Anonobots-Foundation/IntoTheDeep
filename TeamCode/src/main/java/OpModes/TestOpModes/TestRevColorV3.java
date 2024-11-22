package OpModes.TestOpModes;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

//@Disabled
@TeleOp(name = "Rev V3 Sensor Test")
public class TestRevColorV3 extends OpMode {
        RevColorSensorV3 colorSensor;

        public void init() {
            colorSensor = hardwareMap.get(RevColorSensorV3.class,"ColorSensor");
        }
        public void init_loop() {
            telemetry.addData("distance",colorSensor.getDistance(DistanceUnit.MM));
            NormalizedRGBA colorResult = colorSensor.getNormalizedColors();

            telemetry.addData("colorResult",colorResult.toColor());

            telemetry.addData("colorRed",colorSensor.red());
            telemetry.addData("colorBlue",colorSensor.blue());
            telemetry.addData("colorGreen",colorSensor.green());
            String color = "none";
            int b = colorSensor.blue();
            int r = colorSensor.red();
            int g = colorSensor.green();
            if(r > 100 || b > 200) {
                if (b > r && b > g)
                    color = "blue";
                else if( g > r)
                    color = "yellow";
                else
                    color = "red";
            }
            telemetry.addData("color", color);

        }
        public void start() {

        }
        public void loop() {

        }
}
