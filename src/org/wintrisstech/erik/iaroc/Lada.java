package org.wintrisstech.erik.iaroc;

/**************************************************************************
 * Happy version...ultrasonics working...Version 140427A...mods by Vic
 **************************************************************************/
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

import org.wintrisstech.sensors.UltraSonicSensors;

import android.os.SystemClock;

public class Lada extends IRobotCreateAdapter
{
	private final Dashboard dashboard;
	public UltraSonicSensors sonar;
	public int TURNSPEED = 120;
	public int TURNSPEEDSLOW = 60;
	private Robot myRobot; 

	public Lada(IOIO ioio, IRobotCreateInterface create, Dashboard dashboard)
			throws ConnectionLostException
	{
		super(create);
		sonar = new UltraSonicSensors(ioio);
		this.dashboard = dashboard;
	}

	public void initialize() throws ConnectionLostException
	{
		myRobot = new Robot(dashboard, this, sonar);
		//myRobot.log("iAndroid2014 version 0.0.1");
		//myRobot.log("Ready!");
		
		
	}
	
	public void loop() throws ConnectionLostException, InterruptedException
	{

		//Drag Race
		//myRobot.followStraightWall(/*speed (450 max)*/ 300,/*turn speed*/ 10, /*buffer distance*/ 5, /*distance*/ 30, /*wall*/ "Right", /*sleep time*/ 50);		
		
		
		//myRobot.doGoldRush();

		//Maze
		//myRobot.doRightWallHugging(30);
		//SystemClock.sleep(2000); /*Comment Sleep out for Race*/ 
	}
	
	

	public void drawSquare(int lineLength, int amountOfSquares)
			throws ConnectionLostException
	{
		for (int x = 0; x < 4 * amountOfSquares; x++)
		{
			myRobot.goForward(lineLength);
			drawSquare(30, 2);
			turn(90);
		}
	}


	
	public void goToHeading(int heading) throws ConnectionLostException
	{
		int currentHeading = (int)	 dashboard.getAzimuth() %360;
//TODO Get the compass heading working
		while (heading != currentHeading)
		{
			currentHeading = (int) dashboard.getAzimuth()  %360;
			dashboard.log("CurrentHeading: " + currentHeading);
			
			currentHeading = (int) dashboard.getAzimuth();
		}
		stop();
	}

	public void stop() throws ConnectionLostException
	{
		driveDirect(0, 0);
	}

	

	public void accelerate(int maxSpeed) throws ConnectionLostException
	{
		for (int i = 0; i < maxSpeed; i++)
		{
			driveDirect(i, i);
		}
	}

	

	public void checkBumpSensors() throws ConnectionLostException {
		readSensors(SENSORS_BUMPS_AND_WHEEL_DROPS);
		
		if(isBumpRight()) {
			dashboard.log("I hit the wall!");
		}
	}
	
	public void deccelerate() throws ConnectionLostException
	{

		for (int x = 500; x > 0; x--)
		{

			driveDirect(x, x);
		}
	}

	public void turnAround() throws ConnectionLostException
	{
		driveDirect(-500, 500);
		SystemClock.sleep(1850);
	}

	public void goFast(int speed) throws ConnectionLostException
	{
		driveDirect(speed, speed);
	}

	

	public void goForward(int centimeters) throws ConnectionLostException
	{
		int totalDistance = 0;
		readSensors(SENSORS_GROUP_ID6);
		driveDirect(250, 250);
		while (totalDistance < centimeters * 10)
		{
			readSensors(SENSORS_GROUP_ID6);
			int dd = getDistance();
			totalDistance += dd;
			dashboard.log("" + totalDistance / 10 + " cm");
		}
		stop();
	}

	public void tempStop(int tempStopTime) throws ConnectionLostException
	{
		stop();
		SystemClock.sleep(tempStopTime);
	}

	public void turn(int degrees) throws ConnectionLostException
	{
		int initialTurningTarget = (int) (dashboard.getAzimuth() + 180);
		dashboard.log(initialTurningTarget + "start");

		while ((dashboard.getAzimuth() + 180) < (initialTurningTarget + degrees))
		{
		}

		stop();
		dashboard.log((int) (dashboard.getAzimuth() + 180) + "stop");
	}

	public void startingText()
	{
		this.dashboard
				.speak(" Welcome to team win equals true's A.P.I.. Now starting robot.");
		SystemClock.sleep(5500);
	}

	public void readLeftDistance() throws ConnectionLostException,
			InterruptedException
	{
		sonar.read();
		float distance = sonar.getLeftDistance();
		if (distance > 1)
		{
			this.dashboard
					.log(" There are "
							+ distance
							+ " centimeters from the left ultrasonic sensor to the object in front of it.");
			SystemClock.sleep(2000);
		}
	}

	public void readFrontDistance() throws ConnectionLostException,
			InterruptedException
	{
		sonar.read();
		float distance = sonar.getFrontDistance();
		if (distance > 1)
		{
			this.dashboard.log(distance + " centimeters (front sonar)");
			SystemClock.sleep(2000);
		}
	}
}