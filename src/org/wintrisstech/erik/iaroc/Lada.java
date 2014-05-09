package org.wintrisstech.erik.iaroc;

/**************************************************************************
 * Happy version...ultrasonics working...Version 140427A...mods by Vic
 **************************************************************************/
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
	public int TURNSPEED = 120;
	public int TURNSPEEDSLOW = 60;

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
		// song(0, new int[]{58, 10});
	}

	public void initialize() throws ConnectionLostException {
		//startingText();
		//drawSquare(50, 1);
		turn(90);
	}

	/**
	 * This method is called repeatedly
	 * 
	 * @throws ConnectionLostException
	 */
	public void drawSquare(int lineLength, int amountOfSquares)
			throws ConnectionLostException {
		for (int x = 0; x < 4 * amountOfSquares; x++) {
			goForward(lineLength);
			drawSquare(30, 2);
			turn(90);
		}
	}

	public void loop() throws ConnectionLostException {
//		try {
//			sonar.read();
//		} catch (InterruptedException ex) {
//		}
//		dashboard.log("L: " + sonar.getLeftDistance() + " F: "
//				+ sonar.getFrontDistance() + " R: " + sonar.getRightDistance());
//		SystemClock.sleep(100);
//		if (sonar.getLeftDistance() < 20) {
//			driveDirect(0, 100);
//			SystemClock.sleep(1000);
//		}
//		if (sonar.getRightDistance() < 20) {
//			driveDirect(100, 0);
//			SystemClock.sleep(1000);
//		}
		//if (sonar.getFrontDistance() < 20) {
		//	driveDirect(-500, -500);
		//	SystemClock.sleep(1000);
		//}
		//dashboard.log("A:" + (int) dashboard.getAzimuth());
		//driveDirect(0, 0);
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
		driveDirect(250, 250);
		while (totalDistance < centimeters * 10) {
			readSensors(SENSORS_GROUP_ID6);
			int dd = getDistance();
			totalDistance += dd;
			dashboard.log("" + totalDistance / 10 + " cm");
		}
		stop();
	}
	public void tempStop(int tempStopTime) throws ConnectionLostException {
		stop();
		SystemClock.sleep(tempStopTime);
	}
	
	public void turn(int degrees) throws ConnectionLostException { //dumbness activate!!!
		// int turnTime = 15;
	//	int turningProgress = 0;
		int initialTurningTarget = (int) (dashboard.getAzimuth() + 180 + degrees);
		//readSensors(SENSORS_GROUP_ID6);
		//int turnBreakingPoint =  (int) ((Math.abs(degrees)) * 0.8);

		if (degrees > 0) {
			driveDirect(-TURNSPEED, TURNSPEED);
		} else if (degrees < 0) {
			driveDirect(TURNSPEED , -TURNSPEED);
	}
		
		while(dashboard.getAzimuth() + 180 < initialTurningTarget){
			//readSensors(SENSORS_GROUP_ID6);
		    dashboard.log(  "azimuth : " + (int) dashboard.getAzimuth() );
			//SystemClock.sleep(30);
			
		}

		stop();
		// if (degrees > 0) { // positive (+) turns right
		// driveDirect(-150, 150);
		// SystemClock.sleep(turnTime * degrees);
		// } else { // negative (-) turns left
		// driveDirect(150, -150);
		// SystemClock.sleep(-turnTime * degrees);
		// }
	}

	public void startingText() {

		this.dashboard.speak(" Welcome to team win equals true's A.P.I.. Now starting robot.");
		SystemClock.sleep(5500);
	}

	public void readLeftDistance() throws ConnectionLostException,
			InterruptedException {
		sonar.read();
		float distance = sonar.getLeftDistance();
		if (distance > 1) {
			// dashboard.speak(""+ (int)distance);
			this.dashboard
					.log(" There are "
							+ distance
							+ " centimeters from the left ultrasonic sensor to the object in front of it.");
			SystemClock.sleep(2000);
		}
	}

	public void readFrontDistance() throws ConnectionLostException,
			InterruptedException {
		sonar.read();
		float distance = sonar.getFrontDistance();
		if (distance > 1) {
			// dashboard.speak(""+ (int)distance);
			this.dashboard.log(distance + " centimeters (front sonar)");
			SystemClock.sleep(2000);
		}

	}
}
