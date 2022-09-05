import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.jupiter.api.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class ZookeeperTests {

    private ZooKeeper zk;
    
    @BeforeEach
    void connect_zookeeper() {
        try {
            CountDownLatch connectedSignal = new CountDownLatch(1);
            zk = new ZooKeeper("localhost", 30000, new Watcher() {
                @Override
                public void process(WatchedEvent we) {
                    if (we.getState() == Event.KeeperState.SyncConnected) {
                        connectedSignal.countDown();
                    }
                }
            });
            connectedSignal.await();
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

    @Test
    void check_zookeeper_connection() {
        Assertions.assertNotNull(zk);
        Assertions.assertEquals(true, zk.getState().isConnected());
        Assertions.assertEquals(true, zk.getState().isAlive());
    }
}
