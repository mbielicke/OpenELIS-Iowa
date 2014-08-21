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
                xmlns:storageLocationMeta="xalan://org.openelis.metamap.StorageLocationMetaMap"
                xmlns:storageUnitMeta="xalan://org.openelis.meta.StorageUnitMeta"               
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZOneColumn.xsl"/>

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
  <xalan:component prefix="storageLocationMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.StorageLocationMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="storageUnitMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.StorageUnitMeta"/>
  </xalan:component>
  
  <xsl:template match="doc"> 
    <xsl:variable name="storageLoc" select="storageLocationMeta:new()"/>
    <xsl:variable name="storageUnit" select="storageLocationMeta:getStorageUnit($storageLoc)"/>
    <xsl:variable name="storageLocChild" select="storageLocationMeta:getChildStorageLocation($storageLoc)"/>
    <xsl:variable name="storageLocChildUnit" select="storageLocationMeta:getStorageUnit($storageLocChild)"/>
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="Storage" name="{resource:getString($constants,'storageLocation')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<HorizontalPanel spacing="0" padding="0">
			<!--left table goes here -->
			<CollapsePanel key="collapsePanel" height="440px" style="LeftSidePanel">
				<azTable width="100%" key="azTable" maxRows="19" headers="{resource:getString($constants,'name')}" tablewidth="auto" colwidths="175">
			         <buttonPanel key="atozButtons">
	    			   <xsl:call-template name="aToZLeftPanelButtons"/>		
		    		 </buttonPanel>
				</azTable>
				</CollapsePanel>
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
					<xsl:call-template name="deleteButton">
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
						<HorizontalPanel style="FormVerticalSpacing"/>
					</row>
					<row>								
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"name")'/>:</text>
						<textbox case="lower" key="{storageLocationMeta:getName($storageLoc)}" width="150px" max="20" tab="{storageLocationMeta:getLocation($storageLoc)},{storageLocationMeta:getIsAvailable($storageLoc)}"/>
					</row>
					<row>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"location")'/>:</text>
						<textbox case="mixed" key="{storageLocationMeta:getLocation($storageLoc)}" max="80" width="395px" tab="{storageUnitMeta:getDescription($storageUnit)},{storageLocationMeta:getName($storageLoc)}"/>
					</row>
					<row>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"storageUnit")'/>:</text>
						<widget>
							<autoComplete cat="storageUnit" key="{storageUnitMeta:getDescription($storageUnit)}" case="lower" serviceUrl="OpenELISServlet?service=org.openelis.modules.storage.server.StorageLocationService"  width="366px" tab="{storageLocationMeta:getIsAvailable($storageLoc)},{storageLocationMeta:getLocation($storageLoc)}">
								<headers>Desc,Category</headers>
								<widths>267,90</widths>	
							</autoComplete>
							<query>
								<textbox case="lower" width="366px" tab="{storageLocationMeta:getIsAvailable($storageLoc)},{storageLocationMeta:getLocation($storageLoc)}"/>
							</query>
						</widget>	
					</row>
					<row>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"isAvailable")'/>:</text>
						<check key="{storageLocationMeta:getIsAvailable($storageLoc)}" tab="{storageLocationMeta:getName($storageLoc)},{storageUnitMeta:getDescription($storageUnit)}"/>
					</row>
				</TablePanel>
				<VerticalPanel height="10px"/>
					<VerticalPanel spacing="3">
						<widget>
							<table width="auto" key="childStorageLocsTable" maxRows="11" title="" showError="false" showScroll="ALWAYS">
										<headers><xsl:value-of select='resource:getString($constants,"storageUnit")'/>,<xsl:value-of select='resource:getString($constants,"location")'/>,
										<xsl:value-of select='resource:getString($constants,"isAvailable")'/></headers>
										<widths>225,275,80</widths>
										<editors>
											<autoComplete cat="storageUnit" case="lower" serviceUrl="OpenELISServlet?service=org.openelis.modules.storage.server.StorageLocationService" width="150px">
												<headers>Desc,Category</headers>
												<widths>180,70</widths>
											</autoComplete>
											<textbox case="mixed" max="80"/>
											<check/>
										</editors>
										<fields>
											<dropdown key="{storageUnitMeta:getDescription($storageLocChildUnit)}" required="true"/>
											<string key="{storageLocationMeta:getLocation($storageLocChild)}" required="true"/>
											<check key="{storageLocationMeta:getIsAvailable($storageLocChild)}">Y</check>
										</fields>
										<sorts>true,true,true</sorts>
										<filters>false,false ,false</filters>
										<colAligns>left,left,left</colAligns>
									</table>
									<query>
									<queryTable width="auto" maxRows="11" title="" showError="false" showScroll="ALWAYS">
										<headers><xsl:value-of select='resource:getString($constants,"storageUnit")'/>,<xsl:value-of select='resource:getString($constants,"location")'/>,
										<xsl:value-of select='resource:getString($constants,"isAvailable")'/></headers>
										<widths>225,275,80</widths>
										<editors>
											<textbox case="lower"/>
											<textbox case="mixed"/>
											<check threeState="true"/>
										</editors>
										<fields>
											<xsl:value-of select='storageUnitMeta:getDescription($storageLocChildUnit)'/>,
											<xsl:value-of select='storageLocationMeta:getLocation($storageLocChild)'/>,
											<xsl:value-of select='storageLocationMeta:getIsAvailable($storageLocChild)'/>
										</fields>
									</queryTable>
									</query>             
                           </widget>
		                <widget halign = "center">
                            <appButton action="removeRow" onclick="this" key = "removeEntryButton">
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
	<integer key="{storageLocationMeta:getId($storageLoc)}" required="false"/>
    <string key="{storageLocationMeta:getName($storageLoc)}" max="20" required="true"/>
    <dropdown  key="{storageUnitMeta:getDescription($storageUnit)}" type="integer" required="true"/>
    <string key="{storageLocationMeta:getLocation($storageLoc)}" max="80" required="true"/>
    <check key="{storageLocationMeta:getIsAvailable($storageLoc)}" required="false"/>
    <table key="childStorageLocsTable"/>
	</rpc>
	
	<rpc key="query">
	<queryNumber key="{storageLocationMeta:getId($storageLoc)}" type="integer" required="false"/>
    <queryString key="{storageLocationMeta:getName($storageLoc)}" required="false"/>
    <queryString key="{storageLocationMeta:getLocation($storageLoc)}" required="false"/>
    <queryString key="{storageUnitMeta:getDescription($storageUnit)}" required="false"/>
    <queryCheck key="{storageLocationMeta:getIsAvailable($storageLoc)}" required="false"/>
    <table key="childStorageLocsTable"/>
    
	<!-- table values -->
	<queryString key="{storageUnitMeta:getDescription($storageLocChildUnit)}" required="false"/>
	<queryString key="{storageLocationMeta:getLocation($storageLocChild)}" required="false"/>
	<queryCheck key="{storageLocationMeta:getIsAvailable($storageLocChild)}" required="false"/>
	</rpc>
	<rpc key="queryByLetter">
		<queryString key="{storageLocationMeta:getName($storageLoc)}"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>
