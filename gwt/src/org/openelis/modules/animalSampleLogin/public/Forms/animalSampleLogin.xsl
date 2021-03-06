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
<xsl:import href="aToZOneColumn.xsl"/>

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
<screen id="AnimalSampleLogin" name="{resource:getString($constants,'animalSampleLogin')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
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
						<text style="Prompt">Accession #:</text>
						<textbox key="accessionNum" tab="??,??" width="75px"/>
						<text style="Prompt">Order #:</text>
						<textbox key="orderNum" tab="??,??" width="75px"/>
						<text style="Prompt">Collected:</text>
						<calendar begin="0" end="2" key="collected" tab="??,??" width="75px"/>
						<text style="Prompt">Time:</text>
						<textbox key="collectedTime" width="40px"/>
					</row>
					<row>
						<text style="Prompt">Received:</text>
						<calendar key="received" begin="0" end="2" width="110px" tab="??,??"/>
						<text style="Prompt">Status:</text>
						<dropdown key="status" case="mixed" width="110px"/>
						<text style="Prompt">Client Reference:</text>
						<widget colspan="3">
							<textbox key="clientRef" tab="??,??" width="175px"/>					
						</widget>
					</row>
					</TablePanel>
					<VerticalPanel style="subform" width="98%">
					<text style="FormTitle">Animal Info</text>
					<TablePanel style="Form">
					<row>
						<text style="Prompt">Common Name:</text>
						<widget colspan="3">
						<dropdown key="commomnName" width="150px"/>	
						</widget>
							
						<text style="Prompt">Scientific Name:</text>
						<widget colspan="3">
							<dropdown key="sciName" tab="??,??" width="150px"/>		
						</widget>
					</row>
					<row>
						<text style="Prompt">Collector:</text>
						<widget colspan="3">
						<textbox key="collector" tab="??,??" width="235px"/>		
						</widget>
						<text style="Prompt">Phone:</text>
						<widget colspan="3">
							<textbox key="colPhioen" tab="??,??" width="120px"/>		
						</widget>
					</row>
					<row>
						<text style="Prompt">Location:</text>
						<widget colspan="3">
						<multLookup key="locLook" listeners="this">
								    <icon style="LookupButtonImage" mouse="HoverListener" command="ClinicalSampleLogin.id_button_enum.PATIENT_SEARCH"/>
								</multLookup>
						</widget>
					</row>
							</TablePanel>
							</VerticalPanel>
				<HorizontalPanel>
					<VerticalPanel style="subform">
						<text style="FormTitle">Analytes</text>
						<tree-table key="itemsTestsTree" width="auto" showScroll="ALWAYS" manager="this" maxRows="4" enable="true" showError="false">
	    	                <headers>Item/Tests,Source</headers>
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
	                  	<text style="FormTitle">Provider/Organization Info</text>
                  <TablePanel style="Form">
                  		<row>
							<text style="Prompt">Provider:</text>
							<textbox key="provicer" tab="??,??" width="100px"/>		
							<text style="Prompt">Id:</text>
							<textbox key="provid" tab="??,??" width="50px"/>		
						</row>
						<row>
							<text style="Prompt">Phone:</text>
							<widget colspan="3">
							<textbox key="provphone" tab="??,??" width="200px"/>		
							</widget>
						</row>
						<row>
							<text style="Prompt">Project:</text>
							<widget colspan="3">
							<multLookup key="projectLook" listeners="this">
								    <icon style="LookupButtonImage" mouse="HoverListener" command="ClinicalSampleLogin.id_button_enum.PATIENT_SEARCH"/>
								</multLookup>
							</widget>
						</row>
						<row>
							<text style="Prompt">Report To:</text>
							<widget colspan="3">
							<multLookup key="reportToLook" listeners="this">
								    <icon style="LookupButtonImage" mouse="HoverListener" command="ClinicalSampleLogin.id_button_enum.PATIENT_SEARCH"/>
								</multLookup>
							</widget>
						</row>
						<!--<row>
							<text style="Prompt">Bill To:</text>
							<textbox key="domain" tab="??,??" width="200px"/>		
						</row>-->
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
	<rpc key="query">

    </rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>