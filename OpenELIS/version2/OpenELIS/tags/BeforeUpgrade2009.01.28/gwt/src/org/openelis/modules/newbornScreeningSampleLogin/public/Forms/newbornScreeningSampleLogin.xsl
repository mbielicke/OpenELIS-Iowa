<?xml version="1.0" encoding="UTF-8"?>
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
<xsl:stylesheet extension-element-prefixes="resource" version="1.0" xmlns:locale="xalan://java.util.Locale" xmlns:resource="xalan://org.openelis.util.UTFResource" xmlns:xalan="http://xml.apache.org/xalan" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:import href="aToZOneColumn.xsl"/>
	<xalan:component prefix="resource">
		<xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
	</xalan:component>
	<xalan:component prefix="locale">
		<xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
	</xalan:component>
	<xsl:template match="doc">
		<xsl:variable name="language">
			<xsl:value-of select="locale"/>
		</xsl:variable>
		<xsl:variable name="props">
			<xsl:value-of select="props"/>
		</xsl:variable>
		<xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
		<screen id="ClinicalSampleLogin" name="{resource:getString($constants,'newbornScreeningSampleLogin')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
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
						<VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
						<TablePanel style="Form">
							<row>
								<text style="Prompt">Accession #:</text>
								<textbox key="accessionNum" tab="??,??" width="75px"/>
								<text style="Prompt">Order #:</text>
								<textbox key="accessionNum" tab="??,??" width="75px"/>
								<text style="Prompt">Collected:</text>
								<calendar begin="0" end="2" key="domain" tab="??,??" width="75px"/>
								<text style="Prompt">Time:</text>
								<textbox key="time" width="40px"/>
							</row>
							<row>
								<text style="Prompt">Received:</text>
								<calendar begin="0" end="2" key="domain" tab="??,??" width="110px"/>
								<text style="Prompt">Status:</text>
								<dropdown case="mixed" key="organization" width="110px"/>
								<text style="Prompt">Client Reference:</text>
								<widget colspan="3">
									<textbox key="domain" tab="??,??" width="175px"/>
								</widget>
							</row>
						</TablePanel>
						<HorizontalPanel>
						<VerticalPanel style="subform">
	                  	<text style="FormTitle">Baby Info</text>
						<TablePanel style="Form" width="100%">
							<row>
								<text style="Prompt">Id:</text>
								<HorizontalPanel>
									<textbox key="aa" showError="false" width="75px"/>
									<appButton action="idButton1" key="idButton1" onclick="this" style="Button">
										<HorizontalPanel>
											<AbsolutePanel style="LookupButtonImage"/>
										</HorizontalPanel>
									</appButton>
									<appButton action="idButton2" key="idButton2" onclick="this" style="Button">
										<HorizontalPanel>
											<AbsolutePanel style="LookupButtonImage"/>
										</HorizontalPanel>
									</appButton>
									<appButton action="idButton3" key="idButton3" onclick="this" style="Button">
										<HorizontalPanel>
											<AbsolutePanel style="LookupButtonImage"/>
										</HorizontalPanel>
									</appButton>
								</HorizontalPanel>
							</row>
							<row>
								<text style="Prompt">Last:</text>
								<textbox key="domain" tab="??,??" width="175px"/>
							</row>
							<row>
								<text style="Prompt">First:</text>
								<textbox key="domain" tab="??,??" width="150px"/>
							</row>
							<row>
								<text style="Prompt">Gender:</text>
								<HorizontalPanel style="Form" width="100%">
									<dropdown key="aa" width="50px"/>
									<HorizontalPanel halign="right" style="Form">
										<text style="Prompt">Race:</text>
										<dropdown key="race" tab="??,??" width="70px"/>
									</HorizontalPanel>
								</HorizontalPanel>
							</row>
							<row>
								<text style="Prompt">Birth:</text>
								<HorizontalPanel style="Form" width="100%">
									<calendar begin="0" end="2" key="domain" tab="??,??" width="75px"/>
									<HorizontalPanel halign="right" style="Form">
										<text style="Prompt">Time:</text>
										<textbox key="time" width="40px"/>
									</HorizontalPanel>
								</HorizontalPanel>
							</row>
							<row>
								<text style="Prompt">Gest Age:</text>
								<textbox key="aa" width="25px"/>
							</row>
						</TablePanel>
						</VerticalPanel>
						<VerticalPanel style="subform">
	                  	<text style="FormTitle">Mother</text>
						<TablePanel style="Form">
						<row>
							<text style="Prompt">Last:</text>
							<widget colspan="3">
								<textbox key="domain" tab="??,??" width="175px"/>
							</widget>
							<text style="Prompt">Unique Id:</text>
							<textbox key="aa" width="75px"/>
						</row>
						<row>
							<text style="Prompt">First:</text>
							<widget colspan="3">
								<textbox key="domain" tab="??,??" width="175px"/>
							</widget>
							<text style="Prompt">Birth:</text>
							<calendar begin="0" end="2" key="domain" tab="??,??" width="75px"/>
						</row>
						<row>
							<text style="Prompt">Address:</text>
							<widget colspan="3">
								<textbox key="aa" width="186px"/>
							</widget>
							<text style="Prompt">Phone:</text>
							<textbox key="motherPhone" width="80px"/>
						</row>
						<row>
							<text style="Prompt">Mult Unit:</text>
							<widget colspan="3">
								<textbox key="aa" width="186px"/>
							</widget>
						</row>
						<row>
							<text style="Prompt">City:</text>
							<widget colspan="3">
								<textbox key="aa" width="186px"/>
							</widget>
						</row>
						<row>
							<text style="Prompt">State:</text>
							<textbox key="aa" width="33px"/>
							<text style="Prompt">Zip Code:</text>
							<textbox key="domain" tab="??,??" width="63px"/>
						</row>
						</TablePanel>
						</VerticalPanel>
						</HorizontalPanel>
						<VerticalPanel style="subform" width="98%">
	                  	<text style="FormTitle">Sample</text>
						<TablePanel style="Form" width="100%">
							<row>
								<text style="Prompt">Feeding:</text>
								<dropdown key="aa" width="75px"/>
								<text style="Prompt">TPN:</text>
								<check key="tpn"/>
								<text style="Prompt">Transfused:</text>
								<dropdown key="domain" tab="??,??" width="60px"/>
								<text style="Prompt">Date:</text>
								<calendar begin="0" end="2" key="domain" tab="??,??" width="75px"/>
							</row>
							<row>
								<text style="Prompt">Weight:</text>
								<textbox key="weight" width="75px"/>
								<text style="Prompt">Age:</text>
								<textbox key="age" width="53px"/>
								<text style="Prompt">Barcode #:</text>
								<textbox key="domain" tab="??,??" width="60px"/>
								<text style="Prompt">Repeat:</text>
								<check key="repeat"/>
							</row>
						</TablePanel>
						</VerticalPanel>
						<HorizontalPanel>
							<VerticalPanel style="subform">
						<text style="FormTitle">Analytes</text>
								<tree-table enable="true" key="itemsTestsTree" manager="this" maxRows="4" showError="false" showScroll="ALWAYS" width="auto">
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
									<!--	                  		<appButton action="addItem" key="addItemButton" onclick="this" style="Button">
								<HorizontalPanel>
									<AbsolutePanel style="AddRowButtonImage"/>
									<text>Add item</text>
								</HorizontalPanel>
							</appButton>-->
									<appButton action="addTest" key="addTestButton" onclick="this" style="Button">
										<HorizontalPanel>
											<AbsolutePanel style="AddRowButtonImage"/>
											<text>Add Test</text>
										</HorizontalPanel>
									</appButton>
									<HorizontalPanel width="225px"/>
									<appButton action="removeRow" key="removeContactButton" onclick="this" style="Button">
										<HorizontalPanel>
											<AbsolutePanel style="RemoveRowButtonImage"/>
											<text>
												<xsl:value-of select="resource:getString($constants,'removeRow')"/>
											</text>
										</HorizontalPanel>
									</appButton>
								</HorizontalPanel>
							</VerticalPanel>
							<VerticalPanel style="subform">
	                  	<text style="FormTitle">Provider/Organization Info</text>
							<TablePanel style="Form">
								<row>
									<text style="Prompt">Provider:</text>
									<textbox key="domain" tab="??,??" width="100px"/>
									<text style="Prompt">Id:</text>
									<textbox key="domain" tab="??,??" width="50px"/>
								</row>
								<row>
									<text style="Prompt">Phone:</text>
									<widget colspan="3">
										<textbox key="domain" tab="??,??" width="200px"/>
									</widget>
								</row>
								<!--		<row>
							<text style="Prompt">Project:</text>
							<widget colspan="3">
							<HorizontalPanel>
								<textbox key="domain" tab="??,??" width="167px" showError="false"/>	
								<appButton action="reportTo" key="projectButton" onclick="this" style="Button">
									<HorizontalPanel>
										<AbsolutePanel style="LookupButtonImage"/>
									</HorizontalPanel>
								</appButton>
								</HorizontalPanel>	
							</widget>
						</row>-->
								<row>
									<text style="Prompt">Report To:</text>
									<widget colspan="3">
										<HorizontalPanel>
											<textbox key="domain" showError="false" tab="??,??" width="167px"/>
											<appButton action="reportTo" key="reportToButton" onclick="this" style="FieldButton">
									<HorizontalPanel>
										<AbsolutePanel style="LookupButtonImage"/>
									</HorizontalPanel>
								</appButton>
										</HorizontalPanel>
									</widget>
								</row>
								<row>
									<text style="Prompt">Birth Org:</text>
									<widget colspan="3">
										<HorizontalPanel>
											<textbox key="domain" showError="false" tab="??,??" width="167px"/>
											<appButton action="reportTo" key="birthOrgButton" onclick="this" style="FieldButton">
									<HorizontalPanel>
										<AbsolutePanel style="LookupButtonImage"/>
									</HorizontalPanel>
								</appButton>
										</HorizontalPanel>
									</widget>
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
			<rpc key="display"/>
			<rpc key="query"/>
		</screen>
	</xsl:template>
</xsl:stylesheet>
