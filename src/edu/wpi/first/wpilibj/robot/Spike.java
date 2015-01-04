package edu.wpi.first.wpilibj.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.robot.subsystems.AutoCenter;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.robot.subsystems.Angle;
import edu.wpi.first.wpilibj.robot.subsystems.DriveTrain;
import edu.wpi.first.wpilibj.robot.subsystems.OI;
import edu.wpi.first.wpilibj.robot.subsystems.Shooter;

/**
 * It is important to read the comments here. Some things only work if
 * everything is set up correctly. They will need to be disabled or there might
 * be PWM timeout errors.
 */
public class Spike extends IterativeRobot {

    private static boolean readyToFire = false;
    //private static final DigitalInput autonomousSwitch = new DigitalInput(RobotMap.autonomousSwitch);
    private static final DriverStationLCD LCD = DriverStationLCD.getInstance();
    private static final Timer timer = new Timer();
    //Angles for shooting in autonomous.  Auto aiming does not work very well through the pyramid.  
    private static double sideShotAngle = 8.214,
            centerShotAngle = 7.35;
    public static DigitalInput autonomousSwitch = new DigitalInput(RobotMap.autonomousSwitch);

    /**
     * Code here runs once when the robot starts. While loops should only be
     * used here.
     */
    public void robotInit() {
        Angle.angleInit();
        Shooter.shooterInit();
        timer.start();
        timer.reset();
        LCD.updateLCD(); //Clears the LCD

    }

    /**
     * Code here loops every 20 milliseconds during the autonomous period. While
     * loops should not be used.
     */
    public void autonomousPeriodic() {
        Shooter.calculateRPM();
        Shooter.runShooter();
        if (timer.get() < 8) {
            DriveTrain.tankDrive(0, 0);
            if (autonomousSwitch.get() == false) {
                Angle.setAngle(centerShotAngle);
            } else {
                Angle.setAngle(sideShotAngle);
            }

            if (AutoCenter.isAutoAimDone() == true) {
                readyToFire = true;
                LCD.println(DriverStationLCD.Line.kUser6, 1, "FIRING!!! PEW! PEW!");
            } else {
                readyToFire = false;
                LCD.println(DriverStationLCD.Line.kUser6, 1, "........................");
            }
            Shooter.fireShooter(readyToFire);
        } else if (timer.get() < 8.5) {
            DriveTrain.tankDrive(0.54, 0.54);
        } else if (timer.get() < 10.5) {
            DriveTrain.rotateDrive(0.5);
        } else {
            DriveTrain.tankDrive(0, 0);
            LCD.println(DriverStationLCD.Line.kUser6, 1, ",");
        }
        
        if(timer.get() > 15) {
            timer.reset();
        }
        
        runSmartDashboard();
        LCD.updateLCD(); //Updates LCD so that we have feedback on what is happening.  Only one is needed per periodic.  
    }

    /**
     * Code here loops every 20 milliseconds during the autonomous period. While
     * loops should not be used. Everything is nice and tidy compared to last
     * year... Pieces of the robot are written in separate classes for ease of
     * reading and troubleshooting.
     */
    public void teleopPeriodic() {
        OI.driveRobot();
        OI.controlClimb();
        Shooter.calculateRPM();
        //These things can only move before climbing has begun.  Once you hit the climbing button, there's no going back.  
        if (OI.beginClimb == false) {
            Shooter.runShooter();
            OI.controlAutoAim();
            OI.controlFeed();
            OI.controlTrigger();
            OI.controlWinch();
        }

        //This will update the LCD once every 20ms.  Only one updateLCD is needed.  
        runSmartDashboard();
        LCD.updateLCD();
    }

    private static void runSmartDashboard() {
        //SmartDashboard: (Pretty colors)
        SmartDashboard.putNumber("Shooter Angle: ", Angle.angleEncoder.getDistance()); //Should be very accurate.  
        //SmartDashboard.putNumber("Calculated Shooting Angle: ", Vision.calculateAngle()); //If you are getting some astronomical value, don't auto aim.
        SmartDashboard.putNumber("Shooter RPM: ", Shooter.currentRPM); //line plot :D
        SmartDashboard.putBoolean("RPM Status: ", Shooter.shooterStatus()); //Big green/red square on the smartdashboard. 
        SmartDashboard.putNumber("Shooter PWM Value: ", Shooter.shooterPWM1.getSpeed()); //Diagnostic information.  Not really important to the driver
        SmartDashboard.putBoolean("Auto Limit", autonomousSwitch.get());
        SmartDashboard.putBoolean("Front Limit", DriveTrain.frontLimit.get()); //Not really used.
        SmartDashboard.putNumber("Timer", timer.get());
    }
}
