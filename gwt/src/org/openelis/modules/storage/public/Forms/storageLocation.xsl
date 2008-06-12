<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                xmlns:storageLocationMeta="xalan://org.openelis.meta.StorageLocationMeta"
                xmlns:storageLocationChildMeta="xalan://org.openelis.meta.StorageLocationChildMeta"
                xmlns:storageLocationStorageUnitMeta="xalan://org.openelis.meta.StorageLocationStorageUnitMeta"
                xmlns:storageLocationChildStorageUnitMeta="xalan://org.openelis.meta.StorageLocationChildStorageUnitMeta"
                
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZOneColumn.xsl"/>

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
  <xalan:component prefix="storageLocationMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.StorageLocationMeta"/>
  </xalan:component>
  
  <xalan:component prefix="storageLocationChildMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.StorageLocationChildMeta"/>
  </xalan:component>
  
  <xalan:component prefix="storageLocationStorageUnitMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.StorageLocationStorageUnitMeta"/>
  </xalan:component>
  
  <xalan:component prefix="storageLocationChildStorageUnitMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.StorageLocationChildStorageUnitMeta"/>
  </xalan:component>

  <xsl:template match="doc"> 
      <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="Storage" name="{resource:getString($constants,'storageLocation')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<panel layout="horizontal" style="WhiteContentPanel" spacing="0" padding="0" xsi:type="Panel">
			<!--left table goes here -->
				<aToZ height="425px" width="100%" key="hideablePanel" maxRows="19" title="{resource:getString($constants,'name')}" tablewidth="auto" colwidths="175">
			         <buttonPanel key="atozButtons">
	    			   <xsl:call-template name="aToZLeftPanelButtons"/>		
		    		 </buttonPanel>
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
										<textbox case="lower" key="{storageLocationMeta:getName()}" width="150px" max="20" tab="{storageLocationMeta:getLocation()},{storageLocationMeta:getIsAvailable()}"/>
									</widget>
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"location")'/>:</text>
									</widget>
									<widget>
										<textbox case="mixed" key="{storageLocationMeta:getLocation()}" max="80" width="395px" tab="{storageLocationStorageUnitMeta:getDescription()},{storageLocationMeta:getName()}"/>
									</widget>
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"storageUnit")'/>:</text>
									</widget>
									<widget>
									<autoDropdown cat="storageUnit" key="{storageLocationStorageUnitMeta:getDescription()}" case="lower" serviceUrl="OpenELISServlet?service=org.openelis.modules.storage.server.StorageLocationService"  width="350px" tab="{storageLocationMeta:getIsAvailable()},{storageLocationMeta:getLocation()}">
										<headers>Desc,Category</headers>
										<widths>267,90</widths>	
									</autoDropdown>
										<query>
										<textbox case="upper" width="366px" tab="{storageLocationMeta:getIsAvailable()},{storageLocationMeta:getLocation()}"/>
										</query>
									</widget>	
									
								</row>
								<row>
								<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"isAvailable")'/>:</text>
									</widget>
									<widget>
										<check key="{storageLocationMeta:getIsAvailable()}" tab="{storageLocationMeta:getName()},{storageLocationStorageUnitMeta:description()}"/>
									</widget>
								</row>
								</panel>
								<panel height="10px" layout="vertical"  xsi:type="Panel"/>
								
								<panel layout="vertical" xsi:type="Panel" spacing="3">
								<widget>
							<table width="auto" key="childStorageLocsTable" manager="ChildStorageLocsTable" maxRows="11" title="" showError="false" showScroll="true">
										<headers><xsl:value-of select='resource:getString($constants,"storageUnit")'/>,<xsl:value-of select='resource:getString($constants,"location")'/>,
										<xsl:value-of select='resource:getString($constants,"isAvailable")'/></headers>
										<widths>225,275,80</widths>
										<editors>
											<autoDropdown cat="storageUnit" case="lower" serviceUrl="OpenELISServlet?service=org.openelis.modules.storage.server.StorageLocationService" width="150px">
												<headers>Desc,Category</headers>
												<widths>180,70</widths>
											</autoDropdown>
											<textbox case="mixed" max="80"/>
											<check/>
										</editors>
										<fields>
											<dropdown key="{storageLocationChildStorageUnitMeta:description()}" required="true"/>
											<string key="{storageLocationChildMeta:location()}" required="true"/>
											<check key="{storageLocationChildMeta:isAvailable()}">Y</check>
										</fields>
										<sorts>true,true,true</sorts>
										<filters>false,false ,false</filters>
										<colAligns>left,left,left</colAligns>
									</table>
									<query>
									<queryTable width="auto" maxRows="11" title="" showError="false">
										<headers><xsl:value-of select='resource:getString($constants,"storageUnit")'/>,<xsl:value-of select='resource:getString($constants,"location")'/>,
										<xsl:value-of select='resource:getString($constants,"isAvailable")'/></headers>
										<widths>225,275,98</widths>
										<editors>
											<textbox case="lower"/>
											<textbox case="mixed"/>
											<check threeState="true"/>
										</editors>
										<fields>
											<xsl:value-of select='storageLocationChildStorageUnitMeta:description()'/>,<xsl:value-of select='storageLocationChildMeta:location()'/>,
											<xsl:value-of select='storageLocationChildMeta:isAvailable()'/>
										</fields>
									</queryTable>
									</query>             
                           </widget>
		                <widget halign = "center">
                            <appButton  action="removeRow" onclick="this" key = "removeEntryButton">
                            <panel xsi:type="Panel" layout="horizontal">
              						<panel xsi:type="Absolute" layout="absolute" style="RemoveRowButtonImage"/>
                              <widget>
                                  <text><xsl:value-of select='resource:getString($constants,"removeRow")'/></text>
                               </widget> 
                               </panel>
                             </appButton>
                           </widget>	      

							</panel>
				</panel>
			</panel>
		</panel>
	</display>
	<rpc key="display">
	<number key="{storageLocationMeta:getId()}" required="false" type="integer"/>
    <string key="{storageLocationMeta:getName()}" max="20" required="true"/>
    <dropdown  key="{storageLocationStorageUnitMeta:description()}" type="integer" required="true"/>
    <string key="{storageLocationMeta:getLocation()}" max="80" required="true"/>
    <check key="{storageLocationMeta:getIsAvailable()}" required="false"/>
    <table key="childStorageLocsTable"/>
	</rpc>
	
	<rpc key="query">
	<queryNumber key="{storageLocationMeta:getId()}" type="integer" required="false"/>
    <queryString key="{storageLocationMeta:getName()}" required="false"/>
    <queryString key="{storageLocationMeta:getLocation()}" required="false"/>
    <queryString key="{storageLocationStorageUnitMeta:description()}" required="false"/>
    <queryCheck key="{storageLocationMeta:getIsAvailable()}" required="false"/>
    <table key="childStorageLocsTable"/>
    
	<!-- table values -->
	<queryString key="{storageLocationChildStorageUnitMeta:description()}" required="false"/>
	<queryString key="{storageLocationChildMeta:location()}" required="false"/>
	<queryCheck key="{storageLocationChildMeta:isAvailable()}" required="false"/>
	</rpc>
	<rpc key="queryByLetter">
		<queryString key="storageLocation.name"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>