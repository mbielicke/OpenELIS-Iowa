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
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0"
  xmlns:resource="xalan://org.openelis.util.UTFResource"
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:locale="xalan://java.util.Locale"
  xmlns:meta="xalan://org.openelis.metamap.MethodMetaMap"
  extension-element-prefixes="resource"
  xmlns:xalan="http://xml.apache.org/xalan"
  xsi:noNamespaceSchemaLocation="http://openelis.uhl.uiowa.edu/schema/ScreenSchema.xsd">

  <xsl:import href="IMPORT/aToZTwoColumns.xsl" />
  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource" />
  </xalan:component>
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale" />
  </xalan:component>
  <xalan:component prefix="meta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.MethodMetaMap" />
  </xalan:component>
  <xsl:template match="doc">
    <xsl:variable name="method" select="meta:new()" />
    <xsl:variable name="language">
      <xsl:value-of select="locale" />
    </xsl:variable>
    <xsl:variable name="props">
      <xsl:value-of select="props" />
    </xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen name="{resource:getString($constants,'method')}" id="Test">
      <HorizontalPanel padding="0" spacing="0">
<!--left table goes here -->
        <CollapsePanel style="LeftSidePanel" height="235px" key="collapsePanel">
          <HorizontalPanel width="225px">
            <buttonGroup key="atozButtons">
              <xsl:call-template name="aToZLeftPanelButtons" />
            </buttonGroup>
            <VerticalPanel>
              <table width="auto" key="atozTable" maxRows="9">
                <col width="175" header="{resource:getString($constants,'method')}">
                  <label />
                </col>
              </table>
              <widget halign="center">
                <HorizontalPanel>
                  <appButton enable="false" style="Button" key="atozPrev">
                    <AbsolutePanel style="prevNavIndex" />
                  </appButton>
                  <appButton enable="false" style="Button" key="atozNext">
                    <AbsolutePanel style="nextNavIndex" />
                  </appButton>
                </HorizontalPanel>
              </widget>
            </VerticalPanel>
          </HorizontalPanel>
        </CollapsePanel>
        <VerticalPanel padding="0" spacing="0">
<!--button panel code-->
          <AbsolutePanel style="ButtonPanelContainer" spacing="0">
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
          <VerticalPanel width="620px" padding="0" style="WhiteContentPanel" height="235px" spacing="0">
            <TablePanel style="Form">
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'name')" />:
                </text>
                <widget colspan="6">
                  <textbox width="145px" max="20" case="LOWER" key="{meta:getName($method)}" tab="{meta:getDescription($method)},{meta:getActiveEnd($method)}" required="true"/>
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'description')" />:
                </text>
                <widget colspan="6">
                  <textbox width="425px" max="60" case="MIXED" key="{meta:getDescription($method)}" tab="{meta:getReportingDescription($method)},{meta:getName($method)}" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'reportDescription')" />:
                </text>
                <widget colspan="6">
                  <textbox width="425px" max="60" case="MIXED" key="{meta:getReportingDescription($method)}" tab="{meta:getIsActive($method)},{meta:getDescription($method)}" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"active")' />:
                </text>
                <check key="{meta:getIsActive($method)}" tab="{meta:getActiveBegin($method)},{meta:getReportingDescription($method)}" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"beginDate")' />:
                </text>
                <calendar width="90px" key="{meta:getActiveBegin($method)}" begin="0" end="2" tab="{meta:getActiveEnd($method)},{meta:getIsActive($method)}" required="true" pattern="{resource:getString($constants,'datePattern')}"/>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"endDate")' />:
                </text>
                <calendar width="90px" key="{meta:getActiveEnd($method)}" begin="0" end="2" tab="{meta:getName($method)},{meta:getActiveBegin($method)}" required="true"  pattern="{resource:getString($constants,'datePattern')}"/>
              </row>
            </TablePanel>
          </VerticalPanel>
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
