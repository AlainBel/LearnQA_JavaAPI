package lib;

import java.text.SimpleDateFormat;

public class DataGenerator {
    public static String getRandomEmail() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHss").format(new java.util.Date());
        return "learnqa" + timestamp + "@example.com";
    }
}
