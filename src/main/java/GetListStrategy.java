public class GetListStrategy implements IMessageStrategy{
    @Override
    public void execute(String message, ClientHandler clientHandler) {
        ChatServerDemo.server.printClientList();
    }
}
