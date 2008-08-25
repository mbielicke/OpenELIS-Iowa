<!--
 The contents of this file are subject to the Mozilla Public License
 Version 1.1 (the "License"); you may not use this file except in
 compliance with the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/

 Software distributed under the License is distributed on an "AS IS"
 basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 License for the specific language governing rights and limitations under
 the License.
 
 The Original Code is OpenELIS code.
 
 Copyright (C) The University of Iowa.  All Rights Reserved.
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
<screen id="organizeFavoritesAdd" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
         	<menuPanel layout="vertical" style="topMenuContainer" key="favoritesMenu" width="220px">
	  		<xsl:for-each select="favorite">
			    <xsl:variable name="label"><xsl:value-of select="@label"/></xsl:variable>
			    <xsl:variable name="value"><xsl:value-of select="@value"/></xsl:variable>
			   <menuItem  style="TopMenuRowContainer" enabled="true"  
	          		     hover="Hover"
	          	         icon="{$label}Icon"
	   		  		     label="{resource:getString($constants,$label)}"
	          	         description="" 
	                     class="{$value}"
	                     onClick="org.openelis.modules.main.client.openelis.OpenELIS"/>
			    </xsl:for-each>
			</menuPanel>
	</display>
	<rpc key="display"></rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>
