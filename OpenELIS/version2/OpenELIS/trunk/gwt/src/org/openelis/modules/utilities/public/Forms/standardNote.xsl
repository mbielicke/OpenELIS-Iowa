<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.server.constants.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                extension-element-prefixes="resource"
                version="1.0">
               <xsl:import href="aToZTwoColumns.xsl"/> 
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
				<aToZ height="290px" width="100%" key="hideablePanel" visible="false">
				 <panel layout="horizontal" style="ScreenLeftPanel" xsi:type="Panel" spacing="0">
				 <xsl:if test="string($language)='en'">
			<xsl:call-template name="aToZLeftPanelButtons"/>
		</xsl:if>
				<table manager="StandardNoteNameTable" width="auto" style="ScreenLeftTable" key="StandardNoteTable" maxRows="10" title="">
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
										<textbox key="id" width="75px" tab="name,text"/>
									</widget>
								</row>
								<row>								
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"name")'/>:</text>
									</widget>
									<widget>
										<textbox case="mixed" key="name" width="155px" max="20" tab="description,id"/>
									</widget>
								</row>
								<row>								
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"description")'/>:</text>
									</widget>
									<widget>
										<textbox case="mixed" key="description" width="300px" max="60" tab="type,name"/>
									</widget>
								</row>
								<row>								
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"type")'/>:</text>
									</widget>
									<widget>
										<autoDropdown key="type" cat="type" case="mixed" serviceUrl="StandardNoteServlet" width="121px" dropdown="true" type="integer" fromModel="true" tab="text,description">
											<autoWidths>100</autoWidths>
											<autoEditors>
												<label/>
											</autoEditors>
											<autoFields>
												<string/>
											</autoFields>
										</autoDropdown>
										<query>
										<autoDropdown cat="type" case="mixed" serviceUrl="StandardNoteServlet" width="121px" dropdown="true" type="integer" fromModel="true" multiSelect="true" tab="text,description">
											<autoWidths>100</autoWidths>
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
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"text")'/>:</text>
									</widget>
									<widget>
										<textarea key="text" width="300px" height="180px" tab="id,type"/>
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
  	<string key="name" required="true" max="20"/>
  	<string key="description" required="true" max="60"/>
  	<number key="typeId" type="integer" required="true"/>
  	<string key="text" required="true"/>
	</rpc>
	<rpc key="query">
 	<queryNumber key="id" type="integer" required="false"/>
 	<queryString key="name" type="string" required="false"/>
  	<queryString key="description" required="true"/>
  	<collection key="type" type="integer" required="false"/>
	<queryString key="text" required="false"/>
	</rpc>
	<rpc key="queryByLetter">
		<queryString key="name"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>