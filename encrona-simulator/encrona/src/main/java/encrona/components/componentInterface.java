package encrona.components;

/**
 * This interface represents the common functions each component must implement, 
 * specifically the calculate function since the data type varies depending on what it should impact.
 */
public interface componentInterface<T> {
    /**
     * This function is used to calculate this component
     * @throws Exception 
     */
    public void calculate() throws Exception;
}
