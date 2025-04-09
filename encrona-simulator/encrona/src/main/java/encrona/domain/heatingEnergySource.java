package encrona.domain;

public class heatingEnergySource extends objectAbstract {

    private Double kwhPerYearHeating;
    private Double kwhPerYearHeatingWater;
    private Double staticCosts;
    private Double costPerKwh;

    public heatingEnergySource(String name, Double kwhPerYearHeating,Double kwhPerYearHeatingWater, Double staticCost,Double costPerKwh)
    {
        this.setName(name);
        this.kwhPerYearHeating=kwhPerYearHeating;
        this.kwhPerYearHeatingWater=kwhPerYearHeatingWater;
        this.staticCosts=staticCost;
        this.costPerKwh=costPerKwh;
    }

    /**
     * This is a getter for kwhPerYearHeating
     * @return The value of kwhPerYearHeating
     */
    public Double getKwhPerYearHeating(){return this.kwhPerYearHeating;}
    /**
     * This is a setter for kwhPerYearHeating
     * @param newKwhPerYearHeating The new value for kwhPerYearHeating
     */
    public void setKwhPerYearHeating(Double newKwhPerYearHeating){this.kwhPerYearHeating=newKwhPerYearHeating;}

    /**
     * This is a getter for kwhPerYearHeatingWater
     * @return The value of kwhPerYearHeatingWater
     */
    public Double getKwhPerYearHeatingWater(){return this.kwhPerYearHeatingWater;}
    /**
     * This is a setter for kwhPerYearHeatingWater
     * @param newKwhPerYearHeatingWater The new value for kwhPerYearHeatingWater
     */
    public void setKwhPerYearHeatingWater(Double newKwhPerYearHeatingWater){this.kwhPerYearHeatingWater=newKwhPerYearHeatingWater;}

    /**
     * This is a getter for staticCosts
     * @return The value of staticCosts
     */
    public Double getStaticCosts(){return this.staticCosts;}
    /**
     * This is a setter for staticCosts
     * @param newStaticCosts The new value for staticCosts
     */
    public void setStaticCosts(Double newStaticCosts){this.staticCosts=newStaticCosts;}

    /**
     * This is a getter for costPerKwh
     * @return The value of costPerKwh
     */
    public Double getCostPerKwh(){return this.costPerKwh;}
    /**
     * This is a setter for costPerKwh
     * @param newCostPerKwh The new value for costPerKwh
     */
    public void setCostPerKwh(Double newCostPerKwh){this.costPerKwh=newCostPerKwh;}
}
