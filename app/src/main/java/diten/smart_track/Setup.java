package diten.smart_track;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

    private int counter = 1;
    private int step = 3;
    private String addr_mac = null;
    private int X = 0;
    private int Y = 0;
    private int Floor = 0;

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

        //field.setOnEditorActionListener(StartListner2);
    }

    /*Structure du fichier:Il faut un écouteur sur le bouton valider. Ce bouton est l'élement le plus important de la classe Setup. Grâce à ce bouton,
    * on peut avancer dans les étapes. Un compteur de type private permet de savoir le nombre de beacon à configurer, il faut l'incrémenter à chaque fois que
    * toutes les informations nécessaires ont été insérées.
    * Dès qu'on clique sur le bouton valider, on récupère ce qu'il y a dans le champ EditText. S'il n'y a rien, on ne fait rien. S'il y a un nombre,
    * on le stocke dans une variable.
    * Enfin, lorsque toutes les informations ont été données, on crée un Beacon avec ttes les données en paramètre puis on l'ajoute à la liste des
    * beacons.
    * Enfin lorsque nous arrivons à la fin du compteur, on fait un Toast pour dire que c'est la fin puis on passe à la map.
    * Sur la map, on place les balises au bon endroit!*/

    /*Explication algorithmique passage des différents résultats:
    A chaque fois qu'on clique sur le bouton Validé, on change d'étape:
    -> Il y a 4 étapes:
    Etape 0: adresse MAC insérer une adresse mac et gérer un type d'érreur (genre format erreur). On incrémente la variable si tout va bien.
    Etape 1; position x!
    Etape2 : position y!
    Etape 3: numéro de l'étage!
    Etape ERREUR; faire quelque chose si jamais il y a une erreur!!!
     */

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
                                    startActivity(setup);
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
