import java.util.HashMap;
import java.util.Map;

public class MessageStrategyFactory {
    private static Map<String, IMessageStrategy> strategies = new HashMap<>();
    static {
        strategies.put("#JOIN",);
        strategies.put("#MESSAGE",);
    }
    public static IMessageStrategy getStrategy(String strategy) {
        return strategies.get(strategy);
    }
}
