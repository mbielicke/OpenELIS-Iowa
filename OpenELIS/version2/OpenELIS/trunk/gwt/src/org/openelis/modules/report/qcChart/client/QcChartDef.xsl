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
  xmlns:meta="xalan://org.openelis.meta.QcListMeta"
  xmlns:resource="xalan://org.openelis.util.UTFResource"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:noNamespaceSchemaLocation="http://openelis.uhl.uiowa.edu/schema/ScreenSchema.xsd"
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd">

  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="qcChart" name="{resource:getString($constants,'qcChart')}" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      <VerticalPanel height="235" padding="0" spacing="0" style="WhiteContentPanel" width="500">
        <HorizontalPanel>
          <VerticalPanel>
            <TablePanel style="Form">
              <row>
                <widget colspan="5">
                  <text style="heading">
                    <xsl:value-of select='resource:getString($constants,"useDateRange")' />
                  </text>
                </widget>
              </row>
              <row>
                <widget>
                  <HorizontalPanel width="50" />
                </widget>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"begin")' />:
                </text>
                <calendar begin="0" end="2" key="{meta:getWorksheetCreatedDateFrom()}" pattern="{resource:getString($constants,'datePattern')}" tab="{meta:getWorksheetCreatedDateTo()},plotDataTable" width="90" />
              </row>
              <row>
                <widget>
                  <HorizontalPanel width="50" />
                </widget>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"end")' />:
                </text>
                <calendar begin="0" end="2" key="{meta:getWorksheetCreatedDateTo()}" pattern="{resource:getString($constants,'datePattern')}" tab="numInstances,{meta:getWorksheetCreatedDateFrom()}" width="90" />
              </row>
            </TablePanel>
          </VerticalPanel>
          <VerticalPanel>
            <TablePanel>
              <row>
                <text>|</text>
              </row>
              <row>
                <text>|</text>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"or")' />
                </text>
              </row>
              <row>
                <text>|</text>
              </row>
              <row>
                <text>|</text>
              </row>
            </TablePanel>
          </VerticalPanel>
          <VerticalPanel>
            <TablePanel style="Form">
              <row>
                <widget colspan="5">
                  <text style="heading">
                    <xsl:value-of select='resource:getString($constants,"mostRecentQc")' />
                  </text>
                </widget>
              </row>
              <row>
                <widget>
                  <HorizontalPanel width="50" />
                </widget>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"noToRetrieve")' />:
                </text>
                <widget>
                  <textbox field="Integer" key="numInstances" max="30" tab="{meta:getQCName()},{meta:getWorksheetCreatedDateTo()}" width="50" />
                </widget>
              </row>
            </TablePanel>
          </VerticalPanel>
          <VerticalPanel width="50"></VerticalPanel>
          <VerticalPanel>
            <widget>
              <VerticalPanel height="40" />
            </widget>
            <widget>
              <appButton key="getData" style="Button">
                <HorizontalPanel>
                  <AbsolutePanel style="FindButtonImage" />
                  <text>
                    <xsl:value-of select="resource:getString($constants,'getData')" />
                  </text>
                </HorizontalPanel>
              </appButton>
            </widget>
            <widget>
              <VerticalPanel height="5" />
            </widget>
            <widget>
              <appButton key="reCompute" style="Button">
                <HorizontalPanel>
                  <AbsolutePanel style="refreshButtonImage" />
                  <text>
                    <xsl:value-of select="resource:getString($constants,'reCompute')" />
                  </text>
                </HorizontalPanel>
              </appButton>
            </widget>
            <widget>
              <VerticalPanel height="5" />
            </widget>
            <widget>
              <appButton key="plotdata" style="Button">
                <HorizontalPanel>
                  <AbsolutePanel style="chartButtonImage" />
                  <text>
                    <xsl:value-of select="resource:getString($constants,'plotData')" />
                  </text>
                </HorizontalPanel>
              </appButton>
            </widget>
          </VerticalPanel>
        </HorizontalPanel>
        <VerticalPanel>
          <TablePanel style="Form">
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'name')" />:
              </text>
              <widget colspan="3">
                <autoComplete field="String" key="{meta:getQCName()}" required="true" tab="plot,numInstances" width="230">
                  <col header="{resource:getString($constants,'name')}" width="175">
                    <label field="String" />
                  </col>
                  <col header="{resource:getString($constants,'type')}" key="{meta:getQCType()}" width="100">
                    <dropdown field="Integer" width="100" />
                  </col>
                </autoComplete>
              </widget>
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"plotUsing")' />:
              </text>
              <widget colspan="4">
                <dropdown field="Integer" key="plot" tab="plotDataTable,{meta:getQCName()}" width="175" />
              </widget>
            </row>
          </TablePanel>
        </VerticalPanel>
        <widget valign="top">
          <table key="plotDataTable" maxRows="9" multiSelect="true" showScroll="ALWAYS" style="ScreenTableWithSides" tab="{meta:getWorksheetCreatedDateFrom()},plot" title="" width="auto">
            <col align="left" header="{resource:getString($constants,'plot')}" width="30">
              <check />
            </col>
            <col align="left" header="{resource:getString($constants,'accessionNum')}" sort="true" width="80">
              <textbox field="String" />
            </col>
            <col align="left" header="{resource:getString($constants,'lotNum')}" sort="true" width="110">
              <textbox field="String" />
            </col>
            <col align="left" header="{resource:getString($constants,'creationDate')}" sort="true" width="110">
              <calendar begin="0" end="4" pattern="{resource:getString($constants,'dateTimePattern')}" />
            </col>
            <col align="left" header="{resource:getString($constants,'analyte')}" sort="true" width="200">
              <textbox field="String" />
            </col>
            <col align="right" header="{resource:getString($constants,'v1')}" width="50">
              <textbox field="String" />
            </col>
            <col align="right" header="{resource:getString($constants,'v2')}" width="50">
              <textbox field="String" />
            </col>
            <col align="right" header="{resource:getString($constants,'plotValue')}" width="50">
              <textbox field="Double" />
            </col>
            <col align="right" header="{resource:getString($constants,'mean')}" width="50">
              <textbox field="Double" pattern="{resource:getString($constants,'displayDoubleFormat')}" />
            </col>
            <col align="right" header="{resource:getString($constants,'uWL')}" width="50">
              <textbox field="Double" pattern="{resource:getString($constants,'displayDoubleFormat')}" />
            </col>
            <col align="right" header="{resource:getString($constants,'uCL')}" width="50">
              <textbox field="Double" pattern="{resource:getString($constants,'displayDoubleFormat')}" />
            </col>
            <col align="right" header="{resource:getString($constants,'lWL')}" width="50">
              <textbox field="Double" pattern="{resource:getString($constants,'displayDoubleFormat')}" />
            </col>
            <col align="right" header="{resource:getString($constants,'lCL')}" width="50">
              <textbox field="Double" pattern="{resource:getString($constants,'displayDoubleFormat')}" />
            </col>
          </table>
        </widget>
        <HorizontalPanel>
          <widget halign="left">
            <appButton key="selectButton" style="Button">
              <HorizontalPanel>
                <AbsolutePanel style="Checked" />
                <text>
                  <xsl:value-of select='resource:getString($constants,"select")' />
                </text>
              </HorizontalPanel>
            </appButton>
          </widget>
          <widget halign="right">
            <appButton key="unselectButton" style="Button">
              <HorizontalPanel>
                <AbsolutePanel style="Unchecked" />
                <text>
                  <xsl:value-of select='resource:getString($constants,"unselect")' />
                </text>
              </HorizontalPanel>
            </appButton>
          </widget>
          <HorizontalPanel width="20" />
          <widget halign="left">
            <appButton key="selectAllButton" style="Button">
              <HorizontalPanel>
                <AbsolutePanel style="Checked" />
                <text>
                  <xsl:value-of select='resource:getString($constants,"selectAll")' />
                </text>
              </HorizontalPanel>
            </appButton>
          </widget>
          <widget halign="right">
            <appButton key="unselectAllButton" style="Button">
              <HorizontalPanel>
                <AbsolutePanel style="Unchecked" />
                <text>
                  <xsl:value-of select='resource:getString($constants,"unselectAll")' />
                </text>
              </HorizontalPanel>
            </appButton>
          </widget>
        </HorizontalPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>