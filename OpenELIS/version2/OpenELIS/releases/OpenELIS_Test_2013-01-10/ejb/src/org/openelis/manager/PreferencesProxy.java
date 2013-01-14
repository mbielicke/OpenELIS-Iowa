package org.openelis.manager;

import java.util.HashMap;

import org.openelis.util.XMLUtil;
import org.openelis.utils.EJBFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is a proxy to the Preferences that is deployed to JBoss to do
 * persistence
 * 
 */
public class PreferencesProxy {

    /**
     * Proxy method the will call the PreferencesBean for the logged in user
     * preferences. It deconstructs the XML to create the Preferences
     * 
     * @return
     */
    public Preferences userRoot() {
        /**
         * Prefs are stored in the database as an xml string. This method will
         * parse the xml into a HashMap of key,value pairs.
         */
        try {
            return getNodeFor(EJBFactory.getUserCache().getId());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Proxy method the will call the PreferencesBean for the System
     * preferences. It deconstructs the XML to create the Preferences
     * 
     * @return
     */
    public Preferences systemRoot() {
        return getNodeFor(0);
    }

    /**
     * This method will create an XML string of the preferences and pass them to
     * the PreferenceBean for storing.
     * 
     * @param prefs
     */
    public void flush(Preferences prefs) throws Exception {
        int userId;
        Document prefsDoc;

        userId = EJBFactory.getUserCache().getId();
        if (! prefs.isUserNode() && userId != 0)
            return;
        
        /**
         * Preferences are stored in the database as a XML string. This method
         * will create a XML string from the map of key,value pairs.
         */
        prefsDoc = XMLUtil.createNew("root");
        createNode(prefsDoc, prefs, prefsDoc.getDocumentElement());
        EJBFactory.getPreferences().setPreferences(userId, XMLUtil.toString(prefsDoc));
    }

    /**
     * Recursive method to create an XML representation of the preferences
     * 
     * @param doc
     * @param prefs
     * @param node
     */
    private void createNode(Document doc, Preferences prefs, Node node) {
        String[] keys, children;
        String value;
        Node pref, child;

        keys = prefs.keys();
        for (String key : keys) {
            value = prefs.get(key, "");
            pref = doc.createElement(key);
            pref.appendChild(doc.createTextNode(value));
            node.appendChild(pref);
        }

        children = prefs.childrenNames();
        for (String childName : children) {
            child = doc.createElement(childName);
            createNode(doc, prefs.node(childName), child);
            node.appendChild(child);
        }
    }

    /**
     * Method that will call the Preference bean for the passed user to get the
     * XML string and create a preference object from it
     * 
     * @param user
     * @return
     */
    private Preferences getNodeFor(Integer user) {
        Document prefDoc;
        Preferences prefs;
        Node node;

        /**
         * Prefs are stored in the database as an xml string. This method will
         * parse the xml into a HashMap of key,value pairs.
         */
        try {
            prefDoc = getPreferencesDoc(user);
            node = prefDoc.getDocumentElement();
            prefs = getPreferences(node);
            prefs.setIsUser(user > 0);
            prefs.setAsRoot();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return prefs;
    }

    /**
     * Recursive method used to create the Preferences object from the XML
     * string returned from storage
     * 
     * @param path
     * @return
     */
    private Preferences getPreferences(Node path) {
        Preferences prefs, child;
        NodeList nodes;
        Node node;

        nodes = path.getChildNodes();
        prefs = new Preferences();
        prefs.path = path.getNodeName();
        for (int i = 0; i < nodes.getLength(); i++ ) {
            node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (node.getFirstChild() != null) {
                    if (node.getFirstChild().getNodeType() == Node.ELEMENT_NODE) {
                        child = getPreferences(node);
                        child.setParent(prefs);
                        if (prefs.nodes == null)
                            prefs.nodes = new HashMap<String, Preferences>();
                        prefs.nodes.put(child.name(), child);

                    } else
                        prefs.map.put(node.getNodeName(), node.getFirstChild().getNodeValue());
                }
            }
        }
        return prefs;
    }

    /**
     * Returns the XML Document of the stored preferences
     * 
     * @param key
     * @return
     */
    private Document getPreferencesDoc(Integer key) {
        try {
            return XMLUtil.parse(EJBFactory.getPreferences().getPreferences(key));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
