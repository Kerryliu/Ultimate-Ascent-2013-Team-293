package edu.wpi.first.wpilibj.robot.subsystems;

import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.robot.RobotMap;

/**
 * This class is for controlling the lead screw that causes the angle to change.
 * Most of the code is self-explanatory. Also controls the winch, which is
 * utilized when climbing.
 */
public class Angle {

    private final static double //Angle adjustment constants: (Need to be tweaked)
            anglePWMUpwardSpeed = -0.56,
            anglePWMDownwardSpeed = 0.56,
            anglePWMUpwardInitSpeed = -0.4,
            anglePWMDownwardSpeedClimb = 0.65,
            winchPWMUpwardSpeed = 0.48,
            winchPWMDownwardSpeedClimb = -0.81,
            angleErrorValue = 0.25;
    private static double DriveHangSpeed = 0.4; //Good enough
    private static boolean climbAngleStatus = false;
    private static final DriverStationLCD LCD = DriverStationLCD.getInstance();
    private static final DigitalInput shooterAngleLimitLower = new DigitalInput(RobotMap.shooterAngleLimitLower),
            shooterAngleLimitUpper = new DigitalInput(RobotMap.shooterAngleLimitUpper),
            climberClawLimit1 = new DigitalInput(RobotMap.climberClawLimit1), //Closest to shooter
            climberClawLimit2 = new DigitalInput(RobotMap.climberClawLimit2);
    public static final Talon winchPWM = new Talon(RobotMap.winchPWM),
            anglePWM = new Talon(RobotMap.anglePWM);
    public static final Encoder angleEncoder = new Encoder(RobotMap.angleAChannelDin, RobotMap.angleBChannelDin, true, CounterBase.EncodingType.k4X);

    /**
     * Goes into robotInit. Only runs once in the beginning. While loops are
     * used because robotInit does not loop.
     */
    public static void angleInit() {
        if (shooterAngleLimitUpper.get() == false) {
            while (shooterAngleLimitUpper.get() == false) {
                anglePWM.set(anglePWMUpwardInitSpeed);
                winchPWM.set(winchPWMUpwardSpeed); //upward speed is release
            }
            angleStop();
        }
        angleStop(); //failsafe
        winchPWM.set(0);
        angleEncoder.setDistancePerPulse(0.0128); //Random numbers FTW. 
        angleEncoder.start();
        angleEncoder.reset();
    }

    /**
     * Speeds should be calibrated so that winch and lead screw move at the same
     * pace. Otherwise, bad things will happen.
     */
    public static void autoClimb() {
        if (climbAngleStatus == false) {
            setAngle(0);
            if (anglePWM.getSpeed() == 0) {
                winchPWM.set(0.0); //Failsafe
                climbAngleStatus = true;
            }
        }
        if (climbAngleStatus == true) {
            if (climberClawLimit1.get() == true && climberClawLimit2.get() == true) {
                if (angleEncoder.getDistance() < 10.7) {
                    winchPWM.set(winchPWMDownwardSpeedClimb);
                    anglePWM.set(anglePWMDownwardSpeedClimb);
                } else {
                    angleStop();
                }
            } else {
                if (climberClawLimit1.get() == true) {
                    DriveTrain.tankDrive(DriveHangSpeed, 0); //left
                } else if (climberClawLimit2.get() == true) {
                    DriveTrain.tankDrive(0, DriveHangSpeed); //right
                } else if (angleEncoder.getDistance() > 0) { //If neighet limit is pressed, the robot has fallen.  
                    winchPWM.set(winchPWMUpwardSpeed);
                    anglePWM.set(anglePWMUpwardInitSpeed);
                    DriveHangSpeed = 0.4; //Reset speed if the robot has fallen.  
                } else {
                    angleStop();
                }
                if (DriveHangSpeed < 0.5) {
                    DriveHangSpeed += 0.01;
                } else {
                    DriveHangSpeed = 1;
                }
            }
        }
    }

    /**
     * Sets the angle to a certain value.
     */
    public static void setAngle(double angleValue) {
        if (angleEncoder.getDistance() > (angleValue + angleErrorValue)) {
            angleUp();
        } else if (angleEncoder.getDistance() < (angleValue - angleErrorValue)) {
            angleDown();
        } else {
            angleStop();
        }
    }

    /**
     * Moves the angle up.
     */
    protected static void angleUp() { //Protected sounds so much more professional
        if (shooterAngleLimitUpper.get() == false) {
            if (angleEncoder.getDistance() < 1.25) {
                anglePWM.set(anglePWMUpwardSpeed / 2.5);
                LCD.println(DriverStationLCD.Line.kUser2, 1, "Increasing Angle.");
            } else {
                anglePWM.set(anglePWMUpwardSpeed);
                LCD.println(DriverStationLCD.Line.kUser2, 1, "Increasing Angle.");
            }
        } else {
            angleStop();
        }
    }

    /**
     * Moves the angle down.
     */
    protected static void angleDown() {
        if (shooterAngleLimitLower.get() == false) {
            if (angleEncoder.getDistance() > 11) {
                anglePWM.set(anglePWMDownwardSpeed / 3);
                LCD.println(DriverStationLCD.Line.kUser2, 1, "Decreasing Angle.");
            } else {
                anglePWM.set(anglePWMDownwardSpeed);
                LCD.println(DriverStationLCD.Line.kUser2, 1, "Decreasing Angle.");
            }
        } else {
            angleStop();
        }
    }

    /**
     * Stops the winch and angle.
     */
    public static void angleStop() {
        anglePWM.set(0.0);
        winchPWM.set(0.0);
        LCD.println(DriverStationLCD.Line.kUser2, 1, "Angle Stopped.       ");
    }
}
