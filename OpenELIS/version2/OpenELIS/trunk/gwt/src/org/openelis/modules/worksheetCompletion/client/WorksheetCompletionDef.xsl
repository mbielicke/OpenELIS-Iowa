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
  xmlns:meta="xalan://org.openelis.meta.WorksheetCompletionMeta">

  <xsl:import href="IMPORT/button.xsl" />
  <xsl:import href="OPENELIS/org/openelis/modules/note/client/InternalNoteTabDef.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="WorksheetCompletion" name="{resource:getString($constants,'worksheetCompletion')}">
      <VerticalPanel padding="0" spacing="0">
        <AbsolutePanel spacing="0" style="ButtonPanelContainer">
          <HorizontalPanel>
            <xsl:call-template name="printButton">
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
          </HorizontalPanel>
        </AbsolutePanel>
        <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
          <TablePanel style="Form">
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'worksheetNumber')" />:
              </text>
              <textbox key="{meta:getId()}" width="100" case="LOWER" field="Integer"/>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'status')" />:
              </text>
              <dropdown key="{meta:getStatusId()}" width="100" popWidth="100" tab="lookupWorksheetButton,tabPanel" field="Integer" />
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'relatedWorksheetNumber')" />:
              </text>
              <HorizontalPanel>
                <textbox key="{meta:getRelatedWorksheetId()}" width="100" case="LOWER" field="Integer" />
                <appButton key="lookupWorksheetButton" style="LookupButton" tab="instrumentId,{meta:getStatusId()}" action="lookupWorksheet">
                  <AbsolutePanel style="LookupButtonImage" />
                </appButton>
              </HorizontalPanel>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'instrumentName')" />:
              </text>
              <widget colspan="5">
                <autoComplete key="instrumentId" width="150" case="LOWER" popWidth="auto" tab="defaultUser,lookupWorksheetButton" field="Integer">
                  <col width="150" header="Name" />
                  <col width="200" header="Description" />
                  <col width="75" header="Type">
                    <dropdown width="75" popWidth="auto" field="Integer"/>
                  </col>
                  <col width="200" header="Location" />
                </autoComplete>
              </widget>
            </row>
          </TablePanel>
          <HorizontalPanel>
            <appButton key="editWorksheetButton" style="Button">
              <HorizontalPanel>
                <AbsolutePanel style="EditMultipleButtonImage" />
                <text>
                  <xsl:value-of select="resource:getString($constants,'editWorksheet')" />
                </text>
              </HorizontalPanel>
            </appButton>
            <appButton key="loadFromEditButton" style="Button">
              <HorizontalPanel>
                <AbsolutePanel style="LoadButtonImage" />
                <text>
                  <xsl:value-of select="resource:getString($constants,'loadFromEditFile')" />
                </text>
              </HorizontalPanel>
            </appButton>
            <appButton key="loadFilePopupButton" style="Button">
              <HorizontalPanel>
                <AbsolutePanel style="LoadButtonImage" />
                <text>
                  <xsl:value-of select="resource:getString($constants,'loadFromInstrumentFile')" />
                </text>
              </HorizontalPanel>
            </appButton>
          </HorizontalPanel>
<!-- TAB PANEL -->
          <TabPanel key="tabPanel" width="850" height="285">
<!-- TAB 1 -->
            <tab key="worksheetItemTab" tab="worksheetItemTable,worksheetItemTable" text="{resource:getString($constants,'worksheet')}">
              <table key="worksheetItemTable" width="832" maxRows="11" showScroll="ALWAYS" tab="{meta:getId()},{meta:getId()}">
                <col width="50" header="A">
                  <label field="String" />
                </col>
                <col width="90" header="B">
                  <label field="String" />
                </col>
                <col width="110" header="C">
                  <label field="String" />
                </col>
                <col width="90" header="D">
                  <label field="String" />
                </col>
                <col width="100" header="E">
                  <label field="String" />
                </col>
                <col width="100" header="F">
                  <label field="String" />
                </col>
                <col width="90" header="G">
                  <label field="String" />
                </col>
                <col width="100" header="H">
                  <label field="String" />
                </col>
                <col width="100" header="I">
                  <label field="String" />
                </col>
                <col width="100" header="J">
                  <label field="String" />
                </col>
                <col width="100" header="K">
                  <label field="String" />
                </col>
                <col width="100" header="L">
                  <label field="String" />
                </col>
                <col width="100" header="M">
                  <label field="String" />
                </col>
                <col width="100" header="N">
                  <label field="String" />
                </col>
                <col width="100" header="O">
                  <label field="String" />
                </col>
                <col width="100" header="P">
                  <label field="String" />
                </col>
                <col width="100" header="Q">
                  <label field="String" />
                </col>
                <col width="100" header="R">
                  <label field="String" />
                </col>
                <col width="100" header="S">
                  <label field="String" />
                </col>
                <col width="100" header="T">
                  <label field="String" />
                </col>
                <col width="100" header="U">
                  <label field="String" />
                </col>
                <col width="100" header="V">
                  <label field="String" />
                </col>
                <col width="100" header="W">
                  <label field="String" />
                </col>
                <col width="100" header="X">
                  <label field="String" />
                </col>
                <col width="100" header="Y">
                  <label field="String" />
                </col>
                <col width="100" header="Z">
                  <label field="String" />
                </col>
                <col width="100" header="AA">
                  <label field="String" />
                </col>
                <col width="100" header="AB">
                  <label field="String" />
                </col>
                <col width="100" header="AC">
                  <label field="String" />
                </col>
                <col width="100" header="AD">
                  <label field="String" />
                </col>
                <col width="100" header="AE">
                  <label field="String" />
                </col>
                <col width="100" header="AF">
                  <label field="String" />
                </col>
                <col width="100" header="AG">
                  <label field="String" />
                </col>
                <col width="100" header="AH">
                  <label field="String" />
                </col>
                <col width="100" header="AI">
                  <label field="String" />
                </col>
                <col width="100" header="AJ">
                  <label field="String" />
                </col>
                <col width="100" header="AK">
                  <label field="String" />
                </col>
                <col width="100" header="AL">
                  <label field="String" />
                </col>
                <col width="100" header="AM">
                  <label field="String" />
                </col>
              </table>
            </tab>
<!-- TAB 2 -->
            <tab key="notesTab" tab="standardNoteButton,standardNoteButton" text="{resource:getString($constants,'note')}">
              <xsl:call-template name="InternalNoteTab">
          	    <xsl:with-param name="width">850</xsl:with-param>
          		  <xsl:with-param name="height">247</xsl:with-param>
              </xsl:call-template>
            </tab>
          </TabPanel>
        </VerticalPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
