
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
  xmlns:meta="xalan://org.openelis.meta.QcMeta">

  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
<!-- main screen -->
    <screen id="Qc" name="{resource:getString($constants,'QC')}">
      <HorizontalPanel padding="0" spacing="0">
<!--left table goes here -->
        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="175">
            <buttonGroup key="atozButtons">
              <xsl:call-template name="aToZLeftPanelButtons" />
            </buttonGroup>
            <VerticalPanel>
              <table key="atozTable" width="auto" maxRows="20" style="atozTable">
                <col width="145" header="{resource:getString($constants,'name')}">
                  <label field="String" />
                </col>
              </table>
              <widget halign="center">
                <HorizontalPanel>
                  <appButton key="atozPrev" style="Button" enable="false">
                    <AbsolutePanel style="prevNavIndex" />
                  </appButton>
                  <appButton key="atozNext" style="Button" enable="false">
                    <AbsolutePanel style="nextNavIndex" />
                  </appButton>
                </HorizontalPanel>
              </widget>
            </VerticalPanel>
          </HorizontalPanel>
        </CollapsePanel>
<!--button panel code-->
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
                  <menuPanel layout="vertical" position="below" style="topMenuContainer">
                    <xsl:call-template name="duplicateRecordMenuItem" />
                    <menuItem key="qcHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'qcHistory')}" />
                    <menuItem key="qcAnalyteHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'qcAnalyteHistory')}" />
                    <menuItem key="qcLotHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'qcLotHistory')}" />
                  </menuPanel>
                </menuItem>
              </menuPanel>
            </HorizontalPanel>
          </AbsolutePanel>
