package org.openelis.bean;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jfree.util.Log;
import org.openelis.domain.OptionListItem;
import org.openelis.local.PrinterCacheLocal;
import org.openelis.remote.PrinterCacheRemote;
import org.openelis.utils.Printer;

/**
 * This class provides a simple cached interface to the printers.
 */
@Singleton
@SecurityDomain("openelis")
public class PrinterCacheBean implements PrinterCacheLocal, PrinterCacheRemote {

    protected ArrayList<Printer>       printerList;
    protected HashMap<String, Printer> printerHash;
    
    /**
     * Constructor
     */
    public PrinterCacheBean() {
        printerList = new ArrayList<Printer>();
        printerHash = new HashMap<String, Printer>();
        refresh();
    }

    /**
     * Returns the list of printers defined for this server
     */
    public ArrayList<OptionListItem> getList() {
        ArrayList<OptionListItem> tempList;

        tempList = new ArrayList<OptionListItem>();
        for (Printer p : printerList) 
            tempList.add(new OptionListItem(p.getName(), p.getDescription()));        

        return tempList;
    }

    /**
     * Returns a printer specified by name
     */
    public Printer getPrinterByName(String name) {
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
            for (Printer p : printerList) {
                if (type.equals(p.getType()))
                    tempList.add(new OptionListItem(p.getName(), p.getDescription()));
            }
        }
        return tempList;
    }    

    /**
     * Method to refresh the printer list cache
     */
    @Asynchronous
    @TransactionTimeout(30)
    public void refresh() {
        String name, type;
        Printer printer;
        PrintService printers[];
        ArrayList<Printer> tempList;
        HashMap<String, Printer> tempHash;

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
            Log.error(ioE.getMessage());
            tempList = null;
        } finally {
            if (tempList != null) {
                printerList = tempList;
                printerHash = tempHash;
            }
        }
    }
}