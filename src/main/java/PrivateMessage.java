public class PrivateMessage implements IMessageStrategy{
    @Override
    public void execute(String message, ClientHandler clientHandler) {
        ChatServerDemo.getClients();
        clientHandler.setName(message);
        clientHandler.getServer().sendPrivateMessage(clientHandler.getNickName(), message);
    }
}

