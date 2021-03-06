package diten.smart_track;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by matthieu on 26/06/15.
 */
public class List_BLE extends ArrayList<Beacon> implements Parcelable {

    public List_BLE() {}

    public List_BLE(Parcel in)
    {
        this.getFromParcel(in);
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public List_BLE createFromParcel(Parcel in)
        {
            return new List_BLE(in);
        }

        @Override
        public Object[] newArray(int size) {
            return null;
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        //Taille de la liste
        int size = this.size();
        dest.writeInt(size);

        for(int i=0; i < size; i++)
        {
            Beacon beacon = this.get(i); //On vient lire chaque objet personne
            dest.writeParcelable(beacon, flags);
        }
    }

    public void getFromParcel(Parcel in)
    {
        // On vide la liste avant tout remplissage
        this.clear();

        //Récupération du nombre d'objet
        int size = in.readInt();

        //On repeuple la liste avec de nouveau objet
        for(int i = 0; i < size; i++)
        {
            Beacon beacon = in.readParcelable(Beacon.class.getClassLoader());
            this.add(beacon);
        }

    }

    /*******************************************************************************/
    /*******************************************************************************/
    /*******************************************************************************/

    //ArrayList list = new ArrayList<Beacon>();

    void add_beacon(Beacon beacon)
    {
        this.add(beacon);
    }

    void create_beacon(String mac, int RSSI, int numPacket, float X, float Y, float Z)
    {
        add_beacon(new Beacon(mac, RSSI, numPacket, X, Y, Z));
    }

    void remove_beacon(Beacon beacon)
    {
        this.remove(beacon);
    }

    int size_list()
    {
        return this.size();
    }

    Beacon get_beacon(int index)
    {
        return (Beacon)this.get(index);
    }

    String get_addr_mac_index(int i)
    {
        String addr_mac = ((Beacon)(this.get(i))).get_addr_mac();
        return addr_mac;
    }

    float get_abscissa_index(int i)
    {
        float abscissa = ((Beacon)(this.get(i))).getAbscissa();
        return abscissa;
    }

    float get_ordinate_index(int i)
    {
        float ordinate = ((Beacon)(this.get(i))).getOrdinate();
        return ordinate;
    }

    float get_altitude_index(int i)
    {
        float altitude = ((Beacon) (this.get(i))).getAltitude();
        return altitude;
    }

    boolean get_detected_index(int i)
    {
        boolean detected = ((Beacon) (this.get(i))).getDectected();
        return detected;
    }

    float get_RSSI_index(int i)
    {
        float RSSI = ((Beacon) (this.get(i))).getRSSI();
        return RSSI;
    }

    void print_list()
    {
        for(int i = 0; i < this.size(); i++){
            Log.i("List_BLE", " ABSCISSE: " + ((Beacon)(this.get(i))).getAbscissa() + " ORDONNNEE: " + ((Beacon)(this.get(i))).getOrdinate() + "ALTITUDE: " + ((Beacon)(this.get(i))).getAltitude() );
            Log.i("List_BLE", " RSSI: " + ((Beacon) (this.get(i))).getRSSI() + " ADDR MAC: " + ((Beacon) (this.get(i))).get_addr_mac());
        }
    }

    //Cross the list of beacons and resend the address mac of the beacon which it RSSI is the smallest.
    public String min_RSSI()
    {
        float RRSI = -256;
        String addr_mac = "EMPTY";
        for(int i = 0; i < this.size(); i++){
            if((RRSI < get_RSSI_index(i)) && (get_beacon(i).getDectected())) {
                RRSI = get_RSSI_index(i);
                addr_mac = get_addr_mac_index(i);
            }
        }
        // Log.i("List_BLE", " plus proche " + addr_mac);
        list_clear_dectection();
        return addr_mac;
    }

    //Cross the list of beacons and resend the address mac of the beacon which it detected
    public String max_Packet()
    {
        int numpacket = 0;
        String addr_mac = "EMPTY";
        Beacon beacon = null;

        for(int i = 0; i < this.size(); i++){
            beacon = get_beacon(i);
            if ((beacon.getNumPacket() > numpacket)&&(beacon.getDectected())) {
                numpacket = beacon.getNumPacket();
                addr_mac = get_addr_mac_index(i);
            }
        }
        // Log.i("List_BLE", " plus proche " + addr_mac);

        return addr_mac;
    }

    public String min_distance()
    {
        String mac = max_Packet();

        list_clear_dectection();
        list_clear_numPacket();

        return mac;
    }

    //Obtain the index of the beacon thanks to its mac adress
    public int get_index_by_addr_mac(String addr_mac)
    {
        // Log.i("List_BLE", "adr MAC" + (String)addr_mac);
        for (int i = 0; i < this.size(); i++){
            if (get_addr_mac_index(i).compareTo(addr_mac) == 0)
                return i;
        }
        //Log.i("List_BLE", "Problem function get_index_by_adrr_mac");
        return 0;
    }

    //Put attribute 'detected' to false for all beacons
    public void list_clear_dectection()
    {
        for (int i = 0; i < this.size(); i++){
            (get_beacon(i)).setDetected(false);
        }
    }

    //Put attribute 'numPacket' to 0 for all beacons
    public void list_clear_numPacket()
    {
        for (int i = 0; i < this.size(); i++){
            (get_beacon(i)).setNumPacket(0);
        }
    }

    //Find the beacon thanks to its mac adress and put the RSSI value to the good beacon
    public void list_find_by_add(String addr_mac, int RSSI)
    {
        for (int i = 0; i < this.size(); i++)
        {
            if(get_addr_mac_index(i).compareTo(addr_mac) == 0)
            {
                Beacon mybeacon = get_beacon(i);

                mybeacon.setRSSI(RSSI);
                mybeacon.setDetected(true);
                mybeacon.incrementNumPacket();
            }
        }
    }

    public void list_static_initialisation()
    {
        create_beacon("C2:CB:A5:BD:A2:86", 0, 0, 200, 400, 0);
        create_beacon("00:07:80:79:2D:A0", 0, 0, 400, 600, 0);
    }
}






