package org.openelis.manager;

import org.openelis.bean.UserCacheBean;
import org.openelis.constants.OpenELISConstants;
import org.openelis.utils.EJBFactory;

import com.teklabs.gwt.i18n.client.LocaleFactory;
import com.teklabs.gwt.i18n.server.LocaleProxy;

public class MessagesProxy {
    
    static UserCacheBean userCache = EJBFactory.getUserCache();
    
    public static OpenELISConstants get() {
        LocaleProxy.initialize();
        return LocaleFactory.get(OpenELISConstants.class,userCache.getLocale());
    }

}
