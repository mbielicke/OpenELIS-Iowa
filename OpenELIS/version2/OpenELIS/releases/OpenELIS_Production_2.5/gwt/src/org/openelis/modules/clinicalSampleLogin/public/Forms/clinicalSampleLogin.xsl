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
                xmlns:humanMeta="xalan://org.openelis.metamap.SampleHumanMetaMap"
                xmlns:patientMetaMap="xalan://org.openelis.metamap.PatientHumanMetaMap"
                xmlns:providerMetaMap="xalan://org.openelis.metamap.ProviderHumanMetaMap"
                xmlns:sampleMetaMap="xalan://org.openelis.metamap.SampleMetaMap"
                xmlns:addressMeta="xalan://org.openelis.meta.AddressMeta"
                xmlns:sampleItemMetaMap="xalan://org.openelis.metamap.SampleItemMetaMap"
                xmlns:sampleOrgMetaMap="xalan://org.openelis.metamap.SampleOrganizationMetaMap"
                xmlns:orgMeta="xalan://org.openelis.meta.OrganizationMeta"
                xmlns:sampleProjectMetaMap="xalan://org.openelis.metamap.SampleProjectMetaMap"
                xmlns:projectMeta="xalan://org.openelis.meta.ProjectMeta"
                version="1.0">
<xsl:import href="aToZOneColumn.xsl"/>

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>  
  
  <xalan:component prefix="humanMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.SampleHumanMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="patientMetaMap">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.PatientMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="providerMetaMap">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.ProviderMetaMap"/>
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
				 <VerticalPanel style="subform">
					<text style="FormTitle"><xsl:value-of select="resource:getString($constants,'ptntInfo')"/></text>
					 <VerticalPanel style="WhiteContentPanel" padding="0" spacing="0">
					  <TablePanel style="Form">
<!--										<row>
						<widget align="center" colspan="8">
							<text style="FormTitle">Patient Info</text>
						</widget>
					</row>-->
							<row>
								<text style="Prompt"><xsl:value-of select="resource:getString($constants,'id')"/>:</text>
								<multLookup key="patientId" listeners="this">
								    <icon style="LookupButtonImage" mouse="HoverListener" command="ClinicalSampleLogin.id_button_enum.PATIENT_SEARCH"/>
								    <icon style="AdvancedButtonImage" mouse="HoverListener" command="ClinicalSampleLogin.id_button_enum.PATIENT_NAME_SEARCH"/>
								    <icon style="CommentButtonImage" mouse="HoverListener" command="ClinicalSampleLogin.id_button_enum.PATIENT_COMMENTS"/>
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
								<text style="Prompt"><xsl:value-of select="resource:getString($constants,'npi')"/>:</text>
								<widget colspan="3">
								<textbox key="npi" width="75px"/>	
								</widget>
								<text style="Prompt"><xsl:value-of select="resource:getString($constants,'externalId')"/>:</text>
								<textbox key="extId" width="75px"/>	
							</row>
							<row>
								<text style="Prompt"><xsl:value-of select="resource:getString($constants,'lastName')"/>:</text>
								<textbox key="lastName" max = "30" width="175px"/>
								<text style="Prompt"><xsl:value-of select="resource:getString($constants,'address')"/>:</text>
								<widget colspan="3">
								<textbox key="streetAddress" max = "30" width="186px"/>	
								</widget>
								<text style="Prompt"><xsl:value-of select="resource:getString($constants,'gender')"/>:</text>
								<dropdown key="genderId" width="75px"/>	
							</row>
							<row>
								<text style="Prompt"><xsl:value-of select="resource:getString($constants,'firstName')"/>:</text>
								<textbox key="firstName" max = "20" width="150px"/>		
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'aptSuite')"/>:</text>
								<widget colspan="3">									
								<textbox key="multipleUnit" max = "30" width="186px"/>	
								</widget>
								<text style="Prompt"><xsl:value-of select="resource:getString($constants,'race')"/>:</text>
								<textbox key="race" max = "5" width="150px"/>		
							</row>
							<row>
								<text style="Prompt"><xsl:value-of select="resource:getString($constants,'middleName')"/>:</text>
								<textbox key="middleName" max = "20" width="150px"/>		
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'city')"/>:</text>
								<widget colspan="3">									
								<textbox key="city" max = "30" width="186px"/>	
								</widget>
								<text style="Prompt"><xsl:value-of select="resource:getString($constants,'ethnicity')"/>:</text>
								<dropdown key="ethnicityId"  width="150px"/>
							</row>
							<row>
								<text style="Prompt"><xsl:value-of select="resource:getString($constants,'birth')"/>:</text>
								<calendar key="birthDate" begin="0" end="2" width="110px" />
								<text style="Prompt"><xsl:value-of select="resource:getString($constants,'state')"/>:</text>
								<dropdown key="state" width="33px"/>	
								<text style="Prompt"><xsl:value-of select="resource:getString($constants,'zipcode')"/>:</text>
								<textbox key="zipCode" max = "10" width="63px"/>		
								<text style="Prompt"><xsl:value-of select="resource:getString($constants,'phone')"/>:</text>
								<textbox key="phone"  width="125px"/>	
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
						<text style="FormTitle"><xsl:value-of select="resource:getString($constants,'analytes')"/></text>
						<VerticalPanel style="WhiteContentPanel">
						<tree-table key="itemsTestsTree" width="auto" showScroll="ALWAYS" manager="this" maxRows="4" enable="true" showError="false">
	    	                <headers><xsl:value-of select="resource:getString($constants,'itemTests')"/>,<xsl:value-of select="resource:getString($constants,'source')"/></headers>
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
									<text><xsl:value-of select="resource:getString($constants,'addItem')"/></text>
								</HorizontalPanel>
							</appButton>
							<appButton action="addTest" key="addTestButton" onclick="this" style="Button">
								<HorizontalPanel>
									<AbsolutePanel style="AddRowButtonImage"/>
									<text><xsl:value-of select="resource:getString($constants,'addTest')"/></text>
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
	                  	<text style="FormTitle"><xsl:value-of select="resource:getString($constants,'provOrgInfo')"/></text>
          				<VerticalPanel style="WhiteContentPanel" padding="0" spacing="0">
	                  <TablePanel style="Form">
