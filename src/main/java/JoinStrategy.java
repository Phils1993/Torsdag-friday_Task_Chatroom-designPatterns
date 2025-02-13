public class JoinStrategy implements IMessageStrategy{
    @Override
    public void execute(String message, ClientHandler clientHandler) {
        clientHandler.setName(message);
        clientHandler.getServer().broadCast("Welcome to the chat: " + message + "has joined the server");
    }
}
