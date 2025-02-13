import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable, IObserver {
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
