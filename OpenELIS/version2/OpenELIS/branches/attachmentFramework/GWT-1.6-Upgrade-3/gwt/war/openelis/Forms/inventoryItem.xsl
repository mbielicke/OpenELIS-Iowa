 <!--
Exhibit A - UIRF Open-source Based Public Software License.

The contents of this file are subject to the UIRF Open-source Based
Public Software License(the "License"); you may not use this file except
in compliance with the License. You may obtain a copy of the License at
openelis.uhl.uiowa.edu

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.

The Original Code is OpenELIS code.

The Initial Developer of the Original Code is The University of Iowa.
Portions created by The University of Iowa are Copyright 2006-2008. All
Rights Reserved.

Contributor(s): ______________________________________.

Alternatively, the contents of this file marked
"Separately-Licensed" may be used under the terms of a UIRF Software
license ("UIRF Software License"), in which case the provisions of a
UIRF Software License are applicable instead of those above. 
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
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.InventoryItemMetaMap"/>
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
    <xsl:variable name="parentInvItem" select="meta:getParentInventoryItem($invItem)"/>
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
		<HorizontalPanel spacing="0" padding="0">
			<!--left table goes here -->
			<CollapsePanel key="collapsePanel" style="LeftSidePanel">
			    <!--
				<azTable colwidths="110,105" height="250px" key="azTable" maxRows="22" tablewidth="auto" headers = "{resource:getString($constants,'name')},{resource:getString($constants,'store')}" width="100%" >
    				 <buttonPanel key="atozButtons">
	    			   <xsl:call-template name="aToZLeftPanelButtons"/>		
		    		 </buttonPanel>
				</azTable>
				-->
				 <resultsTable key="azTable" height="250px" width="100%">
				   	 <buttonPanel key="atozButtons">
	    			   <xsl:call-template name="aToZLeftPanelButtons"/>		
		    		 </buttonPanel>
		    		 <table maxRows="22" width="auto">
		    		   <headers><xsl:value-of select="resource:getString($constants,'name')"/>,<xsl:value-of select="resource:getString($constants,'store')"/></headers>
		    		   <widths>110,105</widths>
		    		   <editors>
		    		     <label/>
		    		     <label/>
		    		   </editors>
		    		   <fields>
		    		     <string/>
		    		     <string/>
		    		   </fields>
		    		 </table>
				</resultsTable>
			</CollapsePanel>
			<VerticalPanel spacing="0" padding="0">
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
				<VerticalPanel spacing="0" padding="0" style="WhiteContentPanel">
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
										<dropdown key="{meta:getStoreId($invItem)}" case="mixed" width="225px" tab="{meta:getCategoryId($invItem)},{meta:getDescription($invItem)}"/>
									</widget>	
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"category")'/>:</text>
									<widget colspan="3">
										<dropdown key="{meta:getCategoryId($invItem)}" case="mixed" width="180px" tab="{meta:getQuantityMinLevel($invItem)},{meta:getStoreId($invItem)}"/>
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
										<textbox key="{meta:getQuantityMaxLevel($invItem)}" width="55px" tab="{meta:getDispensedUnitsId($invItem)},{meta:getQuantityToReorder($invItem)}"/>
									</widget>	
								</row>
								<row>
									<AbsolutePanel style="VerticalSpacer"/>
								</row>		
