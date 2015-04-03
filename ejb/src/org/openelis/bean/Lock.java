package org.openelis.bean;

public class Lock {

    protected Key key;
    
    protected Long expires;

    protected Integer systemUserId;

    protected String sessionId;
   
    public Lock(Integer referenceTableId, Integer referenceId) {
    	this.key = new Key(referenceTableId,referenceId);
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
    	sb.append(key.referenceTableId);
    	sb.append(":");
    	sb.append(key.referenceId);
    	sb.append(":");
    	sb.append(expires);
    	sb.append(":");
    	sb.append(systemUserId);
    	sb.append(":");
    	sb.append(sessionId);
    	return sb.toString();
    }
    
    public static class Key {
        protected Integer referenceTableId;

        protected Integer referenceId;
        
        public Key(Integer referenceTableId, Integer referenceId) {
        	this.referenceTableId = referenceTableId;
        	this.referenceId = referenceId;
        }
        
        @Override
        public int hashCode() {
            int hash = 5;
            hash = 17 * hash + (referenceTableId == null ? 0 : referenceTableId.hashCode());
            hash = 17 * hash + (referenceId == null ? 0 : referenceId.hashCode());
            return hash;
        }
        
        @Override
        public boolean equals(Object obj) {
        	if (obj instanceof Key) {
        		return referenceTableId.equals(((Key)obj).referenceTableId) &&
        			   referenceId.equals(((Key)obj).referenceId);
        	}
        	return false;
        }
    }
}
