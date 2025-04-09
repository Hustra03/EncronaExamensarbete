package encrona.domain;

public class heatingEnergySource extends objectAbstract {

    private Double kwhPerYearHeating;
    private Double kwhPerYearHeatingWater;
    

    public heatingEnergySource(String name, Double kwhPerYearHeating,Double kwhPerYearHeatingWater)
    {
        this.setName(name);
        this.kwhPerYearHeating=kwhPerYearHeating;
        this.kwhPerYearHeatingWater=kwhPerYearHeatingWater;
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

}
