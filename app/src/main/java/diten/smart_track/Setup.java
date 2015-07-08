package diten.smart_track;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by matthieu on 03/07/15.
 */
public class Setup extends Activity {
    TextView id_beacon = null;
    TextView asking = null;
    EditText field = null;
    Button validate = null;
    List_BLE list;
    Beacon beacon;

    private int counter = 1;
    private int step = 3;
    private String addr_mac = null;
    private int X = 0;
    private int Y = 0;
    private int Floor = 0;

    final String EXTRA_LIST = "list_ble";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup);
        id_beacon = (TextView) findViewById(R.id.id_beacon);
        field = (EditText) findViewById(R.id.field);
        validate = (Button) findViewById(R.id.validate);
        asking = (TextView) findViewById(R.id.asking);
        validate.setOnClickListener(StartListner);
        list = new List_BLE();
        beacon = new Beacon("coucou", 0, 10, 10, 10);

        //field.setOnEditorActionListener(StartListner2);
    }

    private View.OnClickListener StartListner =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (counter == 0){
                        String counterS = field.getText().toString();
                        counter = Integer.parseInt(counterS);
                        //asking.setText("Enter the mac adress: ");
                        asking.setText("Enter the position x: ");
                        Toast.makeText(getApplicationContext(), "Ok, le nombre de Beacon est " + counter, Toast.LENGTH_LONG).show();
                    }
                    else{
                        switch (step) {
                            case 0:
                                addr_mac = field.getText().toString();
                                asking.setText("Enter the position x: ");
                                step = 1;
                                break;
                            case 1:
                                String x = field.getText().toString();
                                X = Integer.parseInt(x);
                                asking.setText("Enter the position y: ");
                                step = 2;
                                break;
                            case 2:
                                String y = field.getText().toString();
                                Y = Integer.parseInt(y);
                                asking.setText("Enter the floor: ");
                                step = 3;
                                break;
                            case 3:
                                String floor = field.getText().toString();
                                Floor = Integer.parseInt(floor);
                                counter--;
                                list.create_beacon("C2:CB:A5:BD:A2:86", 0, 20, 40, Floor);
                                list.create_beacon("00:07:80:79:2D:A0", 0, 40, 60, Floor);
                                if (counter == 0){
                                    Intent setup = new Intent(Setup.this, Map.class);
                                    //setup.putExtra(EXTRA_LIST, beacon);
                                    //Log.i("Setup", "Avant");
                                    startActivity(setup);
                                    Log.i("Setup", "Après");
                                }
                                else {
                                    //asking.setText("Enter the mac adress: ");      //Ajouter une condition
                                    asking.setText("Enter the position x: ");
                                    Toast.makeText(getApplicationContext(), "Nouveau Beacon crée. " + " X: " + X + " Y: " + Y + " Floor: " + Floor, Toast.LENGTH_LONG).show();
                                    list.print_list();
                                    step = 1;
                                }
                                break;
                            default:
                                asking.setText("ERROR, please restart the beacon configuration");
                                step = 0;
                                break;
                        }
                    }
                    id_beacon.setText("Beacon number: " + String.valueOf(counter));
                }
            };
}
