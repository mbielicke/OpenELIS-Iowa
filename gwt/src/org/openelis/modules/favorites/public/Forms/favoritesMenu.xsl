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
		        <menuItem style="TopMenuRowContainer" enabled="true"  
			              hover="Hover"
				          icon="{$label}Icon"
		        		  label="{resource:getString($constants,$label)}"
				          description=""
				          value="{$value}"
				          onClick="org.openelis.modules.main.client.openelis.OpenELIS"/>
			    </xsl:for-each>
			</menuPanel>
	</display>
	<rpc key="display"></rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>