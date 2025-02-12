

public interface IOberserverable {
    //void addObserver(IObserver observer);
    //void removeObserver(IObserver observer);
    void broadCast(String msg);
    void sendPrivateMessage(String nickName, String msg);
    void printClientList();
    void shutDownServer();
}
