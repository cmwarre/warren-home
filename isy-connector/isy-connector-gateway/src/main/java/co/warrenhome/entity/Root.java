package co.warrenhome.entity;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "root")
public class Root extends BaseEntity {
}
