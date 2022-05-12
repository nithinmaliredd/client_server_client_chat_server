import java.io.*;
import java.net.*;
import java.util.AbstractCollection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.lang.String;

public class ServerProject {
    private static Map<String, PrintWriter> clientList = new HashMap<String, PrintWriter>();
    public static void main(String[] arg) throws Exception {
        if(arg.length != 1) {
            System.out.println("Port number is not specified!");
            System.exit(1);
        }
        int port = Integer.parseInt(arg[0]);
        ExecutorService threads = Executors.newFixedThreadPool(10);
        try {
            ServerSocket server = new ServerSocket(port);
            while(true) {
                Socket client = server.accept();
                ClientRequest object = new ClientRequest(client);
                threads.execute(object);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static class ClientRequest implements Runnable {
        private Socket client;
        private String name;
        private PrintWriter output;
        public ClientRequest(Socket client) {
            this.client = client;
        }
        public void run() {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                output = new PrintWriter(client.getOutputStream(), true);
                while(true) {
                    output.println("Name:");
                    name = input.readLine();
                    if(name == null || name == "Name:")
                        return;
                    synchronized(clientList) {
                        if(!clientList.keySet().contains(name) && !name.isEmpty()) {
                            clientList.put(name, output);
                            break;
                        }
                    }
                }
                while(true) {
                    output.println("Accepted: Options 1.)#logout 2.)#client");
                    String message = input.readLine();
                    String[] com;
                    if(message.toLowerCase().equals("#logout"))
                        return;
                    else if(message.toLowerCase().startsWith("#client")) {
                        output.println(clientList.keySet());
                        output.println("Enter client name: ");
                        String cname = input.readLine();
                        String temp = cname;
                        while(!temp.equals("#exit")) {
                            temp = input.readLine();
                            for (String var : clientList.keySet()) {
                                if(var.equals(cname)) {
                                    System.out.println(cname + " " + temp);
                                    clientList.get(var).println(name + ": " + temp);
                                }
                            }
                        }
                    }
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            finally {
                if(name != null) {
                    clientList.remove(name, output);
                }
                try {
                    client.close();
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}