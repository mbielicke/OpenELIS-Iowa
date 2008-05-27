<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale" 
                xmlns:inventoryItemMeta="xalan://org.openelis.meta.InventoryItemMeta" 
                xmlns:inventoryComponentMeta="xalan://org.openelis.meta.InventoryComponentMeta"
                xmlns:inventoryComponentItemMeta="xalan://org.openelis.meta.InventoryComponentItemMeta"
                xmlns:inventoryLocationMeta="xalan://org.openelis.meta.InventoryLocationMeta"
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
				<aToZ height="510px" width="100%" key="hideablePanel" maxRows="19" title="{resource:getString($constants,'name')}" tablewidth="auto" colwidths="175">
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
										<textbox case="lower" key="{inventoryItemMeta:name()}" width="150px" max="20" tab="{inventoryItemMeta:description()},{inventoryItemMeta:isNoInventory()}"/>
									</widget>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"id")'/>:</text>
									</widget>
									<widget>
										<textbox key="{inventoryItemMeta:id()}" width="75px"/>
									</widget>
									
								</row>
									<row>								
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"description")'/>:</text>
										</widget>
										<widget colspan="3">
										<textbox case="mixed" key="{inventoryItemMeta:description()}" width="340px" max="60" tab="{inventoryItemMeta:store()},{inventoryItemMeta:name()}"/>
										</widget>									
									</row>
									<row>
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"store")'/>:</text>
										</widget>
										<widget colspan="3">
											<autoDropdown key="{inventoryItemMeta:store()}" case="mixed" width="225px" tab="{inventoryItemMeta:category()},{inventoryItemMeta:description()}"/>
										</widget>	
									</row>
									<row>
									<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"category")'/>:</text>
										</widget>
										<widget colspan="3">
											<autoDropdown key="{inventoryItemMeta:category()}" case="mixed" width="180px" tab="{inventoryItemMeta:quantityMinLevel()},{inventoryItemMeta:store()}"/>
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
											<textbox key="{inventoryItemMeta:quantityMinLevel()}" width="55px" tab="{inventoryItemMeta:quantityToReorder()},{inventoryItemMeta:category()}"/>
										</widget>
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"reorderLevel")'/>:</text>
										</widget>
										<widget>
											<textbox key="{inventoryItemMeta:quantityToReorder()}" width="55px" tab="{inventoryItemMeta:quantityMaxLevel()},{inventoryItemMeta:quantityMinLevel()}"/>
										</widget>													
									</row>	
									<row>
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"maxOrderLevel")'/>:</text>
										</widget>
										<widget colspan="3">
											<textbox key="{inventoryItemMeta:quantityMaxLevel()}" width="55px" tab="{inventoryItemMeta:purchasedUnits()},{inventoryItemMeta:quantityToReorder()}"/>
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
											<autoDropdown key="{inventoryItemMeta:purchasedUnits()}" case="mixed" width="90px" tab="{inventoryItemMeta:dispensedUnits()},{inventoryItemMeta:quantityMaxLevel()}"/>
										</widget>
									</row>
									<row>
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"dispensedUnits")'/>:</text>
										</widget>
										<widget colspan="3">
											<autoDropdown key="{inventoryItemMeta:dispensedUnits()}" case="mixed" width="90px" tab="{inventoryItemMeta:isActive()},{inventoryItemMeta:purchasedUnits()}"/>
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
										<check key="{inventoryItemMeta:isActive()}" tab="{inventoryItemMeta:isReorderAuto()},{inventoryItemMeta:dispensedUnits()}"/>
									</widget>
								</row>
								<row>
									<widget>
										<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"autoReorder")'/>:</text>
									</widget>
									<widget>
										<check key="{inventoryItemMeta:isReorderAuto()}" tab="{inventoryItemMeta:isLotMaintained()},{inventoryItemMeta:isActive()}"/>
									</widget>	
								</row>
								<row>
									<widget>
										<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"maintainLot")'/>:</text>
									</widget>
									<widget>
										<check key="{inventoryItemMeta:isLotMaintained()}" tab="{inventoryItemMeta:isSerialMaintained()},{inventoryItemMeta:isReorderAuto()}"/>
									</widget>	
								</row>
								<row>
									<widget>
										<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"serialRequired")'/>:</text>
									</widget>
									<widget>
										<check key="{inventoryItemMeta:isSerialMaintained()}" tab="{inventoryItemMeta:isBulk()},{inventoryItemMeta:isLotMaintained()}"/>
									</widget>	
								</row>
								<row>
									<widget>
										<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"bulk")'/>:</text>
									</widget>
									<widget>
										<check key="{inventoryItemMeta:isBulk()}" tab="{inventoryItemMeta:isNotForSale()},{inventoryItemMeta:isSerialMaintained()}"/>
									</widget>
								</row>
								<row>
									<widget>
										<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"notForSale")'/>:</text>
									</widget>
									<widget>
										<check key="{inventoryItemMeta:isNotForSale()}" tab="{inventoryItemMeta:isSubAssembly()},{inventoryItemMeta:isBulk()}"/>
									</widget>	
								</row>
								<row>
									<widget>
										<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"subAssembly")'/>:</text>
									</widget>
									<widget>
										<check key="{inventoryItemMeta:isSubAssembly()}" tab="{inventoryItemMeta:isLabor()},{inventoryItemMeta:isNotForSale()}"/>
									</widget>
								</row>
								<row>
									<widget>
										<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"labor")'/>:</text>
									</widget>
									<widget>
										<check key="{inventoryItemMeta:isLabor()}" tab="{inventoryItemMeta:isNoInventory()},{inventoryItemMeta:isSubAssembly()}"/>
									</widget>	
								</row>
								<row>
									<widget>
										<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"doNotInventory")'/>:</text>
									</widget>
									<widget>
										<check key="{inventoryItemMeta:isNoInventory()}" tab="{inventoryItemMeta:name()},{inventoryItemMeta:isLabor()}"/>
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
											<autoDropdown cat="component" case="lower" autoParams="InventoryComponentAutoParams" serviceUrl="OpenELISServlet?service=org.openelis.modules.inventory.server.InventoryService" width="125px">												
												<widths>118</widths>
											</autoDropdown>
											<label/>
											<textbox case="upper"/>
										</editors>
										<fields>
											<dropdown key="{inventoryComponentMeta:component()}" required="true"/>
											<string key="{inventoryComponentItemMeta:description()}"/>
											<number key="{inventoryComponentMeta:quantity()}" type="double" required="true"/>
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
											<autoDropdown case="mixed" width="90px" multiSelect="true"/>
											<textbox case="upper"/>
											<textbox case="mixed"/>
										</editors>
										<fields><xsl:value-of select='inventoryComponentMeta:component()'/>,<xsl:value-of select='inventoryComponentItemMeta:description()'/>,
										<xsl:value-of select='inventoryComponentMeta:quantity()'/>
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
								<table width="auto" key="locQuantitiesTable" manager="InventoryLocationsTable" maxRows="9" title="" showError="false" showScroll="true">
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
											<string key="{inventoryLocationMeta:storageLocation()}"/>
											<string key="{inventoryLocationMeta:lotNumber()}"/>
											<string key="{inventoryLocationMeta:expirationDate()}"/>
											<number key="{inventoryLocationMeta:quantityOnhand()}" type="integer"/>
										</fields>
										<sorts>true,true,true,true</sorts>
										<filters>false,false,false,false</filters>
										<colAligns>left,left,left,left</colAligns>
									</table>
									<!--<query>
									<queryTable width="auto" title="" maxRows="9" showError="false">
										<headers><xsl:value-of select='resource:getString($constants,"location")'/>,<xsl:value-of select='resource:getString($constants,"lotNum")'/>,
										<xsl:value-of select='resource:getString($constants,"expirationDate")'/>,<xsl:value-of select='resource:getString($constants,"quantityOnHand")'/></headers>
										<widths>230,90,136,123</widths>
										<editors>
											<autoDropdown case="mixed" width="90px" popWidth="auto" multiSelect="true">
											  <widths>90</widths>
											</autoDropdown>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<textbox case="upper"/>	
										</editors>
										<fields>test1,w2,w3,e4
										</fields>							
									</queryTable>
									</query>-->
								</widget>{inventoryItemMeta:description()}

									<widget style="WhiteContentPanel" halign="center">
									<appButton action="removeLocationRow" onclick="this" style="Button" key="removeLocationButton">
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
					<!-- start TAB 3 (Additional Info) -->
					<tab key="tab4" text="{resource:getString($constants,'additionalInfo')}">
						<panel height="229px" width="610px" layout="vertical" xsi:type="Panel">
						<panel layout="table" style="Form" xsi:type="Panel">
						<row>
							<widget>
								<text style="Prompt"><xsl:value-of select='resource:getString($constants,"productURI")'/>:</text>
							</widget>
							<widget colspan="5">
								<textbox case="mixed" key="{inventoryItemMeta:productUri()}" width="490px" max="80"/>
							</widget>									
						</row>
						<row>
							<widget>
								<text style="Prompt"><xsl:value-of select='resource:getString($constants,"averageLeadTime")'/>:</text>
							</widget>
							<widget>
								<textbox key="{inventoryItemMeta:averageLeadTime()}" style="ScreenTextboxDisplayOnly" alwaysDisabled="true" width="55px" max="30"/>
							</widget>
							<widget>
								<text style="Prompt"><xsl:value-of select='resource:getString($constants,"averageCost")'/>:</text>
							</widget>
							<widget>
								<textbox key="{inventoryItemMeta:averageCost()}" style="ScreenTextboxDisplayOnly" alwaysDisabled="true" width="55px" max="30"/>
							</widget>
							<widget>
								<text style="Prompt"><xsl:value-of select='resource:getString($constants,"averageDailyUse")'/>:</text>
							</widget>
							<widget>
								<textbox key="{inventoryItemMeta:averageDailyUse()}" style="ScreenTextboxDisplayOnly" alwaysDisabled="true" width="55px" max="30"/>
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
										<textbox case="mixed" key="{inventoryItemNoteMeta:subject()}" width="435px" max="60" showError="false"/>
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
										<textarea width="549px" height="50px" case="mixed" key="{inventoryItemNoteMeta:text()}" showError="false"/>
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
  	  <number key="{inventoryItemMeta:id()}" type="integer" required="false"/>
      <string key="{inventoryItemMeta:name()}" max="20" required="true"/>
      <string key="{inventoryItemMeta:description()}" max="60" required="false"/>
      <dropdown key="{inventoryItemMeta:store()}" type="integer" required="true"/> 
      <dropdown key="{inventoryItemMeta:category()}" type="integer" required="false"/> 
      <number key="{inventoryItemMeta:quantityMinLevel()}" type="integer" required="false"/>
      <number key="{inventoryItemMeta:quantityMaxLevel()}" type="integer" required="false"/>
      <number key="{inventoryItemMeta:quantityToReorder()}" type="integer" required="false"/>
      <dropdown key="{inventoryItemMeta:purchasedUnits()}" type="integer" required="true"/> 
      <dropdown key="{inventoryItemMeta:dispensedUnits()}" type="integer" required="true"/> 
      <number key="{inventoryItemMeta:averageLeadTime()}" type="integer" required="false"/>
      <number key="{inventoryItemMeta:averageCost()}" type="double" required="false"/>
      <number key="{inventoryItemMeta:averageDailyUse()}" type="integer" required="false"/>
      <table key="componentsTable"/>
      <table key="locQuantitiesTable"/>
      <check key="{inventoryItemMeta:isActive()}" required="false"/>
      <check key="{inventoryItemMeta:isReorderAuto()}" required="false"/>
      <check key="{inventoryItemMeta:isLotMaintained()}" required="false"/>
      <check key="{inventoryItemMeta:isSerialMaintained()}" required="false"/>
      <check key="{inventoryItemMeta:isBulk()}" required="false"/>
      <check key="{inventoryItemMeta:isNotForSale()}" required="false"/>
      <check key="{inventoryItemMeta:isSubAssembly()}" required="false"/>
      <check key="{inventoryItemMeta:isLabor()}" required="false"/>
      <check key="{inventoryItemMeta:isNoInventory()}" required="false"/>
      
      <string key="{inventoryItemMeta:productUri()}" required="false"/>
      
      <string key="{inventoryItemNoteMeta:subject()}" max="60" required="false"/>
      <string key="{inventoryItemNoteMeta:text()}" required="false"/>
      
	</rpc>
	<rpc key="query">
      <queryNumber key="{inventoryItemMeta:id()}" type="integer"/>
      <string key="{inventoryItemMeta:name()}"/>
      <string key="{inventoryItemMeta:description()}"/>
      <dropdown key="{inventoryItemMeta:store()}" type="integer"/> 
      <dropdown key="{inventoryItemMeta:category()}" type="integer"/> 
      <number key="{inventoryItemMeta:quantityMinLevel()}" type="integer"/>
      <number key="{inventoryItemMeta:quantityMaxLevel()}" type="integer"/>
      <number key="{inventoryItemMeta:quantityToReorder()}" type="integer"/>
      <dropdown key="{inventoryItemMeta:purchasedUnits()}" type="integer"/> 
      <dropdown key="{inventoryItemMeta:dispensedUnits()}" type="integer"/> 
      <number key="{inventoryItemMeta:averageLeadTime()}" type="integer"/>
      <number key="{inventoryItemMeta:averageCost()}" type="double"/>
      <number key="{inventoryItemMeta:averageDailyUse()}" type="integer"/>
      <check key="{inventoryItemMeta:isActive()}"/>
      <check key="{inventoryItemMeta:isReorderAuto()}"/>
      <check key="{inventoryItemMeta:isLotMaintained()}"/>
      <check key="{inventoryItemMeta:isSerialMaintained()}"/>
      <check key="{inventoryItemMeta:isBulk()}"/>
      <check key="{inventoryItemMeta:isNotForSale()}"/>
      <check key="{inventoryItemMeta:isSubAssembly()}"/>
      <check key="{inventoryItemMeta:isLabor()}"/>
      <string key="{inventoryItemNoteMeta:subject()}"/>
      
      <!--component table values-->
      <dropdown key="{inventoryComponentMeta:component()}" required="false"/>
	  <queryString key="{inventoryComponentItemMeta:description()}" required="false"/>
	  <queryNumber key="{inventoryComponentMeta:quantity()}" type="integer" required="false"/>
											
      <!--location table values-->
      
	</rpc>
	<rpc key="queryByLetter">
		<queryString key="{inventoryItemMeta:name()}"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>