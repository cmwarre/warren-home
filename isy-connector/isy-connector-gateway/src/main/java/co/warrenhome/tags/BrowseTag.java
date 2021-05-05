package co.warrenhome.tags;

import com.inductiveautomation.ignition.common.model.values.QualifiedValue;
import com.inductiveautomation.ignition.common.sqltags.model.types.DataType;
import com.inductiveautomation.ignition.common.tags.browsing.NodeAttribute;
import com.inductiveautomation.ignition.common.tags.browsing.NodeDescription;
import com.inductiveautomation.ignition.common.tags.config.types.TagObjectType;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * Extentions
 * */
@Data
public class BrowseTag implements Serializable {

    private Set<NodeAttribute> attributes;
    private boolean hasChildren;
    private String tagPath;
    private DataType dataType;
    private QualifiedValue currentValue;
    private String displayFormat;
    private String name;
    private TagObjectType objectType;
    private String subTypeId;

    public BrowseTag(NodeDescription n, String tagPath){
        attributes = n.getAttributes();
        hasChildren = n.hasChildren();
        dataType = n.getDataType();
        currentValue = n.getCurrentValue();
        displayFormat = n.getDisplayFormat();
        name = n.getName();
        objectType = n.getObjectType();
        subTypeId = n.getSubTypeId();
        this.tagPath = tagPath;
    }

    public boolean hasChildren(){
        return hasChildren;
    }

}
