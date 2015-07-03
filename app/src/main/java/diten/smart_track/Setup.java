package diten.smart_track;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by matthieu on 03/07/15.
 */
public class Setup extends Activity {
    TextView id_beacon = null;
    TextView asking = null;
    EditText field = null;
    Button validate = null;
    List_BLE list = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup);
        id_beacon = (TextView) findViewById(R.id.id_beacon);
        field = (EditText) findViewById(R.id.field);
        validate = (Button) findViewById(R.id.validate);
        asking = (TextView) findViewById(R.id.asking);

        validate.setOnClickListener(StartListner);
    }

    private View.OnClickListener StartListner =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = 0;  //Compteur de Beacon
                    id_beacon.setText("Beacon n° " + String.valueOf(i));
                    list.create_beacon("02:L5:32:65", 45, 56, 54, 78);
                }
            };
}
