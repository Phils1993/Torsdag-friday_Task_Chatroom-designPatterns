public class StopServerStrategy implements IMessageStrategy{
    @Override
    public void execute(String message, ClientHandler clientHandler) {
        clientHandler.shutDownClient();
        ChatServerDemo.server.shutDownServer();
    }
}
