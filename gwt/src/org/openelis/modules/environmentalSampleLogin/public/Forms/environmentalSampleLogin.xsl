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
		<VerticalPanel spacing="0" padding="0">
		<!--button panel code-->
				<AbsolutePanel spacing="0" style="ButtonPanelContainer">
              <HorizontalPanel>
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
              </HorizontalPanel>
            </AbsolutePanel>
				<!--end button panel code-->
				<VerticalPanel style="WhiteContentPanel" spacing="0" padding="0">		
					<TablePanel style="Form">
						<row>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'accessionNum')"/>:</text>
							<textbox key="{sampleMetaMap:getAccessionNumber($sample)}"  width="75px" tab="orderNumber,billTo" required="true" field="Integer"/>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'orderNum')"/>:</text>
							<textbox key="orderNumber"  width="75px" tab="{sampleMetaMap:getCollectionDate($sample)},{sampleMetaMap:getAccessionNumber($sample)}" field="Integer"/>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'collected')"/>:</text>
							<calendar begin="0" end="2" key="{sampleMetaMap:getCollectionDate($sample)}" width="75px" tab="{sampleMetaMap:getCollectionTime($sample)},orderNumber"/>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'time')"/>:</text>
							<calendar begin="3" end="5" key="{sampleMetaMap:getCollectionTime($sample)}" width="60px" tab="{sampleMetaMap:getReceivedDate($sample)},{sampleMetaMap:getCollectionDate($sample)}"/>
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'received')"/>:</text>
							<calendar key="{sampleMetaMap:getReceivedDate($sample)}" begin="0" end="2" width="110px" tab="{sampleMetaMap:getStatusId($sample)},{sampleMetaMap:getCollectionTime($sample)}"/>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'status')"/>:</text>
							<dropdown key="{sampleMetaMap:getStatusId($sample)}" case="mixed" width="110px" tab="{sampleMetaMap:getClientReference($sample)},{sampleMetaMap:getReceivedDate($sample)}" required="true" field="Integer"/>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'clntRef')"/>:</text>
							<widget colspan="3">
								<textbox key="{sampleMetaMap:getClientReference($sample)}" width="175px" tab="{envMeta:getIsHazardous($env)},{sampleMetaMap:getStatusId($sample)}" field="String"/>					
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
							<textbox key="{envMeta:getDescription($env)}" tab="{envMeta:getCollector($env)},{envMeta:getIsHazardous($env)}" width="315px" field="String"/>		
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'collector')"/>:</text>
							<textbox key="{envMeta:getCollector($env)}" tab="{envMeta:getCollectorPhone($env)},{envMeta:getDescription($env)}" width="235px" field="String"/>		
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'phone')"/>:</text>
							<textbox key="{envMeta:getCollectorPhone($env)}" tab="{envMeta:getSamplingLocation($env)},{envMeta:getCollector($env)}" width="120px" field="String"/>		
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'location')"/>:</text>
							<HorizontalPanel> 
							<textbox key="{envMeta:getSamplingLocation($env)}" onchange="this" width="175px" field="String"/>
							<appButton key="locButton" style="Button">
									<AbsolutePanel style="LookupButtonImage"/>
							</appButton>
							</HorizontalPanel>
						</row>
					</TablePanel>
					</VerticalPanel>
				<HorizontalPanel>
					<VerticalPanel style="subform">
						<text style="FormTitle"><xsl:value-of select="resource:getString($constants,'itemsAndAnalyses')"/></text>
						<VerticalPanel style="WhiteContentPanel">
						<tree key="itemsTestsTree" width="auto" showScroll="ALWAYS" maxRows="4" tab="{projectMeta:getName($project)},{envMeta:getSamplingLocation($env)}">
							<header>
								<col header="{resource:getString($constants,'itemTests')}" width="280"/>
								<col header="{resource:getString($constants,'typeStatus')}" width="130"/>
							</header>
							<leaf key="sampleItem">
								<col>
									<label />
								</col>
								<col>
									<label />
								</col>
							</leaf>
							<leaf key="analysis">
								<col>
									<label />
								</col>
								<col>
									<dropdown case="lower" width="110px" field="String" />
								</col>
							</leaf>
	                  	</tree>
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
							<appButton action="removeRow" key="removeRowButton" style="Button">
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
								<autoComplete key="{projectMeta:getName($project)}" width="175px" popWidth="auto" cat="project" case="upper" field="Integer">
									<col header="Name" width="115"/>
									<col header="Desc" width="190"/>				
								</autoComplete>
								<appButton key="projectLookup" style="Button">
									<AbsolutePanel style="LookupButtonImage"/>
								</appButton>
							</HorizontalPanel>
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'reportTo')"/>:</text>
							<HorizontalPanel> 
								<autoComplete key="{orgMeta:getName($org)}" cat="organization" width="175px" popWidth="auto" case="upper" field="Integer">	
									<col header="Name" width="180"/>
									<col header="Street" width="110"/>
									<col header="City" width="100"/>
									<col header="St" width="20"/>											
								</autoComplete>
								<appButton key="reportToLookup" style="Button">
									<AbsolutePanel style="LookupButtonImage"/>
								</appButton>
							</HorizontalPanel>
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select="resource:getString($constants,'billTo')"/>:</text>
							<HorizontalPanel> 
								<autoComplete key="billTo" cat="organization" width="175px" popWidth="auto" case="upper" field="Integer">
									<col header="Name" width="180"/>
									<col header="Street" width="110"/>
									<col header="City" width="100"/>
									<col header="St" width="20"/>												
								</autoComplete>
								<appButton key="billToLookup" style="Button">
									<AbsolutePanel style="LookupButtonImage"/>
								</appButton>
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
									<dropdown case="mixed" key="{sampleItemMetaMap:getTypeOfSampleId($sampleItem)}" onchange="this" width="150px" field="Integer"/>
									
								</row>
								<row>
									<text style="Prompt">Container:</text>
									<dropdown case="mixed" key="{sampleItemMetaMap:getContainerId($sampleItem)}" onchange="this" width="225px" field="Integer"/>
									<text style="Prompt">Container Reference:</text>
									<textbox case="mixed" key="{sampleItemMetaMap:getContainerReference($sampleItem)}" onchange="this" width="215px" field="String"/>
								</row>
								<row>
									<text style="Prompt">Qty:</text>
									<textbox case="mixed" key="{sampleItemMetaMap:getQuantity($sampleItem)}" onchange="this" width="150px" field="Double"/>
									<text style="Prompt">Unit:</text>
									<dropdown case="mixed" key="{sampleItemMetaMap:getUnitOfMeasureId($sampleItem)}" onchange="this" width="150px" field="Integer"/>
								</row>
							</TablePanel>
						</VerticalPanel>
					</tab>
					<tab key="tab1" text="{resource:getString($constants,'analysis')}">
						<VerticalPanel height="170px" width="730px">
							<TablePanel style="Form" spacing="0" padding="0">
								<row>
									<text style="Prompt">Test:</text>
									<autoComplete key="{sampleTestMetaMap:getName($test)}" cat="testMethod" width="150px" popWidth="auto" case="lower" field="Integer">
										<col header="Test" width="150"/>
										<col header="Method" width="150"/>												
									</autoComplete>
									<text style="Prompt">Method:</text>
									<autoComplete key="{methodMeta:getName($method)}" cat="testMethod" width="150px" popWidth="auto" case="lower" field="Integer">
										<col header="Method" width="150"/>												
									</autoComplete>
								</row>
								<row>
									<text style="Prompt">Status:</text>
									<dropdown case="mixed" key="{analysisMetaMap:getStatusId($analysis)}" onchange="this" width="150px" field="Integer"/>
									<text style="Prompt">Revision:</text>
									<textbox case="mixed" key="{analysisMetaMap:getRevision($analysis)}" onchange="this" width="60px" field="Integer"/>
								</row>
								<row>
									<text style="Prompt">Reportable:</text>
									<check key="{analysisMetaMap:getIsReportable($analysis)}"/>
									<text style="Prompt">Section:</text>
									<autoComplete key="{analysisMetaMap:getSectionId($analysis)}" cat="section" width="150px" popWidth="auto" case="upper" field="Integer">
										<col header="Name" width="150"/>												
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
<!--						-->
<!--							<table maxRows="6" width="auto">-->
<!--						     <headers>Analyte,Result1,Result2,Result3</headers>-->
<!--						     <widths>175,100,100,100</widths>-->
<!--						     <editors>-->
<!--						       <label/>-->
<!--						       <label/>-->
<!--						       <label/>-->
<!--						       <label/>-->
<!--						     </editors>-->
<!--						     <fields>-->
<!--						       <string/>-->
<!--						       <string/>-->
<!--						       <string/>-->
<!--						       <string/>-->
<!--						     </fields>-->
<!--						     <sorts>true,true,true,true</sorts>-->
<!--							<filters>false,false,false,false</filters>-->
<!--							<colAligns>left,left,left,left</colAligns>-->
<!--						   </table>-->
						</VerticalPanel>
					</tab>
					<tab key="tab3" text="{resource:getString($constants,'analysisExtrnlCmnts')}">
					<VerticalPanel height="170px" width="100%">
						<TablePanel style="Form" padding="0" spacing="0">
						<row>
							<notes key="anExNotesPanel" width="690px" height="150px"/>
						</row>
						<row>
							<appButton action="standardNote" key="anExNoteButton" style="Button">
                          		<HorizontalPanel>
                            		<AbsolutePanel style="StandardNoteButtonImage"/>
                            		<text><xsl:value-of select="resource:getString($constants,'addNote')"/></text>
                          		</HorizontalPanel>
                        	</appButton>
                       	</row>
					</TablePanel>
						</VerticalPanel>
					</tab>
					<tab key="tab4" text="{resource:getString($constants,'analysisIntrnlCmnts')}">
					<VerticalPanel height="170px" width="100%">
						<TablePanel style="Form" padding="0" spacing="0">
						<row>
							<notes key="anIntNotesPanel" width="690px" height="150px"/>
						</row>
						<row>
							<appButton action="standardNote" key="anIntNoteButton" style="Button">
                          		<HorizontalPanel>
                            		<AbsolutePanel style="StandardNoteButtonImage"/>
                            		<text><xsl:value-of select="resource:getString($constants,'addNote')"/></text>
                          		</HorizontalPanel>
                        	</appButton>
                       	</row>
					</TablePanel>
						</VerticalPanel>
					</tab>
					<tab key="tab5" text="{resource:getString($constants,'storage')}">
						<VerticalPanel height="170px" width="730px"/>
					</tab>
					<tab key="tab6" text="{resource:getString($constants,'sampleExtrnlCmnts')}">
					<VerticalPanel height="170px" width="100%">
						<TablePanel style="Form" padding="0" spacing="0">
						<row>
							<notes key="sampleExtNotesPanel" width="690px" height="150px"/>
						</row>
						<row>
							<appButton action="standardNote" key="sampleExtNoteButton" style="Button">
                          		<HorizontalPanel>
                            		<AbsolutePanel style="StandardNoteButtonImage"/>
                            		<text><xsl:value-of select="resource:getString($constants,'addNote')"/></text>
                          		</HorizontalPanel>
                        	</appButton>
                       	</row>
					</TablePanel>
						</VerticalPanel>
					</tab>
					<tab key="tab7" text="{resource:getString($constants,'sampleIntrnlCmnts')}">
					<VerticalPanel height="170px" width="100%">
						<TablePanel style="Form" padding="0" spacing="0">
						<row>
							<notes key="sampleIntNotesPanel" width="690px" height="150px"/>
						</row>
						<row>
							<appButton action="standardNote" key="sampleIntNoteButton" style="Button">
                          		<HorizontalPanel>
                            		<AbsolutePanel style="StandardNoteButtonImage"/>
                            		<text><xsl:value-of select="resource:getString($constants,'addNote')"/></text>
                          		</HorizontalPanel>
                        	</appButton>
                       	</row>
					</TablePanel>
						</VerticalPanel>
					</tab>
				</TabPanel>
			</VerticalPanel>
		</VerticalPanel>
</screen>
  </xsl:template>
</xsl:stylesheet>