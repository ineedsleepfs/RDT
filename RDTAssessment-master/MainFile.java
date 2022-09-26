public class MainFile {

    public static void main(String[] args) {
        NetworkSimulator sim = new NetworkSimulator(10, 0.0, 0.0, 10.0, false, 3);
        // NetworkSimulator sim = new NetworkSimulator(10, 0.05, 0.05, 10.0, false, 3);
        // NetworkSimulator sim = new NetworkSimulator(20, 0.0, 0.0, 10.0, false, 1); // test on a different number of messages

        // TODO: Set the sender   (sim.setSender)
        RDTSender sender = new RDTSender("sender", sim);
        sim.setSender(sender);

        // TODO: Set the receiver (sim.setReceiver)
        RDTReceiver receiver = new RDTReceiver("receiver", sim);
        sim.setReceiver(receiver);

        sim.runSimulation();
    }
}











// IGNORE THIS
/*
        public Event(double evTime, EventType evType, TransportLayer evEntity) {
            this(evTime, evType, evEntity, null); // initially
        }
        (bidirectional && (rng.nextDouble() > 0.5)) ? receiver : sender);
        // if bidirectional is true and rng greater than 0.5, event entity = receiver, else sender
        */
