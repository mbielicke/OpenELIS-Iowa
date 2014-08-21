<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.server.constants.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZTwoColumnsNum.xsl"/>   
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
<screen id="Storage" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<panel layout="horizontal" spacing="0" padding="0" style="WhiteContentPanel" xsi:type="Panel">
			<!--left table goes here -->
				<aToZ height="260px" width="100%" key="hideablePanel" visible="false">
				 <panel layout="horizontal" style="ScreenLeftPanel" xsi:type="Panel" spacing="0">
				 <xsl:if test="string($language)='en'">
			<xsl:call-template name="aToZLeftPanelButtons"/>
		</xsl:if>

				<table manager="StorageUnitDescTable" width="auto" style="ScreenLeftTable" key="StorageUnitTable" maxRows="10" title="" showError="false">
				<headers><xsl:value-of select='resource:getString($constants,"description")'/></headers>
							<widths>175</widths>
							<editors>
								<label/>
							</editors>
							<fields>
								<string/>
							</fields>
							<sorts>false</sorts>
							<filters>false</filters>
				</table>
				</panel>
				</aToZ>
			<panel layout="vertical" spacing="0" xsi:type="Panel">
			<!--button panel code-->
			<xsl:call-template name="buttonPanelTemplate">
				<xsl:with-param name="buttonsParam">qpn|aud|cb</xsl:with-param>
			</xsl:call-template>

				<panel key="formDeck" layout="deck" xsi:type="Deck" align="left">
					<deck>
					<panel layout="vertical" xsi:type="Panel">
							<panel key="secMod2" layout="table" style="Form" xsi:type="Table">
								<row>
									<panel layout="horizontal" xsi:type="Panel" style="FormVerticalSpacing"/>
								</row>
								<row>								
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"category")'/>:</text>
									</widget>
									<widget>
										<autoDropdown key="category" cat="category" case="mixed" serviceUrl="OpenELISServlet?service=org.openelis.modules.storageunit.server.StorageUnitService" width="110px" dropdown="true" type="string" fromModel="true" tab="description,id">
											<autoWidths>89</autoWidths>
											<autoEditors>
												<label/>
											</autoEditors>
											<autoFields>
												<string/>
											</autoFields>
										</autoDropdown>
										<query>
										<autoDropdown cat="category" case="mixed" serviceUrl="OpenELISServlet?service=org.openelis.modules.storageunit.server.StorageUnitService" width="110px" dropdown="true" type="string" fromModel="true" multiSelect="true" tab="description,id">
											<autoWidths>89</autoWidths>
											<autoEditors>
												<label/>
											</autoEditors>
											<autoFields>
												<string/>
											</autoFields>
										</autoDropdown>
										</query>
									</widget>
								</row>
								<row>								
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"description")'/>:</text>
									</widget>
									<widget>
										<textbox case="mixed" key="description" max="60" width="300px" tab="isSingular,category"/>
									</widget>
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"isSingular")'/>:</text>
									</widget>
									<widget>
										<check key="isSingular" tab="id,description"/>
										<query>
											<autoDropdown cat="isSingular" case="upper" serviceUrl="OpenELISServlet?service=org.openelis.modules.storageunit.server.StorageUnitService" width="40px" dropdown="true" type="string" multiSelect="true" tab="id,description">
													<autoWidths>19</autoWidths>
													<autoEditors>
														<label/>
													</autoEditors>
													<autoFields>
														<string/>
													</autoFields>
													<autoItems>
													<item value=""> </item>
													<item value="Y">Y</item>
													<item value="N">N</item>
													</autoItems>
													</autoDropdown>
										</query>
									</widget>
								</row>
							</panel>
				</panel>
					</deck> 
				</panel>
			</panel>
		</panel>
	</display>
	<rpc key="display">
  	<number key="id" type="integer" required="false"/>
  	<string key="categoryId" required="true"/>
  	<string key="description" max="60" required="true"/>
  	<check key="isSingular" required="false"/>
	</rpc>
	<rpc key="query">
 	<queryNumber key="id" type="integer" required="false"/>
 	<collection key="category" type="string" required="false"/>
  	<queryString key="description" required="true"/>
  	<collection key="isSingular" type="string" required="false"/>

	</rpc>
	<rpc key="queryByLetter">
		<queryString key="description"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>