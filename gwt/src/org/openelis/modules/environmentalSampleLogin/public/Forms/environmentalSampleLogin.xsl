
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

<xsl:stylesheet extension-element-prefixes="resource" version="1.0" xmlns:addressMeta="xalan://org.openelis.meta.AddressMeta" xmlns:analysisMetaMap="xalan://org.openelis.metamap.AnalysisMetaMap" xmlns:envMeta="xalan://org.openelis.metamap.SampleEnvironmentalMetaMap" xmlns:locale="xalan://java.util.Locale" xmlns:methodMeta="xalan://org.openelis.meta.MethodMeta" xmlns:orgMeta="xalan://org.openelis.meta.OrganizationMeta" xmlns:projectMeta="xalan://org.openelis.meta.ProjectMeta" xmlns:resource="xalan://org.openelis.util.UTFResource" xmlns:sampleItemMetaMap="xalan://org.openelis.metamap.SampleItemMetaMap" xmlns:sampleMetaMap="xalan://org.openelis.metamap.SampleMetaMap" xmlns:sampleOrgMetaMap="xalan://org.openelis.metamap.SampleOrganizationMetaMap" xmlns:sampleProjectMetaMap="xalan://org.openelis.metamap.SampleProjectMetaMap" xmlns:sampleTestMetaMap="xalan://org.openelis.metamap.SampleTestMetaMap" xmlns:sectionMeta="xalan://org.openelis.meta.SectionMeta" xmlns:xalan="http://xml.apache.org/xalan" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xsi:noNamespaceSchemaLocation="../../../../../../../../OpenELIS-Lib/src/org/openelis/gwt/public/ScreenSchema.xsd" xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform ../../../../../../../../OpenELIS-Lib/src/org/openelis/gwt/public/XSLTSchema.xsd">
  <xsl:import href="aToZOneColumn.xsl" />
  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource" />
  </xalan:component>
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale" />
  </xalan:component>
  <xalan:component prefix="envMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.SampleEnvironmentalMetaMap" />
  </xalan:component>
  <xalan:component prefix="sampleMetaMap">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.SampleMetaMap" />
  </xalan:component>
  <xalan:component prefix="addressMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.AddressMeta" />
  </xalan:component>
  <xalan:component prefix="sampleItemMetaMap">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.SampleItemMetaMap" />
  </xalan:component>
  <xalan:component prefix="sampleOrgMetaMap">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.SampleOrganizationMetaMap" />
  </xalan:component>
  <xalan:component prefix="orgMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrganizationMeta" />
  </xalan:component>
  <xalan:component prefix="sampleProjectMetaMap">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.SampleProjectMetaMap" />
  </xalan:component>
  <xalan:component prefix="projectMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.ProjectMeta" />
  </xalan:component>
  <xalan:component prefix="analysisMetaMap">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.AnalysisMetaMap" />
  </xalan:component>
  <xalan:component prefix="sampleTestMetaMap">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.SampleTestMetaMap" />
  </xalan:component>
  <xalan:component prefix="sectionMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.SectionMeta" />
  </xalan:component>
  <xalan:component prefix="methodMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.MethodMeta" />
  </xalan:component>
  <xsl:template match="doc">
    <xsl:variable name="env" select="envMeta:new()" />
    <xsl:variable name="sample" select="envMeta:getSample($env)" />
    <xsl:variable name="address" select="envMeta:getAddress($env)" />
    <xsl:variable name="sampleItem" select="sampleMetaMap:getSampleItem($sample)" />
    <xsl:variable name="analysis" select="sampleItemMetaMap:getAnalysis($sampleItem)" />
    <xsl:variable name="test" select="analysisMetaMap:getTest($analysis)" />
    <xsl:variable name="section" select="analysisMetaMap:getSection($analysis)" />
    <xsl:variable name="method" select="sampleTestMetaMap:getMethod($test)" />
    <xsl:variable name="sampleOrg" select="sampleMetaMap:getSampleOrganization($sample)" />
    <xsl:variable name="sampleProject" select="sampleMetaMap:getSampleProject($sample)" />
    <xsl:variable name="parentSampleItem" select="sampleItemMetaMap:getParentSampleItem($sampleItem)" />
    <xsl:variable name="org" select="sampleOrgMetaMap:getOrganization($sampleOrg)" />
    <xsl:variable name="project" select="sampleProjectMetaMap:getProject($sampleProject)" />
    <xsl:variable name="language">
      <xsl:value-of select="locale" />
    </xsl:variable>
    <xsl:variable name="props">
      <xsl:value-of select="props" />
    </xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="EnvironmentalSampleLogin" name="{resource:getString($constants,'environmentalSampleLogin')}">
      <VerticalPanel padding="0" spacing="0">

