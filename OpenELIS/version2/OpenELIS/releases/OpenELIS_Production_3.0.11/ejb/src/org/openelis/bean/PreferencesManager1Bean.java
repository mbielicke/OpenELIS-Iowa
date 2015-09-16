package org.openelis.bean;

import java.util.HashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jboss.security.annotation.SecurityDomain;
import org.openelis.manager.Preferences1;
import org.openelis.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class implements the PreferencesRemote and is the bean called by the
 * client to do Preference persistence
 * 
 */
@Stateless
@SecurityDomain("openelis")
public class PreferencesManager1Bean {

    @EJB
    PreferencesBean preferences;

    @EJB
    UserCacheBean   userCache;

    /**
     * Returns the User Preferences for the logged in user
     */
    public Preferences1 userRoot() throws Exception {
        /**
         * Prefs are stored in the database as an xml string. This method will
         * parse the xml into a HashMap of key,value pairs.
         */
        try {
            return getNodeFor(userCache.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the System Preferences
     */
    public Preferences1 systemRoot() throws Exception {
        return getNodeFor(0);
    }

    /**
     * Persists the passed Preferences object
     */
    public void flush(Preferences1 prefs) throws Exception {
        int userId;
        Document prefsDoc;

        userId = userCache.getId();
        if ( !prefs.isUserNode() && userId != 0)
            return;

        /**
         * Preferences are stored in the database as a XML string. This method
         * will create a XML string from the map of key,value pairs.
         */
        prefsDoc = XMLUtil.createNew("root");
        createNode(prefsDoc, prefs, prefsDoc.getDocumentElement());
        preferences.setPreferences(userId, XMLUtil.toString(prefsDoc));
    }

    /**
     * Recursive method to create an XML representation of the preferences
     * 
     * @param doc
     * @param prefs
     * @param node
     */
    private void createNode(Document doc, Preferences1 prefs, Node node) {
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
    private Preferences1 getNodeFor(Integer user) {
        Document prefDoc;
        Preferences1 prefs;
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
    private Preferences1 getPreferences(Node path) {
        Preferences1 prefs, child;
        NodeList nodes;
        Node node;

        nodes = path.getChildNodes();
        prefs = new Preferences1();
        prefs.setPath(path.getNodeName());
        for (int i = 0; i < nodes.getLength(); i++ ) {
            node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (node.getFirstChild() != null) {
                    if (node.getFirstChild().getNodeType() == Node.ELEMENT_NODE) {
                        child = getPreferences(node);
                        child.setParent(prefs);
                        if (prefs.getNodes() == null)
                            prefs.setNodes(new HashMap<String, Preferences1>());
                        prefs.getNodes().put(child.name(), child);

                    } else
                        prefs.getMap().put(node.getNodeName(), node.getFirstChild().getNodeValue());
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
            return XMLUtil.parse(preferences.getPreferences(key));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
