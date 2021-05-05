package co.warrenhome.entity;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Data
@XmlRootElement(name = "nodes")
@XmlAccessorType(XmlAccessType.FIELD)
public class NodeResponse extends BaseEntity {

    private Root root;

    @XmlElement(name="node")
    private List<Node> nodes = new ArrayList<>();
}
