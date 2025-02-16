public class JoinStrategy implements IMessageStrategy{
    @Override
    public void execute(String nickName, ClientHandler clientHandler) {
       /*
        String [] messageParts = message.split(" ",2);
        if(messageParts.length  < 2){
            clientHandler.notify("Invalid join command. Use format: #JOIN <nickname>");
            return;
        }
        clientHandler.setName(messageParts[1]);

        clientHandler.addDecorator(new ColorDecorator("\u001B[0m"));


        */

        clientHandler.setName(nickName);
        clientHandler.getServer().broadCast("Welcome to the chat: " + nickName + " has joined the server");
    }
}
