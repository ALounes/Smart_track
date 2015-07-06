package diten.smart_track;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lounes on 02/07/15.
 */
public class MyTimer {
    Timer t;
    final int time = 2000;
    Map myMap= null;

    MyTimer(Map map){
        myMap = map;
    }

    public void  RepetAction() {
        t = new Timer();
        t.schedule(new BleScaning(myMap), 0, time );
    }

    class BleScaning extends TimerTask {

        Map myMap = null;

        BleScaning(Map map){
            myMap = map;
        }

        public void run()
        {
            Log.i("MyTimer", " TIMER TIMER TIMER TIMER §!!!!!!!!!!!!!!!!!!!!!");
            myMap.scanLeDevice(true);
        }
    }
}