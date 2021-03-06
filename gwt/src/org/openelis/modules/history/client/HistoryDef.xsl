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
    <screen xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" id="History" name="{resource:getString($constants,'history')}">
      <VerticalPanel>
        <HorizontalPanel height="100%" spacing="0">
<!--left table goes here -->
          <VerticalPanel style="WhiteContentPanel">
            <VerticalPanel spacing="0">
              <widget>
                <tree key="historyTree" width="auto" maxRows="10" showScroll="ALWAYS">
                  <header>
                    <col width="200" header="{resource:getString($constants,'nameDateAndTime')}" />
                    <col width="130" header="{resource:getString($constants,'userValue')}" />
                    <col width="80" header="{resource:getString($constants,'operation')}" />
                  </header>
                  <leaf key="itemLabel">
                    <col>
                      <label field="String" />
                    </col>
                  </leaf>
                  <leaf key="historyItem">
                    <col>
                      <textbox pattern="{resource:getString($constants,'dateTimeSecondPattern')}" field="Date" />
                    </col>
                    <col>
                      <label field="String" />
                    </col>
                    <col>
                      <label field="String" />
                    </col>
                  </leaf>
                  <leaf key="fields">
                    <col>
                      <label field="String" />
                    </col>
                    <col>
                      <label field="String" />
                    </col>
                  </leaf>
                  <leaf key="linkfields">
                    <col>
                      <label field="String" />
                    </col>
                    <col>
                      <label field="String" style = "ScreenLabelLink"/>
                    </col>
                  </leaf>
                </tree>
              </widget>
            </VerticalPanel>
          </VerticalPanel>
        </HorizontalPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
