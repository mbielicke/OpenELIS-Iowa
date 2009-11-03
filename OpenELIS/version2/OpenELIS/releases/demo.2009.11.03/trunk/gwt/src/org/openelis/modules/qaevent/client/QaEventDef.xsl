
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
  xmlns:meta="xalan://org.openelis.metamap.QaEventMetaMap"
  xmlns:testMeta="xalan://org.openelis.metamap.TestMetaMap">

  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource" />
  </xalan:component>
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale" />
  </xalan:component>
  <xalan:component prefix="meta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.QaEventMetaMap" />
  </xalan:component>
  <xalan:component prefix="testMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TestMetaMap" />
  </xalan:component>
  <xsl:template match="doc">
    <xsl:variable name="qae" select="meta:new()" />
    <xsl:variable name="test" select="meta:getTest($qae)" />
    <xsl:variable name="language">
      <xsl:value-of select="locale" />
    </xsl:variable>
    <xsl:variable name="props">
      <xsl:value-of select="props" />
    </xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="QAEvent" name="{resource:getString($constants,'QAEvent')}">
      <HorizontalPanel padding="0" spacing="0">

<!--left table goes here -->

        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="270">
            <buttonGroup key="atozButtons">
              <xsl:call-template name="aToZLeftPanelButtons" />
            </buttonGroup>
            <VerticalPanel>
              <table key="atozTable" width="auto" maxRows="19" style="atozTable">
                <col width="120" header="{resource:getString($constants,'name')}">
                  <label />
                </col>
                <col width="120" header="{resource:getString($constants,'test')}">
                  <label />
                </col>
              </table>
              <widget halign="center">
                <HorizontalPanel>
                  <appButton key="atozPrev" style="Button" enable="false">
                    <AbsolutePanel style="prevNavIndex" />
                  </appButton>
                  <appButton key="atozNext" style="Button" enable="false">
                    <AbsolutePanel style="nextNavIndex" />
                  </appButton>
                </HorizontalPanel>
              </widget>
            </VerticalPanel>
          </HorizontalPanel>
        </CollapsePanel>

<!--button panel code-->

        <VerticalPanel padding="0" spacing="0">
        <AbsolutePanel spacing="0" style="ButtonPanelContainer">
          <HorizontalPanel>
            <xsl:call-template name="queryButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="previousButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="nextButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="buttonPanelDivider" />
            <xsl:call-template name="addButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="updateButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="buttonPanelDivider" />
            <xsl:call-template name="commitButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="abortButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
          </HorizontalPanel>
        </AbsolutePanel>

<!--end button panel-->

        <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
          <TablePanel style="Form">
            <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"name")' />:
              </text>
              <textbox key="{meta:getName($qae)}" width="145" case="LOWER" max="20" tab="{meta:getDescription($qae)},{meta:getReportingText($qae)}" required="true" field="String" />
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"description")' />:
              </text>
              <textbox key="{meta:getDescription($qae)}" width="425" max="60" tab="{meta:getTypeId($qae)},{meta:getName($qae)}" required="true" field="String" />
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"type")' />:
              </text>
              <dropdown key="{meta:getTypeId($qae)}" width="120" tab="{testMeta:getName($test)},{meta:getDescription($qae)}" required="true" field="Integer" />
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"test")' />:
              </text>
              <autoComplete key="{testMeta:getName($test)}" width="140" case="LOWER" popWidth="auto" tab="{meta:getIsBillable($qae)},{meta:getTypeId($qae)}" field="Integer">
                <col width="100" header="{resource:getString($constants,'test')}" />
                <col width="100" header="{resource:getString($constants,'method')}" />
                <col width="250" header="{resource:getString($constants,'description')}" />
              </autoComplete>
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"billable")' />:
              </text>
              <check key="{meta:getIsBillable($qae)}" tab="{meta:getReportingSequence($qae)},{testMeta:getName($test)}" />
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"sequence")' />:
              </text>
              <textbox key="{meta:getReportingSequence($qae)}" width="40" tab="{meta:getReportingText($qae)},{meta:getIsBillable($qae)}" field="Integer" />
            </row>
            <row>
              <widget valign="top">
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"text")' />:
                </text>
              </widget>
              <widget halign="center">
                <textarea key="{meta:getReportingText($qae)}" width="425" height="255" tab="{meta:getName($qae)},{meta:getReportingSequence($qae)}" />
              </widget>
            </row>
          </TablePanel>
        </VerticalPanel>
      </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
