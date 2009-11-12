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
  xmlns:dictionary="xalan://org.openelis.metamap.DictionaryMetaMap"
  xmlns:meta="xalan://org.openelis.metamap.CategoryMetaMap"
  xmlns:relentry="xalan://org.openelis.meta.DictionaryMeta">

  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource" />
  </xalan:component>
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale" />
  </xalan:component>
  <xalan:component prefix="meta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.CategoryMetaMap" />
  </xalan:component>
  <xalan:component prefix="dictionary">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.DictionaryMetaMap" />
  </xalan:component>
  <xalan:component prefix="relentry">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.DictionaryMeta" />
  </xalan:component>
  <xsl:template match="doc">
    <xsl:variable name="cat" select="meta:new()" />
    <xsl:variable name="dictNew" select="meta:getDictionary($cat)" />
    <xsl:variable name="rel" select="dictionary:getRelatedEntry($dictNew)" />
    <xsl:variable name="language">
      <xsl:value-of select="locale" />
    </xsl:variable>
    <xsl:variable name="props">
      <xsl:value-of select="props" />
    </xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" id="Dictionary" name="{resource:getString($constants,'dictionary')}">
      <HorizontalPanel padding="0" spacing="0">
        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="225px">
            <buttonGroup key="atozButtons">
              <xsl:call-template name="aToZLeftPanelButtons" />
            </buttonGroup>
            <VerticalPanel>
              <table key="atozTable" width="auto" maxRows="19" style="atozTable">
                <col width="175" header="{resource:getString($constants,'catName')}">
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
              <xsl:call-template name="buttonPanelDivider" />
              <menuPanel key="optionsMenu" layout="vertical" style="topBarItemHolder">
                <menuItem>
                  <menuDisplay>
                    <appButton style="ButtonPanelButton" action="option">
                      <HorizontalPanel>
                        <text>
                          <xsl:value-of select='resource:getString($constants,"options")' />
                        </text>
                        <AbsolutePanel width="20px" height="20px" style="OptionsButtonImage" />
                      </HorizontalPanel>
                    </appButton>
                  </menuDisplay>
                  <menuPanel layout="vertical" position="below" style="topMenuContainer">
                    <xsl:call-template name="historyMenuItem" />
                  </menuPanel>
                </menuItem>
              </menuPanel>
            </HorizontalPanel>
          </AbsolutePanel>

<!--end button panel-->

          <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
            <TablePanel style="Form">
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"catName")' />:
                </text>
                <widget colspan = "5">
                	<textbox key="{meta:getName($cat)}" required = "true" width="355px" case="MIXED" max="50" tab="{meta:getDescription($cat)},{meta:getSystemName($cat)}" />
                </widget>	
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"description")' />:
                </text>
                <widget colspan = "3">                
                	<textbox key="{meta:getDescription($cat)}" width="425px" case="MIXED" max="60" tab="{meta:getSectionId($cat)},{meta:getName($cat)}" />
              	</widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"section")' />:
                </text>
                <dropdown key="{meta:getSectionId($cat)}" width="100px" case="LOWER" tab="{meta:getSystemName($cat)},{meta:getDescription($cat)}"/>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"systemName")' />:
                </text>
                <textbox key="{meta:getSystemName($cat)}" required = "true" width="215px" case="MIXED" max="30" tab="{meta:getIsSystem($cat)},{meta:getSectionId($cat)}" />
              </row>
              <row>
                <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"system")' />:
                  </text>
                  <check key="{meta:getIsSystem($cat)}" tab="dictEntTable,{meta:getSystemName($cat)}" />
                </row>
            </TablePanel>
            <VerticalPanel>
              <widget valign="top">
                <table key="dictEntTable" width="auto" maxRows="13" showScroll="ALWAYS" tab="{meta:getName($cat)},{meta:getSystemName($cat)}" title="" style="atozTable">
                  <col key="{dictionary:getIsActive($dictNew)}" width="60" header="{resource:getString($constants,'active')}">
                    <check>Y</check>
                  </col>
                  <col key="{dictionary:getSystemName($dictNew)}" width="120"  header="{resource:getString($constants,'systemName')}">
                    <textbox case="LOWER" max="30" field="String" />
                  </col>
                  <col key="{dictionary:getLocalAbbrev($dictNew)}" width="120"  header="{resource:getString($constants,'abbr')}">
                    <textbox max="10" field="String" />
                  </col>
                  <col key="{dictionary:getEntry($dictNew)}" width="180" sort = "true" header="{resource:getString($constants,'entry')}">
                    <textbox max="255" field="String" required = "true"/>
                  </col>
                  <col key="{relentry:getEntry($rel)}" width="150" header="{resource:getString($constants,'relEntry')}">
                    <autoComplete width="130" field="Integer">
                      <col width="200" />
                    </autoComplete>
                  </col>
                </table>
              </widget>
              <HorizontalPanel style="TableFooterPanel">
                <widget halign="center">
                  <appButton key="addEntryButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style="AddRowButtonImage" />
                      <text>
                        <xsl:value-of select='resource:getString($constants,"addRow")' />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                </widget>
                <widget halign="center">
                  <appButton key="removeEntryButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style="RemoveRowButtonImage" />
                      <text>
                        <xsl:value-of select='resource:getString($constants,"removeRow")' />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                </widget>
              </HorizontalPanel>
            </VerticalPanel>
          </VerticalPanel>
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
