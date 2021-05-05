package co.warrenhome;

import co.warrenhome.connector.ISYConnector;
import co.warrenhome.settings.records.ISYConnectorSettingsRecord;
import co.warrenhome.settings.web.ISYConnectorSettingsPage;
import co.warrenhome.tags.ISYTagProvider;
import co.warrenhome.util.SettingsManager;
import com.google.common.collect.Lists;
import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.web.models.ConfigCategory;
import com.inductiveautomation.ignition.gateway.web.models.IConfigTab;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GatewayHook extends AbstractGatewayModuleHook {

    LoggerEx logger = LoggerEx.newBuilder().build(GatewayHook.class);

    private GatewayContext gatewayContext;

    @Override
    public void setup(GatewayContext gatewayContext) {
        this.gatewayContext = gatewayContext;
        BundleUtil.get().addBundle("ISYConnector", getClass(), "ISYConnector");
        SettingsManager.initialize(gatewayContext);
        tagProvider = new ISYTagProvider(gatewayContext);
    }

    ISYTagProvider tagProvider;

    @Override
    public void startup(LicenseState licenseState) {
        tagProvider.startup();
        ISYConnector isyConnector = new ISYConnector(gatewayContext, tagProvider);
        gatewayContext.getExecutionManager().register("ISY Connector", "Connection", isyConnector, 1, TimeUnit.MINUTES);

    }

    @Override
    public void shutdown() {
        tagProvider.shutdown();
    }

    @Override
    public boolean isMakerEditionCompatible() {
        return true;
    }

    /**
     * .properties file key for Configuration Menu Category
     */
    public static ConfigCategory CONFIG_CATEGORY = new ConfigCategory("ISYConnector", "ISYConnector.nav.header");

    /**
     * Gateway Configuration Page Config Categories
     */
    @Override
    public List<ConfigCategory> getConfigCategories() {
        return Collections.singletonList(CONFIG_CATEGORY);
    }

    /**
     * Set Gateway Configuration Pages
     */
    @Override
    public List<? extends IConfigTab> getConfigPanels() {
        return Lists.newArrayList(ISYConnectorSettingsPage.MENU_ENTRY);
    }


}
