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
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd">

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
    <screen id="Panel" name="{resource:getString($constants,'testAnalyteAuxDataFilter')}" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
<!--left table goes here -->
      <VerticalPanel height="630" padding="0" spacing="0" style="WhiteContentPanel" width="500">
        <text style="heading">
          <xsl:value-of select='resource:getString($constants,"testAnalyteHeading")' />
        </text>
        <HorizontalPanel>
          <VerticalPanel style="Form">
            <widget valign="top">
              <table key="analyteTable" maxRows="9" showScroll="ALWAYS" style="ScreenTableWithSides" title="" width="auto">
                <col key="select" width="20">
                  <check />
                </col>
                <col align="left" header="{resource:getString($constants,'name')}" key="analyte" width="220">
                  <label field="String" />
                </col>
              </table>
            </widget>
            <HorizontalPanel>
              <HorizontalPanel width="5" />
              <widget style="TableButtonFooter">
                <HorizontalPanel>
                  <appButton key="selectAllAnalyteButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style = "Checked"/>
                      <text>
                        <xsl:value-of select="resource:getString($constants,'selectAll')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                  <appButton key="unselectAllAnalyteButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style = "Unchecked"/>
                      <text>
                        <xsl:value-of select="resource:getString($constants,'unselectAll')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                </HorizontalPanel>
              </widget>
            </HorizontalPanel>
          </VerticalPanel>
          <HorizontalPanel width="30" />
          <VerticalPanel style="Form">
            <widget valign="top">
              <table key="resultTable" maxRows="9" showScroll="ALWAYS" style="ScreenTableWithSides" title="" width="auto">
                <col key="select" width="20">
                  <check />
                </col>
                <col align="left" header="{resource:getString($constants,'result')}" key="result" width="225">
                  <label field="String" />
                </col>
              </table>
            </widget>
            <HorizontalPanel>
              <HorizontalPanel width="5" />
              <widget style="TableButtonFooter">
                <HorizontalPanel>
                  <appButton key="selectAllResultButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style = "Checked"/>
                      <text>
                        <xsl:value-of select="resource:getString($constants,'selectAll')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                  <appButton key="unselectAllResultButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style = "Unchecked"/>
                      <text>
                        <xsl:value-of select="resource:getString($constants,'unselectAll')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                </HorizontalPanel>
              </widget>
            </HorizontalPanel>
          </VerticalPanel>
        </HorizontalPanel>
        <VerticalPanel height="2" />
        <text style="heading">
          <xsl:value-of select='resource:getString($constants,"auxData")' />
        </text>
        <HorizontalPanel>
          <VerticalPanel style="Form">
            <widget valign="top">
              <table key="auxDataTable" maxRows="9" showScroll="ALWAYS" style="ScreenTableWithSides" title="" width="auto">
                <col key="select" width="20">
                  <check />
                </col>
                <col align="left" header="{resource:getString($constants,'name')}" key="auxData" width="225">
                  <label field="String" />
                </col>
              </table>
            </widget>
            <HorizontalPanel>
              <HorizontalPanel width="5" />
              <widget style="TableButtonFooter">
                <HorizontalPanel>
                  <appButton key="selectAllAuxButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style = "Checked"/>
                      <text>
                        <xsl:value-of select="resource:getString($constants,'selectAll')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                  <appButton key="unselectAllAuxButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style = "Unchecked"/>
                      <text>
                        <xsl:value-of select="resource:getString($constants,'unselectAll')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                </HorizontalPanel>
              </widget>
            </HorizontalPanel>
          </VerticalPanel>
          <HorizontalPanel width="30" />
          <VerticalPanel style="Form">
            <widget valign="top">
              <table key="valueTable" maxRows="9" showScroll="ALWAYS" style="ScreenTableWithSides" title="" width="auto">
                <col key="select" width="20">
                  <check />
                </col>
                <col align="left" header="{resource:getString($constants,'value')}" key="result" width="220">
                  <label field="String" />
                </col>
              </table>
            </widget>
            <HorizontalPanel>
              <HorizontalPanel width="5" />
              <widget style="TableButtonFooter">
                <HorizontalPanel>
                  <appButton key="selectAllValueButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style = "Checked"/>
                      <text>
                        <xsl:value-of select="resource:getString($constants,'selectAll')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                  <appButton key="unselectAllValueButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style = "Unchecked"/>
                      <text>
                        <xsl:value-of select="resource:getString($constants,'unselectAll')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                </HorizontalPanel>
              </widget>
            </HorizontalPanel>
          </VerticalPanel>
        </HorizontalPanel>
        <HorizontalPanel>
          <HorizontalPanel width="230" />
          <appButton key="runReportButton" style="Button">
            <HorizontalPanel>
              <AbsolutePanel />
              <text>
                <xsl:value-of select="resource:getString($constants,'runReport')" />
              </text>
            </HorizontalPanel>
          </appButton>
          <appButton key="cancelButton" style="Button">
            <HorizontalPanel>
              <AbsolutePanel />
              <text>
                <xsl:value-of select="resource:getString($constants,'cancel')" />
              </text>
            </HorizontalPanel>
          </appButton>
        </HorizontalPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>