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
<xsl:stylesheet extension-element-prefixes="resource" version="1.0" xmlns:addTestMeta="xalan://org.openelis.metamap.TestMeta" xmlns:label="xalan://org.openelis.meta.LabelMeta" xmlns:locale="xalan://java.util.Locale" xmlns:meta="xalan://org.openelis.metamap.TestMetaMap" xmlns:method="xalan://org.openelis.meta.MethodMeta" xmlns:prepTestMeta="xalan://org.openelis.metamap.TestMeta" xmlns:resource="xalan://org.openelis.util.UTFResource" xmlns:script="xalan://org.openelis.meta.ScriptletMeta" xmlns:testAnalyte="xalan://org.openelis.metamap.TestAnalyteMetaMap" xmlns:testPrep="xalan://org.openelis.metamap.TestPrepMetaMap" xmlns:testRef="xalan://org.openelis.metamap.TestReflexMetaMap" xmlns:testResult="xalan://org.openelis.metamap.TestResultMetaMap" xmlns:testSection="xalan://org.openelis.metamap.TestSectionMetaMap" xmlns:testTOS="xalan://org.openelis.metamap.TestTypeOfSampleMetaMap" xmlns:testTrailer="xalan://org.openelis.meta.TestTrailerMeta" xmlns:testWrksht="xalan://org.openelis.metamap.TestWorksheetMetaMap" xmlns:testWrkshtAna="xalan://org.openelis.metamap.TestWorksheetAnalyteMetaMap" xmlns:testWrkshtItm="xalan://org.openelis.metamap.TestWorksheetItemMetaMap" xmlns:wsscript="xalan://org.openelis.meta.ScriptletMeta" xmlns:xalan="http://xml.apache.org/xalan" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:import href="aToZOneColumn.xsl" />
  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource" />
  </xalan:component>
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale" />
  </xalan:component>
  <xalan:component prefix="meta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TestMetaMap" />
  </xalan:component>
  <xalan:component prefix="method">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.MethodMeta" />
  </xalan:component>
  <xalan:component prefix="script">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.ScriptletMeta" />
  </xalan:component>
  <xalan:component prefix="label">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.LabelMeta" />
  </xalan:component>
  <xalan:component prefix="testTrailer">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.TestTrailerMeta" />
  </xalan:component>
  <xalan:component prefix="testPrep">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TestPrepMetaMap" />
  </xalan:component>
  <xalan:component prefix="prepTestMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.TestMeta" />
  </xalan:component>
  <xalan:component prefix="testTOS">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TestTypeOfSampleMetaMap" />
  </xalan:component>
  <xalan:component prefix="testRef">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TestReflexMetaMap" />
  </xalan:component>
  <xalan:component prefix="testWrksht">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TestWorksheetMetaMap" />
  </xalan:component>
  <xalan:component prefix="testWrkshtItm">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TestWorksheetItemMetaMap" />
  </xalan:component>
  <xalan:component prefix="wsscript">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.ScriptletMeta" />
  </xalan:component>
  <xalan:component prefix="testWrkshtAna">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TestWorksheetAnalyteMetaMap" />
  </xalan:component>
  <xalan:component prefix="testAnalyte">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TestAnalyteMetaMap" />
  </xalan:component>
  <xalan:component prefix="testSection">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TestSectionMetaMap" />
  </xalan:component>
  <xalan:component prefix="testResult">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TestResultMetaMap" />
  </xalan:component>
  <xalan:component prefix="addTestMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.TestMeta" />
  </xalan:component>
  <xsl:template match="doc">
    <xsl:variable name="test" select="meta:new()" />
    <xsl:variable name="mt" select="meta:getMethod($test)" />
    <xsl:variable name="scpt" select="meta:getScriptlet($test)" />
    <xsl:variable name="lbl" select="meta:getLabel($test)" />
    <xsl:variable name="tt" select="meta:getTestTrailer($test)" />
    <xsl:variable name="tp" select="meta:getTestPrep($test)" />
    <xsl:variable name="pt" select="testPrep:getPrepTest($tp)" />
    <xsl:variable name="tos" select="meta:getTestTypeOfSample($test)" />
    <xsl:variable name="tref" select="meta:getTestReflex($test)" />
    <xsl:variable name="at" select="testRef:getAddTest($tref)" />
    <xsl:variable name="tws" select="meta:getTestWorksheet($test)" />
    <xsl:variable name="wss" select="testWrksht:getScriptlet($tws)" />
    <xsl:variable name="twsi" select="meta:getTestWorksheetItem($test)" />
    <xsl:variable name="twsa" select="meta:getTestWorksheetAnalyte($test)" />
    <xsl:variable name="tana" select="meta:getTestAnalyte($test)" />
    <xsl:variable name="ts" select="meta:getTestSection($test)" />
    <xsl:variable name="tr" select="meta:getTestResult($test)" />
    <xsl:variable name="language">
      <xsl:value-of select="locale" />
    </xsl:variable>
    <xsl:variable name="props">
      <xsl:value-of select="props" />
    </xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="Test" name="{resource:getString($constants,'test')}" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">      
        <HorizontalPanel padding="0" spacing="0">
