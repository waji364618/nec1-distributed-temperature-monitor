package client.model;

public class SensorStatistics {

    private int measurementCount;
    private double totalTemperature;
    private double highestTemperature;

    // Tilføjer ny temperatur
    public void addTemperature(double temperature)
    {
        measurementCount++;

        totalTemperature += temperature;

        // Opdater højeste temperatur
        if (temperature > highestTemperature)
        {
            highestTemperature = temperature;
        }
    }

    public double getAverageTemperature()
    {
        if (measurementCount == 0)
        {
            return 0;
        }

        return totalTemperature / measurementCount;
    }

    public double getHighestTemperature()
    {
        return highestTemperature;
    }

    public int getMeasurementCount()
    {
        return measurementCount;
    }

}
