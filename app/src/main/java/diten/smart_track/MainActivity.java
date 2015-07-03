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
import android.os.Handler;
import android.os.Vibrator;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends Activity {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private boolean mScanning ;
    private Handler mHandler;
    private SensorManager SensorManager;
    ImageView Map;
    ImageView Cursor;
    ImageView Beacon1;
    ImageView Beacon2;
    ImageView Beacon3;
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


    MyTimer timer_test = null;

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
                        Cursor.setRotation(accuracy + 180);
                    }
                    else
                        accuracy = orientations;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        magnetometer = SensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometer = SensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        vibs = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);   // The vibrator
        Cursor = (ImageView) findViewById(R.id.cursor);     // The red-point-cursor
        Map = (ImageView) findViewById(R.id.carte);
        Beacon1 = (ImageView) findViewById(R.id.beacon1);       //The first beacon
        Beacon2 = (ImageView) findViewById(R.id.beacon2);       //The second beacon
        Beacon3 = (ImageView) findViewById(R.id.beacon3);       //The third beacon

        mHandler = new Handler();

        Beacon beacon1 = new Beacon("C2:CB:A5:BD:A2:86", 0, 150, 300, 220);
        Beacon beacon2 = new Beacon("00:07:80:79:2D:A0", 0, 500, 220, 40);
        Beacon beacon3 = new Beacon("14:99:E2:05:79:D2", 0, 256, 504, 223);
        Beacon beacon4 = new Beacon("beacon4", 0, 256, 504, 223);

        Cursor cursor = new Cursor();

        start = (Button)findViewById(R.id.Start);
        start.setOnClickListener(StartListner);

        list = new List_BLE();

        list.add_beacon(beacon1);
        list.add_beacon(beacon2);
        list.add_beacon(beacon3);
        list.add_beacon(beacon4);

        //list.print_list();

        Beacon1.setX(beacon1.getAbscissa());
        Beacon1.setY(beacon1.getOrdinate());
        Beacon2.setX(beacon2.getAbscissa());
        Beacon2.setY(beacon2.getOrdinate());
        Beacon3.setX(beacon3.getAbscissa());
        Beacon3.setY(beacon3.getOrdinate());

        // This listener knows when you touch the screen (namely the map) and when you touch
        // the screen, the vibrator is activated.


        Map.setOnTouchListener(ImgTouch);

        //Change the map accordance with the altitude Z
            /*public boolean ChangeMap(float Z){
                if (Z == 0){
                    Intent intent = new Intent(MainActivity.this, Map_Third_Floor_Activity.class);
                    startActivity(intent);
                    // don't forget to transmit data!
                }
                return true;
            }*/



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

        //  scanLeDevice(true);
        timer_test = new MyTimer(this);
        timer_test.RepetAction();
    }

    /*public void update_cursor(){
        cursor.setAbscissa(list.get_abscissa_index(list.get_index_by_addr_mac(list.min_distance())));
        cursor.setOrdinate(list.get_ordinate_index(list.get_index_by_addr_mac(list.min_distance())));
        Log.i("MainActivity", "Le curseur est à: " + cursor.getAbscissa() + "et: " + cursor.getOrdinate());
        //Cursor.setX(cursor.getAbscissa());
        //Cursor.setX(list.get_abscissa_index(list.get_index_by_addr_mac(list.min_distance())));
        //Cursor.setY(list.get_ordinate_index(list.get_index_by_addr_mac(list.min_distance())));
    }*/

    private View.OnTouchListener ImgTouch =
            new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    //vibs.vibrate(100);
                    // mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    //mBluetoothAdapter.startLeScan(mLeScanCallback);
                    return true;
                }
            };

    private View.OnClickListener StartListner =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            };

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

    public void scanLeDevice(final boolean enable) {
        mScanning = false;
        mBluetoothAdapter.stopLeScan(mLeScanCallback);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String t = list.min_distance();
                if (t.compareTo("EMPTY") != 0) {
                    Cursor.setX(list.get_abscissa_index(list.get_index_by_addr_mac(t)) - 150);  //Mettre un DEFINE
                    Cursor.setY(list.get_ordinate_index(list.get_index_by_addr_mac(t)) - 160);
                }
                }
            });
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

/*
    public void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            Log.i("MainActivity", "La balise  la plus proche est: " + list.min_distance());
        }
    }
    */


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
                            //Log.i("MainActivity", " c'est bon c'est bon");
                        }
                    });
                }
            };


}