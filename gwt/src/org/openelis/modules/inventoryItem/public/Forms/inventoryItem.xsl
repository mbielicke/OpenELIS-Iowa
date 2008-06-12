<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale" 
                xmlns:inventoryItemMeta="xalan://org.openelis.meta.InventoryItemMeta" 
                xmlns:inventoryComponentMeta="xalan://org.openelis.meta.InventoryComponentMeta"
                xmlns:inventoryComponentItemMeta="xalan://org.openelis.meta.InventoryComponentItemMeta"
                xmlns:inventoryLocationMeta="xalan://org.openelis.meta.InventoryLocationMeta"
                xmlns:inventoryLocationStorageLocationMeta="xalan://org.openelis.meta.InventoryLocationStorageLocationMeta"
                xmlns:inventoryItemNoteMeta="xalan://org.openelis.meta.InventoryItemNoteMeta"
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZOneColumn.xsl"/>

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
  <xalan:component prefix="inventoryItemMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryItemMeta"/>
  </xalan:component>

  <xalan:component prefix="inventoryComponentMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryComponentMeta"/>
  </xalan:component>
  
  <xalan:component prefix="inventoryComponentItemMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryComponentItemMeta"/>
  </xalan:component>
  
  <xalan:component prefix="inventoryLocationMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryLocationMeta"/>
  </xalan:component>

  <xalan:component prefix="inventoryLocationStorageLocationMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryLocationStorageLocationMeta"/>
  </xalan:component>
  
  <xalan:component prefix="inventoryItemNoteMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryItemNoteMeta"/>
  </xalan:component>
  
  <xsl:template match="doc"> 
      <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="Inventory" name="{resource:getString($constants,'inventoryItem')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<panel layout="horizontal" style="WhiteContentPanel" spacing="0" padding="0" xsi:type="Panel">
			<!--left table goes here -->
				<aToZ height="510px" width="100%" key="hideablePanel" maxRows="24" title="{resource:getString($constants,'name')}" tablewidth="auto" colwidths="175">
    				 <buttonPanel key="atozButtons">
	    			   <xsl:call-template name="aToZLeftPanelButtons"/>		
		    		 </buttonPanel>
				</aToZ>
			<panel layout="vertical" spacing="0" xsi:type="Panel">
		<!--button panel code-->
		<panel xsi:type="Absolute" layout="absolute" spacing="0" style="ButtonPanelContainer">
			<widget>
    			<buttonPanel key="buttons">
    			<xsl:call-template name="queryButton">
    				<xsl:with-param name="language"><xsl:value-of select="language"/></xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="previousButton">
    			<xsl:with-param name="language"><xsl:value-of select="language"/></xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="nextButton">
    			<xsl:with-param name="language"><xsl:value-of select="language"/></xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="buttonPanelDivider"/>
    			<xsl:call-template name="addButton">
    			<xsl:with-param name="language"><xsl:value-of select="language"/></xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="updateButton">
    			<xsl:with-param name="language"><xsl:value-of select="language"/></xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="buttonPanelDivider"/>
    			<xsl:call-template name="commitButton">
    			<xsl:with-param name="language"><xsl:value-of select="language"/></xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="abortButton">
    			<xsl:with-param name="language"><xsl:value-of select="language"/></xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="buttonPanelDivider"/>
    			<xsl:call-template name="optionsButton">
    			<xsl:with-param name="language"><xsl:value-of select="language"/></xsl:with-param>
    			</xsl:call-template>
				</buttonPanel>
 			</widget>
		</panel>
		<!--end button panel-->
		
					<panel layout="vertical" xsi:type="Panel">
						<panel key="a" layout="horizontal" xsi:type="Panel">
							<panel key="secMod2" layout="table" style="Form" xsi:type="Table">
								<row>
									<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"name")'/>:</text>
									</widget>
									<widget>
										<textbox case="lower" key="{inventoryItemMeta:getName()}" width="150px" max="20" tab="{inventoryItemMeta:getDescription()},{inventoryItemMeta:getIsNoInventory()}"/>
									</widget>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"id")'/>:</text>
									</widget>
									<widget>
										<textbox key="{inventoryItemMeta:getId()}" width="75px"/>
									</widget>
									
								</row>
									<row>								
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"description")'/>:</text>
										</widget>
										<widget colspan="3">
										<textbox case="mixed" key="{inventoryItemMeta:getDescription()}" width="340px" max="60" tab="{inventoryItemMeta:getStoreId()},{inventoryItemMeta:getName()}"/>
										</widget>									
									</row>
									<row>
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"store")'/>:</text>
										</widget>
										<widget colspan="3">
											<autoDropdown key="{inventoryItemMeta:getStoreId()}" case="mixed" width="225px" tab="{inventoryItemMeta:getCategoryId()},{inventoryItemMeta:getDescription()}"/>
										</widget>	
									</row>
									<row>
									<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"category")'/>:</text>
										</widget>
										<widget colspan="3">
											<autoDropdown key="{inventoryItemMeta:getCategoryId()}" case="mixed" width="180px" tab="{inventoryItemMeta:getQuantityMinLevel()},{inventoryItemMeta:getStoreId()}"/>
										</widget>	
									</row>
									<row>
										<widget>
											<panel xsi:type="Absolute" layout="absolute" style="VerticalSpacer"/>
										</widget>
									</row>		
									<row>
									<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"minOrderLevel")'/>:</text>
									</widget>
										
										<widget>
											<textbox key="{inventoryItemMeta:getQuantityMinLevel()}" width="55px" tab="{inventoryItemMeta:getQuantityToReorder()},{inventoryItemMeta:getCategoryId()}"/>
										</widget>
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"reorderLevel")'/>:</text>
										</widget>
										<widget>
											<textbox key="{inventoryItemMeta:getQuantityToReorder()}" width="55px" tab="{inventoryItemMeta:getQuantityMaxLevel()},{inventoryItemMeta:getQuantityMinLevel()}"/>
										</widget>													
									</row>	
									<row>
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"maxOrderLevel")'/>:</text>
										</widget>
										<widget colspan="3">
											<textbox key="{inventoryItemMeta:getQuantityMaxLevel()}" width="55px" tab="{inventoryItemMeta:getPurchasedUnitsId()},{inventoryItemMeta:getQuantityToReorder()}"/>
										</widget>	
									</row>
									<row>
										<widget>
											<panel xsi:type="Absolute" layout="absolute" style="VerticalSpacer"/>
										</widget>
									</row>		
									<row>
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"purchasedUnits")'/>:</text>
										</widget>
										<widget colspan="3">
											<autoDropdown key="{inventoryItemMeta:getPurchasedUnitsId()}" case="mixed" width="90px" tab="{inventoryItemMeta:getDispensedUnitsId()},{inventoryItemMeta:getQuantityMaxLevel()}"/>
										</widget>
									</row>
									<row>
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"dispensedUnits")'/>:</text>
										</widget>
										<widget colspan="3">
											<autoDropdown key="{inventoryItemMeta:getDispensedUnitsId()}" case="mixed" width="90px" tab="{inventoryItemMeta:getIsActive()},{inventoryItemMeta:getPurchasedUnitsId()}"/>
										</widget>
									</row>
								</panel>
								<panel layout="vertical" style="Form" xsi:type="Panel">
								<titledPanel key="borderedPanel">
								<legend><text style="LegendTitle"><xsl:value-of select='resource:getString($constants,"controlsParameters")'/></text></legend>
								<content><panel layout="table" style="Form" xsi:type="Table">
								<row>
									<widget>
										<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"active")'/>:</text>
									</widget>
									<widget>
										<check key="{inventoryItemMeta:getIsActive()}" tab="{inventoryItemMeta:getIsReorderAuto()},{inventoryItemMeta:getPurchasedUnitsId()}"/>
									</widget>
								</row>
								<row>
									<widget>
										<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"autoReorder")'/>:</text>
									</widget>
									<widget>
										<check key="{inventoryItemMeta:getIsReorderAuto()}" tab="{inventoryItemMeta:getIsLotMaintained()},{inventoryItemMeta:getIsActive()}"/>
									</widget>	
								</row>
								<row>
									<widget>
										<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"maintainLot")'/>:</text>
									</widget>
									<widget>
										<check key="{inventoryItemMeta:getIsLotMaintained()}" tab="{inventoryItemMeta:getIsSerialMaintained()},{inventoryItemMeta:getIsReorderAuto()}"/>
									</widget>	
								</row>
								<row>
									<widget>
										<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"serialRequired")'/>:</text>
									</widget>
									<widget>
										<check key="{inventoryItemMeta:getIsSerialMaintained()}" tab="{inventoryItemMeta:getIsBulk()},{inventoryItemMeta:getIsLotMaintained()}"/>
									</widget>	
								</row>
								<row>
									<widget>
										<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"bulk")'/>:</text>
									</widget>
									<widget>
										<check key="{inventoryItemMeta:getIsBulk()}" tab="{inventoryItemMeta:getIsNotForSale()},{inventoryItemMeta:getIsSerialMaintained()}"/>
									</widget>
								</row>
								<row>
									<widget>
										<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"notForSale")'/>:</text>
									</widget>
									<widget>
										<check key="{inventoryItemMeta:getIsNotForSale()}" tab="{inventoryItemMeta:getIsSubAssembly()},{inventoryItemMeta:getIsBulk()}"/>
									</widget>	
								</row>
								<row>
									<widget>
										<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"subAssembly")'/>:</text>
									</widget>
									<widget>
										<check key="{inventoryItemMeta:getIsSubAssembly()}" tab="{inventoryItemMeta:getIsLabor()},{inventoryItemMeta:getIsNotForSale()}"/>
									</widget>
								</row>
								<row>
									<widget>
										<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"labor")'/>:</text>
									</widget>
									<widget>
										<check key="{inventoryItemMeta:getIsLabor()}" tab="{inventoryItemMeta:getIsNoInventory()},{inventoryItemMeta:getIsSubAssembly()}"/>
									</widget>	
								</row>
								<row>
									<widget>
										<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"doNotInventory")'/>:</text>
									</widget>
									<widget>
										<check key="{inventoryItemMeta:getIsNoInventory()}" tab="{inventoryItemMeta:getName()},{inventoryItemMeta:getIsLabor()}"/>
									</widget>	
								</row>
								</panel>
								</content>
								</titledPanel>
								</panel>
								</panel>
