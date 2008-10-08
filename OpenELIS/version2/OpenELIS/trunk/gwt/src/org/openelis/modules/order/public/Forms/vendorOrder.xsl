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
                xmlns:orderMeta="xalan://org.openelis.metamap.OrderMetaMap" 
                xmlns:orderItemMeta="xalan://org.openelis.metamap.OrderItemMetaMap"
                xmlns:orgMeta="xalan://org.openelis.metamap.OrderOrganizationMetaMap"
                xmlns:addr="xalan://org.openelis.meta.AddressMeta"
                xmlns:noteMeta="xalan://org.openelis.meta.NoteMeta"
                xmlns:dictionaryMeta="xalan://org.openelis.meta.DictionaryMeta"
                xmlns:invItemMeta="xalan://org.openelis.meta.InventoryItemMeta"
                xmlns:invTransMeta="xalan://org.openelis.metamap.TransReceiptOrderMetaMap"
                xmlns:invReceiptMeta="xalan://org.openelis.meta.InventoryReceiptMeta"
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZOneColumn.xsl"/>

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
    <xalan:component prefix="orderMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrderMetaMap"/>
  </xalan:component>

  <xalan:component prefix="orderItemMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrderItemMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="orgMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrderOrganizationMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="addr">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.AddressMeta"/>
  </xalan:component>
  
  <xalan:component prefix="noteMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.NoteMeta"/>
  </xalan:component>
  
  <xalan:component prefix="dictionaryMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.DictionaryMeta"/>
  </xalan:component>
  
  <xalan:component prefix="invItemMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryItemMeta"/>
  </xalan:component>
  
  <xalan:component prefix="invReceiptMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryReceiptMeta"/>
  </xalan:component>
  
    <xalan:component prefix="invTransMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.TransReceiptOrderMetaMap"/>
  </xalan:component>
  
  <xsl:template match="doc"> 
    <xsl:variable name="order" select="orderMeta:new()"/>
    <xsl:variable name="orderItem" select="orderMeta:getOrderItem($order)"/>
    <xsl:variable name="organization" select="orderMeta:getOrderOrganization($order)"/>
    <xsl:variable name="reportTo" select="orderMeta:getReportToOrganization($order)"/>
    <xsl:variable name="billTo" select="orderMeta:getBillToOrganization($order)"/>
    <xsl:variable name="custNote" select="orderMeta:getCustomerNote($order)"/>
    <xsl:variable name="shippingNote" select="orderMeta:getShippingNote($order)"/>
    <xsl:variable name="store" select="orderMeta:getStore($order)"/>   
    <xsl:variable name="transaction" select="orderMeta:getInventoryTransaction($order)"/>
    <xsl:variable name="receipt" select="invTransMeta:getInventoryReceipt($transaction)"/>
    <xsl:variable name="orderItemInvItem" select="orderItemMeta:getInventoryItem($orderItem)"/>
    <xsl:variable name="orgAddress" select="orgMeta:getAddress($organization)"/>
    <xsl:variable name="reportToAddress" select="orgMeta:getAddress($reportTo)"/>
    <xsl:variable name="billToAddress" select="orgMeta:getAddress($billTo)"/>
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="VendorOrder" name="{resource:getString($constants,'vendorOrder')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
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
    				<xsl:call-template name="buttonPanelDivider"/>
    			<xsl:call-template name="optionsButton">
    			    <xsl:with-param name="language">
    			        <xsl:value-of select="language"/>
    			    </xsl:with-param>
    			</xsl:call-template>
				</buttonPanel>
			</AbsolutePanel>
			<!--end button panel-->
			<VerticalPanel>
				<VerticalPanel>
					<HorizontalPanel>
							<TablePanel style="Form">
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"orderNum")'/>:</text>
									<textbox case="lower" key="{orderMeta:getId($order)}" width="75px" max="20" tab="{orderMeta:getNeededInDays($order)},{orderMeta:getExternalOrderNumber($order)}"/>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"neededDays")'/>:</text>
									<widget colspan="3">
										<textbox key="{orderMeta:getNeededInDays($order)}" width="75px" tab="{orderMeta:getStatusId($order)},{orderMeta:getId($order)}"/>
									</widget>
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"status")'/>:</text>
									<autoDropdown key="{orderMeta:getStatusId($order)}" case="mixed" width="90px" popWidth="auto" tab="{orgMeta:getName($organization)},{orderMeta:getNeededInDays($order)}"/>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"vendor")'/>:</text>
									<widget colspan="3">
										<autoDropdown cat="organization" key="{orgMeta:getName($organization)}" onchange="this" serviceUrl="OpenELISServlet?service=org.openelis.modules.order.server.OrderService" case="upper" width="172px" tab="{orderMeta:getOrderedDate($order)},{orderMeta:getStatusId($order)}">
											<headers>Name,Street,City,St</headers>
											<widths>180,110,100,20</widths>
										</autoDropdown>
										<query>
											<textbox case="upper" width="188px" tab="{orderMeta:getOrderedDate($order)},{orderMeta:getStatusId($order)}"/>
										</query>
									</widget>
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"orderDate")'/>:</text>
									<textbox key="{orderMeta:getOrderedDate($order)}" width="75px" tab="{orderMeta:getRequestedBy($order)},{orgMeta:getName($organization)}"/>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"aptSuite")'/>:</text>
									<widget colspan="3">
										<textbox case="upper" key="{addr:getMultipleUnit($orgAddress)}" width="188px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
									</widget>	
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"requestedBy")'/>:</text>
									<textbox key="{orderMeta:getRequestedBy($order)}" width="203px" tab="{orderMeta:getCostCenterId($order)},{orderMeta:getOrderedDate($order)}"/>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"address")'/>:</text>
									<widget colspan="3">
										<textbox case="upper" key="{addr:getStreetAddress($orgAddress)}" width="188px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
									</widget>			
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"costCenter")'/>:</text>
									<autoDropdown key="{orderMeta:getCostCenterId($order)}" case="mixed" width="187px" popWidth="auto" tab="{orderMeta:getExternalOrderNumber($order)},{orderMeta:getRequestedBy($order)}">
										<widths>167</widths>
									</autoDropdown>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"city")'/>:</text>
									<widget colspan="3">
										<textbox case="upper" key="{addr:getCity($orgAddress)}" width="188px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
									</widget>
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"extOrderNum")'/>:</text>
									<textbox case="mixed" key="{orderMeta:getExternalOrderNumber($order)}" width="188px" max="20" tab="{orderMeta:getId($order)},{orderMeta:getCostCenterId($order)}"/>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"state")'/>:</text>
									<widget>
									<textbox case="mixed" key="{addr:getState($orgAddress)}" width="35px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
									</widget>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"zipcode")'/>:</text>
									<widget>
									<textbox case="mixed" key="{addr:getZipCode($orgAddress)}" width="65px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
									</widget>
								</row>
							</TablePanel>
						</HorizontalPanel>
					</VerticalPanel>
				<!-- TAB PANEL -->
				<TabPanel height="200px" key="orderTabPanel" halign="center">
					<!-- TAB 1 (items) -->
					<tab key="itemsTab" text="{resource:getString($constants,'items')}">
							<VerticalPanel spacing="0" padding="0">
							<widget valign="top">
								<table width="auto" key="itemsTable" manager="this" maxRows="9" title="" showError="false" showScroll="true">
										<headers><xsl:value-of select='resource:getString($constants,"quantity")'/>,<xsl:value-of select='resource:getString($constants,"inventoryItem")'/>,
										<xsl:value-of select='resource:getString($constants,"store")'/>,<xsl:value-of select='resource:getString($constants,"unitCost")'/>,
										<xsl:value-of select='resource:getString($constants,"catalogNum")'/></headers>
										<widths>60,178,163,70,87</widths>
										<editors>
											<textbox case="mixed"/>
											<autoDropdown cat="inventoryItemWithStore" case="lower" serviceUrl="OpenELISServlet?service=org.openelis.modules.order.server.OrderService" width="210px">												
												<headers>Name,Store, Dispensed Units</headers>
												<widths>135,110,110</widths>
											</autoDropdown>
											<label/>
											<textbox case="mixed" displayMask="{resource:getString($constants,'displayCurrencyFormat')}" editorMask="{resource:getString($constants,'editorCurrencyFormat')}"/>
											<textbox case="mixed"/>
										</editors>
										<fields>
											<number key="{orderItemMeta:getQuantityRequested($orderItem)}" type="integer" required="true"/>
											<dropdown key="{invItemMeta:getName($orderItemInvItem)}" required="true"/>
											<string key="{dictionaryMeta:getEntry($store)}" required="false"/>
											<number type="double" required="false"/>
											<string required="false"/>
										</fields>
										<sorts>false,true,true,false,false</sorts>
										<filters>false,false,false,false,false</filters>
										<colAligns>left,left,left,right,left</colAligns>
									</table>
									<query>
									<queryTable width="auto" maxRows="9" title="" showError="false" showScroll="true">
										<headers><xsl:value-of select='resource:getString($constants,"quantity")'/>,<xsl:value-of select='resource:getString($constants,"inventoryItem")'/>,
										<xsl:value-of select='resource:getString($constants,"store")'/>,<xsl:value-of select='resource:getString($constants,"unitCost")'/>,
										<xsl:value-of select='resource:getString($constants,"catalogNum")'/></headers>
										<widths>60,178,163,70,87</widths>
										<editors>
											<textbox case="mixed"/>
											<textbox case="lower"/>
											<textbox case="mixed"/>
											<textbox case="mixed" />
											<textbox case="mixed"/>
										</editors>
										<fields>
											<xsl:value-of select='orderItemMeta:getQuantityRequested($orderItem)'/>,
											<xsl:value-of select='invItemMeta:getName($orderItemInvItem)'/>,
											<xsl:value-of select='dictionaryMeta:getEntry($store)'/>,
											<xsl:value-of select='orderItemMeta:getUnitCost($orderItem)'/>,
											<xsl:value-of select='orderItemMeta:getCatalogNumber($orderItem)'/>
										</fields>								
									</queryTable>
									</query>
								</widget>
								<widget style="WhiteContentPanel" halign="center">
									<appButton action="removeItemRow" onclick="this" style="Button" key="removeItemButton">
									<HorizontalPanel>
              						<AbsolutePanel style="RemoveRowButtonImage"/>
                						<text><xsl:value-of select='resource:getString($constants,"removeRow")'/></text>
 					                </HorizontalPanel>
						            </appButton>
						            </widget>
							</VerticalPanel>
					</tab>		
					<!-- TAB 2 (receipts) -->	
					<tab key="receiptsTab" text="{resource:getString($constants,'receipt')}">
						<VerticalPanel spacing="0" padding="0">
							<widget valign="top">
								<table width="auto" key="receiptsTable" manager="InventoryLocationsTable" maxRows="10" title="" showError="false" showScroll="true">
										<headers><xsl:value-of select='resource:getString($constants,"dateRec")'/>,<xsl:value-of select='resource:getString($constants,"item")'/>,
										<xsl:value-of select='resource:getString($constants,"upc")'/>,<xsl:value-of select='resource:getString($constants,"qty")'/>,
										<xsl:value-of select='resource:getString($constants,"cost")'/>,<xsl:value-of select='resource:getString($constants,"extReference")'/></headers>
										<widths>80,155,95,40,55,130</widths>
										<editors>
											<label/>
											<label/>
											<label/>
											<label/>
											<label displayMask="{resource:getString($constants,'displayCurrencyFormat')}"/>
											<label/>
										</editors>
										<fields>
											<string/>
											<string/>
											<string/>
											<number type="integer"/>
											<number type="double"/>
											<string/>
										</fields>
										<sorts>true,true,true,true,true,true</sorts>
										<filters>false,false,false,false,false,false</filters>
										<colAligns>left,left,left,left,left,left</colAligns>
									</table>
									<query>
									<queryTable width="auto" maxRows="10" title="" showError="false" showScroll="true">
										<headers><xsl:value-of select='resource:getString($constants,"dateRec")'/>,<xsl:value-of select='resource:getString($constants,"item")'/>,
										<xsl:value-of select='resource:getString($constants,"upc")'/>,<xsl:value-of select='resource:getString($constants,"qty")'/>,
										<xsl:value-of select='resource:getString($constants,"cost")'/>,<xsl:value-of select='resource:getString($constants,"extReference")'/>
										</headers>
										<widths>80,155,95,40,55,130</widths>
										<editors>
											<label/>
											<label/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
										</editors>
										<fields>
										<xsl:value-of select='invReceiptMeta:getReceivedDate($receipt)'/>,
										item,
										<xsl:value-of select='invReceiptMeta:getUpc($receipt)'/>,
										<xsl:value-of select='invReceiptMeta:getQuantityReceived($receipt)'/>,
										<xsl:value-of select='invReceiptMeta:getUnitCost($receipt)'/>,
										<xsl:value-of select='invReceiptMeta:getExternalReference($receipt)'/>
										</fields>
									</queryTable>
									</query>
								</widget>
								<HorizontalPanel height="8px"/>
							</VerticalPanel>
					</tab>
					<!-- TAB 3 (order notes) -->
					<tab key="orderNotesTab" text="{resource:getString($constants,'orderShippingNotes')}">
						<VerticalPanel width="100%" height="247px" spacing="0" padding="0">
							<TablePanel key="noteFormPanel" style="Form" padding="0" spacing="0">
								<row>
									<widget colspan="2" align="center">
										<appButton action="standardNoteShipping" onclick="this" key="standardNoteShippingButton" style="Button">
											<HorizontalPanel>
              								<AbsolutePanel style="StandardNoteButtonImage"/>
	                						<text><xsl:value-of select='resource:getString($constants,"standardNote")'/></text>
							              </HorizontalPanel>
						            </appButton>
						            </widget>
								</row>
								<row>
									<HorizontalPanel layout="horizontal" width="14px" xsi:type="Panel"/>
									<widget colspan="2">
										<textarea width="576px" height="197px" case="mixed" key="{noteMeta:getText($shippingNote)}"/>
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
  	  <!-- values on the screen -->
  	  <number key="{orderMeta:getId($order)}" type="integer" required="false"/>
      <number key="{orderMeta:getNeededInDays($order)}" type="integer" required="true"/>
      <dropdown key="{orderMeta:getStatusId($order)}" type="integer" required="true"/> 
      <dropdown key="{orgMeta:getName($organization)}" required="true"/>
      <string key="{orderMeta:getOrderedDate($order)}" required="true"/>
      <string key="{orderMeta:getRequestedBy($order)}" required="true"/>
      <dropdown key="{orderMeta:getCostCenterId($order)}" type="integer" required="false"/>
      <string key="{orderMeta:getExternalOrderNumber($order)}" required="false"/>
     
      <rpc key="shippingNote">
    	  <string key="{noteMeta:getText($shippingNote)}" required="false"/>
      </rpc>
      <rpc key="items">
	      <table key="itemsTable"/>
      </rpc>
	  <rpc key="receipts">
	  	  <table key="receiptsTable"/>
	  </rpc>
      
      <!-- organization address-->
      <string key="{addr:getMultipleUnit($orgAddress)}" required="false"/>
      <string key="{addr:getStreetAddress($orgAddress)}" required="false"/>
      <string key="{addr:getCity($orgAddress)}" required="false"/>
      <string key="{addr:getState($orgAddress)}" required="false"/>
      <string key="{addr:getZipCode($orgAddress)}" required="false"/>
            
      <string key="orderType" reset="false"/>
      
      <string key="orderTabPanel" reset="false">itemsTab</string>
	</rpc>
	<rpc key="query">
	  <queryNumber key="{orderMeta:getId($order)}" type="integer" required="false"/>
      <queryNumber key="{orderMeta:getNeededInDays($order)}" type="integer" required="false"/>
      <dropdown key="{orderMeta:getStatusId($order)}" type="integer" required="false"/> 
      <queryString key="{orgMeta:getName($organization)}" required="false"/>
      <queryString key="{orderMeta:getOrderedDate($order)}" required="false"/>
      <queryString key="{orderMeta:getRequestedBy($order)}" required="false"/>
      <dropdown key="{orderMeta:getCostCenterId($order)}" type="integer" required="false"/>
      <queryString key="{orderMeta:getExternalOrderNumber($order)}" required="false"/>
      
      <string key="orderType" reset="false"/>
      
      <!-- order items table -->
      <table key="itemsTable"/>
      <queryNumber key="{orderItemMeta:getQuantityRequested($orderItem)}" type="integer" required="false"/>
	  <queryString key="{invItemMeta:getName($orderItemInvItem)}" required="false"/>
	  <queryString key="{dictionaryMeta:getEntry($store)}" required="false"/>
	  <queryString key="{orderItemMeta:getCatalogNumber($orderItem)}" required="false"/>
 	  <queryNumber key="{orderItemMeta:getUnitCost($orderItem)}" type="double" required="false"/>

	  <!-- receipts table -->
      <table key="receiptsTable"/>
	  <queryString key="{invReceiptMeta:getReceivedDate($receipt)}" required="false"/>
	  <queryString key="item" required="false"/>
	  <queryString key="{invReceiptMeta:getUpc($receipt)}" required="false"/>
	  <queryNumber key="{invReceiptMeta:getQuantityReceived($receipt)}" type="integer" required="false"/>
	  <queryNumber key="{invReceiptMeta:getUnitCost($receipt)}" type="double" required="false"/>
	  <queryString key="{invReceiptMeta:getExternalReference($receipt)}" required="false"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>
