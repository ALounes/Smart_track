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
    MainActivity myMain = null;

    MyTimer(MainActivity main){
        myMain = main;
    }

    public void  RepetAction() {
        t = new Timer();
        t.schedule(new BleScaning(myMain), 0, time );
    }

    class BleScaning extends TimerTask {

        MainActivity myMain = null;

        BleScaning(MainActivity main){
            myMain = main;
        }

        public void run()
        {
            Log.i("MyTimer", " TIMER TIMER TIMER TIMER §!!!!!!!!!!!!!!!!!!!!!");
            //myMain.scanLeDevice(true);
        }
    }
}