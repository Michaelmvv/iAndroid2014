package org.wintrisstech.erik.iaroc;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

import org.wintrisstech.sensors.UltraSonicSensors;

import android.os.SystemClock;

/**
 * A Lada is an implementation of the IRobotCreateInterface, inspired by Vic's
 * awesome API. It is entirely event driven.
 * 
 * @author Erik
 */
public class Lada extends IRobotCreateAdapter {

	private final Dashboard dashboard;
	public UltraSonicSensors sonar;

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
		sonar = new UltraSonicSensors(ioio, dashboard);
		this.dashboard = dashboard;
		// song(0, new int[]{58, 10});
	}

	public void initialize() throws ConnectionLostException {
		startingText();
		driveDirect(400, 400);
		goForward(100);
		// turn(90);
		// goForward(6);
		// turn(-90);
		// goForward(3);
		// stop();
		
	}


	/**
	 * This method is called repeatedly
	 * 
	 * @throws ConnectionLostException
	 * @throws InterruptedException
	 */
	public void loop() throws ConnectionLostException, InterruptedException {
		readLeftDistance();
	}

	public void accelerate(int maxSpeed) throws ConnectionLostException {
		for (int i = 0; i < maxSpeed; i++) {
			driveDirect(i, i);
		}
	}

	public void turnRight() throws ConnectionLostException {
		driveDirect(-500, 500);
		SystemClock.sleep(300);
	}

	public void deccelerate() throws ConnectionLostException {

		for (int x = 500; x > 0; x--) {

			driveDirect(x, x);
		}
	}

	public void turnAround() throws ConnectionLostException {
		driveDirect(-500, 500);
		SystemClock.sleep(1850);
	}

	public void goFast(int speed) throws ConnectionLostException {
		driveDirect(speed, speed);
	}

	public void stop() throws ConnectionLostException {
		driveDirect(0, 0);
	}

	public void turnLeft() throws ConnectionLostException {
		driveDirect(500, -500);
		SystemClock.sleep(300);
	}

	public void goForward(int centimeters) throws ConnectionLostException {
		int totalDistance = 0;
		readSensors(SENSORS_GROUP_ID6);
		
		while (totalDistance/10 < centimeters) {
			readSensors(SENSORS_GROUP_ID6);
			int dd = getDistance();
			totalDistance += dd;
		}
		stop();
		dashboard.log("" + totalDistance);
	}

	public void turn(int degrees) throws ConnectionLostException {
		if (degrees > 0) {
			driveDirect(0, 250);
		}
		if (degrees < 0) {
			driveDirect(250, 0);
		}
		SystemClock.sleep(16 * degrees);
	}

	public void startingText() {

		this.dashboard.log(" Welcome to AwesomeAPI. Starting robot in five seconds.");
		for (int i = 5; i > 0; i--) {
			SystemClock.sleep(1000);
			this.dashboard.log(" " + i + "...");
		}

		SystemClock.sleep(500);
		this.dashboard.log(" Now starting robot.");
	}

	public void readLeftDistance() throws ConnectionLostException,
			InterruptedException {
		sonar.read();
		float distance = sonar.getLeftDistance();
		if(distance > 1)  {
			dashboard.speak(""+ (int)distance);
		this.dashboard
				.log(" There are "
						+ distance
						+ " centimeters from the left ultrasonic sensor to the object in front of it.");
		}
	}

}
