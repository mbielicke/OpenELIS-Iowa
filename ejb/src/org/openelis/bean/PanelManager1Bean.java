package org.openelis.bean;

import static org.openelis.manager.PanelManager1Accessor.addItem;
import static org.openelis.manager.PanelManager1Accessor.getItems;
import static org.openelis.manager.PanelManager1Accessor.getPanel;
import static org.openelis.manager.PanelManager1Accessor.getRemoved;
import static org.openelis.manager.PanelManager1Accessor.setPanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.domain.Constants;
import org.openelis.domain.DataObject;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.PanelDO;
import org.openelis.domain.PanelItemDO;
import org.openelis.manager.PanelManager1;
import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;

@Stateless
@SecurityDomain("openelis")
public class PanelManager1Bean {

    @Resource
    SessionContext              ctx;

    @EJB
    LockBean                    lock;

    @EJB
    PanelBean                   panel;

    @EJB
    PanelItemBean               panelItem;

    private static final Logger log = Logger.getLogger("openelis");

    public PanelManager1 getInstance() throws Exception {
        PanelManager1 pm = new PanelManager1();
        setPanel(pm, new PanelDO());
        return pm;
    }

    /**
     * Returns a panel manager for specified primary id
     */
    public PanelManager1 fetchById(Integer panelId) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<PanelManager1> pms;

