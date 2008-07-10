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
 
 Copyright (C) The University of Iowa.  All Rights Reserved.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale" 
                xmlns:inventoryReceiptMeta="xalan://org.openelis.metamap.InventoryReceiptMetaMap" 
                xmlns:orderMeta="xalan://org.openelis.metamap.OrderMetaMap"
                xmlns:orderItemMeta="xalan://org.openelis.metamap.OrderItemMetaMap"
                xmlns:inventoryLocationMeta="xalan://org.openelis.meta.InventoryLocationMeta"
                xmlns:organizationMeta="xalan://org.openelis.metamap.OrderOrganizationMetaMap"
                xmlns:addressMeta="xalan://org.openelis.meta.AddressMeta"
                xmlns:inventoryItemMeta="xalan://org.openelis.meta.InventoryItemMeta"
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZOneColumn.xsl"/>

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
  <xalan:component prefix="inventoryReceiptMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryReceiptMetaMap"/>
  </xalan:component>

  <xalan:component prefix="orderMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrderMetaMap"/>
  </xalan:component>

  <xalan:component prefix="orderItemMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrderItemMetaMap"/>
  </xalan:component>
    
  <xalan:component prefix="inventoryLocationMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryLocationMeta"/>
  </xalan:component>
  
    <xalan:component prefix="organizationMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrderOrganizationMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="addressMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.AddressMeta"/>
  </xalan:component>
  
  <xalan:component prefix="inventoryItemMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryItemMeta"/>
  </xalan:component>
  
  <xsl:template match="doc"> 
    <xsl:variable name="receipt" select="inventoryReceiptMeta:new()"/>
    <xsl:variable name="loc" select="inventoryReceiptMeta:getInventoryLocation($receipt)"/>
    <xsl:variable name="order" select="inventoryReceiptMeta:getOrder($receipt)"/>
    <xsl:variable name="orderItem" select="orderMeta:getOrderItem($order)"/>
    <xsl:variable name="invItem" select="orderItemMeta:getInventoryItem($orderItem)"/>
    <xsl:variable name="org" select="orderMeta:getOrderOrganization($order)"/>
    <xsl:variable name="address" select="organizationMeta:getAddress($org)"/>
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="InventoryReceipt" name="Inventory Receipt" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<HorizontalPanel style="WhiteContentPanel" spacing="0" padding="0">
			<VerticalPanel spacing="0">
		<!--button panel code-->
		<AbsolutePanel spacing="0" style="ButtonPanelContainer">
    			<buttonPanel key="buttons">
    			<xsl:call-template name="queryButton">
    				<xsl:with-param name="language">
    					<xsl:value-of select="language"/>
    				</xsl:with-param>
    			</xsl:call-template>
    			<!--<xsl:call-template name="previousButton">
	    			<xsl:with-param name="language">
	    				<xsl:value-of select="language"/>
	    			</xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="nextButton">
    				<xsl:with-param name="language">
    					<xsl:value-of select="language"/>
    				</xsl:with-param>
    			</xsl:call-template>-->
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
				</buttonPanel>
		</AbsolutePanel>
		<!--end button panel-->
			<VerticalPanel>
				<VerticalPanel spacing="0" padding="0" overflow="hidden">
					<widget valign="top">
						<table width="auto" key="receiptsTable" manager="this" maxRows="10" title="" showError="false" showScroll="true">
							<headers><xsl:value-of select='resource:getString($constants,"orderNum")'/>,<xsl:value-of select='resource:getString($constants,"dateRec")'/>,<xsl:value-of select='resource:getString($constants,"upc")'/>,
							<xsl:value-of select='resource:getString($constants,"inventoryItem")'/>,<xsl:value-of select='resource:getString($constants,"organization")'/>,<xsl:value-of select='resource:getString($constants,"qty")'/>,
							<xsl:value-of select='resource:getString($constants,"cost")'/>,<xsl:value-of select='resource:getString($constants,"QC")'/>,<xsl:value-of select='resource:getString($constants,"extReference")'/></headers>
							<widths>60,70,85,140,155,35,50,80,95</widths>										
							<editors>
								<textbox case="mixed"/>
								<calendar begin="0" end="2"/>							
								<textbox case="mixed"/>
								<autoDropdown cat="inventoryItem" case="lower" serviceUrl="OpenELISServlet?service=org.openelis.modules.inventoryReceipt.server.InventoryReceiptService" width="120px">												
									<headers>Name,Store</headers>
									<widths>100,150</widths>
								</autoDropdown>
								<autoDropdown cat="organization" case="upper" serviceUrl="OpenELISServlet?service=org.openelis.modules.inventoryReceipt.server.InventoryReceiptService" width="140px">
									<headers>Name,Street,City,St</headers>
									<widths>180,110,100,20</widths>
								</autoDropdown>
								<textbox case="mixed"/>
								<textbox case="mixed"/>
								<textbox case="mixed"/>
								<textbox case="mixed"/>
							</editors>
							<fields>
								<number key="{orderMeta:getId($order)}" type="integer" required="false"/>
								<date key="{inventoryReceiptMeta:getReceivedDate($receipt)}" begin="0" end="2" required="true"/>
								<string key="{inventoryReceiptMeta:getUpc($receipt)}" required="false"/>
								<dropdown key="{orderItemMeta:getInventoryItemId($orderItem)}" required="true"/>
								<dropdown key="{orderMeta:getOrganizationId($order)}" required="true"/>
								<number key="{inventoryReceiptMeta:getQuantityReceived($receipt)}" type="integer" required="true"/>
								<number key="{inventoryReceiptMeta:getUnitCost($receipt)}" type="double" required="true"/>
								<string key="{inventoryReceiptMeta:getQcReference($receipt)}" required="false"/>
								<string key="{inventoryReceiptMeta:getExternalReference($receipt)}" required="false"/>
							</fields>
							<sorts>true,true,true,true,true,true,true,true,true</sorts>
							<filters>false,false,false,false,false,false,false,false,false</filters>
							<colAligns>left,left,left,left,left,left,left,left,left</colAligns>
						</table>
						<query>
							<queryTable width="auto" title="" maxRows="10" showError="false">
								<headers><xsl:value-of select='resource:getString($constants,"orderNum")'/>,<xsl:value-of select='resource:getString($constants,"dateRec")'/>,<xsl:value-of select='resource:getString($constants,"upc")'/>,
							<xsl:value-of select='resource:getString($constants,"inventoryItem")'/>,<xsl:value-of select='resource:getString($constants,"organization")'/>,<xsl:value-of select='resource:getString($constants,"qty")'/>,
							<xsl:value-of select='resource:getString($constants,"cost")'/>,<xsl:value-of select='resource:getString($constants,"QC")'/>,<xsl:value-of select='resource:getString($constants,"extReference")'/></headers>
								<widths>60,70,85,149,164,35,50,80,95</widths>
								<editors>
									<textbox case="mixed"/>
									<label/>
									<textbox case="mixed"/>
									<textbox case="mixed"/>
									<textbox case="upper"/>
									<textbox case="mixed"/>
									<textbox case="mixed"/>
									<textbox case="mixed"/>
									<textbox case="mixed"/>		 	
								</editors>
								<fields>
									<xsl:value-of select='orderMeta:getId($order)'/>,<xsl:value-of select='inventoryReceiptMeta:getReceivedDate($receipt)'/>,<xsl:value-of select='inventoryReceiptMeta:getUpc($receipt)'/>,
									<xsl:value-of select='orderItemMeta:getInventoryItemId($orderItem)'/>,<xsl:value-of select='orderMeta:getOrganizationId($order)'/>,<xsl:value-of select='inventoryReceiptMeta:getQuantityReceived($receipt)'/>,
									<xsl:value-of select='inventoryReceiptMeta:getUnitCost($receipt)'/>,<xsl:value-of select='inventoryReceiptMeta:getQcReference($receipt)'/>,<xsl:value-of select='inventoryReceiptMeta:getExternalReference($receipt)'/>										
								</fields>
							</queryTable>
							</query>
						</widget>
						<widget style="WhiteContentPanel" halign="center">									
							<appButton action="removeRow" onclick="this" style="Button" key="removeReceiptButton">
								<HorizontalPanel>
              						<AbsolutePanel style="RemoveRowButtonImage"/>
                						<text><xsl:value-of select='resource:getString($constants,"removeRow")'/></text>
							              </HorizontalPanel>
						            </appButton>
						            </widget>
							</VerticalPanel>
						
				</VerticalPanel>
			
			<HorizontalPanel>
				<VerticalPanel style="Form">
					<titledPanel key="borderedPanel">
						<legend><text style="LegendTitle"><xsl:value-of select='resource:getString($constants,"organizationAddress")'/></text></legend>
							<content>
								<TablePanel style="Form">
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"aptSuite")'/>:</text>
									<widget colspan="3">
										<textbox case="upper" key="{addressMeta:getMultipleUnit($address)}" width="180px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
									</widget>		
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"address")'/>:</text>
									<widget colspan="3">
										<textbox case="upper" key="{addressMeta:getStreetAddress($address)}" width="180px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
									</widget>		
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"city")'/>:</text>
									<widget colspan="3">
										<textbox case="upper" key="{addressMeta:getCity($address)}" width="180px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
									</widget>		
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"state")'/>:</text>
									<widget>
										<textbox case="upper" key="{addressMeta:getState($address)}" width="30px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
									</widget>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"zipcode")'/>:</text>
									<widget>
										<textbox case="upper" key="{addressMeta:getZipCode($address)}" width="60px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
									</widget>
								</row>
							</TablePanel>
						</content>
						</titledPanel>
					</VerticalPanel>
					<VerticalPanel style="Form">
						<titledPanel key="borderedPanel">
							<legend><text style="LegendTitle"><xsl:value-of select='resource:getString($constants,"itemInformation")'/></text></legend>
								<content>
								<TablePanel style="Form">
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"description")'/>:</text>
									<widget colspan="2">
										<textbox case="mixed" key="{inventoryItemMeta:getDescription($invItem)}" width="195px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
									</widget>
									<widget valign="middle">
										<check key="addToExisting" onClick="this" tab="{inventoryLocationMeta:getStorageLocationId($loc)},{inventoryLocationMeta:getExpirationDate($loc)}"><text style="CheckboxPrompt"><xsl:value-of select='resource:getString($constants,"addToExisting")'/></text></check>
									</widget>
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"store")'/>:</text>
									<widget>
										<textbox case="mixed" key="{inventoryItemMeta:getStoreId($invItem)}" width="115px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
									</widget>	
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"location")'/>:</text>
									<widget>
									<autoDropdown key="{inventoryLocationMeta:getStorageLocationId($loc)}" cat="location" autoParams="InventoryReceiptAutoParams" serviceUrl="OpenELISServlet?service=org.openelis.modules.inventoryReceipt.server.InventoryReceiptService" case="mixed" width="160px" onchange="this" tab="{inventoryLocationMeta:getLotNumber($loc)},addToExisting">
										<headers>Desc</headers>
										<widths>300</widths>
									</autoDropdown>
									<query>
										<textbox case="mixed" tab="{inventoryLocationMeta:getLotNumber($loc)},addToExisting" width="176px"/>
									</query>
									</widget>
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"purchasedUnits")'/>:</text>
									<widget>
										<textbox case="mixed" key="{inventoryItemMeta:getPurchasedUnitsId($invItem)}" width="90px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
									</widget>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"lotNum")'/>:</text>
									<textbox case="mixed" key="{inventoryLocationMeta:getLotNumber($loc)}" onchange="this" width="100px" max="30" tab="{inventoryLocationMeta:getExpirationDate($loc)},{inventoryLocationMeta:getStorageLocationId($loc)}"/>
								</row>
								<row>
									<widget colspan="2">
										<VerticalPanel/>
									</widget>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"expDate")'/>:</text>
									<calendar key="{inventoryLocationMeta:getExpirationDate($loc)}" onChange="this" begin="0" end="2" tab="addToExisting,{inventoryLocationMeta:getLotNumber($loc)}"/>							
								</row>
								</TablePanel>
								</content>
								</titledPanel>
								</VerticalPanel>
								</HorizontalPanel>
								
				</VerticalPanel>				
		</HorizontalPanel>
	</display>
	<rpc key="display">
		<dropdown key="{inventoryLocationMeta:getStorageLocationId($loc)}" required="false"/>
		<string key="{inventoryLocationMeta:getLotNumber($loc)}" required="false"/>
		<date key="{inventoryLocationMeta:getExpirationDate($loc)}" begin="0" end="2" required="false"/>
		<check key="addToExisting" required="false"/>
	
    	<table key="receiptsTable"/>
    	
    	<!--disabled values -->
    	<string key="{inventoryItemMeta:getDescription($invItem)}" required="false"/>
    	<string key="{inventoryItemMeta:getStoreId($invItem)}" required="false"/>
    	<string key="{inventoryItemMeta:getPurchasedUnitsId($invItem)}" required="false"/>
    	<string key="{addressMeta:getMultipleUnit($address)}" required="false"/>
    	<string key="{addressMeta:getStreetAddress($address)}" required="false"/>
    	<string key="{addressMeta:getCity($address)}" required="false"/>
    	<string key="{addressMeta:getState($address)}" required="false"/>
    	<string key="{addressMeta:getZipCode($address)}" required="false"/>
	</rpc>
	<rpc key="query">
    	<table key="receiptsTable"/>
      	<queryString key="{orderMeta:getId($order)}" required="false"/>
		<!-- this should be changed to date when we can query by dates -->
		<queryString key="{inventoryReceiptMeta:getReceivedDate($receipt)}" required="false"/>-->
		<queryString key="{inventoryReceiptMeta:getUpc($receipt)}" required="false"/>
		<queryString key="{orderItemMeta:getInventoryItemId($orderItem)}" required="false"/>
		<queryString key="{orderMeta:getOrganizationId($order)}" required="false"/>
		<queryNumber key="{inventoryReceiptMeta:getQuantityReceived($receipt)}" type="integer" required="false"/>
		<queryNumber key="{inventoryReceiptMeta:getUnitCost($receipt)}" type="double" required="false"/>
		<queryString key="{inventoryReceiptMeta:getQcReference($receipt)}" required="false"/>
		<queryString key="{inventoryReceiptMeta:getExternalReference($receipt)}" required="false"/>
		
		<queryString key="{inventoryLocationMeta:getStorageLocationId($loc)}" required="false"/>
		<queryString key="{inventoryLocationMeta:getLotNumber($loc)}" required="false"/>
<!--		<date key="{inventoryLocationMeta:getExpirationDate($loc)}" begin="0" end="2" required="false"/>-->
      
    </rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>
