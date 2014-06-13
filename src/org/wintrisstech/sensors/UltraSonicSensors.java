package org.wintrisstech.sensors;

/**************************************************************************
 * Simplified version 140512A by Erik  Super Happy Version
 **************************************************************************/
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.PulseInput;
import ioio.lib.api.PulseInput.PulseMode;
import ioio.lib.api.exception.ConnectionLostException;
import android.os.SystemClock;

/**
 * An UltraSonicSensors instance is used to access three ultrasonic sensors
 * (leftInput, frontInput, and rightInput) and read the measurements from these
 * sensors. version 140427...modified by Vic...ultrasonics works using Ytai's
 * suggestions...cleaned up formatting
 * @author Erik Colban
 */
public class UltraSonicSensors
{
	private static final float CONVERSION_FACTOR = 17280.0F; // cm/s
	private static int LEFT_ULTRASONIC_INPUT_PIN = 35;
	private static int FRONT_ULTRASONIC_INPUT_PIN = 36;
	private static int RIGHT_ULTRASONIC_INPUT_PIN = 37;
	private static final int LEFT_STROBE_ULTRASONIC_OUTPUT_PIN = 15;
	private static final int FRONT_STROBE_ULTRASONIC_OUTPUT_PIN = 16;
	private static final int RIGHT_STROBE_ULTRASONIC_OUTPUT_PIN = 17;
	private DigitalOutput leftStrobe;
	private DigitalOutput frontStrobe;
	private DigitalOutput rightStrobe;
	private volatile int leftDistance;
	private volatile int frontDistance;
	private volatile int rightDistance;
	private IOIO ioio;

	/**
	 * Constructor of a UltraSonicSensors instance.
	 * @param ioio the IOIO instance used to communicate with the sensor
	 * @throws ConnectionLostException
	 */
	public UltraSonicSensors(IOIO ioio) throws ConnectionLostException
	{
		this.ioio = ioio;
		this.leftStrobe = ioio.openDigitalOutput(LEFT_STROBE_ULTRASONIC_OUTPUT_PIN);
		this.rightStrobe = ioio.openDigitalOutput(RIGHT_STROBE_ULTRASONIC_OUTPUT_PIN);
		this.frontStrobe = ioio.openDigitalOutput(FRONT_STROBE_ULTRASONIC_OUTPUT_PIN);
	}

	/**
	 * Makes a reading of the ultrasonic sensors and stores the results locally.
	 * To access these readings, use {@link #getLeftDistance()},
	 * {@link #getFrontDistance()}, and {@link #getRightDistance()}.
	 * @throws ConnectionLostException
	 * @throws InterruptedException
	 */
	public void read() throws ConnectionLostException, InterruptedException
	{
		leftDistance = read(leftStrobe, LEFT_ULTRASONIC_INPUT_PIN);
		frontDistance = read(frontStrobe, FRONT_ULTRASONIC_INPUT_PIN);
		rightDistance = read(rightStrobe, RIGHT_ULTRASONIC_INPUT_PIN);
	}

	private int read(DigitalOutput strobe, int inputPin) throws ConnectionLostException, InterruptedException
	{
		ioio.beginBatch();//order of statements critical...do not change
		strobe.write(true);
		PulseInput input = ioio.openPulseInput(inputPin, PulseMode.POSITIVE);
		ioio.endBatch();
		SystemClock.sleep(20);
		strobe.write(false);
		int distance = (int) (input.getDuration() * CONVERSION_FACTOR);
		input.close();
		return distance;
	}

	public int getLeftDistance()
	{
		return leftDistance;
	}

	public int getFrontDistance()
	{
		return frontDistance;
	}

	public int getRightDistance()
	{
		return rightDistance;
	}

	public void closeConnection()
	{
		leftStrobe.close();
		frontStrobe.close();
		rightStrobe.close();
	}
}