package pl.allegro.tech.hermes.common.admin.zookeeper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.Reaper;
import org.apache.zookeeper.CreateMode;
import pl.allegro.tech.hermes.api.SubscriptionName;
import pl.allegro.tech.hermes.common.admin.AdminTool;
import pl.allegro.tech.hermes.common.admin.AdminToolStartupException;
import pl.allegro.tech.hermes.common.exception.RetransmissionException;

import static pl.allegro.tech.hermes.common.admin.AdminTool.Operations.RETRANSMIT;

public class ZookeeperAdminTool implements AdminTool {

    public static final String ROOT = "/hermes_admin";

    private final CuratorFramework curatorFramework;
    private final ObjectMapper objectMapper;
    private final Reaper reaper;

    public ZookeeperAdminTool(CuratorFramework curatorFramework, ObjectMapper objectMapper, int reapingInterval) {
        this.curatorFramework = curatorFramework;
        this.objectMapper = objectMapper;
        this.reaper = new Reaper(curatorFramework, reapingInterval);
    }

    public void start() throws AdminToolStartupException {
        try {
            this.reaper.start();
        } catch (Exception ex) {
            throw new AdminToolStartupException(ex);
        }
    }

    @Override
    public void retransmit(SubscriptionName subscriptionName) {
        try {
            String path = Joiner.on("/").join(ROOT, RETRANSMIT.name());

            String createdPath = curatorFramework.create()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(path, objectMapper.writeValueAsBytes(subscriptionName));

            reaper.addPath(createdPath);

        } catch (Exception e) {
            throw new RetransmissionException(e);
        }
    }
}
