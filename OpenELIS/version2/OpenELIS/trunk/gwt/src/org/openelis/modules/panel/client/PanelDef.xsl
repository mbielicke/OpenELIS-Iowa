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
    <screen xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" id="Panel" name="{resource:getString($constants,'panel')}">
      <HorizontalPanel padding="0" spacing="0">
<!--left table goes here -->
        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="225">
            <buttonGroup key="atozButtons">
              <xsl:call-template name="aToZLeftPanelButtons" />
            </buttonGroup>
            <VerticalPanel>
              <table key="atozTable" width="auto" maxRows="14" style="atozTable">
                <col width="175" header="{resource:getString($constants,'name')}">
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
                        <AbsolutePanel width="20px" height="20px" style="OptionsButtonImage" />
                      </HorizontalPanel>
                    </appButton>
                  </menuDisplay>
                  <menuPanel layout="vertical" position="below" style="topMenuContainer">
                    <menuItem description="" icon = "historyIcon" key="panelHistory" label="{resource:getString($constants,'panelHistory')}" />
                    <menuItem description="" icon = "historyIcon" key="panelItemHistory" label="{resource:getString($constants,'panelItemHistory')}" />
                  </menuPanel>
                </menuItem>
              </menuPanel>
            </HorizontalPanel>
          </AbsolutePanel>
<!--end button panel-->
          <VerticalPanel width="650" height="235" padding="0" spacing="0" style="WhiteContentPanel">
            <TablePanel style="Form">
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'name')" />:
                </text>
                <textbox key="{meta:getName()}" case = "LOWER" width="145" max="20" tab="{meta:getDescription()},{meta:getDescription()}" required="true" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'description')" />:
                </text>
                <textbox key="{meta:getDescription()}" width="425" max="60" tab="addedTestTable,{meta:getName()}" />
              </row>
            </TablePanel>
            <HorizontalPanel>
              <VerticalPanel style="Form">
                <widget valign="top">
                  <table key="panelItemTable" width="auto" maxRows="9" showScroll="ALWAYS" style="ScreenTableWithSides" tab="{meta:getName()},{meta:getDescription()}" title="">
                    <col key="{meta:getItemTestName()}" width="135" align="left" sort="false" header="{resource:getString($constants,'test')}">
                      <textbox field="String" />
                    </col>
                    <col key="{meta:getItemMethodName()}" width="135" align="left" sort="false" header="{resource:getString($constants,'method')}">
                      <textbox field="String" />
                    </col>
                  </table>
                </widget>
                <HorizontalPanel>
                  <HorizontalPanel width="5px" />
                  <widget style="TableButtonFooter">
                    <HorizontalPanel>
                      <appButton key="removeTestButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="RemoveRowButtonImage" />
                          <text>
                            <xsl:value-of select="resource:getString($constants,'removeRow')" />
                          </text>
                        </HorizontalPanel>
                      </appButton>
                      <appButton key="moveUpButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="upButtonImage"/>
                          <text>
                            <xsl:value-of select="resource:getString($constants,'moveUp')" />
                          </text>
                        </HorizontalPanel>
                      </appButton>
                      <appButton key="moveDownButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="downButtonImage"/>
                          <text>
                            <xsl:value-of select="resource:getString($constants,'moveDown')" />
                          </text>
                        </HorizontalPanel>
                      </appButton>
                    </HorizontalPanel>
                  </widget>
                </HorizontalPanel>
              </VerticalPanel>
              <HorizontalPanel width="10px" />
              <widget valign="middle" style="WhiteContentPanel">
                <appButton key="addTestButton" style="Button">
                  <HorizontalPanel>
                    <AbsolutePanel />
                    <text>
                      <xsl:value-of select="resource:getString($constants,'moveLeft')" />
                    </text>
                  </HorizontalPanel>
                </appButton>
              </widget>
              <HorizontalPanel width="10px" />
              <VerticalPanel style="Form">
                <table key="allTestsTable" width="auto" maxRows="9" multiSelect="true" showScroll="ALWAYS" style="ScreenTableWithSides" title="">
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
                    <appButton key="refreshButton" style="Button">
                      <HorizontalPanel>
                        <AbsolutePanel style="refreshButtonImage"/>
                        <text>
                          <xsl:value-of select="resource:getString($constants,'refresh')" />
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
