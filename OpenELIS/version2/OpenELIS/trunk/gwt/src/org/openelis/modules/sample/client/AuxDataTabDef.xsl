
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
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd">

  <xsl:template name="AuxDataTab">
  	<xsl:param name="col2WidthParam" select="300" />
  	<xsl:param name="col3WidthParam" select="303" />
    <xsl:param name="maxRowsParam" select="6" />
    <VerticalPanel padding="0" spacing="0">
      <TablePanel padding="0" spacing="0">
        <row>
          <widget colspan="6">
            <table key="auxValsTable" width="auto" maxRows="{string($maxRowsParam)}" showScroll="ALWAYS" title="">
              <col width="85" header="{resource:getString($constants,'reportable')}">
                <check />
              </col>
              <col width="{string($col2WidthParam)}" header="{resource:getString($constants,'name')}">
              	<label field="String" />
              </col>
              <col width="{string($col3WidthParam)}" class="org.openelis.modules.sample.client.AuxTableColumn" header="{resource:getString($constants,'value')}">
                <label field="String" />
              </col>
            </table>
          </widget>
        </row>
        <row>
          <widget colspan="6" style="TableButtonFooter">
            <HorizontalPanel>
              <appButton key="removeAuxButton" style="Button">
                <HorizontalPanel>
                  <AbsolutePanel style="RemoveRowButtonImage" />
                  <text>
                    <xsl:value-of select="resource:getString($constants,'removeRow')" />
                  </text>
                </HorizontalPanel>
              </appButton>
              <appButton key="addAuxButton" style="Button">
                <HorizontalPanel>
                  <AbsolutePanel style="PickerButtonImage" />
                  <text>
                    <xsl:value-of select="resource:getString($constants,'auxGroups')" />
                  </text>
                </HorizontalPanel>
              </appButton>
            </HorizontalPanel>
          </widget>
        </row>
      </TablePanel>
      <TablePanel style="Form">
        <row>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'description')" />:
          </text>
          <widget colspan="3">
            <textbox key="auxDesc" width="275" style="ScreenTextboxDisplayOnly" field="String" />
          </widget>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'method')" />:
          </text>
          <textbox key="auxMethod" width="125" style="ScreenTextboxDisplayOnly" field="String" />
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'unit')" />:
          </text>
          <textbox key="auxUnits" width="125" style="ScreenTextboxDisplayOnly" field="String" />
        </row>
      </TablePanel>
    </VerticalPanel>
  </xsl:template>
</xsl:stylesheet>
