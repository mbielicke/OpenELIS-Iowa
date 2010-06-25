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
                xmlns:meta="xalan://org.openelis.metamap.InventoryReceiptMetaMap" 
                xmlns:inventoryReceiptMeta="xalan://org.openelis.meta.InventoryReceiptMeta" 
                xmlns:orderMeta="xalan://org.openelis.metamap.OrderMetaMap"
                xmlns:orderItemMeta="xalan://org.openelis.metamap.OrderItemMetaMap"
                xmlns:transReceiptLocationMeta="xalan://org.openelis.metamap.TransReceiptLocationMetaMap"
                xmlns:inventoryLocationMeta="xalan://org.openelis.metamap.InventoryLocationMetaMap"
                xmlns:organizationMeta="xalan://org.openelis.metamap.OrderOrganizationMetaMap"
                xmlns:addressMeta="xalan://org.openelis.meta.AddressMeta"
                xmlns:inventoryItemMeta="xalan://org.openelis.meta.InventoryItemMeta"
                xmlns:inventoryXUseMeta="xalan://org.openelis.metamap.InventoryXUseMetaMap"
                xmlns:storageLocationMeta="xalan://org.openelis.metamap.StorageLocationMetaMap"
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="IMPORT/aToZOneColumn.xsl"/>  
  <xsl:template match="doc"> 
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="InventoryTransfer" name="{resource:getString($constants,'inventoryTransfer')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<VerticalPanel spacing="0" padding="0">
		<!--button panel code-->
		<AbsolutePanel spacing="0" style="ButtonPanelContainer">
    			<buttonPanel key="buttons">
    			<xsl:call-template name="queryButton">
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
				</buttonPanel>
		</AbsolutePanel>
		<!--end button panel-->
			<VerticalPanel style="WhiteContentPanel" spacing="0" padding="0">
					<widget valign="top">
					<resultsTable query="true" colwidths="2" showNavPanel="false" key="receiptsTable" maxRows="18" tablewidth="auto" headers="{resource:getString($constants,'name')}" width="100%">
						<table width="auto" manager="this" maxRows="10" title="" showError="false" showScroll="ALWAYS">
							<headers>From Item,From Loc,On Hand,To Item,Ext,To Loc,Qty</headers>
							<widths>140,160,65,140,40,160,50</widths>										
							<editors>
								<autoComplete key="{inventoryItemMeta:getName($fromInvItem)}" cat="inventoryItemTrans" case="lower" serviceUrl="OpenELISServlet?service=org.openelis.modules.inventoryReceipt.server.InventoryReceiptService" width="120px">												
									<headers>Name,Store,Location,Qty</headers>
									<widths>135,110,160,30</widths>
								</autoComplete>
								<label cellKey="fromLoc"/>
								<label cellKey="qtyOnHand"/>
								<autoComplete cellKey="{inventoryItemMeta:getName($invItem)}" cat="toInventoryItemTrans" case="lower" autoCall="this" serviceUrl="OpenELISServlet?service=org.openelis.modules.inventoryReceipt.server.InventoryReceiptService" width="120px">												
									<headers>Name,Store</headers>
									<widths>135,110</widths>
								</autoComplete>
								<check cellKey="addToExisting"/>
								<autoComplete cellKey="{storageLocationMeta:getName($toStorageLoc)}" cat="location" autoCall="this" serviceUrl="OpenELISServlet?service=org.openelis.modules.inventoryReceipt.server.InventoryReceiptService" case="mixed" width="160px">
									<headers>Desc</headers>
									<widths>300</widths>
								</autoComplete>
								<textbox cellKey="{inventoryReceiptMeta:getQuantityReceived($receipt)}" case="mixed"/>
							</editors>
							<sorts>false,false,false,false,false,false,false</sorts>
							<filters>false,false,false,false,false,false,false</filters>
							<colAligns>left,left,left,left,left,left,left</colAligns>
						</table>
					</resultsTable>

						</widget>
						<widget style="WhiteContentPanel" halign="center">									
							<appButton action="removeRow" onclick="this" style="Button" key="removeReceiptButton">
								<HorizontalPanel>
              						<AbsolutePanel style="RemoveRowButtonImage"/>
                						<text><xsl:value-of select='resource:getString($constants,"removeRow")'/></text>
							              </HorizontalPanel>
						            </appButton>
						            </widget>
					<VerticalPanel style="subform">
		                <text style="FormTitle"><xsl:value-of select='resource:getString($constants,"itemInformation")'/></text>
							<TablePanel style="Form">
								<row>
									<HorizontalPanel/>
									<text style="TopPrompt"><xsl:value-of select='resource:getString($constants,"description")'/></text>
									<text style="TopPrompt"><xsl:value-of select='resource:getString($constants,"store")'/></text>
									<text style="TopPrompt"><xsl:value-of select='resource:getString($constants,"dispensedUnits")'/></text>
									<text style="TopPrompt"><xsl:value-of select='resource:getString($constants,"lotNum")'/></text>
									<text style="TopPrompt"><xsl:value-of select='resource:getString($constants,"expDate")'/></text>
								</row>
								<row>
									<text style="Prompt">From:</text>
									<widget>
										<textbox case="mixed" key="{inventoryItemMeta:getDescription($invItem)}" width="195px" max="30" enabledStates="" style="ScreenTextboxDisplayOnly"/>
									</widget>
									<widget>
										<textbox case="mixed" key="{inventoryItemMeta:getStoreId($invItem)}" width="115px" max="30" enabledStates="" style="ScreenTextboxDisplayOnly"/>
									</widget>
									<widget>
										<textbox case="mixed" key="{inventoryItemMeta:getDispensedUnitsId($invItem)}" width="90px" max="30" enabledStates="" style="ScreenTextboxDisplayOnly"/>
									</widget>
									<widget>
										<textbox case="mixed" key="{inventoryLocationMeta:getLotNumber($loc)}" onchange="this" width="100px" max="30" enabledStates="" style="ScreenTextboxDisplayOnly"/>
									</widget>
									<widget>
										<textbox case="mixed" key="{inventoryLocationMeta:getExpirationDate($loc)}" width="140px" onChange="this" enabledStates="" style="ScreenTextboxDisplayOnly"/>
									</widget>
								</row>
								<row>
									<text style="Prompt">To:</text>
									<widget>
										<textbox case="mixed" key="toDescription" width="195px" max="30" enabledStates="" style="ScreenTextboxDisplayOnly"/>
									</widget>
									<widget>
										<textbox case="mixed" key="toStoreId" width="115px" max="30" enabledStates="" style="ScreenTextboxDisplayOnly"/>
									</widget>
									<widget>
										<textbox case="mixed" key="toDispensedUnits" width="90px" max="30" enabledStates="" style="ScreenTextboxDisplayOnly"/>
									</widget>
									<widget>
										<textbox case="mixed" key="toLotNumber" onchange="this" width="100px" max="30" enabledStates="" style="ScreenTextboxDisplayOnly"/>
									</widget>
									<widget>
										<textbox case="mixed" key="toExpDate" width="140px" enabledStates="" style="ScreenTextboxDisplayOnly"/>
									</widget>
								</row>
							</TablePanel>
						</VerticalPanel>
								</VerticalPanel>
				</VerticalPanel>
	</display>
	<rpc key="display">
		<table key="receiptsTable">
			<dropdown key="{inventoryItemMeta:getName($fromInvItem)}" required="true"/>
			<string key="fromLoc"/>
			<integer key="qtyOnHand"/>
			<dropdown key="{inventoryItemMeta:getName($invItem)}" required="true"/>
			<check key="addToExisting"/>
			<dropdown key="{storageLocationMeta:getName($toStorageLoc)}" required="true"/>
			<integer key="{inventoryReceiptMeta:getQuantityReceived($receipt)}" required="true"/>
		</table>
    	<string key="type" reset="false">transfer</string>
    	
    	<rpc key="itemInformation">
    	<string key="{addressMeta:getMultipleUnit($address)}" required="false"/>
    	<string key="{addressMeta:getStreetAddress($address)}" required="false"/>
    	<string key="{addressMeta:getCity($address)}" required="false"/>
    	<string key="{addressMeta:getState($address)}" required="false"/>
    	<string key="{addressMeta:getZipCode($address)}" required="false"/>
    	
    	<string key="{inventoryItemMeta:getDescription($invItem)}" required="false"/>
    	<string key="{inventoryItemMeta:getStoreId($invItem)}" required="false"/>
    	<string key="{inventoryItemMeta:getDispensedUnitsId($invItem)}" required="false"/>
    	
    	<check key="addToExisting" required="false"/>
    	<dropdown key="{inventoryLocationMeta:getStorageLocationId($loc)}" required="false"/>
		<string key="{inventoryLocationMeta:getLotNumber($loc)}" required="false"/>
		<date key="{inventoryLocationMeta:getExpirationDate($loc)}" begin="0" end="2" required="false"/>
		
		<string key="toDescription" required="false"/>
		<string key="toStoreId" required="false"/>
		<string key="toDispensedUnits" required="false"/>
		<string key="toLotNumber" required="false"/>
		<string key="toExpDate" required="false"/>
    	</rpc>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>
