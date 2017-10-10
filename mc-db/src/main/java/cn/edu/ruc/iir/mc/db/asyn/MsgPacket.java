package cn.edu.ruc.iir.mc.db.asyn;

public class MsgPacket {
    public String from;
    public String to;
    public String msg;

    public MsgPacket(String from, String to, String msg) {
        this.from = from;
        this.to = to;
        this.msg = msg;
    }

    private static final String DIVID = "#";

    public static String pack(MsgPacket msgPack) {
        return msgPack.from + DIVID
                + msgPack.to + DIVID
                + msgPack.msg;
    }

    public static MsgPacket unpack(String msgStr) {
        String[] msg = msgStr.split(DIVID, 3);
        return new MsgPacket(msg[0], msg[1], msg[2]);
    }
}
