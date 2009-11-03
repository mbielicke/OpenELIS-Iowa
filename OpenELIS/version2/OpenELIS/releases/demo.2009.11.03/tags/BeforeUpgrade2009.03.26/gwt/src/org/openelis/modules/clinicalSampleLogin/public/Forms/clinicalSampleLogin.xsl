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
<screen id="ClinicalSampleLogin" name="{resource:getString($constants,'clinicalSampleLogin')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
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
			<VerticalPanel style="WhiteContentPanel" padding="0" spacing="0">
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
						<calendar key="recieved" begin="0" end="2" width="110px" tab="??,??"/>
						<text style="Prompt">Status:</text>
						<dropdown key="status" case="mixed" width="110px"/>
						<text style="Prompt">Client Reference:</text>
						<widget colspan="3">
							<textbox key="clientRef" tab="??,??" width="175px"/>					
						</widget>
					</row>
					</TablePanel>
					<VerticalPanel style="subform">
					<text style="FormTitle">Patient Info</text>
					<VerticalPanel style="WhiteContentPanel" padding="0" spacing="0">
					<TablePanel style="Form">
<!--										<row>
						<widget align="center" colspan="8">
							<text style="FormTitle">Patient Info</text>
						</widget>
					</row>-->
							<row>
								<text style="Prompt">Id:</text>
								<multLookup key="patientId" listeners="this">
								    <icon style="LookupButtonImage" mouse="HoverListener" command="ClincalSampleLogin.id_button_enum.ID_1"/>
								    <icon style="AdvancedButtonImage" mouse="HoverListener" command="ClincalSampleLogin.id_button_enum.ID_2"/>
								    <icon style="CommentButtonImage" mouse="HoverListener" command="ClincalSampleLogin.id_button_enum.ID_3"/>
								</multLookup>
								
								<!--<HorizontalPanel>
									<textbox key="aa" showError="false" width="75px"/>
									<appButton action="idButton1" key="idButton1" onclick="this" style="FieldButton">
										<HorizontalPanel>
											<AbsolutePanel style="LookupButtonImage"/>
										</HorizontalPanel>
									</appButton>
									<appButton action="idButton2" key="idButton2" onclick="this" style="FieldButton">
										<HorizontalPanel>
											<AbsolutePanel style="AdvancedButtonImage"/>
										</HorizontalPanel>
									</appButton>
									<appButton action="idButton3" key="idButton3" onclick="this" style="FieldButton">
										<HorizontalPanel>
											<AbsolutePanel style="CommentButtonImage"/>
										</HorizontalPanel>
									</appButton>
								</HorizontalPanel>-->
								<text style="Prompt">Npi:</text>
								<widget colspan="3">
								<textbox key="npi" width="75px"/>	
								</widget>
								<text style="Prompt">External Id:</text>
								<textbox key="extId" width="75px"/>	
							</row>
							<row>
								<text style="Prompt">Last:</text>
								<textbox key="last" tab="??,??" width="175px"/>
								<text style="Prompt">Address:</text>
								<widget colspan="3">
								<textbox key="address" width="186px"/>	
								</widget>
								<text style="Prompt">Gender:</text>
								<dropdown key="gender" width="75px"/>	
							</row>
							<row>
								<text style="Prompt">First:</text>
								<textbox key="first" tab="??,??" width="150px"/>		
									<text style="Prompt">Mult Unit:</text>
								<widget colspan="3">									
								<textbox key="multunit" width="186px"/>	
								</widget>
								<text style="Prompt">Race:</text>
								<dropdown key="race" tab="??,??" width="150px"/>		
							</row>
							<row>
								<text style="Prompt">Middle:</text>
								<textbox key="middle" tab="??,??" width="150px"/>		
									<text style="Prompt">City:</text>
								<widget colspan="3">									
								<textbox key="city" width="186px"/>	
								</widget>
								<text style="Prompt">Ethinicity:</text>
								<dropdown key="ethn" tab="??,??" width="150px"/>
							</row>
							<row>
								<text style="Prompt">Birth:</text>
								<calendar key="birth" begin="0" end="2" width="110px" tab="??,??"/>
								<text style="Prompt">State:</text>
								<textbox key="state" width="33px"/>	
								<text style="Prompt">Zip Code:</text>
								<textbox key="zipCode" tab="??,??" width="63px"/>		
								<text style="Prompt">Phone:</text>
								<textbox key="phone" tab="??,??" width="125px"/>	
							</row>
							</TablePanel>
							</VerticalPanel>
							<!--<HorizontalPanel style="subformButtons">
							<appButton action="idButton1" key="idButton1" onclick="this" style="FormButton">
										<HorizontalPanel>
											<AbsolutePanel style="LookupButtonImage"/>
										</HorizontalPanel>
									</appButton>
									<appButton action="idButton2" key="idButton2" onclick="this" style="FormButton">
										<HorizontalPanel>
											<AbsolutePanel style="LookupButtonImage"/>
										</HorizontalPanel>
									</appButton>
									<appButton action="idButton3" key="idButton3" onclick="this" style="FormButton">
										<HorizontalPanel>
											<AbsolutePanel style="LookupButtonImage"/>
										</HorizontalPanel>
									</appButton>
									</HorizontalPanel>-->
									
							</VerticalPanel>
				<HorizontalPanel>
					<VerticalPanel style="subform">
						<text style="FormTitle">Analytes</text>
						<VerticalPanel style="WhiteContentPanel">
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
	                  	<HorizontalPanel style="TableButtonFooter">
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
							<appButton action="removeRow" key="removeContactButton" onclick="this" style="Button">
								<HorizontalPanel>
									<AbsolutePanel style="RemoveRowButtonImage"/>
									<text><xsl:value-of select="resource:getString($constants,'removeRow')"/></text>
								</HorizontalPanel>
							</appButton>
	                  	</HorizontalPanel>
	                  	</VerticalPanel>
	                  	</VerticalPanel>
	                  	<VerticalPanel style="subform">
	                  	<text style="FormTitle">Provider/Organization Info</text>
          				<VerticalPanel style="WhiteContentPanel" padding="0" spacing="0">
	                  <TablePanel style="Form">
