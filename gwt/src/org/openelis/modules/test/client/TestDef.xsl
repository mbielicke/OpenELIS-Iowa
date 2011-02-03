
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
  version="1.0"
  extension-element-prefixes="resource"
  xmlns:locale="xalan://java.util.Locale"
  xmlns:resource="xalan://org.openelis.util.UTFResource"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:noNamespaceSchemaLocation="http://openelis.uhl.uiowa.edu/schema/ScreenSchema.xsd"
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd"
  xmlns:meta="xalan://org.openelis.meta.TestMeta">

  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" id="Test" name="{resource:getString($constants,'test')}">
      <HorizontalPanel padding="0" spacing="0">
        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="225" height="100%">
            <buttonGroup key="atozButtons">
              <xsl:call-template name="aToZLeftPanelButtons" />
            </buttonGroup>
            <VerticalPanel>
              <table key="atozTable" width="auto" maxRows="26" style="AtoZtable">
                <col width="175" header="{resource:getString($constants,'nameMethod')}">
                  <label field="String" />
                </col>
              </table>
              <widget halign="center">
                <HorizontalPanel>
                  <appButton key="atozPrev" style="Button">
                    <AbsolutePanel style="prevNavIndex" />
                  </appButton>
                  <appButton key="atozNext" style="Button">
                    <AbsolutePanel style="nextNavIndex" />
                  </appButton>
                </HorizontalPanel>
              </widget>
            </VerticalPanel>
          </HorizontalPanel>
        </CollapsePanel>
        <VerticalPanel padding="0" spacing="0">
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
              <xsl:call-template name="buttonPanelDivider" />
              <menuPanel key="optionsMenu" layout="vertical" style="topBarItemHolder">
                <menuItem>
                  <menuDisplay>
                    <appButton style="ButtonPanelButton" action="option">
                      <HorizontalPanel>
                        <text>
                          <xsl:value-of select='resource:getString($constants,"options")' />
                        </text>
                        <AbsolutePanel width="20" height="20" style="OptionsButtonImage" />
                      </HorizontalPanel>
                    </appButton>
                  </menuDisplay>
                  <menuPanel width="200" layout="vertical" position="below" style="buttonMenuContainer">
                    <xsl:call-template name="duplicateRecordMenuItem" />
                    <menuItem key="testHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'testHistory')}" />
                    <menuItem key="testSectionHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'testSectionHistory')}" />
                    <menuItem key="testSampleTypeHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'testSampleTypeHistory')}" />
                    <menuItem key="testAnalyteHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'testAnalyteHistory')}" />
                    <menuItem key="testResultHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'testResultHistory')}" />
                    <menuItem key="testPrepHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'testPrepHistory')}" />
                    <menuItem key="testReflexHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'testReflexHistory')}" />
                    <menuItem key="testWorksheetHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'testWorksheetHistory')}" />
                    <menuItem key="testWorksheetItemHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'testWorksheetItemHistory')}" />
                    <menuItem key="testWorksheetAnalyteHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'testWorksheetAnalyteHistory')}" />
                  </menuPanel>
                </menuItem>
              </menuPanel>
            </HorizontalPanel>
          </AbsolutePanel>
          <HorizontalPanel padding="0" spacing="0" style="WhiteContentPanel">
            <VerticalPanel>
              <TablePanel style="Form">
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'id')" />:
                  </text>
                  <textbox key="{meta:getId()}" width="50" tab="{meta:getName()},{meta:getMethodName()}" field="Integer" />
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'name')" />:
                  </text>
                  <textbox key="{meta:getName()}" width="145" case="LOWER" max="20" tab="{meta:getMethodName()},{meta:getId()}" field="String" required="true" />
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'method')" />:
                  </text>
                  <widget>
                    <autoComplete key="{meta:getMethodName()}" width="145" case="LOWER" tab="testTabPanel, {meta:getName()}" field="Integer" required="true">
                      <col width="145" />
                    </autoComplete>
                  </widget>
                </row>
              </TablePanel>
              <TabPanel key="testTabPanel" width="625" height="515">
                <tab key="detailsTab" tab="{meta:getDescription()},{meta:getDescription()}" text="{resource:getString($constants,'testDetails')}">
                  <VerticalPanel padding="0" spacing="0">
                    <TablePanel style="Form">
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'description')" />:
                        </text>
                        <widget colspan="2">
                          <textbox key="{meta:getDescription()}" width="425" max="60" tab="{meta:getReportingDescription()},{meta:getScriptletName()}" field="String" required="true" />
                        </widget>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'reportDescription')" />:
                        </text>
                        <widget colspan="2">
                          <textbox key="{meta:getReportingDescription()}" width="425" max="60" tab="{meta:getTimeTaMax()},{meta:getDescription()}" field="String" required="true" />
                        </widget>
                      </row>
                    </TablePanel>
                    <HorizontalPanel>
                      <VerticalPanel style="subform">
                        <text style="FormTitle">
                          <xsl:value-of select='resource:getString($constants,"turnAround")' />
                        </text>
                        <TablePanel style="Form">
                          <row>
                            <text style="Prompt">
                              <xsl:value-of select='resource:getString($constants,"turnAroundMax")' />:
                            </text>
                            <textbox key="{meta:getTimeTaMax()}" width="50" tab="{meta:getTimeTaAverage()},{meta:getIsReportable()}" field="Integer" required="true" />
                            <text style="Prompt">
                              <xsl:value-of select="resource:getString($constants,'timeTransit')" />:
                            </text>
                            <textbox key="{meta:getTimeTransit()}" width="50" tab="{meta:getTimeHolding()},{meta:getTimeTaWarning()}" field="Integer" required="true" />
                          </row>
                          <row>
                            <text style="Prompt">
                              <xsl:value-of select='resource:getString($constants,"turnAroundAverage")' />:
                            </text>
                            <textbox key="{meta:getTimeTaAverage()}" width="50" tab="{meta:getTimeTaWarning()},{meta:getTimeTaMax()}" field="Integer" required="true" />
                            <text style="Prompt">
                              <xsl:value-of select="resource:getString($constants,'timeHolding')" />:
                            </text>
                            <textbox key="{meta:getTimeHolding()}" width="50" tab="{meta:getIsActive()},{meta:getTimeTransit()}" field="Integer" required="true" />
                          </row>
                          <row>
                            <text style="Prompt">
                              <xsl:value-of select='resource:getString($constants,"turnAroundWarn")' />:
                            </text>
                            <textbox key="{meta:getTimeTaWarning()}" width="50" tab="{meta:getTimeTransit()},{meta:getTimeTaAverage()}" field="Integer" required="true" />
                          </row>
                        </TablePanel>
                      </VerticalPanel>
                      <VerticalPanel style="subform">
                        <text style="FormTitle">
                          <xsl:value-of select='resource:getString($constants,"activity")' />
                        </text>
                        <TablePanel style="Form">
                          <row>
                            <text style="Prompt">
                              <xsl:value-of select='resource:getString($constants,"active")' />:
                            </text>
                            <check key="{meta:getIsActive()}" tab="{meta:getActiveBegin()},{meta:getTimeHolding()}" required="true" />
                          </row>
                          <row>
                            <text style="Prompt">
                              <xsl:value-of select='resource:getString($constants,"beginDate")' />:
                            </text>
                            <calendar key="{meta:getActiveBegin()}" begin="0" end="2" width="90" pattern="{resource:getString($constants,'datePattern')}" tab="{meta:getActiveEnd()},{meta:getIsActive()}" required="true" />
                          </row>
                          <row>
                            <text style="Prompt">
                              <xsl:value-of select='resource:getString($constants,"endDate")' />:
                            </text>
                            <calendar key="{meta:getActiveEnd()}" begin="0" end="2" width="90" pattern="{resource:getString($constants,'datePattern')}" tab="{meta:getLabelName()},{meta:getActiveBegin()}" required="true" />
                          </row>
                        </TablePanel>
                      </VerticalPanel>
                    </HorizontalPanel>
                    <HorizontalPanel>
                      <VerticalPanel style="subform">
                        <text style="FormTitle">
                          <xsl:value-of select='resource:getString($constants,"additionalLabel")' />
                        </text>
                        <TablePanel style="Form">
                          <row>
                            <text style="Prompt">
                              <xsl:value-of select='resource:getString($constants,"type")' />:
                            </text>
                            <widget>
                              <autoComplete key="{meta:getLabelName()}" width="224" tab="{meta:getLabelQty()},{meta:getActiveEnd()}" field="Integer">
                                <col width="224" />
                              </autoComplete>
                            </widget>
                          </row>
                          <row>
                            <text style="Prompt">
                              <xsl:value-of select='resource:getString($constants,"quantity")' />:
                            </text>
                            <textbox key="{meta:getLabelQty()}" width="50" tab="{meta:getIsReportable()},{meta:getLabelName()}" field="Integer" />
                          </row>
                        </TablePanel>
                      </VerticalPanel>
                      <HorizontalPanel width="1" />
                      <VerticalPanel style="subform">
                        <text style="FormTitle">
                          <xsl:value-of select='resource:getString($constants,"sections")' />
                        </text>
                        <widget valign="bottom">
                          <table key="sectionTable" width="auto" maxRows="2" showScroll="ALWAYS" tab="removeTestSectionButton,{meta:getIsReportable()}" title="">
                            <col key="{meta:getSectionSectionId()}" width="119" header="{resource:getString($constants,'name')}">
                              <dropdown width="85" case="MIXED" field="Integer" required="true" />
                            </col>
                            <col key="{meta:getSectionFlagId()}" width="119" header="{resource:getString($constants,'options')}">
                              <dropdown width="110" case="MIXED" field="Integer" />
                            </col>
                          </table>
                        </widget>
                        <HorizontalPanel style="TableFooterPanel">
                          <widget halign="center">
                            <appButton key="addSectionButton" style="Button">
                              <HorizontalPanel>
                                <AbsolutePanel style="AddRowButtonImage" />
                                <text>
                                  <xsl:value-of select="resource:getString($constants,'addRow')" />
                                </text>
                              </HorizontalPanel>
                            </appButton>
                          </widget>
                          <widget halign="center">
                            <appButton key="removeSectionButton" style="Button">
                              <HorizontalPanel>
                                <AbsolutePanel style="RemoveRowButtonImage" />
                                <text>
                                  <xsl:value-of select="resource:getString($constants,'removeRow')" />
                                </text>
                              </HorizontalPanel>
                            </appButton>
                          </widget>
                        </HorizontalPanel>
                      </VerticalPanel>
                    </HorizontalPanel>
                    <TablePanel style="Form">
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"testReportable")' />:
                        </text>
                        <check key="{meta:getIsReportable()}" tab="sectionTable,{meta:getLabelQty()}" required="true" />
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'revisionMethod')" />:
                        </text>
                        <dropdown key="{meta:getRevisionMethodId()}" width="190" tab="{meta:getSortingMethodId()},removeTestSectionButton" field="Integer" />
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'testTrailer')" />:
                        </text>
                        <autoComplete key="{meta:getTestTrailerName()}" width="180" case="LOWER" tab="{meta:getTestFormatId()},{meta:getReportingSequence()}" field="Integer" >
                          <col width="180" />
                        </autoComplete>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'sortingMethod')" />:
                        </text>
                        <dropdown key="{meta:getSortingMethodId()}" width="190" tab="{meta:getReportingMethodId()},{meta:getRevisionMethodId()}" field="Integer" required="true"/>
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'testFormat')" />:
                        </text>
                        <dropdown key="{meta:getTestFormatId()}" width="180" tab="{meta:getScriptletName()},{meta:getTestTrailerName()}" field="Integer" required="true"/>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'reportingMethod')" />:
                        </text>
                        <dropdown key="{meta:getReportingMethodId()}" width="190" tab="{meta:getReportingSequence()},{meta:getSortingMethodId()}" field="Integer" required="true"/>
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'scriptlet')" />:
                        </text>
                        <autoComplete key="{meta:getScriptletName()}" width="180" case="LOWER" tab="{meta:getDescription()},{meta:getTestFormatId()}" field="Integer">
                          <col width="180" />
                        </autoComplete>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'reportingSequence')" />:
                        </text>
                        <textbox key="{meta:getReportingSequence()}" width="80" tab="{meta:getTestTrailerName()},{meta:getReportingMethodId()}" field="Integer" />
                      </row>
                    </TablePanel>
                    <VerticalPanel height="71" />
                  </VerticalPanel>
                </tab>
                <tab key="sampleTypeTab" tab="sampleTypeTable,sampleTypeTable" text="{resource:getString($constants,'sampleType')}">
                  <VerticalPanel>
                    <HorizontalPanel>
                      <widget valign="top">
                        <table key="sampleTypeTable" width="auto" maxRows="21" showScroll="ALWAYS" title="">
                          <col key="{meta:getTypeOfSampleTypeOfSampleId()}" width="301" header="{resource:getString($constants,'sampleType')}">
                            <dropdown width="301" case="MIXED" popWidth="301" field="Integer" required="true" />
                          </col>
                          <col key="{meta:getTypeOfSampleUnitOfMeasureId()}" width="300" header="{resource:getString($constants,'unitOfMeasure')}">
                            <dropdown width="300" case="MIXED" popWidth="300" field="Integer" />
                          </col>
                        </table>
                      </widget>
                    </HorizontalPanel>
                    <HorizontalPanel style="TableFooterPanel">
                      <widget halign="center">
                        <appButton key="addSampleTypeButton" style="Button">
                          <HorizontalPanel>
                            <AbsolutePanel style="AddRowButtonImage" />
                            <text>
                              <xsl:value-of select="resource:getString($constants,'addRow')" />
                            </text>
                          </HorizontalPanel>
                        </appButton>
                      </widget>
                      <widget halign="center">
                        <appButton key="removeSampleTypeButton" style="Button">
                          <HorizontalPanel>
                            <AbsolutePanel style="RemoveRowButtonImage" />
                            <text>
                              <xsl:value-of select="resource:getString($constants,'removeRow')" />
                            </text>
                          </HorizontalPanel>
                        </appButton>
                      </widget>
                    </HorizontalPanel>
                    <VerticalPanel height="10" />
                  </VerticalPanel>
                </tab>
                <tab key="analyteTab" tab="analyteTable,analyteTable" text="{resource:getString($constants,'analytesResults')}">
                  <VerticalPanel padding="0" spacing="0">
                    <table key="analyteTable" width="607" maxRows="8" multiSelect="true" showScroll="ALWAYS">
                      <col key="analyteLookup" width="152" header="1">
                        <autoComplete width="125" case="MIXED" popWidth="auto" field="Integer">
                          <col width="300" />
                        </autoComplete>
                      </col>
                      <col key="analyteLookup2" width="150" header="2">
                        <autoComplete key="analyteLookup" width="125" case="MIXED" popWidth="auto" field="Integer">
                          <col width="150" />
                        </autoComplete>
                      </col>
                      <col key="analyteLookup3" width="150" header="3">
                        <autoComplete key="analyteLookup" width="125" case="MIXED" popWidth="auto" field="Integer">
                          <col width="300" />
                        </autoComplete>
                      </col>
                      <col key="analyteLookup4" width="150" header="4">
                        <autoComplete key="analyteLookup" width="125" case="MIXED" popWidth="auto" field="Integer">
                          <col width="150" />
                        </autoComplete>
                      </col>
                      <col key="analyteLookup5" width="150" header="5">
                        <autoComplete key="analyteLookup" width="125" case="MIXED" popWidth="auto" field="Integer">
                          <col width="150" />
                        </autoComplete>
                      </col>
                      <col key="analyteLookup6" width="150" header="6">
                        <autoComplete key="analyteLookup" width="125" case="MIXED" popWidth="auto" field="Integer">
                          <col width="150" />
                        </autoComplete>
                      </col>
                      <col key="analyteLookup7" width="150" header="7">
                        <autoComplete key="analyteLookup" width="125" case="MIXED" popWidth="auto" field="Integer">
                          <col width="150" />
                        </autoComplete>
                      </col>
                      <col key="analyteLookup8" width="150" header="8">
                        <autoComplete key="analyteLookup" width="125" case="MIXED" popWidth="auto" field="Integer">
                          <col width="150" />
                        </autoComplete>
                      </col>
                      <col key="analyteLookup9" width="150" header="9">
                        <autoComplete key="analyteLookup" width="125" case="MIXED" popWidth="auto" field="Integer">
                          <col width="150" />
                        </autoComplete>
                      </col>
                      <col key="analyteLookup10" width="150" header="10">
                        <autoComplete key="analyteLookup" width="125" case="MIXED" popWidth="auto" field="Integer">
                          <col width="150" />
                        </autoComplete>
                      </col>
                    </table>
                    <HorizontalPanel style="TableButtonFooter">
                      <TablePanel padding="0" spacing="0">
                        <row>
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'tableOptions')" />:
                          </text>
                          <dropdown key="tableActions" width="75" field="String" />
                          <appButton key="addButton" style="Button">
                            <AbsolutePanel style="AddButtonIcon" />
                          </appButton>
                          <appButton key="removeButton" style="Button">
                            <AbsolutePanel style="DeleteButtonIcon" />
                          </appButton>
                        </row>
                      </TablePanel>
                      <HorizontalPanel width="15" />
                      <TablePanel>
                        <row>
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'type')" />:
                          </text>
                          <dropdown key="{meta:getAnalyteTypeId()}" width="80" popWidth="80" field="Integer" />
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'reportable')" />:
                          </text>
                          <check key="{meta:getAnalyteIsReportable()}" />
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'scriptlet')" />:
                          </text>
                          <autoComplete key="{meta:getAnalyteScriptletId()}" width="100" case="LOWER" field="Integer">
                            <col width="165" />
                          </autoComplete>
                        </row>
                      </TablePanel>
                    </HorizontalPanel>
                    <VerticalPanel>
                      <ScrollTabBar key="resultTabPanel" width="583" />
                      <widget valign="top">
                        <table key="resultTable" width="auto" maxRows="9" showScroll="ALWAYS" title="">
                          <col key="{meta:getResultUnitOfMeasureId()}" width="70" header="{resource:getString($constants,'unit')}">
                            <dropdown width="70" case="MIXED" field="Integer" />
                          </col>
                          <col key="{meta:getResultTypeId()}" width="85" header="{resource:getString($constants,'type')}">
                            <dropdown width = "105" field="Integer" required="true" />
                          </col>
                          <col key="{meta:getResultValue()}" width="190" header="{resource:getString($constants,'value')}">
                            <textbox case="MIXED" field="String" />
                          </col>
                          <col key="{meta:getResultFlagsId()}" width="95" header="{resource:getString($constants,'flags')}">
                            <dropdown width="115" case="MIXED" field="Integer" />
                          </col>
                          <col key="{meta:getResultSignificantDigits()}" width="45" header="{resource:getString($constants,'significantDigits')}">
                            <textbox case="MIXED" field="Integer" />
                          </col>
                          <col key="{meta:getResultRoundingMethodId()}" width="104" header="{resource:getString($constants,'roundingMethod')}">
                            <dropdown width="100" case="MIXED" field="Integer" />
                          </col>
                        </table>
                      </widget>
                    </VerticalPanel>
                    <TablePanel>
                      <row>
                        <widget style="TableButtonFooter">
                          <HorizontalPanel>
                            <appButton key="addResultTabButton" style="Button">
                              <HorizontalPanel>
                                <AbsolutePanel style="AddTabButtonImage" />
                                <text>
                                  <xsl:value-of select="resource:getString($constants,'addGroup')" />
                                </text>
                              </HorizontalPanel>
                            </appButton>
                            <appButton key="addTestResultButton" style="Button">
                              <HorizontalPanel>
                                <AbsolutePanel style="AddRowButtonImage" />
                                <text>
                                  <xsl:value-of select="resource:getString($constants,'addRow')" />
                                </text>
                              </HorizontalPanel>
                            </appButton>
                            <appButton key="removeTestResultButton" style="Button">
                              <HorizontalPanel>
                                <AbsolutePanel style="RemoveRowButtonImage" />
                                <text>
                                  <xsl:value-of select="resource:getString($constants,'removeRow')" />
                                </text>
                              </HorizontalPanel>
                            </appButton>
                            <appButton key="dictionaryLookUpButton" style="Button">
                              <HorizontalPanel>
                                <AbsolutePanel style="DictionaryButtonImage" />
                                <text>
                                  <xsl:value-of select='resource:getString($constants,"dictionary")' />
                                </text>
                              </HorizontalPanel>
                            </appButton>
                          </HorizontalPanel>
                        </widget>
                      </row>
                    </TablePanel>
                  </VerticalPanel>
                </tab>
                <tab key="prepAndReflexTab" tab="testPrepTable,testPrepTable" text="{resource:getString($constants,'prepAndReflex')}">
                  <VerticalPanel>
                    <VerticalPanel padding="0" spacing="0">
                      <VerticalPanel padding="0" spacing="0">
                        <HorizontalPanel>
                          <widget valign="top">
                            <table key="testPrepTable" width="auto" maxRows="9" showScroll="ALWAYS" title="">
                              <col key="{meta:getPrepPrepTestName()}" width="312" header="{resource:getString($constants,'prepTest')}">
                                <autoComplete width="312" field="Integer" required="true">
                                  <col width="140" header="{resource:getString($constants,'test')}" />
                                  <col width="135" header="{resource:getString($constants,'method')}" />
                                  <col width="250" header="{resource:getString($constants,'description')}" />
                                </autoComplete>
                              </col>
                              <col key="method" width="212" header="{resource:getString($constants,'method')}">
                                <label field="String" />
                              </col>
                              <col key="{meta:getPrepIsOptional()}" width="74" header="{resource:getString($constants,'optional')}">
                                <check />
                              </col>
                            </table>
                          </widget>
                        </HorizontalPanel>
                        <HorizontalPanel style="TableFooterPanel">
                          <widget halign="center">
                            <appButton key="addPrepTestButton" style="Button">
                              <HorizontalPanel>
                                <AbsolutePanel style="AddRowButtonImage" />
                                <widget>
                                  <text>
                                    <xsl:value-of select='resource:getString($constants,"addRow")' />
                                  </text>
                                </widget>
                              </HorizontalPanel>
                            </appButton>
                          </widget>
                          <widget halign="center">
                            <appButton key="removePrepTestButton" style="Button">
                              <HorizontalPanel>
                                <AbsolutePanel style="RemoveRowButtonImage" />
                                <widget>
                                  <text>
                                    <xsl:value-of select='resource:getString($constants,"removeRow")' />
                                  </text>
                                </widget>
                              </HorizontalPanel>
                            </appButton>
                          </widget>
                        </HorizontalPanel>
                        <VerticalPanel height="5" />
                        <HorizontalPanel>
                          <widget valign="top">
                            <table key="testReflexTable" width="auto" maxRows="9" showScroll="ALWAYS" title="">
                              <col key="{meta:getReflexAddTestName()}" width="90" header="{resource:getString($constants,'reflexiveTest')}">
                                <autoComplete width="80" field="Integer" required="true">
                                  <col width="140" header="{resource:getString($constants,'test')}" />
                                  <col width="135" header="{resource:getString($constants,'method')}" />
                                  <col width="250" header="{resource:getString($constants,'description')}" />
                                </autoComplete>
                              </col>
                              <col key="method" width="65" header="{resource:getString($constants,'method')}">
                                <label field="String" />
                              </col>
                              <col key="{meta:getReflexTestAnalyteName()}" width="194" header="{resource:getString($constants,'testAnalyte')}">
                                <autoComplete width="194" case="MIXED" popWidth="auto" field="Integer" required="true">
                                  <col width="194" />
                                </autoComplete>
                              </col>
                              <col key="{meta:getReflexTestResultValue()}" width="140" header="{resource:getString($constants,'result')}">
                                <autoComplete width="140" case="MIXED" popWidth="auto" field="Integer" required="true">
                                  <col width="140" />
                                </autoComplete>
                              </col>
                              <col key="{meta:getReflexFlagsId()}" width="103" header="{resource:getString($constants,'flags')}">
                                <dropdown width="200" case="MIXED" popWidth="300" field="Integer" required="true" />
                              </col>
                            </table>
                          </widget>
                        </HorizontalPanel>
                        <HorizontalPanel style="TableFooterPanel">
                          <widget halign="center">
                            <appButton key="addReflexTestButton" style="Button">
                              <HorizontalPanel>
                                <AbsolutePanel style="AddRowButtonImage" />
                                <widget>
                                  <text>
                                    <xsl:value-of select='resource:getString($constants,"addRow")' />
                                  </text>
                                </widget>
                              </HorizontalPanel>
                            </appButton>
                          </widget>
                          <widget halign="center">
                            <appButton key="removeReflexTestButton" style="Button">
                              <HorizontalPanel>
                                <AbsolutePanel style="RemoveRowButtonImage" />
                                <widget>
                                  <text>
                                    <xsl:value-of select='resource:getString($constants,"removeRow")' />
                                  </text>
                                </widget>
                              </HorizontalPanel>
                            </appButton>
                          </widget>
                        </HorizontalPanel>
                      </VerticalPanel>
                    </VerticalPanel>
                  </VerticalPanel>
                </tab>
                <tab key="worksheetTab" tab="{meta:getWorksheetFormatId()},{meta:getWorksheetFormatId()}" text="{resource:getString($constants,'worksheetLayout')}">
                  <VerticalPanel>
                    <VerticalPanel>
                      <TablePanel style="Form">
                        <row>
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'numberFormat')" />:
                          </text>
                          <widget colspan="4">
                            <dropdown key="{meta:getWorksheetFormatId()}" width="145" case="MIXED" tab="{meta:getWorksheetBatchCapacity()}, {meta:getWorksheetScriptletName()}" field="Integer" />
                          </widget>
                        </row>
                        <row>
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'batchCapacity')" />:
                          </text>
                          <widget>
                            <textbox key="{meta:getWorksheetBatchCapacity()}" width="40" tab="{meta:getWorksheetTotalCapacity()},{meta:getWorksheetFormatId()}" field="Integer" />
                          </widget>
                          <widget valign="middle">
                            <text style="Prompt">
                              <xsl:value-of select="resource:getString($constants,'totalCapacity')" />:
                            </text>
                          </widget>
                          <widget>
                            <textbox key="{meta:getWorksheetTotalCapacity()}" width="40" tab="{meta:getWorksheetScriptletName()},{meta:getWorksheetBatchCapacity()}" field="Integer" />
                          </widget>
                        </row>
                        <row>
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'scriptlet')" />:
                          </text>
                          <widget colspan="3">
                            <autoComplete key="{meta:getWorksheetScriptletName()}" width="235" field="Integer">
                              <col width="235" />
                            </autoComplete>
                          </widget>
                        </row>
                      </TablePanel>
                    </VerticalPanel>
                    <VerticalPanel style="subform">
                      <text style="FormTitle">
                        <xsl:value-of select='resource:getString($constants,"qcItems")' />
                      </text>
                      <HorizontalPanel>
                        <widget valign="top">
                          <table key="worksheetTable" width="auto" maxRows="6" showScroll="ALWAYS" tab="worksheetAnalyteTable, {meta:getWorksheetScriptletName()}" title="">
                            <col key="{meta:getWorksheetItemPosition()}" width="70" header="{resource:getString($constants,'position')}">
                              <textbox field="Integer" />
                            </col>
                            <col key="{meta:getWorksheetItemTypeId()}" width="150" header="{resource:getString($constants,'type')}">
                              <dropdown width="140" case="MIXED" field="Integer" required="true" />
                            </col>
                            <col key="{meta:getWorksheetItemQcName()}" width="368" header="{resource:getString($constants,'qcName')}">
                              <autoComplete width="368" case="MIXED" field="String">
                                <col width="368" />
                              </autoComplete>
                            </col>
                          </table>
                        </widget>
                      </HorizontalPanel>
                      <HorizontalPanel style="TableFooterPanel">
                        <widget halign="center">
                          <appButton key="addWSItemButton" style="Button" action="action">
                            <HorizontalPanel>
                              <AbsolutePanel style="AddRowButtonImage" />
                              <widget>
                                <text>
                                  <xsl:value-of select='resource:getString($constants,"addRow")' />
                                </text>
                              </widget>
                            </HorizontalPanel>
                          </appButton>
                        </widget>
                        <widget halign="center">
                          <appButton key="removeWSItemButton" style="Button" action="removeRow">
                            <HorizontalPanel>
                              <AbsolutePanel style="RemoveRowButtonImage" />
                              <widget>
                                <text>
                                  <xsl:value-of select='resource:getString($constants,"removeRow")' />
                                </text>
                              </widget>
                            </HorizontalPanel>
                          </appButton>
                        </widget>
                      </HorizontalPanel>
                    </VerticalPanel>
                    <VerticalPanel style="subform">
                      <text style="FormTitle">
                        <xsl:value-of select='resource:getString($constants,"analytesWS")' />
                      </text>
                      <HorizontalPanel>
                        <widget valign="top">
                          <table key="worksheetAnalyteTable" width="auto" maxRows="5" showScroll="ALWAYS" tab="{meta:getWorksheetFormatId()}, worksheetTable" title="">
                            <col key="{meta:getWorksheetAnalyteAnalyteId()}" width="400" header="{resource:getString($constants,'analyte')}">
                              <label field="String" />
                            </col>
                            <col key="{meta:getWorksheetAnalyteRepeat()}" width="88" header="{resource:getString($constants,'repeat')}">
                              <textbox field="Integer" required="true" />
                            </col>
                            <col key="{meta:getWorksheetAnalyteFlagId()}" width="100" header="{resource:getString($constants,'flag')}">
                              <dropdown width="140" field="Integer" />
                            </col>
                          </table>
                        </widget>
                      </HorizontalPanel>
                      <HorizontalPanel style="TableFooterPanel">
                        <widget halign="left">
                          <appButton key="addWSAnalyteButton" style="Button">
                            <HorizontalPanel>
                              <AbsolutePanel style="AddRowButtonImage" />
                              <text>
                                <xsl:value-of select='resource:getString($constants,"addAnalyte")' />
                              </text>
                            </HorizontalPanel>
                          </appButton>
                        </widget>
                        <widget halign="left">
                          <appButton key="removeWSAnalyteButton" style="Button">
                            <HorizontalPanel>
                              <AbsolutePanel style="RemoveRowButtonImage" />
                              <text>
                                <xsl:value-of select='resource:getString($constants,"removeAnalyte")' />
                              </text>
                            </HorizontalPanel>
                          </appButton>
                        </widget>
                      </HorizontalPanel>
                    </VerticalPanel>
                    <VerticalPanel height="15" />
                  </VerticalPanel>
                </tab>
              </TabPanel>
            </VerticalPanel>
          </HorizontalPanel>
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
