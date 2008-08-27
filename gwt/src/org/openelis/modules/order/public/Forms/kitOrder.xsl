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
                xmlns:orderMeta="xalan://org.openelis.metamap.OrderMetaMap" 
                xmlns:orderItemMeta="xalan://org.openelis.metamap.OrderItemMetaMap"
                xmlns:orgMeta="xalan://org.openelis.metamap.OrderOrganizationMetaMap"
                xmlns:addr="xalan://org.openelis.meta.AddressMeta"
                xmlns:noteMeta="xalan://org.openelis.meta.NoteMeta"
                xmlns:dictionaryMeta="xalan://org.openelis.meta.DictionaryMeta"
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
  
  <xsl:template match="doc"> 
    <xsl:variable name="order" select="orderMeta:new()"/>
    <xsl:variable name="orderItem" select="orderMeta:getOrderItem($order)"/>
    <xsl:variable name="organization" select="orderMeta:getOrderOrganization($order)"/>
    <xsl:variable name="reportTo" select="orderMeta:getReportToOrganization($order)"/>
    <xsl:variable name="billTo" select="orderMeta:getBillToOrganization($order)"/>
    <xsl:variable name="custNote" select="orderMeta:getCustomerNote($order)"/>
    <xsl:variable name="shippingNote" select="orderMeta:getShippingNote($order)"/>
    <xsl:variable name="store" select="orderMeta:getStore($order)"/>
    <xsl:variable name="orderItemInvItem" select="orderItemMeta:getInventoryItem($orderItem)"/>
    <xsl:variable name="orgAddress" select="orgMeta:getAddress($organization)"/>
    <xsl:variable name="reportToAddress" select="orgMeta:getAddress($reportTo)"/>
    <xsl:variable name="billToAddress" select="orgMeta:getAddress($billTo)"/>
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="KitOrder" name="{resource:getString($constants,'kitOrder')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
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
							<textbox case="lower" key="{orderMeta:getId($order)}" width="75px" max="20" tab="{orderMeta:getNeededInDays($order)},{orderMeta:getCostCenterId($order)}"/>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"neededDays")'/>:</text>
							<widget colspan="3">
								<textbox key="{orderMeta:getNeededInDays($order)}" width="75px" tab="{orderMeta:getShipFromId($order)},{orderMeta:getId($order)}"/>
							</widget>
						</row>
						<row>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"shipFrom")'/>:</text>
							<autoDropdown key="{orderMeta:getShipFromId($order)}" case="mixed" width="172px" tab="{orgMeta:getName($organization)},{orderMeta:getNeededInDays($order)}"/>							
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"shipTo")'/>:</text>
							<widget colspan="3">
								<autoDropdown cat="organization" key="{orgMeta:getName($organization)}" onchange="this" serviceUrl="OpenELISServlet?service=org.openelis.modules.order.server.OrderService" case="upper" width="172px" tab="{orderMeta:getStatusId($order)},{orderMeta:getShipFromId($order)}">
									<headers>Name,Street,City,St</headers>
									<widths>180,110,100,20</widths>
								</autoDropdown>
								<query>
									<textbox case="upper" width="188px"/>
								</query>
							</widget>
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"status")'/>:</text>
							<autoDropdown key="{orderMeta:getStatusId($order)}" case="mixed" width="90px" popWidth="auto" tab="{orderMeta:getOrderedDate($order)},{orgMeta:getName($organization)}"/>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"aptSuite")'/>:</text>
							<widget colspan="3">
								<textbox case="upper" key="{addr:getMultipleUnit($orgAddress)}" width="188px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
							</widget>	
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"orderDate")'/>:</text>
							<textbox key="{orderMeta:getOrderedDate($order)}" width="75px" tab="{orderMeta:getRequestedBy($order)},{orgMeta:getName($organization)}"/>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"address")'/>:</text>
							<widget colspan="3">
								<textbox case="upper" key="{addr:getStreetAddress($orgAddress)}" width="188px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
							</widget>			
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"requestedBy")'/>:</text>
							<textbox key="{orderMeta:getRequestedBy($order)}" width="203px" tab="{orderMeta:getCostCenterId($order)},{orderMeta:getOrderedDate($order)}"/>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"city")'/>:</text>
							<widget colspan="3">
								<textbox case="upper" key="{addr:getCity($orgAddress)}" width="188px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
							</widget>
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"costCenter")'/>:</text>
							<autoDropdown key="{orderMeta:getCostCenterId($order)}" case="mixed" width="187px" popWidth="auto" tab="{orderMeta:getId($order)},{orderMeta:getRequestedBy($order)}">
								<widths>167</widths>
							</autoDropdown>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"state")'/>:</text>
							<widget>
							<textbox case="upper" key="{addr:getState($orgAddress)}" width="35px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
							</widget>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"zipcode")'/>:</text>
							<widget>
							<textbox case="upper" key="{addr:getZipCode($orgAddress)}" width="65px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
							</widget>
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"description")'/>:</text>
							<widget colspan="3">
								<autoDropdown cat="orderDesc" key="{orderMeta:getDescription($order)}" serviceUrl="OpenELISServlet?service=org.openelis.modules.order.server.OrderService" case="mixed" width="300px" tab="??,??">
									<headers>Description</headers>
									<widths>310</widths>
								</autoDropdown>
								<query>
									<textbox case="mixed" width="318px"/>
								</query>
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
							<table width="auto" key="itemsTable" maxRows="9" title="" manager="this" showError="false" showScroll="true">
								<headers><xsl:value-of select='resource:getString($constants,"quantity")'/>,<xsl:value-of select='resource:getString($constants,"inventoryItem")'/>,
								<xsl:value-of select='resource:getString($constants,"store")'/>,<xsl:value-of select='resource:getString($constants,"location")'/></headers>
								<widths>60,170,164,167</widths>
								<editors>
									<textbox case="mixed"/>
									<autoDropdown cat="inventoryItemWithStoreAndLoc" case="lower" serviceUrl="OpenELISServlet?service=org.openelis.modules.order.server.OrderService" width="130px">												
										<headers>Name,Store,Location,Lot #, Exp Date,Qty</headers>
										<widths>135,110,160,70,70,30</widths>
									</autoDropdown>
									<label/>
									<label/>
								</editors>
								<fields>
									<number key="{orderItemMeta:getQuantityRequested($orderItem)}" type="integer" required="true"/>
									<dropdown key="{invItemMeta:getName($orderItemInvItem)}" required="true"/>
									<string key="{dictionaryMeta:getEntry($store)}" required="false"/>
									<string key="location" required="false"/>
								</fields>
								<sorts>false,true,true,true</sorts>
								<filters>false,false,false,false</filters>
								<colAligns>left,left,left,left</colAligns>
							</table>
							<query>
								<queryTable width="auto" maxRows="9" title="" showError="false">
									<headers><xsl:value-of select='resource:getString($constants,"quantity")'/>,<xsl:value-of select='resource:getString($constants,"inventoryItem")'/>,
									<xsl:value-of select='resource:getString($constants,"store")'/>,<xsl:value-of select='resource:getString($constants,"location")'/></headers>
									<widths>60,188,164,167</widths>
									<editors>
										<textbox case="mixed"/>
										<textbox case="lower"/>
										<textbox case="mixed"/>
										<label/>
									</editors>
									<fields>
										<xsl:value-of select='orderItemMeta:getQuantityRequested($orderItem)'/>,
										<xsl:value-of select='invItemMeta:getName($orderItemInvItem)'/>,
										<xsl:value-of select='dictionaryMeta:getEntry($store)'/>,
										label1
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
					<!-- TAB 2 (customer notes)-->		
					<tab key="customerNotesTab" text="{resource:getString($constants,'customerNotes')}">
						<VerticalPanel width="100%" height="247px" spacing="0" padding="0">
							<TablePanel key="noteFormPanel" style="Form" padding="0" spacing="0">
								<row>
									<widget colspan="2" align="center">
										<appButton action="standardNoteCustomer" onclick="this" key="standardNoteCustomerButton" style="Button">
										<HorizontalPanel>
              								<AbsolutePanel style="StandardNoteButtonImage"/>
                							<text><xsl:value-of select='resource:getString($constants,"standardNote")'/></text>
   					              		</HorizontalPanel>
						            </appButton>
						            </widget>
								</row>
								<row>
									<HorizontalPanel width="14px"/>
									<widget colspan="2">
										<textarea width="576px" height="197px" case="mixed" key="{noteMeta:getText($custNote)}"/>
									</widget>
								</row>
							</TablePanel>
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
              								<AbsolutePanel xsi:type="Absolute" layout="absolute" style="StandardNoteButtonImage"/>
	                						<text><xsl:value-of select='resource:getString($constants,"standardNote")'/></text>
						              </HorizontalPanel>
						            </appButton>
						            </widget>
								</row>
								<row>
									<HorizontalPanel width="14px"/>
									<widget colspan="2">
										<textarea width="576px" height="197px" case="mixed" key="{noteMeta:getText($shippingNote)}"/>
									</widget>
								</row>
							</TablePanel>
						</VerticalPanel>
					</tab>
					<!-- TAB 4 (report to/bill to)-->
					<tab key="reportToBillToTab" text="{resource:getString($constants,'reportToBillTo')}">
						<VerticalPanel height="247px" width="610px">
						<TablePanel style="Form">
							<row>
								<text style="Prompt"><xsl:value-of select='resource:getString($constants,"reportTo")'/>:</text>
								<widget colspan="4">
									<autoDropdown cat="reportTo" key="{orgMeta:getName($reportTo)}" onchange="this" serviceUrl="OpenELISServlet?service=org.openelis.modules.order.server.OrderService" case="upper" width="172px" tab="{orgMeta:getName($reportTo)},{orgMeta:getName($reportTo)}">
										<headers>Name,Street,City,St</headers>
										<widths>180,110,100,20</widths>
									</autoDropdown>
									<query>
										<textbox case="upper" width="188px" tab="{orgMeta:getName($reportTo)},{orgMeta:getName($reportTo)}"/>
									</query>
								</widget>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"billTo")'/>:</text>
								<widget colspan="3">
									<autoDropdown cat="billTo" key="{orgMeta:getName($billTo)}" onchange="this" serviceUrl="OpenELISServlet?service=org.openelis.modules.order.server.OrderService" case="upper" width="172px" tab="{orgMeta:getName($reportTo)},{orgMeta:getName($reportTo)}">
										<headers>Name,Street,City,St</headers>
										<widths>180,110,100,20</widths>
									</autoDropdown>
									<query>
										<textbox case="upper" width="188px" tab="{orgMeta:getName($reportTo)},{orgMeta:getName($reportTo)}"/>
									</query>
								</widget>
							</row>
							<row>
								<text style="Prompt"><xsl:value-of select='resource:getString($constants,"aptSuite")'/>:</text>
								<widget colspan="4">
									<textbox case="upper" key="{addr:getMultipleUnit($reportToAddress)}" width="188px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
								</widget>
								<text style="Prompt"><xsl:value-of select='resource:getString($constants,"aptSuite")'/>:</text>
								<widget colspan="3">
									<textbox case="upper" key="{addr:getMultipleUnit($billToAddress)}" width="188px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
								</widget>	
							</row>
						<row>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"address")'/>:</text>
							<widget colspan="4">
								<textbox case="upper" key="{addr:getStreetAddress($reportToAddress)}" width="188px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
							</widget>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"address")'/>:</text>
							<widget colspan="3">
								<textbox case="upper" key="{addr:getStreetAddress($billToAddress)}" width="188px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
							</widget>			
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"city")'/>:</text>
							<widget colspan="4">
								<textbox case="upper" key="{addr:getCity($reportToAddress)}" width="188px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
							</widget>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"city")'/>:</text>
							<widget colspan="3">
								<textbox case="upper" key="{addr:getCity($billToAddress)}" width="188px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
							</widget>
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"state")'/>:</text>
							<widget>
								<textbox case="upper" key="{addr:getState($reportToAddress)}" width="35px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
							</widget>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"zipcode")'/>:</text>
							<widget>
								<textbox case="upper" key="{addr:getZipCode($reportToAddress)}" width="65px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
							</widget>
							<HorizontalPanel width="15px"/>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"state")'/>:</text>
							<widget>
								<textbox case="upper" key="{addr:getState($billToAddress)}" width="35px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
							</widget>	
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"zipcode")'/>:</text>
							<widget>
								<textbox case="upper" key="{addr:getZipCode($billToAddress)}" width="65px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
							</widget>
						</row>
					</TablePanel>
				</VerticalPanel>
			</tab>
					<tab key="tab4" text="Tab 5">
						<VerticalPanel height="247px" width="610px"/>
					</tab>
					<tab key="tab4" text="Tab 6">
						<VerticalPanel height="247px" width="610px"/>
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
      <string key="{orderMeta:getOrderedDate($order)}" required="true"/>
      <string key="{orderMeta:getRequestedBy($order)}" required="true"/>
      <dropdown key="{orderMeta:getCostCenterId($order)}" type="integer" required="false"/>
      <dropdown key="{orgMeta:getName($organization)}" required="true"/>
      <dropdown key="{orderMeta:getShipFromId($order)}" required="false"/>
      <dropdown key="{orderMeta:getDescription($order)}" required="false"/>
      
      <rpc key="shippingNote">
    	  <string key="{noteMeta:getText($shippingNote)}" required="false"/>
      </rpc>
      <rpc key="custNote">
          <string key="{noteMeta:getText($custNote)}" required="false"/>
      </rpc>
      <rpc key="items">
	      <table key="itemsTable"/>
      </rpc>
      <rpc key="reportToBillTo">
		  <dropdown key="{orgMeta:getName($reportTo)}" required="false"/>
	      <dropdown key="{orgMeta:getName($billTo)}" required="false"/>
      
	      <!--report to address-->
	      <string key="{addr:getMultipleUnit($reportToAddress)}" required="false"/>
	      <string key="{addr:getStreetAddress($reportToAddress)}" required="false"/>
	      <string key="{addr:getCity($reportToAddress)}" required="false"/>
	      <string key="{addr:getState($reportToAddress)}" required="false"/>
	      <string key="{addr:getZipCode($reportToAddress)}" required="false"/>
	      
	      <!--bill to address -->
	      <string key="{addr:getMultipleUnit($billToAddress)}" required="false"/>
	      <string key="{addr:getStreetAddress($billToAddress)}" required="false"/>
	      <string key="{addr:getCity($billToAddress)}" required="false"/>
	      <string key="{addr:getState($billToAddress)}" required="false"/>
	      <string key="{addr:getZipCode($billToAddress)}" required="false"/>
      </rpc>
	  
      <!-- organization address-->
      <string key="{addr:getMultipleUnit($orgAddress)}" required="false"/>
      <string key="{addr:getStreetAddress($orgAddress)}" required="false"/>
      <string key="{addr:getCity($orgAddress)}" required="false"/>
      <string key="{addr:getState($orgAddress)}" required="false"/>
      <string key="{addr:getZipCode($orgAddress)}" required="false"/>
      
      <string key="orderType" reset="false"/>
      
      <!-- values not on this screen -->
      <string key="{orderMeta:getExternalOrderNumber($order)}" required="false"/>    

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
      <queryString key="{orgMeta:getName($reportTo)}" required="false"/>
      <queryString key="{orgMeta:getName($billTo)}" required="false"/>
      <queryString key="{orderMeta:getExternalOrderNumber($order)}" required="false"/>
      
      <string key="orderType" reset="false"/>
            
      <table key="itemsTable"/>
      <queryNumber key="{orderItemMeta:getQuantityRequested($orderItem)}" type="integer" required="false"/>
	  <queryString key="{invItemMeta:getName($orderItemInvItem)}" required="false"/>
	  <queryString key="{dictionaryMeta:getEntry($store)}" required="false"/>
      <queryString key="label1" required="false"/>
      
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>
