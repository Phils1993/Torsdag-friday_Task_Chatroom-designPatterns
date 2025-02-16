import java.io.*;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServerDemo implements IOberserverable {

    private static final List<ClientHandler> clients = new ArrayList<>();
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

    public static List<ClientHandler> getClients() {
        return clients;
    }

    public static void main(String[] args) {
        new ChatServerDemo().startServer(12345);
    }

    static volatile IOberserverable server = getInstance();

    public void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            ExecutorService executorService = Executors.newCachedThreadPool();
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("welcome " + clientSocket.getRemoteSocketAddress());
                Runnable runnable = new ClientHandler(clientSocket, this);

                executorService.submit(runnable);
                IObserver clientHandler = (IObserver) runnable;

                clients.add((ClientHandler) runnable);
            }
        } catch (IOException e) {
            if (running) {
                e.printStackTrace();
            }
        }finally {
            shutDownServer();
        }
    }

    @Override
    public void broadCast(String msg) {
        for (ClientHandler clientHandler : clients) {
            clientHandler.notify(msg);
        }
    }

    @Override
    public void sendPrivateMessage(String nickName, String msg) {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.getNickName().equals(nickName)) {
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

}
