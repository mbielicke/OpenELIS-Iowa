
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
  xmlns:resource="xalan://org.openelis.util.UTFResource"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:noNamespaceSchemaLocation="http://openelis.uhl.uiowa.edu/schema/ScreenSchema.xsd"
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd" 
  xmlns:meta="xalan://org.openelis.meta.ExchangeLocalTermMeta">

  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
<!-- main screen -->
    <screen id="ExchangeVocabularyMap" name="{resource:getString($constants,'exchangeVocabularyMap')}">
      <HorizontalPanel padding="0" spacing="0">
        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="230">        
            <VerticalPanel>
              <table key="atozTable" maxRows="20" style="atozTable" width="auto">
                <col header="{resource:getString($constants,'localTerm')}" key="{meta:getReferenceId()}" width="175">
                  <label field="String" />
                </col>
                <col header="{resource:getString($constants,'type')}" key="{meta:getReferenceTableId()}" width="70">
                  <dropdown field="Integer" width="70" />
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
                    <menuItem key="localTermHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'localTermHistory')}" />
                    <menuItem key="externalTermHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'externalTermHistory')}" />
                  </menuPanel>
                </menuItem>
              </menuPanel>
            </HorizontalPanel>
          </AbsolutePanel>
          <VerticalPanel height="235" padding="0" spacing="0" style="WhiteContentPanel" width="590">
            <TablePanel style="Form">
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"type")' />:
                </text>
                <dropdown field="Integer" key="{meta:getReferenceTableId()}" width="85" required = "true" tab = "{meta:getReferenceName()}, termMappingTable" />
                <widget>
                	<HorizontalPanel width = "20"/>
                </widget>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"localTerm")' />:
                </text>
                <autoComplete field="String" key="{meta:getReferenceName()}" tab = "termMappingTable,{meta:getReferenceTableId()}" required = "true" width="400" popWidth="auto">
                  <col width="400" />
                </autoComplete>
              </row>
            </TablePanel>
            <VerticalPanel height = "5"/>
            <table key="termMappingTable" maxRows="16" showScroll="ALWAYS" width="auto" style="ScreenTableWithSides" tab = "{meta:getReferenceTableId()}, referenceName">
                <col header="{resource:getString($constants,'active')}" key = "{meta:getExtTermProfileIsActive()}" width="45" >
                  <check/>
                </col>
                <col header="{resource:getString($constants,'profile')}"  key = "{meta:getExternalTermExchangeProfileId()}" width="75" >
                  <dropdown field="Integer" width = "75" required = "true"/>
                </col>
                <col header="{resource:getString($constants,'externalTerm')}" key = "{meta:getExternalTermExternalTerm()}" width="120" >
                  <textbox field="String" required = "true"/>
                </col>
                <col header="{resource:getString($constants,'externalDescription')}" key = "{meta:getExternalTermExternalDescription()}" width="190">
                  <textbox field="String" />
                </col>
                <col  header="{resource:getString($constants,'externalCodingSystem')}" key = "{meta:getExternalTermExternalCodingSystem()}" width="190" >
                  <textbox field="String" />
                </col>
            </table>
            <HorizontalPanel>
              <HorizontalPanel>
                <appButton key="addExternalTermButton" style="Button">
                  <HorizontalPanel>
                    <AbsolutePanel style="AddRowButtonImage" />
                    <text>
                      <xsl:value-of select="resource:getString($constants,'addRow')" />
                    </text>
                  </HorizontalPanel>
                </appButton>
                <appButton key="removeExternalTermButton" style="Button">
                  <HorizontalPanel>
                    <AbsolutePanel style="RemoveRowButtonImage" />
                    <text>
                      <xsl:value-of select="resource:getString($constants,'removeRow')" />
                    </text>
                  </HorizontalPanel>
                </appButton>
              </HorizontalPanel>
            </HorizontalPanel>            
          </VerticalPanel>
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>