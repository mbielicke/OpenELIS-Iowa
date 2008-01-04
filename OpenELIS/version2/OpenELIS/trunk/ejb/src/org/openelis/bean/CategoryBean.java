package org.openelis.bean;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.openelis.domain.CategoryDO;
import org.openelis.local.LockLocal;
import org.openelis.remote.CategoryRemote;

import edu.uiowa.uhl.security.local.SystemUserUtilLocal;

@Stateless
public class CategoryBean implements CategoryRemote {
    
    @PersistenceContext(name = "openelis")
    private EntityManager manager;

    
    @EJB
    private SystemUserUtilLocal sysUser;
    
    @Resource
    private SessionContext ctx;
    
    private LockLocal lockBean;
    
    {
        try {
            InitialContext cont = new InitialContext();
            lockBean =  (LockLocal)cont.lookup("openelis/LockBean/local");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public CategoryDO getCategory(Integer categoryId, boolean unlock) {
        // TODO Auto-generated method stub
        return null;
    }

    public List getCategoryNameListByLetter(String letter,
                                            int startPos,
                                            int maxResults) {
        return null;
    }

    public CategoryDO getCategoryUpdate(Integer id) throws Exception {
        
        return null;
    }

    public Integer getSystemUserId() {
        
        return null;
    }

    public List query(HashMap fields, int first, int max) throws RemoteException {
       
        return null;
    }

    public Integer updateCategory(CategoryDO categoryDO, List dictEntries) {
        
        return null;
    }

}
