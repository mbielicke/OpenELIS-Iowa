
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
  xmlns:meta="xalan://org.openelis.meta.SampleMeta">

  <xsl:template name="ResultTab">
    <xsl:param name="widthParam" select="697" />
    <xsl:param name="maxRowsParam" select="9" />
    <VerticalPanel padding="0" spacing="0">
      <TablePanel padding="0" spacing="0">
        <row>
          <table key="testResultsTable" width="{string($widthParam)}" rows="{string($maxRowsParam)}" vscroll="ALWAYS" hscroll="ALWAYS">
            <col width="65" class="org.openelis.modules.sample.client.SampleResultTableColumn">
              <label field="String" />
            </col>
            <col width="200">
              <textbox max="80" field="String" />
            </col>
            <col width="200">
              <textbox max="80" field="String" />
            </col>
            <col width="200">
              <textbox max="80" field="String" />
            </col>
            <col width="200">
              <textbox max="80" field="String" />
            </col>
            <col width="200">
              <textbox max="80" field="String" />
            </col>
            <col width="200">
              <textbox max="80" field="String" />
            </col>
            <col width="200">
              <textbox max="80" field="String" />
            </col>
            <col width="200">
              <textbox max="80" field="String" />
            </col>
            <col width="200">
              <textbox max="80" field="String" />
            </col>
            <col width="200">
              <textbox max="80" field="String" />
            </col>
          </table>
        </row>
        <row>
          <widget style="TableButtonFooter">
            <HorizontalPanel>
              <button key="addResultButton" icon="AddRowButtonImage" text="{resource:getString($constants,'addRow')}" style="Button"/>
              <button key="removeResultButton" icon="RemoveRowButtonImage" text="{resource:getString($constants,'removeRow')}" style="Button"/>
              <button key="suggestionsButton" icon="PickerButtonImage" text="{resource:getString($constants,'suggestions')}" style="Button"/>
              <button key="popoutTable" icon="popoutButtonImage" text="{resource:getString($constants,'popout')}" style="Button"/>
            </HorizontalPanel>
          </widget>
        </row>
      </TablePanel>
    </VerticalPanel>
  </xsl:template>
</xsl:stylesheet>
