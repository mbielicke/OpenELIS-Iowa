package org.openelis.manager;

import java.io.Serializable;
import java.util.ArrayList;

import org.openelis.domain.DataObject;
import org.openelis.domain.PanelDO;
import org.openelis.domain.PanelItemDO;

/**
 * This class encapsulates a panel and all its related information including
 * items. Although the class provides some basic functions internally, it is
 * designed to interact with EJB methods to provide majority of the operations
 * needed to manage a panel.
 */
public class PanelManager1 implements Serializable {
    private static final long        serialVersionUID = 1L;

    protected PanelDO                panel;
    protected ArrayList<PanelItemDO> items;
    protected ArrayList<DataObject>  removed;

    transient public final PanelItem item             = new PanelItem();

    /**
     * Initialize an empty panel manager
     */
    public PanelManager1() {
    }

    /**
     * Returns the order view DO
     */
    public PanelDO getPanel() {
        return panel;
    }

    /**
     * Class to manage panel Item information
     */
    public class PanelItem {
        /**
         * Returns the item at specified index.
         */
        public PanelItemDO get(int i) {
            return items.get(i);
        }

        public PanelItemDO add() {
            PanelItemDO data;

            data = new PanelItemDO();
            if (items == null)
                items = new ArrayList<PanelItemDO>();
            items.add(data);

            return data;
        }

        /**
         * Adds the item at the end of the list
         */
        public void add(PanelItemDO item) {
            if (items == null)
                items = new ArrayList<PanelItemDO>();
            items.add(item);
        }

        /**
         * Adds the item at specified index
         */
        public void addAt(PanelItemDO item, int i) {
            if (items == null)
                items = new ArrayList<PanelItemDO>();
            items.add(i, item);
        }

        /**
         * Removes an item from the list at specified index
         */
        public PanelItemDO remove(int i) {
            PanelItemDO data;

            if (items == null || i >= items.size())
                return null;
            data = items.remove(i);
            dataObjectRemove(data.getId(), data);
            return data;
        }

        public void remove(PanelItemDO data) {
            items.remove(data);
            dataObjectRemove(data.getId(), data);
        }

        /**
         * Returns the number of items associated with this panel
         */
        public int count() {
            if (items != null)
                return items.size();
            return 0;
        }

        public void moveItem(int oldIndex, int newIndex) {
            PanelItemDO item;

            if (items == null)
                return;

            item = items.remove(oldIndex);

            if (newIndex >= count())
                add(item);
            else
                addAt(item, newIndex);
        }
    }

    /**
     * Adds the specified data object to the list of objects that should be
     * removed from the database.
     */
    private void dataObjectRemove(Integer id, DataObject data) {
        if (removed == null)
            removed = new ArrayList<DataObject>();
        if (id != null)
            removed.add(data);
    }
}
