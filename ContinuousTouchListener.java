import android.view.MotionEvent;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class will detect user interaction with a particular {@link View}.
 * If user continue touches that view, {@code onTouchesCompleted} will be fired.
 * If user delay in touches, then touch counter will be reset and started again.
 * @author Mohsin Khan
 * @date 2/13/2016
 */
public abstract class ContinuousTouchListener implements View.OnTouchListener{
    /**
     * It is the number that how much touches will be considered?
     */
    private int touches;

    /**
     * On which view this continuous touch listener will work?
     */
    private View view;

    /**
     * Total number of touches in particular time period, Value will be reset to 0 if time up
     */
    private static int counter = 0;

    /**
     * To control duration
     */
    private Timer timer;

    /**
     * Default constructor
     * @param touches how many touch you want to move ahead
     * @param view on which view, this listener will be set
     */
    public ContinuousTouchListener(int touches, View view) {
        this.touches = touches;
        this.view = view;
        this.view.setOnTouchListener(this);
    }

    /**
     * This event will be fired if user completes the touches
     * @param view on which this listener is working
     * @param touches current number of touches in that particular time
     */
    public abstract void onTouchesCompleted(View view, int touches);

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                counter++;
                if (counter < touches) {
                    scheduleTimer(1000);
                } else {
                    onTouchesCompleted(view, touches);
                    counter = 0;
                    timer.cancel();
                }
                break;
        }
        return false;
    }

    /**
     * This will reschedule timer every time it is called
     * @param ms this is duration in milliseconds
     */
    private void scheduleTimer(int ms) {
        if (this.timer != null) timer.cancel();
        this.timer = new Timer();
        timer.schedule(new TimerTask(){
            @Override
            public void run() {
                counter = 0;
                timer.cancel();
            }
        }, ms);
    }
}
