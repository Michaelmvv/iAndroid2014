package org.wintrisstech.erik.iaroc;

/**************************************************************************
 * Super Happy version...ultrasonics working...Version 140512A...mods by Vic
 * Added compass class...works..updatged to adt bundle 20140321
 **************************************************************************/
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

import org.wintrisstech.irobot.ioio.IRobotCreateAdapter;
import org.wintrisstech.irobot.ioio.IRobotCreateInterface;
import org.wintrisstech.sensors.UltraSonicSensors;

import android.os.SystemClock;

import java.util.TimerTask;

/**
 * A Lada is an implementation of the IRobotCreateInterface, inspired by Vic's
 * awesome API. It is entirely event driven.
 * @author Erik
 * Simplified version 140512A by Erik  Super Happy Version
 */
public class Lada extends IRobotCreateAdapter {
	private final Dashboard dashboard;
	public UltraSonicSensors sonar;
	private boolean firstPass = true;;
	private int commandAzimuth;
	private Robot myRobot; 

	/**
	 * Constructs a Lada, an amazing machine!
	 * 
	 * @param ioio
	 *            the IOIO instance that the Lada can use to communicate with
	 *            other peripherals such as sensors
	 * @param create
	 *            an implementation of an iRobot
	 * @param dashboard
	 *            the Dashboard instance that is connected to the Lada
	 * @throws ConnectionLostException
	 */
	public Lada(IOIO ioio, IRobotCreateInterface create, Dashboard dashboard)
			throws ConnectionLostException {
		super(create);
		sonar = new UltraSonicSensors(ioio);
		this.dashboard = dashboard;
	}

	public void initialize() throws ConnectionLostException {
	
		myRobot = new Robot(dashboard, this, sonar);
		myRobot.log("iAndroid2014 version 0.0.1");
		myRobot.log("Ready!");
		//myRobot.speak("Hi.Cameron!");
		//myRobot.goForward(10);
		//myRobot.rotateRight();
		
		if (false)
		{
			
		
		java.util.Timer t = new java.util.Timer();
		t.schedule(new TimerTask() {

		            @Override
		            public void run() {
		                //myRobot.log("This will run every 5 seconds");
		            	myRobot.maintainHeadingLogic();

		            }
		        }, 1000, 1000);
		}
		myRobot.rightSpeed = 100;
		myRobot.leftSpeed = 100;
		myRobot.log("exiting initialize()...");
	}

	/**
	 * This method is called repeatedly
	 * 
	 * @throws ConnectionLostException
	 * @throws InterruptedException 
	 */
	public void loop() throws ConnectionLostException, InterruptedException {
		
		myRobot.followStraightWall(100, 15, 1, 10, "right", 200);
		//dashboard.log(String.valueOf(readCompass()));
		//myRobot.turnToHeading(5);
//		
//		myRobot.maintainHeading();
//		SystemClock.sleep(100);
//		myRobot.log("Front Distance: " + myRobot.getFrontDistance());
//		SystemClock.sleep(100);
//		
//		myRobot.log("Right Distance: " + myRobot.getRightDistance());
//		SystemClock.sleep(100);
//		myRobot.log("Left Distance: " + myRobot.getLeftDistance());
		
		
	}

	public void turn(int commandAngle) throws ConnectionLostException //Doesn't work for turns through 360
	{
		int startAzimuth = 0;
		if (firstPass) {
			startAzimuth += readCompass();
			commandAzimuth = (startAzimuth + commandAngle) % 360;
			dashboard.log("commandaz = " + commandAzimuth + " startaz = " + startAzimuth);
			firstPass = false;
		}
		int currentAzimuth = readCompass();
		dashboard.log("now = " + currentAzimuth);
		if (currentAzimuth >= commandAzimuth) {
			driveDirect(0, 0);
			firstPass = true;
			dashboard.log("finalaz = " + readCompass());
		}
	}

	public int readCompass() {
		return (int) (dashboard.getAzimuth() + 360) % 360;
	}
}