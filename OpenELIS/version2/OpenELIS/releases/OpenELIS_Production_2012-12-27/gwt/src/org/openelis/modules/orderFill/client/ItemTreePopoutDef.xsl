
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

  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language">
      <xsl:value-of select="locale" />
    </xsl:variable>
    <xsl:variable name="props">
      <xsl:value-of select="props" />
    </xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="FillOrder" name="{resource:getString($constants,'itemsOrdered')}" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      <VerticalPanel>
      <VerticalPanel style="WhiteContentPanel">
        <widget>
          <tree key="itemsTree" maxRows="20" showScroll="ALWAYS" style="ScreenTableWithSides" width="auto">
            <header>
              <col header="{resource:getString($constants,'ordNum')}" width="50" />
              <col header="{resource:getString($constants,'qty')}" width="30" />
              <col header="{resource:getString($constants,'item')}" width="250" />
              <col header="{resource:getString($constants,'location')}" width="200" />
              <col header="{resource:getString($constants,'lotNum')}" width="60" />
              <col header="{resource:getString($constants,'expDate')}" width="70" />
            </header>
            <leaf key="top">
              <col>
                <label field="Integer" required="true" />
              </col>
              <col>
                <textbox field="Integer" required="true" />
              </col>
              <col>
                <autoComplete case="LOWER" field="Integer" required="true" width="137">
                  <col width="137" />
                </autoComplete>
              </col>
            </leaf>
            <leaf key="orderItem">
              <col>
                <label field="Integer" />
              </col>
              <col>
                <textbox field="Integer" required="true" />
              </col>
              <col>
                <autoComplete case="LOWER" field="Integer" width="130">
                  <col width="118" />
                </autoComplete>
              </col>
              <col>
                <autoComplete case="LOWER" field="Integer" required="true" width="125">
                  <col header="{resource:getString($constants,'description')}" width="300" />
                  <col header="{resource:getString($constants,'lotNum')}" width="65" />
                  <col header="{resource:getString($constants,'qty')}" width="55" />
                  <col header="{resource:getString($constants,'expDate')}" width="65">
                    <calendar begin="0" end="2" pattern="{resource:getString($constants,'datePattern')}" />
                  </col>
                </autoComplete>
              </col>
              <col>
                <label field="String" />
              </col>
              <col>
                <calendar begin="0" end="2" pattern="{resource:getString($constants,'datePattern')}" />
              </col>
            </leaf>
          </tree>
        </widget>
        <HorizontalPanel>
          <appButton key="removeItemButton" style="Button">
            <HorizontalPanel>
              <AbsolutePanel style="RemoveRowButtonImage" />
              <text>
                <xsl:value-of select="resource:getString($constants,'removeRow')" />
              </text>
            </HorizontalPanel>
          </appButton>
          <appButton key="addItemButton" style="Button">
            <HorizontalPanel>
              <AbsolutePanel style="AddRowButtonImage" />
              <text>
                <xsl:value-of select="resource:getString($constants,'addLocation')" />
              </text>
            </HorizontalPanel>
          </appButton>
        </HorizontalPanel>
      </VerticalPanel>
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