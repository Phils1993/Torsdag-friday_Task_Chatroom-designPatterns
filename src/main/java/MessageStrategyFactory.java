import java.util.HashMap;
import java.util.Map;

public class MessageStrategyFactory {
    private static Map<String, IMessageStrategy> strategies = new HashMap<>();
    static {
        strategies.put("#JOIN",new JoinStrategy());
        //strategies.put("#MESSAGE",new );
    }
    public static IMessageStrategy getStrategy(String strategy) {
        return strategies.get(strategy);
    }
}
