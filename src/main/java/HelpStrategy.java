public class HelpStrategy implements IMessageStrategy{
    @Override
    public void execute(String message, ClientHandler clientHandler) {
        clientHandler.help();
    }
}
