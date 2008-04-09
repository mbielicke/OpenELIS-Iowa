package org.openelis.domain;

import java.io.Serializable;

public class PreferencesDO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer system_user;
    private String key;
    private String text;
    
    public PreferencesDO() {
        
    }
    
    public PreferencesDO(Integer id,
                         Integer system_user,
                         String  key,
                         String  text) {
        this.id = id;
        this.system_user = system_user;
        this.key = key;
        this.text = text;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getSystem_user() {
        return system_user;
    }

    public void setSystem_user(Integer system_user) {
        this.system_user = system_user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    

}
