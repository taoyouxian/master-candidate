package cn.edu.ruc.iir.mc.db.asyn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketUtil {
    public static final String MSG_QUIT = "quit";

    private Socket mSocket;
    private MsgQueue<String> mMsgQueue = new MsgQueue<String>();

    public SocketUtil(Socket socket) {
        mSocket = socket;
        new Thread(new Sender(), "Sender").start();
        new Thread(new Receiver(), "Receiver").start();
    }

    private class MsgQueue<T> {
        private static final int CAPACITY = 10;
        private List<T> mMsgs = new ArrayList<T>();

        public synchronized void push(T msg) {
            try {
                while (mMsgs.size() >= CAPACITY) {
                    wait();
                }
                mMsgs.add(msg);
                notifyAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public synchronized T pop() {
            T msg = null;
            try {
                while (mMsgs.size() <= 0) {
                    wait();
                }
                msg = mMsgs.get(0);
                mMsgs.remove(0);
                notifyAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return msg;
        }
    }

    private class Sender implements Runnable {
        @Override
        public void run() {
            System.out.println("Sender ... start --- " + Thread.currentThread().getName());
            try {
                PrintWriter out = new PrintWriter(mSocket.getOutputStream(), true);
                String msg = "";
                while (!(msg = mMsgQueue.pop()).equals(MSG_QUIT)) {
                    onMsgSendStart(msg);
                    out.println(msg);
                    onMsgSendEnd(msg, true);
                }
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Sender ... end --- " + Thread.currentThread().getName());
        }
    }

    private class Receiver implements Runnable {
        @Override
        public void run() {
            System.out.println("Receiver ... start --- " + Thread.currentThread().getName());
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                String msg = "";
                while ((msg = in.readLine()) != null) {
                    onMsgReceived(msg);
                }
                in.close();
                pushMsg(MSG_QUIT);//quit sender
                onSocketClosedRemote();
            } catch (IOException e) {
                //e.printStackTrace();
                onSocketClosedSelf();
            }
            System.out.println("Receiver ... end --- " + Thread.currentThread().getName());
        }
    }

    public final void pushMsg(String msg) {
        mMsgQueue.push(msg);
    }

    public final void quit() {
        pushMsg(MSG_QUIT);//quit sender
        try {
            if (mSocket != null) {
                mSocket.close();
                mSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final Socket getSocket() {
        return mSocket;
    }

    private void onSocketClosedSelf() {
    }

    protected void onSocketClosedRemote() {
    }

    protected void onMsgSendStart(String msg) {
    }

    protected void onMsgSendEnd(String msg, boolean success) {
    }

    protected void onMsgReceived(String msg) {
    }

    protected void onMsgInput(String msg) {
    }
}
