
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
  xmlns:meta="xalan://org.openelis.meta.SampleMeta">

  <xsl:import href="IMPORT/button.xsl" />
  <xsl:variable name="language" select="doc/locale" />
  <xsl:variable name="props" select="doc/props" />
  <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
  <xsl:template match="doc">
    <screen id="QuickEntry" name="{resource:getString($constants,'quickEntry')}">
      <VerticalPanel padding="0" spacing="0">

<!--button panel code-->

        <AbsolutePanel spacing="0" style="ButtonPanelContainer">
          <HorizontalPanel>
            <xsl:call-template name="commitButton" />
            <xsl:call-template name="abortButton" />
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
                <menuPanel layout="vertical" position="below" style="buttonMenuContainer">
                  <xsl:call-template name="historyMenuItem" />
                </menuPanel>
              </menuItem>
            </menuPanel>
          </HorizontalPanel>
        </AbsolutePanel>

<!--end button panel code-->

        <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
        <HorizontalPanel width="100%">
          <TablePanel style="Form">
          	<row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'entry')" />:
              </text>
              <textbox key="entry" width="135px" field="Integer"/>
              </row>
          </TablePanel>
          <widget halign="right">
          <TablePanel style="Form">
          <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'accessionNum')" />:
              </text>
              <textbox key="accessionNumber" width="75px" field="Integer" style="ScreenTextboxDisplayOnly"/>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'received')" />:
              </text>
              <calendar key="receivedDate" begin="0" end="4" max="0" width="120px" style="ScreenTextboxDisplayOnly" pattern="{resource:getString($constants,'dateTimePattern')}"/>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'tubeNum')" />:
              </text>
              <textbox key="tubeNumber" width="75px" style="ScreenTextboxDisplayOnly" field="Integer"/>
              </row>
              <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'testMethodSampleType')" />:
              </text>
              <widget colspan="5">
              	<dropdown key="testMethodSampleType" width="400" popWidth="400" style="ScreenTextboxDisplayOnly" field="Integer" />
              </widget>
              </row>
          </TablePanel>
          </widget>
          </HorizontalPanel>
          <table key="quickEntryTable" width="auto" maxRows="15" showScroll="ALWAYS" title="">
            <col width="90" header="{resource:getString($constants,'accessionNum')}">
              <label />
            </col>
            <col width="130" header="{resource:getString($constants,'received')}">
              <label />
            </col>
            <col width="80" header="{resource:getString($constants,'tubeNum')}">
              <label />
            </col>
            <col width="150" header="{resource:getString($constants,'test')}">
              <label />
            </col>
            <col width="150" header="{resource:getString($constants,'method')}">
              <label />
            </col>
            <col width="150" header="{resource:getString($constants,'sampleType')}">
              <label />
            </col>
            </table>
             <widget style="TableButtonFooter">
                      <appButton key="removeRowButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="RemoveRowButtonImage" />
                          <text>
                            <xsl:value-of select="resource:getString($constants,'removeRow')" />
                          </text>
                        </HorizontalPanel>
                      </appButton>
                  </widget>
      </VerticalPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
