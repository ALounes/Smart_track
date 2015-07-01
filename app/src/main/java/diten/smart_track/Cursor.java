package diten.smart_track;


/**
 * Created by matthieu on 30/06/15.
 */
public class Cursor {
    float abscissa;
    float ordinate;
    float altitude;


    Cursor() {
        abscissa = 0;
        ordinate = 0;
        altitude = 0;
    }

    Cursor(float abscissa, float ordinate, float altitude) {
        this.abscissa = abscissa;
        this.ordinate = ordinate;
        this.altitude = altitude;
    }

    public float getAbscissa() {
        return abscissa;
    }

    public float getAltitude() {
        return altitude;
    }

    public float getOrdinate() {
        return ordinate;
    }

    public void setAbscissa(float abscissa) {
        this.abscissa = abscissa;
    }

    public void setAltitude(float altitude) {
        this.altitude = altitude;
    }

    public void setOrdinate(float ordinate) {
        this.ordinate = ordinate;
    }

}

