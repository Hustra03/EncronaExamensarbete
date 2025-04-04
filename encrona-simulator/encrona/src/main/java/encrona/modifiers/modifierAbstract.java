package encrona.modifiers;

import java.util.List;

import encrona.components.componentAbstract;

/**
 * This abstract class describes the common properties of a modifier
 * Modifiers are the parts of the program which modify another value, and they represent a "real" change made to that value,
 * using the modify function.
 * This is used to provide methods with the exact same structure, ex getters and setters
 */
public abstract class modifierAbstract<T> implements modifierInterface<T>{
  private String name; //This is the name of this measure
  private String description; //This is a description of this measure, to specify what it represents
  private List<? extends componentAbstract<T>> componentsToImpact;//This is a list of components this specific measure should impact

  /**
   * This is a getter for the name attribute
   * @return the instances name
   */
  public String getName(){return this.name;} 

  /**
   * A setter for the name attribute
   * @param newName The new name
   */
  public void setName(String newName){this.name=newName;}


  /**
   * This is a getter for the description attribute
   * @return the instances description
   */
  public String getDescription(){return this.description;}

  /**
   * A setter for the description attribute
   * @param newDescription The new description
   */
  public void setDescription(String newDescription){this.description=newDescription;}


  /**
   * This is a getter for the componentsToImpact attribute
   * @return the instances componentsToImpact
   */
  public List<? extends componentAbstract<T>> getComponentsToImpact() {return this.componentsToImpact;}

}
