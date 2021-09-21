package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Message;

/**
 * This event is sent by the Time service to update a tick in each micro service
 */

public class TickBroadcast implements Broadcast {
    private int tick;


    public TickBroadcast(int tick) {
        this.tick = tick;

    }

    public int getTick() {
        return tick;
    }


}