<!--								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"purchasedUnits")'/>:</text>
									<widget colspan="3">
										<autoDropdown key="{meta:getPurchasedUnitsId($invItem)}" case="mixed" width="90px" tab="{meta:getDispensedUnitsId($invItem)},{meta:getQuantityMaxLevel($invItem)}"/>
									</widget>
								</row>
								-->
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"dispensedUnits")'/>:</text>
									<dropdown key="{meta:getDispensedUnitsId($invItem)}" case="mixed" width="90px" tab="{meta:getIsActive($invItem)},{meta:getQuantityMaxLevel($invItem)}"/>
									<!--
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"ratio")'/>:</text>
									<textbox key="ratio" width="55px" tab="??,??"/>
									-->
								</row>
							</TablePanel>
							<VerticalPanel style="subform">
			                  	<text style="FormTitle"><xsl:value-of select='resource:getString($constants,"controlsParameters")'/></text>
								<TablePanel style="Form">
									<row>
										<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"active")'/>:</text>
										<check key="{meta:getIsActive($invItem)}" tab="{meta:getIsReorderAuto($invItem)},{meta:getDispensedUnitsId($invItem)}"/>
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
								</VerticalPanel>
								</HorizontalPanel>
			 	<VerticalPanel halign="center">
				<!--TAB PANEL-->
				<TabPanel height="200px" key="itemTabPanel">
					<!-- TAB 1 (Components) -->
					<tab key="componentsTab" text="{resource:getString($constants,'components')}">
						<TablePanel spacing="0" padding="0" height="249px" width="645px">
							<row>
							<widget align="center">
								<table width="auto" key="componentsTable" maxRows="9" title="" showError="false" showScroll="ALWAYS">
										<headers><xsl:value-of select='resource:getString($constants,"component")'/>,<xsl:value-of select='resource:getString($constants,"description")'/>,
										<xsl:value-of select='resource:getString($constants,"quantity")'/></headers>
										<widths>129,335,104</widths>
										<editors>
											<autoComplete cat="component" autoCall="this" case="lower" serviceUrl="OpenELISServlet?service=org.openelis.modules.inventoryItem.server.InventoryItemService" width="100px">												
												<widths>118</widths>
											</autoComplete>
											<label/>
											<textbox case="mixed"/>
										</editors>
										<fields>
											<dropdown key="{componentMeta:getComponentId($component)}" required="true"/>
											<string key="{invItemMeta:getDescription($compInvItem)}"/>
											<double key="{componentMeta:getQuantity($component)}" required="true"/>
										</fields>
										<sorts>true,true,true</sorts>
										<filters>false,false,false</filters>
										<colAligns>left,left,left</colAligns>
									</table>
									<!--
									<query>
									<queryTable width="auto" title="" maxRows="9" showError="false" showScroll="ALWAYS">
										<headers><xsl:value-of select='resource:getString($constants,"component")'/>,<xsl:value-of select='resource:getString($constants,"description")'/>,
										<xsl:value-of select='resource:getString($constants,"quantity")'/></headers>
										<widths>129,335,104</widths>
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
									-->
								</widget>
								</row>
								<row>
									<widget align="center">
									<appButton action="removeComponentRow" onclick="this" style="Button" key="removeComponentButton">
									<HorizontalPanel>
              						<AbsolutePanel style="RemoveRowButtonImage"/>
                						<text><xsl:value-of select='resource:getString($constants,"removeRow")'/></text>
  					                </HorizontalPanel>
						            </appButton>
						            </widget>
						            </row>
							</TablePanel>
					</tab>			
					<!-- start TAB 2 (Location/Quantity) -->
					<tab key="locationTab" text="{resource:getString($constants,'locationQuantity')}">
						<TablePanel spacing="0" padding="0" height="249px" width="645px">
						<row>
							<widget align="center">
								<table width="auto" key="locQuantitiesTable" maxRows="10" title="" showError="false" showScroll="ALWAYS">
										<headers><xsl:value-of select='resource:getString($constants,"location")'/>,<xsl:value-of select='resource:getString($constants,"lotNum")'/>,
										<xsl:value-of select='resource:getString($constants,"locationNum")'/>,
										<xsl:value-of select='resource:getString($constants,"expirationDate")'/>,<xsl:value-of select='resource:getString($constants,"quantityOnHand")'/></headers>
										<widths>166,70,70,133,123</widths>
										<editors>
											<label/>
											<label/>
											<label/>
											<label/>
											<label/>
										</editors>
										<fields>
											<string key="{locationMeta:getStorageLocationId($location)}"/>
											<string key="{locationMeta:getLotNumber($location)}"/>
											<integer key="{locationMeta:getId($location)}"/>
											<string key="{locationMeta:getExpirationDate($location)}"/>
											<integer key="{locationMeta:getQuantityOnhand($location)}"/>
										</fields>
										<sorts>true,true,true,true,true</sorts>
										<filters>false,false,false,false,false</filters>
										<colAligns>left,left,left,left,left</colAligns>
									</table>
									<!--
									<query>
									<queryTable width="auto" title="" maxRows="10" showError="false" showScroll="ALWAYS">
										<headers><xsl:value-of select='resource:getString($constants,"location")'/>,<xsl:value-of select='resource:getString($constants,"lotNum")'/>,
										<xsl:value-of select='resource:getString($constants,"locationNum")'/>,
										<xsl:value-of select='resource:getString($constants,"expirationDate")'/>,<xsl:value-of select='resource:getString($constants,"quantityOnHand")'/></headers>
										<widths>166,70,70,133,123</widths>
										<editors>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
											<label/>
											<textbox case="mixed"/>	
										</editors>
										<fields>
											<xsl:value-of select='storageLocationMeta:getLocation($locStorageLoc)'/>,<xsl:value-of select='locationMeta:getLotNumber($location)'/>,
    										<xsl:value-of select='locationMeta:getId($location)'/>,<xsl:value-of select='locationMeta:getExpirationDate($location)'/>,
    										<xsl:value-of select='locationMeta:getQuantityOnhand($location)'/>
										</fields>							
									</queryTable>
									</query>
									-->
								</widget>
								</row>
							</TablePanel>
					</tab>
					<!-- start TAB 3 (Additional Info) -->
					<tab key="additionalInfoTab" text="{resource:getString($constants,'additionalInfo')}">
					<VerticalPanel height="249px" width="645px" padding="0" spacing="0">
						<TablePanel style="Form" spacing="0" padding="0">
						<row>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"productURI")'/>:</text>
							<widget colspan="5">
								<textbox case="mixed" key="{meta:getProductUri($invItem)}" width="490px" max="80" tab="{invItemMeta:getName($parentInvItem)},{meta:getParentRatio($invItem)}"/>
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
						<row>
							<text style="Prompt">Parent Item:</text>
							<widget colspan="3">
								<autoComplete key="{invItemMeta:getName($parentInvItem)}" cat="parentItem" serviceUrl="OpenELISServlet?service=org.openelis.modules.inventoryItem.server.InventoryItemService" case="mixed" width="210px" tab="??,??">
									<headers>Name,Store</headers>
									<widths>135,130</widths>
								</autoComplete>
							</widget>
							<text style="Prompt">Parent Ratio:</text>
							<textbox key="{meta:getParentRatio($invItem)}"  width="55px" max="30" tab="{meta:getProductUri($invItem)},{invItemMeta:getName($parentInvItem)}"/>
						</row>
						
						</TablePanel>
						</VerticalPanel>
					</tab>
					
					<!-- start TAB 4 (Manufacturing) -->
					<tab key="manufacturingTab" text="{resource:getString($constants,'manufacturing')}">
