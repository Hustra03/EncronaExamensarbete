package encrona.domain;

/**
 * This abstract class gives the common structure for improvements which are represented as objects 
 */
public class improvement extends objectAbstract {
    private Double kwhPerM2BuildingHeating;
    private Double kwhPerM2WaterHeating; 
    private Double kwhPerM2Electricity; 
    private Double m3PerM2Water;
    private Double costPerM2;
    private Integer yearsOfService;

    /**
     * This is the constructor for the improvemnt class
     * @param name The name of this improvement
     * @param kwhPerM2BuildingHeating The effect this improvement has in energy consumption per m2 in heating the building over the course of the improvement lifetime
     * @param kwhPerM2WaterHeating The effect this improvement has in energy consumption per m2 in heating water over the course of the improvement lifetime
     * @param kwhPerM2Electricity The effect this improvement has in energy consumption per m2 in electricity over the course of the improvement lifetime
     * @param m3PerM2Water The effect this improvement has in water consumption per m2 over the course of the improvement lifetime
     * @param costPerM2 The cost of this improvement per m2 over the course of the improvement lifetime
     * @param yearsOfService How long this improvement is effective
     */
    public improvement(String name, Double kwhPerM2BuildingHeating,Double kwhPerM2WaterHeating,Double kwhPerM2Electricity,Double m3PerM2Water, Double costPerM2,Integer yearsOfService)
    {
        this.setName(name);
        this.kwhPerM2BuildingHeating=kwhPerM2BuildingHeating;
        this.kwhPerM2WaterHeating=kwhPerM2WaterHeating;
        this.kwhPerM2Electricity=kwhPerM2Electricity;
        this.m3PerM2Water=m3PerM2Water;
        this.costPerM2=costPerM2;
        this.yearsOfService=yearsOfService;
    }

    /**
     * Creates a string for this improvement
     */
    public String toString()
    {
        String improvementString=this.getName()+ " saves ";
        if (kwhPerM2BuildingHeating!=0.0) {
            improvementString+=kwhPerM2BuildingHeating + " kwh per m2 in building heating ";
        }
        if (kwhPerM2WaterHeating!=0.0) {
            improvementString+=kwhPerM2WaterHeating + " kwh per m2 in water heating ";
        }
        if (kwhPerM2Electricity!=0.0) {
            improvementString+=kwhPerM2Electricity + " kwh per m2 in electricity ";
        }
        if (m3PerM2Water!=0.0) {
            improvementString+=m3PerM2Water + " m^3 per m2 in water ";
        }
        return improvementString + " for the cost "+ costPerM2 + " per m2 over the course of " + yearsOfService;
        
    }

    /**
     * This is a getter for kwhPerM2BuildingHeating
     * @return The value of kwhPerM2BuildingHeating
     */
    public Double getKwhPerM2BuildingHeating(){return this.kwhPerM2BuildingHeating;}
    /**
     * This is a setter for kwhPerM2BuildingHeating
     * @param newKwhPerM2 The new value for kwhPerM2BuildingHeating 
     */
    public void setKwhPerM2BuildingHeating(Double newKwhPerM2){this.kwhPerM2BuildingHeating=newKwhPerM2;}

    /**
     * This is a getter for kwhPerM2WaterHeating
     * @return The value of kwhPerM2WaterHeating
     */
    public Double getKwhPerM2WaterHeating(){return this.kwhPerM2WaterHeating;}
    /**
     * This is a setter for kwhPerM2WaterHeating
     * @param newKwhPerM2 The new value for kwhPerM2WaterHeating 
     */
    public void setKwhPerM2WaterHeating(Double newKwhPerM2){this.kwhPerM2WaterHeating=newKwhPerM2;}

        /**
     * This is a getter for kwhPerM2Electricity
     * @return The value of kwhPerM2Electricity
     */
    public Double getKwhPerM2Electricity(){return this.kwhPerM2Electricity;}
    /**
     * This is a setter for kwhPerM2Electricity
     * @param newKwhPerM2 The new value for kwhPerM2Electricity 
     */
    public void setKwhPerM2Electricity(Double newKwhPerM2){this.kwhPerM2Electricity=newKwhPerM2;}

        /**
     * This is a getter for m3PerM2Water
     * @return The value of m3PerM2Water
     */
    public Double getM3PerM2Water(){return this.m3PerM2Water;}
    /**
     * This is a setter for m3PerM2Water
     * @param newKwhPerM2 The new value for m3PerM2Water 
     */
    public void setM3PerM2Water(Double newM3PerM2Water){this.m3PerM2Water=newM3PerM2Water;}

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
}
