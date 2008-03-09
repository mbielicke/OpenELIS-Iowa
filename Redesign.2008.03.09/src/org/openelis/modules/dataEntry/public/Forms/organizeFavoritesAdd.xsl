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
<screen id="organizeFavoritesAdd" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display  constants="OpenELISConstants">
		<panel layout="vertical" xsi:type="Panel">
				<widget halign="center">
					<buttonPanel buttons="cb" key="popupButtons"/>
				</widget>
				<panel layout="vertical" xsi:type="Panel">
				<!-- table -->
				<widget>
								<table width="auto" height="200px" key="favoriteItemsTable" rows="12" title="">
										<headers><xsl:value-of select='resource:getString($constants,"add")'/>,
										<xsl:value-of select='resource:getString($constants,"name")'/></headers>
										<widths>25,250</widths>
										<editors>
											<check/>
											<label/>
										</editors>
										<fields>
											<check/>
											<string/>
										</fields>
										<sorts>false,true</sorts>
										<filters>false,true</filters>
										<colAligns>center,left</colAligns>
									</table>
								</widget>
				<panel layout="horizontal" xsi:type="Panel" spacing="10">
				<!-- spacer -->
				<panel layout="vertical" width="60px" xsi:type="Panel"/>
				<!-- 2 buttons -->
				<!-- deselect all -->
				<widget>
					<button key="selectAllButton" text="{resource:getString($constants,'selectAll')}" width="100px"/>
				</widget>
				<!-- select all -->
				<widget>
					<button key="deselectAllButton" text="{resource:getString($constants,'deselectAll')}" width="100px"/>
				</widget>
				</panel>
				</panel>
		</panel>
	</display>
	<rpc></rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>