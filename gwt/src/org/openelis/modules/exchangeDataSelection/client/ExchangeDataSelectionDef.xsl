
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
  xmlns:meta="xalan://org.openelis.meta.ExchangeCriteriaMeta"
  xmlns:resource="xalan://org.openelis.util.UTFResource"
  xmlns:sampleMeta="xalan://org.openelis.meta.SampleMeta"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:noNamespaceSchemaLocation="http://openelis.uhl.uiowa.edu/schema/ScreenSchema.xsd"
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd">

  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
<!-- main screen -->
    <screen id="ExchangeDataSelection" name="{resource:getString($constants,'exchangeDataSelection')}">
      <HorizontalPanel padding="0" spacing="0">
        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="180">
            <buttonGroup key="atozButtons">
              <xsl:call-template name="aToZLeftPanelButtons" />
            </buttonGroup>
            <VerticalPanel>
              <table key="atozTable" maxRows="22" style="atozTable" width="auto">
                <col header="{resource:getString($constants,'name')}" key="{meta:getId()}" width="175">
                  <label field="String" />
                </col>
              </table>
              <widget halign="center">
                <HorizontalPanel>
                  <appButton enable="false" key="atozPrev" style="Button">
                    <AbsolutePanel style="prevNavIndex" />
                  </appButton>
                  <appButton enable="false" key="atozNext" style="Button">
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
              <xsl:call-template name="deleteButton">
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
                  <menuPanel layout="vertical" position="below" style="topMenuContainer">
                    <xsl:call-template name="duplicateRecordMenuItem" />
                    <html>&lt;hr/&gt;</html>
                    <menuItem key="exchangeCriteriaHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'exchangeCriteriaHistory')}" />
                    <menuItem key="exchangeProfileHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'exchangeProfileHistory')}" />
                  </menuPanel>
                </menuItem>
              </menuPanel>
            </HorizontalPanel>
          </AbsolutePanel>
          <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
           <HorizontalPanel>
           <HorizontalPanel width = "2"/>
            <TablePanel>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"name")' />:
                </text>
                <textbox case="LOWER" field="String" key="{meta:getName()}" required="true" tab="{meta:getEnvironmentId()},{sampleMeta:getAnalysisResultTestResultFlagsId()}" width="300" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"environment")' />:
                </text>
                <dropdown field="Integer" key="{meta:getEnvironmentId()}" required="true" tab="{meta:getDestinationUri()},{meta:getName()}" width="100" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"destinationUri")' />:
                </text>
                <textbox field="String" key="{meta:getDestinationUri()}" required="true" tab="{meta:getIsAllAnalysesIncluded()},{meta:getEnvironmentId()}" width="555" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"includeAllAnalyses")' />:
                </text>
                <check key="{meta:getIsAllAnalysesIncluded()}" tab="lastRunDate, {meta:getDestinationUri()}" />
              </row>
            </TablePanel>
            </HorizontalPanel>
            <HorizontalPanel>
              <VerticalPanel>
                <VerticalPanel height="4" />
                <table key="profileTable" maxRows="10" showScroll="ALWAYS" style="ScreenTableWithSides" tab="{sampleMeta:getAnalysisReleasedDate()},lastRunDate" title="" width="auto">
                  <col align="left" header="{resource:getString($constants,'priorityProfile')}" key="{meta:getProfileProfileId()}" width="102">
                    <dropdown field="Integer" required="true" width="102" />
                  </col>
                </table>
                <HorizontalPanel>
                  <appButton key="addProfileButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style="AddButtonIcon" />
                    </HorizontalPanel>
                  </appButton>
                  <appButton key="removeProfileButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style="DeleteButtonIcon" />
                    </HorizontalPanel>
                  </appButton>
                </HorizontalPanel>
              </VerticalPanel>
              <HorizontalPanel width="5" />
              <VerticalPanel>
                <VerticalPanel padding="0" spacing="0" style="subform">
                  <text style="FormTitle">
                    <xsl:value-of select='resource:getString($constants,"sampleSelectionCriteria")' />
                  </text>
                  <ScrollPanel height="250px" key="queryBuilder" width="550px">
                    <TablePanel>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"released")' />:
                        </text>
                        <calendar begin="0" end="4" key="{sampleMeta:getAnalysisReleasedDate()}" pattern="{resource:getString($constants,'dateTimePattern')}" tab="{sampleMeta:getDomain()},profileTable" width="250" />
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"domain")' />:
                        </text>
                        <dropdown field="String" key="{sampleMeta:getDomain()}" tab="{sampleMeta:getAnalysisTestId()},{sampleMeta:getAnalysisReleasedDate()}" width="120" />
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"test")' />:
                        </text>
                        <dropdown case="LOWER" field="Integer" key="{sampleMeta:getAnalysisTestId()}" tab="{sampleMeta:getOrgId()},{sampleMeta:getDomain()}" width="250" />
                      </row>
                      <row>
                        <widget valign="top">
                          <text style="Prompt">
                            <xsl:value-of select='resource:getString($constants,"reportTo")' />:
                          </text>  
                        </widget>
                        <widget colspan="3">
                         <HorizontalPanel>
                           <table key="reportToTable" maxRows="5" showScroll="ALWAYS" style="ScreenTableWithSides" tab="{sampleMeta:getOrgParamValue()},{sampleMeta:getAnalysisTestId()}" width="auto">
                             <col header="" width="227">
                               <autoComplete case="UPPER" field="Integer" key="{sampleMeta:getOrgId()}" popWidth="auto" required = "true" width="227">
                                <col header="{resource:getString($constants,'name')}" width="180" />
                                <col header="{resource:getString($constants,'street')}" width="110" />
                                <col header="{resource:getString($constants,'city')}" width="100" />
                                <col header="{resource:getString($constants,'st')}" width="20" />
                              </autoComplete>
                            </col>
                          </table>
                          <VerticalPanel>
                          <appButton key="addReportToButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style="AddRowButtonImage" />
                      <text>
                        <xsl:value-of select="resource:getString($constants,'addRow')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                  <appButton key="removeReportToButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style="RemoveRowButtonImage" />
                      <text>
                        <xsl:value-of select="resource:getString($constants,'removeRow')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                          </VerticalPanel>
                        </HorizontalPanel>
                       </widget>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'profileVersion')" />:
                        </text>
                        <dropdown field="String" key="{sampleMeta:getOrgParamValue()}" popWidth="auto" tab="{sampleMeta:getAnalysisResultTestResultFlagsId()},{sampleMeta:getOrgId()}" width="120" />
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'testResultFlags')" />:
                        </text>
                        <dropdown field="Integer" key="{sampleMeta:getAnalysisResultTestResultFlagsId()}" popWidth="auto" tab="{meta:getName()},{sampleMeta:getOrgParamValue()}" width="120" />
                      </row>
                    </TablePanel>
                  </ScrollPanel>
                </VerticalPanel>
              </VerticalPanel>
              <HorizontalPanel width="3" />
              <widget>
                <HorizontalPanel>
                  <appButton key="searchButton" style="Button">
                    <HorizontalPanel>
                      <text>
                        <xsl:value-of select="resource:getString($constants,'search')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                </HorizontalPanel>
              </widget>
            </HorizontalPanel>
            <VerticalPanel height="2" />
            <HorizontalPanel>
              <VerticalPanel>
                <VerticalPanel height="2" />
                <table key="lastRunTable" maxRows="2" showScroll="ALWAYS" style="ScreenTableWithSides" tab = "queryResults,{sampleMeta:getAnalysisResultTestResultFlagsId()}" width="auto">
                  <col header="{resource:getString($constants,'lastRun')}" width="102">
                    <calendar begin="0" end="4" key="lastRunDate" pattern="{resource:getString($constants,'dateTimePattern')}" />
                  </col>
                </table>
                <HorizontalPanel>
                  <widget style="TableButtonFooter">
                    <HorizontalPanel>
                      <appButton enable="false" key="lastRunPrevButton" style="Button">
                        <AbsolutePanel style="prevNavIndex" />
                      </appButton>
                    </HorizontalPanel>
                  </widget>
                  <widget style="TableButtonFooter">
                    <HorizontalPanel>
                      <appButton enable="false" key="lastRunNextButton" style="Button">
                        <AbsolutePanel style="nextNavIndex" />
                      </appButton>
                    </HorizontalPanel>
                  </widget>
                </HorizontalPanel>
              </VerticalPanel>
              <HorizontalPanel width="9" />
              <textarea height="105" key="queryResults" tab = "{meta:getName()},lastRunTable" width="552" />
              <HorizontalPanel width="5" />
              <VerticalPanel>
                <widget style="TableButtonFooter">
                  <HorizontalPanel>
                    <appButton key="exportToLocationButton" style="Button">
                      <HorizontalPanel>
                        <text>
                          <xsl:value-of select="resource:getString($constants,'exportToLocation')" />
                        </text>
                      </HorizontalPanel>
                    </appButton>
                  </HorizontalPanel>
                </widget>
              </VerticalPanel>
            </HorizontalPanel>
          </VerticalPanel>
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>