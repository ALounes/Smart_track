package diten.smart_track;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.DrawableRes;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by lounes on 06/07/15.
 */
public class Map extends Activity {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private boolean mScanning ;
    private Handler mHandler;
    private SensorManager SensorManager;
    ImageView Map;
    ImageView Cursor;
    Sensor magnetometer;
    Sensor accelerometer;
    public static Vibrator vibs;
    List_BLE list = null;
    Button start = null;
    private float accuracy;
    private float orientations;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 4000;
    Cursor cursor = null;


    MyTimer myTimer = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);


        SensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        magnetometer = SensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometer = SensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        vibs = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);   // The vibrator
        Cursor = (ImageView) findViewById(R.id.cursor);     // The red-point-cursor
        Map = (ImageView) findViewById(R.id.carte);


        mHandler = new Handler();
        Cursor cursor = new Cursor();
        start = (Button)findViewById(R.id.Start);
        start.setOnClickListener(StartListner);

        list = new List_BLE();
        myTimer = new MyTimer(this);

        // This listener knows when you touch the screen (namely the map) and when you touch
        // the screen, the vibrator is activated.

        Map.setOnTouchListener(ImgTouch);

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this,"BLE NOT SUPPORTED", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth Not Supported", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        myTimer.RepetAction();

    }
    /**********************************************************************************************/
    /***************************** FONCTION DE GESTION DES LISTNER ********************************/
    /**********************************************************************************************/

    final SensorEventListener SensorEventListener = new SensorEventListener() {
        float[] accelerometerVector;
        float[] magneticVector;
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelerometerVector=event.values;
            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticVector=event.values;
            }
            if (accelerometerVector != null && magneticVector != null) {
                float R[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, null, accelerometerVector, magneticVector);
                if (success) {
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    orientations = (float) Math.toDegrees(orientation[0]);
                    if (Math.abs(orientations - accuracy) >= 5){
                        accuracy = orientations;
                        Cursor.setRotation(accuracy);
                    }
                    else
                        accuracy = orientations;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    private View.OnTouchListener ImgTouch =
            new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            };

    private View.OnClickListener StartListner =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            };

    /**********************************************************************************************/
    /***************************** FONCTION DE GESTION DU PROCESSUS *******************************/
    /**********************************************************************************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        SensorManager.registerListener(SensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        SensorManager.registerListener(SensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        scanLeDevice(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SensorManager.unregisterListener(SensorEventListener, accelerometer);
        SensorManager.unregisterListener(SensorEventListener, magnetometer);
        scanLeDevice(false);
    }


    /**********************************************************************************************/
    /******************************* fONCTION DE GESTION DU BLE ***********************************/
    /**********************************************************************************************/

    public void scanLeDevice(final boolean enable) {
        mScanning = false;
        mBluetoothAdapter.stopLeScan(mLeScanCallback);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String t = list.min_distance();
                if (t.compareTo("EMPTY") != 0) {
                    Cursor.setX(list.get_abscissa_index(list.get_index_by_addr_mac(t)) - 25);
                    Cursor.setY(list.get_ordinate_index(list.get_index_by_addr_mac(t)) - 25);
                }
            }
        });
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }


    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), device + " | " + rssi, Toast.LENGTH_SHORT).show();
                            list.list_find_by_add(device.toString(), rssi);
                        }
                    });
                }
            };
}
