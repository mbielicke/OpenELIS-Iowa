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
		<panel layout="horizontal" style="WhiteContentPanel" spacing="0" padding="0" xsi:type="Panel">
			<!--left table goes here -->
				<aToZ height="325px" width="100%" key="hideablePanel" visible="false" maxRows="10" title="{resource:getString($constants,'name')}" tablewidth="auto" colwidths="175">
				 <xsl:if test="string($language)='en'">
			         <buttonPanel key="atozButtons">
	    			   <xsl:call-template name="aToZLeftPanelButtons"/>		
		    		 </buttonPanel>
		        </xsl:if>
				</aToZ>
			<panel layout="vertical" spacing="0" xsi:type="Panel">
				<!--button panel code-->
		<panel xsi:type="Absolute" layout="absolute" spacing="0" style="ButtonPanelContainer">
			<widget>
    			<buttonPanel key="buttons">
    			<xsl:call-template name="queryButton"/>
    			<xsl:call-template name="previousButton"/>
    			<xsl:call-template name="nextButton"/>
    			<xsl:call-template name="buttonPanelDivider"/>
    			<xsl:call-template name="addButton"/>
    			<xsl:call-template name="updateButton"/>
    			<xsl:call-template name="deleteButton"/>
    			<xsl:call-template name="buttonPanelDivider"/>
    			<xsl:call-template name="commitButton"/>
    			<xsl:call-template name="abortButton"/>
				</buttonPanel>
 			</widget>
		</panel>
		<!--end button panel-->
						
					<panel layout="vertical" xsi:type="Panel">
							<panel key="secMod2" layout="table" style="Form" xsi:type="Table">
								<row>
									<panel layout="horizontal" xsi:type="Panel" style="FormVerticalSpacing"/>
								</row>
								<row>								
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"name")'/>:</text>
									</widget>
									<widget>
										<textbox case="mixed" key="storageLocation.name" width="150px" max="20" tab="storageLocation.location,id"/>
									</widget>
								</row>
								<row>								
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"location")'/>:</text>
									</widget>
									<widget>
										<textbox case="mixed" key="storageLocation.location" max="80" width="195px" tab="parentStorageLocation.name,storageLocation.name"/>
									</widget>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"parentStorage")'/>:</text>
									</widget>
									<widget>
										<autoDropdown cat="parentStorage" key="parentStorageLocation.name" case="upper" serviceUrl="OpenELISServlet?service=org.openelis.modules.storage.server.StorageLocationService" width="150px" popWidth="150px" tab="storageLocation.storageUnit.description,storageLocation.location">
											<headers>Id,Name,Location</headers>
											<widths>50,120,150</widths>
										</autoDropdown>
										<query>
											<textbox case="upper" width="150px" tab="storageLocation.storageUnit.description,storageLocation.location"/>
										</query>
									</widget>	
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"storageUnit")'/>:</text>
									</widget>
									<widget>
									<autoDropdown cat="storageUnit" key="storageLocation.storageUnit.description" case="upper" serviceUrl="OpenELISServlet?service=org.openelis.modules.storage.server.StorageLocationService"  width="150px" popWidth="150px" tab="storageLocation.isAvailable,parentStorageLocation.name">
										<headers>Desc,Category,Singlular</headers>
										<widths>160,80,45</widths>	
									</autoDropdown>
										<query>
										<textbox case="upper" width="150px" tab="storageLocation.isAvailable,parentStorageLocation.name"/>
										</query>
									</widget>	
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"isAvailable")'/>:</text>
									</widget>
									<widget>
										<check key="storageLocation.isAvailable" tab="id,storageLocation.storageUnit.description"/>
									</widget>
								</row>
								<row>
								<panel layout="vertical" height="15px" xsi:type="Panel"/>
								</row>
								<row>
								<widget  colspan="4" halign="center">
							<table width="auto" key="childStorageLocsTable" manager="ChildStorageLocsTable" maxRows="7" title="" showError="false">
										<headers><xsl:value-of select='resource:getString($constants,"name")'/>,<xsl:value-of select='resource:getString($constants,"location")'/>,
										<xsl:value-of select='resource:getString($constants,"storageUnit")'/>,<xsl:value-of select='resource:getString($constants,"isAvailable")'/></headers>
										<widths>130,200,115,80</widths>
										<editors>
											<textbox case="mixed" max="20"/>
											<textbox case="mixed" max="80"/>
											<autoDropdown cat="storageUnit" case="upper" serviceUrl="OpenELISServlet?service=org.openelis.modules.storage.server.StorageLocationService" width="150px" popWidth="150px">
												<headers>Desc,Category,Singlular</headers>
												<widths>160,80,45</widths>
											</autoDropdown>
											<check/>
										</editors>
										<fields>
											<string/>
											<string/>
											<dropdown/>
											<string/>											
										</fields>
										<sorts>true,true,true,true</sorts>
										<filters>false,false ,false,false</filters>
										<colAligns>left,left,left,left</colAligns>
									</table>
									<query>
									<table width="auto" maxRows="7" rows="1" title="" showError="false">
										<headers><xsl:value-of select='resource:getString($constants,"name")'/>,<xsl:value-of select='resource:getString($constants,"location")'/>,
										<xsl:value-of select='resource:getString($constants,"storageUnit")'/>,<xsl:value-of select='resource:getString($constants,"isAvailable")'/></headers>
										<widths>130,200,115,80</widths>
										<editors>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="upper"/>
											<check/>
										</editors>
										<fields>
											<queryString/>
											<queryString/>
											<queryString/>
											<queryString/>	
										</fields>
										<sorts>true,true,true,true</sorts>
										<filters>false,false ,false,false</filters>
										<colAligns>left,left,left,left</colAligns>
									</table>
									</query>
							</widget>
								</row>
							</panel>
				</panel>
			</panel>
		</panel>
	</display>
	<rpc key="display">
	<number key="storageLocation.id" required="false" type="integer"/>
    <string key="storageLocation.name" max="20" required="true"/>
    <dropdown  key="storageLocation.storageUnit.description" type="integer" required="true"/> 
   <!-- <number key="sortOrderId" required="true" type="integer"/>-->
    <string key="storageLocation.location" max="80" required="true"/>
    <dropdown key="parentStorageLocation.name" type="integer" required="false"/>
    <string key="storageLocation.isAvailable" required="false"/>
    <table key="childStorageLocsTable"/>
	</rpc>
	
	<rpc key="query">
	<queryNumber key="storageLocation.id" type="integer"/>
    <queryString key="storageLocation.name"/>
    <queryString key="storageLocation.location"/>
    <queryString key="storageLocation.storageUnit.description"/>
    <queryString key="parentStorageLocation.name"/>  
    <!--<collection key="sortOrder" type="integer" required="false"/>-->
    <queryString key="storageLocation.isAvailable" required="false"/>
    <table key="childStorageLocsTable"/>
	</rpc>
	<rpc key="queryByLetter">
		<queryString key="storageLocation.name"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>