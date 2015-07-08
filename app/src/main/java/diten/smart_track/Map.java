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
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by lounes on 06/07/15.
 */
public class Map extends Activity {

    private   BluetoothAdapter mBluetoothAdapter;
    private   BluetoothManager bluetoothManager;
    private   static final int REQUEST_ENABLE_BT = 1;
    private   static final long SCAN_PERIOD = 4000;
    private   boolean mScanning ;
    private   Handler mHandler;
    MyTimer   myTimer1 = null;
    MyTimer2  myTimer2 = null;
    List_BLE  list     = null;
    Cursor    cursor   = null;
    Button    start    = null;
    ImageView Cursor;
    ImageView Map;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view =getLayoutInflater().inflate(R.layout.map,null); // get reference to root activity view
        setContentView(view);

        start = (Button) findViewById(R.id.Start);
        Map = (ImageView) findViewById(R.id.carte);

        myTimer1 = new MyTimer(this);
        myTimer2 = new MyTimer2(this);
        list = new List_BLE();

        Cursor cursor = new Cursor();

        // initialisation statique voir la class List_BLE
        list.list_static_initialisation();

        BLE_management();

        myTimer2.RepetAction();
        myTimer1.RepetAction();

    }




    /**********************************************************************************************/
    /***************************** FONCTION DE GESTION DES LISTNER ********************************/
    /**********************************************************************************************/

    private View.OnClickListener listner_01 =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            };

    private View.OnClickListener listner_02 =
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


    /**********************************************************************************************/
    /******************************* fONCTION DE GESTION DU BLE ***********************************/
    /**********************************************************************************************/

    private void BLE_management()
    {
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
    }

    public void scanLeDevice(final boolean enable)
    {
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        if (enable) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /*
                    String t = list.min_distance();
                    if (t.compareTo("EMPTY") != 0) {
                        Cursor.setX(list.get_abscissa_index(list.get_index_by_addr_mac(t)) - 180);   //Transformer en Constante globale
                        Cursor.setY(list.get_ordinate_index(list.get_index_by_addr_mac(t)) - 180);
                    }
                    */
                }
            });
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
    }


    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
                    Log.i("MAP", " BRAAAAAAAAAAAAAAAAAAAAAAAA");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), device + " | " + rssi, Toast.LENGTH_SHORT).show();
                            Log.i("MAP", "BEACON : " + device + " CT :");
                            list.list_find_by_add(device.toString(), rssi);
                        }
                    });
                }
            };
}
