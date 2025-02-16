import java.awt.*;

public class ColorDecorator implements TextDecorator{
    private final String colorCode;

    public ColorDecorator(String colorCode) {
        this.colorCode = colorCode;
    }

    @Override
    public String decorate(String message) {
        return colorCode + message + "\u001B[0m";
    }
}
