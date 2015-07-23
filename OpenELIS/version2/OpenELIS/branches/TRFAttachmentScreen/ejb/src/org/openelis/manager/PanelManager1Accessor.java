package org.openelis.manager;

import java.util.ArrayList;

import org.openelis.domain.DataObject;
import org.openelis.domain.PanelDO;
import org.openelis.domain.PanelItemDO;

public class PanelManager1Accessor {
    /**
     * Set/get objects from panel manager
     */
    public static PanelDO getPanel(PanelManager1 pm) {
        return pm.panel;
    }

    public static void setPanel(PanelManager1 pm, PanelDO panel) {
        pm.panel = panel;
    }

    public static ArrayList<PanelItemDO> getItems(PanelManager1 pm) {
        return pm.items;
    }

    public static void setItems(PanelManager1 pm, ArrayList<PanelItemDO> items) {
        pm.items = items;
    }

    public static void addItem(PanelManager1 pm, PanelItemDO item) {
        if (pm.items == null)
            pm.items = new ArrayList<PanelItemDO>();
        pm.items.add(item);
    }

    public static ArrayList<DataObject> getRemoved(PanelManager1 pm) {
        return pm.removed;
    }
}
