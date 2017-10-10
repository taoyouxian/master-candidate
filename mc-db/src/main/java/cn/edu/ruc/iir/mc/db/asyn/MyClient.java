package cn.edu.ruc.iir.mc.db.asyn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MyClient {
    private static final String SEVER_IP = "127.0.0.1";
    private static final int SEVER_PORT = 9876;

    private Socket mSocket;
    private ClientSocketUtil mSocketUtil;

    public MyClient() {
        try {
            mSocket = new Socket(SEVER_IP, SEVER_PORT);
            System.out.println("My socket: " + mSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        new Thread(new PushThread(), "PushThread").start();
        mSocketUtil = new ClientSocketUtil(mSocket);
    }

    private class ClientSocketUtil extends SocketUtil {//TODO socket from who to who

        public ClientSocketUtil(Socket socket) {
            super(socket);
        }

        @Override
        protected void onMsgSendStart(String msg) {
            System.out.println("[ME]: " + msg);
        }

        @Override
        protected void onMsgReceived(String msg) {
            System.out.println("[SERVER]: " + msg);
        }

        @Override
        protected void onSocketClosedRemote() {
            socketClosedRemote = true;
            System.out.println("Remote socket closed, input any words to quit.");
        }
    }

    private boolean socketClosedRemote = false;

    private class PushThread implements Runnable {
        @Override
        public void run() {
            System.out.println("PushThread ... start --- " + Thread.currentThread().getName());
            try {
                BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
                String msg = "";
                while (!socketClosedRemote && !(msg = sysIn.readLine()).equals(SocketUtil.MSG_QUIT)) {
                    mSocketUtil.pushMsg(msg);
                }
                sysIn.close();
                mSocketUtil.quit();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("PushThread ... end --- " + Thread.currentThread().getName());
        }
    }
}
