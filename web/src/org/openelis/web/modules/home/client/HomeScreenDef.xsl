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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                xmlns:fn="http://www.w3.org/2005/xpath-functions" 
                extension-element-prefixes="resource"
               	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    			xsi:noNamespaceSchemaLocation="http://openelis.uhl.uiowa.edu/schema/ScreenSchema.xsd"
    			xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd" 
                version="1.0">

    <xalan:component prefix="resource">
      <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
    </xalan:component>
  
    <xalan:component prefix="locale">
      <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
    </xalan:component>
    
  
    <xsl:variable name="language"><xsl:value-of select="doc/locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="doc/props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
    <xsl:template match="doc">
      <screen name="Home">
      <DeckPanel height="100%" key="home" width="100%" style="ContentPanel">
        <deck>      
              <TablePanel height = "100" width="100%">
               <row> 
                 <html style="welcome"><![CDATA[Welcome to State Hygienic Laboratory WebPortal. This portal allows you to access your most current laboratory data at any time, and from anywhere you have internet connectivity. To begin, please choose an icon from the left panel to start your search: <ul><li>Locating report of results (Final Report)</li>  <li> Checking the status of your samples (Sample inhouse status report)</li> <li>Advance exporting results by test/analyte (Samples by test)</li><li>Setting email notification alerts (Notification preference)</li><li>Changing your password to this portal (Change password)</li></ul>]]></html>
               </row>                            
               <row> 
                 <html style="news">NEWS: We recently added the capability to view private water quality results through our web portal. This is especially useful for our realtor and large private testing groups that need quick and convenient access to their results.</html>
               </row>              
              </TablePanel>
                   </deck>   
        </DeckPanel> 
      </screen>
    </xsl:template>
</xsl:stylesheet>
