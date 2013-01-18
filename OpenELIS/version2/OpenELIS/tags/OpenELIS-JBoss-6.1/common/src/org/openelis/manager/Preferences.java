/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.manager;

import java.util.HashMap;

import org.openelis.gwt.common.RPC;

/**
 * This is an implementation of the java.util.prefs.Preferences that is
 * available on the client and the server environment.
 */
public class Preferences implements RPC {

    private static final long                 serialVersionUID = 1L;

    /*
     * Since this implementation is serialized through RPC, we can not use hash
     * of hash to store preferences. This data is stored in map and nodes.
     */

    /**
     * Map of key,value pairs for this set.
     */
    protected HashMap<String, String>         map              = new HashMap<String, String>();
    /**
     * Map of child nodes belonging to this set
     */
    protected HashMap<String, Preferences>    nodes;
    /**
     * path name of this preference
     */
    protected String                          path;

    /**
     * Flag to mark this node as the root
     */
    protected boolean                         isRoot, isUser;
    /**
     * Link to the parent of this node
     */
    protected Preferences                     parent;

    /**
     * Link to the Proxy to be used for persistence
     */
    private transient static PreferencesProxy proxy;

    /**
     * Protected no-arg constructor for RPC requirement
     */
    protected Preferences() {
    }

    /**
     * Method will set the Parent of this node to the Preferences passed.
     * 
     * @param parent
     */
    protected void setParent(Preferences parent) {
        this.parent = parent;
    }

    /**
     * Method will mark this node as the root of a user or system preferences
     */
    protected void setAsRoot() {
        isRoot = true;
    }

    /**
     * Loads the node as a child of this node
     * 
     * @param node
     */
    protected void loadNode(Preferences node) {
        if (nodes == null)
            nodes = new HashMap<String, Preferences>();

        nodes.put(node.path, node);
        node.setParent(this);
    }

    /**
     * Returns true if there is a descendant node in the path from this node
     * 
     * @param path
     * @return
     */
    public boolean nodeExists(String path) {
        String paths[];
        Preferences prefs;

        if (nodes == null)
            return false;

        paths = path.split("/");
        prefs = this;
        for (int i = 0; i < paths.length; i++ ) {
            if (prefs.nodes == null || !prefs.nodes.containsKey(paths[i]))
                return false;
            prefs = prefs.nodes.get(paths[i]);
        }

        return true;
    }

    /**
     * Returns the descendant node based on the path returned. If node does not
     * exist then it will be created with all intermediate nodes created in
     * between as well
     * 
     * @param key
     * @return
     */
    public Preferences node(String key) {
        String[] paths;
        Preferences prefs, newPref;

        paths = key.split("/");
        prefs = this;
        for (int i = 0; i < paths.length; i++ ) {
            if (prefs.nodeExists(paths[i]))
                prefs = prefs.nodes.get(paths[i]);
            else {
                newPref = new Preferences();
                newPref.setParent(prefs);
                newPref.path = paths[i];
                prefs.loadNode(newPref);
                prefs = newPref;
            }
        }
        return prefs;
    }

    /**
     * Returns the String value stored in the map for the passed key. If the key
     * is not present or returns null the default parameter will be echoed back
     * 
     * @param key
     * @param def
     * @return
     */
    public String get(String key, String def) {
        String val;

        return ( (val = map.get(key)) != null) ? val : def;
    }

    /**
     * Returns the boolean value stored in the map for the passed key. If the
     * key is not present or returns null the default parameter will be echoed
     * back
     * 
     * @param key
     * @param def
     * @return
     */
    public boolean getBoolean(String key, boolean def) {
        String val;

        return ( (val = map.get(key)) != null) ? Boolean.valueOf(val).booleanValue() : def;
    }

    /**
     * Returns the double value stored in the map for the passed key. If the key
     * is not present or returns null the default parameter will be echoed back
     * 
     * @param key
     * @param def
     * @return
     */
    public double getDouble(String key, double def) {
        String val;

        return ( (val = map.get(key)) != null) ? Double.parseDouble(val) : def;
    }

    /**
     * Returns the int value stored in the map for the passed key. If the key is
     * not present or returns null the default parameter will be echoed back
     * 
     * @param key
     * @param def
     * @return
     */
    public int getInt(String key, int def) {
        String val;

        return ( (val = map.get(key)) != null) ? Integer.parseInt(val) : def;
    }

    /**
     * Returns the long value stored in the map for the passed key. If the key
     * is not present or returns null the default parameter will be echoed back
     * 
     * @param key
     * @param def
     * @return
     */
    public long getLong(String key, long def) {
        String val;

        return ( (val = map.get(key)) != null) ? Long.parseLong(val) : def;
    }

    /**
     * Stores the key,value pair of a String to the map
     * 
     * @param key
     * @param value
     */
    public void put(String key, String value) {
        map.put(key, value);
        // flush();
    }

    /**
     * Stores the key,value pair of a boolean to the map
     * 
     * @param key
     * @param value
     */
    public void putBoolean(String key, boolean value) {
        put(key, Boolean.toString(value));
    }

    /**
     * Stores the key,value pair of a double to the map
     * 
     * @param key
     * @param value
     */
    public void putDouble(String key, double value) {
        put(key, Double.toString(value));
    }

    /**
     * Stores the key,value pair of a int to the map
     * 
     * @param key
     * @param value
     */
    public void putInt(String key, int value) {
        put(key, Integer.toString(value));
    }

    /**
     * Stores the key,value pair of a long to the map
     * 
     * @param key
     * @param value
     */
    public void putLong(String key, long value) {
        put(key, Long.toString(value));
    }

    /**
     * Sets the value of the key passed for the map to null
     * 
     * @param key
     */
    public void remove(String key) {
        put(key, null);
    }

    /**
     * Removes the child node and all its descendants from this preference
     * 
     * @param key
     */
    public void removeNode(String key) {
        nodes.remove(key);
    }

    /**
     * Method to determine if this Preference represents a user or the system
     * 
     * @return
     */
    public boolean isUserNode() {
        return isUser;
    }

    /**
     * Returns the relative name of this node
     * 
     * @return
     */
    protected String name() {
        return path;
    }

    /**
     * Sets the system_user_id for the user this preferences represent
     * 
     * @param user
     */
    protected void setIsUser(boolean isUser) {
        this.isUser = isUser;
    }

    /**
     * Returns a list of all keys in the preference map
     * 
     * @return
     */
    public String[] keys() {
        return map != null ? map.keySet().toArray(new String[] {}) : new String[] {};
    }

    /**
     * Returns a list of all direct child node names belonging to this node.
     * 
     * @return
     */
    public String[] childrenNames() {
        return nodes != null ? nodes.keySet().toArray(new String[] {}) : new String[] {};
    }

    /**
     * Calling this method will force the preference to update itself.
     */
    public void flush() throws Exception {
        if ( !isRoot)
            parent.flush();
        else
            proxy().flush(this);
    }

    /**
     * Returns the User Preferences for the currently logged in User.
     * 
     * @return
     * @throws Exception
     */
    public static Preferences userRoot() throws Exception {
        return proxy().userRoot();
    }

    /**
     * Returns the preferences for the System
     * 
     * @return
     * @throws Exception
     */
    public static Preferences systemRoot() throws Exception {
        return proxy().systemRoot();
    }

    /**
     * Creates an instance of the persistence proxy for preferences
     * 
     * @return
     */
    private static PreferencesProxy proxy() {
        if (proxy == null)
            proxy = new PreferencesProxy();
        return proxy;
    }

}
