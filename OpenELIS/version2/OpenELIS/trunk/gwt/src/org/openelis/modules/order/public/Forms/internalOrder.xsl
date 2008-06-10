<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale" 
                xmlns:orderMeta="xalan://org.openelis.meta.OrderMeta" 
                xmlns:orderItemMeta="xalan://org.openelis.meta.OrderItemMeta"
                xmlns:orderItemInventoryItemMeta="xalan://org.openelis.meta.OrderItemInventoryItemMeta"
                xmlns:orderOrganizationAddressMeta="xalan://org.openelis.meta.OrderOrganizationAddressMeta"
                xmlns:orderReportToAddressMeta="xalan://org.openelis.meta.OrderReportToAddressMeta"
                xmlns:orderBillToAddressMeta="xalan://org.openelis.meta.OrderBillToAddressMeta"
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
  
  <xalan:component prefix="orderItemInventoryItemMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrderItemInventoryItemMeta"/>
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
  
  <xalan:component prefix="orderItemStoreMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrderItemStoreMeta"/>
  </xalan:component>
  
  <xsl:template match="doc"> 
      <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="KitOrder" name="{resource:getString($constants,'internalOrder')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
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
										<textbox case="lower" key="{orderMeta:id()}" width="75px" max="20" tab="{orderMeta:neededInDays()},{orderMeta:costCenter()}"/>
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
										<autoDropdown key="{orderMeta:status()}" case="mixed" width="90px" popWidth="auto" tab="{orderMeta:requestedBy()},{orderMeta:neededInDays()}">
													<widths>167</widths>
										</autoDropdown>
									</widget>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"requestedBy")'/>:</text>
									</widget>
									<widget>
										<textbox key="{orderMeta:requestedBy()}" width="175px" tab="{orderMeta:orderedDate()},{orderMeta:status()}"/>
									</widget>	
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"orderDate")'/>:</text>
									</widget>
									<widget>
										<textbox key="{orderMeta:orderedDate()}" width="75px" tab="{orderMeta:costCenter()},{orderMeta:requestedBy()}"/>
									</widget>
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"costCenter")'/>:</text>
									</widget>
									<widget>
										<autoDropdown key="{orderMeta:costCenter()}" case="mixed" width="187px" popWidth="auto" tab="{orderMeta:id()},{orderMeta:requestedBy()}">
													<widths>167</widths>
										</autoDropdown>
									</widget>									
								</row>
								</panel>
								</panel>
								
								</panel>

				<panel height="10px" layout="vertical"  xsi:type="Panel"/>
					
					<panel layout="vertical"  spacing="3" xsi:type="Panel">
						<widget>
							<table width="auto" key="itemsTable" manager="this" maxRows="9" title="" showError="false" showScroll="true">
									<headers><xsl:value-of select='resource:getString($constants,"quantity")'/>,<xsl:value-of select='resource:getString($constants,"inventoryItem")'/>,
									<xsl:value-of select='resource:getString($constants,"store")'/>,<xsl:value-of select='resource:getString($constants,"location")'/></headers>
										<widths>83,160,159,159</widths>
										<editors>
											<textbox case="mixed"/>
											<autoDropdown cat="inventoryItemWithStoreAndLoc" case="lower" serviceUrl="OpenELISServlet?service=org.openelis.modules.order.server.OrderService" width="130px">												
												<headers>Name,Store,Location, Qty</headers>
												<widths>100,150,150,40</widths>
											</autoDropdown>
											<label/>
											<label/>
										</editors>
										<fields>
											<number key="{orderItemMeta:quantityRequested()}" type="integer" required="true"/>
											<dropdown key="{orderItemInventoryItemMeta:name()}" required="true"/>
											<string key="{orderItemStoreMeta:entry()}" required="false"/>
											<string key="location" required="false"/>
										</fields>
										<sorts>true,true,true,true</sorts>
										<filters>false,false,false,false</filters>
										<colAligns>left,left,left,left</colAligns>
									</table>
									<query>
									<queryTable width="auto" maxRows="9" title="" showError="false">
										<headers><xsl:value-of select='resource:getString($constants,"quantity")'/>,<xsl:value-of select='resource:getString($constants,"inventoryItem")'/>,
									<xsl:value-of select='resource:getString($constants,"store")'/>,<xsl:value-of select='resource:getString($constants,"location")'/></headers>
										<widths>83,178,159,159</widths>
										<editors>
											<textbox case="mixed"/>
											<textbox case="lower"/>
											<textbox case="mixed"/>
											<label/>
										</editors>
										<fields>
											<xsl:value-of select='orderItemMeta:quantityRequested()'/>,
											<xsl:value-of select='orderItemInventoryItemMeta:name()'/>,
											<xsl:value-of select='orderItemStoreMeta:entry()'/>,
											label1
										</fields>							
									</queryTable>
									</query>
								</widget>							                
									<widget halign="center">
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
				</panel>
			</panel>
		</panel>
	</display>
	<rpc key="display">
	  <!-- values on the screen -->
  	  <number key="{orderMeta:id()}" type="integer" required="false"/>
      <number key="{orderMeta:neededInDays()}" type="integer" required="true"/>
      <dropdown key="{orderMeta:status()}" type="integer" required="true"/>  
      <string key="{orderMeta:orderedDate()}" required="true"/>
      <string key="{orderMeta:requestedBy()}" required="true"/>
      <dropdown key="{orderMeta:costCenter()}" type="integer" required="false"/>
      <table key="itemsTable"/>
            
      <!-- defaulted values -->
      <!--<string key="{orderMeta:isExternal()}" required="false">N</string>-->
      <string key="orderType" required="false"/>
      
      <!-- values not on this screen -->
      <dropdown key="{orderMeta:organization()}" required="false"/>
      <string key="{orderMeta:externalOrderNumber()}" required="false"/>
	  <dropdown key="{orderMeta:reportTo()}" required="false"/>
      <dropdown key="{orderMeta:billTo()}" required="false"/>

      <!-- organization address-->
      <string key="{orderOrganizationAddressMeta:multipleUnit()}" required="false"/>
      <string key="{orderOrganizationAddressMeta:streetAddress()}" required="false"/>
      <string key="{orderOrganizationAddressMeta:city()}" required="false"/>
      <string key="{orderOrganizationAddressMeta:state()}" required="false"/>
      <string key="{orderOrganizationAddressMeta:zipCode()}" required="false"/>
            
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
      <queryString key="{orderMeta:orderedDate()}" required="false"/>
      <queryString key="{orderMeta:requestedBy()}" required="false"/>
      <dropdown key="{orderMeta:costCenter()}" type="integer" required="false"/>
      
      <string key="orderType" required="false"/>

	  <!-- order items table -->
	  <table key="itemsTable"/>
      <queryNumber key="{orderItemMeta:quantityRequested()}" type="integer" required="false"/>
	  <queryString key="{orderItemInventoryItemMeta:name()}" required="false"/>
	  <queryString key="{orderItemStoreMeta:entry()}" required="false"/>
      <queryString key="label1" required="false"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>
