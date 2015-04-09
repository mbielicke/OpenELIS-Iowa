package org.openelis.bean;

public class Lock {

    protected Key key;
    protected Long expires;
    protected Integer systemUserId;
    protected String sessionId;
   
    public Lock(Integer referenceTableId, Integer referenceId) {
    	this.key = new Key(referenceTableId,referenceId);
    } 
    
    public Lock(Integer tableId, Integer id, Integer systemUserId, Long expires, String sessionId) {
    	this(tableId,id);
    	this.systemUserId = systemUserId;
    	this.expires = expires;
    	this.sessionId = sessionId;
    }
    
    @Override
    public int hashCode() {
    	return key.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (obj instanceof Lock) {
    		return key.equals(((Lock)obj).key);
    	}
    	return false;
    }
    
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append(key.tableId);
    	sb.append(":");
    	sb.append(key.id);
    	sb.append(":");
    	sb.append(expires);
    	sb.append(":");
    	sb.append(systemUserId);
    	sb.append(":");
    	sb.append(sessionId);
    	return sb.toString();
    }
    
    public static class Key {
        protected Integer tableId,id;
       
        public Key(Integer tableId, Integer id) {
        	this.tableId = tableId;
        	this.id = id;
        }
        
        @Override
        public int hashCode() {
            int hash = 5;
            hash = 17 * hash + (tableId == null ? 0 : tableId.hashCode());
            hash = 17 * hash + (id == null ? 0 : id.hashCode());
            return hash;
        }
        
        @Override
        public boolean equals(Object obj) {
        	if (obj instanceof Key) {
        		return tableId.equals(((Key)obj).tableId) &&
        			   id.equals(((Key)obj).id);
        	}
        	return false;
        }
    }
}
