package org.openelis.bean;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.jboss.ejb3.annotation.SecurityDomain;

@Singleton
@SecurityDomain("openelis")
public class LockCacheBean {
	
	private HashMap<Lock.Key,Lock> locks = new HashMap<Lock.Key,Lock>();
	
	@javax.ejb.Lock(LockType.READ)
	public Lock get(Lock.Key key) {
		return locks.get(key);
	}
	
	public void remove(Lock lock) {
		remove(lock.key);
	}
	
	public void remove(Lock.Key key) {
		locks.remove(key);
	}
	
	public void add(Lock lock) {
		locks.put(lock.key, lock);
	}

	@javax.ejb.Lock(LockType.READ)
	public boolean containsAllLocks(List<Lock> locks) {
		return locks.containsAll(locks);
	}
	
	@javax.ejb.Lock(LockType.READ)
	public boolean containsAllKeys(List<Lock.Key> keys) {
		return locks.keySet().containsAll(keys);
	}
	
	public Collection<Lock> getAll() {
		return locks.values();
	}
}
