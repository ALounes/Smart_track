package diten.smart_track;

import android.util.Log;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lounes on 02/07/15.
 */
public class MyTimer2 {
    Timer t;
    final int time = 3000;
    Map myMap= null;

    MyTimer2(Map map){
        myMap = map;
    }

    public void  RepetAction() {
        t = new Timer();
        t.schedule(new Counter(myMap), 0, time );
    }

    class Counter extends TimerTask {

        Map myMap = null;

        Counter(Map map){
            myMap = map;
        }

        public void run()
        {
            Log.i("MyTimer2", " proche beacon est : " + myMap.list.min_distance() );
        }
    }
}