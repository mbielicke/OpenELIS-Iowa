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
                xmlns:transReceiptLocationMeta="xalan://org.openelis.metamap.TransReceiptLocationMetaMap" 
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
  
  <xalan:component prefix="meta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.InventoryReceiptMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="inventoryReceiptMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryReceiptMeta"/>
  </xalan:component>
  
  <xalan:component prefix="transReceiptLocationMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TransReceiptLocationMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="orderMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.OrderMetaMap"/>
  </xalan:component>

  <xalan:component prefix="orderItemMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.OrderItemMetaMap"/>
  </xalan:component>
    
  <xalan:component prefix="inventoryLocationMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryLocationMeta"/>
  </xalan:component>
  
    <xalan:component prefix="organizationMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.OrderOrganizationMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="addressMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.AddressMeta"/>
  </xalan:component>
  
  <xalan:component prefix="inventoryItemMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryItemMeta"/>
  </xalan:component>
  
  <xsl:template match="doc"> 
    <xsl:variable name="receipt" select="meta:new()"/>
    <xsl:variable name="transForLoc" select="meta:getTransReceiptLocation($receipt)"/>
    <xsl:variable name="loc" select="transReceiptLocationMeta:getInventoryLocation($transForLoc)"/>
    <xsl:variable name="orderItem" select="meta:getOrderItem($receipt)"/>
    <xsl:variable name="order" select="orderItemMeta:getOrder($orderItem)"/>
    <xsl:variable name="invItem" select="meta:getInventoryitem($receipt)"/>
    <xsl:variable name="org" select="meta:getOrganization($receipt)"/>
    <xsl:variable name="address" select="organizationMeta:getAddress($org)"/>
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="InventoryReceipt" name="{resource:getString($constants,'inventoryReceipt')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
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
			<VerticalPanel style="WhiteContentPanel" spacing="0" padding="0">
					<widget valign="top">
					<resultsTable query="true" colwidths="2" showNavPanel="false" key="receiptsTable" maxRows="18" tablewidth="auto" headers="{resource:getString($constants,'name')}" width="100%">
						<table width="auto" manager="this" maxRows="10" title="" showError="false" showScroll="ALWAYS">
							<headers><xsl:value-of select='resource:getString($constants,"ordNum")'/>,<xsl:value-of select='resource:getString($constants,"dateRec")'/>,<xsl:value-of select='resource:getString($constants,"upc")'/>,
							<xsl:value-of select='resource:getString($constants,"inventoryItem")'/>,<xsl:value-of select='resource:getString($constants,"vendor")'/>,<xsl:value-of select='resource:getString($constants,"numRec")'/>,
							<xsl:value-of select='resource:getString($constants,"numReq")'/>,<xsl:value-of select='resource:getString($constants,"cost")'/>,<xsl:value-of select='resource:getString($constants,"extQC")'/>,
							<xsl:value-of select='resource:getString($constants,"extReference")'/></headers>
							<widths>50,75,80,140,155,50,50,55,80,100</widths>										
							<editors>
								<textbox case="mixed"/>
								<calendar begin="0" end="2"/>							
								<textbox case="mixed"/>
								<autoComplete cat="inventoryItem" case="lower" serviceUrl="OpenELISServlet?service=org.openelis.modules.inventoryReceipt.server.InventoryReceiptService" width="120px">												
									<headers>Name,Store</headers>
									<widths>100,150</widths>
								</autoComplete>
								<autoComplete cat="organization" case="upper" serviceUrl="OpenELISServlet?service=org.openelis.modules.inventoryReceipt.server.InventoryReceiptService" width="140px">
									<headers>Name,Street,City,St</headers>
									<widths>180,110,100,20</widths>
								</autoComplete>
								<textbox case="mixed"/>
								<label/>
								<textbox case="mixed" displayMask="{resource:getString($constants,'displayCurrencyFormat')}" editorMask="{resource:getString($constants,'editorCurrencyFormat')}"/>
								<textbox case="mixed"/>
								<textbox case="mixed"/>
							</editors>
							<fields>
								<integer key="{orderMeta:getId($order)}" required="false"/>
								<date key="{inventoryReceiptMeta:getReceivedDate($receipt)}" begin="0" end="2" required="true">current</date>
								<string key="{inventoryReceiptMeta:getUpc($receipt)}" required="false"/>
								<dropdown key="{inventoryItemMeta:getName($invItem)}" required="true"/>
								<dropdown key="{organizationMeta:getName($org)}" required="true"/>
								<integer key="{inventoryReceiptMeta:getQuantityReceived($receipt)}" required="false"/>
								<integer key="{orderItemMeta:getQuantity($orderItem)}" required="false"/>
								<double key="{inventoryReceiptMeta:getUnitCost($receipt)}" required="false"/>
								<string key="{inventoryReceiptMeta:getQcReference($receipt)}" required="false"/>
								<string key="{inventoryReceiptMeta:getExternalReference($receipt)}" required="false"/>
							</fields>
							<sorts>false,false,true,true,true,false,false,false,true,true</sorts>
							<filters>false,false,false,false,false,false,false,false,false,false</filters>
							<colAligns>left,left,left,left,left,left,left,right,left,left</colAligns>
						</table>
						<!--
						<query>
							<queryTable width="auto" title="" maxRows="10" showError="false" showScroll="ALWAYS">
								<headers><xsl:value-of select='resource:getString($constants,"ordNum")'/>,<xsl:value-of select='resource:getString($constants,"dateRec")'/>,<xsl:value-of select='resource:getString($constants,"upc")'/>,
							<xsl:value-of select='resource:getString($constants,"inventoryItem")'/>,<xsl:value-of select='resource:getString($constants,"vendor")'/>,<xsl:value-of select='resource:getString($constants,"numRec")'/>,
							<xsl:value-of select='resource:getString($constants,"numReq")'/>,<xsl:value-of select='resource:getString($constants,"cost")'/>,<xsl:value-of select='resource:getString($constants,"extQC")'/>,
							<xsl:value-of select='resource:getString($constants,"extReference")'/></headers>
							<widths>50,75,80,140,155,50,50,55,80,100</widths>										
								<editors>
									<textbox case="mixed"/>
									<calendar begin="0" end="2"/>
									<textbox case="mixed"/>
									<textbox case="mixed"/>
									<textbox case="upper"/>
									<label/>
									<label/>
									<label/>
									<label/>
									<textbox case="mixed"/>		 	
								</editors>
								<fields>
									<xsl:value-of select='orderMeta:getId($order)'/>,<xsl:value-of select='inventoryReceiptMeta:getReceivedDate($receipt)'/>,<xsl:value-of select='inventoryReceiptMeta:getUpc($receipt)'/>,
									<xsl:value-of select='inventoryItemMeta:getName($invItem)'/>,<xsl:value-of select='organizationMeta:getName($org)'/>,<xsl:value-of select='inventoryReceiptMeta:getQuantityReceived($receipt)'/>,
									<xsl:value-of select='orderItemMeta:getQuantity($orderItem)'/>,
									<xsl:value-of select='inventoryReceiptMeta:getUnitCost($receipt)'/>,<xsl:value-of select='inventoryReceiptMeta:getQcReference($receipt)'/>,<xsl:value-of select='inventoryReceiptMeta:getExternalReference($receipt)'/>										
								</fields>
							</queryTable>
							</query>
							-->
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
			<HorizontalPanel>
				<VerticalPanel style="subform">
	                <text style="FormTitle"><xsl:value-of select='resource:getString($constants,"vendorAddress")'/></text>
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
					</VerticalPanel>
					<VerticalPanel style="subform">
		                <text style="FormTitle"><xsl:value-of select='resource:getString($constants,"itemInformation")'/></text>
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
								<autoComplete key="{inventoryLocationMeta:getStorageLocationId($loc)}" cat="location" autoCall="this" serviceUrl="OpenELISServlet?service=org.openelis.modules.inventoryReceipt.server.InventoryReceiptService" case="mixed" width="160px" onchange="this" tab="{inventoryLocationMeta:getLotNumber($loc)},addToExisting">
									<headers>Desc</headers>
									<widths>300</widths>
								</autoComplete>
								<query>
									<textbox case="mixed" tab="{inventoryLocationMeta:getLotNumber($loc)},addToExisting" width="160px"/>
								</query>
								</widget>
							</row>
							<row>
								<text style="Prompt"><xsl:value-of select='resource:getString($constants,"dispensedUnits")'/>:</text>
								<widget>
									<textbox case="mixed" key="{inventoryItemMeta:getDispensedUnitsId($invItem)}" width="90px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
								</widget>
								<text style="Prompt"><xsl:value-of select='resource:getString($constants,"lotNum")'/>:</text>
								<textbox case="mixed" key="{inventoryLocationMeta:getLotNumber($loc)}" onchange="this" width="100px" max="30" tab="{inventoryLocationMeta:getExpirationDate($loc)},{inventoryLocationMeta:getStorageLocationId($loc)}"/>
							</row>
							<row>
							<widget colspan="2">
							<HorizontalPanel/>
							</widget>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"expDate")'/>:</text>
							<calendar key="{inventoryLocationMeta:getExpirationDate($loc)}" onChange="this" begin="0" end="2" tab="addToExisting,{inventoryLocationMeta:getLotNumber($loc)}"/>							
							</row>
							</TablePanel>
								</VerticalPanel>
								</HorizontalPanel>
								
				</VerticalPanel>				
		</VerticalPanel>
	</display>
	<rpc key="display">
		<table key="receiptsTable"/>
    	<string key="type" reset="false">receipt</string>
    	
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
	<!--
	<rpc key="query">
		<string key="type" reset="false">receipt</string>
    	<table key="receiptsTable"/>
      	<queryNumber key="{orderMeta:getId($order)}" type="integer" required="false"/>
		<queryDate key="{inventoryReceiptMeta:getReceivedDate($receipt)}"  begin="0" end="2" required="false"/>
		<queryString key="{inventoryReceiptMeta:getUpc($receipt)}" required="false"/>
		<queryString key="{inventoryItemMeta:getName($invItem)}" required="false"/>
		<queryString key="{organizationMeta:getName($org)}" required="false"/>
		<queryNumber key="{inventoryReceiptMeta:getQuantityReceived($receipt)}" type="integer" required="false"/>
		<queryNumber key="{orderItemMeta:getQuantity($orderItem)}" type="integer" required="false"/>
		<queryNumber key="{inventoryReceiptMeta:getUnitCost($receipt)}" type="double" required="false"/>
		<queryString key="{inventoryReceiptMeta:getQcReference($receipt)}" required="false"/>
		<queryString key="{inventoryReceiptMeta:getExternalReference($receipt)}" required="false"/>
      
    </rpc>
    -->
</screen>
  </xsl:template>
</xsl:stylesheet>
