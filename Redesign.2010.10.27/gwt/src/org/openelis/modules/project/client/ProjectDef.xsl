
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
  xmlns:meta="xalan://org.openelis.meta.ProjectMeta">

  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
<!-- main screen -->
    <screen id="Project" name="{resource:getString($constants,'project')}">
      <HorizontalPanel padding="0" spacing="0">
<!--left table goes here -->
        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="225">
            <buttonGroup key="atozButtons">
              <xsl:call-template name="aToZLeftPanelButtons" />
            </buttonGroup>
            <VerticalPanel>
              <table key="atozTable" width="auto" maxRows="18" style="atozTable">
                <col width="175" header="{resource:getString($constants,'name')}">
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
                        <AbsolutePanel width="20" height="20" style="OptionsButtonImage" />
                      </HorizontalPanel>
                    </appButton>
                  </menuDisplay>
                  <menuPanel layout="vertical" position="below" style="topMenuContainer">
                    <menuItem key="projectHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'projectHistory')}" />
                    <menuItem key="projectParameterHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'projectParameterHistory')}" />
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
                  <xsl:value-of select="resource:getString($constants,'id')" />:
                </text>
                <textbox key="{meta:getId()}" width="50" tab="{meta:getName()},parameterTable" field="Integer" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'name')" />:
                </text>
                <widget colspan="6">
                  <textbox key="{meta:getName()}" width="145" case="LOWER" max="20" tab="{meta:getDescription()},{meta:getId()}" field="String" required="true" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'description')" />:
                </text>
                <widget colspan="6">
                  <textbox key="{meta:getDescription()}" width="425" max="60" tab="{meta:getOwnerId()},{meta:getName()}" field="String" required="true" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'owner')" />:
                </text>
                <widget>
                  <autoComplete key="{meta:getOwnerId()}" width="145" case="LOWER" tab="{meta:getIsActive()},{meta:getDescription()}" field="Integer" required="true" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"active")' />:
                </text>
                <check key="{meta:getIsActive()}" tab="{meta:getStartedDate()},{meta:getOwnerId()}" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'referenceTo')" />:
                </text>
                <widget>
                  <textbox key="{meta:getReferenceTo()}" width="145" max="20" tab="{meta:getScriptletName()},{meta:getCompletedDate()}" field="String" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"beginDate")' />:
                </text>
                <calendar key="{meta:getStartedDate()}" begin="0" end="2" width="90" tab="{meta:getCompletedDate()},{meta:getIsActive()}" required="true" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'scriptlet')" />:
                </text>
                <autoComplete key="{meta:getScriptletName()}" width="180" case="LOWER" tab="parameterTable,{meta:getReferenceTo()}" field="Integer" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"endDate")' />:
                </text>
                <calendar key="{meta:getCompletedDate()}" begin="0" end="2" width="90" tab="{meta:getReferenceTo()},{meta:getStartedDate()}" required="true" />
              </row>
            </TablePanel>
            <VerticalPanel height="5" />
<!-- parameter table -->
            <HorizontalPanel width="609">
              <widget valign="top">
                <table key="parameterTable" width="590" maxRows="8" showScroll="ALWAYS" style="ScreenTableWithSides" tab="{meta:getId()},{meta:getScriptletName()}">
                  <col key="{meta:getProjectParameterParameter()}" width="325" header="{resource:getString($constants,'parameter')}">
                    <textbox field="String" required="true" />
                  </col>
                  <col key="{meta:getProjectParameterOperationId()}" width="80" header="{resource:getString($constants,'operation')}">
                    <dropdown width="80" field="Integer" required="true" />
                  </col>
                  <col key="{meta:getProjectParameterValue()}" width="400" header="{resource:getString($constants,'value')}">
                    <textbox field="String" required="true" />
                  </col>
                </table>
              </widget>
            </HorizontalPanel>
            <HorizontalPanel>
              <widget>
                <appButton key="addParameterButton" style="Button">
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
              <widget>
                <appButton key="removeParameterButton" style="Button">
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
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
