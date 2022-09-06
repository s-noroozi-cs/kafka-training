import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.temporal.ChronoField;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

    @Test
    void check_create_node() {
        try {
            String path = "/" + UUID.randomUUID();
            String data = String.valueOf(System.currentTimeMillis());
            zk.create(path, data.getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            Stat stat = zk.exists(path, true);
            Assertions.assertEquals(0, stat.getVersion());
            Assertions.assertEquals(0, stat.getNumChildren());
        } catch (Throwable ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Test
    void check_set_data() {
        try {
            String path = "/" + UUID.randomUUID();
            String data = String.valueOf(System.currentTimeMillis());
            zk.create(path, data.getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            zk.setData(path, "new-data".getBytes(StandardCharsets.UTF_8), 0);

            Assertions.assertThrowsExactly(KeeperException.BadVersionException.class, () -> {
                zk.setData(path, "new-data".getBytes(StandardCharsets.UTF_8), 0);
            });

            zk.setData(path, "new-data".getBytes(StandardCharsets.UTF_8), 1);

        } catch (Throwable ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Test
    void check_watcher() {
        try {
            String path = "/" + UUID.randomUUID();
            CountDownLatch nodeWatcher = new CountDownLatch(2);
            zk.addWatch(path, new Watcher() {
                @Override
                public void process(WatchedEvent we) {
                    if(Event.EventType.NodeCreated.equals(we.getType())
                            || Event.EventType.NodeDataChanged.equals(we.getType())){
                        if(path.equals(we.getPath())){
                            nodeWatcher.countDown();
                        }
                    }

                }
            },AddWatchMode.PERSISTENT);

            zk.create(path, "data".getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            zk.setData(path, "new-data".getBytes(StandardCharsets.UTF_8), 0);

            nodeWatcher.await(3000, TimeUnit.MILLISECONDS);

            Assertions.assertEquals(0L,nodeWatcher.getCount());

        } catch (Throwable ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
}
