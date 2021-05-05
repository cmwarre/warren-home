package co.warrenhome.tags;

import co.warrenhome.util.ReflectionUtils;
import com.inductiveautomation.ignition.common.TypeUtilities;
import com.inductiveautomation.ignition.common.browsing.BrowseFilter;
import com.inductiveautomation.ignition.common.browsing.Results;
import com.inductiveautomation.ignition.common.model.values.QualityCode;
import com.inductiveautomation.ignition.common.sqltags.model.types.DataType;
import com.inductiveautomation.ignition.common.tags.model.TagPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.persistence.Enumerated;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Consumer;

/**
 * This Class will Find All Fields Annotated with @PublishTag
 * and publish them to the MES Managed Tag Provider
 * TODO nested/recursive types work but aren't great...  as in buggy
 * */
@Slf4j
public class EntityTagMapper<T extends Object> {

    /**
     * Root Path to Map Tags to
     * */
    private final String rootPath;

    /**
     * DataType of Generic Model
     * */
    private final Class<T> type;

    /**
     * Instance of the Provided Model
     * */
    private T model;

    /**
     * Tag Provider Instance
     * */
    private ISYTagProvider provider; // todo

    @SuppressWarnings("unchecked")
    public EntityTagMapper(ISYTagProvider provider, String rootPath, T model) {
        this.provider = provider;
        this.model = model;
        this.rootPath = rootPath;
        this.type = (Class<T>) model.getClass();
    }

    public EntityTagMapper(String rootPath, Class<T> type, T model) {
        this.model = model;
        this.rootPath = rootPath;
        this.type = type;
    }

    private static <T extends Object> EntityTagMapper<T> from(String rootPath, Class<T> type, Object model){
       return new EntityTagMapper<>(rootPath, type, type.cast(model));
    }

    public void setModel(T model){
        this.model = model;
    }

    /**
     * Search through a model for fields and pass them to a consumer
     * */
    public void getModelFields(Consumer<Field> consumer){

        List<Field> fields = ReflectionUtils.getModelFieldsAsList(type);

        for (Field field : fields) {
            PublishTag tag = field.getAnnotation(PublishTag.class);
            if (tag != null) {
                log.trace(String.format("Found Object Field/Tag %s", tag.name()));
                consumer.accept(field);
            }
        }
    }

    /**
     * Builds All Tags for Associated Object in Managed Tag Provider.
     * Recursive Method if there are Object Members of Classes
     * */
    public void configureTags() {
        getModelFields(this::configureTag);
    }

    /**
     * Discerns the type of tag, how it should map into the tag tree and maps it
     * */
    public void configureTag(Field field){
        PublishTag tag = field.getAnnotation(PublishTag.class);
        String tagPath = String.format("%s%s/%s", rootPath, tag.relativePath(), tag.name());

        if (tag.dataType().isArray() && !tag.dataType().equals(DataType.DataSet))
            configureArray(field, tag);
        else if(tag.dataType().equals(DataType.Document))
            configureNestedObject(field, tag);
        else
            provider.configureTag(tagPath, TagConfig.from(tag));

        if (tag.writeable())
            provider.registerWriteHandler(tagPath, (p, o) -> handleWrite(field, p, o));
    }