<!-- tabbed panel needs to go here -->
				<panel height="200px" key="tabPanel" halign="center" layout="tab" xsi:type="Tab">
					<!-- TAB 1 (Components) -->
					<tab key="tab1" text="{resource:getString($constants,'components')}">
							<panel layout="vertical" spacing="0" padding="0" xsi:type="Panel" overflow="hidden">
							<widget valign="top">
								<table width="auto" key="componentsTable" manager="InventoryComponentsTable" maxRows="9" title="" showError="false" showScroll="true">
										<headers><xsl:value-of select='resource:getString($constants,"component")'/>,<xsl:value-of select='resource:getString($constants,"description")'/>,
										<xsl:value-of select='resource:getString($constants,"quantity")'/></headers>
										<widths>125,335,104</widths>
										<editors>
											<autoDropdown cat="component" case="lower" autoParams="InventoryComponentAutoParams" serviceUrl="OpenELISServlet?service=org.openelis.modules.inventoryItem.server.InventoryItemService" width="100px">												
												<widths>118</widths>
											</autoDropdown>
											<label/>
											<textbox case="mixed"/>
										</editors>
										<fields>
											<dropdown key="{inventoryComponentMeta:getComponentId()}" required="true"/>
											<string key="{inventoryComponentItemMeta:getDescription()}"/>
											<number key="{inventoryComponentMeta:getQuantity()}" type="double" required="true"/>
										</fields>
										<sorts>true,true,true</sorts>
										<filters>false,false,false</filters>
										<colAligns>left,left,left</colAligns>
									</table>
									<query>
									<queryTable width="auto" title="" maxRows="9" showError="false">
										<headers><xsl:value-of select='resource:getString($constants,"component")'/>,<xsl:value-of select='resource:getString($constants,"description")'/>,
										<xsl:value-of select='resource:getString($constants,"quantity")'/></headers>
										<widths>125,353,104</widths>
										<editors>
											<autoDropdown cat="queryComponent" case="lower" serviceUrl="OpenELISServlet?service=org.openelis.modules.inventoryItem.server.InventoryItemService" width="125px">												
												<widths>118</widths>
											</autoDropdown>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
										</editors>
										<fields><xsl:value-of select='inventoryComponentMeta:getComponentId()'/>,<xsl:value-of select='inventoryComponentItemMeta:getDescription()'/>,
										<xsl:value-of select='inventoryComponentMeta:getQuantity()'/>
										</fields>										
									</queryTable>
									</query>
								</widget>
									<widget style="WhiteContentPanel" halign="center">
									<appButton action="removeComponentRow" onclick="this" style="Button" key="removeComponentButton">
									<panel xsi:type="Panel" layout="horizontal">
              						<panel xsi:type="Absolute" layout="absolute" style="RemoveRowButtonImage"/>
						              <widget>
                						<text><xsl:value-of select='resource:getString($constants,"removeRow")'/></text>
							              </widget>
							              </panel>
						            </appButton>
						            </widget>
							</panel>
					</tab>			
					<!-- start TAB 2 (Location/Quantity) -->
					<tab key="tab2" text="{resource:getString($constants,'locationQuantity')}">
						<panel layout="vertical" spacing="0" padding="0" xsi:type="Panel" overflow="hidden">
							<widget valign="top">
								<table width="auto" key="locQuantitiesTable" manager="InventoryLocationsTable" maxRows="10" title="" showError="false" showScroll="true">
										<headers><xsl:value-of select='resource:getString($constants,"location")'/>,<xsl:value-of select='resource:getString($constants,"lotNum")'/>,
										<xsl:value-of select='resource:getString($constants,"expirationDate")'/>,<xsl:value-of select='resource:getString($constants,"quantityOnHand")'/></headers>
										<widths>212,90,136,123</widths>
										<editors>
											<label/>
											<label/>
											<label/>
											<label/>
										</editors>
										<fields>
											<string key="{inventoryLocationMeta:getStorageLocationId()}"/>
											<string key="{inventoryLocationMeta:getLotNumber()}"/>
											<string key="{inventoryLocationMeta:getExpirationDate()}"/>
											<number key="{inventoryLocationMeta:getQuantityOnhand()}" type="integer"/>
										</fields>
										<sorts>true,true,true,true</sorts>
										<filters>false,false,false,false</filters>
										<colAligns>left,left,left,left</colAligns>
									</table>
									<query>
									<queryTable width="auto" title="" maxRows="10" showError="false">
										<headers><xsl:value-of select='resource:getString($constants,"location")'/>,<xsl:value-of select='resource:getString($constants,"lotNum")'/>,
										<xsl:value-of select='resource:getString($constants,"expirationDate")'/>,<xsl:value-of select='resource:getString($constants,"quantityOnHand")'/></headers>
										<widths>212,108,136,123</widths>
										<editors>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
											<label/>
											<textbox case="mixed"/>	
										</editors>
										<fields>
											<xsl:value-of select='inventoryLocationStorageLocationMeta:getLocation()'/>,<xsl:value-of select='inventoryLocationMeta:getLotNumber()'/>,
    										<xsl:value-of select='inventoryLocationMeta:getExpirationDate()'/>,<xsl:value-of select='inventoryLocationMeta:getQuantityOnhand()'/>
										</fields>							
									</queryTable>
									</query>
								</widget>
								<widget>
									<panel xsi:type="Panel" layout="horizontal" height="10px"/>
    				            </widget>
							</panel>
					</tab>
					<!-- start TAB 3 (Additional Info) -->
					<tab key="tab4" text="{resource:getString($constants,'additionalInfo')}">
						<panel height="229px" width="610px" layout="vertical" xsi:type="Panel">
						<panel layout="table" style="Form" xsi:type="Panel">
						<row>
							<widget>
								<text style="Prompt"><xsl:value-of select='resource:getString($constants,"productURI")'/>:</text>
							</widget>
							<widget colspan="5">
								<textbox case="mixed" key="{inventoryItemMeta:getProductUri()}" width="490px" max="80"/>
							</widget>									
						</row>
						<row>
							<widget>
								<text style="Prompt"><xsl:value-of select='resource:getString($constants,"averageLeadTime")'/>:</text>
							</widget>
							<widget>
								<textbox key="{inventoryItemMeta:getAverageLeadTime()}" style="ScreenTextboxDisplayOnly" alwaysDisabled="true" width="55px" max="30"/>
							</widget>
							<widget>
								<text style="Prompt"><xsl:value-of select='resource:getString($constants,"averageCost")'/>:</text>
							</widget>
							<widget>
								<textbox key="{inventoryItemMeta:getAverageCost()}" style="ScreenTextboxDisplayOnly" alwaysDisabled="true" width="55px" max="30"/>
							</widget>
							<widget>
								<text style="Prompt"><xsl:value-of select='resource:getString($constants,"averageDailyUse")'/>:</text>
							</widget>
							<widget>
								<textbox key="{inventoryItemMeta:getAverageDailyUse()}" style="ScreenTextboxDisplayOnly" alwaysDisabled="true" width="55px" max="30"/>
							</widget>
						</row>
						</panel>
						</panel>
					</tab>
					
					<!-- start TAB 4 (Manufacturing) -->
					<tab key="tab4" text="{resource:getString($constants,'manufacturing')}">
						<panel height="229px" width="610px" layout="vertical" xsi:type="Panel"/>
					</tab>
					<!-- start TAB 5 (Comments) -->
					<tab key="tab5" text="{resource:getString($constants,'comments')}">
						<panel key="secMod3" layout="vertical" width="100%" height="164px" spacing="0" padding="0" xsi:type="Panel">
							<panel key="noteFormPanel" layout="table" style="Form" xsi:type="Table" padding="0" spacing="0">
										<row>
										<widget>
												<text style="Prompt"><xsl:value-of select='resource:getString($constants,"subject")'/></text>
										</widget>
										<widget>
										<textbox case="mixed" key="{inventoryItemNoteMeta:getSubject()}" width="435px" max="60" showError="false"/>
										</widget>
										<widget>
										<appButton action="standardNote" onclick="this" key="standardNoteButton" style="Button">
										<panel xsi:type="Panel" layout="horizontal">
              							<panel xsi:type="Absolute" layout="absolute" style="StandardNoteButtonImage"/>
						              <widget>
                						<text><xsl:value-of select='resource:getString($constants,"standardNote")'/></text>
							              </widget>
							              </panel>
						            </appButton>
						            </widget>
										</row>
										<row>
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"note")'/></text>
										</widget>locsController
										<widget colspan="2">
										<textarea width="549px" height="50px" case="mixed" key="{inventoryItemNoteMeta:getText()}" showError="false"/>
										</widget>
										</row>
								 
							<row>
								<widget>
								<html key="spacer" xml:space="preserve"> </html>
								</widget>
								<widget colspan="2">
								<panel style="notesPanelContainer" layout="horizontal" xsi:type="Panel">
								<panel key="notesPanel" style="NotesPanel" valign="top" onclick="this" height="138px" width="549px" layout="vertical" overflowX="auto" overflowY="scroll" xsi:type="Panel">
								
								</panel>
								</panel>
								</widget>
							</row>
						</panel>
						</panel>
					</tab>
					
				</panel>
				</panel>
			</panel>
		</panel>
	</display>
	<rpc key="display">
  	  <number key="{inventoryItemMeta:getId()}" type="integer" required="false"/>
      <string key="{inventoryItemMeta:getName()}" max="20" required="true"/>
      <string key="{inventoryItemMeta:getDescription()}" max="60" required="false"/>
      <dropdown key="{inventoryItemMeta:getStoreId()}" type="integer" required="true"/> 
      <dropdown key="{inventoryItemMeta:getCategoryId()}" type="integer" required="false"/> 
      <number key="{inventoryItemMeta:getQuantityMinLevel()}" type="integer" required="false"/>
      <number key="{inventoryItemMeta:getQuantityMaxLevel()}" type="integer" required="false"/>
      <number key="{inventoryItemMeta:getQuantityToReorder()}" type="integer" required="false"/>
      <dropdown key="{inventoryItemMeta:getPurchasedUnitsId()}" type="integer" required="true"/> 
      <dropdown key="{inventoryItemMeta:getDispensedUnitsId()}" type="integer" required="true"/> 
      <number key="{inventoryItemMeta:getAverageLeadTime()}" type="integer" required="false"/>
      <number key="{inventoryItemMeta:getAverageCost()}" type="double" required="false"/>
      <number key="{inventoryItemMeta:getAverageDailyUse()}" type="integer" required="false"/>
      <table key="componentsTable"/>
      <table key="locQuantitiesTable"/>
      <check key="{inventoryItemMeta:getIsActive()}" required="false"/>
      <check key="{inventoryItemMeta:getIsReorderAuto()}" required="false"/>
      <check key="{inventoryItemMeta:getIsLotMaintained()}" required="false"/>
      <check key="{inventoryItemMeta:getIsSerialMaintained()}" required="false"/>
      <check key="{inventoryItemMeta:getIsBulk()}" required="false"/>
      <check key="{inventoryItemMeta:getIsNotForSale()}" required="false"/>
      <check key="{inventoryItemMeta:getIsSubAssembly()}" required="false"/>
      <check key="{inventoryItemMeta:getIsLabor()}" required="false"/>
      <check key="{inventoryItemMeta:getIsNoInventory()}" required="false"/>
      
      <string key="{inventoryItemMeta:getProductUri()}" required="false"/>
      
      <string key="{inventoryItemNoteMeta:getSubject()}" max="60" required="false"/>
      <string key="{inventoryItemNoteMeta:getText()}" required="false"/>
      
	</rpc>
	<rpc key="query">
      <queryNumber key="{inventoryItemMeta:getId()}" type="integer" required="false"/>
      <queryString key="{inventoryItemMeta:getName()}" max="20" required="false"/>
      <queryString key="{inventoryItemMeta:getDescription()}" max="60" required="false"/>
      <dropdown key="{inventoryItemMeta:getStoreId()}" type="integer" required="false"/> 
      <dropdown key="{inventoryItemMeta:getCategoryId()}" type="integer" required="false"/> 
      <queryNumber key="{inventoryItemMeta:getQuantityMinLevel()}" type="integer" required="false"/>
      <queryNumber key="{inventoryItemMeta:getQuantityMaxLevel()}" type="integer" required="false"/>
      <queryNumber key="{inventoryItemMeta:getQuantityToReorder()}" type="integer" required="false"/>
      <dropdown key="{inventoryItemMeta:getPurchasedUnitsId()}" type="integer" required="false"/> 
      <dropdown key="{inventoryItemMeta:getDispensedUnitsId()}" type="integer" required="false"/> 
      <queryCheck key="{inventoryItemMeta:getIsActive()}" required="false"/>
      <queryCheck key="{inventoryItemMeta:getIsReorderAuto()}" required="false"/>
      <queryCheck key="{inventoryItemMeta:getIsLotMaintained()}" required="false"/>
      <queryCheck key="{inventoryItemMeta:getIsSerialMaintained()}" required="false"/>
      <queryCheck key="{inventoryItemMeta:getIsBulk()}" required="false"/>
      <queryCheck key="{inventoryItemMeta:getIsNotForSale()}" required="false"/>
      <queryCheck key="{inventoryItemMeta:getIsSubAssembly()}" required="false"/>
      <queryCheck key="{inventoryItemMeta:getIsLabor()}" required="false"/>
      <queryCheck key="{inventoryItemMeta:getIsNoInventory()}" required="false"/>

      <!--Additional info tab-->
      <queryNumber key="{inventoryItemMeta:getAverageLeadTime()}" type="integer" required="false"/>
      <queryNumber key="{inventoryItemMeta:getAverageCost()}" type="double" required="false"/>
      <queryNumber key="{inventoryItemMeta:getAverageDailyUse()}" type="integer" required="false"/>
      <queryString key="{inventoryItemMeta:getProductUri()}" required="false"/>
      
      <!--comments tab-->
      <queryString key="{inventoryItemNoteMeta:getSubject()}" max="60" required="false"/>
      
      <table key="componentsTable"/>
      <!--component table values-->
      <dropdown key="{inventoryComponentMeta:getComponentId()}" required="false"/>
	  <queryString key="{inventoryComponentItemMeta:getDescription()}" required="false"/>
	  <queryNumber key="{inventoryComponentMeta:getQuantity()}" type="double" required="false"/>   

      <table key="locQuantitiesTable"/>	  
	  <!--location table values-->
	  <queryString key="{inventoryLocationStorageLocationMeta:getLocation()}" required="false"/>
	  <queryString key="{inventoryLocationMeta:getLotNumber()}" required="false"/>
	  <queryString key="{inventoryLocationMeta:getExpirationDate()}" required="false"/>
	  <queryNumber key="{inventoryLocationMeta:getQuantityOnhand()}" type="integer" required="false"/>
	</rpc>
	<rpc key="queryByLetter">
		<queryString key="{inventoryItemMeta:getName()}"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>