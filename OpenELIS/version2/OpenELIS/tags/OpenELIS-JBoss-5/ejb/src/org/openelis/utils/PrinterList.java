package org.openelis.utils;

import java.util.ArrayList;
import java.util.HashMap;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.openelis.domain.OptionListItem;

/**
 * This class provides a simple cached interface to the printers.
 */

public class PrinterList {

    protected ArrayList<Printer>       printerList;
    protected HashMap<String, Printer> printerHash;
    protected long                     lastPolled;

    protected int                      CACHE_TIME;

    private static PrinterList         instance = new PrinterList();

    /**
     * Returns an cached version of this class. 
     */
    public static PrinterList getInstance() {
        return instance;
    }
    
    /**
     * Constructor
     */
    protected PrinterList() {
        CACHE_TIME = 60 * 60 * 1000;
        lastPolled = 0;
    }

    /**
     * Returns the list of printers defined for this server
     */
    public ArrayList<OptionListItem> getList() {
        ArrayList<OptionListItem> tempList;

        refresh();
        tempList = new ArrayList<OptionListItem>();
        
        for (Printer p : printerList) 
            tempList.add(new OptionListItem(p.getName(), p.getDescription()));        

        return tempList;
    }

    /**
     * Returns a printer specified by name
     */
    public Printer getPrinterByName(String name) {
        refresh();
        return printerHash.get(name);
    }

    /**
     * Returns a list of printers that match the specified type. Currently the types are
     * "pdf", "zpl", and "unk". 
     */
    public ArrayList<OptionListItem> getListByType(String type) {
        ArrayList<OptionListItem> tempList;

        tempList = new ArrayList<OptionListItem>();
        if (type != null) {
            refresh();
            for (Printer p : printerList) {
                if (type.equals(p.getType()))
                    tempList.add(new OptionListItem(p.getName(), p.getDescription()));
            }
        }
        return tempList;
    }    

    /*
     * Internal method to refresh the printer list cache
     */
    protected void refresh() {
        long now;
        String name, type;
        Printer printer;
        PrintService printers[];
        ArrayList<Printer> tempList;
        HashMap<String, Printer> tempHash;

        now = System.currentTimeMillis();
        if (now > lastPolled + CACHE_TIME) {
            lastPolled = now;
            tempList = new ArrayList<Printer>();
            tempHash = new HashMap<String, Printer>();

            try {
                printers = PrintServiceLookup.lookupPrintServices(null, null);
                for (PrintService p : printers) {
                    name = p.getName();
                    /*
                     * This is a patch until we can figure out how to distinguish barcode
                     * printers from their mime type
                     */
                    type = "unk";
                    if (name.contains("_bar")) {
                        type = "zpl";
                    } else if (p.getSupportedDocFlavors() != null) {
                        for (DocFlavor f : p.getSupportedDocFlavors())
                            if (f.getMimeType().contains("application/pdf")) {
                                type = "pdf";
                                break;
                            }
                    }
                    printer = new Printer(name, name, type, p);
                    tempList.add(printer);
                    tempHash.put(name, printer);
                }
            } catch (Exception ioE) {
                ioE.printStackTrace();
                tempList = null;
                lastPolled = 0;
            } finally {
                if (tempList != null) {
                    printerList = tempList;
                    printerHash = tempHash;
                }
            }
        }
    }

}