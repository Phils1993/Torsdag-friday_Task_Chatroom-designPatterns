public class LeaveStrategy implements IMessageStrategy{
    @Override
    public void execute(String nickName, ClientHandler clientHandler) {
        //clientHandler.broadCast(nickName);
        clientHandler.getServer().broadCast(clientHandler.getNickName() + " left the chat");
        clientHandler.shutDownClient();
        }
    }
