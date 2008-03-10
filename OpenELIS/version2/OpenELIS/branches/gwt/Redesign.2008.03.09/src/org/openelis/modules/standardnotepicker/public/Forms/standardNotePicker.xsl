<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.server.constants.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="buttonPanel.xsl"/>
                
  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.server.constants.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>

  <xsl:template match="doc"> 
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle('org.openelis.modules.main.server.constants.OpenELISConstants',locale:new(string($language)))"/>
<screen id="StandardNotePicker" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<panel layout="vertical" spacing="0" width="300px" xsi:type="Panel">
				<panel key="treeContainer" layout="vertical" height="250px" width="320px" overflow="auto" xsi:type="Panel">
				<!--tree-->
				<pagedTree key="noteTree" vertical="true" height = "200px" width = "300px" itemsPerPage="1000" title=""/>
				</panel>
				<panel layout="horizontal" xsi:type="Panel" spacing="10">
				<widget>
				<!-- text area-->
				<textarea key="noteText" width="300px" height="200px"/>
				</widget>
				</panel>
				
				<!--button panel code-->
			<xsl:call-template name="buttonPanelTemplate">
				<xsl:with-param name="buttonsParam">cb</xsl:with-param>
			</xsl:call-template>
			</panel>
	</display>
	<rpc key="display">
	<tree key="noteTree"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>