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
                xmlns:meta="xalan://org.openelis.metamap.ShippingMetaMap" 
                xmlns:orgMeta="xalan://org.openelis.metamap.OrganizationMetaMap"
                xmlns:addr="xalan://org.openelis.meta.AddressMeta"
                xmlns:trackingMeta="xalan://org.openelis.meta.ShippingTrackingMeta" 
                xmlns:shippingItemMeta="xalan://org.openelis.meta.ShippingItemMeta" 
                xmlns:noteMeta="xalan://org.openelis.meta.NoteMeta" 
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
  
  <xalan:component prefix="noteMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.NoteMeta"/>
  </xalan:component>

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
    <xsl:variable name="organization" select="meta:getOrganizationMeta($shipping)"/>
    <xsl:variable name="orgAddress" select="orgMeta:getAddress($organization)"/>
    <xsl:variable name="tracking" select="meta:getTrackingMeta($shipping)"/>
    <xsl:variable name="items" select="meta:getShippingItemMeta($shipping)"/>
    <xsl:variable name="note" select="meta:getNoteMeta($shipping)"/>
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="Storage" name="{resource:getString($constants,'shipping')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
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
			<VerticalPanel spacing="0" padding="0" style="WhiteContentPanel">
				<TablePanel style="Form">
					<row>								
						<text style="Prompt"><xsl:value-of select="resource:getString($constants,'status')"/>:</text>
						<dropdown key="{meta:getStatusId($shipping)}" case="mixed" width="90px" popWidth="auto" tab="{meta:getShippedDate($shipping)},{meta:getShippedMethodId($shipping)}"/>
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
						<dropdown key="{meta:getShippedFromId($shipping)}" case="mixed" width="172px" popWidth="auto" tab="{orgMeta:getName($organization)},{meta:getCost($shipping)}"/>
						<text style="Prompt"><xsl:value-of select="resource:getString($constants,'shippedTo')"/>:</text>
						<widget colspan="3">
							<autoComplete case="upper" cat="shippedTo" key="{orgMeta:getName($organization)}" onchange="this" serviceUrl="OpenELISServlet?service=org.openelis.modules.shipping.server.ShippingService" width="188px" tab="{meta:getProcessedDate($shipping)},{meta:getShippedFromId($shipping)}">
								<headers>Name,Street,City,St</headers>
								<widths>180,110,100,20</widths>
							</autoComplete>
							<query>
								<textbox case="upper" width="188px"/>
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
						<dropdown key="{meta:getShippedMethodId($shipping)}" case="mixed" width="140px" popWidth="auto" tab="{meta:getStatusId($shipping)},{meta:getProcessedById($shipping)}"/>
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
					</TablePanel>
					<TabPanel height="200px" key="shippingTabPanel" halign="center">
						<tab key="itemsTab" text="{resource:getString($constants,'items')}">
					<HorizontalPanel height="200px" width="625px">
						<widget>
							<table key="itemsTable" manager="this" maxRows="8" showError="false" showScroll="ALWAYS" title="" width="auto">
								<headers><xsl:value-of select="resource:getString($constants,'qty')"/>,<xsl:value-of select="resource:getString($constants,'item')"/></headers>
								<widths>50,300</widths>
								<editors>
									<label/>
									<label/>
								</editors>
								<fields>
									<integer required="true"/>
									<string required="true"/>
								</fields>
								<sorts>false,false</sorts>
								<filters>false,false</filters>
								<colAligns>left,left</colAligns>
							</table>
						</widget>	
						<HorizontalPanel width="5px"/>
						<VerticalPanel>
						<widget>						
						<table key="trackingNumbersTable" manager="this" maxRows="6" showError="false" showScroll="ALWAYS" title="" width="auto">
							<headers><xsl:value-of select="resource:getString($constants,'trackingNums')"/></headers>
							<widths>180</widths>
							<editors>
								<textbox case="mixed" max="30"/>
							</editors>
							<fields>
								<string key="{trackingMeta:getTrackingNumber($tracking)}" required="true"/>
							</fields>
							<sorts>false</sorts>
							<filters>false</filters>
							<colAligns>left</colAligns>
						</table>
						<!--
						<query>
							<queryTable maxRows="6" showError="false" title="" width="auto" showScroll="ALWAYS">
								<headers><xsl:value-of select="resource:getString($constants,'trackingNums')"/></headers>
								<widths>180</widths>
								<editors>
									<textbox case="mixed"/>
								</editors>
								<fields>
									<xsl:value-of select="trackingMeta:getTrackingNumber($tracking)"/>
								</fields>
							</queryTable>
						</query>
						-->
						</widget>
						<widget halign="center" style="WhiteContentPanel">
							<appButton action="removeRow" key="removeRowButton" onclick="this" style="Button">
								<HorizontalPanel>
									<AbsolutePanel style="RemoveRowButtonImage"/>
									<text><xsl:value-of select="resource:getString($constants,'removeRow')"/></text>
								</HorizontalPanel>
							</appButton>
						</widget>
						</VerticalPanel>
					</HorizontalPanel>
					</tab>
					<tab key="orderNotesTab" text="{resource:getString($constants,'orderShippingNotes')}">
						<VerticalPanel width="625px" height="200px" spacing="0" padding="0">
							<TablePanel key="noteFormPanel" style="Form" padding="0" spacing="0">
							<row>
							<HorizontalPanel height="15px"/>
							</row>
								<row>
									<HorizontalPanel width="14px"/>
									<widget>
										<textarea width="576px" height="155px" case="mixed" key="{noteMeta:getText($note)}"/>
									</widget>
								</row>
							</TablePanel>
						</VerticalPanel>
					</tab>
					</TabPanel>
				</VerticalPanel>
			</VerticalPanel>
	</display>
	<rpc key="display">
		<integer key="{meta:getId($shipping)}" required="false"/>
		<dropdown key="{meta:getStatusId($shipping)}" required="true"/>
		<integer key="{meta:getNumberOfPackages($shipping)}" required="true"/>
		<double key="{meta:getCost($shipping)}" required="false"/>
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
        <integer key="systemUserId" required="false"/>
		
		<rpc key="shippingItems">
    		<table key="itemsTable"/>
	   		<table key="trackingNumbersTable"/>
	   	</rpc>
	   	
	   	<rpc key="orderShippingNotes">
			<string key="{noteMeta:getText($note)}" required="false"/>	   	
	   	</rpc>
	   	
 		<string key="shippingTabPanel" reset="false">itemsTab</string>  
 		<model key="unlockModel"/>
	</rpc>
	<!--
	<rpc key="query">
		<dropdown key="{meta:getStatusId($shipping)}"/>
		<queryNumber key="{meta:getNumberOfPackages($shipping)}" type="integer"/>
		<queryNumber key="{meta:getCost($shipping)}" type="double"/>
		<dropdown key="{meta:getShippedFromId($shipping)}"/>
		<queryString key="{orgMeta:getName($organization)}"/>
		<queryDate key="{meta:getProcessedDate($shipping)}" begin="0" end="2"/>
   	    <queryDate key="{meta:getShippedDate($shipping)}" begin="0" end="2"/>
	    <dropdown key="{meta:getShippedMethodId($shipping)}" required="false"/>
	    
	    <table key="trackingNumbersTable"/>
	    <queryString key="{trackingMeta:getTrackingNumber($tracking)}" required="false"/>
	</rpc>
	-->
</screen>
  </xsl:template>
</xsl:stylesheet>
