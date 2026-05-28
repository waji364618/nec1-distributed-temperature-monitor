package client.model;

public class SensorStatistics {

    private int measurementCount;
    private double totalTemperature;
    private double highestTemperature;


    public synchronized void addTemperature(double temperature)
    {
        measurementCount++;

        totalTemperature += temperature;

              if (temperature > highestTemperature)
        {
            highestTemperature = temperature;
        }
    }

    public synchronized double getAverageTemperature()
    {
        if (measurementCount == 0)
        {
            return 0;
        }

        return totalTemperature / measurementCount;
    }

    public synchronized  double getHighestTemperature()
    {
        return highestTemperature;
    }

    public synchronized  int getMeasurementCount()
    {
        return measurementCount;
    }

}
