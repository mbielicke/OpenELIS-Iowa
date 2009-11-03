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
                extension-element-prefixes="resource"
                version="1.0">

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>

  <xsl:template match="doc">
      <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
 <screen id="favorites" serviceUrl="OpenELISService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
	<menuPanel layout="vertical" style="topMenuContainer" key="favoritesMenu" width="220px">
	  <xsl:for-each select="favorite">
	    <xsl:variable name="icon"><xsl:value-of select="@label"/>Icon</xsl:variable>
	    <xsl:variable name="label"><xsl:value-of select="@label"/></xsl:variable>
	    <xsl:variable name="description"><xsl:value-of select="@label"/>Description</xsl:variable>
	    <xsl:variable name="value"><xsl:value-of select="@value"/></xsl:variable>
        <menuItem enabled="false"
	              hover="Hover">
	      <menuDisplay>
	        <TablePanel style="TopMenuRowContainer">
	          <row>
	           <widget align="right">
	             <HorizontalPanel>
	               <widget>
	                 <check key="{$value}"/>
	               </widget>
	             </HorizontalPanel>
	           </widget>
	            <widget>
	              <VerticalPanel>
	                <widget style="topMenuItemMiddle">
	                  <label wordwrap="true" text="{resource:getString($constants,$label)}" style="topMenuItemTitle,locked"/>
	                </widget>
	                <widget>
	                  <label wordwrap="true" text="{resource:getString($constants,$description)}" style="topMenuItemDesc,locked"/>
	                </widget>
	             </VerticalPanel>
	           </widget>
	         </row>  
	        </TablePanel>
	      </menuDisplay>
	    </menuItem>
	  </xsl:for-each>
	</menuPanel>
   </display>
   <rpc key="display">
     <xsl:for-each select="favorite">
       <check key="{@value}" required="false"><xsl:value-of select="@selected"/></check>
     </xsl:for-each>
   </rpc>
 </screen>
</xsl:template>
</xsl:stylesheet>
