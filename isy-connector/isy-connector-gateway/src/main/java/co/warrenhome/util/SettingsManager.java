package co.warrenhome.util;

import co.warrenhome.settings.records.ISYConnectorSettingsRecord;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

import java.sql.SQLException;

public class SettingsManager {

    private static final LoggerEx logger = LoggerEx.newBuilder().build(SettingsManager.class);
    private final GatewayContext gatewayContext;

    public SettingsManager(GatewayContext gatewayContext){
        this.gatewayContext = gatewayContext;
        verifySchema();
        maybeCreateSettings();
    }

    private static SettingsManager instance;

    public static SettingsManager getInstance(){
        return instance;
    }

    public static void initialize(GatewayContext gatewayContext){
        instance = new SettingsManager(gatewayContext);
    }

    /**
     * Verify that all internal DB tables have been created/exist
     */
    private void verifySchema() {
        try {
            gatewayContext.getSchemaUpdater().updatePersistentRecords(ISYConnectorSettingsRecord.META);
        } catch (SQLException e) {
            logger.error("Error verifying internal database records for TamakiMES", e);
        }
    }

    /**
     * Create a new settings record if one doesn't currently exist
     */
    private void maybeCreateSettings() {
        logger.debug("Creating Default TamakiMES Settings");

        try {
            ISYConnectorSettingsRecord settingsRecord = gatewayContext.getLocalPersistenceInterface().createNew(ISYConnectorSettingsRecord.META);
            settingsRecord.setId(0L);
            gatewayContext.getSchemaUpdater().ensureRecordExists(settingsRecord);
        } catch (Exception e) {
            logger.error("Failed to add default record for TamakiMES settings", e);
        }
    }

    /**
     * Get Settings From InternalDB
     * */
    public ISYConnectorSettingsRecord querySettings(){
        return gatewayContext.getLocalPersistenceInterface().find(ISYConnectorSettingsRecord.META, 0L);
    }

}
