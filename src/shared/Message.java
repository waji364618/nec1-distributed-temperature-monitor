package shared;

public class Message {

    private MessageType type;
    private String clientId;
    private double temperature;
    private int interval;

    // Constructor til temperaturbeskeder
    public Message(MessageType type, String clientId, double temperature)
    {
        this.type = type;
        this.clientId = clientId;
        this.temperature = temperature;
    }

    // Constructor til intervalbeskeder
    public Message(MessageType type, int interval)
    {
        this.type = type;
        this.interval = interval;
    }
    // Broadcast constructor
    public Message(
            MessageType type,
            String clientId)
    {
        this.type = type;
        this.clientId = clientId;
    }

    public MessageType getType()
    {
        return type;
    }

    public String getClientId()
    {
        return clientId;
    }

    public double getTemperature()
    {
        return temperature;
    }
    public int getInterval()
    {
        return interval;
    }
}
