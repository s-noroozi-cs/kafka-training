import java.util.Random;

public class Util {

    public static int getRandomNumber(int bound) {
        return new Random().nextInt(10 * bound);
    }

    public static String getRandomTopicName() {
        return "test-" + getRandomNumber(4);
    }

    public static String getRandomProducerTrxCfg(){
        return "prod-" + getRandomNumber(1);
    }

    public static String getRandomConsumerGroupId() {
        return "test-app-consumer-" + getRandomNumber(4);
    }
}
