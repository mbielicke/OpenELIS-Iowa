package org.openelis.bean;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
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
	
	public List<Lock> get(Collection<Lock.Key> keys) {
		ArrayList<Lock> locks = new ArrayList<Lock>();
		Lock lock = null;
		for(Lock.Key key : keys) {
			lock = get(key);
			if(lock != null) {
				locks.add(get(key));
			}
		}
		return locks;
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
		return this.locks.values().containsAll(locks);
	}
	
	@javax.ejb.Lock(LockType.READ)
	public boolean containsAllKeys(List<Lock.Key> keys) {
		return locks.keySet().containsAll(keys);
	}
	
	@javax.ejb.Lock(LockType.READ)
	public Collection<Lock> getAll() {
		return locks.values();
	}
	
	public void removeAll() {
		locks.clear();
	}
}
