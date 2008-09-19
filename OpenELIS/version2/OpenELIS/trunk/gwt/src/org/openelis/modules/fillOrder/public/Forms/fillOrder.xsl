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
                xmlns:meta="xalan://org.openelis.metamap.FillOrderMetaMap" 
                xmlns:orderItemMeta="xalan://org.openelis.metamap.OrderItemMetaMap"
                xmlns:orgMeta="xalan://org.openelis.metamap.OrderOrganizationMetaMap"
                xmlns:addrMeta="xalan://org.openelis.meta.AddressMeta"
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
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.FillOrderMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="orderItemMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.OrderItemMetaMap"/>
  </xalan:component>

  <xalan:component prefix="orgMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.OrderOrganizationMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="addrMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.AddressMeta"/>
  </xalan:component>
  
  
  <xsl:template match="doc"> 
    <xsl:variable name="order" select="meta:new()"/>
    <xsl:variable name="orderItem" select="meta:getOrderItem($order)"/>
    <xsl:variable name="org" select="meta:getOrderOrganization($order)"/>
    <xsl:variable name="address" select="orgMeta:getAddress($org)"/>
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="FillOrder" name="{resource:getString($constants,'fillOrder')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
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
    			<xsl:call-template name="buttonPanelDivider"/>
    			<xsl:call-template name="processButton">
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
				<VerticalPanel spacing="0" padding="0">
			<widget valign="top">
						<table width="auto" key="fillItemsTable" manager="this" maxRows="10" title="" showError="false" showScroll="true">
							<headers> ,<xsl:value-of select='resource:getString($constants,"ordNum")'/>,<xsl:value-of select='resource:getString($constants,"status")'/>,
							<xsl:value-of select='resource:getString($constants,"orderDate")'/>,<xsl:value-of select='resource:getString($constants,"shipFrom")'/>,
							<xsl:value-of select='resource:getString($constants,"shipTo")'/>,<xsl:value-of select='resource:getString($constants,"description")'/>,
							<xsl:value-of select='resource:getString($constants,"neededNumDays")'/>,<xsl:value-of select='resource:getString($constants,"numDaysLeft")'/></headers>
							<widths>20,45,90,65,120,150,145,60,60</widths>										
							<editors>
								<check onClick="this"/>
								<textbox case="mixed"/>
								<autoDropdown case="mixed" width="70px"/>
								<calendar begin="0" end="2"/>							
								<autoDropdown case="mixed" width="100px"/>
								<autoDropdown case="upper" width="130px"/>
								<textbox case="mixed"/>
								<textbox case="mixed"/>
								<textbox case="mixed"/>								
							</editors>
							<fields>
								<check key="process" type="integer" required="false"/>
								<string key="{meta:getId($order)}" required="true"/>
								<dropdown key="{meta:getStatusId($order)}" required="false"/>
								<date key="{meta:getOrderedDate($order)}" begin="0" end="2" required="false"/>
								<dropdown key="{meta:getShipFromId($order)}" required="false"/>
								<dropdown key="{orgMeta:getName($org)}" required="false"/>
								<string key="{meta:getDescription($order)}" required="false"/>
								<number key="{meta:getNeededInDays($order)}" type="integer" required="false"/>
								<number key="daysLeft" type="integer" required="false"/>
							</fields>
							<sorts>false,false,true,false,true,true,true,false,false</sorts>
							<filters>false,false,false,false,false,false,false,false,false</filters>
							<colAligns>left,left,left,left,left,left,left,left,left,left,left</colAligns>
						</table>
						<query>
							<queryTable width="auto" title="" maxRows="10" showError="false" showScroll="true">
							<headers> ,<xsl:value-of select='resource:getString($constants,"ordNum")'/>,<xsl:value-of select='resource:getString($constants,"status")'/>,
							<xsl:value-of select='resource:getString($constants,"orderDate")'/>,<xsl:value-of select='resource:getString($constants,"shipFrom")'/>,
							<xsl:value-of select='resource:getString($constants,"shipTo")'/>,<xsl:value-of select='resource:getString($constants,"description")'/>,
							<xsl:value-of select='resource:getString($constants,"neededNumDays")'/>,<xsl:value-of select='resource:getString($constants,"numDaysLeft")'/></headers>
							<widths>20,45,90,65,120,150,145,60,60</widths>			
								<editors>
									<label/>
									<textbox case="mixed"/>
									<autoDropdown case="mixed" width="70px"/>
									<textbox case="mixed"/>
									<autoDropdown case="mixed" width="100px"/>
									<textbox case="mixed"/>
									<textbox case="mixed"/>
									<textbox case="mixed"/>
									<textbox case="mixed"/> 	
								</editors>
								<fields>
								process,<xsl:value-of select='meta:getId($order)'/>,<xsl:value-of select='meta:getStatusId($order)'/>,
								<xsl:value-of select='meta:getOrderedDate($order)'/>,<xsl:value-of select='meta:getShipFromId($order)'/>,
								<xsl:value-of select='orgMeta:getName($org)'/>,<xsl:value-of select='meta:getDescription($order)'/>,
								<xsl:value-of select='meta:getNeededInDays($order)'/>,daysLeft
								</fields>
							</queryTable>
							</query>
						</widget>
							</VerticalPanel>
						
				</VerticalPanel>
			
			<HorizontalPanel>
				<VerticalPanel style="Form">
					<titledPanel key="borderedPanel">
						<legend><text style="LegendTitle"><xsl:value-of select='resource:getString($constants,"shipToAddress")'/></text></legend>
							<content>
								<TablePanel style="Form">
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"requestedBy")'/>:</text>
									<widget colspan="3">
										<textbox key="{meta:getRequestedBy($order)}" width="203px" alwaysDisabled="true"/>
									</widget>		
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"costCenter")'/>:</text>
									<widget colspan="3">
										<autoDropdown key="{meta:getCostCenterId($order)}" case="mixed" width="164px" alwaysDisabled="true"/>
									</widget>		
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"aptSuite")'/>:</text>
									<widget colspan="3">
										<textbox case="upper" key="{addrMeta:getMultipleUnit($address)}" width="180px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
									</widget>		
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"address")'/>:</text>
									<widget colspan="3">
										<textbox case="upper" key="{addrMeta:getStreetAddress($address)}" width="180px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
									</widget>		
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"city")'/>:</text>
									<widget colspan="3">
										<textbox case="upper" key="{addrMeta:getCity($address)}" width="180px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
									</widget>		
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"state")'/>:</text>
									<widget>
										<textbox case="upper" key="{addrMeta:getState($address)}" width="30px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
									</widget>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"zipcode")'/>:</text>
									<widget>
										<textbox case="upper" key="{addrMeta:getZipCode($address)}" width="60px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true"/>
									</widget>
								</row>
							</TablePanel>
						</content>
						</titledPanel>
					</VerticalPanel>
					<HorizontalPanel width="15px"/>
					<VerticalPanel>
					<VerticalPanel height="5px"/>
						<table width="auto" key="orderItemsTable" manager="this" maxRows="7" title="" showError="false" showScroll="true">
							<headers>Item/Sample,Qty,Location</headers>
							<widths>150,40,135</widths>										
							<editors>
								<label/>
								<textbox case="mixed"/>
								<label/>
							</editors>
							<fields>
								<string required="false"/>
								<number type="integer" required="false"/>
								<string required="false"/>
							</fields>
							<sorts>false,false,false</sorts>
							<filters>false,false,false</filters>
							<colAligns>left,left,left</colAligns>
						</table>
						</VerticalPanel>
						<VerticalPanel>
						<VerticalPanel height="10px"/>
						<appButton action="package" key="packageButton" onclick="this" style="Button">
							<HorizontalPanel>
								<text><xsl:value-of select="resource:getString($constants,'package')"/></text>
							</HorizontalPanel>
						</appButton>
					</VerticalPanel>
					</HorizontalPanel>
					</VerticalPanel>				
		</HorizontalPanel>
	</display>
	<rpc key="display">
		<table key="fillItemsTable"/>
		<table key="orderItemsTable"/>
		<string key="{meta:getRequestedBy($order)}" required="true"/>
		<dropdown key="{meta:getCostCenterId($order)}" required="false"/>
  		<string key="{addrMeta:getMultipleUnit($address)}" required="false"/>
		<string key="{addrMeta:getStreetAddress($address)}" required="false"/>
		<string key="{addrMeta:getCity($address)}" required="false"/>
		<string key="{addrMeta:getState($address)}" required="false"/>
		<string key="{addrMeta:getZipCode($address)}" required="false"/>
		
	</rpc>
	
	<rpc key="query">
    	<table key="fillItemsTable"/>
    	<queryString key="process" required="false"/>
    	<queryNumber key="{meta:getId($order)}" type="integer" required="false"/>
    	<dropdown key="{meta:getStatusId($order)}" required="false"/>
		<queryDate key="{meta:getOrderedDate($order)}" begin="0" end="2" required="false"/>
		<dropdown key="{meta:getShipFromId($order)}" required="false"/>
		<queryString key="{orgMeta:getName($org)}" required="false"/>
		<queryString key="{meta:getDescription($order)}" required="false"/>
		<queryNumber key="{meta:getNeededInDays($order)}" type="integer" required="false"/>
	 	<queryString key="daysLeft" type="integer" required="false"/> 
	 	
	 	<queryString key="{meta:getRequestedBy($order)}" required="false"/>
		<dropdown key="{meta:getCostCenterId($order)}" required="false"/>	
    </rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>
