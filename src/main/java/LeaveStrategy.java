public class LeaveStrategy implements IMessageStrategy{
    @Override
    public void execute(String nickName, ClientHandler clientHandler) {
        clientHandler.broadCast(nickName);
        clientHandler.getServer().broadCast("i'm leaving the chat: " + nickName + " good bye");
        clientHandler.shutDownClient();
        }
    }
