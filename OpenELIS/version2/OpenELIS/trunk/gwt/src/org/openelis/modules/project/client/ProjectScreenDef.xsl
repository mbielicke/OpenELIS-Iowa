
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
  xmlns:meta="xalan://org.openelis.metamap.ProjectMetaMap"
  xmlns:prmtrMeta="xalan://org.openelis.metamap.ProjectParameterMetaMap"
  xmlns:script="xalan://org.openelis.meta.ScriptletMeta">

  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource" />
  </xalan:component>
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale" />
  </xalan:component>
  <xalan:component prefix="meta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.ProjectMetaMap" />
  </xalan:component>
  <xalan:component prefix="prmtrMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.ProjectParameterMetaMap" />
  </xalan:component>
  <xalan:component prefix="script">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.ScriptletMeta" />
  </xalan:component>
  <xsl:template match="doc">
    <xsl:variable name="proj" select="meta:new()" />
    <xsl:variable name="scpt" select="meta:getScriptlet($proj)" />
    <xsl:variable name="prm" select="meta:getProjectParameter($proj)" />
    <xsl:variable name="language">
      <xsl:value-of select="locale" />
    </xsl:variable>
    <xsl:variable name="props">
      <xsl:value-of select="props" />
    </xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="Project" name="{resource:getString($constants,'project')}">
        <HorizontalPanel padding="0" spacing="0">

<!--left table goes here -->

          <CollapsePanel key="collapsePanel" style="LeftSidePanel">
            <HorizontalPanel width="225">
              <buttonGroup key="atozButtons">
                <xsl:call-template name="aToZLeftPanelButtons" />
              </buttonGroup>
              <VerticalPanel>
                <table key="atozTable" width="auto" maxRows="18" style="atozTable">
                  <col width="175" header="{resource:getString($constants,'name')}">
                    <label />
                  </col>
              	</table>
                <widget halign="center">
                  <HorizontalPanel >
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
          <VerticalPanel padding="0" spacing="0">

<!--button panel code-->

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

            <VerticalPanel height="235" padding="0" spacing="0" style="WhiteContentPanel" width="620">
              <TablePanel style="Form">
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'id')" />:
                  </text>
                  <textbox key="{meta:getId($proj)}" tab="{meta:getName($proj)},parameterTable" width="50" field="Integer" />
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'name')" />:
                  </text>
                  <widget colspan="6">
                    <textbox case="LOWER" key="{meta:getName($proj)}" max="20" tab="{meta:getDescription($proj)},{meta:getId($proj)}" width="145" required="true"/>
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'description')" />:
                  </text>
                  <widget colspan="6">
                    <textbox key="{meta:getDescription($proj)}" max="60" tab="{meta:getOwnerId($proj)},{meta:getName($proj)}" width="425" required="true"/>
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'owner')" />:
                  </text>
                  <widget>
                    <autoComplete key="{meta:getOwnerId($proj)}" case="LOWER" tab="{meta:getIsActive($proj)},{meta:getDescription($proj)}" width="145" required="true"/>
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"active")' />:
                  </text>
                  <check key="{meta:getIsActive($proj)}" tab="{meta:getStartedDate($proj)},{meta:getOwnerId($proj)}" />
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'referenceTo')" />:
                  </text>
                  <widget>
                    <textbox key="{meta:getReferenceTo($proj)}" max="20" tab="{script:getName($scpt)},{meta:getCompletedDate($proj)}" width="145" />
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"beginDate")' />:
                  </text>
                  <calendar begin="0" end="2" key="{meta:getStartedDate($proj)}" tab="{meta:getCompletedDate($proj)},{meta:getIsActive($proj)}" width="90" required="true" />
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'scriptlet')" />:
                  </text>
                  <autoComplete case="LOWER" key="{script:getName($scpt)}" tab="parameterTable,{meta:getId($proj)}" width="180" field="Integer"/>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"endDate")' />:
                  </text>
                  <calendar begin="0" end="2" key="{meta:getCompletedDate($proj)}" tab="{meta:getReferenceTo($proj)},{meta:getStartedDate($proj)}" width="90" required="true"/>
                </row>
              </TablePanel>
              <VerticalPanel height="5" />
              <HorizontalPanel width="635">
                <widget valign="top">
                  <table key="parameterTable" maxRows="8" showScroll="ALWAYS" tab="{meta:getId($proj)},{script:getName($scpt)}" width="615" style="atozTable">
                    <col header="{resource:getString($constants,'parameter')}" key="{prmtrMeta:getParameter($prm)}" width="325">
                      <textbox required="true"/>
                    </col>
                    <col header="{resource:getString($constants,'operation')}" key="{prmtrMeta:getOperationId($prm)}" width="80"> 
                       <dropdown width="80" required="true"/>
                    </col>
                    <col header="{resource:getString($constants,'value')}" key="{prmtrMeta:getValue($prm)}" width="400">
                       <textbox required="true"/>
                    </col>
                  </table>
                </widget>
              </HorizontalPanel>
              <HorizontalPanel>
                  <widget>
                    <appButton key="addParameterButton" style="Button">
                      <HorizontalPanel>
                        <AbsolutePanel style="AddRowButtonImage" />
                        <widget>
                          <text>
                            <xsl:value-of select='resource:getString($constants,"addRow")' />
                          </text>
                        </widget>
                      </HorizontalPanel>
                    </appButton>
                  </widget>
                  <widget>
                    <appButton key="removeParameterButton" style="Button">
                      <HorizontalPanel>
                        <AbsolutePanel style="RemoveRowButtonImage" />
                        <widget>
                          <text>
                            <xsl:value-of select='resource:getString($constants,"removeRow")' />
                          </text>
                        </widget>
                      </HorizontalPanel>
                    </appButton>
                  </widget>
              </HorizontalPanel>
            </VerticalPanel>
          </VerticalPanel>
        </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
