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
                xmlns:meta="xalan://org.openelis.metamap.TestMetaMap" 
                xmlns:addTestMeta="xalan://org.openelis.metamap.TestMeta" 
                xmlns:label="xalan://org.openelis.meta.LabelMeta"
                xmlns:method="xalan://org.openelis.meta.MethodMeta"
                xmlns:prepTestMeta="xalan://org.openelis.metamap.TestMeta" 
                xmlns:script="xalan://org.openelis.meta.ScriptletMeta" 
                xmlns:testAnalyte="xalan://org.openelis.metamap.TestAnalyteMetaMap"
                xmlns:testPrep="xalan://org.openelis.metamap.TestPrepMetaMap"
                xmlns:testRef="xalan://org.openelis.metamap.TestReflexMetaMap"
                xmlns:testRefRes="xalan://org.openelis.meta.TestResultMeta"
                xmlns:testRefTana="xalan://org.openelis.metamap.TestAnalyteMetaMap"
                xmlns:testRefAna="xalan://org.openelis.meta.AnalyteMeta"
                xmlns:testResult="xalan://org.openelis.metamap.TestResultMetaMap"
                xmlns:testSection="xalan://org.openelis.metamap.TestSectionMetaMap"
                xmlns:testTOS="xalan://org.openelis.metamap.TestTypeOfSampleMetaMap"
                xmlns:testTrailer="xalan://org.openelis.meta.TestTrailerMeta"
                xmlns:testWrksht="xalan://org.openelis.metamap.TestWorksheetMetaMap" 
                xmlns:testWrkshtAna="xalan://org.openelis.metamap.TestWorksheetAnalyteMetaMap" 
                xmlns:testWrkshtItm="xalan://org.openelis.metamap.TestWorksheetItemMetaMap"
                xmlns:wsscript="xalan://org.openelis.meta.ScriptletMeta">
  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
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
  <xalan:component prefix="testRefRes">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.TestResultMeta" />
  </xalan:component>
  <xalan:component prefix="testRefTana">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TestAnalyteMetaMap" />
  </xalan:component>
  <xalan:component prefix="testRefAna">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.AnalyteMeta" />
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
    <xsl:variable name="trefr" select="testRef:getTestResult($tref)" />
    <xsl:variable name="trefta" select="testRef:getTestAnalyte($tref)" />
    <xsl:variable name="trefa" select="testRefTana:getAnalyte($trefta)" />
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
        <HorizontalPanel height = "604" padding="0" spacing="0">
        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="225px">
            <buttonGroup key="atozButtons">
              <xsl:call-template name="aToZLeftPanelButtons" />
            </buttonGroup>
            <VerticalPanel>
              <table key="atozTable" width="auto" maxRows="26">
                <col width="175" header="{resource:getString($constants,'nameMethod')}">
                  <label />
                </col>
              </table>
              <widget halign="center">
              <HorizontalPanel >
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
                <xsl:call-template name="optionsButton">
                  <xsl:with-param name="language">
                    <xsl:value-of select="language" />
                  </xsl:with-param>
                </xsl:call-template>
                </HorizontalPanel>
            </AbsolutePanel>            
            <HorizontalPanel padding="0" spacing="0" style="WhiteContentPanel">
              <VerticalPanel>
                <TablePanel style="Form">
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'id')" />:
                    </text>
                    <textbox key="{meta:getId($test)}" width="50px" tab="{meta:getName($test)},{method:getName($mt)}" field="Integer" />
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'name')" />:
                    </text>
                    <textbox key="{meta:getName($test)}" width="145px"  max="20" tab="{method:getName($mt)},{meta:getId($test)}" field="String" required="true" />
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'method')" />:
                    </text>
                    <widget>
                      <autoComplete key="{method:getName($mt)}" width="145px" tab="{meta:getId($test)}, {meta:getName($test)}" field="Integer" required="true">
                        <col width="145"  />
                      </autoComplete>
                    </widget>
                  </row>
                </TablePanel>
                <TabPanel key="testTabPanel" width="620px">
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
                              <calendar key="{meta:getActiveBegin($test)}" begin="0" end="2" width="90px" tab="{meta:getActiveEnd($test)},{meta:getIsActive($test)}"  required="true" />
                            </row>
                            <row>
                              <text style="Prompt">
                                <xsl:value-of select='resource:getString($constants,"endDate")' />:
                              </text>
                              <calendar key="{meta:getActiveEnd($test)}" begin="0" end="2" width="90px" tab="{label:getName($lbl)},{meta:getActiveBegin($test)}"  required="true" />
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
                                <autoComplete key="{label:getName($lbl)}" width="224px"  tab="{meta:getLabelQty($test)},{meta:getActiveEnd($test)}" field="Integer" >
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
                                <dropdown width="85" case="MIXED" field="Integer" required="true" />
                              </col>
                              <col key="{testSection:getFlagId($ts)}" width="119" sort="false" header="{resource:getString($constants,'options')}">
                                <dropdown width="110" case="MIXED" field="Integer" />
                              </col>
                            </table>
                          </widget>
                          <HorizontalPanel style="TableFooterPanel">
                            <widget halign="center" >
                              <appButton key="addSectionButton" style="Button">
                                <HorizontalPanel>
                                  <AbsolutePanel style="AddRowButtonImage" />
                                  <text>
                                    <xsl:value-of select="resource:getString($constants,'addRow')" />
                                  </text>
                                </HorizontalPanel>
                              </appButton>
                            </widget>
                            <widget halign="center" >
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
                          <text  style="Prompt">
                            <xsl:value-of select='resource:getString($constants,"testReportable")' />:
                          </text>
                          <check key="{meta:getIsReportable($test)}" tab="sectionTable,{meta:getLabelQty($test)}" required="true" />
                        </row>
                        <row>
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'revisionMethod')" />:
                          </text>
                          <dropdown key="{meta:getRevisionMethodId($test)}" width="190px" tab="{meta:getSortingMethodId($test)},removeTestSectionButton" field="Integer" />
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'testTrailer')" />:
                          </text>
                          <autoComplete key="{testTrailer:getName($tt)}" width="180px" case="LOWER" tab="{meta:getTestFormatId($test)},{meta:getReportingSequence($test)}" field="Integer" required="false">
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
                          <dropdown key="{meta:getTestFormatId($test)}" width="180px" tab="{script:getName($scpt)},{testTrailer:getName($tt)}" field="Integer"/>
                        </row>
                        <row>
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'reportingMethod')" />:
                          </text>
                          <dropdown key="{meta:getReportingMethodId($test)}" width="190px" tab="{meta:getReportingSequence($test)},{meta:getSortingMethodId($test)}" field="Integer"/>
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'scriptlet')" />:
                          </text>
                          <autoComplete key="{script:getName($scpt)}" width="180px" case="LOWER" tab="{meta:getDescription($test)},{meta:getTestFormatId($test)}" field="Integer">
                            <col width="180"/>
                          </autoComplete>
                        </row>
                        <row>
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'reportingSequence')" />:
                          </text>
                          <textbox key="{meta:getReportingSequence($test)}" width="80px" tab="{testTrailer:getName($tt)},{meta:getReportingMethodId($test)}" field="Integer"/>
                        </row>
                      </TablePanel>
                      <VerticalPanel height="102px" />
                    </VerticalPanel>
                  </tab>
                  <tab key="sampleTypeTab" text="{resource:getString($constants,'sampleType')}">
                    <VerticalPanel>
                      <HorizontalPanel>
                        <widget valign="top">
                          <table key="sampleTypeTable" width="auto" maxRows="21" showScroll="ALWAYS" title="">
                            <col key="{testTOS:getTypeOfSampleId($tos)}" width="300" sort="false" header="{resource:getString($constants,'sampleType')}">
                              <dropdown width="300" popWidth = "300" case="MIXED" field="Integer" required="true" />
                            </col>
                            <col key="{testTOS:getUnitOfMeasureId($tos)}" width="297" sort="false" header="{resource:getString($constants,'unitOfMeasure')}">
                              <dropdown width="297" popWidth = "297" case="MIXED" field="Integer" />
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
                      <VerticalPanel height="20px" />
                    </VerticalPanel>
                  </tab>
                  <tab key="analyteTab" text="{resource:getString($constants,'analytesResults')}">
                  <VerticalPanel padding="0" spacing="0">
                          <table key="analyteTable" width="604px" maxRows="9" showScroll="ALWAYS">
                              <col key="analyteLookup" width="152" sort="false">
                                <autoComplete width="125px" case="MIXED" popWidth="auto" field="Integer">
                                  <col width="300"/>
                                </autoComplete>
                              </col>
                              <col key="analyteLookup2" width="150" sort="false">
                                <autoComplete key="analyteLookup" width="125px" case="MIXED" popWidth="auto" field="Integer">
                                  <col width="150" />
                                </autoComplete>
                              </col>
                              <col key="analyteLookup3" width="150" sort="false">
                                <autoComplete key="analyteLookup" width="125px" case="MIXED" popWidth="auto" field="Integer">
                                  <col width="300"/>
                                </autoComplete>
                              </col>
                              <col key="analyteLookup4" width="150" sort="false">
                                <autoComplete key="analyteLookup" width="125px" case="MIXED" popWidth="auto" field="Integer">
                                  <col width="150"/>
                                </autoComplete>
                              </col>
                              <col key="analyteLookup5" width="150" sort="false">
                                <autoComplete key="analyteLookup" width="125px" case="MIXED" popWidth="auto" field="Integer">
                                  <col width="150"/>
                                </autoComplete>
                              </col>
                              <col key="analyteLookup6" width="150" sort="false">
                                <autoComplete key="analyteLookup" width="125px" case="MIXED" popWidth="auto" field="Integer">
                                  <col width="150"/>
                                </autoComplete>
                              </col>
                              <col key="analyteLookup7" width="150" sort="false">
                                <autoComplete key="analyteLookup" width="125px" case="MIXED" popWidth="auto" field="Integer">
                                  <col width="150"/>
                                </autoComplete>
                              </col>
                              <col key="analyteLookup8" width="150" sort="false">
                                <autoComplete key="analyteLookup" width="125px" case="MIXED" popWidth="auto" field="Integer">
                                  <col width="150"/>
                                </autoComplete>
                              </col>
                              <col key="analyteLookup9" width="150" sort="false">
                                <autoComplete key="analyteLookup" width="125px" case="MIXED" popWidth="auto" field="Integer">
                                  <col width="150"/>
                                </autoComplete>
                              </col>
                              <col key="analyteLookup10" width="150" sort="false">
                                <autoComplete key="analyteLookup" width="125px" case="MIXED" popWidth="auto" field="Integer">
                                  <col width="150"/>
                                </autoComplete>
                              </col>
                            </table>
                            <HorizontalPanel style="TableButtonFooter">
                            <TablePanel padding="0" spacing="0">
                            <row>
                            <dropdown key="tableActions" width="125" field="String"/>
                            <appButton key="addButton" style="Button">
                                     <AbsolutePanel style="AddButtonIcon" />
                                </appButton>
                             <appButton key="removeButton" style="Button">
                                    <AbsolutePanel style="DeleteButtonIcon" />
                                </appButton>
                                </row>
                                </TablePanel>
                                <HorizontalPanel width = "55px"/>
                                <TablePanel>
                                <row>
                               <text style="Prompt">
                                <xsl:value-of select="resource:getString($constants,'type')" />:
                              </text>
                                <dropdown key="{testAnalyte:getTypeId($tana)}" width="100px" field="Integer" />
                              <text style="Prompt">
                                <xsl:value-of select="resource:getString($constants,'analyteReportable')" />:
                              </text>
                              <check key="{testAnalyte:getIsReportable($tana)}"/>
                              <text style="Prompt">
                                <xsl:value-of select="resource:getString($constants,'scriptlet')" />:
                              </text>
                                <autoComplete key="{testAnalyte:getScriptletId($tana)}" width="100px" case="LOWER" field="Integer">
                                  <col width="165" header="Name" />
                                </autoComplete>
                            </row>
                            </TablePanel>
                		</HorizontalPanel>	
                      <VerticalPanel> 
                      <ScrollTabBar key="resultTabPanel" width = "602px"/>                                                                                            
                          <widget valign="top">
                            <table key="resultTable" width="auto" maxRows="9" showScroll="ALWAYS" title="">
                              <col key="{testResult:getUnitOfMeasureId($tr)}" width="75" sort="true" header="{resource:getString($constants,'unit')}">
                                <dropdown width="75" case="MIXED" field="Integer" />
                              </col>
                              <col key="{testResult:getTypeId($tr)}" width="90" sort="true" header="{resource:getString($constants,'type')}">
                                <dropdown width="75" case="MIXED" field="Integer" required="true" />
                              </col>
                              <col key="{testResult:getValue($tr)}" width="200" sort="true" header="{resource:getString($constants,'value')}">
                                <textbox case="MIXED" field="String" />
                              </col>
                              <col key="{testResult:getFlagsId($tr)}" width="110" sort="true" header="{resource:getString($constants,'flags')}">
                                <dropdown width="115" case="MIXED" field="Integer" />
                              </col>
                              <col key="{testResult:getSignificantDigits($tr)}" width="40" header="{resource:getString($constants,'significantDigits')}">
                                <textbox case="MIXED" field="Integer" />
                              </col>
                              <col key="{testResult:getRoundingMethodId($tr)}" width="70" header="{resource:getString($constants,'roundingMethod')}">
                                <dropdown width="95" case="MIXED" field="Integer" />
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
                                <AbsolutePanel style="AddTabButtonImage"/>
                                <text><xsl:value-of select="resource:getString($constants,'addGroup')" /></text>
                              </HorizontalPanel>
                            </appButton>
                            <appButton key="addTestResultButton" style="Button">
                              <HorizontalPanel>
                                <AbsolutePanel style="AddRowButtonImage" />
                                <text><xsl:value-of select="resource:getString($constants,'addRow')" /></text>
                              </HorizontalPanel>
                            </appButton>
                            <appButton key="removeTestResultButton" style="Button">
                              <HorizontalPanel>
                                <AbsolutePanel style="RemoveRowButtonImage" />
                                <text><xsl:value-of select="resource:getString($constants,'removeRow')" /></text>
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
                    	<VerticalPanel height="7px" />
                        </VerticalPanel>
                        </tab>
                  <tab key="prepAndReflexTab" text="{resource:getString($constants,'prepAndReflex')}">
                    <VerticalPanel>
                      <VerticalPanel padding="0" spacing="0">
                        <VerticalPanel padding="0" spacing="0">
                          <HorizontalPanel>
                            <widget valign="top">
                              <table key="testPrepTable" width="auto" maxRows="9" showScroll="ALWAYS"  title="">
                                <col key="{prepTestMeta:getName($pt)}" width="527" header="{resource:getString($constants,'prepTestMethod')}">
                                  <autoComplete width="525" field="Integer" >
                                    <col width="525" />
                                  </autoComplete>
                                </col>
                                <col key="{testPrep:getIsOptional($tp)}" width ="70" header="{resource:getString($constants,'optional')}">
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
                          <HorizontalPanel>
                            <widget valign="top">
                              <table key="testReflexTable" width="auto" maxRows="10" showScroll="ALWAYS"  title="">
                                <col key="{addTestMeta:getName($at)}" width="150" header="{resource:getString($constants,'reflexiveTest')}">
                                  <autoComplete width="150px" case="MIXED" required="true">
                                    <col width="160" header="Name" />
                                  </autoComplete>
                                </col>
                                <col key="{testRefAna:getName($trefa)}" width="190" sort="false" header="{resource:getString($constants,'testAnalyte')}">
                                 <autoComplete width="181" case="MIXED" popWidth="auto" required="true">
                                   <col width="181"/>
                                 </autoComplete>
                                </col>
                                <col key="{testRefRes:getValue($trefr)}" width="140" header="{resource:getString($constants,'result')}">
                                 <autoComplete width="140" case="MIXED" popWidth="auto" required="true">
                                   <col width="140"/>
                                 </autoComplete>
                                </col>
                                <col key="{testRef:getFlagsId($tref)}" width="111" header="{resource:getString($constants,'flags')}">
                                  <dropdown width="200" case="MIXED" field="Integer" popWidth="300" required="true" />
                                </col>
                              </table>
                            </widget>
                          </HorizontalPanel>
                          <HorizontalPanel style="TableFooterPanel">
                            <widget halign="center">
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
                      <VerticalPanel height="16px" />
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
                              <dropdown key="{testWrksht:getFormatId($tws)}" width="145px" case="MIXED" tab="{testWrksht:getBatchCapacity($tws)}, {wsscript:getName($wss)}" field="Integer" />
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
                              <autoComplete key="{wsscript:getName($wss)}" width="235px"  field="Integer" >
                                <col width="235"/>
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
                            <table key="worksheetTable" width="auto" maxRows="7" showScroll="ALWAYS" tab="removeWSItemButton, {testWrksht:getScriptletId($tws)}" title="">
                              <col key="{testWrkshtItm:getPosition($twsi)}" width="63" header="{resource:getString($constants,'position')}">
                                <textbox field="Integer" />
                              </col>
                              <col key="{testWrkshtItm:getTypeId($twsi)}" width="150" header="{resource:getString($constants,'type')}">
                                <dropdown width="140" case="MIXED" field="Integer" required="true" />
                              </col>
                              <col key="{testWrkshtItm:getQcName($twsi)}" width="370" header="{resource:getString($constants,'qcName')}">
                                <autoComplete width="340" case="MIXED" field="String" >
                                  <col width="350" header="Name" />
                                </autoComplete>
                              </col>
                            </table>
                          </widget>
                        </HorizontalPanel>
                        <HorizontalPanel style="TableFooterPanel">
                          <widget halign="center">
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
                            <table key="worksheetAnalyteTable" width="auto" maxRows="6" showScroll="ALWAYS" tab="removeWSItemButton, {testWrksht:getScriptletId($tws)}" title="">
                              <col key="{testWrkshtAna:getAnalyteId($twsa)}" width="400" header="{resource:getString($constants,'analyte')}">
                                <label field="String" />
                              </col>                             
                              <col key="{testWrkshtAna:getRepeat($twsa)}" width="83" header="{resource:getString($constants,'repeat')}">
                                <textbox field="Integer" required = "true"/>
                              </col>
                              <col key="{testWrkshtAna:getFlagId($twsa)}" width="100" header="{resource:getString($constants,'flag')}">
                                <dropdown width="140" case="MIXED" field="Integer" />
                              </col>
                            </table>
                          </widget>                                               
                        </HorizontalPanel>
                        <HorizontalPanel style="TableFooterPanel">
                              <widget halign="left">
                                <appButton key="addWSAnalyteButton" style="Button">
                                  <HorizontalPanel>
                                    <AbsolutePanel style="AddRowButtonImage" />
                                    <text><xsl:value-of select='resource:getString($constants,"addAnalyte")'/></text>
                                  </HorizontalPanel>
                                </appButton>
                              </widget>
                              <widget halign="left">
                                <appButton key="removeWSAnalyteButton" style="Button">
                                  <HorizontalPanel>
                                    <AbsolutePanel style="RemoveRowButtonImage" />
                                    <text><xsl:value-of select='resource:getString($constants,"removeAnalyte")'/></text>
                                  </HorizontalPanel>
                                </appButton>
                              </widget>
                            </HorizontalPanel>    
                      </VerticalPanel>  
                      <VerticalPanel height = "4px"/>                    
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