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
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd">

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
            <!-- 
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
            </menuPanel>-->
          </HorizontalPanel>
        </AbsolutePanel>

<!--end button panel code-->

        <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
          <TablePanel>
          <row>
          		<text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'entry')" />:
              </text>
              <textbox key="entry" width="158px" max="20" case="UPPER" field="String" tab="entry, entry"/>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'accessionNum')" />:
              </text>
              <textbox key="accessionNumber" width="75px" field="Integer" tab="entry, entry"/>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'tubeNum')" />:
              </text>
              <textbox key="tubeNumber" width="75px" field="Integer" tab="entry, entry"/>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'received')" />:
              </text>
              <calendar key="receivedDate" begin="0" end="4" minValue="364" maxValue="0" width="125px" pattern="{resource:getString($constants,'dateTimePattern')}" tab="entry, entry"/>
              </row>
              <row>
              <widget colspan="2">
              <HorizontalPanel/>
              </widget>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'testMethodSampleType')" />:
              </text>
              <widget colspan="5">
              	<dropdown key="testMethodSampleType" width="419" popWidth="auto" field="String" tab="entry, entry"/>
              </widget>
              </row>
              <row>
              <widget colspan="2">
              <HorizontalPanel/>
              </widget>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'testSection')" />:
              </text>
              <widget colspan="5">
              	<dropdown key="testSection" width="419" popWidth="auto" field="Integer" tab="entry, entry"/>
              </widget>
              </row>
              <row>
              <widget colspan="2">
              <HorizontalPanel/>
              </widget>
              <text style="Prompt">
              	<xsl:value-of select="resource:getString($constants,'currentDateTime')" />:
              </text>
              <check key="currentDateTime" tab="entry, entry"/>
         	  <text style="Prompt">
	        	<xsl:value-of select="resource:getString($constants,'printLabels')" />:
	          </text>
              	<check key="printLabels" tab="entry, entry"/>
             <text style="Prompt">
	        	<xsl:value-of select="resource:getString($constants,'printer')" />:
	    	</text>
		    <dropdown key="printer" width="121" popWidth="auto" field="Integer" tab="entry, entry"/>
              </row>
          </TablePanel>
          <table key="quickEntryTable" width="auto" maxRows="15" style="ScreenTableWithSides" showScroll="ALWAYS" title="" tab="entry,entry">
            <col width="90" header="{resource:getString($constants,'accessionNum')}">
              <label field="Integer"/>
            </col>
            <col width="130" header="{resource:getString($constants,'received')}">
              <calendar begin="0" end="4" maxValue="0" pattern="{resource:getString($constants,'dateTimePattern')}"/>
            </col>
            <col width="160" header="{resource:getString($constants,'test')}">
              <label field="String"/>
            </col>
            <col width="150" header="{resource:getString($constants,'method')}">
              <label field="String"/>
            </col>
            <col width="150" header="{resource:getString($constants,'sampleType')}">
              <label field="String"/>
            </col>
            <col width="80" header="{resource:getString($constants,'tubeNum')}">
              <label field="String"/>
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
