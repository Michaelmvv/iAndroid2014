package org.wintrisstech.sensors;


import org.wintrisstech.erik.iaroc.Dashboard;

import android.os.SystemClock;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.PulseInput;
import ioio.lib.api.PulseInput.PulseMode;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * An UltraSonicSensors instance is used to access three ultrasonic sensors
 * (leftInput, frontInput, and rightInput) and read the measurements from these
 * sensors.
 * 
 * @author Erik Colban
 */
public class UltraSonicSensors {
	private static final float CONVERSION_FACTOR = 17280.0F; // cm / s
	private static final int NUM_SAMPLES = 1;
	private static final int LEFT_ULTRASONIC_INPUT_PIN = 35;
	private static final int FRONT_ULTRASONIC_INPUT_PIN = 36;
	private static final int RIGHT_ULTRASONIC_INPUT_PIN = 37;
	private static final int LEFT_STROBE_ULTRASONIC_OUTPUT_PIN = 15;
	private static final int FRONT_STROBE_ULTRASONIC_OUTPUT_PIN = 16;
	private static final int RIGHT_STROBE_ULTRASONIC_OUTPUT_PIN = 17;
	private final PulseInput leftInput;
	private final PulseInput frontInput;
	private final PulseInput rightInput;
	private DigitalOutput leftStrobe;
	private DigitalOutput frontStrobe;
	private DigitalOutput righttStrobe;
	private float leftDistance;
	private volatile int frontDistance = 10;
	private volatile int rightDistance;
	private IOIO ioio;
	private Dashboard dashboard;

	/**
	 * Constructor of a UltraSonicSensors instance.
	 * @param ioio the IOIO instance used to communicate with the sensor
	 * @throws ConnectionLostException
	 */
	public UltraSonicSensors(IOIO ioio, Dashboard dashboard)
			throws ConnectionLostException {
		this.leftInput = ioio.openPulseInput(LEFT_ULTRASONIC_INPUT_PIN,
				PulseMode.POSITIVE);
		this.frontInput = ioio.openPulseInput(FRONT_ULTRASONIC_INPUT_PIN,
				PulseMode.POSITIVE);
		this.rightInput = ioio.openPulseInput(RIGHT_ULTRASONIC_INPUT_PIN,
				PulseMode.POSITIVE);
		this.leftStrobe = ioio
				.openDigitalOutput(LEFT_STROBE_ULTRASONIC_OUTPUT_PIN);// *******
		this.righttStrobe = ioio
				.openDigitalOutput(RIGHT_STROBE_ULTRASONIC_OUTPUT_PIN);// *******
		this.frontStrobe = ioio
				.openDigitalOutput(FRONT_STROBE_ULTRASONIC_OUTPUT_PIN);// *******
		this.ioio = ioio;
		this.dashboard = dashboard;
	}

	/**
	 * Makes a reading of the ultrasonic sensors and stores the results locally.
	 * To access these readings, use {@link #getLeftDistance()},
	 * {@link #getFrontDistance()}, and {@link #getRightDistance()}.
	 * @throws ConnectionLostException
	 * @throws InterruptedException
	 */
	public void read() throws ConnectionLostException, InterruptedException {
		read(leftStrobe, leftInput);
		// frontDistance = read(frontStrobe, frontInput);
		// rightDistance = read(righttStrobe, rightInput);
	}

	private float read(DigitalOutput strobe, PulseInput input)
			throws ConnectionLostException, InterruptedException {
		leftDistance = 0;
		for (int i = 0; i < NUM_SAMPLES; i++) {
			ioio.beginBatch(); // Start batching to prevent elongating strobe pulse
			strobe.write(true);
			strobe.write(false);
			ioio.endBatch();
			leftDistance +=  (input.getDuration() * CONVERSION_FACTOR);
			SystemClock.sleep(100);
		}
		return leftDistance / NUM_SAMPLES;
	}

	/**
	 * Gets the last read distance in cm of the leftInput sensor
	 * 
	 * @return the leftInput distance in cm
	 */
	public float getLeftDistance() {
		return leftDistance;
	}

	/**
	 * Gets the last read distance in cm of the frontInput sensor
	 * 
	 * @return the frontInput distance in cm
	 */
	public synchronized int getFrontDistance() {
		return frontDistance;
	}

	/**
	 * Gets the last read distance in cm of the rightInput sensor
	 * 
	 * @return the rightInput distance in cm
	 */
	public synchronized int getRightDistance() {
		return rightDistance;
	}

	/**
	 * Closes all the connections to the used pins
	 */
	public void closeConnection() {
		leftInput.close();
		frontInput.close();
		rightInput.close();
		leftStrobe.close();
	}
}
