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
    final int time = 2;

    public void  RepetAction() {
        t = new Timer();
        t.schedule(new BleScaning(), 0, time * 1000);
    }

    class BleScaning extends TimerTask {


        public void run()
        {
            Log.i("MyTimer", " TIMER TIMER TIMER TIMER §!!!!!!!!!!!!!!!!!!!!!");

        }
    }
}