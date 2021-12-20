package bgu.spl.mics;

import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import bgu.spl.mics.example.services.ExampleEventHandlerService;
import bgu.spl.mics.example.services.ExampleMessageSenderService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.*;

public class MessageBusTest {

    private static MessageBus bus;
    private static MicroService eventSender;
    private static MicroService eventHandler;
    private static MicroService broadcastListner;
    private static MicroService broadcastListner2;
    private static MicroService broadcastListner3;
    private static ExampleEvent e;
    private static ExampleBroadcast b;
    private static String result;


    @Before
    public void setUp() throws Exception {
        bus = MessageBusImpl.getInstance();

        String[] arg = {"broadcast"};
        String[] arg1 = {"2"};
        String[] arg2 = {"2"};

        eventSender = new ExampleMessageSenderService("Tahel", arg);
        eventHandler = new ExampleEventHandlerService("Nadav",arg1);
        broadcastListner = new ExampleBroadcastListenerService("Liad", arg2);
        broadcastListner2 = new ExampleBroadcastListenerService("Gilad", arg2);
        broadcastListner3 = new ExampleBroadcastListenerService("Tuval", arg2);
        bus.register(eventSender);
        bus.register(eventHandler);
        bus.register(broadcastListner);
        bus.register(broadcastListner2);
        bus.register(broadcastListner3);


        result = "success";

        e = new ExampleEvent("Tahel");
        b = new ExampleBroadcast("Tahel");


    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSubscribeEvent() {
        bus.subscribeEvent(e.getClass(), eventHandler);
        assertTrue("service not subscribed to event", bus.isMSSubscribedToEvent(e.getClass(), eventHandler));
    }

    @Test
    public void testSubscribeBroadcast() {
        bus.subscribeBroadcast(b.getClass(), broadcastListner);
        assertTrue("service not subscribed to broadcast", bus.isMSSubscribedToBroadcast(b.getClass(), broadcastListner));
    }

    @Test
    public void testComplete() {
        bus.subscribeEvent(e.getClass(), eventHandler);
        Future<String> f = eventSender.sendEvent(e);
        bus.complete(e, result);
        assertTrue(f.isDone());
    }

    @Test
    public void testSendBroadcast() {
        bus.subscribeBroadcast(b.getClass(), broadcastListner);
        bus.subscribeBroadcast(b.getClass(), broadcastListner2);
        bus.subscribeBroadcast(b.getClass(), broadcastListner3);
        bus.sendBroadcast(b);
        assertTrue(bus.isBroadcastInQueue(b, broadcastListner));
        assertTrue(bus.isBroadcastInQueue(b, broadcastListner2));
        assertTrue(bus.isBroadcastInQueue(b, broadcastListner3));
    }

    @Test
    public void testSendEvent() {
        bus.subscribeEvent(e.getClass(), eventHandler);
        MicroService m = bus.peekEventQueue(e.getClass());
        Future<String> f = bus.sendEvent(e);
        assertNotNull(f);
        assertTrue(bus.isEventInQueue(e, m));
    }

    @Test
    public void testRegister() {
        assertTrue(bus.isMSRegistered(eventSender));
    }

    @Test
    public void testUnregister() {
        bus.subscribeEvent(e.getClass(), eventSender);
        bus.subscribeBroadcast(b.getClass(), eventSender);
        bus.unregister(eventSender);
        assertFalse(bus.isMSRegistered(eventSender));
        // change from original- added getClass
        assertFalse(bus.isMSSubscribedToMessage(e.getClass(), eventSender));
        assertFalse(bus.isMSSubscribedToMessage(b.getClass(), eventSender));
    }

    @Test
    public void testAwaitMessage() {
        LocalTime begin;
        LocalTime end;
        long i;
        // test if blocked and released
        bus.subscribeBroadcast(b.getClass(), broadcastListner);
        final Message[] message = new Message[1];
        Thread t2 = new Thread(() -> {
            try {
                message[0] = bus.awaitMessage(broadcastListner);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
        Thread t3 = new Thread(()-> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            bus.sendBroadcast(b);});
        begin = LocalTime.now();
        t2.start();
        t3.start();
        try {
            t2.join();
        } catch (Exception ignored){}
        end = LocalTime.now();
        i = begin.until(end, ChronoUnit.SECONDS);
        assertTrue("m2 not blocked", i > 1);
        assertEquals("wrong value", message[0], b);
    }

}