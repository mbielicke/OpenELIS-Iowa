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
  xmlns:meta="xalan://org.openelis.meta.CategoryMeta">

  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" name="{resource:getString($constants,'dictionary')}">
      <HorizontalPanel padding="0" spacing="0">
        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="225">
            <buttonGroup key="atozButtons">
              <xsl:call-template name="aToZLeftPanelButtons" />
            </buttonGroup>
            <VerticalPanel>
              <table key="atozTable" rows="20" style="atozTable">
                <col width="175" header="{resource:getString($constants,'catName')}">
                  <label field="String" />
                </col>
              </table>
              <widget halign="center">
                <HorizontalPanel>
                  <button key="atozPrev" style="Button" enabled="false">
                    <AbsolutePanel style="prevNavIndex" />
                  </button>
                  <button key="atozNext" style="Button" enabled="false">
                    <AbsolutePanel style="nextNavIndex" />
                  </button>
                </HorizontalPanel>
              </widget>
            </VerticalPanel>
          </HorizontalPanel>
        </CollapsePanel>
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
                <menu>
                  <menuDisplay>
                    <button style="ButtonPanelButton" action="option">
                      <HorizontalPanel>
                        <text>
                          <xsl:value-of select='resource:getString($constants,"options")' />
                        </text>
                        <AbsolutePanel width="20" height="20" style="OptionsButtonImage" />
                      </HorizontalPanel>
                    </button>
                  </menuDisplay>
                  <menuItem key="categoryHistory" enabled="false" icon="historyIcon" display="{resource:getString($constants,'categoryHistory')}" />
                  <menuItem key="dictionaryHistory" enabled="false" icon="historyIcon" display="{resource:getString($constants,'dictionaryHistory')}" />
                </menu>
            </HorizontalPanel>
          </AbsolutePanel>
<!--end button panel-->
          <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
            <TablePanel style="Form">
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"catName")' />:
                </text>
                <widget colspan="5">
                  <textbox key="{meta:getName()}" width="355" case="MIXED" max="50" tab="{meta:getDescription()},{meta:getSystemName()}" field="String" required="true" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"description")' />:
                </text>
                <widget colspan="3">
                  <textbox key="{meta:getDescription()}" width="425" case="MIXED" max="60" tab="{meta:getSectionId()},{meta:getName()}" field="String" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"section")' />:
                </text>
                <dropdown key="{meta:getSectionId()}" width="100" case="LOWER" tab="{meta:getSystemName()},{meta:getDescription()}" field="Integer" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"systemName")' />:
                </text>
                <textbox key="{meta:getSystemName()}" width="215" case="MIXED" max="30" tab="{meta:getIsSystem()},{meta:getSectionId()}" field="String" required="true" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"system")' />:
                </text>
                <check key="{meta:getIsSystem()}" tab="dictEntTable,{meta:getSystemName()}" />
              </row>
            </TablePanel>
            <VerticalPanel>
              <widget valign="top">
                <table key="dictEntTable" rows="12" vscroll="ALWAYS" hscroll="ALWAYS" style="ScreenTableWithSides" tab="{meta:getName()},{meta:getSystemName()}" >
                  <col key="{meta:getDictionaryIsActive()}" width="60" header="{resource:getString($constants,'active')}">
                    <check>Y</check>
                  </col>
                  <col key="{meta:getDictionarySystemName()}" width="120" header="{resource:getString($constants,'systemName')}">
                    <textbox case="LOWER" max="30" field="String" />
                  </col>
                  <col key="{meta:getDictionaryLocalAbbrev()}" width="120" header="{resource:getString($constants,'abbr')}">
                    <textbox max="10" field="String" />
                  </col>
                  <col key="{meta:getDictionaryEntry()}" width="180" sort="true" header="{resource:getString($constants,'entry')}">
                    <textbox max="255" field="String" required="true" />
                  </col>
                  <col key="{meta:getDictionaryRelatedEntryEntry()}" width="150" header="{resource:getString($constants,'relEntry')}">
                    <autoComplete width="130">
                      <col width="200" />
                    </autoComplete>
                  </col>
                </table>
              </widget>
              <HorizontalPanel style="TableFooterPanel">
                <widget halign="center">
                  <button key="addEntryButton" icon="AddRowButtonImage" text="{resource:getString($constants,'addRow')}" style="Button"/>
                </widget>
                <widget halign="center">
                  <button key="removeEntryButton" icon="RemoveRowButtonImage" text="{resource:getString($constants,'removeRow')}" style="Button"/>
                </widget>
              </HorizontalPanel>
            </VerticalPanel>
          </VerticalPanel>
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
