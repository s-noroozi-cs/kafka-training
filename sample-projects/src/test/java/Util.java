import java.util.Random;

public class Util {

    public static int getRandomNumber() {
        return new Random().nextInt(10_000);
    }

    public static String getRandomTopicName() {
        return "test-" + getRandomNumber();
    }

    public static String getRandomProducerTrxCfg(){
        return "prod-" + getRandomNumber();
    }

    public static String getRandomConsumerGroupId() {
        return "test-app-consumer-" + getRandomNumber();
    }
}
