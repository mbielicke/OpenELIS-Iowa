package org.openelis.bean;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.openelis.ui.common.DatabaseException;

@Stateful
public class RollbackLockBean {
	
	@EJB
	LockBean lock;
	
	public void setLock(Integer tableId, Integer id) throws Exception {
		lock.lock(tableId, id);
	}
	
	public void multiLockCall() throws Exception {
		lock.lock(1,1);
		lock.lock(1,2);
	}
	
	public void multiLockCallRollback() throws Exception {
		multiLockCall();
		rollback();
	}
	public void rollback() throws Exception {
		throw new DatabaseException("Rollback");
	}
	
	public void setAndRollBack(Integer tableId, Integer id) throws Exception {
		setLock(tableId,id);
		rollback();
	}
	
	public void setAndRollBackList(Integer tableId, List<Integer> ids) throws Exception {
		lock.lock(tableId, ids);
		rollback();
	}

}
