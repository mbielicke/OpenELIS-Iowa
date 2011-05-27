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
  xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:locale="xalan://java.util.Locale"
  xmlns:resource="xalan://org.openelis.util.UTFResource"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:noNamespaceSchemaLocation="http://openelis.uhl.uiowa.edu/schema/ScreenSchema.xsd"
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd">

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource" />
  </xalan:component>
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale" />
  </xalan:component>
  <xsl:variable name="language">
    <xsl:value-of select="doc/locale" />
  </xsl:variable>
  <xsl:variable name="props">
    <xsl:value-of select="doc/props" />
  </xsl:variable>
  <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
  <xsl:template match="doc">
    <screen id="FinalReport" name="{resource:getString($constants,'finalReport')}">
      <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
        <TablePanel style="Form">
          <row>
            <text style="Prompt">
              <xsl:value-of select="resource:getString($constants,'commentOne')" />
            </text>
          </row>
        </TablePanel>
        <VerticalPanel style="subform">
          <text style="FormTitle">
            <xsl:value-of select='resource:getString($constants,"resultCriteria")' />
          </text>
          <TablePanel style="Form">
            <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"dateReleased")' />:
              </text>
              <text>
                <xsl:value-of select="resource:getString($constants,'from')" />
              </text>
              <text>
                <xsl:value-of select="resource:getString($constants,'to')" />
              </text>
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"calendarformat")' />
              </text>
              <widget>
                <calendar begin="0" end="2" key="RELEASED_FROM" pattern="{resource:getString($constants,'datePattern')}"  width="90" />
              </widget>
              <widget>
                <calendar begin="0" end="2" key="RELEASED_TO" pattern="{resource:getString($constants,'datePattern')}" width="90" />
              </widget>
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"dateCollected")' />:
              </text>
              <text>
                <xsl:value-of select="resource:getString($constants,'from')" />
              </text>
              <text>
                <xsl:value-of select="resource:getString($constants,'to')" />
              </text>
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"calendarformat")' />
              </text>
              <widget>
                <calendar begin="0" end="2" key="COLLECTED_FROM" pattern="{resource:getString($constants,'datePattern')}" width="90" />
              </widget>
              <widget>
                <calendar begin="0" end="2" key="COLLECTED_TO" pattern="{resource:getString($constants,'datePattern')}"  width="90" />
              </widget>
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"collectorName")' />:
              </text>
              <widget colspan="2">
                <textbox case="MIXED" field="String" key="COLLECTOR_NAME" max="60" width="180" />
              </widget>
            </row>
            <row>
              <text style="Prompt"></text>
              <text>
                <xsl:value-of select="resource:getString($constants,'from')" />
              </text>
              <text>
                <xsl:value-of select="resource:getString($constants,'to')" />
              </text>
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"accessionNumber")' />:
              </text>
              <widget>
                <textbox  field="Integer" key="ACCESSION_FROM" max="60" width="90" />
              </widget>
              <widget>
                <textbox field="Integer" key="ACCESSION_TO" max="60" width="90" />
              </widget>
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"clientReference")' />:
              </text>
              <widget colspan="2">
                <textbox case="MIXED" field="String" key="CLIENT_REFERENCE" max="60" width="180" />
              </widget>
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"collectionSite")' />:
              </text>
              <widget colspan="2">
                <textbox case="MIXED" field="String" key="COLLECTION_SITE" max="60" width="180" />
              </widget>
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"collectionTown")' />:
              </text>
              <widget colspan="2">
                <textbox case="MIXED" field="String" key="COLLECTION_TOWN" max="60" width="180" />
              </widget>
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"projectCode")' />:
              </text>
              <widget colspan="2">
                <dropdown field="Integer" key="PROJECT_CODE" width="180" />
              </widget>
            </row>
          </TablePanel>
        </VerticalPanel>
        <TablePanel style="Form">
          <row>
            <text style="Prompt">
              <xsl:value-of select="resource:getString($constants,'commentTwo')" />
            </text>
          </row>
        </TablePanel>
        <HorizontalPanel style="TableFooterPanel">
          <widget halign="center">
            <appButton key="getSampleListButton" style="Button">
              <HorizontalPanel>
                <AbsolutePanel />
                <text>
                  <xsl:value-of select='resource:getString($constants,"getSampleList")' />
                </text>
              </HorizontalPanel>
            </appButton>
          </widget>
          <widget halign="center">
            <appButton key="resetButton" style="Button">
              <HorizontalPanel>
                <AbsolutePanel />
                <text>
                  <xsl:value-of select='resource:getString($constants,"reset")' />
                </text>
              </HorizontalPanel>
            </appButton>
          </widget>
        </HorizontalPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
