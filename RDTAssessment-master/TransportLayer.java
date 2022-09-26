import java.util.zip.CRC32;
import java.util.zip.Checksum;

public abstract class TransportLayer {

    String name;
    NetworkSimulator simulator;

    public TransportLayer(String name, NetworkSimulator simulator) {
        this.name = name;
        this.simulator = simulator;
    }
    /* call these: sendToNetworkLayer() and sendToApplicationLayer()
     * to send packets forward to layer above and below */

    public abstract void init();

    public abstract void rdt_send(byte[] data);

    public abstract void rdt_receive(TransportLayerPacket pkt);

    public abstract void timerInterrupt();

    public String getName() {
        return this.name;
    }

    // create checksum
    // ensure packets are not corrupted
    public long checkSum(byte[] data) {
        Checksum checksum = new CRC32();
        checksum.update(data);
        return checksum.getValue();
    }

    public TransportLayerPacket makePacket(byte[] data){
        long checksum = checkSum(data);
        return new TransportLayerPacket(data, checksum);
    }
}