        ids = new ArrayList<Integer>(1);
        ids.add(panelId);
        pms = fetchByIds(ids);
        return pms.size() == 0 ? null : pms.get(0);
    }

    /**
     * Returns panel managers for specified primary ids and requested load
     * elements
     */
    public ArrayList<PanelManager1> fetchByIds(ArrayList<Integer> panelIds) throws Exception {
        PanelManager1 pm;
        ArrayList<Integer> ids1;
        ArrayList<PanelManager1> pms;
        ArrayList<PanelDO> panels;
        HashMap<Integer, PanelManager1> map1;

        /*
         * to reduce database select calls, we are going to fetch everything for
         * a given select and unroll through loops.
         */
        pms = new ArrayList<PanelManager1>();

        /*
         * if there are no panels, then return an empty list
         */
        panels = panel.fetchByIds(panelIds);
        if (panels.size() < 1)
            return pms;

        /*
         * build level 1, everything is based on panel ids
         */
        ids1 = new ArrayList<Integer>();
        map1 = new HashMap<Integer, PanelManager1>();

        for (PanelDO data : panels) {
            pm = new PanelManager1();
            setPanel(pm, data);
            pms.add(pm);

            ids1.add(data.getId()); // for fetch
            map1.put(data.getId(), pm); // for linking
        }

        /*
         * item lists for each panel
         */
        for (PanelItemDO data : panelItem.fetchByPanelIds(ids1)) {
            pm = map1.get(data.getPanelId());
            addItem(pm, data);
        }

        return pms;
    }

    /**
     * Returns a panel manager based on the specified query
     */
    public ArrayList<PanelManager1> fetchByQuery(ArrayList<QueryData> fields, int first, int max) throws Exception {
        ArrayList<Integer> ids;

        ids = new ArrayList<Integer>();

        for (IdNameVO vo : panel.query(fields, first, max))
            ids.add(vo.getId());
        return fetchByIds(ids);
    }

    /**
     * Returns a locked panel manager with specified panel id
     */
    @RolesAllowed("panel-update")
    public PanelManager1 fetchForUpdate(Integer panelId) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<PanelManager1> pms;

        ids = new ArrayList<Integer>(1);
        ids.add(panelId);
        pms = fetchForUpdate(ids);
        return pms.size() == 0 ? null : pms.get(0);
    }

    /**
     * Returns a list of locked panel managers with specified panel ids
     */
    @RolesAllowed("panel-update")
    public ArrayList<PanelManager1> fetchForUpdate(ArrayList<Integer> panelIds) throws Exception {
        lock.lock(Constants.table().PANEL, panelIds);
        return fetchByIds(panelIds);
    }

    /**
     * Unlocks and returns a panel manager with specified panel id
     */
    @RolesAllowed({"panel-add", "panel-update"})
    public PanelManager1 unlock(Integer panelId) throws Exception {
        ArrayList<Integer> ids;
        ArrayList<PanelManager1> sms;

        ids = new ArrayList<Integer>(1);
        ids.add(panelId);
        sms = unlock(ids);
        return sms.size() == 0 ? null : sms.get(0);
    }

    /**
     * Unlocks and returns list of panel managers with specified panel ids
     */
    @RolesAllowed({"panel-add", "panel-update"})
    public ArrayList<PanelManager1> unlock(ArrayList<Integer> panelIds) throws Exception {
        lock.unlock(Constants.table().PANEL, panelIds);
        return fetchByIds(panelIds);
    }

    /**
     * Adds/updates the panel and related records in the database. The records
     * are validated before add/update and the panel record must have a lock
     * record if it has an id.
     */
    @RolesAllowed({"panel-add", "panel-update"})
    public PanelManager1 update(PanelManager1 pm) throws Exception {
        ArrayList<PanelManager1> pms;

        pms = new ArrayList<PanelManager1>(1);
        pms.add(pm);
        update(pms);
        return pms.get(0);
    }

    /**
     * Adds/updates all the panel and related records in the database. All the
     * records are validated before add/update and the panel records must each
     * have a lock record if they have an id.
     */
    @RolesAllowed({"panel-add", "panel-update"})
    public ArrayList<PanelManager1> update(ArrayList<PanelManager1> pms) throws Exception {
        PanelItemDO item;
        HashSet<Integer> ids;
        ArrayList<Integer> locks;
        ArrayList<PanelItemDO> items;

        ids = new HashSet<Integer>();

        validate(pms);

        /*
         * check all the locks
         */
        for (PanelManager1 pm : pms) {
            if (getPanel(pm).getId() != null)
                ids.add(getPanel(pm).getId());
        }
        if (ids.size() > 0) {
            locks = new ArrayList<Integer>(ids);
            lock.validateLock(Constants.table().PANEL, locks);
        } else {
            locks = null;
        }
        ids = null;

        for (PanelManager1 pm : pms) {

            /*
             * go through remove list and delete all the unwanted records
             */
            if (getRemoved(pm) != null) {
                for (DataObject data : getRemoved(pm)) {
                    if (data instanceof PanelItemDO)
                        panelItem.delete( ((PanelItemDO)data));
                    else
                        throw new Exception("ERROR: DataObject passed for removal is of unknown type");
                }
            }
            // add/update panel
            if (getPanel(pm).getId() == null)
                panel.add(getPanel(pm));
            else
                panel.update(getPanel(pm));

            if (getItems(pm) != null) {
                items = getItems(pm);
                for (int i = 0; i < items.size(); i++ ) {
                    item = items.get(i);
                    item.setSortOrder(i + 1);
                    if (item.getId() == null) {
                        item.setPanelId(getPanel(pm).getId());
                        panelItem.add(item);
                    } else {
                        panelItem.update(item);
                    }
                }
            }
        }

        if (locks != null)
            lock.unlock(Constants.table().PANEL, locks);

        return pms;
    }

    /**
     * Validates the panel manager for add or update. The routine throws a list
     * of exceptions/warnings listing all the problems for each panel.
     */
    private void validate(ArrayList<PanelManager1> pms) throws Exception {
        ValidationErrorsList e;

        e = new ValidationErrorsList();

        for (PanelManager1 pm : pms) {
            /*
             * panel level
             */
            if (getPanel(pm) != null) {
                if (getPanel(pm).isChanged()) {
                    try {
                        panel.validate(getPanel(pm));
                    } catch (Exception err) {
                        DataBaseUtil.mergeException(e, err);
                    }
                }
            }

            if (getItems(pm) != null) {
                for (PanelItemDO data : getItems(pm)) {
                    try {
                        panelItem.validate(data);
                    } catch (Exception err) {
                        DataBaseUtil.mergeException(e, err);
                    }
                }
            }
        }

        if (e.size() > 0)
            throw e;
    }
}
