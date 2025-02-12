import java.io.*;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServerDemo implements IOberserverable {

    private final List<ClientHandler> clients = new ArrayList<>();
    private ServerSocket serverSocket;

    private volatile boolean running = true;

    private ChatServerDemo() {
    }

    public synchronized static IOberserverable getInstance() {
        if (server == null) {
            server = new ChatServerDemo();
        }
        return server;
    }

    public static void main(String[] args) {
        new ChatServerDemo().startServer(12345);
    }

    private static volatile IOberserverable server = getInstance();

    public void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("welcome " + clientSocket.getRemoteSocketAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);

                clients.add(clientHandler);

                Thread thread = new Thread(clientHandler);
                thread.start();
                //startRandomPrivateMessage(clientHandler);
            }
        } catch (IOException e) {
            if (running) {
                e.printStackTrace();
            }
        }finally {
            shutDownServer();
        }
    }

    /*
    private void startRandomPrivateMessage(ClientHandler clientHandler) {
        Random random = new Random();
        new Thread(() -> {
            while (true) {
                int randomDelay = random.nextInt(50000);
                try {
                    Thread.sleep(randomDelay);
                    String randomMessage = "Random private message at " + System.currentTimeMillis();
                    clientHandler.notify("Server: " + randomMessage);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
     */

    @Override
    public void broadCast(String msg) {
        for (ClientHandler clientHandler : clients) {
            clientHandler.notify(msg);
        }
    }

    @Override
    public void sendPrivateMessage(String nickName, String msg) {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.nickName.equals(nickName)) {
                clientHandler.notify(msg);
                break;
            }
        }
    }

    @Override
    public void printClientList() {
        StringBuilder clientList = new StringBuilder("Connected clients: ");
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.getNickName() != null) {
                clientList.append(clientHandler.getNickName()).append(", ");
            }
        }
        if (clientList.length() > 17) {
            clientList.setLength(clientList.length() - 2);
        } else {
            clientList.append("No clients connected.");
        }
        for (ClientHandler clientHandler : clients) {
            clientHandler.notify(clientList.toString());
        }
    }

    @Override
    public void shutDownServer() {
        try {
            running = false;
            //System.out.println("Shutting down chat server...");
            for (ClientHandler clientHandler : clients) {
                clientHandler.notify("Shutting down chat server...");
                clientHandler.shutDownClient();
            }
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static class ClientHandler implements Runnable, IObserver {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        private IOberserverable server;
        private String nickName = null;
        private List<String> bannedWords = new ArrayList<>();

        public ClientHandler(Socket clientSocket, IOberserverable server) throws IOException {
            this.clientSocket = clientSocket;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.server = server;

            bannedWords.add("bitch");
            bannedWords.add("fuck");
            bannedWords.add("orm");
            bannedWords.add("idiot");
        }

        public String getNickName() {
            return nickName;
        }


        @Override
        public void run() {
            String msg;
            try {
                while ((msg = in.readLine()) != null) {
                    msg=filterMessage(msg);
                    //System.out.println(msg);
                    if (msg.startsWith("#JOIN")) {
                        this.nickName = msg.split(" ")[1];
                        server.broadCast("new person joined the chat. Welcome to: " + nickName + clientSocket.getRemoteSocketAddress());
                    }
                    if (msg.startsWith("#PRIVATE")) {
                        String[] messagePart = msg.split(" ", 3);
                        if (messagePart.length < 3) {
                            out.println("invalid message, type a valid message");
                        }
                        String recipient = messagePart[1];
                        String privateMessage = messagePart[2];
                        server.sendPrivateMessage( recipient, "Private msg: " + privateMessage);
                    } else if (msg.startsWith("#MESSAGE")) {
                        if (nickName != null) {
                            server.broadCast(nickName + ": " + msg.substring(9));
                        } else{
                            server.broadCast("Broadcasting: " + msg.substring(9));
                        }
                    }
                    else if (msg.startsWith("#LEAVE")) {
                        out.println("leaving the chat " + nickName);
                        shutDownClient();
                    }
                    else if (msg.startsWith("#GETLIST")) {
                        out.println("Lists of all clients: ");
                        server.printClientList();
                    }
                    else if (msg.startsWith("#HELP")){
                        out.println(nickName + ": " );
                        help();
                    } else if (msg.startsWith("#STOPSERVER")) {
                        if ("admin".equals(nickName)) {
                            out.println("Admin has stopped the server - GOOD BYE");
                            server.shutDownServer();
                        } else {
                            out.println("You are not admin and have no access to stop the server");
                        }
                    }
                    else if (msg.startsWith("#BANWORD")) {
                        String [] messagePart = msg.split(" ", 2);
                        if (messagePart.length < 2) {
                            out.println("invalid message, type a valid message");
                        }else {
                            String bannedWord = messagePart[1].trim();
                            addBadWordsToArray(bannedWord);
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                }
            }

        @Override
        public void notify(String msg) {
            System.out.println(msg);
            out.println(msg);
        }

        @Override
        public void shutDownClient() {
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void help() {
            out.println("You are asking for HELP?? THIS is my help: ALT+F4");
        }

        @Override
        public void addBadWordsToArray(String badWords) {
            if (!bannedWords.contains(badWords.toLowerCase())) {
                bannedWords.add(badWords.toLowerCase());
                out.println("Banned word is added to the list " + badWords);
            }
            else {
                out.println("Banned word is already added to the list " + badWords);
            }
        }

        private String filterMessage(String message) {
            for (String bannedWord: bannedWords){
                String stars = "*".repeat(bannedWord.length());
                message = message.replaceAll("(?i)\\b" + bannedWord + "\\b", stars);
            }
            return message;
        }
    }


}
