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
                xmlns:analysisMetaMap="xalan://org.openelis.metamap.AnalysisMetaMap"
                xmlns:sampleTestMetaMap="xalan://org.openelis.metamap.SampleTestMetaMap"
                xmlns:sectionMeta="xalan://org.openelis.meta.SectionMeta"
                xmlns:methodMeta="xalan://org.openelis.meta.MethodMeta"
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
  
  <xalan:component prefix="analysisMetaMap">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.AnalysisMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="sampleTestMetaMap">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.SampleTestMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="sectionMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.SectionMeta"/>
  </xalan:component>
  
  <xalan:component prefix="methodMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.MethodMeta"/>
  </xalan:component>
  
  <xsl:template match="doc"> 
  <xsl:variable name="env" select="envMeta:new()"/>
    <xsl:variable name="sample" select="envMeta:getSample($env)"/>
    <xsl:variable name="address" select="envMeta:getAddress($env)"/>
    <xsl:variable name="sampleItem" select="sampleMetaMap:getSampleItem($sample)"/>
    <xsl:variable name="analysis" select="sampleItemMetaMap:getAnalysis($sampleItem)"/>
    <xsl:variable name="test" select="analysisMetaMap:getTest($analysis)"/>
    <xsl:variable name="section" select="analysisMetaMap:getSection($analysis)"/>
    <xsl:variable name="method" select="sampleTestMetaMap:getMethod($test)"/>
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
							<textbox key="{sampleMetaMap:getAccessionNumber($sample)}"  width="75px" tab="orderNumber,billTo"/>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'orderNum')"/>:</text>
							<textbox key="orderNumber"  width="75px" tab="{sampleMetaMap:getCollectionDate($sample)},{sampleMetaMap:getAccessionNumber($sample)}"/>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'collected')"/>:</text>
							<calendar begin="0" end="2" key="{sampleMetaMap:getCollectionDate($sample)}" width="75px" tab="{sampleMetaMap:getCollectionTime($sample)},orderNumber"/>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'time')"/>:</text>
							<textbox key="{sampleMetaMap:getCollectionTime($sample)}" width="40px" tab="{sampleMetaMap:getReceivedDate($sample)},{sampleMetaMap:getCollectionDate($sample)}"/>
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'received')"/>:</text>
							<calendar key="{sampleMetaMap:getReceivedDate($sample)}" begin="0" end="2" width="110px" tab="{sampleMetaMap:getStatusId($sample)},{sampleMetaMap:getCollectionTime($sample)}"/>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'status')"/>:</text>
							<dropdown key="{sampleMetaMap:getStatusId($sample)}" case="mixed" width="110px" enabledStates="" tab="{sampleMetaMap:getClientReference($sample)},{sampleMetaMap:getReceivedDate($sample)}"/>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'clntRef')"/>:</text>
							<widget colspan="3">
								<textbox key="{sampleMetaMap:getClientReference($sample)}" width="175px" tab="{envMeta:getIsHazardous($env)},{sampleMetaMap:getStatusId($sample)}"/>					
							</widget>
						</row>
					</TablePanel>
					<VerticalPanel style="subform" width="98%">
					<text style="FormTitle"><xsl:value-of select="resource:getString($constants,'envInfo')"/></text>
					<TablePanel style="Form" width="100%">
						<row>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'hazardous')"/>:</text>
							<check key="{envMeta:getIsHazardous($env)}" tab="{envMeta:getDescription($env)},{sampleMetaMap:getClientReference($sample)}"/>	
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'desc')"/>:</text>
							<textbox key="{envMeta:getDescription($env)}" tab="{envMeta:getCollector($env)},{envMeta:getIsHazardous($env)}" width="315px"/>		
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'collector')"/>:</text>
							<textbox key="{envMeta:getCollector($env)}" tab="{envMeta:getCollectorPhone($env)},{envMeta:getDescription($env)}" width="235px"/>		
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'phone')"/>:</text>
							<textbox key="{envMeta:getCollectorPhone($env)}" tab="{envMeta:getSamplingLocation($env)},{envMeta:getCollector($env)}" width="120px"/>		
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'location')"/>:</text>
							<HorizontalPanel> 
							<textbox key="{envMeta:getSamplingLocation($env)}" onchange="this" width="175px"/>
							<comButton key="locButton" style="LookupButtonImage" useDiv="true" listeners="this" enabledStates="display,query,add,update" command="EnvironmentalSampleLogin.id_button_enum.LOCATION_VIEW"/>
							</HorizontalPanel>
						</row>
					</TablePanel>
					</VerticalPanel>
				<HorizontalPanel>
					<VerticalPanel style="subform">
						<text style="FormTitle"><xsl:value-of select="resource:getString($constants,'itemsAndAnalyses')"/></text>
						<VerticalPanel style="WhiteContentPanel">
						<tree-table key="itemsTestsTree" width="auto" showScroll="ALWAYS" manager="this" maxRows="4" enable="true" showError="false" tab="{projectMeta:getName($project)},{envMeta:getSamplingLocation($env)}">
	    	                <headers><xsl:value-of select="resource:getString($constants,'itemTests')"/>,<xsl:value-of select="resource:getString($constants,'typeStatus')"/></headers>
	                        <widths>280,130</widths>					
	                        <leaves>
	        	                <leaf type="sampleItem">
	            	                <editors>
	                	                <label/>
	                	                <label/>
	                               	</editors>
	                                <fields>
	                                	<string/>
	                                	<string/>
	                              	</fields>
	                        	</leaf>
	                        	<leaf type="analysis">
	            	                <editors>
	                	                <label/>
	                	                <dropdown width="110px"/>
	                               	</editors>
	                                <fields>
	                                	<string/>
	                                	<dropdown/>
	                              	</fields>
	                        	</leaf>
                            </leaves> 
	                  	</tree-table>
	                  	<HorizontalPanel style="TableButtonFooter">
	                  		<appButton action="addItem" key="addItemButton" onclick="this" style="Button" enabledStates="add,update">
								<HorizontalPanel>
									<AbsolutePanel style="AddRowButtonImage"/>
									<text><xsl:value-of select="resource:getString($constants,'addItem')"/></text>
								</HorizontalPanel>
							</appButton>
							<appButton action="addTest" key="addTestButton" onclick="this" style="Button" enabledStates="add,update">
								<HorizontalPanel>
									<AbsolutePanel style="AddRowButtonImage"/>
									<text><xsl:value-of select="resource:getString($constants,'addTest')"/></text>
								</HorizontalPanel>
							</appButton>
							<appButton action="removeRow" key="removeRowButton" onclick="this" style="Button" enabledStates="add,update">
								<HorizontalPanel>
									<AbsolutePanel style="RemoveRowButtonImage"/>
									<text><xsl:value-of select="resource:getString($constants,'removeRow')"/></text>
								</HorizontalPanel>
							</appButton>
	                  	</HorizontalPanel>
	                  	</VerticalPanel>
	                  	</VerticalPanel>
	                  	<VerticalPanel style="subform">
	                  	<text style="FormTitle"><xsl:value-of select="resource:getString($constants,'organizationInfo')"/></text>
                  <TablePanel style="Form">
						<row>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'project')"/>:</text>
							<HorizontalPanel> 
								<autoComplete key="{projectMeta:getName($project)}" width="175px" onchange="this" cat="project" case="upper" serviceUrl="OpenELISServlet?service=org.openelis.modules.sampleProject.server.SampleProjectService">				
									<headers>Name, Desc</headers>
									<widths>100,170</widths>												
								</autoComplete>
								<comButton key="projectLookupIcon" style="LookupButtonImage" useDiv="true" listeners="this" command="EnvironmentalSampleLogin.id_button_enum.PROJECT_VIEW"/>
							</HorizontalPanel>
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'reportTo')"/>:</text>
							<HorizontalPanel> 
								<autoComplete key="{orgMeta:getName($org)}" cat="organization" width="175px" onchange="this" case="upper" serviceUrl="OpenELISServlet?service=org.openelis.modules.order.server.OrderService">												
									<headers>Name,Street,City,St</headers>
									<widths>180,110,100,20</widths>
								</autoComplete>
								<comButton key="reportToLookupIcon" style="LookupButtonImage" useDiv="true" listeners="this" command="EnvironmentalSampleLogin.id_button_enum.REPORT_TO_VIEW"/>
							</HorizontalPanel>
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'billTo')"/>:</text>
							<HorizontalPanel> 
								<autoComplete key="billTo" cat="organization" width="175px" onchange="this" case="upper" serviceUrl="OpenELISServlet?service=org.openelis.modules.order.server.OrderService">												
									<headers>Name,Street,City,St</headers>
									<widths>180,110,100,20</widths>
								</autoComplete>
								<comButton key="billToLookupIcon" style="LookupButtonImage" useDiv="true" listeners="this" command="EnvironmentalSampleLogin.id_button_enum.REPORT_TO_VIEW"/>
							</HorizontalPanel>
						</row>
					</TablePanel>
					</VerticalPanel>
				</HorizontalPanel>
				<VerticalPanel height="5px"/>
				<TabPanel height="170px" key="sampleItemTabPanel">
					<tab key="tab0" text="{resource:getString($constants,'sampleItem')}">
						<VerticalPanel height="170px" width="730px">
							<TablePanel style="Form" spacing="0" padding="0">
								<row>
									<text style="Prompt">Sample Type:</text>
									<dropdown case="mixed" key="{sampleItemMetaMap:getTypeOfSampleId($sampleItem)}" onchange="this" width="150px"/>
									
								</row>
								<row>
									<text style="Prompt">Container:</text>
									<dropdown case="mixed" key="{sampleItemMetaMap:getContainerId($sampleItem)}" onchange="this" width="225px"/>
									<text style="Prompt">Container Reference:</text>
									<textbox case="mixed" key="{sampleItemMetaMap:getContainerReference($sampleItem)}" onchange="this" width="215px"/>
								</row>
								<row>
									<text style="Prompt">Qty:</text>
									<textbox case="mixed" key="{sampleItemMetaMap:getQuantity($sampleItem)}" onchange="this" width="150px"/>
									<text style="Prompt">Unit:</text>
									<dropdown case="mixed" key="{sampleItemMetaMap:getUnitOfMeasureId($sampleItem)}" onchange="this" width="150px"/>
								</row>
							</TablePanel>
						</VerticalPanel>
					</tab>
					<tab key="tab1" text="{resource:getString($constants,'analysis')}">
						<VerticalPanel height="170px" width="730px">
							<TablePanel style="Form" spacing="0" padding="0">
								<row>
									<text style="Prompt">Test:</text>
									<autoComplete key="{sampleTestMetaMap:getName($test)}" cat="testMethod" width="150px" onchange="this" case="lower" serviceUrl="OpenELISServlet?service=org.openelis.modules.environmentalSampleLogin.server.EnvironmentalSampleLoginService">												
										<headers>Test,Method</headers>
										<widths>150,150</widths>
									</autoComplete>
									<text style="Prompt">Method:</text>
									<autoComplete key="{methodMeta:getName($method)}" cat="testMethod" width="150px" onchange="this" case="lower" enabledStates="" serviceUrl="OpenELISServlet?service=org.openelis.modules.environmentalSampleLogin.server.EnvironmentalSampleLoginService">												
										<headers>Method</headers>
										<widths>150</widths>
									</autoComplete>
								</row>
								<row>
									<text style="Prompt">Status:</text>
									<dropdown case="mixed" key="{analysisMetaMap:getStatusId($analysis)}" enabledStates="" onchange="this" width="150px"/>
									<text style="Prompt">Revision:</text>
									<textbox case="mixed" key="{analysisMetaMap:getRevision($analysis)}" onchange="this" width="60px"/>
								</row>
								<row>
									<text style="Prompt">Reportable:</text>
									<check key="{analysisMetaMap:getIsReportable($analysis)}"/>
									<text style="Prompt">Section:</text>
									<autoComplete key="{analysisMetaMap:getSectionId($analysis)}" cat="section" width="150px" onchange="this" case="upper" serviceUrl="OpenELISServlet?service=org.openelis.modules.environmentalSampleLogin.server.EnvironmentalSampleLoginService">												
										<headers>name</headers>
										<widths>150</widths>
									</autoComplete>
								</row>
								<row>
									<text style="Prompt">Started:</text>
									<calendar begin="0" end="2" key="{analysisMetaMap:getStartedDate($analysis)}"/>
									<text style="Prompt">Completed:</text>
									<calendar begin="0" end="2" key="{analysisMetaMap:getCompletedDate($analysis)}"/>
								</row>
								<row>
									<text style="Prompt">Released:</text>
									<calendar begin="0" end="2" key="{analysisMetaMap:getReleasedDate($analysis)}"/>
									<text style="Prompt">Printed:</text>
									<calendar begin="0" end="2" key="{analysisMetaMap:getPrintedDate($analysis)}"/>
								</row>
							</TablePanel>
						</VerticalPanel>
					</tab>
					<tab key="tab2" text="{resource:getString($constants,'testResults')}">
						<VerticalPanel height="170px" width="730px">
							<table maxRows="6" width="auto">
						     <headers>Analyte,Result1,Result2,Result3</headers>
						     <widths>175,100,100,100</widths>
						     <editors>
						       <label/>
						       <label/>
						       <label/>
						       <label/>
						     </editors>
						     <fields>
						       <string/>
						       <string/>
						       <string/>
						       <string/>
						     </fields>
						     <sorts>true,true,true,true</sorts>
							<filters>false,false,false,false</filters>
							<colAligns>left,left,left,left</colAligns>
						   </table>
						</VerticalPanel>
					</tab>
					<tab key="tab3" text="{resource:getString($constants,'analysisExtrnlCmnts')}">
						<VerticalPanel height="170px" width="730px">
						<TablePanel style="Form" padding="0" spacing="0">
						<row>
							<widget colspan="2" align="center">
								<appButton action="analysisNoteExt" onclick="this" key="analysisNoteExt" style="Button" enabledStates="">
									<HorizontalPanel>
              							<AbsolutePanel xsi:type="Absolute" layout="absolute" style="StandardNoteButtonImage"/>
	                					<text><xsl:value-of select='resource:getString($constants,"edit")'/></text>
						             </HorizontalPanel>
						    	</appButton>
						  	</widget>
						</row>
						<row>
							<widget>
								<textarea width="710px" height="125px" case="mixed"/>
							</widget>
						</row>
					</TablePanel>
						</VerticalPanel>
					</tab>
					<tab key="tab4" text="{resource:getString($constants,'analysisIntrnlCmnts')}">
						<VerticalPanel height="170px" width="730px">
							<widget halign="center">
								<appButton action="analysisNoteInt" key="analysisNoteInt" onclick="this" style="Button" enabledStates="">
									<HorizontalPanel>
										<AbsolutePanel style="StandardNoteButtonImage"/>
										<text><xsl:value-of select="resource:getString($constants,'edit')"/></text>
									</HorizontalPanel>
								</appButton>
							</widget>
							<widget halign="center">
								<HorizontalPanel style="notesPanelContainer">
									<VerticalPanel height="125px" key="notesPanel" onclick="this" overflowX="auto" overflowY="scroll" style="NotesPanel" valign="top" width="715px"/>
								</HorizontalPanel>
							</widget>
						</VerticalPanel>
					</tab>
					<tab key="tab5" text="{resource:getString($constants,'storage')}">
						<VerticalPanel height="170px" width="730px"/>
					</tab>
					<tab key="tab6" text="{resource:getString($constants,'sampleExtrnlCmnts')}">
						<VerticalPanel height="170px" width="730px">
						<TablePanel style="Form" padding="0" spacing="0">
						<row>
							<widget colspan="2" align="center">
								<appButton action="standardNoteShipping" onclick="this" key="sampleNoteExtButton" style="Button">
									<HorizontalPanel>
              							<AbsolutePanel xsi:type="Absolute" layout="absolute" style="StandardNoteButtonImage"/>
	                					<text><xsl:value-of select='resource:getString($constants,"edit")'/></text>
						             </HorizontalPanel>
						    	</appButton>
						  	</widget>
						</row>
						<row>
							<widget>
								<textarea width="710px" height="125px" case="mixed"/>
							</widget>
						</row>
					</TablePanel>
						</VerticalPanel>
					</tab>
					<tab key="tab7" text="{resource:getString($constants,'sampleIntrnlCmnts')}">
						<VerticalPanel height="170px" width="730px">
							<widget halign="center">
								<appButton action="standardNote" key="sampleNoteIntButton" onclick="this" style="Button">
									<HorizontalPanel>
										<AbsolutePanel style="StandardNoteButtonImage"/>
										<text><xsl:value-of select="resource:getString($constants,'edit')"/></text>
									</HorizontalPanel>
								</appButton>
							</widget>
							<widget halign="center">
								<HorizontalPanel style="notesPanelContainer">
									<VerticalPanel height="125px" key="notesPanel" onclick="this" overflowX="auto" overflowY="scroll" style="NotesPanel" valign="top" width="715px"/>
								</HorizontalPanel>
							</widget>
						</VerticalPanel>
					</tab>
				</TabPanel>
			</VerticalPanel>
		</VerticalPanel>
	</display>
	<rpc key="display">
		<integer key="{sampleMetaMap:getId($sample)}" required="true"/>
		<integer key="{sampleMetaMap:getAccessionNumber($sample)}" required="true"/>
		<integer key="orderNumber" required="false"/>
		<date key="{sampleMetaMap:getCollectionDate($sample)}" begin="0" end="2" required="false"/>
		<date key="{sampleMetaMap:getCollectionTime($sample)}" begin="3" end="5" required="false"/>
		<date key="{sampleMetaMap:getReceivedDate($sample)}" begin="0" end="2" required="false"/>
		<dropdown key="{sampleMetaMap:getStatusId($sample)}" required="true"/>
		<string key="{sampleMetaMap:getClientReference($sample)}" required="false"/>
		
		<rpc key="envInfo">
			<check key="{envMeta:getIsHazardous($env)}"/>
			<string key="{envMeta:getDescription($env)}" required="false"/>		
			<string key="{envMeta:getCollector($env)}" required="false"/>		
			<string key="{envMeta:getCollectorPhone($env)}" required="false"/>	
				
			<rpc key="locationInfo">
				<string key="{envMeta:getSamplingLocation($env)}" required="false"/>
				<string key="{addressMeta:getMultipleUnit($address)}"/>
				<string key="{addressMeta:getStreetAddress($address)}"/>
				<string key="{addressMeta:getCity($address)}"/>
				<dropdown key="{addressMeta:getState($address)}"/>
				<string key="{addressMeta:getZipCode($address)}"/>
				<dropdown key="{addressMeta:getCountry($address)}"/>
			</rpc>   
		</rpc>
		
		<rpc key="sampleItemAndAnalysis">
			<tree key="itemsTestsTree"/>
		</rpc>
		
		<rpc key="orgprojectInfo">
			<dropdown key="{projectMeta:getName($project)}" required="false"/>
			<dropdown key="{orgMeta:getName($org)}"/>
			<dropdown key="billTo" required="false"/>
			
			<rpc key="sampleOrganization">
				<table key="sampleOrganizationTable">
					<dropdown/>
					<integer/>
					<dropdown/>
					<string/>
					<string/>
				</table>
			</rpc>
			
			<rpc key="sampleProject">
				<table key="sampleProjectTable">
					<dropdown/>
					<string/>
					<check/>
				</table>
			</rpc>
		</rpc>
		
		<rpc key="sampleItemForm">
			<dropdown key="{sampleItemMetaMap:getTypeOfSampleId($sampleItem)}"/>
			<dropdown key="{sampleItemMetaMap:getContainerId($sampleItem)}"/>
			<string key="{sampleItemMetaMap:getContainerReference($sampleItem)}"/>
			<integer key="{sampleItemMetaMap:getQuantity($sampleItem)}"/>
			<dropdown key="{sampleItemMetaMap:getUnitOfMeasureId($sampleItem)}"/>
		</rpc>
		
		<rpc key="testInfoResult">
			<!--	empty	 -->
		</rpc>
		
		<rpc key="analysis">
			<dropdown key="{sampleTestMetaMap:getName($test)}" required="false"/>
			<dropdown key="{methodMeta:getName($method)}" required="false"/>
			<dropdown key="{analysisMetaMap:getStatusId($analysis)}" required="false"/>
			<integer key="{analysisMetaMap:getRevision($analysis)}" required="false"/>
			<check key="{analysisMetaMap:getIsReportable($analysis)}"/>
			<dropdown key="{analysisMetaMap:getSectionId($analysis)}" required="false"/>
			<date begin="0" end="2" key="{analysisMetaMap:getStartedDate($analysis)}" required="false"/>
			<date begin="0" end="2" key="{analysisMetaMap:getCompletedDate($analysis)}" required="false"/>
			<date begin="0" end="2" key="{analysisMetaMap:getReleasedDate($analysis)}" required="false"/>
			<date begin="0" end="2" key="{analysisMetaMap:getPrintedDate($analysis)}" required="false"/>
		</rpc>
		
		<rpc key="analysisExternalComment">
			<!--	empty	 -->
		</rpc>
		
		<rpc key="analysisInternalComments">
			<!--	empty	 -->
		</rpc>
<!--		-->
<!--		<rpc key="storage">-->
	 
<!--		</rpc>-->
<!--		-->
<!--		<rpc key="sampleExternalComment">-->
	 
<!--		</rpc>-->
<!--		-->
<!--		<rpc key="sampleInternalComments">-->
	 
<!--		</rpc>-->
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>