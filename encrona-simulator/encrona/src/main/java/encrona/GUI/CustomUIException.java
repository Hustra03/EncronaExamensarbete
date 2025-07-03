package encrona.GUI;

/**
 * This is a custom exception, to be thrown by anything issue which should show an error box, but which does not result in a complete program halt
 */
public class CustomUIException extends Exception{

    public CustomUIException(String string) {
        super(string);
    }
    
}
