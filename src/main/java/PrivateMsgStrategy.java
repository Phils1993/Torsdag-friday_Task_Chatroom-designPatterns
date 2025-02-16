public class PrivateMsgStrategy implements IMessageStrategy{
    @Override
    public void execute(String message, ClientHandler clientHandler) {
        String [] messageParts = message.split(" ",2);
        if(messageParts.length < 2){
            clientHandler.notify("Invalid message format");
            return;
        }
        String recipientNickname = messageParts[0];
        String privateMessage = messageParts[1];

        ChatServerDemo server = clientHandler.getServer();
        server.sendPrivateMessage(recipientNickname, clientHandler.getNickName() + "(private): " + privateMessage );
    }
}

