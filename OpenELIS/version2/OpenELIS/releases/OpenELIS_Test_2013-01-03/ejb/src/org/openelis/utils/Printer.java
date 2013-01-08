package org.openelis.utils;

import javax.print.PrintService;

/**
 * This class represents the structure of a printer entry
 */

public class Printer {
    protected String       name, description, type;
    protected PrintService printService;

    /**
     * Constructor for creating a printer definition
     */

    public Printer(String name, String description, String type, PrintService printService) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.printService = printService;
    }

    /**
     * Returns the short printer name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the display name
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns printer type. Printer types (for now) are 'hp' (HP), 'zpl'
     * (barcode), and 'unk' (unknown).
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the printer service for this printer
     */
    public PrintService getPrintService() {
        return printService;
    }
    
    /**
     * Overriden the toString to provide for prompt option list.
     */
    public String toString() {
        return description + "|" + name;
    }
}
