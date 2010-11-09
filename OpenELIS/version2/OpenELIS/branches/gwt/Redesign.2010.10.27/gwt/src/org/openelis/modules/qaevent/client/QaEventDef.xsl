
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
  xmlns:meta="xalan://org.openelis.meta.QaEventMeta">

  <xsl:import href="IMPORT/aToZTwoColumns.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen name="{resource:getString($constants,'QAEvent')}">
      <HorizontalPanel padding="0" spacing="0">
<!--left table goes here -->
        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="270">
            <buttonGroup key="atozButtons">
              <xsl:call-template name="aToZLeftPanelButtons" />
            </buttonGroup>
            <VerticalPanel>
              <table key="atozTable" rows="14">
                <col width="120" header="{resource:getString($constants,'name')}">
                  <label field="String" />
                </col>
                <col width="120" header="{resource:getString($constants,'test')}">
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
                  <menuItem key="qaeventHistory" description="" enabled="false" icon="historyIcon" display="{resource:getString($constants,'qaeventHistory')}" />
                </menu>
            </HorizontalPanel>
          </AbsolutePanel>
<!--end button panel-->
          <VerticalPanel width="590" height="235" padding="0" spacing="0" style="WhiteContentPanel">
            <TablePanel style="Form">
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"name")' />:
                </text>
                <widget colspan = "5">
                  <textbox key="{meta:getName()}" width="145" case="LOWER" max="20" tab="{meta:getDescription()},{meta:getReportingText()}" field="String" required="true" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"description")' />:
                </text>
                <widget colspan = "5">
                  <textbox key="{meta:getDescription()}" width="425" max="60"  tab="{meta:getTypeId()},{meta:getName()}" field="String" required="true" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"type")' />:
                </text>
                <widget colspan = "5">
                  <dropdown key="{meta:getTypeId()}" width="120" tab="{meta:getTestName()},{meta:getDescription()}" field="Integer" required="true" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"test")' />:
                </text>
                <autoComplete key="{meta:getTestName()}" width="140" case="LOWER" tab="{meta:getIsBillable()},{meta:getTypeId()}">
                  <col width="100" header="{resource:getString($constants,'test')}" />
                  <col width="100" header="{resource:getString($constants,'method')}" />
                  <col width="250" header="{resource:getString($constants,'description')}" />
                </autoComplete>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"method")' />:
                </text>
                <textbox key="method" width="140" style="ScreenTextboxDisplayOnly" field="String" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"billable")' />:
                </text>
                <check key="{meta:getIsBillable()}" tab="{meta:getReportingSequence()},{meta:getTestName()}" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"sequence")' />:
                </text>
                <textbox key="{meta:getReportingSequence()}" width="40" tab="{meta:getReportingText()},{meta:getIsBillable()}" field="Integer" />
              </row>
              <row>
                <widget valign="top">
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"text")' />:
                  </text>
                </widget>
                <widget colspan = "5">
                  <textarea key="{meta:getReportingText()}" width="425" height="155" tab="{meta:getName()},{meta:getReportingSequence()}" />
                </widget>
              </row>
            </TablePanel>
          </VerticalPanel>
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
