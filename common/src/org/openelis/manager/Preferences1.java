package org.openelis.manager;

import java.io.Serializable;
import java.util.HashMap;

public class Preferences1 implements Serializable {

    private static final long               serialVersionUID = 1L;

    /**
     * Map of key,value pairs for this set.
     */
    protected HashMap<String, String>       map              = new HashMap<String, String>();
    /**
     * Map of child nodes belonging to this set
     */
    protected HashMap<String, Preferences1> nodes;
    /**
     * path name of this preference
     */
    protected String                        path;

    /**
     * Flag to mark this node as the root
     */
    protected boolean                       isRoot, isUser;
    /**
     * Link to the parent of this node
     */
    protected Preferences1                  parent;

    /**
     * Public no-arg constructor for Serializable requirement
     */
    public Preferences1() {
    }

    /**
     * Method will set the Parent of this node to the Preferences passed.
     * 
     * @param parent
     */
    public void setParent(Preferences1 parent) {
        this.parent = parent;
    }

    /**
     * Method will mark this node as the root of a user or system preferences
     */
    public void setAsRoot() {
        isRoot = true;
    }

    /**
     * Loads the node as a child of this node
     * 
     * @param node
     */
    public void loadNode(Preferences1 node) {
        if (nodes == null)
            nodes = new HashMap<String, Preferences1>();

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
        Preferences1 prefs;

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
    public Preferences1 node(String key) {
        String[] paths;
        Preferences1 prefs, newPref;

        paths = key.split("/");
        prefs = this;
        for (int i = 0; i < paths.length; i++ ) {
            if (prefs.nodeExists(paths[i]))
                prefs = prefs.nodes.get(paths[i]);
            else {
                newPref = new Preferences1();
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
    public String name() {
        return path;
    }

    /**
     * Sets the system_user_id for the user this preferences represent
     * 
     * @param user
     */
    public void setIsUser(boolean isUser) {
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

    public HashMap<String, Preferences1> getNodes() {
        return nodes;
    }

    public void setNodes(HashMap<String, Preferences1> nodes) {
        this.nodes = nodes;
    }

    public Preferences1 getParent() {
        return parent;
    }

    public HashMap<String, String> getMap() {
        return map;
    }

    public void setMap(HashMap<String, String> map) {
        this.map = map;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
