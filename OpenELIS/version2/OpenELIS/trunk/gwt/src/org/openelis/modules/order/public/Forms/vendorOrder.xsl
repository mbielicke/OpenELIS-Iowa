<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale" 
                xmlns:orderMeta="xalan://org.openelis.meta.OrderMeta" 
                xmlns:orderItemMeta="xalan://org.openelis.meta.OrderItemMeta"
                xmlns:orderOrganizationAddressMeta="xalan://org.openelis.meta.OrderOrganizationAddressMeta"
                xmlns:orderReportToAddressMeta="xalan://org.openelis.meta.OrderReportToAddressMeta"
                xmlns:orderBillToAddressMeta="xalan://org.openelis.meta.OrderBillToAddressMeta"
                xmlns:orderShippingNoteMeta="xalan://org.openelis.meta.OrderShippingNoteMeta"
                xmlns:orderCustomerNoteMeta="xalan://org.openelis.meta.OrderCustomerNoteMeta"
                xmlns:orderItemInventoryItemMeta="xalan://org.openelis.meta.OrderItemInventoryItemMeta"
                xmlns:orderItemStoreMeta="xalan://org.openelis.meta.OrderItemStoreMeta"
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
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrderMeta"/>
  </xalan:component>

  <xalan:component prefix="orderItemMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrderItemMeta"/>
  </xalan:component>
   
  <xalan:component prefix="orderOrganizationAddressMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrderOrganizationAddressMeta"/>
  </xalan:component>
  
  <xalan:component prefix="orderReportToAddressMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrderReportToAddressMeta"/>
  </xalan:component>
  
  <xalan:component prefix="orderBillToAddressMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrderBillToAddressMeta"/>
  </xalan:component>
  
  <xalan:component prefix="orderShippingNoteMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrderShippingNoteMeta"/>
  </xalan:component>
  
  <xalan:component prefix="orderCustomerNoteMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrderCustomerNoteMeta"/>
  </xalan:component>
  
  <xalan:component prefix="orderItemInventoryItemMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrderItemInventoryItemMeta"/>
  </xalan:component>
  
  <xalan:component prefix="orderItemStoreMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrderItemStoreMeta"/>
  </xalan:component>
  
  <xsl:template match="doc"> 
      <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="VendorOrder" name="{resource:getString($constants,'vendorOrder')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<panel layout="horizontal" style="WhiteContentPanel" spacing="0" padding="0" xsi:type="Panel">
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
				</buttonPanel>
 			</widget>
		</panel>
		<!--end button panel-->

					<panel layout="vertical" xsi:type="Panel">

						<panel key="a" layout="vertical" xsi:type="Panel">
			<panel layout="horizontal" xsi:type="Panel">
							<panel key="secMod2" layout="table" style="Form" xsi:type="Table">
								<row>
									<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"orderNum")'/>:</text>
									</widget>
									<widget>
										<textbox case="lower" key="{orderMeta:id()}" width="75px" max="20" tab="{orderMeta:neededInDays()},{orderMeta:externalOrderNumber()}"/>
									</widget>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"neededDays")'/>:</text>
									</widget>
									<widget colspan="3">
										<textbox key="{orderMeta:neededInDays()}" width="75px" tab="{orderMeta:status()},{orderMeta:id()}"/>
									</widget>
								</row>
								<row>
								<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"status")'/>:</text>
									</widget>
									<widget>
										<autoDropdown key="{orderMeta:status()}" case="mixed" width="90px" popWidth="auto" tab="{orderMeta:organization()},{orderMeta:neededInDays()}">
													<widths>167</widths>
										</autoDropdown>
									</widget>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"vendor")'/>:</text>
									</widget>
									<widget colspan="3">
										<autoDropdown cat="organization" key="{orderMeta:organization()}" onchange="this" serviceUrl="OpenELISServlet?service=org.openelis.modules.order.server.OrderService" case="upper" width="172px" tab="{orderMeta:orderedDate()},{orderMeta:status()}">
										<headers>Name,Street,City,St</headers>
										<widths>180,110,100,20</widths>
										</autoDropdown>
										<query>
											<textbox case="upper" width="188px" tab="{orderMeta:orderedDate()},{orderMeta:status()}"/>
										</query>
									</widget>
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"orderDate")'/>:</text>
									</widget>
									<widget>
										<textbox key="{orderMeta:orderedDate()}" width="75px" tab="{orderMeta:requestedBy()},{orderMeta:organization()}"/>
									</widget>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"aptSuite")'/>:</text>
									</widget>
									<widget colspan="3">
										<textbox case="upper" key="{orderOrganizationAddressMeta:multipleUnit()}" width="188px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
									</widget>	
									
										
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"requestedBy")'/>:</text>
									</widget>
									<widget>
										<textbox key="{orderMeta:requestedBy()}" width="203px" tab="{orderMeta:costCenter()},{orderMeta:orderedDate()}"/>
									</widget>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"address")'/>:</text>
									</widget>
									<widget colspan="3">
										<textbox case="upper" key="{orderOrganizationAddressMeta:streetAddress()}" width="188px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
									</widget>			
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"costCenter")'/>:</text>
									</widget>
									<widget>
										<autoDropdown key="{orderMeta:costCenter()}" case="mixed" width="187px" popWidth="auto" tab="{orderMeta:externalOrderNumber()},{orderMeta:requestedBy()}">
													<widths>167</widths>
										</autoDropdown>
									</widget>
									
										<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"city")'/>:</text>
									</widget>
									<widget colspan="3">
										<textbox case="upper" key="{orderOrganizationAddressMeta:city()}" width="188px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
									</widget>
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"extOrderNum")'/>:</text>
									</widget>
									<widget>
										<textbox case="mixed" key="{orderMeta:externalOrderNumber()}" width="188px" max="20" tab="{orderMeta:id()},{orderMeta:costCenter()}"/>
									</widget>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"state")'/>:</text>
									</widget>
									<widget>
										<textbox case="mixed" key="{orderOrganizationAddressMeta:state()}" width="35px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
									</widget>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"zipcode")'/>:</text>
									</widget>
									<widget>
										<textbox case="mixed" key="{orderOrganizationAddressMeta:zipCode()}" width="65px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
									</widget>	
									
								</row>
								</panel>
								</panel>
								
								</panel>
