package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.ThreadsCounter;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.messages.*;
//import com.sun.jmx.snmp.tasks.Task;
import java.util.Timer;
import java.util.*;


/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {

    private int speed;
    private int duration;
    private int currentTick;
    private Timer timer;


    public TimeService(int speed, int duration) {
        super("TimeService");

        this.duration = duration;
        this.speed = speed;
        currentTick = 1;
        timer = new Timer();

    }
    @Override

    protected void initialize() {
        ThreadsCounter.getInstance().Add1();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(currentTick == duration){
                    sendBroadcast(new TerminateBrodcast());
                    timer.cancel();
                }
                else{
                    sendBroadcast(new TickBroadcast(currentTick));
                    currentTick++;
                }
            }
        }, 0, speed);
        this.terminate();
    }
}