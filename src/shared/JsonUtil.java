package shared;

import com.google.gson.Gson;

public class JsonUtil {

    // Gson objekt bruges til JSON konvertering
    private static final Gson gson = new Gson();

     // Konverterer Message objekt til JSON string

    public static String toJson(Message message)
    {
        return gson.toJson(message);
    }
    // Konverterer JSON string til Message objekt

    public static Message fromJson(String json)
    {
        return gson.fromJson(json, Message.class);
    }
}