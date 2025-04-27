package encrona.expertSystem;

/**
 * This class represents a rule in the expert system, with it accepting a model state and potentially modifying it if its condition is met or not met and it has a negation post condition
 */
public class Rule {
    
    private String name;
    private String description;
    private Condition condition;
    private PostCondition postConditionIfCondition;
    private PostCondition postConditionIfNotCondition; 

    /**
     * This is the constructor
     * @param name The name of the rule, so that it can be identified
     * @param description A short description of what this rule is meant to represent, it is used in the GUI for the rule
     * @param condition This is checked, to confirm if the rule should be triggered or not
     * @param postConditionIfCondition This is triggered if condition is true, may not be null
     * @param postConditionIfNotCondition This is triggered if condition is false, may be null if no else statement exists
     */
    public Rule(String name, String description,Condition condition,PostCondition postConditionIfCondition,PostCondition postConditionIfNotCondition)
    {
        this.name=name;
        this.description=description;
        this.condition=condition;
        this.postConditionIfCondition=postConditionIfCondition;
        this.postConditionIfNotCondition=postConditionIfNotCondition;
    }

    public Boolean testRule(ExpertSystemModel currentState)
    {

        Boolean conditionStatus=condition.testCondition(currentState);

        if (conditionStatus) {
            postConditionIfCondition.makeStateChange(currentState);
            return true;
        }
        else
        {
            if (!conditionStatus&&postConditionIfNotCondition!=null) {
                postConditionIfNotCondition.makeStateChange(currentState);
                return true;

            }
        }

        return false;
    }

    public String toString()
    {
        return name+" : " + description;
    }
}
