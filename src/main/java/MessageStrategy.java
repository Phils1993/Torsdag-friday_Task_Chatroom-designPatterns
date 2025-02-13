public class MessageStrategy implements IMessageStrategy {
    @Override
    public void execute(String message, ClientHandler clientHandler) {
        clientHandler.broadCast(message);
        clientHandler.getServer().broadCast(clientHandler.getNickName() + " " + message);
    }
}
