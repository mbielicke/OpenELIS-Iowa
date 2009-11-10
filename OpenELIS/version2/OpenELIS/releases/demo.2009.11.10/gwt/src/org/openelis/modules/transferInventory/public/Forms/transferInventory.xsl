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
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="button.xsl"/>

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
  <xsl:template match="doc"> 
     <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
     <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
   <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="TransferInventory" name="{resource:getString($constants,'transferInventory')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<VerticalPanel style="WhiteContentPanel" spacing="0" padding="0">
			<TablePanel style="Form">
				<row>
					<text style="Prompt"><xsl:value-of select="resource:getString($constants,'qty')"/>:</text>
					<textbox key="qty" tab="??,??" width="75px" alwaysEnabled="true"/>
				</row>
				</TablePanel>
				<HorizontalPanel style="form">
					<titledPanel key="borderedPanel">
						<legend><text style="LegendTitle"><xsl:value-of select='resource:getString($constants,"from")'/></text></legend>
						<content>
							<TablePanel style="Form">
							<row>
								<text style="Prompt"><xsl:value-of select='resource:getString($constants,"item")'/>:</text>
								<autoComplete key="fromItemId" cat="inventoryItemWithStoreAndLoc" case="lower" serviceUrl="OpenELISServlet?service=org.openelis.modules.order.server.OrderService" width="225px" alwaysEnabled="true" tab="??,??">												
									<headers>Name,Store,Location,Lot #, Exp Date,Qty</headers>
									<widths>135,110,160,70,70,30</widths>
								</autoComplete>
							</row>
							<row>
								<text style="Prompt"><xsl:value-of select='resource:getString($constants,"store")'/>:</text>
								<textbox key="fromStoreId" case="mixed" width="225px" tab="??,??"/>
								<!--<dropdown key="fromStoreId" case="mixed" width="225px" tab="??,??"/>-->
							</row>
							<row>
								<text style="Prompt"><xsl:value-of select='resource:getString($constants,"location")'/>:</text>
								<textbox key="fromLocationId" case="mixed" width="225px" tab="??,??"/>
								<!--<autoComplete key="fromLocationId" cat="location" autoCall="this" serviceUrl="OpenELISServlet?service=org.openelis.modules.inventoryReceipt.server.InventoryReceiptService" case="mixed" width="225px" tab="??,??">
									<headers>Desc</headers>
									<widths>300</widths>
								</autoComplete>-->
							</row>
							</TablePanel>
						</content>
					</titledPanel>
					<titledPanel key="borderedPanel">
						<legend><text style="LegendTitle"><xsl:value-of select='resource:getString($constants,"to")'/></text></legend>
						<content>
							<TablePanel style="Form">
							<row>
								<text style="Prompt"><xsl:value-of select='resource:getString($constants,"item")'/>:</text>
								<autoComplete key="toItemId" cat="inventoryItemWithStoreAndLoc" case="lower" serviceUrl="OpenELISServlet?service=org.openelis.modules.order.server.OrderService" width="225px" alwaysEnabled="true">												
									<headers>Name,Store,Location,Lot #, Exp Date,Qty</headers>
									<widths>135,110,160,70,70,30</widths>
								</autoComplete>
							</row>
							<row>
								<text style="Prompt"><xsl:value-of select='resource:getString($constants,"store")'/>:</text>
								<textbox key="toStoreId" case="mixed" width="225px" tab="??,??"/>
							    <!--<dropdown key="toStoreId" case="mixed" width="225px" tab="??,??"/>-->
							</row>
							<row>
								<text style="Prompt"><xsl:value-of select='resource:getString($constants,"location")'/>:</text>
								<textbox key="fromLocationId" case="mixed" width="225px" tab="??,??"/>								
								<!--<autoComplete key="toLocationId" cat="location" autoCall="this" serviceUrl="OpenELISServlet?service=org.openelis.modules.inventoryReceipt.server.InventoryReceiptService" case="mixed" width="225px" tab="??,??">
									<headers>Desc</headers>
									<widths>300</widths>
								</autoComplete>-->
							</row>
							</TablePanel>
						</content>
					</titledPanel>
				</HorizontalPanel>
				<VerticalPanel height="10px"/>
		<!--button panel code-->
		<AbsolutePanel spacing="0" style="ButtonPanelContainer" align="center">
    			<buttonPanel key="buttons">
    			<xsl:call-template name="popupTransferButton">
						<xsl:with-param name="language">
							<xsl:value-of select="language"/>
						</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="popupCancelButton">
						<xsl:with-param name="language">
							<xsl:value-of select="language"/>
						</xsl:with-param>
					</xsl:call-template>
				</buttonPanel>
		</AbsolutePanel>
		<!--end button panel-->
		</VerticalPanel>
	</display>
	<rpc key="display">
		<number key="qty" type="integer" required="true"/>
		<dropdown key="fromItemId" required="true"/>
		<string key="fromStoreId" required="true"/>
		<string key="fromLocationId" required="true"/>
		<dropdown key="toItemId" required="true"/>
		<string key="fromStoreId" required="true"/>
		<string key="fromLocationId" required="true"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>
