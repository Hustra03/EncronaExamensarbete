package encrona.modifiers;

/**
 * This interface represents the common functions each modifier must implement, 
 * specifically the modify function since the data type varies depending on what it should impact.
 */
public interface modifierInterface<T>{
  /**
   * This function is used to perform the modification this represents
   * @param value The current value
   * @return The modified value
 * @throws Exception 
   */
  public T modify(T value) throws Exception;
}