<!--end button panel-->
          <VerticalPanel width="620" height="235" padding="0" spacing="0" style="WhiteContentPanel">
            <HorizontalPanel padding="0" spacing="0">
              <TablePanel style="Form">
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'id')" />:
                  </text>
                  <widget colspan="6">
                    <textbox key="{meta:getId()}" width="75" tab="{meta:getName()},tabPanel" field="Integer" />
                  </widget>
                </row> 
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'name')" />:
                  </text>
                  <widget colspan="6">
                    <textbox key="{meta:getName()}" width="215" case="LOWER" max="30" tab="{meta:getTypeId()},{meta:getId()}" field="String" required="true" />
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'type')" />:
                  </text>
                  <dropdown key="{meta:getTypeId()}" width="100" tab="{meta:getInventoryItemName()},{meta:getName()}" field="Integer" />
                </row>
              </TablePanel>
              <TablePanel style="Form">
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'inventoryItem')" />:
                  </text>
                  <widget>
                    <autoComplete key="{meta:getInventoryItemName()}" width="145" tab="{meta:getSource()},{meta:getTypeId()}" field="Integer">
                      <col width="135" header="{resource:getString($constants,'name')}" />
                      <col width="110" header="{resource:getString($constants,'store')}" />
                    </autoComplete>
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'source')" />:
                  </text>
                  <widget colspan="6">
                    <textbox key="{meta:getSource()}" width="215" max="30" tab="{meta:getIsActive()},{meta:getInventoryItemName()}" field="String" required="true" />
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"active")' />:
                  </text>
                  <check key="{meta:getIsActive()}" tab="tabPanel,{meta:getSource()}" />
                </row>
              </TablePanel>
            </HorizontalPanel>
            <VerticalPanel height="5" />
            <TabPanel key="tabPanel" width="660">
              <tab key="analyteTab" tab ="qcAnalyteTable,qcAnalyteTable" text="{resource:getString($constants,'analyte')}">
                <VerticalPanel>
                  <table key="qcAnalyteTable" width="640" maxRows="13" showScroll="ALWAYS" style="ScreenTableWithSides" tab="{meta:getId()},{meta:getIsActive()}">
                    <col key="{meta:getQcAnalyteAnalyteName()}" width="270" align="left" header="{resource:getString($constants,'analyte')}">
                      <autoComplete width="270" popWidth="310" field="Integer" required="true">
                        <col width="300" />
                      </autoComplete>
                    </col>
                    <col key="{meta:getQcAnalyteTypeId()}" width="55" align="left" header="{resource:getString($constants,'type')}">
                      <dropdown width="90" field="Integer" required="true" />
                    </col>
                    <col key="{meta:getQcAnalyteIsTrendable()}" width="55" align="center" header="{resource:getString($constants,'trendable')}">
                      <check />
                    </col>
                    <col key="{meta:getQcAnalyteValue()}" width="245" align="left" header="{resource:getString($constants,'expectedValue')}">
                      <textbox max="80" field="String" />
                    </col>
                  </table>
                  <widget style="TableButtonFooter">
                    <HorizontalPanel>
                      <appButton key="addAnalyteButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="AddRowButtonImage" />
                          <text>
                            <xsl:value-of select="resource:getString($constants,'addRow')" />
                          </text>
                        </HorizontalPanel>
                      </appButton>
                      <appButton key="removeAnalyteButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="RemoveRowButtonImage" />
                          <text>
                            <xsl:value-of select="resource:getString($constants,'removeRow')" />
                          </text>
                        </HorizontalPanel>
                      </appButton>
                      <appButton key="dictionaryButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="DictionaryButtonImage" />
                          <text>
                            <xsl:value-of select='resource:getString($constants,"dictionary")' />
                          </text>
                        </HorizontalPanel>
                      </appButton>
                    </HorizontalPanel>
                  </widget>
                </VerticalPanel>
              </tab>
              <tab key="lotTab" tab = "qcLotTable,qcLotTable" text="{resource:getString($constants,'lotInformation')}">
                <VerticalPanel>
                  <table key="qcLotTable" width="640" maxRows="13" showScroll="ALWAYS" style="ScreenTableWithSides" tab="{meta:getId()},{meta:getIsActive()}">
                    <col key="{meta:getQcLotIsActive()}" width="40" align="center" header="{resource:getString($constants,'active')}">
                      <check/>
                    </col>
                    <col key="{meta:getQcLotLotNumber()}" width="210" align="left" header="{resource:getString($constants,'lotNumber')}">
                      <textbox field="String" case = "UPPER" max = "30" required="true" />
                    </col>
                    <col key="{meta:getQcLotLocationId()}" width="60" align="left" header="{resource:getString($constants,'location')}">
                      <dropdown width="100" field="Integer" required="true" />
                    </col>
                    <col key="{meta:getQcLotPreparedDate()}" width="105" align="left"  header="{resource:getString($constants,'preparedDate')}">
                      <calendar begin="0" end="4" pattern="{resource:getString($constants,'dateTimePattern')}" />
                    </col>
                    <col key="{meta:getQcLotUsableDate()}" width="105" align="left" header="{resource:getString($constants,'usableDate')}">
                      <calendar begin="0" end="4" width="105" pattern="{resource:getString($constants,'dateTimePattern')}" required="true" />
                    </col>
                    <col key="{meta:getQcLotExpireDate()}" width="105" align="left" header="{resource:getString($constants,'expireDate')}">
                      <calendar begin="0" end="4" width="105" pattern="{resource:getString($constants,'dateTimePattern')}" required="true" />
                    </col>
                    <col key="{meta:getQcLotPreparedVolume()}" width="105" align="right"  header="{resource:getString($constants,'preparedVolume')}">
                      <textbox field="Double"/>
                    </col>
                    <col key="{meta:getQcLotPreparedUnitId()}" width="100" align="right" header="{resource:getString($constants,'preparedUnit')}">
                      <dropdown width="150" field="Integer" required="true" />
                    </col>
                    <col key="{meta:getQcLotPreparedById()}" width="145" align="left" header="{resource:getString($constants,'preparedBy')}">
                      <autoComplete width="145" field="Integer">
                        <col width="145" />
                      </autoComplete>
                    </col>                    
                  </table>
                  <widget style="TableButtonFooter">
                    <HorizontalPanel>
                      <appButton key="addLotButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="AddRowButtonImage" />
                          <text>
                            <xsl:value-of select="resource:getString($constants,'addRow')" />
                          </text>
                        </HorizontalPanel>
                      </appButton>
                      <appButton key="removeLotButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="RemoveRowButtonImage" />
                          <text>
                            <xsl:value-of select="resource:getString($constants,'removeRow')" />
                          </text>
                        </HorizontalPanel>
                      </appButton>
                    </HorizontalPanel>
                  </widget>
                </VerticalPanel>
              </tab>
            </TabPanel>
          </VerticalPanel>
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>