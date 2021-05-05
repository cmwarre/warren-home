package co.warrenhome.connector;

import co.warrenhome.entity.Node;
import co.warrenhome.entity.NodeProperty;
import co.warrenhome.entity.NodeResponse;
import co.warrenhome.settings.records.ISYConnectorSettingsRecord;
import co.warrenhome.tags.EntityTagMapper;
import co.warrenhome.tags.ISYTagProvider;
import co.warrenhome.util.SettingsManager;
import com.inductiveautomation.ignition.common.TypeUtilities;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class ISYConnector implements Runnable {

    private final GatewayContext gatewayContext;
    private final ISYTagProvider provider;
    private final SettingsManager settingsManager = SettingsManager.getInstance();
    LoggerEx logger = LoggerEx.newBuilder().build(ISYConnector.class);
    private List<Node> nodes = new ArrayList<>();

    public ISYConnector(GatewayContext gatewayContext, ISYTagProvider provider) {
        this.gatewayContext = gatewayContext;
        this.provider = provider;
    }

    @Override
    public void run() {
        getNodes();
    }

    private WebTarget getClient() {
        ISYConnectorSettingsRecord settings = settingsManager.querySettings();
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(settings.getUsername(), settings.getPassword());

        return ClientBuilder.newClient()
                .target(settings.getAddress())
                .register(feature);
    }

    public void getNodes() {
        WebTarget client = getClient().path("rest/nodes");
        Invocation.Builder invocationBuilder = client.request(MediaType.APPLICATION_XML);
        Response response = invocationBuilder.get();
        logger.debugf("Request Complete with status %d", response.getStatus());

        if (response.getStatus() == 200) {
            NodeResponse nodeResponse = response.readEntity(NodeResponse.class);
            if (nodeResponse != null) {
                this.nodes = nodeResponse.getNodes();
                for (Node node : nodes) {
                    node.getProperty().addPropertyChangeListener("value", evt -> {
                        Double value = TypeUtilities.toDouble(evt.getNewValue());
                        if (value > 0.0)
                            commandOn(node, value);
                        else
                            commandOff(node);
                    });
                    updateTags();
                }
            }
        }
    }

    public void commandOn(Node node, double value) {
        WebTarget client = getClient().path(String.format("rest/nodes/%s/cmd/DON/%d", node.getAddress(), (int) value));
        Invocation.Builder invocationBuilder = client.request(MediaType.APPLICATION_XML);
        Response response = invocationBuilder.get();
        logger.debugf("Request Complete with status %d", response.getStatus());
    }

    public void commandOff(Node node) {
        WebTarget client = getClient().path(String.format("rest/nodes/%s/cmd/DOF/", node.getAddress()));
        Invocation.Builder invocationBuilder = client.request(MediaType.APPLICATION_XML);
        Response response = invocationBuilder.get();
        logger.debugf("Request Complete with status %d", response.getStatus());
    }

    public void updateTags() {
        for (Node node : nodes) {
            logger.debugf(node.toString());
            String rootPath = String.format("Nodes/%s", node.getName());

            EntityTagMapper<Node> nodeTagMapper = new EntityTagMapper<>(provider, rootPath, node);
            nodeTagMapper.configureTags();
            nodeTagMapper.updateTags();

            EntityTagMapper<NodeProperty> nodePropertyTagMapper = new EntityTagMapper<>(provider, rootPath, node.getProperty());
            nodePropertyTagMapper.configureTags();
            nodePropertyTagMapper.updateTags();
        }
    }
}
