package edu.wpi.first.wpilibj.robot.subsystems;

import edu.wpi.first.wpilibj.DriverStationLCD;

/**
 * All this class does is automatically center the robot so that it is facing
 * the target. It can also check and make sure that autoAiming is done or not.
 * Works best on a fresh battery.
 */
public class AutoCenter {

    private final static double //Left and right bounds change according to the resolution of the camera.  Currently the resolution should be 320x240.  
            largeMaxLeftBound = 190, // +/- 25 pixels of error.  
            largeMaxRightBound = 290, //135 is center.  
            smallMaxLeftBound = 210, // +/- 25 pixels of error.
            smallMaxRightBound = 270,
            //Lining up the target constants: 
            largeTurningRightSpeed = -0.45,
            largeTurningLeftSpeed = 0.45,
            smallTurningRightSpeed = -0.38,
            smallTurningLeftSpeed = 0.38;
    private static final DriverStationLCD LCD = DriverStationLCD.getInstance();

    /**
     * This code might require a bit of operator control. If the robot is moving
     * too fast, it will over shoot and start oscillating. Operator must know
     * when to let go of the button. Following should be self-explanatory. Join
     * the build team if you do not understand.
     */
    public static void lineUpTarget() {
        if (Vision.centerOfGravity() < largeMaxLeftBound) {
            DriveTrain.rotateDrive(largeTurningRightSpeed);
            LCD.println(DriverStationLCD.Line.kUser1, 1, "...Turning Left...");
        } else if (Vision.centerOfGravity() > largeMaxRightBound) {
            DriveTrain.rotateDrive(largeTurningLeftSpeed);
            LCD.println(DriverStationLCD.Line.kUser1, 1, "...Turning Right...");
        } else {
            if (Vision.centerOfGravity() < smallMaxLeftBound) { //Space between the last dot is to check that the loop is working correctly. 
                DriveTrain.rotateDrive(smallTurningRightSpeed);
                LCD.println(DriverStationLCD.Line.kUser1, 1, "...Turning Left.. .");
            } else if (Vision.centerOfGravity() > smallMaxRightBound) {
                DriveTrain.rotateDrive(smallTurningLeftSpeed);
                LCD.println(DriverStationLCD.Line.kUser1, 1, "...Turning Right.. .");
            } else {
                DriveTrain.tankDrive(0, 0);
                LCD.println(DriverStationLCD.Line.kUser1, 1, "<Centered on Target>");
            }
        }
    }

    /**
     * If the driveTrain + angle lead screw is not moving, autoAim is done. (In
     * theory)
     */
    public static boolean isAutoAimDone() {
        if (Angle.anglePWM.getSpeed() == 0 && DriveTrain.leftDrivePWM1.getSpeed() == 0 && DriveTrain.rightDrivePWM1.getSpeed() == 0) {
            return true;
        } else {
            return false;
        }
    }
}