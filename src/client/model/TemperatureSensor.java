package client.model;

public class TemperatureSensor {

    private String sensorId;
    private double temperature;
    private int interval;

    public TemperatureSensor(
            String sensorId,
            int interval)
    {
        this.sensorId = sensorId;
        this.interval = interval;
    }

    public String getSensorId()
    {
        return sensorId;
    }

    public double getTemperature()
    {
        return temperature;
    }

    public void setTemperature(
            double temperature)
    {
        this.temperature = temperature;
    }

    public int getInterval()
    {
        return interval;
    }

    public void setInterval(int interval)
    {
        this.interval = interval;
    }
}
