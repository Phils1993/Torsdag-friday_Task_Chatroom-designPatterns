public class JoinStrategy implements IMessageStrategy{
    @Override
    public void execute(String nickName, ClientHandler clientHandler) {
        clientHandler.setName(nickName);
        clientHandler.getServer().broadCast("Welcome to the chat: " + nickName + " has joined the server");
    }
}