<!--left table goes here -->
          <CollapsePanel height='530px' key='collapsePanel' style='LeftSidePanel'>
            <resultsTable height='425px' key='azTable' showError='false' width='100%'>
              <buttonGroup key='atozButtons'>
                <xsl:call-template name='aToZLeftPanelButtons' />
              </buttonGroup>
              <table maxRows='27' width='auto'>
                <col header="{resource:getString($constants,'nameMethod')}" width='175'>
                  <label />
                </col>
              </table>
            </resultsTable>
          </CollapsePanel>
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
                <xsl:call-template name="buttonPanelDivider" />
                <xsl:call-template name="optionsButton">
                  <xsl:with-param name="language">
                    <xsl:value-of select="language" />
                  </xsl:with-param>
                </xsl:call-template>
                </HorizontalPanel>
            </AbsolutePanel>
<!--end button panel-->
            <HorizontalPanel padding="0" spacing="0" style="WhiteContentPanel">
              <VerticalPanel>
                <TablePanel style="Form">
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'id')" />:
                    </text>
                    <textbox key="{meta:getId($test)}" width="50px" tab="{meta:getName($test)},{method:getName($mt)}" field="Integer" />
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'name')" />:
                    </text>
                    <textbox key="{meta:getName($test)}" width="145px" case="lower" max="20" tab="{method:getName($mt)},{meta:getId($test)}" field="String" required="true" />
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'method')" />:
                    </text>
                    <widget>
                      <autoComplete key="{method:getName($mt)}" width="145px" case="mixed" tab="{meta:getId($test)}, {meta:getName($test)}" field="Integer" required="true" cat="method">
                        <col width="145"  />
                      </autoComplete>
                    </widget>
                  </row>
                </TablePanel>
                <VerticalPanel height="10px" />
                <TabPanel key="testTabPanel" width="635px" halign="center">
                  <tab key="detailsTab" text="{resource:getString($constants,'testDetails')}">
                    <VerticalPanel padding="0" spacing="0">
                      <TablePanel style="Form">
                        <row>
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'description')" />:
                          </text>
                          <widget colspan="2">
                            <textbox key="{meta:getDescription($test)}" width="425px" max="60" tab="{meta:getReportingDescription($test)},{script:getName($scpt)}" field="String" required="true" />
                          </widget>
                        </row>
                        <row>
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'reportDescription')" />:
                          </text>
                          <widget colspan="2">
                            <textbox key="{meta:getReportingDescription($test)}" width="425px" max="60" tab="{meta:getTimeTaMax($test)},{meta:getDescription($test)}" field="String" required="true" />
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
                              <textbox key="{meta:getTimeTaMax($test)}" width="50px" tab="{meta:getTimeTaAverage($test)},{meta:getIsReportable($test)}" field="Integer" required="true" />
                              <text style="Prompt">
                                <xsl:value-of select="resource:getString($constants,'timeTransit')" />:
                              </text>
                              <textbox key="{meta:getTimeTransit($test)}" width="50px" tab="{meta:getTimeHolding($test)},{meta:getTimeTaWarning($test)}" field="Integer" required="true" />
                            </row>
                            <row>
                              <text style="Prompt">
                                <xsl:value-of select='resource:getString($constants,"turnAroundAverage")' />:
                              </text>
                              <textbox key="{meta:getTimeTaAverage($test)}" width="50px" tab="{meta:getTimeTaWarning($test)},{meta:getTimeTaMax($test)}" field="Integer" required="true" />
                              <text style="Prompt">
                                <xsl:value-of select="resource:getString($constants,'timeHolding')" />:
                              </text>
                              <textbox key="{meta:getTimeHolding($test)}" width="50px" tab="{meta:getIsActive($test)},{meta:getTimeTransit($test)}" field="Integer" required="true" />
                            </row>
                            <row>
                              <text style="Prompt">
                                <xsl:value-of select='resource:getString($constants,"turnAroundWarn")' />:
                              </text>
                              <textbox key="{meta:getTimeTaWarning($test)}" width="50px" tab="{meta:getTimeTransit($test)},{meta:getTimeTaAverage($test)}" field="Integer" required="true" />
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
                              <check key="{meta:getIsActive($test)}" tab="{meta:getActiveBegin($test)},{meta:getTimeHolding($test)}" required="true" />
                            </row>
                            <row>
                              <text style="Prompt">
                                <xsl:value-of select='resource:getString($constants,"beginDate")' />:
                              </text>
                              <calendar key="{meta:getActiveBegin($test)}" begin="0" end="2" width="90px" tab="{meta:getActiveEnd($test)},{meta:getIsActive($test)}" field="Date" required="true" />
                            </row>
                            <row>
                              <text style="Prompt">
                                <xsl:value-of select='resource:getString($constants,"endDate")' />:
                              </text>
                              <calendar key="{meta:getActiveEnd($test)}" begin="0" end="2" width="90px" tab="{label:getName($lbl)},{meta:getActiveBegin($test)}" field="Date" required="true" />
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
                                <autoComplete key="{label:getName($lbl)}" width="224px" case="lower" tab="{meta:getLabelQty($test)},{meta:getActiveEnd($test)}" field="Integer" cat="label">
                                  <col width="224"/>
                                </autoComplete>
                              </widget>
                            </row>
                            <row>
                              <text style="Prompt">
                                <xsl:value-of select='resource:getString($constants,"quantity")' />:
                              </text>
                              <textbox key="{meta:getLabelQty($test)}" width="50px" tab="{meta:getIsReportable($test)},{label:getName($lbl)}" field="Integer" />
                            </row>
                            <row>
                              <widget>
                                <VerticalPanel height="54px" />
                              </widget>
                            </row>
                          </TablePanel>
                        </VerticalPanel>
                        <HorizontalPanel width="1px" />
                        <VerticalPanel style="subform">
                          <text style="FormTitle">
                            <xsl:value-of select='resource:getString($constants,"sections")' />
                          </text>
                          <widget valign="bottom">
                            <table key="sectionTable" width="auto" maxRows="2" showScroll="ALWAYS" tab="removeTestSectionButton,{meta:getIsReportable($test)}" title="">
                              <col key="{testSection:getSectionId($ts)}" width="119" sort="false" header="{resource:getString($constants,'name')}">
                                <dropdown width="85" case="mixed" field="Integer" required="true" />
                              </col>
                              <col key="{testSection:getFlagId($ts)}" width="119" sort="false" header="{resource:getString($constants,'options')}">
                                <dropdown width="110" case="mixed" field="Integer" required="true" />
                              </col>
                            </table>
                          </widget>
                          <HorizontalPanel style="WhiteContentPanel">
                            <widget halign="center" style="WhiteContentPanel">
                              <appButton key="addSectionButton" style="Button">
                                <HorizontalPanel>
                                  <AbsolutePanel style="AddRowButtonImage" />
                                  <text>
                                    <xsl:value-of select="resource:getString($constants,'addRow')" />
                                  </text>
                                </HorizontalPanel>
                              </appButton>
                            </widget>
                            <widget halign="center" style="WhiteContentPanel">
                              <appButton key="removeSectionButton" style="Button" tab="{meta:getRevisionMethodId($test)},sectionTable">
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
                          <text halign="right" style="Prompt">
                            <xsl:value-of select='resource:getString($constants,"testReportable")' />:
                          </text>
                          <check key="{meta:getIsReportable($test)}" tab="sectionTable,{meta:getLabelQty($test)}" required="true" />
                        </row>
                        <row>
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'revisionMethod')" />:
                          </text>
                          <dropdown key="{meta:getRevisionMethodId($test)}" width="190px" tab="{meta:getSortingMethodId($test)},removeTestSectionButton" field="Integer" />
                          <text halign="left" style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'testTrailer')" />:
                          </text>
                          <autoComplete key="{testTrailer:getName($tt)}" width="180px" case="lower" tab="{meta:getTestFormatId($test)},{meta:getReportingSequence($test)}" field="Integer" required="false" cat="trailer">
                            <col width="180"/>
                          </autoComplete>
                        </row>
                        <row>
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'sortingMethod')" />:
                          </text>
                          <dropdown key="{meta:getSortingMethodId($test)}" width="190px" tab="{meta:getReportingMethodId($test)},{meta:getRevisionMethodId($test)}" field="Integer" />
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'testFormat')" />:
                          </text>
                          <dropdown key="{meta:getTestFormatId($test)}" width="180px" tab="{script:getName($scpt)},{testTrailer:getName($tt)}" field="Integer" />
                        </row>
                        <row>
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'reportingMethod')" />:
                          </text>
                          <dropdown key="{meta:getReportingMethodId($test)}" width="190px" tab="{meta:getReportingSequence($test)},{meta:getSortingMethodId($test)}" field="Integer" />
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'scriptlet')" />:
                          </text>
                          <autoComplete key="{script:getName($scpt)}" width="180px" case="lower" tab="{meta:getDescription($test)},{meta:getTestFormatId($test)}" field="Integer" cat="scriptlet">
                            <col width="180"/>
                          </autoComplete>
                        </row>
                        <row>
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'reportingSequence')" />:
                          </text>
                          <textbox key="{meta:getReportingSequence($test)}" width="80px" tab="{testTrailer:getName($tt)},{meta:getReportingMethodId($test)}" field="Integer" />
                        </row>
                      </TablePanel>
                      <VerticalPanel height="36px" />
                    </VerticalPanel>
                  </tab>
                  <tab key="sampleTypeTab" text="{resource:getString($constants,'sampleType')}">
                    <VerticalPanel>
                      <HorizontalPanel>
                        <widget valign="top">
                          <table key="sampleTypeTable" width="auto" maxRows="21" showScroll="ALWAYS" showError="false" title="">
                            <col key="{testTOS:getTypeOfSampleId($tos)}" width="290" sort="false" header="{resource:getString($constants,'sampleType')}">
                              <dropdown width="285" popWidth = "285" case="mixed" field="Integer" required="true" />
                            </col>
                            <col key="{testTOS:getUnitOfMeasureId($tos)}" width="291" sort="false" header="{resource:getString($constants,'unitOfMeasure')}">
                              <dropdown width="286" popWidth = "286" case="mixed" field="Integer" />
                            </col>
                          </table>
                        </widget>
                      </HorizontalPanel>
                      <HorizontalPanel style="WhiteContentPanel">
                        <widget halign="center" style="WhiteContentPanel">
                          <appButton key="addSampleTypeButton" style="Button">
                            <HorizontalPanel>
                              <AbsolutePanel style="AddRowButtonImage" />
                              <text>
                                <xsl:value-of select="resource:getString($constants,'addRow')" />
                              </text>
                            </HorizontalPanel>
                          </appButton>
                        </widget>
                        <widget halign="center" style="WhiteContentPanel">
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
                      <VerticalPanel height="7px" />
                    </VerticalPanel>
                  </tab>
                  <tab key="analyteTab" text="{resource:getString($constants,'analytesResults')}">
                    <VerticalPanel>
                      <HorizontalPanel>
                        <HorizontalPanel width="455px">
                          <widget valign="top">
                            <table key="analyteTable" width="420px" maxRows="10" showScroll="ALWAYS" drag="default" drop="default" targets="analyteTable">
                              <col key="analyteLookup" width="150" sort="false" header="1">
                                <autoComplete width="125px" case="mixed" popWidth="auto" field="Integer" autoCall="AnalyteAndResultTab" cat="analyte">
                                  <col width="300" field="Integer" required="true" header="Name" />
                                </autoComplete>
                              </col>
                              <col key="{testAnalyte:getResultGroup($tana)}" width="150" sort="false" header="2">
                                <autoComplete key="analyteLookup" width="125px" case="mixed" popWidth="auto" field="Integer" autoCall="AnalyteAndResultTab">
                                  <col width="150" field="Integer" header="Name" />
                                </autoComplete>
                              </col>
                              <col key="analyteLookup2" width="150" sort="false" header="3">
                                <autoComplete key="analyteLookup" width="125px" case="mixed" popWidth="auto" field="Integer" autoCall="AnalyteAndResultTab">
                                  <col width="300" field="Integer" header="Name" />
                                </autoComplete>
                              </col>
                              <col key="analyteLookup3" width="150" sort="false" header="4">
                                <autoComplete key="analyteLookup" width="125px" case="mixed" popWidth="auto" field="Integer" autoCall="AnalyteAndResultTab">
                                  <col width="150" field="Integer" header="Name" />
                                </autoComplete>
                              </col>
                              <col key="analyteLookup4" width="150" sort="false" header="5">
                                <autoComplete key="analyteLookup" width="125px" case="mixed" popWidth="auto" field="Integer" autoCall="AnalyteAndResultTab">
                                  <col width="150" field="Integer" header="Name" />
                                </autoComplete>
                              </col>
                              <col key="analyteLookup5" width="150" sort="false" header="6">
                                <autoComplete key="analyteLookup" width="125px" case="mixed" popWidth="auto" field="Integer" autoCall="AnalyteAndResultTab">
                                  <col width="150" field="Integer" header="Name" />
                                </autoComplete>
                              </col>
                              <col key="analyteLookup6" width="150" sort="false" header="7">
                                <autoComplete key="analyteLookup" width="125px" case="mixed" popWidth="auto" field="Integer" autoCall="AnalyteAndResultTab">
                                  <col width="150" field="Integer" header="Name" />
                                </autoComplete>
                              </col>
                              <col key="analyteLookup7" width="150" sort="false" header="8">
                                <autoComplete key="analyteLookup" width="125px" case="mixed" popWidth="auto" field="Integer" autoCall="AnalyteAndResultTab">
                                  <col width="150" field="Integer" header="Name" />
                                </autoComplete>
                              </col>
                              <col key="analyteLookup8" width="150" sort="false" header="9">
                                <autoComplete key="analyteLookup" width="125px" case="mixed" popWidth="auto" field="Integer" autoCall="AnalyteAndResultTab">
                                  <col width="150" field="Integer" header="Name" />
                                </autoComplete>
                              </col>
                              <col key="analyteLookup9" width="150" sort="false" header="10">
                                <autoComplete key="analyteLookup" width="125px" case="mixed" popWidth="auto" field="Integer" autoCall="AnalyteAndResultTab">
                                  <col width="150" field="Integer" header="Name" />
                                </autoComplete>
                              </col>
                            </table>
                          </widget>
                        </HorizontalPanel>
                        <VerticalPanel>
                          <TablePanel style="Form">
                            <row>
                              <text style="Prompt">
                                <xsl:value-of select="resource:getString($constants,'type')" />:
                              </text>
                              <widget colspan="4">
                                <dropdown key="{testAnalyte:getTypeId($tana)}" width="100px" field="Integer" required="true" />
                              </widget>
                            </row>
                            <row>
                              <text style="Prompt">
                                <xsl:value-of select="resource:getString($constants,'analyteReportable')" />:
                              </text>
                              <check key="{testAnalyte:getIsReportable($tana)}" width="50px" />
                            </row>
                            <row>
                              <text style="Prompt">
                                <xsl:value-of select="resource:getString($constants,'scriptlet')" />:
                              </text>
                              <widget colspan="4">
                                <autoComplete key="{testAnalyte:getScriptletId($tana)}" width="100px" case="lower" field="Integer" cat="scriptlet">
                                  <col width="165" header="Name" />
                                </autoComplete>
                              </widget>
                            </row>
                          </TablePanel>
                          <!-- <VerticalPanel height="85px"/> -->
                          <VerticalPanel>
                            <HorizontalPanel style="WhiteContentPanel">
                              <widget halign="left" style="WhiteContentPanel">
                                <appButton key="addAnalyteButton" style="Button">
                                  <HorizontalPanel>
                                    <AbsolutePanel style="AddRowButtonImage" />
                                    <text>+ Analyte</text>
                                  </HorizontalPanel>
                                </appButton>
                              </widget>
                              <widget halign="left" style="WhiteContentPanel">
                                <appButton key="removeAnalyteButton" style="Button">
                                  <HorizontalPanel>
                                    <AbsolutePanel style="WhiteContentPanel" />
                                    <text>- Analyte</text>
                                  </HorizontalPanel>
                                </appButton>
                              </widget>
                            </HorizontalPanel>
                            <HorizontalPanel>
                              <widget halign="left" style="WhiteContentPanel">
                                <appButton key="addHeaderButton" style="Button">
                                  <HorizontalPanel>
                                    <AbsolutePanel style="WhiteContentPanel" />
                                    <text>+ Header</text>
                                  </HorizontalPanel>
                                </appButton>
                              </widget>
                              <widget halign="left" style="WhiteContentPanel">
                                <appButton key="removeHeaderButton" style="Button">
                                  <HorizontalPanel>
                                    <AbsolutePanel style="RemoveRowButtonImage"/>
                                    <text>- Header</text>
                                  </HorizontalPanel>
                                </appButton>
                              </widget>
                            </HorizontalPanel>
                            <HorizontalPanel>
                              <widget halign="left" style="WhiteContentPanel">
                                <appButton key="addColumnButton" style="Button">
                                  <HorizontalPanel>
                                    <AbsolutePanel style="ButtonPanelButton" />
                                    <text>+ Column</text>
                                  </HorizontalPanel>
                                </appButton>
                              </widget>
                              <widget halign="left" style="WhiteContentPanel">
                                <appButton key="removeColumnButton" style="Button">
                                  <HorizontalPanel>
                                    <AbsolutePanel style="ButtonPanelButton" />
                                    <text>- Column</text>
                                  </HorizontalPanel>
                                </appButton>
                              </widget>
                            </HorizontalPanel>
                          </VerticalPanel>
                        </VerticalPanel>
                      </HorizontalPanel>
               
                    <HorizontalPanel>
                      <VerticalPanel width = "545px"> 
                      <ScrollTabBar key="resultTabPanel" width = "545px"/>                                                                                            
                          <widget valign="top">
                            <table key="resultTable" width="520px" maxRows="7" showScroll="ALWAYS" showError="false" title="">
                              <col key="{testResult:getUnitOfMeasureId($tr)}" width="50" sort="true" header="{resource:getString($constants,'unit')}">
                                <dropdown width="75" case="mixed" field="Integer" />
                              </col>
                              <col key="{testResult:getTypeId($tr)}" width="55" sort="true" header="{resource:getString($constants,'type')}">
                                <dropdown width="75" case="mixed" field="Integer" required="true" />
                              </col>
                              <col key="{testResult:getValue($tr)}" width="200" sort="true" header="{resource:getString($constants,'value')}">
                                <textbox case="mixed" field="String" />
                              </col>
                              <col key="{testResult:getFlagsId($tr)}" width="110" sort="true" header="{resource:getString($constants,'flags')}">
                                <dropdown width="115" case="mixed" field="Integer" />
                              </col>
                              <col key="{testResult:getSignificantDigits($tr)}" width="40" header="{resource:getString($constants,'significantDigits')}">
                                <textbox case="mixed" field="Integer" />
                              </col>
                              <col key="{testResult:getRoundingMethodId($tr)}" width="90" header="{resource:getString($constants,'roundingMethod')}">
                                <dropdown width="95" case="mixed" field="Integer" />
                              </col>
                            </table>
                          </widget>
                        </VerticalPanel>
                        <VerticalPanel>
                          <VerticalPanel height = "30px"/>	
                          <widget halign="center" style="WhiteContentPanel">
                            <appButton key="addResultTabButton" style="Button">
                              <HorizontalPanel>
                                <AbsolutePanel style="ButtonPanelButton"/>
                                <text>+ Group</text>
                              </HorizontalPanel>
                            </appButton>
                          </widget>
                          <widget halign="center" style="WhiteContentPanel">
                            <appButton key="addTestResultButton" style="Button">
                              <HorizontalPanel>
                                <AbsolutePanel style="AddRowButtonImage" />
                                <text>+ Row</text>
                              </HorizontalPanel>
                            </appButton>
                          </widget>
                          <widget halign="center" style="WhiteContentPanel">
                            <appButton key="removeTestResultButton" style="Button">
                              <HorizontalPanel>
                                <AbsolutePanel style="RemoveRowButtonImage" />
                                <text>- Row</text>
                              </HorizontalPanel>
                            </appButton>
                          </widget>
                          <widget halign="center" style="WhiteContentPanel">
                            <appButton key="dictionaryLookUpButton" style="Button">
                              <HorizontalPanel>
                                <AbsolutePanel style="ButtonPanelButton" />
                                <text>
                                  <xsl:value-of select='resource:getString($constants,"dictionary")' />
                                </text>
                              </HorizontalPanel>
                            </appButton>
                          </widget>
                        </VerticalPanel>
                     </HorizontalPanel> 
                    </VerticalPanel>
                  </tab>
                  <tab key="prepAndReflexTab" text="{resource:getString($constants,'prepAndReflex')}">
                    <VerticalPanel>
                      <VerticalPanel padding="0" spacing="0">
                        <VerticalPanel padding="0" spacing="0">
                          <HorizontalPanel>
                            <widget valign="top">
                              <table key="testPrepTable" width="auto" maxRows="9" showScroll="ALWAYS" manager="this" showError="false" title="">
                                <col key="{prepTestMeta:getName($pt)}" width="489" header="{resource:getString($constants,'prepTestMethod')}">
                                  <autoComplete width="470" case="mixed" field="Integer" cat="testMethod">
                                    <col width="470" field="Integer" header="Name" />
                                  </autoComplete>
                                </col>
                                <col key="{testPrep:getIsOptional($tp)}" header="{resource:getString($constants,'optional')}">
                                  <check />
                                </col>
                              </table>
                            </widget>
