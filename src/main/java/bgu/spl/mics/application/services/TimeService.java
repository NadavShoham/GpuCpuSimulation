package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	private int speed;
	private int duration;
	private int currTime;

	public TimeService(int speed, int duration) {
		super("TimeService");
		this.speed = speed;
		this.duration = duration;
		this.currTime = 1;
	}

	@Override
	protected void initialize() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (currTime == duration){
					sendBroadcast(new TerminateBroadcast());
					timer.cancel();
				} else {
					sendBroadcast(new TickBroadcast(currTime));
					currTime++;
					// TODO print
					//if (currTime % 1000 == 0) System.out.println("TickTIme: " + currTime);
				}
			}
		}, 0, 1);
		terminate();
	}

}
