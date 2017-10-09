package cn.edu.ruc.iir.mc.db.basic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @version V1.0
 * @Package: cn.edu.ruc.iir.mc.db.basic
 * @ClassName: TalkClient
 * @Description: TalkClient
 * @author: taoyouxian
 * @date: Create in 2017-10-07 11:06
 **/
public class TalkClient {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 4700);
            BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter os = new PrintWriter(socket.getOutputStream());
            BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String readline;
            readline = sin.readLine();
            while (!readline.equals("bye")) {
                os.println(readline);
                os.flush();
                System.out.println("TalkClient:" + readline);
                System.out.println("Server:" + is.readLine());
                readline = sin.readLine();
            }
            os.close();
            is.close();
            socket.close();
        } catch (Exception e) {
            System.out.println("Error" + e);
        }
    }
}
