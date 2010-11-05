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
  xmlns:meta="xalan://org.openelis.meta.PanelMeta">

  <xsl:import href="IMPORT/aToZTwoColumns.xsl" />
  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource" />
  </xalan:component>
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale" />
  </xalan:component>
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" name="{resource:getString($constants,'panel')}">
      <HorizontalPanel padding="0" spacing="0">
<!--left table goes here -->
        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="225">
            <buttonGroup key="atozButtons">
              <xsl:call-template name="aToZLeftPanelButtons" />
            </buttonGroup>
            <VerticalPanel>
              <table key="atozTable" width="auto" rows="14" style="atozTable">
                <col width="175" header="{resource:getString($constants,'name')}">
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
              <xsl:call-template name="deleteButton"/>
              <xsl:call-template name="buttonPanelDivider" />
              <xsl:call-template name="commitButton"/>
              <xsl:call-template name="abortButton"/>
              <xsl:call-template name="buttonPanelDivider" />
                <menu selfShow="true" showBelow="true">
                  <menuDisplay>
                    <button style="ButtonPanelButton" action="option">
                      <Grid cols="2">
                        <row>
                          <cell text="{resource:getString($constants,'options')}" />
                          <cell style="OptionsButtonImage" />
                        </row>
                      </Grid>
                    </button>
                  </menuDisplay>
                    <menuItem key="panelHistory" description="" enabled="false" icon="historyIcon" display="{resource:getString($constants,'panelHistory')}" />
                    <menuItem key="panelItemHistory" description="" enabled="false" icon="historyIcon" display="{resource:getString($constants,'panelItemHistory')}" />
                </menu>
            </HorizontalPanel>
          </AbsolutePanel>
<!--end button panel-->
          <VerticalPanel width="650" height="235" padding="0" spacing="0" style="WhiteContentPanel">
            <TablePanel style="Form">
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'name')" />:
                </text>
                <textbox key="{meta:getName()}" width="145" case="LOWER" max="20" tab="{meta:getDescription()},{meta:getDescription()}" field="String" required="true" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'description')" />:
                </text>
                <textbox key="{meta:getDescription()}" width="425" max="60" tab="addedTestTable,{meta:getName()}" field="String" />
              </row>
            </TablePanel>
            <HorizontalPanel>
              <VerticalPanel style="Form">
                <widget valign="top">
                  <table key="panelItemTable" width="auto" rows="9" vscroll="ALWAYS" hscroll="ALWAYS" style="ScreenTableWithSides" tab="{meta:getName()},{meta:getDescription()}">
                    <col key="{meta:getItemTestName()}" width="135" align="left" header="{resource:getString($constants,'test')}">
                      <textbox field="String" />
                    </col>
                    <col key="{meta:getItemMethodName()}" width="135" align="left" header="{resource:getString($constants,'method')}">
                      <textbox field="String" />
                    </col>
                  </table>
                </widget>
                <HorizontalPanel>
                  <HorizontalPanel width="5" />
                  <widget style="TableButtonFooter">
                    <HorizontalPanel>
                      <button key="removeTestButton" icon="RemoveRowButtonImage" text="{resource:getString($constants,'removeRow')}" style="Button"/>
                      <button key="moveUpButton" icon="upButtonImage" text="{resource:getString($constants,'moveUp')}" style="Button"/>
                      <button key="moveDownButton" icon="downButtonImage" text="{resource:getString($constants,'moveDown')}" style="Button"/>
                    </HorizontalPanel>
                  </widget>
                </HorizontalPanel>
              </VerticalPanel>
              <HorizontalPanel width="10" />
              <widget valign="middle" style="WhiteContentPanel">
                <button key="addTestButton" text="{resource:getString($constants,'moveLeft')}" style="Button"/>
              </widget>
              <HorizontalPanel width="10" />
              <VerticalPanel style="Form">
                <table key="allTestsTable" rows="9" multiSelect="true" vscroll="ALWAYS" hscroll="ALWAYS" style="ScreenTableWithSides">
                  <col key="test" width="90" align="left" sort="true" header="{resource:getString($constants,'test')}">
                    <label field="String" />
                  </col>
                  <col key="method" width="90" align="left" sort="true" header="{resource:getString($constants,'method')}">
                    <label field="String" />
                  </col>
                  <col key="section" width="90" align="left" sort="true" header="{resource:getString($constants,'section')}">
                    <label field="String" />
                  </col>
                </table>
                <widget style="TableButtonFooter">
                  <HorizontalPanel>
                    <button key="refreshButton" icon="refreshButtonImage" text="{resource:getString($constants,'refresh')}" style="Button"/>
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
