package encrona.domain;

/**
 * This abstract class gives the common structure for improvements which are represented as objects 
 */
public class improvement extends objectAbstract {
    private Double kwhPerM2;
    private Double costPerM2;
    private Integer yearsOfService;
    private improvementImpactEnum impactType;


    public improvement(String name, Double kwhPerM2, Double costPerM2,Integer yearsOfService, improvementImpactEnum impactType)
    {
        this.setName(name);
        this.kwhPerM2=kwhPerM2;
        this.costPerM2=costPerM2;
        this.yearsOfService=yearsOfService;
        this.impactType=impactType;
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
