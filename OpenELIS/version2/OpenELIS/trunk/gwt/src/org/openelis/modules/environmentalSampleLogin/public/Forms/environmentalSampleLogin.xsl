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
                xmlns:envMeta="xalan://org.openelis.metamap.SampleEnvironmentalMetaMap"
                xmlns:sampleMetaMap="xalan://org.openelis.metamap.SampleMetaMap"
                xmlns:addressMeta="xalan://org.openelis.meta.AddressMeta"
                xmlns:sampleItemMetaMap="xalan://org.openelis.metamap.SampleItemMetaMap"
                xmlns:sampleOrgMetaMap="xalan://org.openelis.metamap.SampleOrganizationMetaMap"
                xmlns:orgMeta="xalan://org.openelis.meta.OrganizationMeta"
                xmlns:sampleProjectMetaMap="xalan://org.openelis.metamap.SampleProjectMetaMap"
                xmlns:projectMeta="xalan://org.openelis.meta.ProjectMeta"
                
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZOneColumn.xsl"/>

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
  <xalan:component prefix="envMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.SampleEnvironmentalMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="sampleMetaMap">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.SampleMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="addressMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.AddressMeta"/>
  </xalan:component>
  
  <xalan:component prefix="sampleItemMetaMap">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.SampleItemMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="sampleOrgMetaMap">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.SampleOrganizationMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="orgMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrganizationMeta"/>
  </xalan:component>
  
  <xalan:component prefix="sampleProjectMetaMap">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.SampleProjectMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="projectMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.ProjectMeta"/>
  </xalan:component>
  
  <xsl:template match="doc"> 
  <xsl:variable name="env" select="envMeta:new()"/>
    <xsl:variable name="sample" select="envMeta:getSample($env)"/>
    <xsl:variable name="address" select="envMeta:getAddress($env)"/>
    <xsl:variable name="sampleItem" select="sampleMetaMap:getSampleItem($sample)"/>
    <xsl:variable name="sampleOrg" select="sampleMetaMap:getSampleOrganization($sample)"/>
    <xsl:variable name="sampleProject" select="sampleMetaMap:getSampleProject($sample)"/>
    <xsl:variable name="parentSampleItem" select="sampleItemMetaMap:getParentSampleItem($sampleItem)"/>
    <xsl:variable name="org" select="sampleOrgMetaMap:getOrganization($sampleOrg)"/>
    <xsl:variable name="project" select="sampleProjectMetaMap:getProject($sampleProject)"/>
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="EnvironmentalSampleLogin" name="{resource:getString($constants,'environmentalSampleLogin')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
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
				<!--end button panel code-->
				<VerticalPanel style="WhiteContentPanel" spacing="0" padding="0">		
					<TablePanel style="Form">
						<row>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'accessionNum')"/>:</text>
							<textbox key="accessionNumber"  width="75px"/>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'orderNum')"/>:</text>
							<textbox key="orderNum"  width="75px"/>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'collected')"/>:</text>
							<calendar begin="0" end="2" key="collectionDate" width="75px"/>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'time')"/>:</text>
							<textbox key="collectionTime" width="40px"/>
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'received')"/>:</text>
							<calendar key="receivedDate" begin="0" end="2" width="110px"/>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'status')"/>:</text>
							<dropdown key="statusId" case="mixed" width="110px"/>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'clntRef')"/>:</text>
							<widget colspan="3">
								<textbox key="clientReference" width="175px"/>					
							</widget>
						</row>
					</TablePanel>
					<VerticalPanel style="subform" width="98%">
					<text style="FormTitle">Environmental Info</text>
					<TablePanel style="Form" width="100%">
						<row>
							<text style="Prompt">Hazardous:</text>
							<check key="hazard"/>	
							<text style="Prompt">Desc:</text>
							<textbox key="desc" tab="??,??" width="315px"/>		
						</row>
						<row>
							<text style="Prompt">Collector:</text>
							<textbox key="collector" tab="??,??" width="235px"/>		
							<text style="Prompt">Phone:</text>
							<textbox key="colPhone" tab="??,??" width="120px"/>		
						</row>
						<row>
							<text style="Prompt">Location:</text>
							<multLookup key="locationLook" listeners="this">
								    <icon style="LookupButtonImage" mouse="HoverListener" command="EnvironmentalSampleLogin.id_button_enum.LOCATION_VIEW"/>
							</multLookup>
						</row>
					</TablePanel>
					</VerticalPanel>
				<HorizontalPanel>
					<VerticalPanel style="subform">
						<text style="FormTitle">Analytes</text>
						<tree-table key="itemsTestsTree" width="auto" showScroll="ALWAYS" maxRows="4" manager="this" enable="true" showError="false">
	    	                <headers>Item/Tests,Type</headers>
	                        <widths>280,130</widths>					
	                        <leaves>
	        	                <leaf type="top">
	            	                <editors>
	                	                <label/>
									 	<label/>
	                               	</editors>
	                                <fields>
	                                	<string/>
	                                    <string/>
	                              	</fields>
	                        	</leaf>
	                            <!--<leaf type="orderItem">                                   
	                             	<editors>
	                                	<textbox/>
	                              		<label/>
	                              		<textbox/>
	                               		<label/>
	                             	</editors>
	                                <fields>
										<number type="integer" required="false"/>
									    <string required="false"/>
										<string required="false"/>
										<string required="false"/>
	                              	</fields>
	                        	</leaf>--> 
	                      	</leaves> 
	                  	</tree-table>
	                  	<HorizontalPanel>
	                  		<appButton action="addItem" key="addItemButton" onclick="this" style="Button">
								<HorizontalPanel>
									<AbsolutePanel style="AddRowButtonImage"/>
									<text>Add item</text>
								</HorizontalPanel>
							</appButton>
							<appButton action="addTest" key="addTestButton" onclick="this" style="Button">
								<HorizontalPanel>
									<AbsolutePanel style="AddRowButtonImage"/>
									<text>Add Test</text>
								</HorizontalPanel>
							</appButton>
							<HorizontalPanel width="135px"/>
							<appButton action="removeRow" key="removeContactButton" onclick="this" style="Button">
								<HorizontalPanel>
									<AbsolutePanel style="RemoveRowButtonImage"/>
									<text><xsl:value-of select="resource:getString($constants,'removeRow')"/></text>
								</HorizontalPanel>
							</appButton>
	                  	</HorizontalPanel>
	                  	</VerticalPanel>
	                  	<VerticalPanel style="subform">
	                  	<text style="FormTitle">Organization Info</text>
                  <TablePanel style="Form">
						<row>
							<text style="Prompt">Project:</text>
							<multLookup key="projectLook" listeners="this">
								    <icon style="LookupButtonImage" mouse="HoverListener" command="EnvironmentalSampleLogin.id_button_enum.PROJECT_VIEW"/>
							</multLookup>
						</row>
						<row>
							<text style="Prompt">Report To:</text>
							<!--<lookup key="" icon="LookupButtonImage" onclick="this"/>-->
							<multLookup key="reportToLook" listeners="this">
							    <icon style="LookupButtonImage" mouse="HoverListener" command="EnvironmentalSampleLogin.id_button_enum.REPORT_TO_VIEW"/>
							</multLookup>
						</row>
						<row>
							<text style="Prompt">Bill To:</text>
							<textbox key="billto" tab="??,??" width="200px"/>		
						</row>
					</TablePanel>
					</VerticalPanel>
				</HorizontalPanel>
				<VerticalPanel height="5px"/>
				<TabPanel height="170px" key="orderTabPanel">
					<tab key="tab1" text="Test Info/Result">
						<VerticalPanel height="170px" width="730px"/>
					</tab>
					<tab key="tab2" text="Analysis">
						<VerticalPanel height="170px" width="730px"/>
					</tab>
					<tab key="tab3" text="External Comment">
						<VerticalPanel height="170px" width="730px"/>
					</tab>
					<tab key="tab4" text="Internal Comments">
						<VerticalPanel height="170px" width="730px"/>
					</tab>
				</TabPanel>
			</VerticalPanel>
		</VerticalPanel>
	</display>
	<rpc key="display">

	</rpc>

</screen>
  </xsl:template>
</xsl:stylesheet>