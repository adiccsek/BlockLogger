package hu.shiya.blockLogger;

import java.util.HashMap;
import java.util.Map;

public class Placeholder {
    public static String placeholder(String message, HashMap<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            message = message.replace("%" + entry.getKey() + "%" , entry.getValue());
        }
    return message;
    }
}
//for(String placeholder: values.keySet()) {
//message = message.replace("%" + placeholder + "%", values.get(placeholder));
//        }
