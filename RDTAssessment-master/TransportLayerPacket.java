
public class TransportLayerPacket {

    private int seqnum;
    private int acknum;
    private final byte[] data;
    // added:
    private final long checkSum;

    public TransportLayerPacket(byte[] data, long checkSum) {
        this.data = data;
        this.checkSum = checkSum;
    }

    public TransportLayerPacket(TransportLayerPacket pkt) {
        this.data = pkt.data;
        this.seqnum = pkt.seqnum;
        this.acknum = pkt.acknum;
        this.checkSum = pkt.checkSum;
    }

    public void setSeqnum(int seqnum) {
        this.seqnum = seqnum;
    }
    public void setAcknum(int acknum) {
        this.acknum = acknum;
    }
    public int getSeqnum() {
        return seqnum;
    }
    public int getAcknum() {
        return acknum;
    }
    public byte[] getData() {
        return data;
    }
    public long getCheckSum() {
        return checkSum;
    }
}
