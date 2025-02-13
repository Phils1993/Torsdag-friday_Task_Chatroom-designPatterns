import java.util.HashMap;
import java.util.Map;

public class MessageStrategyFactory {
    private static final Map<String, IMessageStrategy> strategies = new HashMap<>();
    static {
        strategies.put("#JOIN",new JoinStrategy());
        strategies.put("#MESSAGE",new MessageStrategy());
        strategies.put("#LEAVE",new LeaveStrategy());
        strategies.put("#PRIVATE", new LeaveStrategy());
    }
    public static  IMessageStrategy getStrategy(String strategy) {
        return strategies.getOrDeault(strategy, new IMessageStrategy() {
            @Override
            public void execute (String message, ClientHandler client) {
                client.notify("Sorry no command with that name: " + message);
            }
        });
    }
}
