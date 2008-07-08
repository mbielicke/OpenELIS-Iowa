<!--
 The contents of this file are subject to the Mozilla Public License
 Version 1.1 (the "License"); you may not use this file except in
 compliance with the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/

 Software distributed under the License is distributed on an "AS IS"
 basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 License for the specific language governing rights and limitations under
 the License.
 
 The Original Code is OpenELIS code.
 
 Copyright (C) OpenELIS.  All Rights Reserved.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale" 
                xmlns:meta="xalan://org.openelis.metamap.InventoryItemMetaMap"
                xmlns:noteMeta="xalan://org.openelis.meta.NoteMeta"
                xmlns:locationMeta="xalan://org.openelis.metamap.InventoryLocationMetaMap"
                xmlns:componentMeta="xalan://org.openelis.metamap.InventoryComponentMetaMap"
                xmlns:storageLocationMeta="xalan://org.openelis.meta.StorageLocationMeta"                
                xmlns:invItemMeta="xalan://org.openelis.meta.InventoryItemMeta"                
                extension-element-prefixes="resource"
                version="1.0">
                
<xsl:import href="aToZOneColumn.xsl"/>

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
  <xalan:component prefix="meta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryItemMetaMap"/>
  </xalan:component>

  <xalan:component prefix="noteMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.NoteMeta"/>
  </xalan:component>
  
  <xalan:component prefix="locationMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryLocationMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="componentMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryComponentMetaMap"/>
  </xalan:component>

  <xalan:component prefix="storageLocationMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.StorageLocationMeta"/>
  </xalan:component>
  
  <xalan:component prefix="invItemMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryItemMeta"/>
  </xalan:component>
  
  <xsl:template match="doc"> 
    <xsl:variable name="invItem" select="meta:new()"/>
    <xsl:variable name="component" select="meta:getInventoryComponent($invItem)"/>
    <xsl:variable name="location" select="meta:getInventoryLocation($invItem)"/>
    <xsl:variable name="note" select="meta:getNote($invItem)"/>
    <xsl:variable name="compInvItem" select="componentMeta:getInventoryItem($component)"/>
    <xsl:variable name="locStorageLoc" select="locationMeta:getStorageLocation($location)"/>
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="Inventory" name="{resource:getString($constants,'inventoryItem')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<HorizontalPanel spacing="0" padding="0" style="WhiteContentPanel">
			<!--left table goes here -->
			<CollapsePanel key="collapsePanel">
				<azTable width="auto" key="azTable" maxRows="24" title="{resource:getString($constants,'name')}" tablewidth="auto" colwidths="175">
    				 <buttonPanel key="atozButtons">
	    			   <xsl:call-template name="aToZLeftPanelButtons"/>		
		    		 </buttonPanel>
				</azTable>
			</CollapsePanel>
			<VerticalPanel spacing="0">
		<!--button panel code-->
		<AbsolutePanel spacing="0" style="ButtonPanelContainer">
			<widget>
    			<buttonPanel key="buttons">
    			<xsl:call-template name="queryButton">
    				<xsl:with-param name="language">
    				    <xsl:value-of select="language"/>
    				</xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="previousButton">
    			    <xsl:with-param name="language">
    			        <xsl:value-of select="language"/>
    			    </xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="nextButton">
    			    <xsl:with-param name="language">
    			        <xsl:value-of select="language"/>
    			    </xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="buttonPanelDivider"/>
    			<xsl:call-template name="addButton">
                    <xsl:with-param name="language">
                        <xsl:value-of select="language"/>
                    </xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="updateButton">
    			    <xsl:with-param name="language">
    			        <xsl:value-of select="language"/>
    			    </xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="buttonPanelDivider"/>
    			<xsl:call-template name="commitButton">
    			    <xsl:with-param name="language">
    			        <xsl:value-of select="language"/>
    			    </xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="abortButton">
    			    <xsl:with-param name="language">
    			        <xsl:value-of select="language"/>
    			    </xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="buttonPanelDivider"/>
    			<xsl:call-template name="optionsButton">
    			    <xsl:with-param name="language">
    			        <xsl:value-of select="language"/>
    			    </xsl:with-param>
    			</xsl:call-template>
				</buttonPanel>
 			</widget>
		</AbsolutePanel>
		<!--end button panel-->
		
					<VerticalPanel>
						<HorizontalPanel>
							<TablePanel style="Form">
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"name")'/>:</text>
									<textbox case="lower" key="{meta:getName($invItem)}" width="150px" max="20" tab="{meta:getDescription($invItem)},{meta:getIsNoInventory($invItem)}"/>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"id")'/>:</text>
						            <textbox key="{meta:getId($invItem)}" width="75px"/>								
								</row>
								<row>								
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"description")'/>:</text>
									<widget colspan="3">
										<textbox case="mixed" key="{meta:getDescription($invItem)}" width="340px" max="60" tab="{meta:getStoreId($invItem)},{meta:getName($invItem)}"/>
										</widget>									
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"store")'/>:</text>
									<widget colspan="3">
										<autoDropdown key="{meta:getStoreId($invItem)}" case="mixed" width="225px" tab="{meta:getCategoryId($invItem)},{meta:getDescription($invItem)}"/>
									</widget>	
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"category")'/>:</text>
									<widget colspan="3">
										<autoDropdown key="{meta:getCategoryId($invItem)}" case="mixed" width="180px" tab="{meta:getQuantityMinLevel($invItem)},{meta:getStoreId($invItem)}"/>
									</widget>	
								</row>
								<row>
									<AbsolutePanel style="VerticalSpacer"/>
								</row>		
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"minOrderLevel")'/>:</text>
									<textbox key="{meta:getQuantityMinLevel($invItem)}" width="55px" tab="{meta:getQuantityToReorder($invItem)},{meta:getCategoryId($invItem)}"/>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"reorderLevel")'/>:</text>
									<textbox key="{meta:getQuantityToReorder($invItem)}" width="55px" tab="{meta:getQuantityMaxLevel($invItem)},{meta:getQuantityMinLevel($invItem)}"/>
								</row>	
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"maxOrderLevel")'/>:</text>
									<widget colspan="3">
										<textbox key="{meta:getQuantityMaxLevel($invItem)}" width="55px" tab="{meta:getPurchasedUnitsId($invItem)},{meta:getQuantityToReorder($invItem)}"/>
									</widget>	
								</row>
								<row>
									<AbsolutePanel style="VerticalSpacer"/>
								</row>		
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"purchasedUnits")'/>:</text>
									<widget colspan="3">
										<autoDropdown key="{meta:getPurchasedUnitsId($invItem)}" case="mixed" width="90px" tab="{meta:getDispensedUnitsId($invItem)},{meta:getQuantityMaxLevel($invItem)}"/>
									</widget>
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"dispensedUnits")'/>:</text>
									<widget colspan="3">
										<autoDropdown key="{meta:getDispensedUnitsId($invItem)}" case="mixed" width="90px" tab="{meta:getIsActive($invItem)},{meta:getPurchasedUnitsId($invItem)}"/>
									</widget>
								</row>
							</TablePanel>
							<VerticalPanel style="Form">
								<titledPanel key="borderedPanel">
								<legend><text style="LegendTitle"><xsl:value-of select='resource:getString($constants,"controlsParameters")'/></text></legend>
								<content><TablePanel style="Form">
								<row>
									<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"active")'/>:</text>
									<check key="{meta:getIsActive($invItem)}" tab="{meta:getIsReorderAuto($invItem)},{meta:getPurchasedUnitsId($invItem)}"/>
								</row>
								<row>
									<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"autoReorder")'/>:</text>
									<check key="{meta:getIsReorderAuto($invItem)}" tab="{meta:getIsLotMaintained($invItem)},{meta:getIsActive($invItem)}"/>
								</row>
								<row>
									<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"maintainLot")'/>:</text>
									<check key="{meta:getIsLotMaintained($invItem)}" tab="{meta:getIsSerialMaintained($invItem)},{meta:getIsReorderAuto($invItem)}"/>
								</row>
								<row>
									<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"serialRequired")'/>:</text>
									<check key="{meta:getIsSerialMaintained($invItem)}" tab="{meta:getIsBulk($invItem)},{meta:getIsLotMaintained($invItem)}"/>
								</row>
								<row>
									<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"bulk")'/>:</text>
									<check key="{meta:getIsBulk($invItem)}" tab="{meta:getIsNotForSale($invItem)},{meta:getIsSerialMaintained($invItem)}"/>
								</row>
								<row>
									<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"notForSale")'/>:</text>
									<check key="{meta:getIsNotForSale($invItem)}" tab="{meta:getIsSubAssembly($invItem)},{meta:getIsBulk($invItem)}"/>
								</row>
								<row>
									<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"subAssembly")'/>:</text>
									<check key="{meta:getIsSubAssembly($invItem)}" tab="{meta:getIsLabor($invItem)},{meta:getIsNotForSale($invItem)}"/>
								</row>
								<row>
									<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"labor")'/>:</text>
									<check key="{meta:getIsLabor($invItem)}" tab="{meta:getIsNoInventory($invItem)},{meta:getIsSubAssembly($invItem)}"/>
								</row>
								<row>
									<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"doNotInventory")'/>:</text>
									<check key="{meta:getIsNoInventory($invItem)}" tab="{meta:getName($invItem)},{meta:getIsLabor($invItem)}"/>
								</row>
								</TablePanel>
								</content>
								</titledPanel>
								</VerticalPanel>
								</HorizontalPanel>
				<!--TAB PANEL-->
				<TabPanel height="200px" key="tabPanel">
					<!-- TAB 1 (Components) -->
					<tab key="tab1" text="{resource:getString($constants,'components')}">
							<VerticalPanel spacing="0" padding="0" overflow="hidden">
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
											<dropdown key="{componentMeta:getComponentId($component)}" required="true"/>
											<string key="{invItemMeta:getDescription($compInvItem)}"/>
											<number key="{componentMeta:getQuantity($component)}" type="double" required="true"/>
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
											<textbox case="lower"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
										</editors>
										<fields><xsl:value-of select='invItemMeta:getName($compInvItem)'/>,<xsl:value-of select='invItemMeta:getDescription($compInvItem)'/>,
										<xsl:value-of select='componentMeta:getQuantity($component)'/>
										</fields>										
									</queryTable>
									</query>
								</widget>
									<widget style="WhiteContentPanel" halign="center">
									<appButton action="removeComponentRow" onclick="this" style="Button" key="removeComponentButton">
									<HorizontalPanel>
              						<AbsolutePanel style="RemoveRowButtonImage"/>
                						<text><xsl:value-of select='resource:getString($constants,"removeRow")'/></text>
  					                </HorizontalPanel>
						            </appButton>
						            </widget>
							</VerticalPanel>
					</tab>			
					<!-- start TAB 2 (Location/Quantity) -->
					<tab key="tab2" text="{resource:getString($constants,'locationQuantity')}">
						<VerticalPanel spacing="0" padding="0" overflow="hidden">
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
											<string key="{locationMeta:getStorageLocationId($location)}"/>
											<string key="{locationMeta:getLotNumber($location)}"/>
											<string key="{locationMeta:getExpirationDate($location)}"/>
											<number key="{locationMeta:getQuantityOnhand($location)}" type="integer"/>
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
											<xsl:value-of select='storageLocationMeta:getLocation($locStorageLoc)'/>,<xsl:value-of select='locationMeta:getLotNumber($location)'/>,
    										<xsl:value-of select='locationMeta:getExpirationDate($location)'/>,<xsl:value-of select='locationMeta:getQuantityOnhand($location)'/>
										</fields>							
									</queryTable>
									</query>
								</widget>
								<HorizontalPanel height="10px"/>
							</VerticalPanel>
					</tab>
					<!-- start TAB 3 (Additional Info) -->
					<tab key="tab4" text="{resource:getString($constants,'additionalInfo')}">
						<VerticalPanel height="229px" width="610px">
						<TablePanel style="Form">
						<row>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"productURI")'/>:</text>
							<widget colspan="5">
								<textbox case="mixed" key="{meta:getProductUri($invItem)}" width="490px" max="80"/>
							</widget>									
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"averageLeadTime")'/>:</text>
							<widget>
								<textbox key="{meta:getAverageLeadTime($invItem)}" style="ScreenTextboxDisplayOnly" alwaysDisabled="true" width="55px" max="30"/>
							</widget>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"averageCost")'/>:</text>
							<widget>
								<textbox key="{meta:getAverageCost($invItem)}" style="ScreenTextboxDisplayOnly" alwaysDisabled="true" width="55px" max="30"/>
							</widget>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"averageDailyUse")'/>:</text>
							<widget>
								<textbox key="{meta:getAverageDailyUse($invItem)}" style="ScreenTextboxDisplayOnly" alwaysDisabled="true" width="55px" max="30"/>
							</widget>
						</row>
						</TablePanel>
						</VerticalPanel>
					</tab>
					
					<!-- start TAB 4 (Manufacturing) -->
					<tab key="tab4" text="{resource:getString($constants,'manufacturing')}">
						<VerticalPanel height="229px" width="610px">
						<richtext key="manufacturingText"/>"
						</VerticalPanel>
					</tab>
					<!-- start TAB 5 (Comments) -->
					<tab key="tab5" text="{resource:getString($constants,'comments')}">
						<VerticalPanel width="100%" height="164px" spacing="0" padding="0">
							<TablePanel key="noteFormPanel" style="Form" padding="0" spacing="0">
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"subject")'/></text>
									<textbox case="mixed" key="{noteMeta:getSubject($note)}" width="429px" max="60" showError="false"/>
									<appButton action="standardNote" onclick="this" key="standardNoteButton" style="Button">
										<HorizontalPanel>
              							<AbsolutePanel style="StandardNoteButtonImage"/>
						              <widget>
                						<text><xsl:value-of select='resource:getString($constants,"standardNote")'/></text>
							              </widget>
							              </HorizontalPanel>
						            </appButton>
 								</row>
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"note")'/></text>
									<widget colspan="2">
										<textarea width="545px" height="50px" case="mixed" key="{noteMeta:getText($note)}" showError="false"/>
									</widget>
								</row> 
								<row>
									<html key="spacer" xml:space="preserve"> </html>
									<widget colspan="2">
										<HorizontalPanel style="notesPanelContainer">
								<VerticalPanel key="notesPanel" style="NotesPanel" valign="top" onclick="this" height="136px" width="545px" overflowX="auto" overflowY="scroll">				
								</VerticalPanel>
								</HorizontalPanel>
								</widget>
							</row>
						</TablePanel>
						</VerticalPanel>
					</tab>					
				</TabPanel>
				</VerticalPanel>
			</VerticalPanel>
		</HorizontalPanel>
	</display>
	<rpc key="display">
  	  <number key="{meta:getId($invItem)}" type="integer" required="false"/>
      <string key="{meta:getName($invItem)}" max="20" required="true"/>
      <string key="{meta:getDescription($invItem)}" max="60" required="false"/>
      <dropdown key="{meta:getStoreId($invItem)}" type="integer" required="true"/> 
      <dropdown key="{meta:getCategoryId($invItem)}" type="integer" required="false"/> 
      <number key="{meta:getQuantityMinLevel($invItem)}" type="integer" required="false"/>
      <number key="{meta:getQuantityMaxLevel($invItem)}" type="integer" required="false"/>
      <number key="{meta:getQuantityToReorder($invItem)}" type="integer" required="false"/>
      <dropdown key="{meta:getPurchasedUnitsId($invItem)}" type="integer" required="true"/> 
      <dropdown key="{meta:getDispensedUnitsId($invItem)}" type="integer" required="true"/> 
      <number key="{meta:getAverageLeadTime($invItem)}" type="integer" required="false"/>
      <number key="{meta:getAverageCost($invItem)}" type="double" required="false"/>
      <number key="{meta:getAverageDailyUse($invItem)}" type="integer" required="false"/>
      <table key="componentsTable"/>
      <table key="locQuantitiesTable"/>
      <check key="{meta:getIsActive($invItem)}" required="false"/>
      <check key="{meta:getIsReorderAuto($invItem)}" required="false"/>
      <check key="{meta:getIsLotMaintained($invItem)}" required="false"/>
      <check key="{meta:getIsSerialMaintained($invItem)}" required="false"/>
      <check key="{meta:getIsBulk($invItem)}" required="false"/>
      <check key="{meta:getIsNotForSale($invItem)}" required="false"/>
      <check key="{meta:getIsSubAssembly($invItem)}" required="false"/>
      <check key="{meta:getIsLabor($invItem)}" required="false"/>
      <check key="{meta:getIsNoInventory($invItem)}" required="false"/>
      
      <string key="{meta:getProductUri($invItem)}" required="false"/>
      
      <string key="{noteMeta:getSubject($note)}" max="60" required="false"/>
      <string key="{noteMeta:getText($note)}" required="false"/>
      
	</rpc>
	<rpc key="query">
      <queryNumber key="{meta:getId($invItem)}" type="integer" required="false"/>
      <queryString key="{meta:getName($invItem)}" max="20" required="false"/>
      <queryString key="{meta:getDescription($invItem)}" max="60" required="false"/>
      <dropdown key="{meta:getStoreId($invItem)}" type="integer" required="false"/> 
      <dropdown key="{meta:getCategoryId($invItem)}" type="integer" required="false"/> 
      <queryNumber key="{meta:getQuantityMinLevel($invItem)}" type="integer" required="false"/>
      <queryNumber key="{meta:getQuantityMaxLevel($invItem)}" type="integer" required="false"/>
      <queryNumber key="{meta:getQuantityToReorder($invItem)}" type="integer" required="false"/>
      <dropdown key="{meta:getPurchasedUnitsId($invItem)}" type="integer" required="false"/> 
      <dropdown key="{meta:getDispensedUnitsId($invItem)}" type="integer" required="false"/> 
      <queryCheck key="{meta:getIsActive($invItem)}" required="false"/>
      <queryCheck key="{meta:getIsReorderAuto($invItem)}" required="false"/>
      <queryCheck key="{meta:getIsLotMaintained($invItem)}" required="false"/>
      <queryCheck key="{meta:getIsSerialMaintained($invItem)}" required="false"/>
      <queryCheck key="{meta:getIsBulk($invItem)}" required="false"/>
      <queryCheck key="{meta:getIsNotForSale($invItem)}" required="false"/>
      <queryCheck key="{meta:getIsSubAssembly($invItem)}" required="false"/>
      <queryCheck key="{meta:getIsLabor($invItem)}" required="false"/>
      <queryCheck key="{meta:getIsNoInventory($invItem)}" required="false"/>

      <!--Additional info tab-->
      <queryNumber key="{meta:getAverageLeadTime($invItem)}" type="integer" required="false"/>
      <queryNumber key="{meta:getAverageCost($invItem)}" type="double" required="false"/>
      <queryNumber key="{meta:getAverageDailyUse($invItem)}" type="integer" required="false"/>
      <queryString key="{meta:getProductUri($invItem)}" required="false"/>
      
      <!--comments tab-->
      <queryString key="{noteMeta:getSubject($note)}" max="60" required="false"/>
      
      <table key="componentsTable"/>
      <!--component table values-->
      <queryString key="{invItemMeta:getName($compInvItem)}" required="false"/>
	  <queryString key="{invItemMeta:getDescription($compInvItem)}" required="false"/>
	  <queryNumber key="{componentMeta:getQuantity($component)}" type="double" required="false"/>   

      <table key="locQuantitiesTable"/>	  
	  <!--location table values-->
	  <queryString key="{storageLocationMeta:getLocation($locStorageLoc)}" required="false"/>
	  <queryString key="{locationMeta:getLotNumber($location)}" required="false"/>
	  <queryString key="{locationMeta:getExpirationDate($location)}" required="false"/>
	  <queryNumber key="{locationMeta:getQuantityOnhand($location)}" type="integer" required="false"/>
	</rpc>
	<rpc key="queryByLetter">
		<queryString key="{meta:getName($invItem)}"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>