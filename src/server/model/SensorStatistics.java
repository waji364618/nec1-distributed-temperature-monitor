package server.model;

public class SensorStatistics {


    private int measurementCount;
    private double totalTemperature;
    private double highestTemperature;


     // Tilføjer ny temperatur til statistik

    public synchronized void addTemperature(double temperature)
    {
        measurementCount++;

        totalTemperature += temperature;

        // Opdater højeste temperatur
        if (temperature > highestTemperature)
        {
            highestTemperature = temperature;
        }
    }

     // Returnerer gennemsnitlig temperatur

    public synchronized double getAverageTemperature()
    {
        if (measurementCount == 0)
        {
            return 0;
        }

        return totalTemperature / measurementCount;
    }


    // Returnerer højeste temperatur

    public synchronized double getHighestTemperature()
    {
        return highestTemperature;
    }


     // Returnerer antal målinger
    public synchronized int getMeasurementCount()
    {
        return measurementCount;
    }

}
