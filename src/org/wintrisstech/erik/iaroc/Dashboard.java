package org.wintrisstech.erik.iaroc;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.ScrollView;
import android.widget.TextView;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import java.util.Locale;
import org.wintrisstech.irobot.ioio.IRobotCreateInterface;
import org.wintrisstech.irobot.ioio.SimpleIRobotCreate;

/**
 * This is the main activity of the iRobot2012 application.
 * 
 * <p>
 * This class assumes that there are 3 ultrasonic sensors attached to the
 * iRobot. An instance of the Dashboard class will display the readings of these
 * three sensors.
 * 
 * <p>
 * There should be no need to modify this class. Modify Lada instead.
 * 
 * @author Erik Colban
 * 
 */
public class Dashboard extends IOIOActivity implements
		TextToSpeech.OnInitListener, SensorEventListener {

	/**
	 * Tag used for debugging.
	 */
	private static final String TAG = "Dashboard";
	/**
	 * Text view that contains all logged messages
	 */
	private TextView mText;
	private ScrollView scroller;
	/**
	 * A Lada instance
	 */
	private Lada kalina;
	/**
	 * TTS stuff
	 */
	protected static final int MY_DATA_CHECK_CODE = 33;
	private TextToSpeech mTts;
	/**
	 * Compass stuff
	 */
	SensorManager sensorManager;
	private Sensor sensorAccelerometer;
	private Sensor sensorMagneticField;

	private float[] valuesAccelerometer;
	private float[] valuesMagneticField;

	private float[] matrixR;
	private float[] matrixI;
	private float[] matrixValues;
	private double azimuth;
	private double pitch;
	private double roll;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * Since the android device is carried by the iRobot Create, we want to
		 * prevent a change of orientation, which would cause the activity to
		 * pause.
		 */
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.main);

		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);

		// Compass stuff
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorAccelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorMagneticField = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		valuesAccelerometer = new float[3];
		valuesMagneticField = new float[3];

		matrixR = new float[9];
		matrixI = new float[9];
		matrixValues = new float[3];

		mText = (TextView) findViewById(R.id.text);
		scroller = (ScrollView) findViewById(R.id.scroller);
		log(getString(R.string.wait_ioio));

	}

	@Override
	public void onPause() {
		if (kalina != null) {
			log("Pausing");
		}
		sensorManager.unregisterListener(this, sensorAccelerometer);
		sensorManager.unregisterListener(this, sensorMagneticField);
		super.onPause();
	}

	@Override
	protected void onResume() {

		sensorManager.registerListener(this, sensorAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, sensorMagneticField,
				SensorManager.SENSOR_DELAY_NORMAL);
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MY_DATA_CHECK_CODE) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				// success, create the TTS instance
				mTts = new TextToSpeech(this, this);
			} else {
				// missing data, install it
				Intent installIntent = new Intent();
				installIntent
						.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
		}
	}

	public void onInit(int arg0) {
	}

	public void speak(String stuffToSay) {
		mTts.setLanguage(Locale.US);
		if (!mTts.isSpeaking()) {
			mTts.speak(stuffToSay, TextToSpeech.QUEUE_FLUSH, null);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			for (int i = 0; i < 3; i++) {
				valuesAccelerometer[i] = event.values[i];
			}
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			for (int i = 0; i < 3; i++) {
				valuesMagneticField[i] = event.values[i];
			}
			break;
		}

		boolean success = SensorManager.getRotationMatrix(matrixR, matrixI,
				valuesAccelerometer, valuesMagneticField);

		if (success) {
			SensorManager.getOrientation(matrixR, matrixValues);
			synchronized (this) {
				azimuth = Math.toDegrees(matrixValues[0]);
				pitch = Math.toDegrees(matrixValues[1]);
				roll = Math.toDegrees(matrixValues[2]);
			}
		}

	}

	/**
	 * Gets the azimuth
	 * @return the azimuth
	 */
	public synchronized double getAzimuth() {
		return azimuth;
	}
	
	/**
	 * Gets the pitch
	 * @return the pitch
	 */
	public synchronized double getPitch() {
		return pitch;
	}

	/**
	 * Gets the roll
	 * @return the roll
	 */
	public synchronized double getRoll() {
		return roll;
	}
	
	@Override
	public IOIOLooper createIOIOLooper() {
		return new IOIOLooper() {

			public void setup(IOIO ioio) throws ConnectionLostException,
					InterruptedException {
				/*
				 * When the setup() method is called the IOIO is connected.
				 */
				log(getString(R.string.ioio_connected));

				/*
				 * Establish communication between the android and the iRobot
				 * Create through the IOIO board.
				 */
				log(getString(R.string.wait_create));
				IRobotCreateInterface iRobotCreate = new SimpleIRobotCreate(
						ioio);
				log(getString(R.string.create_connected));

				/*
				 * Get a Lada (built on the iRobot Create) and let it go... The
				 * ioio_ instance is passed to the constructor in case it is
				 * needed to establish connections to other peripherals, such as
				 * sensors that are not part of the iRobot Create.
				 */
				kalina = new Lada(ioio, iRobotCreate, Dashboard.this);
				kalina.initialize();
			}

			public void loop() throws ConnectionLostException,
					InterruptedException {
				kalina.loop();
			}

			public void disconnected() {
				log(getString(R.string.ioio_disconnected));
			}

			public void incompatible() {
			}
		};
	}

	/**
	 * Writes a message to the Dashboard instance.
	 * 
	 * @param msg
	 *            the message to write
	 */
	public void log(final String msg) {
		runOnUiThread(new Runnable() {

			public void run() {
				mText.append(msg);
				mText.append("\n");
				scroller.smoothScrollTo(0, mText.getBottom());
			}
		});
	}
}
