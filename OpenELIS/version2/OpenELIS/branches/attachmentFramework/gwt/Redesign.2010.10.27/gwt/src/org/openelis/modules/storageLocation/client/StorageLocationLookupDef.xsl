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

  <xsl:import href="IMPORT/aToZTwoColumns.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" name="{resource:getString($constants,'storageLocationSelection')}">
      <VerticalPanel>
        <HorizontalPanel height="100%" spacing="0">
<!--left table goes here -->
          <VerticalPanel spacing="0" style="WhiteContentPanel">
            <HorizontalPanel>
              <TablePanel style="Form">
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'search')" />:
                  </text>
                  <autoComplete key="location" width="375">
                    <col width="375" />
                  </autoComplete>
                </row>
              </TablePanel>
            </HorizontalPanel>
            <VerticalPanel spacing="0">
              <widget>
                <table key="storageLocationTable" rows="10" vscroll="ALWAYS" hscroll="ALWAYS" style="ScreenTableWithSides">
                  <col key="parent" width="140" header="{resource:getString($constants,'parent')}">
                    <label field="String" />
                  </col>
                  <col key="unitDesc" width="140" header="{resource:getString($constants,'storageUnit')}">
                    <label field="String" />
                  </col>
                  <col key="location" width="140" header="{resource:getString($constants,'location')}">
                    <label field="String" />
                  </col>
                </table>
              </widget>
            </VerticalPanel>
          </VerticalPanel>
        </HorizontalPanel>
        <VerticalPanel width="457" spacing="0">
          <AbsolutePanel align="center" spacing="0" style="BottomButtonPanelContainer">
            <HorizontalPanel>
              <xsl:call-template name="okButton"/>
              <xsl:call-template name="cancelButton"/>
            </HorizontalPanel>
          </AbsolutePanel>
        </VerticalPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
