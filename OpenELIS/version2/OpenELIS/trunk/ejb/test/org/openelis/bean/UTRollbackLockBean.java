package org.openelis.bean;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.UserTransaction;


@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class UTRollbackLockBean {
	
	@EJB
	LockBean lock;
	
	@Resource
	UserTransaction ut;
	
	public void setAndRollBack(Integer tableId, Integer id) throws Exception {
		ut.begin();
		lock.lock(tableId, id);
		ut.rollback();
	}
	
	public void setLock(Integer tableId, Integer id) throws Exception {
		ut.begin();
		lock.lock(tableId, id);
		ut.commit();
	}

}
