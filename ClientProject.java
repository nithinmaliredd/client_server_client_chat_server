import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientProject {
    private static volatile AtomicBoolean complete = new AtomicBoolean();
    public static void main(String[] arg) throws Exception {
        if(arg.length != 2) {
            System.out.println("Port number or Server name is not specified");
            System.exit(1);
        }
        int port = Integer.parseInt(arg[0]);
        String serverName = arg[1];
        ClientData object = new ClientData(serverName, port);
        object.execute();
    }

    private static class ClientData {
        private String serverName;
        private int port;
        public ClientData(String serverName, int port) {
            this.serverName = serverName;
            this.port = port;
        }
        public void execute() throws Exception {
            InetAddress clientIP = InetAddress.getByName(serverName);
            Socket clientSocket = new Socket(clientIP, port);
            Thread outThread = new Thread(new Runnable(){
        
                @Override
                public void run() {
                    try {
                        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        String msg;
                        while(!complete.get()) {
                            msg = read.readLine();
                            if(msg.equals("#logout")) {
                                synchronized (complete) {
                                    complete.set(true);
                                }
                            }
                            out.println(msg);
                        }
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            outThread.start();
    
            Thread inThread = new Thread(new Runnable(){
            
                @Override
                public void run() {
                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        String com = in.readLine();
                        while(!complete.get()) {
                            System.out.println(com);
                            com = in.readLine();
                        }
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            inThread.start();
        }
    }
}