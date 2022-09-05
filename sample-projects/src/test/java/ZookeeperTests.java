import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ZookeeperTests {

    private ZooKeeper zk;

    @BeforeEach
    void connect_zookeeper() {
        try {
            zk = new ZooKeeper("localhost", 30000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {

                }
            });
        } catch (Throwable ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @AfterEach
    void close_zookeeper() {
        try {
            zk.close();
        } catch (Throwable ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
}
