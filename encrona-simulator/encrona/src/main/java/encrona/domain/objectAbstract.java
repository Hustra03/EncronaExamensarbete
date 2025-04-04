package encrona.domain;

/**
 * This abstract class gives the common attributes and methods for objects
 */
public abstract class objectAbstract {
    private String name; //The specific objects name
    public String getName(){return this.name;} //A getter for the objects name
    public void setName(String name){this.name=name;} //A setter for the objects name
}
