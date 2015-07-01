package diten.smart_track;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by matthieu on 26/06/15.
 */
public class List_BLE {

    ArrayList list = new ArrayList<Beacon>();

    void add_beacon(Beacon beacon){
        list.add(beacon);
    }

    void remove_beacon(Beacon beacon){
        list.remove(beacon);
    }

    int size_list(){
        return list.size();
    }

    String get_addr_mac_index(int i){
        String addr_mac = ((Beacon)(list.get(i))).get_addr_mac();
        return addr_mac;
    }

    float get_abscissa_index(int i){
        float abscissa = ((Beacon)(list.get(i))).getAbscissa();
        return abscissa;
    }

    float get_ordinate_index(int i){
        float ordinate = ((Beacon)(list.get(i))).getOrdinate();
        return ordinate;
    }

    float get_altitude_index(int i) {
        float altitude = ((Beacon) (list.get(i))).getAltitude();
        return altitude;
    }

    float get_RSSI_index(int i) {
        float RSSI = ((Beacon) (list.get(i))).getRSSI();
        return RSSI;
    }

    void print_list(){
        for(int i = 0; i < list.size(); i++){
            Log.i("List_BLE", " ABSCISSE: " + ((Beacon)(list.get(i))).getAbscissa() + " ORDONNNEE: " + ((Beacon)(list.get(i))).getOrdinate() + "ALTITUDE: " + ((Beacon)(list.get(i))).getAltitude() );
            Log.i("List_BLE", " RSSI: " + ((Beacon)(list.get(i))).getRSSI() + " ADDR MAC: " + ((Beacon)(list.get(i))).get_addr_mac());
        }
    }

    //Cross the list of beacons and resend the address mac of the beacon which it RSSI is the smallest.
    public String min_distance(List_BLE list_ble) {
        float RRSI = get_RSSI_index(0);
        String addr_mac = get_addr_mac_index(0);
        for(int i = 1; i < list_ble.size_list(); i++){
            if(RRSI > get_RSSI_index(i)) {
                RRSI = get_RSSI_index(i);
                addr_mac = get_addr_mac_index(i);
            }
        }
        return addr_mac;
    }
}



