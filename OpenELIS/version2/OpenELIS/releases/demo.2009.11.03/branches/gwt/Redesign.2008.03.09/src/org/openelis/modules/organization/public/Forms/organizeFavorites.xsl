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
	<widget>
          <buttonPanel key="buttons">
            <appButton action="commit">
              <widget>
                <text>Ok</text>
              </widget>
            </appButton>
            <html>&lt;div
style="width:1px;height:20px;background:grey"/&gt;</html>
            <appButton action="abort">
              <widget>
                <text>Cancel</text>
              </widget>
            </appButton>
          </buttonPanel>
				</widget>
				<panel layout="vertical" xsi:type="Panel">
				<widget halign="center">
					<buttonPanel buttons="cb" key="buttons"/>
				</widget>
				<panel layout="horizontal" xsi:type="Panel">
				<!-- table -->
				<!--<widget>
								<table width="auto" height="135px" key="favoritesTable" rows="10" title="{resource:getString($constants,'favorites')}">
										<headers><xsl:value-of select='resource:getString($constants,"name")'/></headers>
										<widths>175</widths>
										<editors>
											<label/>
										</editors>
										<fields>
											<string/>
										</fields>
										<sorts>false</sorts>
										<filters>false</filters>
										<colAligns>left</colAligns>
									</table>
								</widget>-->
				<panel layout="vertical" xsi:type="Panel" spacing="10">
				<!-- spacer -->
				<panel layout="vertical" height="25px" xsi:type="Panel"/>
				<!-- 4 buttons -->
				<!-- add -->
				<widget>
					<button key="addButton" text="{resource:getString($constants,'addButton')}" width="75px"/>
				</widget>
				<!-- remove -->
				<widget>
					<button key="removeButton" text="{resource:getString($constants,'removeButton')}" width="75px"/>
				</widget>
				<!-- up -->
				<widget>
					<button key="upButton" text="{resource:getString($constants,'upButton')}" width="75px"/>
				</widget>
				<!-- down -->
				<widget>
					<button key="downButton" text="{resource:getString($constants,'downButton')}" width="75px"/>
				</widget>
				</panel>
				</panel>
		</panel>
		</panel>
	</display>
	<rpc></rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>
