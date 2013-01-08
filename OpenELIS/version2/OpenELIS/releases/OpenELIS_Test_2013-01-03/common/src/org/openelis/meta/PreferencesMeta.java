package org.openelis.meta;

public class PreferencesMeta {
	
	public static final String DEFAULT_PRINTER = "defaultPrinter",
	                           DEFAULT_BAR_CODE_PRINTER = "defaultBarCodePrinter",
	                           LOCATION = "location";
	
	public static String getDefaultPrinter() {
		return DEFAULT_PRINTER;
	}
	
	public static String getDefaultBarCodePrinter() {
		return DEFAULT_BAR_CODE_PRINTER;
	}
	
	public static String getLocation() {
		return LOCATION;
	}

}
