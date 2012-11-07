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
  xmlns:meta="xalan://org.openelis.meta.OrderMeta"
  xmlns:resource="xalan://org.openelis.util.UTFResource"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:noNamespaceSchemaLocation="http://openelis.uhl.uiowa.edu/schema/ScreenSchema.xsd"
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd">

  <xsl:import href="IMPORT/aToZTwoColumns.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="TestContainerPopoutLookup" name="{resource:getString($constants,'testsAndContainers')}">
      <VerticalPanel>
        <HorizontalPanel padding="0" spacing="0" style="WhiteContentPanel">
          <VerticalPanel padding="0" spacing="0">
            <widget valign="top">
              <tree key="testTree" maxRows="11" showScroll="ALWAYS" style="ScreenTableWithSides" width="440">
                <header>
                  <col header="{resource:getString($constants,'itemNum')}" width="55" />
                  <col header="{resource:getString($constants,'testMethodDescription')}" width="526" />
                </header>
                <leaf key="test">
                  <col align="left" key="{meta:getTestItemSequence()}">
                    <textbox field="Integer" required="true" />
                  </col>
                  <col align="left" key="{meta:getTestName()}">
                    <autoComplete case="LOWER" field="Integer" popWidth="560" required="true" width="160">
                      <col header="{resource:getString($constants,'testMethodDescription')}" width="555" />
                    </autoComplete>
                  </col>
                </leaf>
                <leaf key="analyte">
                  <col>
                    <check />
                  </col>
                  <col align="left" key="analyte">
                    <label field="String" />
                  </col>
                </leaf>
              </tree>
            </widget>
            <HorizontalPanel>
              <widget style="TableButtonFooter">
                <HorizontalPanel>
                  <appButton key="addTestButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style="AddRowButtonImage" />
                      <text>
                        <xsl:value-of select="resource:getString($constants,'addRow')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                  <appButton key="removeTestButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style="RemoveRowButtonImage" />
                      <text>
                        <xsl:value-of select="resource:getString($constants,'removeRow')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                  <appButton key="checkAllButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style = "Checked"/>
                        <text>
                          <xsl:value-of select="resource:getString($constants,'checkAll')" />
                        </text>
                    </HorizontalPanel>
                  </appButton>
                  <appButton key="uncheckAllButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style = "Unchecked"/>
                        <text>
                          <xsl:value-of select="resource:getString($constants,'uncheckAll')" />
                        </text>
                    </HorizontalPanel>
                  </appButton>
                </HorizontalPanel>
              </widget>
            </HorizontalPanel>
          </VerticalPanel>
          <HorizontalPanel width="30"/>
          <VerticalPanel padding="0" spacing="0">
            <widget valign="top">
              <table key="orderContainerTable" maxRows="11" showScroll="ALWAYS" style="ScreenTableWithSides" title="" width="440">
                <col align="left" header="{resource:getString($constants,'itemNum')}" key="{meta:getContainerItemSequence()}" width="45">
                  <textbox field="Integer" />
                </col>
                <col align="left" header="{resource:getString($constants,'container')}" key="{meta:getContainerContainerId()}" width="302">
                  <dropdown field="Integer" popWidth="340" required="true" width="340" />
                </col>
                <col align="left" header="{resource:getString($constants,'sampleType')}" key="{meta:getContainerTypeOfSampleId()}" width="84">
                  <dropdown field="Integer" popWidth="200" width="191" />
                </col>
              </table>
            </widget>
            <HorizontalPanel>
              <HorizontalPanel>
                <appButton key="addContainerButton" style="Button">
                  <HorizontalPanel>
                    <AbsolutePanel style="AddRowButtonImage" />
                    <text>
                      <xsl:value-of select="resource:getString($constants,'addRow')" />
                    </text>
                  </HorizontalPanel>
                </appButton>
                <appButton key="removeContainerButton" style="Button">
                  <HorizontalPanel>
                    <AbsolutePanel style="RemoveRowButtonImage" />
                    <text>
                      <xsl:value-of select="resource:getString($constants,'removeRow')" />
                    </text>
                  </HorizontalPanel>
                </appButton>
                <appButton key="duplicateContainerButton" style="Button">
                  <HorizontalPanel>
                    <AbsolutePanel style="DuplicateRowButtonImage" />
                    <text>
                      <xsl:value-of select="resource:getString($constants,'duplicateRecord')" />
                    </text>
                  </HorizontalPanel>
                </appButton>
              </HorizontalPanel>
            </HorizontalPanel>
          </VerticalPanel>
        </HorizontalPanel>
        <AbsolutePanel align="center" spacing="0" style="BottomButtonPanelContainer">
          <HorizontalPanel>
            <xsl:call-template name="okButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>            
          </HorizontalPanel>
        </AbsolutePanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>