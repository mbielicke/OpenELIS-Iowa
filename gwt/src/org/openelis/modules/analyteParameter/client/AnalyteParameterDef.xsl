
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
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd" 
  xmlns:meta="xalan://org.openelis.meta.AnalyteParameterMeta">

  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
<!-- main screen -->
    <screen id="AnalyteParameter" name="{resource:getString($constants,'analyteParameter')}">
      <HorizontalPanel padding="0" spacing="0">
        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="250">            
            <VerticalPanel>
              <table key="atozTable" maxRows="20" style="atozTable" width="auto">
                <col header="{resource:getString($constants,'type')}" key="{meta:getReferenceTableId()}"  width="45">
                  <dropdown field="Integer" width="45" />
                </col>
                <col header="{resource:getString($constants,'name')}" width="175">
                  <label field="String" />
                </col>
              </table>
              <widget halign="center">
                <HorizontalPanel>
                  <appButton enable="false" key="atozPrev" style="Button">
                    <AbsolutePanel style="prevNavIndex" />
                  </appButton>
                  <appButton enable="false" key="atozNext" style="Button">
                    <AbsolutePanel style="nextNavIndex" />
                  </appButton>
                </HorizontalPanel>
              </widget>
            </VerticalPanel>
          </HorizontalPanel>
        </CollapsePanel>
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
          <VerticalPanel height="235" padding="0" spacing="0" style="WhiteContentPanel" width="590">
            <TablePanel style="Form">
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"type")' />:
                </text>
                <dropdown field="Integer" key="{meta:getReferenceTableId()}" width="60" required = "true" tab = "referenceName, parameterTree" />
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"name")' />:
                </text>
                <autoComplete field="Integer" key="{meta:getReferenceName()}" tab = "parameterTree, {meta:getReferenceTableId()}" required = "true" popWidth="auto" width="175">
                    <col width="175" />
                  </autoComplete>
              </row>
            </TablePanel>
            <VerticalPanel height = "5"/>
            <tree key="parameterTree" maxRows="18" showScroll="ALWAYS" width="auto" style="ScreenTableWithSides" tab = "{meta:getReferenceTableId()}, referenceName">
              <header>
              	<col header="{resource:getString($constants,'active')}" width="55" />
                <col header="{resource:getString($constants,'analyte')}" width="200" />
                <col header="{resource:getString($constants,'sampleType')}" width="80" />
                <col header="{resource:getString($constants,'beginDate')}" width="110" />
                <col header="{resource:getString($constants,'endDate')}" width="110" />
                <col header="{resource:getString($constants,'p1')}" width="50" />
                <col header="{resource:getString($constants,'p2')}" width="50" />
                <col header="{resource:getString($constants,'p3')}" width="50" />
              </header>
              <leaf key="latest">
                <col>
                  <check key="active"/>
                </col>
                <col>                  
                  <label field="String" key="{meta:getAnalyteName()}"/>                    
                </col>
                <col>
                  <dropdown field="Integer" key = "{meta:getTypeOfSampleId()}" popWidth="150" width="80" />
                </col>
                <col align="left">
                  <calendar begin="0" end="4" key = "{meta:getActiveBegin()}" pattern="{resource:getString($constants,'dateTimePattern')}" />
                </col>
                <col align="left">
                  <calendar begin="0" end="4" key = "{meta:getActiveEnd()}" pattern="{resource:getString($constants,'dateTimePattern')}" />
                </col>
                <col>
                  <textbox field="Double" key = "{meta:getP1()}" />
                </col>
                <col>
                  <textbox field="Double" key = "{meta:getP2()}"/>
                </col>
                <col>
                  <textbox field="Double" key = "{meta:getP3()}"/>
                </col>
              </leaf>
              <leaf key="previous">
                <col>
                  <check key="active"/>
                </col>
                <col>
                  <label field="String" key="{meta:getAnalyteName()}"/>
                </col>
                <col>
                  <dropdown field="Integer" key = "{meta:getTypeOfSampleId()}" popWidth="75" width="75" />
                </col>
                <col align="left">
                  <calendar begin="0" end="4" key = "{meta:getActiveBegin()}" pattern="{resource:getString($constants,'dateTimePattern')}" />
                </col>
                <col align="left">
                  <calendar begin="0" end="4" key = "{meta:getActiveEnd()}" pattern="{resource:getString($constants,'dateTimePattern')}" />
                </col>
                <col>
                  <textbox field="Double" key = "{meta:getP1()}" />
                </col>
                <col>
                  <textbox field="Double" key = "{meta:getP2()}"/>
                </col>
                <col>
                  <textbox field="Double" key = "{meta:getP3()}"/>
                </col>
              </leaf>
            </tree>            
          </VerticalPanel>
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>