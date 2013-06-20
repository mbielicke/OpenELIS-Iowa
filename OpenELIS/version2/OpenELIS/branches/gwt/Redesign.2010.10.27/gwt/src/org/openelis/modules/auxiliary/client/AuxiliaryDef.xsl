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
  xmlns:meta="xalan://org.openelis.meta.AuxFieldGroupMeta">

  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" name="{resource:getString($constants,'auxiliaryPrompt')}">
      <HorizontalPanel padding="0" spacing="0">
<!--left table goes here -->
        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="225">
            <buttonGroup key="atozButtons">
              <xsl:call-template name="aToZLeftPanelButtons" />
            </buttonGroup>
            <VerticalPanel>
              <table key="atozTable" rows="25">
                <col width="175" header="{resource:getString($constants,'groupName')}">
                  <label field="String" />
                </col>
              </table>
              <widget halign="center">
                <HorizontalPanel>
                  <button key="atozPrev" style="Button" enabled="false">
                    <AbsolutePanel style="PreviousButtonImage" />
                  </button>
                  <button key="atozNext" style="Button" enabled="false">
                    <AbsolutePanel style="NextButtonImage" />
                  </button>
                </HorizontalPanel>
              </widget>
            </VerticalPanel>
          </HorizontalPanel>
        </CollapsePanel>
        <VerticalPanel padding="0" spacing="0">
<!--button panel code-->
          <AbsolutePanel spacing="0" style="ButtonPanelContainer">
            <HorizontalPanel>
              <xsl:call-template name="queryButton"/>
              <xsl:call-template name="previousButton"/>
              <xsl:call-template name="nextButton"/>
              <xsl:call-template name="buttonPanelDivider" />
              <xsl:call-template name="addButton"/>
              <xsl:call-template name="updateButton"/>
              <xsl:call-template name="buttonPanelDivider" />
              <xsl:call-template name="commitButton"/>
              <xsl:call-template name="abortButton"/>
              <xsl:call-template name="buttonPanelDivider" />
              <menu key="optionsMenu" selfShow="true">
                  <menuDisplay>
                    <button style="ButtonPanelButton" action="option">
                      <Grid cols="2">
                        <row>
                          <cell style="ScreenLabel,ButtonAdj" text="{resource:getString($constants,'options')}" />
                          <cell style="OptionsButtonImage" />
                        </row>
                      </Grid>
                    </button>
                  </menuDisplay>
                  <menuItem key="auxFieldGroupHistory" enabled="false" icon="historyIcon" display="{resource:getString($constants,'auxFieldGroupHistory')}" />
                  <menuItem key="auxFieldHistory" enabled="false" icon="historyIcon" display="{resource:getString($constants,'auxFieldHistory')}" />
                  <menuItem key="auxFieldValueHistory" enabled="false" icon="historyIcon" display="{resource:getString($constants,'auxFieldValueHistory')}" />
              </menu>
            </HorizontalPanel>
          </AbsolutePanel>
<!--end button panel-->
          <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
            <TablePanel style="Form">
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'groupName')" />:
                </text>
                <widget colspan="5">
                  <textbox key="{meta:getName()}" width="145" case="LOWER" max="20" tab="{meta:getDescription()},auxFieldValueTable" field="String" required="true" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'description')" />:
                </text>
                <widget colspan="5">
                  <textbox key="{meta:getDescription()}" width="425" max="60" tab="{meta:getIsActive()},{meta:getName()}" field="String" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"active")' />:
                </text>
                <check key="{meta:getIsActive()}" tab="{meta:getActiveBegin()},{meta:getDescription()}" />
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"beginDate")' />:
                </text>
                <calendar key="{meta:getActiveBegin()}" begin="0" end="2" width="90" pattern="{resource:getString($constants,'datePattern')}" tab="{meta:getActiveEnd()},{meta:getIsActive()}" required="true" />
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"endDate")' />:
                </text>
                <calendar key="{meta:getActiveEnd()}" begin="0" end="2" width="90" pattern="{resource:getString($constants,'datePattern')}" tab="auxFieldTable,{meta:getActiveBegin()}" required="true" />
              </row>
            </TablePanel>
            <HorizontalPanel width="630">
              <table key="auxFieldTable" width="620" rows="10" vscroll="ALWAYS" hscroll="ALWAYS" style="ScreenTableWithSides" tab="auxFieldValueTable,{meta:getActiveEnd()}">
                <col key="{meta:getFieldAnalyteName()}" width="250" header="{resource:getString($constants,'analyte')}">
                  <autoComplete width="300" required="true">
                    <col width="300" />
                  </autoComplete>
                </col>
                <col key="{meta:getFieldMethodName()}" width="70" header="{resource:getString($constants,'method')}">
                  <autoComplete width="300">
                    <col width="100" />
                  </autoComplete>
                </col>
                <col key="{meta:getFieldUnitOfMeasureId()}" width="50" header="{resource:getString($constants,'unit')}">
                  <dropdown width="80" field="Integer" />
                </col>
                <col key="{meta:getFieldIsActive()}" width="60" header="{resource:getString($constants,'active')}">
                  <check />
                </col>
                <col key="{meta:getFieldIsRequired()}" width="70" header="{resource:getString($constants,'required')}">
                  <check />
                </col>
                <col key="{meta:getFieldIsReportable()}" width="60" header="{resource:getString($constants,'auxReportable')}">
                  <check />
                </col>
                <col key="{meta:getFieldDescription()}" width="200" header="{resource:getString($constants,'description')}">
                  <textbox max="60" field="String" />
                </col>
                <col key="{meta:getFieldScriptletName()}" width="150" header="{resource:getString($constants,'scriptlet')}">
                  <autoComplete width="150">
                    <col width="150" />
                  </autoComplete>
                </col>
              </table>
            </HorizontalPanel>
            <widget style="TableButtonFooter">
              <HorizontalPanel>
                <button key="addAuxFieldButton" icon="AddRowButtonImage" text="{resource:getString($constants,'addRow')}" style="Button"/>
                <button key="removeAuxFieldButton" icon="RemoveRowButtonImage" text="{resource:getString($constants,'removeRow')}" style="Button"/>
              </HorizontalPanel>
            </widget>
            <table key="auxFieldValueTable" width="620" rows="6" vscroll="ALWAYS" hscroll="ALWAYS" style="ScreenTableWithSides" tab="{meta:getName()},auxFieldTable" >
              <col key="{meta:getFieldValueTypeId()}" width="110" align="left" header="{resource:getString($constants,'type')}">
                <dropdown width="110" field="Integer" required="true" />
              </col>
              <col key="{meta:getFieldValueValue()}" width="500" align="left" header="{resource:getString($constants,'value')}">
                <textbox max="80" field="String" />
              </col>
            </table>
            <HorizontalPanel>
              <widget style="TableButtonFooter">
                <HorizontalPanel>
                  <button key="addAuxFieldValueButton" icon="AddRowButtonImage" text="{resource:getString($constants,'addRow')}" style="Button"/>
                  <button key="removeAuxFieldValueButton" icon="RemoveRowButtonImage" text="{resource:getString($constants,'removeRow')}" style="Button"/>
                  <button key="dictionaryLookUpButton" icon="DictionaryButtonImage" text="{resource:getString($constants,'dictionary')}" style="Button"/>
                </HorizontalPanel>
              </widget>
            </HorizontalPanel>
          </VerticalPanel>
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>