<!--                  		<row>
                  			<widget colspan="4" align="center">
		                  		<text style="FormTitle">Provider/Organization Info</text>
							</widget>
                  		</row>
                  		-->
                  		<row>
							<text style="Prompt">Provider:</text>
							<textbox key="provider" tab="??,??" width="100px"/>		
							<text style="Prompt">Id:</text>
							<textbox key="provId" tab="??,??" width="50px"/>		
						</row>
						<row>
							<text style="Prompt">Phone:</text>
							<widget colspan="3">
							<textbox key="provPhone" tab="??,??" width="200px"/>		
							</widget>
						</row>
						<row>
							<text style="Prompt">Project:</text>
							<widget colspan="3">
							<multLookup key="projectLook" listeners="this">
								    <icon style="LookupButtonImage" mouse="HoverListener" command="ClincalSampleLogin.id_button_enum.ID_4"/>
								</multLookup>
								<!--
							<HorizontalPanel>
								<textbox key="project" tab="??,??" width="167px" showError="false"/>	
								<appButton action="reportTo" key="projectButton" onclick="this" style="FieldButton">
									<HorizontalPanel>
										<AbsolutePanel style="LookupButtonImage"/>
									</HorizontalPanel>
								</appButton>
								</HorizontalPanel>	-->
							</widget>
						</row>
						<row>
							<text style="Prompt">Report To:</text>
							<widget colspan="3">
								<multLookup key="reportToLook" listeners="this">
								    <icon style="LookupButtonImage" mouse="HoverListener" command="ClincalSampleLogin.id_button_enum.ID_5"/>
								</multLookup>
								<!--
															<HorizontalPanel>
								<textbox key="reportTo" tab="??,??" width="167px" showError="false"/>	
								<appButton action="reportTo" key="reportToButton" onclick="this" style="FieldButton">
									<HorizontalPanel>
										<AbsolutePanel style="LookupButtonImage"/>
									</HorizontalPanel>
								</appButton>
								</HorizontalPanel>-->
							</widget>
						</row>
					</TablePanel>
					</VerticalPanel>
					</VerticalPanel>
				</HorizontalPanel>
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