package encrona.expertSystem;

/**
 * This class defines the structure of the lambda functions used to make a
 * change to the system model when a rule is triggered
 */
public interface PostCondition {
    /**
     * This method modifies the provided modelState in some manner, by directly
     * modifying its attributes
     * <p>
     * Note that it should therefore not attempt to modify the modelState directly.
     * For example, to remove an item to a list attribute it should call .remove on the list, not create a new list and set the value of the model to a new value. 
     * <p>
     * 
     * @param modelState The current model state
     */
    public void makeStateChange(ExpertSystemModel modelState);
}