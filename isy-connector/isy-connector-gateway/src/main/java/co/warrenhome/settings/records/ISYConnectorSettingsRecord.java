package co.warrenhome.settings.records;

import com.inductiveautomation.ignition.gateway.datasource.records.DatasourceRecord;
import com.inductiveautomation.ignition.gateway.localdb.persistence.*;
import com.inductiveautomation.ignition.gateway.sqltags.config.TagHistoryProviderRecord;
import com.inductiveautomation.ignition.gateway.web.components.editors.PasswordEditorSource;
import simpleorm.dataset.SRecordInstance;

import javax.swing.*;

public class ISYConnectorSettingsRecord extends PersistentRecord {

    public static final RecordMeta<ISYConnectorSettingsRecord> META = new RecordMeta<>(
            ISYConnectorSettingsRecord.class, "ISYConnector").setNounKey("ISYConnector.Settings.Noun")
            .setNounPluralKey("ISYConnector.Settings.Noun.Plural");

    // <editor-fold desc="Fields">

    public static final IdentityField Id = new IdentityField(META);

    /**
     * Database connection
     */
    public static final LongField DatasourceId = new LongField(META, "DatasourceId");
    public static final ReferenceField<DatasourceRecord> Datasource =
            new ReferenceField<>(META, DatasourceRecord.META, "Datasource", DatasourceId);

    /**
     * Tag Provider Name
     * */
    public static final StringField TagProviderName = new StringField(META, "TagProviderName").setDefault("TamakiMES");

    /**
     * Default historical provider to usu
     */
    public static final LongField HistoryProviderId = new LongField(META, "HistoryProviderId");
    public static final ReferenceField<TagHistoryProviderRecord> HistoricalProvider =
            new ReferenceField<>(META, TagHistoryProviderRecord.META, "HistoricalProvider", HistoryProviderId);

    /**
     * ISY Address
     */
    public static final StringField Address = new StringField(META, "Address");

    /**
     * ISY User
     */
    public static final StringField Username = new StringField(META, "Username");

    /**
     * ISY Password
     */
    public static final StringField Password = new StringField(META, "Password");

    /*
     * Sets keys for name and description properties in TamakiMES.properties resource file
     * */
    static {
        Datasource.getFormMeta().setFieldNameKey("ISYConnector.Settings.Datasource.Name");
        Datasource.getFormMeta().setFieldDescriptionKey("ISYConnector.Settings.Datasource.Desc");
        TagProviderName.getFormMeta().setFieldNameKey("ISYConnector.Settings.TagProviderName.Name");
        TagProviderName.getFormMeta().setFieldDescriptionKey("ISYConnector.Settings.TagProviderName.Desc");
        HistoricalProvider.getFormMeta().setFieldNameKey("ISYConnector.Settings.HistoricalProvider.Name");
        HistoricalProvider.getFormMeta().setFieldDescriptionKey("ISYConnector.Settings.HistoricalProvider.Desc");

        Address.getFormMeta().setFieldNameKey("ISYConnector.Settings.Address.Name");
        Address.getFormMeta().setFieldDescriptionKey("ISYConnector.Settings.Address.Desc");
        Username.getFormMeta().setFieldNameKey("ISYConnector.Settings.Username.Name");
        Username.getFormMeta().setFieldDescriptionKey("ISYConnector.Settings.Username.Desc");
        Password.getFormMeta().setFieldNameKey("ISYConnector.Settings.Password.Name");
        Password.getFormMeta().setFieldDescriptionKey("ISYConnector.Settings.Password.Desc");
        Password.getFormMeta().setEditorSource(PasswordEditorSource.getSharedInstance());
    }

    // </editor-fold>

    // <editor-fold desc="Accessors">


    /**
     * Eagerly load references while session is still active.
     */
    @Override
    protected void onQueryRecord() {
        super.onQueryRecord();

        if(getReferenceNoQuery(Datasource) != null) {
            findReference(Datasource);
            getDatasource().getDriver();
            getDatasource().getTranslator();
        }

        if(getReferenceNoQuery(HistoricalProvider) != null)
            findReference(HistoricalProvider);

    }

    /**
     * I wish this wasn't required but it seems to be...  This will first check if an instance of the reference
     * is available, and if not try to load it
     */
    private <T extends SRecordInstance> T getReference(ReferenceField<T> referenceField, Class<T> type) {
        //Get a reference, but do not query either the dataset or the database.
        // Returns null if a corresponding scalar field is null.
        // Returns Boolean.FALSE if it is not null, but has not been queried from the database.
        Object reference = getReferenceNoQuery(referenceField);

        if (type.isInstance(reference)) {
            return type.cast(reference);
        } else if (reference instanceof Boolean && getDataSet().getSession() != null) {
            findReference(referenceField);
            reference = getReferenceNoQuery(referenceField);
            return type.cast(reference);
        } else {
            return null;
        }
    }

    @Override
    public RecordMeta<?> getMeta() {
        return META;
    }

    public Long getId() {
        return getLong(Id);
    }

    public void setId(Long id) {
        setLong(Id, id);
    }

    public DatasourceRecord getDatasource() {
        return getReference(Datasource, DatasourceRecord.class);
    }

    public void setDatasource(DatasourceRecord datasource) {
        setReference(Datasource, datasource);
    }

    public String getTagProviderName(){
        return getString(TagProviderName);
    }

    public void setTagProviderName(String tagProviderName){
        setString(TagProviderName, tagProviderName);
    }

    public TagHistoryProviderRecord getHistoricalProvider() {
        return getReference(HistoricalProvider, TagHistoryProviderRecord.class);
    }

    public void setHistoricalProvider(TagHistoryProviderRecord historicalProvider) {
        setReference(HistoricalProvider, historicalProvider);
    }

    public String getAddress(){
        return getString(Address);
    }

    public void setAddress(String address){
        setString(Address, address);
    }

    public String getUsername(){
        return getString(Username);
    }

    public void setUsername(String username){
        setUsername(username);
    }

    public String getPassword(){
        return getString(Password);
    }

    public void setPassword(String password){
        setString(Password, password);
    }

    // </editor-fold>

    // <editor-fold desc="Categories">

    public static final Category General = new Category("ISYConnector.Settings.Category.General", 1000)
            .include(Datasource, TagProviderName, HistoricalProvider);

    // </editor-fold>

}
