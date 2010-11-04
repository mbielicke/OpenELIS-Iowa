package org.openelis.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.naming.InitialContext;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.openelis.bean.ApplicationCacheInt;

/**
 * This class provides a simple cached interface to the printers.
 */

public class PrinterList {

    protected ArrayList<Printer>       printerList;
    protected HashMap<String, Printer> printerHash;
    protected long                     lastPolled;

    protected int                      CACHE_TIME;

    private static ApplicationCacheInt application;

    static {
        InitialContext ctx;

        try {
            ctx = new InitialContext();
            application = (ApplicationCacheInt)ctx.lookup("openelis/ApplicationCache/local");
            application.setAttribute("Printer", "List", new PrinterList());
        } catch (Exception e) {
            application = null;
            e.printStackTrace();
        }
    }

    /**
     * Returns an cached version of this class. 
     */
    public static PrinterList getInstance() {
        PrinterList pl;

        pl = (PrinterList) application.getAttribute("Printer", "List");
        if (pl == null) {
            pl = new PrinterList();
            application.setAttribute("Printer", "List", pl);
        }
        return pl;
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
    public ArrayList<Printer> getList() {
        refresh();
        return (ArrayList<Printer>)printerList.clone();
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
     * "pdf", "zpl", and "unk" 
     */
    public ArrayList<Printer> getListByType(String type) {
        ArrayList<Printer> tempList;

        tempList = new ArrayList<Printer>();
        if (type != null) {
            refresh();
            for (Printer p : printerList) {
                if (type.equals(p.getType()))
                    tempList.add(p);
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
                    } else {
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
                printerList = tempList;
                printerHash = tempHash;
            } catch (Exception ioE) {
                ioE.printStackTrace();
            }
        }
    }

}