    /**
     * Adds an array of tags into the tag tree as a folder of tags
     * */
    private void configureArray(Field field, PublishTag tag){
        try {
            // find array length to configure tags
            String[] values = BeanUtils.getArrayProperty(model, field.getName());
            if (values != null) {
                for (int i = 0; i < values.length; i++) {
                    String tagPath = String.format("%s%s%s/%s[%d]",
                            rootPath, tag.relativePath(), tag.name(), tag.name(), i);

                    TagConfig config = TagConfig.from(tag);
                    config.setDataType(tag.dataType().getComponentDataType());
                    provider.configureTag(tagPath, config);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.warn("Error while finding values for array", e);
        }
    }

    /**
     * Adds a nested object into the tag tree as a folder of tags
     * */
    private void configureNestedObject(Field field, PublishTag tag){
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Object> newType = (Class<? extends Object> )field.getType();
            String newRootPath = String.format("%s%s", rootPath, tag.relativePath());
            Object value = FieldUtils.readField(field, model, true);
            if(value != null)
                EntityTagMapper.from(newRootPath, newType, newType.cast(value)).configureTags();
        } catch (Exception e) {
            log.warn("Error while mapping subclass", e);
        }
    }

    /**
     * Configures a mapped tag to have a write handler
     * */
    private QualityCode handleWrite(Field field, TagPath tagPath, Object value){
        log.trace(String.format("Attempting to Write %s with Value %s", tagPath, value.toString()));

        try {
            if(field.getAnnotation(Enumerated.class) != null)
               handleEnumWrite(field, value);
            else
                BeanUtils.setProperty(model, field.getName(), value);

            provider.updateValue(tagPath.toStringFull(), value, QualityCode.Good);

            log.trace(String.format("%s Written with Value %s", tagPath, value.toString()));
            return QualityCode.Good;
        } catch (Exception e) {
            log.warn(String.format("Error writing to tag %s", tagPath), e);
            return QualityCode.Bad;
        }
    }

    /**
     * Handle writes to an enum field.  This is separated for jUnit tests
     * */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void handleEnumWrite(Field field, Object value) throws InvocationTargetException, IllegalAccessException {
        Enum<?> enumValue = Enum.valueOf((Class<Enum>)field.getType(), value.toString());
        BeanUtils.setProperty(model, field.getName(), enumValue);
    }

    /**
     * Remove All Tags from Managed Tag Provider.  Directly deleting tags didn't seem to work so this will do a
     * recursive browse and directly delete every tag that it sees.
     * */
    public void removeTags() {
        Results<BrowseTag> results = provider.browseRecursive(rootPath, new BrowseFilter());
        for(BrowseTag tag : results.getResults()){
            provider.removeTag(tag.getTagPath());
        }
        provider.removeTag(rootPath);
    }

    /**
     * Update All Tag Values for Entity Object
     * */
    public void updateTags() {
        getModelFields(this::updateTag);
    }

    /**
     * Update the value for a Single Class Member/Tag
     * */
    private void updateTag(Field field){
        PublishTag tag = field.getAnnotation(PublishTag.class);
        String tagPath = String.format("%s%s/%s", rootPath, tag.relativePath(), tag.name());
        log.trace(String.format("Updating Tag %s", tagPath));

        try {
            if(tag.dataType().isArray() && tag.dataType() != DataType.DataSet){
                updateArrayTag(field, tag);
            } else if(tag.dataType().equals(DataType.Document)) {
                updateNestedObject(field, tag);
            } else if(field.getClass().isEnum()) {
                updateEnumTag(field, tagPath, tag);
            } else {
                updateTag(field, tagPath, tag);
            }
        } catch (Exception e) {
            log.error(String.format("Uncaught exception while updating field %s for class %s",
                    field.getName(), type.toString()), e);
            provider.updateValue(tagPath, null, QualityCode.Uncertain);
        }
    }

    /**
     * Update an array of tags that match a class member
     * */
    private void updateArrayTag(Field field, PublishTag tag){
        try {
            String[] values = BeanUtils.getArrayProperty(model, field.getName());
            for(int i=0; i < values.length; i++){
                Object value;
                String tagPath = String.format("%s%s%s/%s[%d]", rootPath, tag.relativePath(), tag.name(), tag.name(), i);
                value = TypeUtilities.coerce(values[i], tag.dataType().getJavaType());
                provider.updateValue(tagPath, value, QualityCode.Good);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error(String.format("Exception Thrown While Updating Field %s for Class %s",
                    field.getName(), type.toString()), e);
        }
    }

    /**
     * Mapping for datatypes.  This will create a new instance of an object tag mapper recursively
     * */
    private void updateNestedObject(Field field, PublishTag tag){
        try {
            Class<?> newType = field.getType();
            String newRootPath = String.format("%s%s", rootPath, tag.relativePath());
            Object value = FieldUtils.readField(field, model, true);
            if(value != null)
                EntityTagMapper.from(newRootPath, newType, newType.cast(value)).updateTags();
        } catch (Exception e) {
            log.warn("Error while mapping subclass", e);
        }
    }

    /**
     * Updates an enum tag member
     * */
    private void updateEnumTag(Field field, String tagPath, PublishTag tag){
        try {
            Object value = FieldUtils.readField(field, model, true);
            provider.updateValue(tagPath,
                    TypeUtilities.coerce(value, tag.dataType().getJavaType()), QualityCode.Good);
        } catch (IllegalAccessException e) {
            log.warn(String.format("Error updating enum tag %s", tagPath), e);
        }
    }

    /**
     * Updates a single tag instance
     * */
    private void updateTag(Field field, String tagPath, PublishTag tag){
        try {
            Object value = FieldUtils.readField(field, model, true);

            if(tag.dataType() != DataType.DataSet)
                provider.updateValue(tagPath,
                        TypeUtilities.coerce(value, tag.dataType().getJavaType()), QualityCode.Good);
            else
                provider.updateValue(tagPath, value, QualityCode.Good);
        }catch (IllegalAccessException e){
            log.error(String.format("Exception Thrown While Updating Field %s for Class %s",
                    field.getName(), type.toString()), e);
        }
    }

}
