import java.util.Arrays;

import static java.lang.reflect.Array.getLength;

public class RDTReceiver extends TransportLayer {

    int seq = 0; // every time before we sent acknowledgment, set seq to packet.getAcknum()
    int ack = 0; // every time we receive data, add size(data) to ack

    public RDTReceiver(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }

    @Override
    public void init() {
        System.out.println("RECEIVER INITIALIZED");
    }

    @Override
    //send ack only if it is not corrupt
    public void rdt_send(byte[] data) {
        TransportLayerPacket sendPkt = makePacket(data);
        simulator.sendToNetworkLayer(this, sendPkt);
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {
        long ogCheckSum = pkt.getCheckSum();
        long newCheckSum = checkSum(pkt.getData());
        System.out.println("Received Packet: " + Arrays.toString(pkt.getData()) + "\n");
        System.out.println("New Checksum: " + newCheckSum + "\n");
        System.out.println("Original Checksum: " + ogCheckSum + "\n");

        // check for if checksums don't match
        // send back NAK
        if(newCheckSum != ogCheckSum) {
            TransportLayerPacket negPkt = makePacket(pkt.getData());
            negPkt.setSeqnum(seq);
            negPkt.setAcknum(ack);
            simulator.sendToNetworkLayer(this, negPkt);
            System.out.println("Packet Corrupt");
        } else if (ack >= pkt.getSeqnum()) {
            // we've already received this packet, send back actual location
            System.out.println("Duplicate packet, sending NAK with actual sequence location");
            TransportLayerPacket dupPkt = makePacket(pkt.getData());
            dupPkt.setSeqnum(seq);
            dupPkt.setAcknum(ack);
            simulator.sendToNetworkLayer(this, dupPkt);
        }
        else {
            // packet is healthy, send to application layer
            System.out.println("Healthy packet, sending ACK");
            simulator.sendToApplicationLayer(this, pkt.getData());
            TransportLayerPacket ackPkt = makePacket(pkt.getData());

            // set seq
            seq = pkt.getAcknum();
            System.out.println("R-DEBUGGING seqnum is " + seq);
            System.out.println("R-DEBUGGING acknum is " + ack + "\n");

            // set ack
            ack += getLength(pkt.getData());
            ackPkt.setAcknum(ack);
            System.out.println("R-DEBUGGING seqnum is " + seq);
            System.out.println("R-DEBUGGING acknum is " + ack + "\n");

            System.out.println("Sending ACK to network layer\n");
            ackPkt.setSeqnum(seq);
            ackPkt.setAcknum(ack);
            simulator.sendToNetworkLayer(this, ackPkt);
        }
    }

    @Override
    public void timerInterrupt() {

    }
}