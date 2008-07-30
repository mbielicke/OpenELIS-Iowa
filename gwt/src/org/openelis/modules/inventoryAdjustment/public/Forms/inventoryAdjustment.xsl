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
                xmlns:meta="xalan://org.openelis.metamap.InventoryAdjustmentMetaMap" 
                xmlns:transAdjustmentLocationMeta="xalan://org.openelis.metamap.TransAdjustmentLocationMetaMap" 
                xmlns:inventoryLocationMeta="xalan://org.openelis.metamap.InventoryLocationMetaMap" 
                xmlns:inventoryItemMeta="xalan://org.openelis.meta.InventoryItemMeta" 
                xmlns:dictionaryMeta="xalan://org.openelis.meta.DictionaryMeta" 
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
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.InventoryAdjustmentMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="transAdjustmentLocationMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TransAdjustmentLocationMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="inventoryLocationMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.InventoryLocationMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="inventoryItemMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryItemMeta"/>
  </xalan:component>
  
  <xalan:component prefix="dictionaryMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.DictionaryMeta"/>
  </xalan:component>
  
  <xsl:template match="doc"> 
  <xsl:variable name="adj" select="meta:new()"/>
  <xsl:variable name="transAdjustmentLocation" select="meta:getTransAdjustmentLocation($adj)"/>
   <xsl:variable name="loc" select="transAdjustmentLocationMeta:getInventoryLocation($transAdjustmentLocation)"/>
   <xsl:variable name="invItem" select="inventoryLocationMeta:getInventoryItem($loc)"/>
   <xsl:variable name="dict" select="meta:getDictionary($adj)"/>
   <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
   <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
   <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="Organization" name="{resource:getString($constants,'inventoryAdjustment')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
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
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"adjustmentNum")'/>:</text>
						<textbox case="lower" key="{meta:getId($adj)}" width="75px" max="20" tab="{meta:getDescription($adj)},??"/>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"description")'/>:</text>
						<textbox case="lower" key="{meta:getDescription($adj)}" width="350px" max="20" tab="{meta:getAdjustmentDate($adj)},{meta:getId($adj)}"/>
					</row>
					<row>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"adjDate")'/>:</text>
						<textbox case="lower" key="{meta:getAdjustmentDate($adj)}" width="85px" max="20" tab="??,{meta:getDescription($adj)}"/>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"user")'/>:</text>
						<textbox case="lower" key="aa" width="125px" max="20" tab="{meta:getId($adj)},{meta:getAdjustmentDate($adj)}"/>
					</row>
				</TablePanel>
				<VerticalPanel spacing="0" padding="0" overflow="hidden">
					<widget valign="top">
						<table width="auto" key="adjustmentsTable" manager="this" maxRows="14" title="" showError="false" showScroll="true">
							<headers><xsl:value-of select='resource:getString($constants,"locationNum")'/>,<xsl:value-of select='resource:getString($constants,"inventoryItem")'/>,
							<xsl:value-of select='resource:getString($constants,"store")'/>, 
							<xsl:value-of select='resource:getString($constants,"storageLocation")'/>, <xsl:value-of select='resource:getString($constants,"onHand")'/>, 
							<xsl:value-of select='resource:getString($constants,"physCount")'/>, <xsl:value-of select='resource:getString($constants,"adjQuan")'/></headers>
							<widths>60,170,140,140,70,70,70</widths>										
							<editors>
								<textbox case="upper"/>
								<textbox case="upper"/>
								<textbox case="upper"/>
								<textbox case="upper"/>
								<textbox case="upper"/>
								<textbox case="upper"/>
							</editors>
							<fields>
								<string key="{meta:getId($org)}" required="false"/>
								<string key="{meta:getId($org)}" required="false"/>
								<string key="{meta:getId($org)}" required="false"/>
								<string key="{meta:getId($org)}" required="false"/>
								<string key="{meta:getId($org)}" required="false"/>
								<string key="{meta:getId($org)}" required="false"/>
								<string key="{meta:getId($org)}" required="false"/>
							</fields>
							<sorts>false,false,false,false,false,false</sorts>
							<filters>false,false,false,false,false,false</filters>
							<colAligns>left,left,left,left,left,left</colAligns>
						</table>
						<query>
							<queryTable width="auto" title="" maxRows="14" showError="false">
								<headers><xsl:value-of select='resource:getString($constants,"inventoryItem")'/>, <xsl:value-of select='resource:getString($constants,"store")'/>, 
							<xsl:value-of select='resource:getString($constants,"storageLocation")'/>, <xsl:value-of select='resource:getString($constants,"onHand")'/>, 
							<xsl:value-of select='resource:getString($constants,"physCount")'/>, <xsl:value-of select='resource:getString($constants,"adjQuan")'/></headers>
								<widths>179,179,170,70,70,70</widths>
								<editors>
									<textbox case="upper"/>
									<textbox case="upper"/>
									<textbox case="upper"/>
									<label/>
									<textbox case="mixed"/>		 	
								</editors>
								<fields><xsl:value-of select='inventoryLocationMeta:getId($loc)'/>,<xsl:value-of select='inventoryItemMeta:getName($invItem)'/>,<xsl:value-of select='dictionaryMeta:getEntry($dict)'/>,label1,
								<xsl:value-of select='inventoryLocationMeta:getQuantityOnhand($loc)'/>,<xsl:value-of select='transAdjustmentLocationMeta:getPhysicalCount($transAdjustmentLocation)'/>,
								<xsl:value-of select='transAdjustmentLocationMeta:getQuantity($transAdjustmentLocation)'/></fields>
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
				</VerticalPanel>				
		</HorizontalPanel>
	</display>
	<rpc key="display">
  	  <number key="{meta:getId($adj)}" type="integer" required="false"/>
      <string key="{meta:getDescription($adj)}" max="60" required="true"/>
      <string key="{meta:getAdjustmentDate($adj)}" required="true"/>
      <string key="{meta:getSystemUserId($adj)}" required="true"/>
      <table key="adjustmentsTable"/>
	</rpc>
	<rpc key="query">
  	  <queryNumber key="{meta:getId($adj)}" type="integer" required="false"/>
      <queryString key="{meta:getDescription($adj)}" required="false"/>
      <queryString key="{meta:getAdjustmentDate($adj)}" required="false"/>
      <queryString key="{meta:getSystemUserId($adj)}" required="false"/>

      <table key="adjustmentsTable"/>
      <queryNumber key="{inventoryLocationMeta:getId($loc)}" type="integer" required="false"/>
      <queryString key="{inventoryItemMeta:getName($invItem)}" required="false"/>
      <queryString key="{dictionaryMeta:getEntry($dict)}" required="false"/>
      <queryString key="label1" required="false"/>
      <queryNumber key="{inventoryLocationMeta:getQuantityOnhand($loc)}" type="integer" required="false"/>
      <queryNumber key="{transAdjustmentLocationMeta:getPhysicalCount($transAdjustmentLocation)" type="integer" required="false"/>
      <queryNumber key="{transAdjustmentLocationMeta:getQuantity($transAdjustmentLocation)}" type="integer" required="false"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>