<!--                  		<row>
                  			<widget colspan="4" align="center">
		                  		<text style="FormTitle">Provider/Organization Info</text>
							</widget>
                  		</row>
                  		-->
                  		<row>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'provider')"/>:</text>
							<textbox key="providerName"  width="100px"/>		
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'id')"/>:</text>
							<textbox key="providerId"  width="50px"/>
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'phone')"/>:</text>
							<widget colspan="3">
							<textbox key="providerPhone" max = "21" width="200px"/>		
							</widget>
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'project')"/>:</text>
							<widget colspan="3">
							<multLookup key="projectLook" listeners="this">
								    <icon style="LookupButtonImage" mouse="HoverListener" command="ClinicalSampleLogin.id_button_enum.PROJECT_VIEW"/>
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
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'reportTo')"/>:</text>
							<widget colspan="3">
								<multLookup key="reportToLook" listeners="this">
								    <icon style="LookupButtonImage" mouse="HoverListener" command="ClinicalSampleLogin.id_button_enum.ORGANIZATION_VIEW"/>
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
				<TabPanel height="170px" key="sampleItemTabPanel">
					<tab key="tab1" text="{resource:getString($constants,'testResults')}">
						<VerticalPanel height="170px" width="730px"/>
					</tab>
					<tab key="tab2" text="{resource:getString($constants,'analysis')}">
						<VerticalPanel height="170px" width="730px"/>
					</tab>
					<tab key="tab3" text="{resource:getString($constants,'extrnlCmnts')}">
						<VerticalPanel height="170px" width="730px"/>
					</tab>
					<tab key="tab4" text="{resource:getString($constants,'intrnlCmnts')}">
						<VerticalPanel height="170px" width="730px"/>
					</tab>
				</TabPanel>
			</VerticalPanel>
		</VerticalPanel>
	</display>
	<rpc key="display">
     <integer key="id" required="false"/> 
     <integer key="accessionNumber" required="true"/>
     <date key="collectionDate" begin="0" end="2" required="true"/> 
     <string key = "collectionTime" required = "false"/>
     <date key="receivedDate" begin="0" end="2" required="false"/> 
     <string key="clientReference" required = "false"/>
     <string key="lastName" required = "false"/>
     <string key="multipleUnit" required = "false"/>
     <integer key="race" required = "false"/>
     <string key="middleName" required = "false"/>
     <string key="city" required = "false"/>
     <dropdown key="ethnicityId" required = "false"/>
     <date key="birthDate" begin="0" end="2" required="true"/>
     <dropdown key="state" required = "false"/>
     <string key="zipCode" required = "false"/>
     <tree key = "itemsTestsTree"/>
     <string key="providerName" required = "false"/>
     <integer key="providerId" required = "false"/>
     <string key="providerPhone" required = "false"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>