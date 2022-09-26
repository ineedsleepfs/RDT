import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.reflect.Array.getLength;

public class RDTSender extends TransportLayer {

    ArrayList<byte[]> sendArray; //where we store our packets that are to be sent
    boolean sendPkt; // first packet
    boolean timer = false; // check for if timer is active

    int seq = 0; // every time before we send data, add size(data) to seq
    int ack = 0; // every time we receive acknowledgment, set ack to packet.getAcknum()

    public RDTSender(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }

    @Override
    public void init() {
        sendArray = new ArrayList<>();
        sendPkt = true;
    }

    @Override
    public void rdt_send(byte[] data) {
        System.out.println("Adding data to queue\n");
        sendArray.add(data); //send data to get added to list
        System.out.println("DEBUGGING sendArray size is " + sendArray.size());

        // code to send first packet if none have been sent
        if (sendPkt) {
            sendPkt = false;
            // prepare data
            System.out.println("Making Packet\n");
            System.out.println("Sending: " + Arrays.toString(sendArray.get(0)) + "\n");
            TransportLayerPacket pkt = makePacket(sendArray.get(0).clone());

            // prepare seq/ack and send data
            System.out.println("Sending to network layer\n");
            pkt.setSeqnum(seq);
            pkt.setAcknum(ack);
            simulator.sendToNetworkLayer(this, pkt); // send packet

            // set seq
            seq += getLength(pkt.getData());
            System.out.println("DEBUGGING seqnum is " + seq);
            System.out.println("DEBUGGING acknum is " + ack + "\n");

            // ngl I have no idea how the timer works (but I tried)
            simulator.startTimer(this, 300.0);
        }
    }

    @Override
    //if a corrupted file is received then resend packet
    //if no more packets are to be sent, end simulation?
    public void rdt_receive(TransportLayerPacket pkt) {
        long newCheckSum = checkSum(pkt.getData());
        long ogCheckSum = pkt.getCheckSum();
        System.out.println("Receiving Packet: " + pkt + "\n");
        System.out.println("New Checksum: " + newCheckSum + "\n");
        System.out.println("Original Checksum: " + ogCheckSum + "\n");
        System.out.println("Packet Seq Contents: " + pkt.getSeqnum() + "\n");
        System.out.println("Packet Ack Contents: " + pkt.getAcknum() + "\n");

        if (newCheckSum != ogCheckSum) {
            // checksum faulty, resends packet to network layer
            System.out.println("Corrupted Packet Received\n");
            TransportLayerPacket resend = makePacket(sendArray.get(0).clone()); // creates a packet with the current data
            System.out.println("Resending packet to network layer\n");
            resend.setSeqnum(seq);
            resend.setAcknum(ack);
            simulator.sendToNetworkLayer(this, resend);
        } else if (ack >= pkt.getAcknum()) {
            // NAK is received, resends packet to network layer
            System.out.println("NAK received, resending packet\n");
            TransportLayerPacket resend = makePacket(sendArray.get(0).clone());
            System.out.println("Resending packet to network layer\n");
            resend.setSeqnum(seq);
            resend.setAcknum(ack);
            simulator.sendToNetworkLayer(this, resend);
        } else {
            // ACK is received, sends next packet if one exists
            System.out.println("ACK received\n");
            simulator.stopTimer(this);

            // set ack
            ack = pkt.getAcknum();
            System.out.println("DEBUGGING seqnum is " + seq);
            System.out.println("DEBUGGING acknum is " + ack + "\n");

            /*
             this may look like two identical statements, but first we need to check whether
             the array is empty for removing the previously sent packet, and then we need to
             check whether the remaining array is empty for creating the next packet
            */
            if (!sendArray.isEmpty()) { sendArray.remove(0); } // removes previous data in array
            if (!sendArray.isEmpty()) {
                // prepare next packet of data
                TransportLayerPacket nextPkt = makePacket(sendArray.get(0).clone());

                // prepare seq/ack and send next packet of data
                System.out.println("Sending packet to network layer\n");
                nextPkt.setSeqnum(seq);
                nextPkt.setAcknum(ack);
                simulator.sendToNetworkLayer(this, nextPkt);
                simulator.startTimer(this, 200.0);

                // set seq
                seq += getLength(nextPkt.getData());
                System.out.println("DEBUGGING seqnum is " + seq);
                System.out.println("DEBUGGING acknum is " + ack + "\n");
            }
        }
    }

        @Override
        // TODO: make this
        public void timerInterrupt () {
            System.err.println("INTERRUPTED");
            System.out.println(" A packet is corrupted or not received.");

            // stop any current timers
            if (timer) {
                simulator.stopTimer(this);
                timer = false;
            }

            // rebuild packet
            if (!sendArray.isEmpty()) {
                TransportLayerPacket resend = makePacket(sendArray.get(0).clone()); // creates a packet with the current data
                System.out.println("Resending packet to network layer\n");
                simulator.sendToNetworkLayer(this, resend);

                // restart timer
                simulator.startTimer(this, 1000.0);
                timer = true;
            }
        }
    }