<!-- 
<HorizontalPanel width = "10px"/>
  -->
                          </HorizontalPanel>
                          <HorizontalPanel style="WhiteContentPanel">
                            <widget halign="center" style="WhiteContentPanel">
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
                            <widget halign="center" style="WhiteContentPanel">
                              <appButton  key="removePrepTestButton" style="Button">
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
                          <VerticalPanel height="10px" />
                          <HorizontalPanel>
                            <widget valign="top">
                              <table key="testReflexTable" width="auto" maxRows="9" showScroll="ALWAYS" manager="this" showError="false" title="">
                                <col key="{testRef:getAddTestId($tref)}" width="150" header="{resource:getString($constants,'reflexiveTest')}">
                                  <autoComplete width="150px" case="mixed" cat="testMethod">
                                    <col width="160" header="Name" />
                                  </autoComplete>
                                </col>
                                <col key="{testRef:getTestAnalyteId($tref)}" width="181" header="{resource:getString($constants,'testAnalyte')}">
                                  <dropdown width="181" case="mixed" field="Integer" required="true" />
                                </col>
                                <col key="{testRef:getTestResultId($tref)}" width="140" header="{resource:getString($constants,'result')}">
                                  <dropdown width="140" case="mixed" field="Integer" required="true" />
                                </col>
                                <col key="{testRef:getFlagsId($tref)}" width="104" header="{resource:getString($constants,'flags')}">
                                  <dropdown width="200" case="mixed" field="Integer" required="false" />
                                </col>
                              </table>
                            </widget>
                          </HorizontalPanel>
                          <HorizontalPanel style="WhiteContentPanel">
                            <widget halign="center" style="WhiteContentPanel">
                              <appButton  key="addReflexTestButton" style="Button">
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
                            <widget halign="center" style="WhiteContentPanel">
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
                  <tab key="worksheetTab" text="{resource:getString($constants,'worksheetLayout')}">
                    <VerticalPanel>
                      <VerticalPanel>
                        <TablePanel style="Form">
                          <row>
                            <text style="Prompt">
                              <xsl:value-of select="resource:getString($constants,'numberFormat')" />:
                            </text>
                            <widget colspan="4">
                              <dropdown key="{testWrksht:getFormatId($tws)}" width="145px" case="mixed" tab="{testWrksht:getBatchCapacity($tws)}, {wsscript:getName($wss)}" field="Integer" />
                            </widget>
                          </row>
                          <row>
                            <text style="Prompt">
                              <xsl:value-of select="resource:getString($constants,'batchCapacity')" />:
                            </text>
                            <widget>
                              <textbox key="{testWrksht:getBatchCapacity($tws)}" width="40px" tab="{testWrksht:getTotalCapacity($tws)},{testWrksht:getFormatId($tws)}" field="Integer" />
                            </widget>
                            <widget valign="middle">
                              <text style="Prompt">
                                <xsl:value-of select="resource:getString($constants,'totalCapacity')" />:
                              </text>
                            </widget>
                            <widget>
                              <textbox key="{testWrksht:getTotalCapacity($tws)}" width="40px" tab="{wsscript:getName($wss)},{testWrksht:getBatchCapacity($tws)}" field="Integer" />
                            </widget>
                          </row>
                          <row>
                            <text style="Prompt">
                              <xsl:value-of select="resource:getString($constants,'scriptlet')" />:
                            </text>
                            <widget colspan="3">
                              <autoComplete key="{wsscript:getName($wss)}" width="235px" case="lower" field="Integer" cat="scriptlet">
                                <widths>235</widths>
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
                            <table key="worksheetTable" width="auto" maxRows="7" showScroll="ALWAYS" tab="removeWSItemButton, {testWrksht:getScriptletId($tws)}" manager="this" showError="false" title="">
                              <col key="{testWrkshtItm:getPosition($twsi)}" width="74" header="{resource:getString($constants,'position')}">
                                <textbox field="Integer" />
                              </col>
                              <col key="{testWrkshtItm:getTypeId($twsi)}" width="147" header="{resource:getString($constants,'type')}">
                                <dropdown width="140" case="mixed" field="Integer" required="true" />
                              </col>
                              <col key="{testWrkshtItm:getQcName($twsi)}" width="347" header="{resource:getString($constants,'qcName')}">
                                <autoComplete width="340" case="mixed" field="Integer" cat="testMethod">
                                  <col width="350" header="Name" />
                                </autoComplete>
                              </col>
                            </table>
                          </widget>
                        </HorizontalPanel>
                        <HorizontalPanel style="WhiteContentPanel">
                          <widget halign="center" style="WhiteContentPanel">
                            <appButton action = "action" key="addWSItemButton" style="Button">
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
                          <widget halign="center" style="WhiteContentPanel">
                            <appButton key="removeWSItemButton" style="Button" tab="worksheetTable, worksheetAnalyteTable" action="removeRow">
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
                      <VerticalPanel height="3px" />
                      <VerticalPanel style="subform">
                        <text style="FormTitle">
                          <xsl:value-of select='resource:getString($constants,"analytesWS")' />
                        </text>
                        <HorizontalPanel>
                          <widget valign="top">
                            <table key="worksheetAnalyteTable" width="auto" maxRows="6" showScroll="ALWAYS" tab="removeWSItemButton, {testWrksht:getScriptletId($tws)}" title="">
                              <col key="analyteName" width="275" header="{resource:getString($constants,'analyte')}">
                                <label field="String" />
                              </col>
                              <col key="available" width="156" header="{resource:getString($constants,'availableWS')}">
                                <check />
                              </col>
                              <col key="{testWrkshtAna:getRepeat($twsa)}" width="65" header="{resource:getString($constants,'repeat')}">
                                <textbox field="Integer" />
                              </col>
                              <col key="{testWrkshtAna:getFlagId($twsa)}" width="72" header="{resource:getString($constants,'flag')}">
                                <dropdown width="140" case="mixed" field="Integer" required="true" />
                              </col>
                            </table>
                          </widget>
                        </HorizontalPanel>
                      </VerticalPanel>
                    </VerticalPanel>
                  </tab>
                </TabPanel>
              </VerticalPanel>
              <HorizontalPanel width="10px" />
            </HorizontalPanel>
          </VerticalPanel>
        </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>