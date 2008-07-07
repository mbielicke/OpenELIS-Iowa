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
                xmlns:inventoryReceiptMeta="xalan://org.openelis.meta.InventoryReceiptMetaMap" 
                xmlns:orderMeta="xalan://org.openelis.meta.OrderMetaMap"
                xmlns:orderItemMeta="xalan://org.openelis.meta.OrderItemMetaMap"
                xmlns:inventoryLocationMeta="xalan://org.openelis.meta.InventoryLocationMeta"
                xmlns:organizationMeta="xalan://org.openelis.meta.OrderOrganizationMetaMap"
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
							<widths>60,70,85,140,155,30,50,80,100</widths>										
							<editors>
								<textbox case="upper"/>
								<textbox case="upper"/>
								<textbox case="upper"/>
								<textbox case="upper"/>
								<textbox case="upper"/>
								<textbox case="upper"/>
								<textbox case="upper"/>
								<textbox case="upper"/>
								<textbox case="upper"/>
							</editors>
							<fields>
								<string key="{orderMeta:getId($order)}" required="false"/>
								<string key="{inventoryReceiptMeta:getReceivedDate($receipt)}" required="false"/>
								<string key="{inventoryReceiptMeta:getUpc($receipt)}" required="false"/>
								<string key="{orderItemMeta:getInventoryItemId($orderItem)}" required="false"/>
								<string key="{orderMeta:getOrganizationId($order)}" required="false"/>
								<string key="{inventoryReceiptMeta:getQuantityReceived($receipt)}" required="false"/>
								<string key="{inventoryReceiptMeta:getUnitCost($receipt)}" required="false"/>
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
								<widths>60,70,85,149,164,30,50,80,100</widths>
								<editors>
									<textbox case="upper"/>
									<textbox case="upper"/>
									<textbox case="upper"/>
									<textbox case="upper"/>
									<textbox case="upper"/>
									<textbox case="upper"/>
									<textbox case="upper"/>
									<textbox case="upper"/>
									<textbox case="upper"/>		 	
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
							<appButton action="removeRow" onclick="this" style="Button" key="removeContactButton">
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
									<text style="Prompt">Description:</text>
									<widget colspan="2">
										<textbox case="upper" key="{inventoryItemMeta:getDescription($invItem)}" width="195px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true" tab="??,??"/>
									</widget>
									<widget valign="middle">
										<check key="abc" tab="??,??"><text style="CheckboxPrompt">Add To Existing</text></check>
									</widget>
								</row>
								<row>
									<text style="Prompt">Store:</text>
									<widget>
										<textbox case="upper" key="{inventoryItemMeta:getStoreId($invItem)}" width="115px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true" tab="??,??"/>
									</widget>	
									<text style="Prompt">Location:</text>
									<autoDropdown key="{inventoryLocationMeta:getStorageLocationId($loc)}" case="mixed" width="160px" tab="??,??"/>
								</row>
								<row>
									<text style="Prompt">Purchased Units:</text>
									<widget>
										<textbox case="upper" key="{inventoryItemMeta:getPurchasedUnitsId($invItem)}" width="90px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true" tab="??,??"/>
									</widget>
									<text style="Prompt">Lot #:</text>
									<textbox case="upper" key="{inventoryLocationMeta:getLotNumber($loc)}" width="100px" max="30" tab="??,??"/>
								</row>
								<row>
									<widget colspan="2">
										<VerticalPanel/>
									</widget>
									<text style="Prompt">Exp Date:</text>
									<calendar key="{inventoryLocationMeta:getExpirationDate($loc)}" begin="0" end="2"/>							
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
		<dropdown key="{inventoryLocationMeta:getStorageLocationId($loc)}" required="true"/>
		<string key="{inventoryLocationMeta:getLotNumber($loc)}" required="false"/>
		<date key="{inventoryLocationMeta:getExpirationDate($loc)}" required="false"/>
	
    	<table key="receiptsTable"/>
	</rpc>
	<rpc key="query">
    	<table key="receiptsTable"/>
      	<queryString key="{orderMeta:getId($order)}" required="false"/>
		<string key="{inventoryReceiptMeta:getReceivedDate($receipt)}" required="false"/>
		<string key="{inventoryReceiptMeta:getUpc($receipt)}" required="false"/>
		<string key="{orderItemMeta:getInventoryItemId($orderItem)}" required="false"/>
		<string key="{orderMeta:getOrganizationId($order)}" required="false"/>
		<string key="{inventoryReceiptMeta:getQuantityReceived($receipt)}" required="false"/>
		<string key="{inventoryReceiptMeta:getUnitCost($receipt)}" required="false"/>
		<string key="{inventoryReceiptMeta:getQcReference($receipt)}" required="false"/>
		<string key="{inventoryReceiptMeta:getExternalReference($receipt)}" required="false"/>
      
    </rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>