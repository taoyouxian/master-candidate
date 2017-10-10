package cn.edu.ruc.iir.mc.db.asyn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MyServer {
    private static final int PORT = 9876;
    private static final int MAX_CLIENT_COUNT = Integer.MAX_VALUE;

    private ServerSocket mSS;
    private Map<String, Client> mClients = new HashMap<String, Client>();

    public MyServer() {
        try {
            mSS = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer() {
        new Thread(new PushThread(), "PushThread").start();
        try {
            while (mClients.size() < MAX_CLIENT_COUNT) {
                Socket socket = mSS.accept();
                new Thread(new LoginThread(socket), "LoginThread").start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class LoginThread implements Runnable {
        private static final int MAX_TRY = 3;
        private Socket mSocket;

        public LoginThread(Socket socket) {
            mSocket = socket;
        }

        @Override
        public void run() {
            System.out.println("LoginThread ... start --- " + Thread.currentThread().getName());
            String id = waitForLogin();
            if (id != null) {
                Client client = new Client(mSocket, id);
                mClients.put(id, client);
                System.out.println("A new socket(" + mSocket + ") connected." +
                        " Client size: " + mClients.size());
//                tellAllClientChanged();
            } else {
                try {
                    mSocket.close();
                    mSocket = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("LoginThread ... end --- " + Thread.currentThread().getName());
        }

        private String waitForLogin() {
            String loginId = null;
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                PrintWriter out = new PrintWriter(mSocket.getOutputStream(), true);
                for (int i = MAX_TRY; i > 0; i--) {
                    out.println("Login: you can try " + i + " times.");
                    out.println("Please input your id:");
                    String id = in.readLine();
                    if (!isUserExist(id)) {
                        out.println("User (" + id + ") not exist!");
                        continue;
                    }
                    out.println("Please input your password:");
                    String pwd = in.readLine();
                    if (!isPwdCorrect(id, pwd)) {
                        out.println("Password error!");
                        continue;
                    }
                    if (isRepeatLogin(id)) {
                        out.println("User (" + id + ") is already online!");
                        continue;
                    } else {
                        loginId = id;
                        break;
                    }
                }
                //in.close();//do not close here
                if (loginId == null) {
                    out.println("I'm so sorry! Login failed!");
                } else {
                    out.println("Welcome " + loginId + "! Login success!");
                }
            } catch (IOException e) {
                //e.printStackTrace();
            }
            return loginId;
        }

        private boolean isUserExist(String id) {
            return true;//TODO
        }

        private boolean isPwdCorrect(String id, String pwd) {
            return (id.equals(pwd));//TODO
        }

        private boolean isRepeatLogin(String id) {
            return mClients.containsKey(id);
        }
    }

//    private void tellAllClientChanged() {
//        Iterator<String> iterator = mClients.keySet().iterator();
//        while (iterator.hasNext()) {
//            String id = iterator.next();
//            Client client = mClients.get(id);
//            Socket socket = client.getSocket();
//            String ip = socket.getInetAddress().toString();
//            int port = socket.getPort();
//            pushMsgToAllClient("-------------["+id+"]" + ip + ":" + port);
//        }
//    }

    class Client extends SocketUtil {
        private String mId;

        public Client(Socket socket, String id) {
            super(socket);
            mId = id;
        }

        @Override
        protected void onMsgSendStart(String msg) {
            System.out.println("to <" + mId + ">: " + msg);
        }

        @Override
        protected void onMsgReceived(String msg) {
            System.out.println("[" + mId + "]: " + msg);
            pushMsg("Your msg is: " + msg);
        }

        protected void onSocketClosedRemote() {
            mClients.remove(mId);
            System.out.println("Client (" + mId + ") offline. Client size: " + mClients.size());
//            tellAllClientChanged();
        }
    }

    private class PushThread implements Runnable {
        @Override
        public void run() {
            System.out.println("PushThread ... start --- " + Thread.currentThread().getName());
            try {
                BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
                String msg = "";
                while (!(msg = sysIn.readLine()).equals(SocketUtil.MSG_QUIT)) {
                    pushMsgToAllClient(msg);
                }
                sysIn.close();
                closeServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("PushThread ... end --- " + Thread.currentThread().getName());
        }
    }

    private void pushMsgToAllClient(String msg) {
        Iterator<String> iterator = mClients.keySet().iterator();
        while (iterator.hasNext()) {
            String id = iterator.next();
            Client client = mClients.get(id);
            System.out.println("push message to [" + id + "]" + client);
            client.pushMsg(msg);
        }
    }

    private void closeServer() {
        Iterator<String> iterator = mClients.keySet().iterator();
        while (iterator.hasNext()) {
            String id = iterator.next();
            Client client = mClients.get(id);
            System.out.println("Close [" + id + "]" + client);
            client.quit();
        }
        try {
            mSS.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