<!--button panel code-->

        <AbsolutePanel spacing="0" style="ButtonPanelContainer">
          <HorizontalPanel>
            <xsl:call-template name="queryButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="previousButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="nextButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="buttonPanelDivider" />
            <xsl:call-template name="addButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="updateButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="buttonPanelDivider" />
            <xsl:call-template name="commitButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="abortButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
          </HorizontalPanel>
        </AbsolutePanel>

<!--end button panel code-->

        <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
          <TablePanel style="Form">
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'accessionNum')" />:
              </text>
              <textbox key="{sampleMetaMap:getAccessionNumber($sample)}" width="75px" tab="orderNumber,billTo" field="Integer" required="true" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'orderNum')" />:
              </text>
              <textbox key="orderNumber" width="75px" tab="{sampleMetaMap:getCollectionDate($sample)},{sampleMetaMap:getAccessionNumber($sample)}" field="Integer" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'collected')" />:
              </text>
              <calendar key="{sampleMetaMap:getCollectionDate($sample)}" begin="0" end="2" width="80px" pattern="{resource:getString($constants,'datePattern')}" tab="{sampleMetaMap:getCollectionTime($sample)},orderNumber" required="true" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'time')" />:
              </text>
              <textbox key="{sampleMetaMap:getCollectionTime($sample)}" begin="3" end="5" width="60px" pattern="{resource:getString($constants,'timePattern')}" tab="{sampleMetaMap:getReceivedDate($sample)},{sampleMetaMap:getCollectionDate($sample)}" field="Date" />
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'received')" />:
              </text>
              <calendar key="{sampleMetaMap:getReceivedDate($sample)}" begin="0" end="5" width="110px" pattern="{resource:getString($constants,'dateTimePattern')}" tab="{sampleMetaMap:getStatusId($sample)},{sampleMetaMap:getCollectionTime($sample)}" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'status')" />:
              </text>
              <dropdown key="{sampleMetaMap:getStatusId($sample)}" width="110px" tab="{sampleMetaMap:getClientReference($sample)},{sampleMetaMap:getReceivedDate($sample)}" field="Integer" required="true" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'clntRef')" />:
              </text>
              <widget colspan="3">
                <textbox key="{sampleMetaMap:getClientReference($sample)}" width="175px" tab="{envMeta:getIsHazardous($env)},{sampleMetaMap:getStatusId($sample)}" field="String" />
              </widget>
            </row>
          </TablePanel>
          <VerticalPanel width="98%" style="subform">
            <text style="FormTitle">
              <xsl:value-of select="resource:getString($constants,'envInfo')" />
            </text>
            <TablePanel width="100%" style="Form">
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'hazardous')" />:
                </text>
                <check key="{envMeta:getIsHazardous($env)}" tab="{envMeta:getDescription($env)},{sampleMetaMap:getClientReference($sample)}" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'desc')" />:
                </text>
                <textbox key="{envMeta:getDescription($env)}" width="315px" tab="{envMeta:getCollector($env)},{envMeta:getIsHazardous($env)}" field="String" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'collector')" />:
                </text>
                <textbox key="{envMeta:getCollector($env)}" width="235px" tab="{envMeta:getCollectorPhone($env)},{envMeta:getDescription($env)}" field="String" required="true" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'phone')" />:
                </text>
                <textbox key="{envMeta:getCollectorPhone($env)}" width="120px" tab="{envMeta:getSamplingLocation($env)},{envMeta:getCollector($env)}" field="String" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'location')" />:
                </text>
                <HorizontalPanel>
                  <textbox key="{envMeta:getSamplingLocation($env)}" width="175px" field="String" />
                  <appButton key="locButton" style="Button">
                    <AbsolutePanel style="LookupButtonImage" />
                  </appButton>
                </HorizontalPanel>
              </row>
            </TablePanel>
          </VerticalPanel>
          <HorizontalPanel>
            <VerticalPanel style="subform">
              <text style="FormTitle">
                <xsl:value-of select="resource:getString($constants,'itemsAndAnalyses')" />
              </text>
              <VerticalPanel style="WhiteContentPanel">
                <tree key="itemsTestsTree" width="auto" maxRows="4" showScroll="ALWAYS" tab="{projectMeta:getName($project)},{envMeta:getSamplingLocation($env)}">
                  <header>
                    <col width="280" header="{resource:getString($constants,'itemTests')}" />
                    <col width="130" header="{resource:getString($constants,'typeStatus')}" />
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
                      <dropdown width="110px" case="LOWER" field="String" />
                    </col>
                  </leaf>
                </tree>
                <HorizontalPanel style="TableButtonFooter">
                  <appButton key="addItemButton" style="Button" action="addItem">
                    <HorizontalPanel>
                      <AbsolutePanel style="AddRowButtonImage" />
                      <text>
                        <xsl:value-of select="resource:getString($constants,'addItem')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                  <appButton key="addTestButton" style="Button" action="addTest">
                    <HorizontalPanel>
                      <AbsolutePanel style="AddRowButtonImage" />
                      <text>
                        <xsl:value-of select="resource:getString($constants,'addTest')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                  <appButton key="removeRowButton" style="Button" action="removeRow">
                    <HorizontalPanel>
                      <AbsolutePanel style="RemoveRowButtonImage" />
                      <text>
                        <xsl:value-of select="resource:getString($constants,'removeRow')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                </HorizontalPanel>
              </VerticalPanel>
            </VerticalPanel>
            <VerticalPanel style="subform">
              <text style="FormTitle">
                <xsl:value-of select="resource:getString($constants,'organizationInfo')" />
              </text>
              <TablePanel style="Form">
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'project')" />:
                  </text>
                  <HorizontalPanel>
                    <autoComplete key="{projectMeta:getName($project)}" width="175px" case="UPPER" popWidth="auto" field="Integer">
                      <col width="115" header="Name" />
                      <col width="190" header="Desc" />
                    </autoComplete>
                    <appButton key="projectLookup" style="Button">
                      <AbsolutePanel style="LookupButtonImage" />
                    </appButton>
                  </HorizontalPanel>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'reportTo')" />:
                  </text>
                  <HorizontalPanel>
                    <autoComplete key="{orgMeta:getName($org)}" width="175px" case="UPPER" popWidth="auto" field="Integer">
                      <col width="180" header="Name" />
                      <col width="110" header="Street" />
                      <col width="100" header="City" />
                      <col width="20" header="St" />
                    </autoComplete>
                    <appButton key="reportToLookup" style="Button">
                      <AbsolutePanel style="LookupButtonImage" />
                    </appButton>
                  </HorizontalPanel>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'billTo')" />:
                  </text>
                  <HorizontalPanel>
                    <autoComplete key="billTo" width="175px" case="UPPER" popWidth="auto" field="Integer">
                      <col width="180" header="Name" />
                      <col width="110" header="Street" />
                      <col width="100" header="City" />
                      <col width="20" header="St" />
                    </autoComplete>
                    <appButton key="billToLookup" style="Button">
                      <AbsolutePanel style="LookupButtonImage" />
                    </appButton>
                  </HorizontalPanel>
                </row>
              </TablePanel>
            </VerticalPanel>
          </HorizontalPanel>
          <VerticalPanel height="5px" />
          <TabPanel key="sampleItemTabPanel" height="170px">
            <tab key="tab0" text="{resource:getString($constants,'sampleItem')}">
              <VerticalPanel width="730px" height="170px">
                <TablePanel padding="0" spacing="0" style="Form">
                  <row>
                    <text style="Prompt">Sample Type:</text>
                    <dropdown key="{sampleItemMetaMap:getTypeOfSampleId($sampleItem)}" width="150px" field="Integer" />
                  </row>
                  <row>
                    <text style="Prompt">Container:</text>
                    <dropdown key="{sampleItemMetaMap:getContainerId($sampleItem)}" width="225px" field="Integer" />
                    <text style="Prompt">Container Reference:</text>
                    <textbox key="{sampleItemMetaMap:getContainerReference($sampleItem)}" width="215px" field="String" />
                  </row>
                  <row>
                    <text style="Prompt">Qty:</text>
                    <textbox key="{sampleItemMetaMap:getQuantity($sampleItem)}" width="150px" field="Double" />
                    <text style="Prompt">Unit:</text>
                    <dropdown key="{sampleItemMetaMap:getUnitOfMeasureId($sampleItem)}" width="150px" field="Integer" />
                  </row>
                </TablePanel>
              </VerticalPanel>
            </tab>
            <tab key="tab1" text="{resource:getString($constants,'analysis')}">
              <VerticalPanel width="730px" height="170px">
                <TablePanel padding="0" spacing="0" style="Form">
                  <row>
                    <text style="Prompt">Test:</text>
                    <autoComplete key="{sampleTestMetaMap:getName($test)}" width="150px" case="LOWER" popWidth="auto" field="Integer">
                      <col width="150" header="Test" />
                      <col width="150" header="Method" />
                    </autoComplete>
                    <text style="Prompt">Method:</text>
                    <autoComplete key="{methodMeta:getName($method)}" width="150px" case="LOWER" popWidth="auto" field="Integer">
                      <col width="150" header="Method" />
                    </autoComplete>
                  </row>
                  <row>
                    <text style="Prompt">Status:</text>
                    <dropdown key="{analysisMetaMap:getStatusId($analysis)}" width="150px" field="Integer" />
                    <text style="Prompt">Revision:</text>
                    <textbox key="{analysisMetaMap:getRevision($analysis)}" width="60px" field="Integer" />
                  </row>
                  <row>
                    <text style="Prompt">Reportable:</text>
                    <check key="{analysisMetaMap:getIsReportable($analysis)}" />
                    <text style="Prompt">Section:</text>
                    <autoComplete key="{sectionMeta:getName($section)}" width="150px" case="UPPER" popWidth="auto" field="Integer">
                      <col width="150" header="Name" />
                    </autoComplete>
                  </row>
                  <row>
                    <text style="Prompt">Started:</text>
                    <calendar key="{analysisMetaMap:getStartedDate($analysis)}" begin="0" end="5" pattern="{resource:getString($constants,'dateTimePattern')}" />
                    <text style="Prompt">Completed:</text>
                    <calendar key="{analysisMetaMap:getCompletedDate($analysis)}" begin="0" end="5" pattern="{resource:getString($constants,'dateTimePattern')}" />
                  </row>
                  <row>
                    <text style="Prompt">Released:</text>
                    <calendar key="{analysisMetaMap:getReleasedDate($analysis)}" begin="0" end="5" pattern="{resource:getString($constants,'dateTimePattern')}" />
                    <text style="Prompt">Printed:</text>
                    <calendar key="{analysisMetaMap:getPrintedDate($analysis)}" begin="0" end="5" pattern="{resource:getString($constants,'dateTimePattern')}" />
                  </row>
                </TablePanel>
              </VerticalPanel>
            </tab>
            <tab key="tab2" text="{resource:getString($constants,'testResults')}">
              <VerticalPanel width="730px" height="170px">

