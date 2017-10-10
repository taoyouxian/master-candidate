package cn.edu.ruc.iir.mc.db.asyn;

public class Client {
    public static void main(String[] args) {
        System.out.println("client main");
        new MyClient().connect();
    }
}
