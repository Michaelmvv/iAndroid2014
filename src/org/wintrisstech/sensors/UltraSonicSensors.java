package org.wintrisstech.sensors;

/**************************************************************************
 * Happy version...ultrasonics working
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
	private static final float CONVERSION_FACTOR = 17280.0F; // cm / s
	private static int LEFT_ULTRASONIC_INPUT_PIN = 35;
	private static int FRONT_ULTRASONIC_INPUT_PIN = 36;
	private static int RIGHT_ULTRASONIC_INPUT_PIN = 37;
	private static final int LEFT_STROBE_ULTRASONIC_OUTPUT_PIN = 15;
	private static final int FRONT_STROBE_ULTRASONIC_OUTPUT_PIN = 16;
	private static final int RIGHT_STROBE_ULTRASONIC_OUTPUT_PIN = 17;
	private PulseInput leftInput;
	private PulseInput frontInput;
	private PulseInput rightInput;
	private DigitalOutput leftStrobe;
	private DigitalOutput frontStrobe;
	private DigitalOutput righttStrobe;
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
		this.leftStrobe = ioio.openDigitalOutput(LEFT_STROBE_ULTRASONIC_OUTPUT_PIN);// *******
		this.righttStrobe = ioio.openDigitalOutput(RIGHT_STROBE_ULTRASONIC_OUTPUT_PIN);// *******
		this.frontStrobe = ioio.openDigitalOutput(FRONT_STROBE_ULTRASONIC_OUTPUT_PIN);// *******
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
		leftDistance = read(leftStrobe, leftInput, LEFT_ULTRASONIC_INPUT_PIN);
		frontDistance = read(frontStrobe, frontInput, FRONT_ULTRASONIC_INPUT_PIN);
		rightDistance = read(righttStrobe, rightInput, RIGHT_ULTRASONIC_INPUT_PIN);
	}

	private int read(DigitalOutput strobe, PulseInput input, int inputPin) throws ConnectionLostException, InterruptedException
	{
		int distance = 0;
		ioio.beginBatch();
		strobe.write(true);
		input = ioio.openPulseInput(inputPin, PulseMode.POSITIVE);
		ioio.endBatch();
		SystemClock.sleep(40);
		strobe.write(false);
		distance += (int) (input.getDuration() * CONVERSION_FACTOR);
		input.close();
		return distance;
	}

	public synchronized int getLeftDistance()
	{
		return leftDistance;
	}

	public synchronized int getFrontDistance()
	{
		return frontDistance;
	}

	public synchronized int getRightDistance()
	{
		return rightDistance;
	}

	public void closeConnection()
	{
		leftInput.close();
		frontInput.close();
		rightInput.close();
		leftStrobe.close();
	}
}