<!--						-->


<!--							
<table maxRows="6" width="auto">
  -->


<!--						     
<headers>Analyte,Result1,Result2,Result3
</headers>
  -->


<!--						     
<widths>175,100,100,100
</widths>
  -->


<!--						     
<editors>
  -->


<!--						       
<label/>
  -->


<!--						       
<label/>
  -->


<!--						       
<label/>
  -->


<!--						       
<label/>
  -->


<!--						     
</editors>
  -->


<!--						     
<fields>
  -->


<!--						       
<string/>
  -->


<!--						       
<string/>
  -->


<!--						       
<string/>
  -->


<!--						       
<string/>
  -->


<!--						     
</fields>
  -->


<!--						     
<sorts>true,true,true,true
</sorts>
  -->


<!--							
<filters>false,false,false,false
</filters>
  -->


<!--							
<colAligns>left,left,left,left
</colAligns>
  -->


<!--						   
</table>
  -->
</VerticalPanel>
            </tab>
            <tab key="tab3" text="{resource:getString($constants,'analysisExtrnlCmnts')}">
              <VerticalPanel width="100%" height="170px">
                <TablePanel padding="0" spacing="0" style="Form">
                  <row>
                    <notes key="anExNotesPanel" width="690px" height="150px" />
                  </row>
                  <row>
                    <appButton key="anExNoteButton" style="Button" action="standardNote">
                      <HorizontalPanel>
                        <AbsolutePanel style="StandardNoteButtonImage" />
                        <text>
                          <xsl:value-of select="resource:getString($constants,'addNote')" />
                        </text>
                      </HorizontalPanel>
                    </appButton>
                  </row>
                </TablePanel>
              </VerticalPanel>
            </tab>
            <tab key="tab4" text="{resource:getString($constants,'analysisIntrnlCmnts')}">
              <VerticalPanel width="100%" height="170px">
                <TablePanel padding="0" spacing="0" style="Form">
                  <row>
                    <notes key="anIntNotesPanel" width="690px" height="150px" />
                  </row>
                  <row>
                    <appButton key="anIntNoteButton" style="Button" action="standardNote">
                      <HorizontalPanel>
                        <AbsolutePanel style="StandardNoteButtonImage" />
                        <text>
                          <xsl:value-of select="resource:getString($constants,'addNote')" />
                        </text>
                      </HorizontalPanel>
                    </appButton>
                  </row>
                </TablePanel>
              </VerticalPanel>
            </tab>
            <tab key="tab5" text="{resource:getString($constants,'storage')}">
              <VerticalPanel height="170px">
                <table key="storageTable" width="auto" maxRows="6" showScroll="ALWAYS" title="">
                  <col width="155" header="User">
                    <label />
                  </col>
                  <col width="230" header="Location">
                    <autoComplete key="" width="210px" case="LOWER" popWidth="auto" field="Integer">
                      <col width="180" header="Name" />
                    </autoComplete>
                  </col>
                  <col width="130" header="Check In">
                    <calendar key="" begin="0" end="4" width="110" pattern="{resource:getString($constants,'dateTimePattern')}" />
                  </col>
                  <col width="130" header="Check Out">
                    <calendar key="" begin="0" end="4" width="110" pattern="{resource:getString($constants,'dateTimePattern')}" />
                  </col>
                </table>
                <HorizontalPanel>
                  <appButton key="addStorageButton" style="Button" action="addStorage">
                    <HorizontalPanel>
                      <AbsolutePanel style="AddRowButtonImage" />
                      <text>
                        <xsl:value-of select="resource:getString($constants,'addRow')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                  <appButton key="removeStorageButton" style="Button" action="removeStorage">
                    <HorizontalPanel>
                      <AbsolutePanel style="RemoveRowButtonImage" />
                      <text>
                        <xsl:value-of select="resource:getString($constants,'removeRow')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                </HorizontalPanel>
              </VerticalPanel>
            </tab>
            <tab key="tab6" text="{resource:getString($constants,'sampleExtrnlCmnts')}">
              <VerticalPanel width="100%" height="170px">
                <TablePanel padding="0" spacing="0" style="Form">
                  <row>
                    <notes key="sampleExtNotesPanel" width="690px" height="150px" />
                  </row>
                  <row>
                    <appButton key="sampleExtNoteButton" style="Button" action="standardNote">
                      <HorizontalPanel>
                        <AbsolutePanel style="StandardNoteButtonImage" />
                        <text>
                          <xsl:value-of select="resource:getString($constants,'addNote')" />
                        </text>
                      </HorizontalPanel>
                    </appButton>
                  </row>
                </TablePanel>
              </VerticalPanel>
            </tab>
            <tab key="tab7" text="{resource:getString($constants,'sampleIntrnlCmnts')}">
              <VerticalPanel width="100%" height="170px">
                <TablePanel padding="0" spacing="0" style="Form">
                  <row>
                    <notes key="sampleIntNotesPanel" width="690px" height="150px" />
                  </row>
                  <row>
                    <appButton key="sampleIntNoteButton" style="Button" action="standardNote">
                      <HorizontalPanel>
                        <AbsolutePanel style="StandardNoteButtonImage" />
                        <text>
                          <xsl:value-of select="resource:getString($constants,'addNote')" />
                        </text>
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
