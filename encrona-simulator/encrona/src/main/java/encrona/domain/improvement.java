package encrona.domain;

/**
 * This abstract class gives the common structure for improvements which are represented as objects 
 */
public class improvement extends objectAbstract {
    private Double kwhPerM2; //This is the sum of the impact over the years of service
    private Double costPerM2;
    private Integer yearsOfService;
    private improvementImpactEnum impactType;

    /**
     * This is the constructor for the improvemnt class
     * @param name The name of this improvement
     * @param kwhPerM2 The effect this improvement has in energy consumption per m2
     * @param costPerM2 The cost of this improvement per m2
     * @param yearsOfService How long this improvement is effective
     * @param impactType What this improvement effects
     */
    public improvement(String name, Double kwhPerM2, Double costPerM2,Integer yearsOfService, improvementImpactEnum impactType)
    {
        this.setName(name);
        this.kwhPerM2=kwhPerM2;
        this.costPerM2=costPerM2;
        this.yearsOfService=yearsOfService;
        this.impactType=impactType;
    }

    /**
     * Creates a string for this improvement
     */
    public String toString()
    {
        return this.getName()+ " saves " + kwhPerM2 + "kwh per m2 for the cost "+ costPerM2 + " per m2 over the course of " + yearsOfService +" years for " + impactType.toString();
        
    }

    /**
     * This is a getter for kwhPerM2
     * @return The value of kwhPerM2
     */
    public Double getKwhPerM2(){return this.kwhPerM2;}
    /**
     * This is a setter for kwhPerM2
     * @param newKwhPerM2 The new value for kwhPerM2 
     */
    public void setKwhPerM2(Double newKwhPerM2){this.kwhPerM2=newKwhPerM2;}

    /**
     * This is a getter for costPerM2
     * @return The value of costPerM2
     */
    public Double getCostPerM2(){return this.costPerM2;}
    /**
     * This is a setter for costPerM2
     * @param newCostPerM2 The new value for costPerM2 
     */
    public void setCostPerM2(Double newCostPerM2){this.costPerM2=newCostPerM2;}

    /**
     * This is a getter for yearsOfService
     * @return The value of yearsOfService
     */
    public Integer getYearsOfService(){return this.yearsOfService;}
    /**
     * This is a setter for yearsOfService
     * @param newYearsOfService The new value for yearsOfService 
     */
    public void setYearsOfService(Integer newYearsOfService){this.yearsOfService=newYearsOfService;}

    /**
     * This is a getter for impactType
     * @return The value of impactType
     */
    public improvementImpactEnum getImpactType(){return this.impactType;}
    /**
     * This is a setter for impactType
     * @param newImpactType The new value for impactType
     */
    public void setImpactType(improvementImpactEnum newImpactType){this.impactType=newImpactType;}


}
