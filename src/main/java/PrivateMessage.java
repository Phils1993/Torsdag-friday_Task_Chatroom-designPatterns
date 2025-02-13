public class PrivateMessage implements IMessageStrategy{
    @Override
    public void execute(String message, ClientHandler clientHandler) {
        String[] parts = message.trim().split(" ", 2);
        if(parts.length < 2){
            clientHandler.notify("Invalid private message format. Use: #PRIVATE <nickname> <message>");
            return;
        }
    }
}
