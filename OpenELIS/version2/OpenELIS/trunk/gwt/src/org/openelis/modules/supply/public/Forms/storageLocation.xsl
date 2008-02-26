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
		<panel layout="horizontal" style="WhiteContentPanel" spacing="0" padding="0" xsi:type="Panel">
			<!--left table goes here -->
				<aToZ height="325px" width="100%" key="hideablePanel" visible="false">
				 <panel layout="horizontal" style="ScreenLeftPanel" xsi:type="Panel" spacing="0">
				 <xsl:if test="string($language)='en'">
			<xsl:call-template name="aToZLeftPanelButtons"/>
		</xsl:if>
		
				<table manager="StorageNameTable" width="auto" style="ScreenLeftTable" key="storageLocsTable" maxRows="10" title="{resource:getString($constants,'name')}">
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
					<panel layout="vertical" xsi:type="Panel">
							<panel key="secMod2" layout="table" style="FormBorderless" xsi:type="Table">
								<row>
									<panel layout="horizontal" xsi:type="Panel" style="FormVerticalSpacing"/>
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"id")'/>:</text>
									</widget>
									<widget>
										<textbox key="id" width="75px" tab="name,isAvailable"/>
									</widget>
								</row>
								<row>								
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"name")'/>:</text>
									</widget>
									<widget>
										<textbox case="mixed" key="name" width="150px" max="20" tab="location,id"/>
									</widget>
								</row>
								<row>								
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"location")'/>:</text>
									</widget>
									<widget>
										<textbox case="mixed" key="location" max="80" width="195px" tab="parentStorage,name"/>
									</widget>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"parentStorage")'/>:</text>
									</widget>
									<widget>
									<auto cat="parentStorageLoc" case="upper" serviceUrl="StorageLocationServlet" key="parentStorage" width="150px" type="integer" tab="storageUnit,location">
										<autoHeaders>Id,Name,Location</autoHeaders>
										<autoWidths>50,120,150</autoWidths>
										<autoEditors>
											<label/>
											<label/>
											<label/>
										</autoEditors>
										<autoFields>
											<string/>
											<string/>
											<string/>
										</autoFields>
										</auto>
										<query>
										<textbox case="upper" width="150px" tab="storageUnit,location"/>
										</query>
									</widget>	
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"storageUnit")'/>:</text>
									</widget>
									<widget>
									<auto cat="storageUnit" case="upper" serviceUrl="StorageLocationServlet" key="storageUnit" width="150px" type="integer" tab="isAvailable,parentStorage">
										<autoHeaders>Id,Desc,Category,Singlular</autoHeaders>
										<autoWidths>50,160,80,45</autoWidths>
										<autoEditors>
											<label/>
											<label/>
											<label/>
											<label/>
										</autoEditors>
										<autoFields>
											<string/>
											<string/>
											<string/>
											<string/>
										</autoFields>
										</auto>
										<query>
										<textbox case="upper" width="150px" tab="isAvailable,parentStorage"/>
										</query>
									</widget>	
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"isAvailable")'/>:</text>
									</widget>
									<widget>
										<check key="isAvailable" tab="id,storageUnit"/>
										<query>
											<autoDropdown cat="isAvailable" case="upper" serviceUrl="StorageLocationServlet" width="40px" dropdown="true" type="string" multiSelect="true" tab="id,storageUnit">
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
								<row>
								<panel layout="vertical" height="15px" xsi:type="Panel"/>
								</row>
								<row>
								<widget  colspan="4" halign="center">
							<table width="auto" key="childStorageLocsTable" manager="ChildStorageLocsTable" maxRows="7" title="">
										<headers><xsl:value-of select='resource:getString($constants,"name")'/>,<xsl:value-of select='resource:getString($constants,"location")'/>,
										<xsl:value-of select='resource:getString($constants,"storageUnit")'/>,<xsl:value-of select='resource:getString($constants,"isAvailable")'/></headers>
										<widths>130,200,115,80</widths>
										<editors>
											<textbox case="mixed" max="20"/>
											<textbox case="mixed" max="80"/>
											<auto cat="storageUnit" case="upper" serviceUrl="StorageLocationServlet" width="150px" type="integer">
										<autoHeaders>Id,Desc,Category,Singlular</autoHeaders>
										<autoWidths>50,160,80,45</autoWidths>
										<autoEditors>
											<label/>
											<label/>
											<label/>
											<label/>
										</autoEditors>
										<autoFields>
											<string/>
											<string/>
											<string/>
											<string/>
										</autoFields>
										</auto>
											<check/>
										</editors>
										<fields>
											<string/>
											<string/>
											<number type="integer">0</number>
											<check/>											
										</fields>
										<sorts>true,true,true,true</sorts>
										<filters>false,false ,false,false</filters>
										<colAligns>left,left,left,left</colAligns>
									</table>
									<query>
									<table width="auto" maxRows="7" rows="1" title="">
										<headers><xsl:value-of select='resource:getString($constants,"name")'/>,<xsl:value-of select='resource:getString($constants,"location")'/>,
										<xsl:value-of select='resource:getString($constants,"storageUnit")'/>,<xsl:value-of select='resource:getString($constants,"isAvailable")'/></headers>
										<widths>130,200,115,80</widths>
										<editors>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="upper"/>
											<autoDropdown cat="isAvailable" case="upper" serviceUrl="StorageLocationServlet" width="64px" type="string" multiSelect="true">
													<autoWidths>64</autoWidths>
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
										</editors>
										<fields>
											<queryString/>
											<queryString/>
											<queryString/>
											<collection type="string"/>		
										</fields>
										<sorts>true,true,true,true</sorts>
										<filters>false,false ,false,false</filters>
										<colAligns>left,left,left,left</colAligns>
									</table>
									</query>
							</widget>
								</row>
							</panel>
							<!--<panel layout="vertical" height="15px" xsi:type="Panel"/>
						<panel layout="vertical" width="100%" xsi:type="Panel">
							
							</panel>-->
				</panel>
			</panel>
		</panel>
	</display>
	<rpc key="display">
	<number key="id" required="false" type="integer"/>
    <string key="name" max="20" required="true"/>
    <number key="storageUnitId" type="integer" required="false"/> 
   <!-- <number key="sortOrderId" required="true" type="integer"/>-->
    <string key="location" max="80" required="true"/>
    <number key="parentStorageId" type="integer" required="false"/>
    <check key="isAvailable" required="false"/>
    <table key="childStorageLocsTable"/>
	</rpc>
	
	<rpc key="query">
	<queryNumber key="id" type="integer"/>
    <queryString key="name"/>
    <queryString key="location"/>
    <queryString key="storageUnit"/>
    <queryString key="parentStorage"/>  
    <!--<collection key="sortOrder" type="integer" required="false"/>-->
    <collection key="isAvailable" type="string" required="false"/>
    <table key="childStorageLocsTable"/>
	</rpc>
	<rpc key="queryByLetter">
		<queryString key="name"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>