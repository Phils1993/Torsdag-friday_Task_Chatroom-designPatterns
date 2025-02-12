
public interface IObserver {
    void notify(String msg);
    void shutDownClient();
    void help();
    void addBadWordsToArray(String badWords);
}
