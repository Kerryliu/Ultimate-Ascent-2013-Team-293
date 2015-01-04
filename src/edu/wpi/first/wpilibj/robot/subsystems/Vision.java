package edu.wpi.first.wpilibj.robot.subsystems;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 * Most vision processing is done with RoboRealm on the Laptop and sent over to
 * the robot via networktables. This class is perfect. No need to change
 * anything. :)
 */
public class Vision {

    private static final NetworkTable cameraTable = NetworkTable.getTable("SmartDashboard");

    public static double centerOfGravity() { //Only COG_X is required to center the robot.  
        double centerOfGravity = cameraTable.getNumber("COG_X", 160); //160 as default, so nothing will happen if no value is acquired from smartdashboard
        return centerOfGravity;
    }

    public static double getDistance() { //In feet
        double distance = cameraTable.getNumber("Distance", -1);
        return distance;
    }

    public static double calculateAngle() {//This is an best fit line generated from a table of values with excel.  
        double angle = cameraTable.getNumber("Calculated Shooting Angle", -1);//Apparently the Math.pow function is missing. 
        return angle;
    }
}
