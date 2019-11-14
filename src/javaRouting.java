import RIP.*;

import java.util.concurrent.LinkedBlockingQueue;

// Routz RIP implementation challenge :)
// RIPv1 as per https://tools.ietf.org/html/rfc1058 in ripListener
// RIPv2 as per https://tools.ietf.org/html/rfc2453 in ripListener
// RIPv2 MD5: https://tools.ietf.org/html/rfc2082 in ripListener

public class javaRouting {

    // Lets define some addresses we want to send back
    public static final String[] defaultRoute = { "0.0.0.0", "0.0.0.0" }; // We will send a default of 0.0.0.0/0 using RIP
    public static final String[] ourNetwork = { "10.50.75.0", "255.255.255.0" }; // We will send a default of 0.0.0.0/0 using RIP

    // define RIP command tags
    public static final byte RIP_REQUEST = 0x01, RIP_RESPONSE = 0x02, RIP_TRACEON = 0x03, RIP_TRACEOFF =0x04, RIP_RESERVED = 0x05;

    // Set our RIP Timers
    public static int updateTimer = 30;
    public static int intervalTimer = 30;
    public static int holdTimer = 30;
    public static int flushTimer = 30;

    // create a routing table to store received routs
    private static routingTable routingTable = new routingTable();

    public static void main(String[] args) {
        System.out.println("Routz Coding Challenge v0.5");

        // construct a synchronised list to be used as  queue for exchange of RIP packets between broadcast and multicast listening threads
        LinkedBlockingQueue routeHandler = new LinkedBlockingQueue<>(1024);

        // start thread for receiving RIPv2 traffic (As RIPv2 is backwards compatible; this one also handles RIPv1 traffic
        Thread ripListener = new Thread (new ripListener(routeHandler));
        ripListener.start();

        // start a thread for maintaining routing table entries
        // todo: cleanup routing tables.

        while (true) {
           try {
               // fetch routes from queue and insert into into our 'routing' table
               ripRouteEntry routeEntry = (ripRouteEntry)routeHandler.take();
               routingTable.addRipRouteEntry(routeEntry);

           } catch (InterruptedException e) {
               System.out.println("Cannot sleep! Phun intended :)");
           }
        }
    }
}