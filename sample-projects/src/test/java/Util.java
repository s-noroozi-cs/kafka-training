import java.util.Random;

public class Util {

    public static int getRandomNumber() {
        return new Random().nextInt(1_000);
    }

    public static String getRandomTopicName() {
        return "test-" + getRandomNumber();
    }

    public static String getRandomConsumerGroupId() {
        return "test-app-consumer-" + getRandomNumber();
    }
}
