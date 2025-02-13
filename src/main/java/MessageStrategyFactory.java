import java.util.HashMap;
import java.util.Map;

public class MessageStrategyFactory {
    private static Map<String, IMessageStrategy> strategies = new HashMap<>();
    static {
        strategies.put("#JOIN",new JoinStrategy());
        strategies.put("#MESSAGE",new MessageStrategy());
        strategies.put("#LEAVE",new LeaveStrategy());
    }
    public static  IMessageStrategy getStrategy(String strategy) {
        return strategies.get(strategy);
    }
}
