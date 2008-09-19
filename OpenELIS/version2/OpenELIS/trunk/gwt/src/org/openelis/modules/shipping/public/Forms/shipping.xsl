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
                xmlns:meta="xalan://org.openelis.metamap.ShippingMetaMap" 
                xmlns:orgMeta="xalan://org.openelis.metamap.OrganizationMetaMap"
                xmlns:addr="xalan://org.openelis.meta.AddressMeta"
                xmlns:trackingMeta="xalan://org.openelis.meta.ShippingTrackingMeta" 
                xmlns:shippingItemMeta="xalan://org.openelis.meta.ShippingItemMeta" 
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZTwoColumns.xsl"/> 

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>

  <xalan:component prefix="meta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.ShippingMetaMap"/>
  </xalan:component>
  <!--
  xmlns:orderMeta="xalan://org.openelis.metamap.OrderMetaMap" 
  <xalan:component prefix="orderMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.OrderMetaMap"/>
  </xalan:component>
  -->
  <xalan:component prefix="orgMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrganizationMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="addr">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.AddressMeta"/>
  </xalan:component>
  
  <xalan:component prefix="trackingMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.ShippingTrackingMeta"/>
  </xalan:component>
  
  <xalan:component prefix="shippingItemMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.ShippingItemMeta"/>
  </xalan:component>
  
  <xsl:template match="doc"> 
    <xsl:variable name="shipping" select="meta:new()"/>