<!-- tabbed panel needs to go here -->
				<panel height="200px" key="tabPanel" halign="center" layout="tab" xsi:type="Tab">
					<!-- TAB 1 (Components) -->
					<tab key="tab1" text="{resource:getString($constants,'items')}">
							<panel layout="vertical" spacing="0" padding="0" xsi:type="Panel" overflow="hidden">
							<widget valign="top">
								<table width="auto" key="itemsTable" manager="this" maxRows="9" title="" showError="false" showScroll="true">
										<headers><xsl:value-of select='resource:getString($constants,"quantity")'/>,<xsl:value-of select='resource:getString($constants,"inventoryItem")'/>,
										<xsl:value-of select='resource:getString($constants,"store")'/></headers>
										<widths>83,241,240</widths>
										<editors>
											<textbox case="mixed"/>
											<autoDropdown cat="inventoryItemWithStore" case="lower" serviceUrl="OpenELISServlet?service=org.openelis.modules.order.server.OrderService" width="210px">												
												<headers>Name,Store</headers>
												<widths>100,150</widths>
											</autoDropdown>
											<label/>
										</editors>
										<fields>
											<number key="{orderItemMeta:quantityRequested()}" type="integer" required="true"/>
											<dropdown key="{orderItemInventoryItemMeta:name()}" required="true"/>
											<string key="{orderItemStoreMeta:entry()}" required="false"/>
										</fields>
										<sorts>true,true,true</sorts>
										<filters>false,false,false</filters>
										<colAligns>left,left,left</colAligns>
									</table>
									<query>
									<queryTable width="auto" maxRows="9" title="" showError="false">
										<headers><xsl:value-of select='resource:getString($constants,"quantity")'/>,<xsl:value-of select='resource:getString($constants,"inventoryItem")'/>,
									<xsl:value-of select='resource:getString($constants,"store")'/></headers>
										<widths>83,250,249</widths>
										<editors>
											<textbox case="mixed"/>
											<textbox case="lower"/>
											<textbox case="mixed"/>
										</editors>
										<fields>
											<xsl:value-of select='orderItemMeta:quantityRequested()'/>,
											<xsl:value-of select='orderItemInventoryItemMeta:name()'/>,
											<xsl:value-of select='orderItemStoreMeta:entry()'/>
										</fields>								
									</queryTable>
									</query>
								</widget>

									<widget style="WhiteContentPanel" halign="center">
									<appButton action="removeItemRow" onclick="this" style="Button" key="removeItemButton">
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
					<!-- TAB 2 -->	
					<tab key="tab2" text="{resource:getString($constants,'receipt')}">
						<panel layout="vertical" spacing="0" padding="0" xsi:type="Panel" overflow="hidden">
							<widget valign="top">
								<table width="auto" key="receiptsTable" manager="InventoryLocationsTable" maxRows="10" title="" showError="false" showScroll="true">
										<headers>Date Rec, UPC, Qty, Cost, QC</headers>
										<widths>110,130,70,90,158</widths>
										<editors>
											<label/>
											<label/>
											<label/>
											<label/>
											<label/>
										</editors>
										<fields>
											<string/>
											<string/>
											<number type="integer"/>
											<number type="double"/>
											<string/>
										</fields>
										<sorts>true,true,true,true,true</sorts>
										<filters>false,false,false,false,false</filters>
										<colAligns>left,left,left,left,left</colAligns>
									</table>
								</widget>
								<widget>
									<panel xsi:type="Panel" layout="horizontal" height="10px"/>
    				            </widget>
							</panel>
					</tab>
					<!-- TAB 3 -->
					<tab key="tab3" text="{resource:getString($constants,'orderShippingNotes')}">
						<panel key="secMod3" layout="vertical" width="100%" height="229px" spacing="0" padding="0" xsi:type="Panel">
							<panel key="noteFormPanel" layout="table" style="Form" xsi:type="Table" padding="0" spacing="0">
										<row>
										<widget colspan="2" align="center">
										<appButton action="standardNoteShipping" onclick="this" key="standardNoteShippingButton" style="Button">
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
											<panel layout="horizontal" width="14px" xsi:type="Panel"/>
										</widget>
										<widget colspan="2">
										<textarea width="576px" height="179px" case="mixed" key="{orderShippingNoteMeta:text()}"/>
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
  	  <!-- values on the screen -->
  	  <number key="{orderMeta:id()}" type="integer" required="false"/>
      <number key="{orderMeta:neededInDays()}" type="integer" required="true"/>
      <dropdown key="{orderMeta:status()}" type="integer" required="true"/> 
      <dropdown key="{orderMeta:organization()}" required="true"/>
      <string key="{orderMeta:orderedDate()}" required="true"/>
      <string key="{orderMeta:requestedBy()}" required="true"/>
      <dropdown key="{orderMeta:costCenter()}" type="integer" required="false"/>
      <string key="{orderMeta:externalOrderNumber()}" required="false"/>
      <string key="{orderShippingNoteMeta:text()}" required="false"/>
      <table key="itemsTable"/>
      
      <!-- organization address-->
      <string key="{orderOrganizationAddressMeta:multipleUnit()}" required="false"/>
      <string key="{orderOrganizationAddressMeta:streetAddress()}" required="false"/>
      <string key="{orderOrganizationAddressMeta:city()}" required="false"/>
      <string key="{orderOrganizationAddressMeta:state()}" required="false"/>
      <string key="{orderOrganizationAddressMeta:zipCode()}" required="false"/>
            
      <string key="orderType" required="false"/>
      
      <!-- values not on this screen -->
	  <dropdown key="{orderMeta:reportTo()}" required="false"/>
      <dropdown key="{orderMeta:billTo()}" required="false"/>
      <string key="{orderCustomerNoteMeta:text()}" required="false"/>
      
      <!--report to address-->
      <string key="{orderReportToAddressMeta:multipleUnit()}" required="false"/>
      <string key="{orderReportToAddressMeta:streetAddress()}" required="false"/>
      <string key="{orderReportToAddressMeta:city()}" required="false"/>
      <string key="{orderReportToAddressMeta:state()}" required="false"/>
      <string key="{orderReportToAddressMeta:zipCode()}" required="false"/>
      
      <!--bill to address -->
      <string key="{orderBillToAddressMeta:multipleUnit()}" required="false"/>
      <string key="{orderBillToAddressMeta:streetAddress()}" required="false"/>
      <string key="{orderBillToAddressMeta:city()}" required="false"/>
      <string key="{orderBillToAddressMeta:state()}" required="false"/>
      <string key="{orderBillToAddressMeta:zipCode()}" required="false"/>
      
	</rpc>
	<rpc key="query">
	  <queryNumber key="{orderMeta:id()}" type="integer" required="false"/>
      <queryNumber key="{orderMeta:neededInDays()}" type="integer" required="false"/>
      <dropdown key="{orderMeta:status()}" type="integer" required="false"/> 
      <queryString key="{orderMeta:organization()}" required="false"/>
      <queryString key="{orderMeta:orderedDate()}" required="false"/>
      <queryString key="{orderMeta:requestedBy()}" required="false"/>
      <dropdown key="{orderMeta:costCenter()}" type="integer" required="false"/>
      <queryString key="{orderMeta:externalOrderNumber()}" required="false"/>
      
      <string key="orderType" required="false"/>
      
      <!-- order items table -->
      <table key="itemsTable"/>
      <queryNumber key="{orderItemMeta:quantityRequested()}" type="integer" required="false"/>
	  <queryString key="{orderItemInventoryItemMeta:name()}" required="false"/>
	  <queryString key="{orderItemStoreMeta:entry()}" required="false"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>