package encrona.domain;

public class heatingEnergySource extends objectAbstract {

    private Double kwhPerYearHeating;
    private Double kwhPerYearHeatingWater;
    private Double kwhPerYearInElectricity;
    private Double costPerKwh;

    /**
     * This is a constructor for heatingEnergySource
     * @param name The name of the heatingEnergySource
     * @param kwhPerYearHeating The kwh needed for heating of the building, excluding heating water
     * @param kwhPerYearHeatingWater The kwh needed for heating water specifically
     * @param staticCost Any costs which do not depend on kwh consumption
     * @param costPerKwh Kr per kwh
     */
    public heatingEnergySource(String name, Double kwhPerYearHeating,Double kwhPerYearHeatingWater, Double kwhPerYearInElectricity,Double costPerKwh)
    {
        this.setName(name);
        this.kwhPerYearHeating=kwhPerYearHeating;
        this.kwhPerYearHeatingWater=kwhPerYearHeatingWater;
        this.kwhPerYearInElectricity=kwhPerYearInElectricity;
        this.costPerKwh=costPerKwh;
    }

    /**
     * This constructor creates an instance with the same values as the provided heatingEnergySource
     * @param toCopy The heatingEnergySource to copy the values of
     */
    public heatingEnergySource(heatingEnergySource toCopy)
    {
        this.setName(toCopy.getName());
        this.kwhPerYearHeating=toCopy.getKwhPerYearHeating();
        this.kwhPerYearHeatingWater=toCopy.getKwhPerYearHeatingWater();
        this.kwhPerYearInElectricity=toCopy.getKwhPerYearInElectricity();
        this.costPerKwh=toCopy.getCostPerKwh();
    }

    public String toString()
    {
        return getName() + " provides " + kwhPerYearHeating +" for building heating and " + kwhPerYearHeatingWater +" for water for the cost " + costPerKwh +" per kwh and requiring " + kwhPerYearInElectricity+" kwh per year of electricity";
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
     * This is a getter for kwhPerYearInElectricity
     * @return The value of kwhPerYearInElectricity
     */
    public Double getKwhPerYearInElectricity(){return this.kwhPerYearInElectricity;}
    /**
     * This is a setter for kwhPerYearInElectricity
     * @param newKwhPerYearInElectricity The new value for kwhPerYearInElectricity
     */
    public void setKwhPerYearInElectricity(Double newKwhPerYearInElectricity){this.kwhPerYearInElectricity=newKwhPerYearInElectricity;}

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
