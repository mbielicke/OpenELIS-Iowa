
<!--
Exhibit A - UIRF Open-source Based Public Software License.
  
The contents of this file are subject to the UIRF Open-source Based
Public Software License(the "License"); you may not use this file except
in compliance with the License. You may obtain a copy of the License at
openelis.uhl.uiowa.edu
  
Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.
  
The Original Code is OpenELIS code.
  
The Initial Developer of the Original Code is The University of Iowa.
Portions created by The University of Iowa are Copyright 2006-2008. All
Rights Reserved.
  
Contributor(s): ______________________________________.
  
Alternatively, the contents of this file marked
"Separately-Licensed" may be used under the terms of a UIRF Software
license ("UIRF Software License"), in which case the provisions of a
UIRF Software License are applicable instead of those above. 
  -->
<xsl:stylesheet
  version="1.0"
  extension-element-prefixes="resource"
  xmlns:locale="xalan://java.util.Locale"
  xmlns:resource="xalan://org.openelis.util.UTFResource"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:noNamespaceSchemaLocation="http://openelis.uhl.uiowa.edu/schema/ScreenSchema.xsd"
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd">

  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="ProcessShipping" name="{resource:getString($constants,'processShipping')}">       
         <VerticalPanel width="380" padding="0" spacing="0" style="WhiteContentPanel">
           <TablePanel style="Form">
             <row>
               <widget colspan = "5">
                 <label key = "scanShippingBarcode" field = "String" text ="{resource:getString($constants,'scanShippingBarcode')}" />
               </widget>
             </row>
             <row>
               <widget colspan = "5">
                 <label key = "scanTrackingBarcode" field = "String" text ="{resource:getString($constants,'scanTrackingBarcode')}" />
               </widget>
             </row>
             <row>
               <widget colspan = "5">
                 <label key = "repeatProcess" field = "String" text ="{resource:getString($constants,'repeatProcess')}" />
               </widget>
             </row>             
             <row>
               <widget colspan = "5">
                 <label key = "closeWindow" field = "String" text ="{resource:getString($constants,'closeWindow')}" />
               </widget>
             </row>
             <row>
               <widget colspan = "5">
                 <VerticalPanel height="5"/>
               </widget>
             </row>
             <row>
               <text style="Prompt">
                 <xsl:value-of select='resource:getString($constants,"barcode")' />:
               </text>
               <textbox key="barcode" width="305" field="String" />               
             </row>
           </TablePanel>
         </VerticalPanel>       
    </screen>
  </xsl:template>
</xsl:stylesheet>