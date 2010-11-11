
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
    <screen name="{resource:getString($constants,'QC')}">
      <HorizontalPanel padding="0" spacing="0">
<!--left table goes here -->
        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="225">
            <buttonGroup key="atozButtons">
              <xsl:call-template name="aToZLeftPanelButtons" />
            </buttonGroup>
            <VerticalPanel>
              <table key="atozTable" rows="20">
                <col width="95" header="{resource:getString($constants,'name')}">
                  <label field="String" />
                </col>
                <col width="95" header="{resource:getString($constants,'lotNumber')}">
                  <label field="String" />
                </col>
              </table>
              <widget halign="center">
                <HorizontalPanel>
                  <button key="atozPrev" style="Button" enabled="false">
                    <AbsolutePanel style="PreviousButtonImage" />
                  </button>
                  <button key="atozNext" style="Button" enabled="false">
                    <AbsolutePanel style="NextButtonImage" />
                  </button>
                </HorizontalPanel>
              </widget>
            </VerticalPanel>
          </HorizontalPanel>
        </CollapsePanel>
<!--button panel code-->
        <VerticalPanel padding="0" spacing="0">
          <AbsolutePanel spacing="0" style="ButtonPanelContainer">
            <HorizontalPanel>
              <xsl:call-template name="queryButton"/>
              <xsl:call-template name="previousButton"/>
              <xsl:call-template name="nextButton"/>
              <xsl:call-template name="buttonPanelDivider" />
              <xsl:call-template name="addButton"/>
              <xsl:call-template name="updateButton"/>
              <xsl:call-template name="buttonPanelDivider" />
              <xsl:call-template name="commitButton"/>
              <xsl:call-template name="abortButton"/>
              <xsl:call-template name="buttonPanelDivider" />
              <menu key="optionsMenu" selfShow="true" showBelow="true">
                  <menuDisplay>
                    <button style="ButtonPanelButton" action="option">
                      <Grid cols="2">
                        <row>
                          <cell style="ScreenLabel,ButtonAdj" text="{resource:getString($constants,'options')}" />
                          <cell style="OptionsButtonImage" />
                        </row>
                      </Grid>
                    </button>
                  </menuDisplay>
                  <xsl:call-template name="duplicateRecordMenuItem" />
                  <menuItem key="qcHistory" enabled="false" icon="historyIcon" display="{resource:getString($constants,'qcHistory')}" />
                  <menuItem key="qcAnalyteHistory" enabled="false" icon="historyIcon" display="{resource:getString($constants,'qcAnalyteHistory')}" />
              </menu>
            </HorizontalPanel>
          </AbsolutePanel>
<!--end button panel-->
          <VerticalPanel width="620" height="235" padding="0" spacing="0" style="WhiteContentPanel">
            <HorizontalPanel padding="0" spacing="0">
              <TablePanel style="Form">
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'name')" />:
                  </text>
                  <widget colspan="6">
                    <textbox key="{meta:getName()}" width="215" case="LOWER" max="30" tab="{meta:getTypeId()},QcAnalyteTable" field="String" required="true" />
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'type')" />:
                  </text>
                  <dropdown key="{meta:getTypeId()}" width="100" tab="{meta:getInventoryItemName()},{meta:getName()}" field="Integer" />
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'inventoryItem')" />:
                  </text>
                  <widget>
                    <autoComplete key="{meta:getInventoryItemName()}" width="145" tab="{meta:getSource()},{meta:getTypeId()}" >
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
                    <textbox key="{meta:getSource()}" width="215" max="30" tab="{meta:getLotNumber()},{meta:getInventoryItemName()}" field="String" required="true" />
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'lotNumber')" />:
                  </text>
                  <widget colspan="6">
                    <textbox key="{meta:getLotNumber()}" width="215" max="30" tab="{meta:getIsActive()},{meta:getSource()}" field="String" case = "UPPER" required="true" />
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"active")' />:
                  </text>
                  <check key="{meta:getIsActive()}" tab="{meta:getPreparedDate()},{meta:getLotNumber()}" />
                </row>
              </TablePanel>
              <TablePanel style="Form">
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"preparedDate")' />:
                  </text>
                  <calendar key="{meta:getPreparedDate()}" begin="0" end="4" width="140" pattern="{resource:getString($constants,'dateTimePattern')}" tab="{meta:getPreparedVolume()},{meta:getIsActive()}" required="true" />
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'preparedVolume')" />:
                  </text>
                  <widget colspan="6">
                    <textbox key="{meta:getPreparedVolume()}" width="100" tab="{meta:getPreparedUnitId()},{meta:getPreparedDate()}" field="Double" />
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'preparedUnit')" />:
                  </text>
                  <dropdown key="{meta:getPreparedUnitId()}" width="150" tab="{meta:getPreparedById()},{meta:getPreparedVolume()}" field="Integer" />
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'preparedBy')" />:
                  </text>
                  <widget>
                    <autoComplete key="{meta:getPreparedById()}" width="145" tab="{meta:getUsableDate()},{meta:getPreparedUnitId()}">
                      <col width="145" />
                    </autoComplete>
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"usableDate")' />:
                  </text>
                  <calendar key="{meta:getUsableDate()}" begin="0" end="4" width="140" pattern="{resource:getString($constants,'dateTimePattern')}" tab="{meta:getExpireDate()},{meta:getPreparedById()}" required="true" />
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"expireDate")' />:
                  </text>
                  <calendar key="{meta:getExpireDate()}" begin="0" end="4" width="140" pattern="{resource:getString($constants,'dateTimePattern')}" tab="QcAnalyteTable,{meta:getUsableDate()}" required="true" />
                </row>
              </TablePanel>
            </HorizontalPanel>
            <VerticalPanel height="10" />
            <VerticalPanel>
              <table key="QcAnalyteTable" width="625" rows="10" vscroll="ALWAYS" hscroll="ALWAYS" style="ScreenTableWithSides" tab="{meta:getName()},{meta:getExpireDate()}">
                <col key="{meta:getQcAnalyteAnalyteName()}" width="270" align="left" header="{resource:getString($constants,'analyte')}">
                  <autoComplete width="270" required="true">
                    <col width="300" />
                  </autoComplete>
                </col>
                <col key="{meta:getQcAnalyteTypeId()}" width="55" align="left" header="{resource:getString($constants,'type')}">
                  <dropdown width="55" field="Integer" required="true" />
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
                  <button key="addAnalyteButton" icon="AddRowButtonImage" text="{resource:getString($constants,'addRow')}" style="Button"/>
                  <button key="removeAnalyteButton" icon="RemoveRowButtonImage" text="{resource:getString($constants,'removeRow')}" style="Button"/>
                  <button key="dictionaryButton" icon="DictionaryButtonImage" text="{resource:getString($constants,'dictionary')}" style="Button"/>
                </HorizontalPanel>
              </widget>
            </VerticalPanel>
          </VerticalPanel>
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
