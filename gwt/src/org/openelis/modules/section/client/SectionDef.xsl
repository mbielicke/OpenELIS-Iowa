

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
  xmlns:meta="xalan://org.openelis.metamap.SectionMetaMap"
  xmlns:org="xalan://org.openelis.meta.OrganizationMeta"
  xmlns:parentSect="xalan://org.openelis.meta.SectionMeta">

  <xsl:import href="IMPORT/aToZTwoColumns.xsl" />
  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource" />
  </xalan:component>
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale" />
  </xalan:component>
  <xalan:component prefix="meta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.SectionMetaMap" />
  </xalan:component>
  <xalan:component prefix="org">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrganizationMeta" />
  </xalan:component>
  <xalan:component prefix="parentSect">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.SectionMeta" />
  </xalan:component>
  <xsl:template match="doc">
    <xsl:variable name="sect" select="meta:new()" />
    <xsl:variable name="o" select="meta:getOrganization($sect)" />
    <xsl:variable name="psect" select="meta:getParentSection($sect)" />
    <xsl:variable name="language">
      <xsl:value-of select="locale" />
    </xsl:variable>
    <xsl:variable name="props">
      <xsl:value-of select="props" />
    </xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />

<!-- main screen -->

    <screen id="Section" name="{resource:getString($constants,'labSection')}">
      <HorizontalPanel padding="0" spacing="0">

<!--left table goes here -->

        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="225">
            <buttonGroup key="atozButtons">
              <xsl:call-template name="aToZLeftPanelButtons" />
            </buttonGroup>
            <VerticalPanel>
              <table key="atozTable" width="auto" maxRows="9" style="atozTable">
                <col width="175" header="{resource:getString($constants,'name')}">
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

          <VerticalPanel height="220" padding="0" spacing="0" style="WhiteContentPanel">
            <TablePanel style="Form">
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'name')" />:
                </text>
                <widget colspan="6">
                  <textbox key="{meta:getName($sect)}" width="145" case="LOWER" max="20" tab="{meta:getDescription($sect)},{parentSect:getName($psect)}" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'description')" />:
                </text>
                <widget colspan="6">
                  <textbox key="{meta:getDescription($sect)}" width="425" case="MIXED" max="60" tab="{meta:getIsExternal($sect)},{meta:getName($sect)}" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"external")' />:
                </text>
                <check key="{meta:getIsExternal($sect)}" tab="{org:getName($o)},{meta:getDescription($sect)}" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'organization')" />:
                </text>
                <widget>
                  <autoComplete key="{org:getName($o)}" width="285" case="UPPER" popWidth="auto" tab="{parentSect:getName($psect)},{meta:getIsExternal($sect)}" field="Integer">
                    <col width="180" header="{resource:getString($constants,'name')}" />
                    <col width="110" header="{resource:getString($constants,'street')}" />
                    <col width="100" header="{resource:getString($constants,'city')}" />
                    <col width="20" header="{resource:getString($constants,'st')}" />
                  </autoComplete>
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'parentSection')" />:
                </text>
                <autoComplete key="{parentSect:getName($psect)}" width="180" case="LOWER" popWidth="auto" tab="{meta:getName($sect)},{org:getName($o)}" field="Integer">
                  <col width="180" />
                </autoComplete>
              </row>
            </TablePanel>
          </VerticalPanel>
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
