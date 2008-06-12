
package org.openelis.entity;

/**
  * MethodResult Entity POJO for database 
  */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.openelis.util.Datetime;
import org.openelis.util.XMLUtil;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.openelis.utils.AuditUtil;
import org.openelis.utils.Auditable;

@Entity
@Table(name="method_result")
@EntityListeners({AuditUtil.class})
public class MethodResult implements Auditable, Cloneable {
  
  @Id
  @GeneratedValue
  @Column(name="id")
  private Integer id;             

  @Column(name="method_id")
  private Integer methodId;             

  @Column(name="result_group_id")
  private Integer resultGroupId;             

  @Column(name="flags")
  private String flags;             

  @Column(name="type")
  private String type;             

  @Column(name="value")
  private String value;             


  @Transient
  private MethodResult original;

  
  public Integer getId() {
    return id;
  }
  protected void setId(Integer id) {
    if((id == null && this.id != null) || 
       (id != null && !id.equals(this.id)))
      this.id = id;
  }

  public Integer getMethodId() {
    return methodId;
  }
  public void setMethodId(Integer methodId) {
    if((methodId == null && this.methodId != null) || 
       (methodId != null && !methodId.equals(this.methodId)))
      this.methodId = methodId;
  }

  public Integer getResultGroupId() {
    return resultGroupId;
  }
  public void setResultGroupId(Integer resultGroupId) {
    if((resultGroupId == null && this.resultGroupId != null) || 
       (resultGroupId != null && !resultGroupId.equals(this.resultGroupId)))
      this.resultGroupId = resultGroupId;
  }

  public String getFlags() {
    return flags;
  }
  public void setFlags(String flags) {
    if((flags == null && this.flags != null) || 
       (flags != null && !flags.equals(this.flags)))
      this.flags = flags;
  }

  public String getType() {
    return type;
  }
  public void setType(String type) {
    if((type == null && this.type != null) || 
       (type != null && !type.equals(this.type)))
      this.type = type;
  }

  public String getValue() {
    return value;
  }
  public void setValue(String value) {
    if((value == null && this.value != null) || 
       (value != null && !value.equals(this.value)))
      this.value = value;
  }

  
  public void setClone() {
    try {
      original = (MethodResult)this.clone();
    }catch(Exception e){}
  }
  
  public String getChangeXML() {
    try {
      Document doc = XMLUtil.createNew("change");
      Element root = doc.getDocumentElement();
      
      AuditUtil.getChangeXML(id,original.id,doc,"id");

      AuditUtil.getChangeXML(methodId,original.methodId,doc,"method_id");

      AuditUtil.getChangeXML(resultGroupId,original.resultGroupId,doc,"result_group_id");

      AuditUtil.getChangeXML(flags,original.flags,doc,"flags");

      AuditUtil.getChangeXML(type,original.type,doc,"type");

      AuditUtil.getChangeXML(value,original.value,doc,"value");

      if(root.hasChildNodes())
        return XMLUtil.toString(doc);
    }catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }
   
  public String getTableName() {
    return "method_result";
  }
  
}   
