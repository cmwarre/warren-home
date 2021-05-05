package co.warrenhome.entity;

import co.warrenhome.tags.PublishTag;
import com.inductiveautomation.ignition.common.sqltags.model.types.DataType;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "node")
@XmlAccessorType(XmlAccessType.FIELD)
public class Node extends BaseEntity {

    @PublishTag(name = "Address", dataType = DataType.String)
    private String address;

    @PublishTag(name = "Name", dataType = DataType.String)
    private String name;

    @PublishTag(name = "Type", dataType = DataType.String)
    private String type;

    @PublishTag(name = "Enabled", dataType = DataType.String)
    private Boolean enabled;

    @PublishTag(name = "DeviceClass", dataType = DataType.String)
    private Integer deviceClass;

    @PublishTag(name = "Wattage", dataType = DataType.String)
    private Double wattage;

    @PublishTag(name = "DCPeriod", dataType = DataType.String)
    private Double dcPeriod;

    @PublishTag(name = "StartDelay", dataType = DataType.String)
    private Integer startDelay;

    @PublishTag(name = "EndDelay", dataType = DataType.String)
    private Integer endDelay;

    @PublishTag(name = "pNode", dataType = DataType.String)
    private String pnode;

    @PublishTag(name = "ELK_ID", dataType = DataType.String)
    private String ELK_ID;

    @XmlElement(name="property")
    private NodeProperty property;

}
