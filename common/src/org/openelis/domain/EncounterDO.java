package org.openelis.domain;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;


public class EncounterDO extends DataObject {

    private static final long serialVersionUID = 1L;
    

    
    protected Integer   id,enteredBy,contact1,contact2,contactMethod,outcome,event,recommendation;
    protected String    contactInfo,contactName,comments,deceased,type;
    protected Datetime  occurred,updated;
    
    public EncounterDO() {
        
    }
    
    public EncounterDO(Integer id, Integer event, String type, Integer enteredBy, Integer contact1, Integer contact2, 
                       String contactName, String contactInfo, Datetime occurred, Datetime updated,
                       Integer contactMethod, Integer outcome, Integer recommendation, String deceased, String comments) {
        setId(id);
        setEvent(event);
        setType(type);
        setEnteredBy(enteredBy);
        setContact1(contact1);
        setContact2(contact2);
        setContactName(contactName);
        setContactInfo(contactInfo);
        setOccurred(occurred);
        setUpdated(updated);
        setContactMethod(contactMethod);
        setOutcome(outcome);
        setRecommendation(recommendation);
        setDeceased(deceased);
        setComments(comments);
        _changed = false;
    }

    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
        _changed = true;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
        _changed = true;
    }
        
    public Datetime getOccurred() {
        return occurred;
    }
    
    public void setOccurred(Datetime occurred) {
        this.occurred = occurred;
        _changed = true;
    }
    
    public Datetime getUpdated() {
        return updated;
    }
    
    public void setUpdated(Datetime updated) {
        this.updated = updated;
        _changed = true;
    }
    
    public Integer getEnteredBy() {
        return enteredBy;
    }
    
    public void setEnteredBy(Integer enteredBy) {
        this.enteredBy = enteredBy;
        _changed = true;
    }
    
    public Integer getContact1() {
        return contact1;
    }
    
    public void setContact1(Integer contact1) {
        this.contact1 = contact1;
        _changed = true;
    }
    
    public Integer getContact2() {
        return contact2;
    }
    
    public void setContact2(Integer contact2) {
        this.contact2 = contact2;
        _changed = true;
    }
    
    public Integer getContactMethod() {
        return contactMethod;
    }
    
    public void setContactMethod(Integer contactMethod) {
        this.contactMethod = contactMethod;
        _changed = true;
    }
    
    public String getContactInfo() {
        return contactInfo;
    }
    
    public void setContactInfo(String contactInfo) {
        this.contactInfo = DataBaseUtil.trim(contactInfo);
        _changed = true;
    }
    
    public Integer getOutcome() {
        return outcome;
    }
    
    public void setOutcome(Integer outcome) {
        this.outcome = outcome;
        _changed = true;
    }
    
    public Integer getEvent() {
        return event;
    }
    
    public void setEvent(Integer event) {
        this.event = event;
        _changed = true;
    }
    
    public String getContactName() {
        return contactName;
    }
    
    public void setContactName(String contactName) {
        this.contactName = DataBaseUtil.trim(contactName);
        _changed = true;
    }
    
    public String getComments() {
        return comments;
    }
    
    public void setComments(String comments) {
        this.comments = comments;
        _changed = true;
    }
    
    public String getDeceased() {
        return deceased;
    }
    
    public void setDeceased(String deceased) {
        this.deceased = DataBaseUtil.trim(deceased);
        _changed = true;
    }
    
    public Integer getRecommendation() {
        return recommendation;
    }
    
    public void setRecommendation(Integer recommendation) {
        this.recommendation = recommendation;
        _changed = true;
    }    

}
