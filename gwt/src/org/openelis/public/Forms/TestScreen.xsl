<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://java.util.ResourceBundle"
                xmlns:locale="xalan://java.util.Locale"
                extension-element-prefixes="resource"
                version="1.0">

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://java.util.ResourceBundle"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>

  <xsl:template match="doc">
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle('org.openelis.server.constants.OpenELISConstants',locale:new(string($language)))"/>
    <screen id="main" serviceUrl="OpenELISService"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:noNamespaceSchemaLocation="file:///home/tschmidt/workspace/libraries/metadata/FormSchema.xsd">
      <display constants="OpenELISConstants">
	    <panel xsi:type="Panel" layout="vertical"> 
          <widget>
	        <text><xsl:value-of select='resource:getString($constants,"greeting")'/></text>
	      </widget> 
	      <widget>
	        <text>This is a blank example screen for testing.</text>
	      </widget>
    	</panel>
      </display>
      <rpc>
      </rpc>
    </screen>
  </xsl:template>
</xsl:stylesheet>
