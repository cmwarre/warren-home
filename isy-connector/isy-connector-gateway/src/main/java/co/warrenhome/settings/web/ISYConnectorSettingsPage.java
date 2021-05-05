package co.warrenhome.settings.web;

import co.warrenhome.GatewayHook;
import co.warrenhome.settings.records.ISYConnectorSettingsRecord;
import com.inductiveautomation.ignition.gateway.model.IgnitionWebApp;
import com.inductiveautomation.ignition.gateway.web.components.RecordEditForm;
import com.inductiveautomation.ignition.gateway.web.models.DefaultConfigTab;
import com.inductiveautomation.ignition.gateway.web.models.IConfigTab;
import com.inductiveautomation.ignition.gateway.web.models.LenientResourceModel;
import com.inductiveautomation.ignition.gateway.web.pages.IConfigPage;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.Application;

/**
 * Wicket Web Page for ISY Connector Configuration.  Shows up on gateway web configuration
 * */
public class ISYConnectorSettingsPage extends RecordEditForm {

    public static final IConfigTab MENU_ENTRY = DefaultConfigTab.builder()
            .category(GatewayHook.CONFIG_CATEGORY)
            .name("isy-connector")
            .i18n("ISYConnector.Settings.MenuTitle")
            .page(ISYConnectorSettingsPage.class)
            .terms("isy connector settings")
            .build();

    public ISYConnectorSettingsPage(final IConfigPage configPage) {
        super(configPage, null, new LenientResourceModel("ISYConnector.Settings.PageTitle"),
                ((IgnitionWebApp) Application.get()).getContext().getPersistenceInterface().find(ISYConnectorSettingsRecord.META, 0L));
    }

    @Override
    public Pair<String, String> getMenuLocation() {
        return Pair.of("isy-connector", "settings");
    }

}