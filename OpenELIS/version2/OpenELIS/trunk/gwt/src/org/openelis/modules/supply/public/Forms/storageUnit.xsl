<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.server.constants.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                extension-element-prefixes="resource"
                version="1.0">
             <xsl:import href="aToZTwoColumnsNum.xsl"/>   
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
				<aToZ height="260px" width="100%" key="hideablePanel" visible="false" onclick="this">
				 <panel layout="horizontal" style="ScreenLeftPanel" xsi:type="Panel" spacing="0">
				 <xsl:if test="string($language)='en'">
			<xsl:call-template name="aToZLeftPanelButtons"/>
		</xsl:if>

				<table manager="StorageUnitDescTable" width="auto" style="ScreenLeftTable" key="StorageUnitTable" maxRows="10" title="">
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
			<panel layout="vertical" spacing="0" width="515px" xsi:type="Panel">
					<panel xsi:type="Absolute" layout="absolute" spacing="0" style="ButtonPanelContainer">
				<widget>
          <buttonPanel key="buttons">
            <appButton action="query" toggle="true">
             <panel xsi:type="Panel" layout="horizontal">
              <panel xsi:type="Absolute" layout="absolute" style="QueryButtonImage"/>
              <widget>
                <text>Query</text>
              </widget>
              </panel>
            </appButton>
 <appButton action="prev" toggle="true">
  <panel xsi:type="Panel" layout="horizontal">
              <panel xsi:type="Absolute" layout="absolute" style="PreviousButtonImage"/>
              <widget>
                <text>Previous</text>
              </widget>
              </panel>
            </appButton>
 <appButton action="next" toggle="true">
  <panel xsi:type="Panel" layout="horizontal">
              <panel xsi:type="Absolute" layout="absolute" style="NextButtonImage"/>
              <widget>
                <text>Next</text>
              </widget>
              </panel>
            </appButton>
              <panel xsi:type="Absolute" layout="absolute" style="ButtonDivider"/>
            <appButton action="add" toggle="true">
            <panel xsi:type="Panel" layout="horizontal">
              <panel xsi:type="Absolute" layout="absolute" style="AddButtonImage"/>
              <widget>
                <text>Add</text>
              </widget>
              </panel>
            </appButton>
            <appButton action="update" toggle="true">
            <panel xsi:type="Panel" layout="horizontal">
              <panel xsi:type="Absolute" layout="absolute" style="UpdateButtonImage"/>
              <widget>
                <text>Update</text>
              </widget>
              </panel>
            </appButton>

 <appButton action="delete" toggle="true">
 <panel xsi:type="Panel" layout="horizontal">
              <panel xsi:type="Absolute" layout="absolute" style="DeleteButtonImage"/>
              <widget>
                <text>Delete</text>
              </widget>
              </panel>
            </appButton>
  <panel xsi:type="Absolute" layout="absolute" style="ButtonDivider"/>
            <appButton action="commit">
            <panel xsi:type="Panel" layout="horizontal">
              <panel xsi:type="Absolute" layout="absolute" style="CommitButtonImage"/>
              <widget>
                <text>Commit</text>
              </widget>
              </panel>
            </appButton>
            <appButton action="abort">
            <panel xsi:type="Panel" layout="horizontal">
              <panel xsi:type="Absolute" layout="absolute" style="AbortButtonImage"/>
              <widget>
                <text>Abort</text>
              </widget>
              </panel>
            </appButton>
          </buttonPanel>
				</widget>
				</panel>
				<panel key="formDeck" layout="deck" xsi:type="Deck" align="left">
					<deck>
					<panel layout="vertical" width="400px" xsi:type="Panel">
							<panel key="secMod2" layout="table" style="FormBorderless" width="225px" xsi:type="Table">
								<row>
									<panel layout="horizontal" xsi:type="Panel" style="FormVerticalSpacing"/>
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"id")'/>:</text>
									</widget>
									<widget>
										<textbox key="id" width="75px" tab="category,isSingular"/>
									</widget>
								</row>
								<row>								
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"category")'/>:</text>
									</widget>
									<widget>
										<autoDropdown key="category" cat="category" case="mixed" serviceUrl="StorageUnitServlet" width="110px" dropdown="true" type="string" fromModel="true" tab="description,id">
											<autoWidths>89</autoWidths>
											<autoEditors>
												<label/>
											</autoEditors>
											<autoFields>
												<string/>
											</autoFields>
										</autoDropdown>
										<query>
										<autoDropdown cat="category" case="mixed" serviceUrl="StorageUnitServlet" width="110px" dropdown="true" type="string" fromModel="true" multiSelect="true" tab="description,id">
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
											<autoDropdown cat="isSingular" case="upper" serviceUrl="StorageUnitServlet" width="40px" dropdown="true" type="string" multiSelect="true" tab="id,description">
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