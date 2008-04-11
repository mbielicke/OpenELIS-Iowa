<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.server.constants.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                extension-element-prefixes="resource"
                version="1.0">

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.server.constants.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>

  <xsl:template match="doc">
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle('org.openelis.modules.main.server.constants.OpenELISConstants',locale:new(string($language)))"/>
<screen id="organizeFavorites" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>	
	<panel layout="vertical" spacing="0" width="300px" xsi:type="Panel">
	  <xsl:for-each select="favorite">
	    <panel layout="horizontal" xsi:type="Panel">
	      <widget>
	      	<check key="{@label}"/>
	      </widget>
	      <widget>
	        <text><xsl:value-of select="@label"/></text>
	      </widget>
	    </panel>
	  </xsl:for-each>
	</panel>
	</display>
	<rpc>
	  <xsl:for-each select="favorite">
	    <check key="{@label}" required="false"/>
	  </xsl:for-each>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>
