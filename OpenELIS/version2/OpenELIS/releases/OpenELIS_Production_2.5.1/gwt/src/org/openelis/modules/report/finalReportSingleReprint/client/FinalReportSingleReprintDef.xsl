
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
  xmlns:meta="xalan://org.openelis.meta.SystemVariableMeta">

  <xsl:import href="IMPORT/aToZTwoColumns.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="FinalReportSingle" name="{resource:getString($constants,'finalReportSingleReprint')}">
          <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
            <TablePanel style="Form">
              <row>               
                <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'accessionNum')" />:
                </text>
                <textbox key="accessionNumber" width="75" field="Integer" required="true" tab = "print,comment"/>
              </row>
            </TablePanel>
            <TablePanel style="Form">
              <row>              
                <check key="print" tab = "organization,accessionNumber"/>
                <text style="LeftAlignPrompt">
                  <xsl:value-of select='resource:getString($constants,"print")' />
                </text>
              </row>
            </TablePanel>
            <TablePanel style="Form">  
              <row>
                <widget>
                  <HorizontalPanel width="12" />
                </widget>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'organization')" />:
                </text>
                <dropdown key="organization" width = "225" field="Integer" tab = "printer,print" />
              </row>
              <row>
               <widget>
                 <HorizontalPanel width="12" />
                </widget>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'printer')" />:
                </text>
                <dropdown key="printer" width = "225" field="String" tab = "fax,organization"/>
              </row>
            </TablePanel>
            <TablePanel style="Form">
              <row>
                <check key="fax" tab = "destination,printer"/>
                <text style="LeftAlignPrompt">
                  <xsl:value-of select='resource:getString($constants,"fax")' />
                </text>
              </row>
            </TablePanel>
            <TablePanel style="Form">
              <row>
                <widget>
                  <HorizontalPanel width="15" />
                </widget>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"destination")' />:
                </text>
                <dropdown key="destination" width = "225" field="String" tab = "faxNumber,fax"/>
              </row>
              <row>
                <widget>
                  <HorizontalPanel width="15" />
                </widget>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"faxNumber")' />:
                </text>
                <textbox case="LOWER" field="String" key="faxNumber" max="17" width="120" mask="{resource:getString($constants,'phoneWithExtensionPattern')}" tab = "from,destination"/>
              </row>
              <row>
                <widget>
                  <HorizontalPanel width="15" />
                </widget>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"from")' />:
                </text>
                <textbox field="String" key="from" width="225" tab = "toName,faxNumber"/>
              </row>
              <row>
                <widget>
                  <HorizontalPanel width="15" />
                </widget>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"toName")' />:
                </text>
                <textbox case="UPPER" field="String" key="toName" width="225" tab = "toCompany,from"/>
              </row>
              <row>
                <widget>
                 <HorizontalPanel width="15" />
                </widget>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"toCompany")' />:
                </text>
                <textbox case="UPPER" field="String" key="toCompany" width="225" tab = "comment,toName"/>
              </row>
              <row>
                <widget>
                 <HorizontalPanel width="15" />
                </widget>
                <widget valign="top">
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"comment")' />:
                  </text>
                </widget>
                <textarea key="comment" width="300" height = "100" tab = "accessionNumber,toCompany"/>
              </row>
            </TablePanel>
            <widget halign="center">
              <HorizontalPanel>
                  <appButton key="runReportButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel />
                      <text>
                        <xsl:value-of select="resource:getString($constants,'runReport')" />
                      </text>
                   </HorizontalPanel>
                  </appButton>
                  <appButton key="resetButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel />
                      <text>
                        <xsl:value-of select="resource:getString($constants,'reset')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
              </HorizontalPanel>
            </widget>
          </VerticalPanel>
      </screen>
    </xsl:template>
</xsl:stylesheet>