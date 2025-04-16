package encrona.expertSystem;

/**
 * This class defines the structure of the lambda functions used to check for if a specific condition is fulfilled, 
 * in order to trigger a rule
 */
public interface Condition {
    public Boolean testCondition(ExpertSystemModel modelState);
}
