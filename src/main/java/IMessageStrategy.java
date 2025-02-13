public interface IMessageStrategy {
    void execute(String message, ClientHandler clientHandler);
}
