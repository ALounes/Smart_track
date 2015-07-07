package diten.smart_track;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by matthieu on 29/06/15.
 */
public class Beacon implements Parcelable {

    String  addr_mac;
    int     RSSI;
    float   abscissa;
    float   ordinate;
    float   altitude;
    boolean detected;

    Beacon(String addr_mac, int RSSI, float abscissa, float ordinate, float altitude)
    {
        this.addr_mac = addr_mac;
        this.RSSI = RSSI;
        this.abscissa = abscissa;
        this.ordinate = ordinate;
        this.altitude = altitude;
        this.detected = false;
    }


    /*********************************************************************************/
    /********************** Les Methodes Pour les parcelable *************************/
    /*********************************************************************************/

    // Constructeur qui sera appelé lords de la deparcelabilisation
    public Beacon(Parcel in)
    {
        this.getFromParcel(in);
    }

    // sert a decrire le contenu de notre parcel et plus precisement
    // le nombre d'objet dans un parcel
    @Override
    public int describeContents(){
        return 0;
    }

    // permet de remplir notre objet a paritir du parcel
    public void getFromParcel(Parcel in)
    {
        boolean[] bool = new boolean[]{this.detected};
        in.readBooleanArray(bool);

        this.setAddr_mac(in.readString());
        this.setRSSI(in.readInt());
        this.setAbscissa(in.readFloat());
        this.setOrdinate(in.readFloat());
        this.setAltitude(in.readFloat());
        this.setDetected(bool[0]);
    }

    @Override
    public void writeToParcel(Parcel dest, int flag)
    {
        dest.writeString(get_addr_mac());
        dest.writeInt(getRSSI());
        dest.writeFloat(getAbscissa());
        dest.writeFloat(getOrdinate());
        dest.writeFloat(getAltitude());
        dest.writeBooleanArray(new boolean[]{getDectected()});
    }


    /*********************************************************************************/
    /*************************** Les Methodes de SET *********************************/
    /*********************************************************************************/

    void setAddr_mac(String addr_mac){
        this.addr_mac = addr_mac;
    }

    void setRSSI(int RSSI){
        this.RSSI = RSSI;
    }

    void setAbscissa(float abscissa){
        this.abscissa = abscissa;
    }

    void setOrdinate(float ordinate){
        this.ordinate = ordinate;
    }

    void setAltitude(float altitude) { this.altitude = altitude; }

    void setDetected(boolean detected) { this.detected = detected; }


    /*********************************************************************************/
    /*************************** Les Methodes de GET *********************************/
    /*********************************************************************************/

    String get_addr_mac(){
        return addr_mac;
    }

    int getRSSI(){
        return RSSI;
    }

    float getAbscissa(){
        return abscissa;
    }

    float getOrdinate(){
        return ordinate;
    }

    float getAltitude() {
        return altitude;
    }

    boolean getDectected(){ return detected;}
}