<!--    <xsl:variable name="order" select="meta:getOrderMeta($shipping)"/>-->
    <xsl:variable name="organization" select="meta:getOrganizationMeta($shipping)"/>
    <xsl:variable name="orgAddress" select="orgMeta:getAddress($organization)"/>
    <xsl:variable name="tracking" select="meta:getTrackingMeta($shipping)"/>
    <xsl:variable name="items" select="meta:getShippingItemMeta($shipping)"/>
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="Storage" name="{resource:getString($constants,'shipping')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<HorizontalPanel spacing="0" padding="0" style="WhiteContentPanel">
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
				<TablePanel style="Form">
					<row>
						<HorizontalPanel style="FormVerticalSpacing"/>
					</row>
					<row>								
						<text style="Prompt"><xsl:value-of select="resource:getString($constants,'status')"/>:</text>
						<autoDropdown key="{meta:getStatusId($shipping)}" case="mixed" width="90px" popWidth="auto" tab="{meta:getShippedDate($shipping)},{meta:getShippedMethodId($shipping)}"/>
						<text style="Prompt"><xsl:value-of select="resource:getString($constants,'shippedDate')"/>:</text>
						<widget colspan="3">
							<calendar key="{meta:getShippedDate($shipping)}" width="80px" begin="0" end="2" tab="{meta:getNumberOfPackages($shipping)},{meta:getStatusId($shipping)}"/>						
						</widget>
					</row>
					<row>								
						<text style="Prompt"><xsl:value-of select="resource:getString($constants,'numPackages')"/>:</text>
						<textbox case="mixed" key="{meta:getNumberOfPackages($shipping)}" width="60px" tab="{meta:getCost($shipping)},{meta:getShippedDate($shipping)}"/>
						<text style="Prompt"><xsl:value-of select="resource:getString($constants,'cost')"/>:</text>
						<widget colspan="3">
							<textbox case="mixed" key="{meta:getCost($shipping)}" width="60px" tab="{meta:getShippedFromId($shipping)},{meta:getNumberOfPackages($shipping)}"/>
						</widget>
					</row>						
					<row>
						<text style="Prompt"><xsl:value-of select="resource:getString($constants,'shippedFrom')"/>:</text>
						<autoDropdown key="{meta:getShippedFromId($shipping)}" case="mixed" width="172px" popWidth="auto" tab="{orgMeta:getName($organization)},{meta:getCost($shipping)}"/>
						<text style="Prompt"><xsl:value-of select="resource:getString($constants,'shippedTo')"/>:</text>
						<widget colspan="3">
							<autoDropdown case="upper" cat="shippedTo" key="{orgMeta:getName($organization)}" onchange="this" serviceUrl="OpenELISServlet?service=org.openelis.modules.shipping.server.ShippingService" width="172px" tab="{meta:getProcessedDate($shipping)},{meta:getShippedFromId($shipping)}">
								<headers>Name,Street,City,St</headers>
								<widths>180,110,100,20</widths>
							</autoDropdown>
							<query>
								<textbox case="upper" width="198px"/>
							</query>
							
						</widget>
					</row>
					<row>								
						<text style="Prompt"><xsl:value-of select="resource:getString($constants,'processedDate')"/>:</text>
						<calendar key="{meta:getProcessedDate($shipping)}" width="80px" begin="0" end="2" tab="{meta:getProcessedById($shipping)},{orgMeta:getName($organization)}"/>		
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"aptSuite")'/>:</text>
						<widget colspan="3">
							<textbox case="upper" key="{addr:getMultipleUnit($orgAddress)}" width="199px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
						</widget>						
					</row>
					<row>
						<text style="Prompt"><xsl:value-of select="resource:getString($constants,'processedBy')"/>:</text>
						<textbox case="mixed" key="{meta:getProcessedById($shipping)}" width="203px" tab="{meta:getShippedMethodId($shipping)},{meta:getProcessedDate($shipping)}"/>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"address")'/>:</text>
						<widget colspan="3">
							<textbox case="upper" key="{addr:getStreetAddress($orgAddress)}" width="199px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
						</widget>	
					</row>
					<row>
						<text style="Prompt"><xsl:value-of select="resource:getString($constants,'shippedMethod')"/>:</text>
						<autoDropdown key="{meta:getShippedMethodId($shipping)}" case="mixed" width="140px" popWidth="auto" tab="{meta:getStatusId($shipping)},{meta:getProcessedById($shipping)}"/>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"city")'/>:</text>
						<widget colspan="3">
							<textbox case="upper" key="{addr:getCity($orgAddress)}" width="199px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
						</widget>
					</row>
					<row>								
						<widget colspan="2">
						<HorizontalPanel/>
						</widget>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"state")'/>:</text>
						<widget>
							<textbox case="mixed" key="{addr:getState($orgAddress)}" width="35px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
						</widget>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"zipcode")'/>:</text>
						<widget>
							<textbox case="mixed" key="{addr:getZipCode($orgAddress)}" width="65px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
						</widget>						
					</row>
					<row>	
						<widget colspan="3">
							<table key="itemsTable" manager="this" maxRows="6" showError="false" showScroll="true" title="" width="auto">
								<headers><xsl:value-of select="resource:getString($constants,'itemsShipped')"/></headers>
								<widths>395</widths>
								<editors>
									<label/>
								</editors>
								<fields>
									<string key="test" required="true"/>
								</fields>
								<sorts>false</sorts>
								<filters>false</filters>
								<colAligns>left</colAligns>
							</table>
						</widget>	
						<widget valign="top" colspan="3">						
						<table key="trackingNumbersTable" manager="this" maxRows="6" showError="false" showScroll="true" title="" width="auto">
							<headers><xsl:value-of select="resource:getString($constants,'trackingNums')"/></headers>
							<widths>180</widths>
							<editors>
								<textbox case="mixed"/>
							</editors>
							<fields>
								<string key="test" required="true"/>
							</fields>
							<sorts>false</sorts>
							<filters>false</filters>
							<colAligns>left</colAligns>
						</table>
						</widget>
					</row>
				</TablePanel>
				</VerticalPanel>
			</VerticalPanel>
		</HorizontalPanel>
	</display>
	<rpc key="display">
		<number key="{meta:getId($shipping)}" type="integer" required="false"/>
		<dropdown key="{meta:getStatusId($shipping)}" required="true"/>
		<number key="{meta:getNumberOfPackages($shipping)}" type="integer" required="true"/>
		<number key="{meta:getCost($shipping)}" type="double" required="false"/>
		<dropdown key="{meta:getShippedFromId($shipping)}" required="true"/>
		<dropdown key="{orgMeta:getName($organization)}" required="true"/>
		<date key="{meta:getProcessedDate($shipping)}" begin="0" end="2" required="false"/>
	    <string key="{meta:getProcessedById($shipping)}" required="false"/>
   	    <date key="{meta:getShippedDate($shipping)}" begin="0" end="2" required="false"/>
	    <dropdown key="{meta:getShippedMethodId($shipping)}" required="false"/>
	    <string key="{addr:getMultipleUnit($orgAddress)}" required="false"/>
		<string key="{addr:getStreetAddress($orgAddress)}" required="false"/>
	    <string key="{addr:getCity($orgAddress)}" required="false"/>
	    <string key="{addr:getState($orgAddress)}" required="false"/>
	    <string key="{addr:getZipCode($orgAddress)}" required="false"/>
        <number key="systemUserId" type="integer" required="false"/>

    	<table key="itemsTable"/>
	   	<table key="trackingNumbersTable"/>
	</rpc>
	<rpc key="query">
		<dropdown key="{meta:getStatusId($shipping)}"/>
		<queryNumber key="{meta:getNumberOfPackages($shipping)}" type="integer"/>
		<queryNumber key="{meta:getCost($shipping)}" type="double"/>
		<dropdown key="{meta:getShippedFromId($shipping)}"/>
		<queryString key="{orgMeta:getName($organization)}"/>
		<queryDate key="{meta:getProcessedDate($shipping)}" begin="0" end="2"/>
   	    <queryDate key="{meta:getShippedDate($shipping)}" begin="0" end="2"/>
	    <dropdown key="{meta:getShippedMethodId($shipping)}" required="false"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>
