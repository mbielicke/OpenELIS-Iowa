package org.openelis.modules.scriptlet.client;

import java.util.HashMap;

public class ScriptletFactory {
	
	public static interface Scriptlet<T> {
		public T getScriptlet();
	}
	
	static HashMap<String,Scriptlet<?>> scripts = new HashMap<String, Scriptlet<?>>();
	
	//This static block of code is to initialize the RunAsyncCode to create Scriptlet Objects
	static {
		/* Example of creating a code init for a scriptlet
		scripts.put("MyScript", new Scriptlet<MyScript>(){
			MyScript scriptlet;
	        public MyScript getScriptlet() {
	        	GWT.runAsync(new RunAsyncCallback() {
					public void onFailure(Throwable reason) {
						reason.printStackTrace();
						Window.alert(reason.getMessage());
					}
					public void onSuccess() {
						scriptlet = new MyScript();
					}
	        	});
	        	return scriptlet;
	        }
		});
		*/
	}
	    
    public static <T> T getScriptlet(String name) {
    	return (T)scripts.get(name).getScriptlet();
    }

}
