package org.openelis.local;

import javax.ejb.Local;

@Local
public interface LockLocal {
    
    public boolean isLocked(Integer table, Integer row);
    
    public Integer getLock(Integer table, Integer row) throws Exception;
    
    public void giveUpLock(Integer table, Integer row);
    
    public void giveUpUserLocks();

}
