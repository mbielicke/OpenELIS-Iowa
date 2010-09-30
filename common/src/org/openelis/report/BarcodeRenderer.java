/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/

package org.openelis.report;

import net.sourceforge.barbecue.*;
/**
 * A wrapper for the Drawable interface in the JCommon library: you will need the
 * JCommon classes in your classpath to compile this class. In particular this can be
 * used to allow JFreeChart objects to be included in the output report in vector form.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: BarcodeRenderer.java 811 2006-04-19 22:22:16Z mbielick $
 */
public class BarcodeRenderer {
	/**
	 *
	 */	
    private Barcode barcode;

    /**
     *
     */ 
    public BarcodeRenderer(String codeType, Object value) {
        this(codeType, value, false);
    }

    /**
	 *
	 */	
	public BarcodeRenderer(String codeType, Object value, boolean printLabel) {
        String text = "";
        
        if (value != null)
            text = value.toString();
        
        try {
            if ("2of7".equals(codeType))
                barcode = BarcodeFactory.create2of7(text);
            else if ("3of9".equals(codeType))
                barcode = BarcodeFactory.create3of9(text, false);
            else if ("Codabar".equals(codeType))
                barcode = BarcodeFactory.createCodabar(text);
            else if ("Code128".equals(codeType))
                barcode = BarcodeFactory.createCode128(text);
            else if ("Code128A".equals(codeType))
                barcode = BarcodeFactory.createCode128A(text);
            else if ("Code128B".equals(codeType))
                barcode = BarcodeFactory.createCode128B(text);
            else if ("Code128C".equals(codeType))
                barcode = BarcodeFactory.createCode128C(text);
            else if ("Code39".equals(codeType))
                barcode = BarcodeFactory.createCode39(text, false);
            else if ("EAN128".equals(codeType))
                barcode = BarcodeFactory.createEAN128(text);
            else if ("GlobalTradeItemNumber".equals(codeType))
                barcode = BarcodeFactory.createGlobalTradeItemNumber(text);
            else if ("Monarch".equals(codeType))
                barcode = BarcodeFactory.createMonarch(text);
            else if ("NW7".equals(codeType))
                barcode = BarcodeFactory.createNW7(text);
            else if ("PDF417".equals(codeType))
                barcode = BarcodeFactory.createPDF417(text);
            else if ("SCC14ShippingCode".equals(codeType))
                barcode = BarcodeFactory.createSCC14ShippingCode(text);
            else if ("ShipmentIdentificationNumber".equals(codeType))
                barcode = BarcodeFactory.createShipmentIdentificationNumber(text);
            else if ("SSCC18".equals(codeType))
                barcode = BarcodeFactory.createSSCC18(text);
            else if ("USD3".equals(codeType))
                barcode = BarcodeFactory.createUSD3(text, false);
            else if ("USD4".equals(codeType))
                barcode = BarcodeFactory.createUSD4(text);
            else if ("USPS".equals(codeType))
                barcode = BarcodeFactory.createUSPS(text);
        } catch (BarcodeException ignE) {}
        barcode.setDrawingText(printLabel);
	}

	/**
	 *
	 */
	public java.awt.Image render() {
		if (barcode != null) {
            return BarcodeImageHandler.getImage(barcode);
		}
        return null;
	}
}