<<<<<<< .working
						<VerticalPanel height="247px" width="645px">
						<richtext key="manufacturingText"/>
						</VerticalPanel>
=======
					<VerticalPanel height="249px" width="645px" spacing="0" padding="0" halign="center">
					<TablePanel key="manFormPanel" style="Form" padding="0" spacing="0">
						<row>
							<widget align="center">
								<appButton action="editManufacturing" onclick="this" key="editManufacturingButton" style="Button">
									<HorizontalPanel>
              							<AbsolutePanel xsi:type="Absolute" layout="absolute" style="StandardNoteButtonImage"/>
	                					<text>Edit</text>
						             </HorizontalPanel>
						    	</appButton>
						  	</widget>
						</row>
						<row>
							<VerticalPanel overflow="auto" height="200px" width="625px" spacing="0" padding="0" halign="center">
								<html key="manufacturingText" width="615px" height="188px" showError="false"/>
							</VerticalPanel>
						</row>
						</TablePanel>
				</VerticalPanel>
>>>>>>> .merge-right.r2499
					</tab>
					<!-- start TAB 5 (Comments) -->
					<tab key="commentsTab" text="{resource:getString($constants,'comments')}">
						<VerticalPanel width="645px" height="249px" spacing="0" padding="0">
							<TablePanel key="noteFormPanel" style="Form" padding="0" spacing="0">
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"subject")'/></text>
									<textbox case="mixed" key="{noteMeta:getSubject($note)}" width="435px" max="60" showError="false"/>
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
										<textarea width="551px" height="50px" case="mixed" key="{noteMeta:getText($note)}" showError="false"/>
									</widget>
								</row> 
								<row>
									<html key="spacer" xml:space="preserve"> </html>
									<widget colspan="2">
										<HorizontalPanel style="notesPanelContainer">
								<VerticalPanel key="notesPanel" style="notesPanel" valign="top" onclick="this" height="150px" width="551px" overflowX="auto" overflowY="scroll">				
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
			</VerticalPanel>
		</HorizontalPanel>
	</display>
	<rpc key="display">
  	  <integer key="{meta:getId($invItem)}" required="false"/>
      <string key="{meta:getName($invItem)}" max="20" required="true"/>
      <string key="{meta:getDescription($invItem)}" max="60" required="false"/>
      <dropdown key="{meta:getStoreId($invItem)}" type="integer" required="true"/> 
      <dropdown key="{meta:getCategoryId($invItem)}" type="integer" required="false"/> 
      <integer key="{meta:getQuantityMinLevel($invItem)}" required="false"/>
      <integer key="{meta:getQuantityMaxLevel($invItem)}" required="false"/>
      <integer key="{meta:getQuantityToReorder($invItem)}" required="false"/>
      <dropdown key="{meta:getDispensedUnitsId($invItem)}" type="integer" required="true"/> 
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
      <integer key="{meta:getAverageLeadTime($invItem)}" required="false"/>
      <double key="{meta:getAverageCost($invItem)}" required="false"/>
      <integer key="{meta:getAverageDailyUse($invItem)}" required="false"/>
      
      <dropdown key="{invItemMeta:getName($parentInvItem)}" required="false"/>
      <integer key="{meta:getParentRatio($invItem)}" required="false"/>
      
      <rpc key="components">
	      <table key="componentsTable"/>
      </rpc>

      <rpc key="locations">
	      <table key="locQuantitiesTable"/>
      </rpc>

      <!--<rpc key="additionalInfo">
      </rpc>-->

      <rpc key="manufacturing">
      	<string key="manufacturingText" required="false"/>
      </rpc>

      <rpc key="comments">
	      <string key="{noteMeta:getSubject($note)}" max="60" required="false"/>
    	  <string key="{noteMeta:getText($note)}" required="false"/>
		  <string key="notesPanel"/>
      </rpc>

      <string key="itemTabPanel" reset="false">componentsTab</string>
	</rpc>
	<!--
	<rpc key="query">
      <queryNumber key="{meta:getId($invItem)}" type="integer" required="false"/>
      <queryString key="{meta:getName($invItem)}" max="20" required="false"/>
      <queryString key="{meta:getDescription($invItem)}" max="60" required="false"/>
      <dropdown key="{meta:getStoreId($invItem)}" type="integer" required="false"/> 
      <dropdown key="{meta:getCategoryId($invItem)}" type="integer" required="false"/> 
      <queryNumber key="{meta:getQuantityMinLevel($invItem)}" type="integer" required="false"/>
      <queryNumber key="{meta:getQuantityMaxLevel($invItem)}" type="integer" required="false"/>
      <queryNumber key="{meta:getQuantityToReorder($invItem)}" type="integer" required="false"/>
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

      <queryNumber key="{meta:getAverageLeadTime($invItem)}" type="integer" required="false"/>
      <queryNumber key="{meta:getAverageCost($invItem)}" type="double" required="false"/>
      <queryNumber key="{meta:getAverageDailyUse($invItem)}" type="integer" required="false"/>
      <queryString key="{meta:getProductUri($invItem)}" required="false"/>
      
      <dropdown key="{invItemMeta:getName($parentInvItem)}" required="false"/>
      <queryNumber key="{meta:getParentRatio($invItem)}" type="integer" required="false"/>
      
      <queryString key="{noteMeta:getSubject($note)}" max="60" required="false"/>
      
      <table key="componentsTable"/>

      <queryString key="{invItemMeta:getName($compInvItem)}" required="false"/>
	  <queryString key="{invItemMeta:getDescription($compInvItem)}" required="false"/>
	  <queryNumber key="{componentMeta:getQuantity($component)}" type="double" required="false"/>   

      <table key="locQuantitiesTable"/>	  

	  <queryString key="{storageLocationMeta:getLocation($locStorageLoc)}" required="false"/>
	  <queryString key="{locationMeta:getLotNumber($location)}" required="false"/>
	  <queryNumber key="{locationMeta:getId($location)}" type="integer" required="false"/>
	  <queryString key="{locationMeta:getExpirationDate($location)}" required="false"/>
	  <queryNumber key="{locationMeta:getQuantityOnhand($location)}" type="integer" required="false"/>
	</rpc>
	<rpc key="queryByLetter">
		<queryString key="{meta:getName($invItem)}"/>
	</rpc>
	-->
</screen>
  </xsl:template>
</xsl:stylesheet>
