package co.warrenhome.entity;

import co.warrenhome.tags.PublishTag;
import com.inductiveautomation.ignition.common.sqltags.model.types.DataType;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "property")
@XmlAccessorType(XmlAccessType.FIELD)
public class NodeProperty extends BaseEntity {

    @XmlAttribute(name = "id")
    String name;

    @PublishTag(name="Value", dataType = DataType.Float8, writeable = true)
    @XmlAttribute(name = "value")
    Double value;

    public void setValue(Double value){
        Double old = this.value;
        firePropertyChange("value", old, value);
        this.value = value;
    }

    @PublishTag(name="ValueStr", dataType = DataType.String)
    @XmlAttribute(name = "formatted")
    String valueString;

    @XmlAttribute(name = "uom")
    String uom;

}
