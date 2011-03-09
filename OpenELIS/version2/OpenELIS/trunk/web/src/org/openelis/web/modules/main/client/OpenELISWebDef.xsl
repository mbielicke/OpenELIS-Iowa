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
      <screen name="Openelis Web V2.0">
        <TablePanel style="AppBackground" width="100%" height="100%" spacing="0" padding="0">
        	<row >
        	  <widget width="60px" height="20px">
        	    <icon style="homeIcon" key="home" enable="true"/>
        	  </widget>
        	  <widget align="right">
        	    <HorizontalPanel style="WelcomeLogout">
              	  <label style="webLabel" key="welcome"/>
              	  <text style="webLabel"> | </text>
              	  <label style="webLabel,clickable" key="logout" text="Logout"/>
                </HorizontalPanel>
              </widget>
        	</row> 
        	<row>
        	  <AbsolutePanel style="Links" key="links" height="100%"/>
        	  <widget colspan="2">
        	    <AbsolutePanel key="content" width="100%" height="100%"/>
        	  </widget>
        	</row>
        </TablePanel>
      </screen>
    </xsl:template>
</xsl:stylesheet>