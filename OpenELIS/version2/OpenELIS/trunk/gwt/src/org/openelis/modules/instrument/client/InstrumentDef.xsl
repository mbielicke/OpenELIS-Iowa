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
  xmlns:instLog="xalan://org.openelis.metamap.InstrumentLogMetaMap"
  xmlns:meta="xalan://org.openelis.metamap.InstrumentMetaMap"
  xmlns:script="xalan://org.openelis.meta.ScriptletMeta">

  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="inst" select="meta:new()" />
    <xsl:variable name="scpt" select="meta:getScriptlet($inst)" />
    <xsl:variable name="il" select="meta:getInstrumentLog($inst)" />
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" id="Instrument" name="{resource:getString($constants,'instrument')}">
      <HorizontalPanel padding="0" spacing="0">
<!--left table goes here -->
        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="225">
            <buttonGroup key="atozButtons">
              <xsl:call-template name="aToZLeftPanelButtons" />
            </buttonGroup>
            <VerticalPanel>
              <table key="atozTable" width="auto" maxRows="20" style="atozTable">
                <col width="75" header="{resource:getString($constants,'name')}">
                  <label />
                </col>
                <col width="100" header="{resource:getString($constants,'serialNumber')}">
                  <label />
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
              <menuPanel key="optionsMenu" layout="vertical" style="topBarItemHolder">
                <menuItem>
                  <menuDisplay>
                    <appButton style="ButtonPanelButton" action="option">
                      <HorizontalPanel>
                        <text>
                          <xsl:value-of select='resource:getString($constants,"options")' />
                        </text>
                        <AbsolutePanel width="20px" height="20px" style="OptionsButtonImage" />
                      </HorizontalPanel>
                    </appButton>
                  </menuDisplay>
                  <menuPanel layout="vertical" position="below" style="buttonMenuContainer">
                    <xsl:call-template name="historyMenuItem" />
                  </menuPanel>
                </menuItem>
              </menuPanel>
            </HorizontalPanel>
          </AbsolutePanel>
<!--end button panel-->
          <VerticalPanel width="620" height="235" padding="0" spacing="0" style="WhiteContentPanel">
            <TablePanel style="Form">
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'name')" />:
                </text>
                <widget>
                  <textbox key="{meta:getName($inst)}" width="145" case="LOWER" max="20" tab="{meta:getDescription($inst)},{meta:getActiveEnd($inst)}" required="true" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'description')" />:
                </text>
                <widget colspan="6">
                  <textbox key="{meta:getDescription($inst)}" width="425" max="60" tab="{meta:getModelNumber($inst)},{meta:getName($inst)}" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'modelNumber')" />:
                </text>
                <widget>
                  <textbox key="{meta:getModelNumber($inst)}" width="285" max="40" tab="{meta:getSerialNumber($inst)},{meta:getDescription($inst)}" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'serialNumber')" />:
                </text>
                <widget>
                  <textbox key="{meta:getSerialNumber($inst)}" width="285" max="40" tab="{meta:getTypeId($inst)},{meta:getModelNumber($inst)}" required="true" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"type")' />:
                </text>
                <dropdown key="{meta:getTypeId($inst)}" width="150" tab="{meta:getLocation($inst)},{meta:getSerialNumber($inst)}" required="true" />
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"active")' />:
                </text>
                <check key="{meta:getIsActive($inst)}" tab="{meta:getActiveBegin($inst)},{script:getName($scpt)}" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'location')" />:
                </text>
                <widget>
                  <textbox key="{meta:getLocation($inst)}" width="425" max="60" tab="{script:getName($scpt)},{meta:getTypeId($inst)}" required="true" />
                </widget>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"beginDate")' />:
                </text>
                <calendar key="{meta:getActiveBegin($inst)}" begin="0" end="2" width="90" pattern="{resource:getString($constants,'datePattern')}" tab="{meta:getActiveEnd($inst)},{meta:getIsActive($inst)}" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'scriptlet')" />:
                </text>
                <autoComplete key="{script:getName($scpt)}" width="180" case="LOWER" tab="{meta:getIsActive($inst)},{meta:getLocation($inst)}">
                  <col width="180" />
                </autoComplete>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"endDate")' />:
                </text>
                <calendar key="{meta:getActiveEnd($inst)}" begin="0" end="2" width="90" pattern="{resource:getString($constants,'datePattern')}" tab="{meta:getName($inst)},{meta:getActiveBegin($inst)}" />
              </row>
            </TablePanel>
            <widget valign="top">
              <table key="logTable" width="668" maxRows="9" showScroll="ALWAYS" style="ScreenTableWithSides" title="">
                <col key="{instLog:getTypeId($il)}" width="120" align="left" header="{resource:getString($constants,'type')}">
                  <dropdown width="155" field="Integer" required="true" />
                </col>
                <col key="{instLog:getWorksheetId($il)}" width="100" align="left" header="{resource:getString($constants,'worksheet')}">
                  <textbox field="Integer" />
                </col>
                <col key="{instLog:getEventBegin($il)}" width="140" align="left" header="{resource:getString($constants,'beginDate')}">
                  <calendar begin="0" end="4" pattern="{resource:getString($constants,'dateTimePattern')}" />
                </col>
                <col key="{instLog:getEventEnd($il)}" width="140" align="left" header="{resource:getString($constants,'endDate')}">
                  <calendar begin="0" end="4" pattern="{resource:getString($constants,'dateTimePattern')}" />
                </col>
                <col key="{instLog:getText($il)}" width="400" align="left" header="{resource:getString($constants,'note')}">
                  <textbox field="String" />
                </col>
              </table>
            </widget>
            <widget style="TableButtonFooter">
              <HorizontalPanel>
                <appButton key="addLogButton" style="Button">
                  <HorizontalPanel>
                    <AbsolutePanel style="AddRowButtonImage" />
                    <text>
                      <xsl:value-of select="resource:getString($constants,'addRow')" />
                    </text>
                  </HorizontalPanel>
                </appButton>
                <appButton key="removeLogButton" style="Button">
                  <HorizontalPanel>
                    <AbsolutePanel style="RemoveRowButtonImage" />
                    <text>
                      <xsl:value-of select="resource:getString($constants,'removeRow')" />
                    </text>
                  </HorizontalPanel>
                </appButton>
              </HorizontalPanel>
            </widget>
            <VerticalPanel height="8px" />
          </VerticalPanel>
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
