package diten.smart_track;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
    private boolean mScanning ;
    private Handler mHandler;
    ImageView IMG;
    ImageView Cursor;
    ImageView Beacon1;
    ImageView Beacon2;
    ImageView Beacon3;
    public static Vibrator vibs;
    List_BLE list = null;
    Button start = null;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 2000;
    Cursor cursor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vibs = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);   // The vibrator
        Cursor = (ImageView) findViewById(R.id.cursor);     // The red-point-cursor
        IMG = (ImageView) findViewById(R.id.imageView);
        Beacon1 = (ImageView) findViewById(R.id.beacon1);       //The first beacon
        Beacon2 = (ImageView) findViewById(R.id.beacon2);       //The second beacon
        Beacon3 = (ImageView) findViewById(R.id.beacon3);       //The third beacon
        mHandler = new Handler();

        Beacon beacon1 = new Beacon("C2:CB:A5:BD:A2:86", 0, 150, 300, 220);
        Beacon beacon2 = new Beacon("00:07:80:79:2D:A0", 0, 500, 220, 40);
        Beacon beacon3 = new Beacon("beacon3", 0, 256, 504, 223);
        Beacon beacon4 = new Beacon("beacon4", 0, 256, 504, 223);

        cursor = new Cursor();
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
        IMG.setOnTouchListener(ImgTouch);


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
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth Not Supported", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        scanLeDevice(true);
    }

    public boolean Cursor(float X, float Y) {
        Cursor = (ImageView) findViewById(R.id.cursor);
        Cursor.setX(X);
        Cursor.setY(Y);
        return true;
    }

    private View.OnTouchListener ImgTouch =
            new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    cursor.setAbscissa(event.getX());
                    cursor.setOrdinate(event.getY());
                    float X = event.getX();
                    float Y = event.getY();
                    Cursor(X, Y);
                    //Log.i("MainActivity", " Abscissa: " + X + " Ordinate: " + Y);
                    Log.i("MainActivity", "La balise  la plus proche est: " + list.min_distance(list));
                    list.min_distance(list);
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
                    scanLeDevice(true);
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
        scanLeDevice(false);
    }

    public void scanLeDevice(final boolean enable) {

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mBluetoothAdapter.startLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

    }


/*
// ATTENTIONNNNNNNNNNNNNNNNNNNNN !!!!
// ne pas SUPPPRIIIIMMMMEERRRRRR !!!!

    private void scanLeDevice(final boolean enable) {
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
                            list.list_find_by_add(device.toString(),rssi);
                        }
                    });
                }
            };

    public void monlapin(){
        Toast.makeText(getApplicationContext(), "testeur de LoL", Toast.LENGTH_SHORT).show();
    }
}
