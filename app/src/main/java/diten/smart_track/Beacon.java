package diten.smart_track;

/**
 * Created by matthieu on 29/06/15.
 */
public class Beacon {
     String addr_mac;
     int RSSI;
     float abscissa;
     float ordinate;
     float altitude;

     Beacon(String addr_mac, int RSSI, float abscissa, float ordinate, float altitude){
        this.addr_mac = addr_mac;
        this.RSSI = RSSI;
        this.abscissa = abscissa;
        this.ordinate = ordinate;
        this.altitude = altitude;
    }

    void setAddr_mac(String addr_mac){
        this.addr_mac = addr_mac;
    }

    void setRSSI(int RSSI){
        this.RSSI = RSSI;
    }

    void setAbscissa(int abscissa){
        this.abscissa = abscissa;
    }

    void setOrdinate(int ordinate){
        this.ordinate = ordinate;
    }

    void setAltitude(int altitude) { this.altitude = altitude; }

    String get_addr_mac(){ return addr_mac; }
    int getRSSI(){
        return RSSI;
    }
    float getAbscissa(){
        return abscissa;
    }
    float getOrdinate(){
        return ordinate;
    }
    float getAltitude() { return altitude; }
}
