
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
<xsl:stylesheet
  extension-element-prefixes="resource"
  version="1.0"
  xmlns:locale="xalan://java.util.Locale"
  xmlns:meta="xalan://org.openelis.meta.SampleWebMeta"
  xmlns:resource="xalan://org.openelis.util.UTFResource"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:noNamespaceSchemaLocation="http://openelis.uhl.uiowa.edu/schema/ScreenSchema.xsd"
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd">

  <xsl:import href="IMPORT/button.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="DataView" name="{resource:getString($constants,'dataView')}">
      <VerticalPanel padding="0" spacing="0">
        <AbsolutePanel spacing="0" style="ButtonPanelContainer">
         <HorizontalPanel>
          <fileUpload key="fileUpload" service="org.openelis.modules.dataDump.server.DataDumpService">
          <appButton key="chooseQueryButton" style="ButtonPanelButton">
            <HorizontalPanel>
             <AbsolutePanel style="OpenButtonImage" />
              <text>
                <xsl:value-of select="resource:getString($constants,'openQuery')" />
              </text>
            </HorizontalPanel>
          </appButton>
          </fileUpload>
          <appButton key="saveQueryButton" style="ButtonPanelButton">
            <HorizontalPanel>
             <AbsolutePanel style="SaveButtonImage" />
              <text>
                <xsl:value-of select="resource:getString($constants,'saveQuery')" />
              </text>
            </HorizontalPanel>
          </appButton>          
          <appButton key="executeQueryButton" style="ButtonPanelButton">
            <HorizontalPanel>
              <AbsolutePanel style="NextButtonImage" />
              <text>
                <xsl:value-of select="resource:getString($constants,'executeQuery')" />
              </text>
            </HorizontalPanel>
          </appButton>
            </HorizontalPanel>
          </AbsolutePanel>
        <VerticalPanel style="WhiteContentPanel">  
        <VerticalPanel height="5" />        
        <TabPanel height="410" key="tabPanel" width="630">
          <tab text="{resource:getString($constants,'query')}">
            <VerticalPanel>
              <TablePanel style="Form" width="400">
                <row>
                  <widget colspan="5">
                    <text style="heading">
                      <xsl:value-of select='resource:getString($constants,"analysisFieldSearchBy")' />
                    </text>
                  </widget>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="50" />
                  </widget>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"test")' />:
                  </text>
                  <textbox field="String" key="{meta:getAnalysisTestName()}" case = "LOWER" width="85" tab = "{meta:getAnalysisMethodName()},excludeAuxData" />
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"method")' />:
                  </text>
                  <textbox field="String" key="{meta:getAnalysisMethodName()}" case = "LOWER" width="85" tab = "excludeResultOverride,{meta:getAnalysisTestName()}"/>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="50" />
                  </widget>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'excludeResultOverride')" />:
                  </text>
                  <check key="excludeResultOverride" tab = "{meta:getAnalysisStatusId()},{meta:getAnalysisMethodName()}"/>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'status')" />:
                  </text>
                  <dropdown field="Integer" key="{meta:getAnalysisStatusId()}" popWidth="auto" width="85" tab = "{meta:getAnalysisCompletedDateFrom()},excludeResultOverride"/>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="50" />
                  </widget>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"completed")' />:
                  </text>
                  <calendar begin="0" end="2" key="{meta:getAnalysisCompletedDateFrom()}" pattern="{resource:getString($constants,'datePattern')}" width="90" tab = "{meta:getAnalysisCompletedDateTo()},{meta:getAnalysisStatusId()}"/>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"to")' />:
                  </text>
                  <calendar begin="0" end="2" key="{meta:getAnalysisCompletedDateTo()}" pattern="{resource:getString($constants,'datePattern')}" width="90" tab = "{meta:getAnalysisReleasedDateFrom()},{meta:getAnalysisCompletedDateFrom()}"/>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="50" />
                  </widget>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"released")' />:
                  </text>
                  <calendar begin="0" end="4" key="{meta:getAnalysisReleasedDateFrom()}" pattern="{resource:getString($constants,'dateTimePattern')}" width="125" tab = "{meta:getAnalysisReleasedDateTo()},{meta:getAnalysisCompletedDateTo()}"/>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"to")' />:
                  </text>
                  <calendar begin="0" end="4" key="{meta:getAnalysisReleasedDateTo()}" pattern="{resource:getString($constants,'dateTimePattern')}" width="125" tab = "{meta:getAccessionNumberFrom()},{meta:getAnalysisReleasedDateFrom()}"/>
                </row>         
                <row>
                  <widget>
                    <HorizontalPanel width="20" />
                  </widget>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'reportable')" />:
                  </text>
                  <dropdown field="String" key="{meta:getAnalysisIsReportable()}" popWidth="auto" width="85" tab = "{meta:getAnalysisCompletedDateFrom()},excludeResultOverride"/>
                </row>      
              </TablePanel>
              <TablePanel style="Form">
                <row>
                  <widget colspan="5">
                    <text style="heading">
                      <xsl:value-of select='resource:getString($constants,"sampleFieldSearchBy")' />
                    </text>
                  </widget>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="50" />
                  </widget>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"accessionNum")' />:
                  </text>
                  <textbox field="Integer" key="{meta:getAccessionNumberFrom()}" width="85" tab = "{meta:getAccessionNumberTo()},{meta:getAnalysisReleasedDateTo()}"/>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"to")' />:
                  </text>
                  <textbox field="Integer" key="{meta:getAccessionNumberTo()}" width="85" tab = "{meta:getCollectionDateFrom()},{meta:getAccessionNumberFrom()}"/>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="90" />
                  </widget>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"collected")' />:
                  </text>
                  <calendar begin="0" end="2" key="{meta:getCollectionDateFrom()}" pattern="{resource:getString($constants,'datePattern')}" width="90" tab = "{meta:getCollectionDateTo()},{meta:getAccessionNumberTo()}"/>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"to")' />:
                  </text>
                  <calendar begin="0" end="2" key="{meta:getCollectionDateTo()}" pattern="{resource:getString($constants,'datePattern')}" width="90" tab = "{meta:getReceivedDateFrom()},{meta:getCollectionDateFrom()}"/>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="90" />
                  </widget>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"received")' />:
                  </text>
                  <calendar begin="0" end="4" key="{meta:getReceivedDateFrom()}" pattern="{resource:getString($constants,'dateTimePattern')}" width="125" tab = "{meta:getReceivedDateTo()},{meta:getCollectionDateTo()}"/>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"to")' />:
                  </text>
                  <calendar begin="0" end="4" key="{meta:getReceivedDateTo()}" pattern="{resource:getString($constants,'dateTimePattern')}" width="125" tab = "{meta:getEnteredDateFrom()},{meta:getReceivedDateFrom()}"/>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="90" />
                  </widget>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"entered")' />:
                  </text>
                  <calendar begin="0" end="4" key="{meta:getEnteredDateFrom()}" pattern="{resource:getString($constants,'dateTimePattern')}" width="125" tab = "{meta:getEnteredDateTo()},{meta:getReceivedDateTo()}"/>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"to")' />:
                  </text>
                  <calendar begin="0" end="4" key="{meta:getEnteredDateTo()}" pattern="{resource:getString($constants,'dateTimePattern')}" width="125" tab = "{meta:getClientReference()},{meta:getEnteredDateFrom()}"/>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="90" />
                  </widget>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"clntRef")' />:
                  </text>
                  <textbox field="String" key="{meta:getClientReference()}" width="85" tab = "{meta:getProjectId()},{meta:getEnteredDateTo()}"/>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'project')" />:
                  </text>
                  <dropdown field="Integer" key="{meta:getProjectId()}" popWidth="auto" width="120" tab = "reportToOrganizationName,{meta:getClientReference()}"/>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="90" />
                  </widget>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"reportTo")' />:
                  </text>
                  <widget colspan="3">
                    <textbox field="String" key="reportToOrganizationName" case = "UPPER" width="225" tab = "excludeResults,{meta:getProjectId()}"/>
                  </widget>
                </row>  
              </TablePanel>
              <TablePanel style="Form">
                <row>
                  <widget colspan="7">
                    <text style="heading">
                      <xsl:value-of select='resource:getString($constants,"resultFieldSearchBy")' />
                    </text>
                  </widget>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="100" />
                  </widget>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'excludeResults')" />:
                  </text>
                  <check key="excludeResults" tab = "excludeAuxData,reportToOrganizationName"/>                                  
                  <widget>
                    <HorizontalPanel width="48" />
                  </widget>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'excludeAuxData')" />:
                  </text>
                  <check key="excludeAuxData" tab = "{meta:getAnalysisTestName()},excludeResults"/>
                </row>
              </TablePanel>
            </VerticalPanel>
          </tab>
          <tab text="{resource:getString($constants,'common')}">
            <VerticalPanel>
              <TablePanel style="Form">
                <row>
                  <widget colspan="5">
                    <text style="heading">
                      <xsl:value-of select='resource:getString($constants,"selectSampleField")' />
                    </text>
                  </widget>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="55" />
                  </widget>
                  <check key="{meta:getAccessionNumber()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'accessionNum')" />
                  </text>
                  <check key="{meta:getRevision()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'revision')" />
                  </text>
                  <check key="{meta:getCollectionDate()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'collected')" />
                  </text>
                  <check key="{meta:getReceivedDate()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'received')" />
                  </text>
                  <check key="{meta:getEnteredDate()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'entered')" />
                  </text>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="55" />
                  </widget>
                  <check key="{meta:getReleasedDate()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'released')" />
                  </text>
                  <check key="{meta:getStatusId()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'status')" />
                  </text>
                  <check key="{meta:getProjectName()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'project')" />
                  </text>
                  <check key="{meta:getClientReferenceHeader()}" />
                  <widget colspan = "3">
                    <text style="LeftAlignPrompt">
                      <xsl:value-of select='resource:getString($constants,"clntRef")' />
                    </text>
                  </widget>
                </row>
                <row>
                  <widget colspan="7">
                    <VerticalPanel height="5" />
                  </widget>
                </row>
                <row>
                  <widget colspan="7">
                    <text style="heading">
                      <xsl:value-of select='resource:getString($constants,"selectOrganizationField")' />
                    </text>
                  </widget>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="50" />
                  </widget>
                  <check key="{meta:getSampleOrgId()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'id')" />
                  </text>
                  <check key="{meta:getSampleOrgOrganizationName()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'name')" />
                  </text>
                  <check key="{meta:getSampleOrgAttention()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'attention')" />
                  </text>
                  <check key="{meta:getAddressMultipleUnit()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'aptSuite')" />
                  </text>
                  <check key="{meta:getAddressStreetAddress()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'address')" />
                  </text>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="50" />
                  </widget>
                  <check key="{meta:getAddressCity()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'city')" />
                  </text>
                  <check key="{meta:getAddressState()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'state')" />
                  </text>
                  <check key="{meta:getAddressZipCode()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'zipcode')" />
                  </text>
                </row>
                <row>
                  <widget colspan="6">
                    <VerticalPanel height="5" />
                  </widget>
                </row>
                <row>
                  <widget colspan="6">
                    <text style="heading">
                      <xsl:value-of select='resource:getString($constants,"selectSampleItemField")' />
                    </text>
                  </widget>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="50" />
                  </widget>
                  <check key="{meta:getItemTypeofSampleId()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'sampleType')" />
                  </text>
                  <check key="{meta:getItemSourceOfSampleId()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'source')" />
                  </text>
                  <check key="{meta:getItemSourceOther()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'sourceOther')" />
                  </text>
                  <check key="{meta:getItemContainerId()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'container')" />
                  </text>
                  <check key="{meta:getItemContainerReference()}" />
                  <widget colspan = "3">
                    <text style="LeftAlignPrompt">
                      <xsl:value-of select="resource:getString($constants,'containerReference')" />
                    </text>
                  </widget>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="50" />
                  </widget>      
                  <check key="{meta:getItemItemSequence()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'sequence')" />
                  </text>
                </row>
                <row>
                  <widget colspan="5">
                    <VerticalPanel height="5" />
                  </widget>
                </row>
                <row>
                  <widget colspan="5">
                    <text style="heading">
                      <xsl:value-of select='resource:getString($constants,"selectAnalysisField")' />
                    </text>
                  </widget>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="55" />
                  </widget>
                  <check key="{meta:getAnalysisId()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'id')" />
                  </text>
                  <check key="{meta:getAnalysisTestNameHeader()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'test')" />
                  </text>
                  <check key="{meta:getAnalysisMethodNameHeader()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'method')" />
                  </text>
                  <check key="{meta:getAnalysisStatusIdHeader()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'status')" />
                  </text>
                  <check key="{meta:getAnalysisRevision()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'revision')" />
                  </text>
                  <check key="{meta:getAnalysisIsReportableHeader()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'testReportable')" />
                  </text>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="55" />
                  </widget>
                  <check key="{meta:getAnalysisUnitOfMeasureId()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'unit')" />
                  </text>
                  <check key="{meta:getAnalysisSubQaName()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'QAEvent')" />
                  </text>
                  <check key="{meta:getAnalysisCompletedDate()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'completed')" />
                  </text>
                  <check key="{meta:getAnalysisCompletedBy()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'by')" />
                  </text>
                  <check key="{meta:getAnalysisReleasedDate()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'released')" />
                  </text>
                  <check key="{meta:getAnalysisReleasedBy()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'by')" />
                  </text>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="55" />
                  </widget>
                  <check key="{meta:getAnalysisStartedDate()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'started')" />
                  </text>
                  <check key="{meta:getAnalysisPrintedDate()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'printed')" />
                  </text>
                </row>
              </TablePanel>
            </VerticalPanel>
          </tab>
          <tab text="{resource:getString($constants,'environmental')}">
            <VerticalPanel>
              <TablePanel style="Form">
                <row>
                  <widget colspan="6">
                    <text style="heading">
                      <xsl:value-of select='resource:getString($constants,"selectEnvironmentalField")' />
                    </text>
                  </widget>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="60" />
                  </widget>
                  <check key="{meta:getEnvIsHazardous()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select='resource:getString($constants,"hazardous")' />
                  </text>
                  <widget>
                    <HorizontalPanel width="52" />
                  </widget>
                  <check key="{meta:getEnvPriority()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select='resource:getString($constants,"priority")' />
                  </text>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="60" />
                  </widget>
                  <check key="{meta:getEnvCollectorHeader()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'collector')" />
                  </text>
                  <widget>
                    <HorizontalPanel width="52" />
                  </widget>
                  <check key="{meta:getEnvCollectorPhone()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'phone')" />
                  </text>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="60" />
                  </widget>
                  <check key="{meta:getEnvLocationHeader()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select='resource:getString($constants,"location")' />
                  </text>
                  <widget>
                    <HorizontalPanel width="52" />
                  </widget>
                  <check key="{meta:getLocationAddrCityHeader()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select='resource:getString($constants,"locationCity")' />
                  </text>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="60" />
                  </widget>
                  <check key="{meta:getEnvDescription()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select='resource:getString($constants,"description")' />
                  </text>
                </row>
              </TablePanel>
            </VerticalPanel>
          </tab>
          <tab text="{resource:getString($constants,'privateWell')}">
            <VerticalPanel>
              <TablePanel style="Form">
                <row>
                  <widget colspan="6">
                    <text style="heading">
                      <xsl:value-of select='resource:getString($constants,"selectPrivateWellField")' />
                    </text>
                  </widget>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="60" />
                  </widget>
                  <check key="{meta:getWellOwner()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select='resource:getString($constants,"owner")' />
                  </text>
                  <widget>
                    <HorizontalPanel width="42" />
                  </widget>
                  <check key="{meta:getWellCollector()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'collector')" />
                  </text>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="60" />
                  </widget>
                  <check key="{meta:getWellWellNumber()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select='resource:getString($constants,"wellNum")' />
                  </text>
                  <widget>
                    <HorizontalPanel width="42" />
                  </widget>
                  <check key="{meta:getWellReportToAddressWorkPhone()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'phone')" />
                  </text>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="60" />
                  </widget>
                  <check key="{meta:getWellReportToAddressFaxPhone()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select='resource:getString($constants,"faxNumber")' />
                  </text>
                  <widget>
                    <HorizontalPanel width="42" />
                  </widget>
                  <check key="{meta:getWellLocation()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select='resource:getString($constants,"location")' />
                  </text>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="60" />
                  </widget>
                  <check key="{meta:getWellLocationAddrCity()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select='resource:getString($constants,"locationCity")' />
                  </text>
                </row>
              </TablePanel>
            </VerticalPanel>
          </tab>
          <tab text="{resource:getString($constants,'sdwis')}">
            <VerticalPanel>
              <TablePanel style="Form">
                <row>
                  <widget colspan="6">
                    <text style="heading">
                      <xsl:value-of select='resource:getString($constants,"selectSdwisField")' />
                    </text>
                  </widget>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="60" />
                  </widget>
                  <check key="{meta:getSDWISPwsId()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select='resource:getString($constants,"pwsId")' />
                  </text>
                  <widget>
                    <HorizontalPanel width="40" />
                  </widget>
                  <check key="{meta:getPwsName()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'pwsName')" />
                  </text>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="60" />
                  </widget>
                  <check key="{meta:getSDWISStateLabId()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select='resource:getString($constants,"stateLabNo")' />
                  </text>
                  <widget>
                    <HorizontalPanel width="40" />
                  </widget>
                  <check key="{meta:getSDWISFacilityId()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'facilityId')" />
                  </text>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="60" />
                  </widget>
                  <check key="{meta:getSDWISSampleTypeId()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select='resource:getString($constants,"sampleType")' />
                  </text>
                  <widget>
                    <HorizontalPanel width="40" />
                  </widget>
                  <check key="{meta:getSDWISSampleCategoryId()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select='resource:getString($constants,"sampleCat")' />
                  </text>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="60" />
                  </widget>
                  <check key="{meta:getSDWISSamplePointId()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select='resource:getString($constants,"samplePtId")' />
                  </text>
                  <widget>
                    <HorizontalPanel width="40" />
                  </widget>
                  <check key="{meta:getSDWISLocation()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select='resource:getString($constants,"location")' />
                  </text>
                </row>
                <row>
                  <widget>
                    <HorizontalPanel width="60" />
                  </widget>
                  <check key="{meta:getSDWISCollector()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select='resource:getString($constants,"collector")' />
                  </text>
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