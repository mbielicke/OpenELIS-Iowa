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
  
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
  
    <screen id="QaEventsTab" name="{resource:getString($constants,'qaEvents')}">
      <VerticalPanel padding="0" spacing="0">
              	<TablePanel padding="0" spacing="0" height="100%" style="TabSubForm">
              	<row>
	                 <table key="sampleQATable" style="ScreenTableWithSides" title="" width="auto" maxRows="8" showScroll="ALWAYS">
	                  <col width="172" key="{meta:getSampleSubQaName()}" header="{resource:getString($constants,'sampleQAEvent')}">
	                  	<label/>
	                  </col>
	                  <col width="90" key="{meta:getSampleQaTypeId()}" header="{resource:getString($constants,'type')}">
		                  <dropdown width="75px" popWidth="75px" field="Integer"/>
	                  </col>
	                  <col width="61" key="{meta:getSampleQaIsBillable()}" header="{resource:getString($constants,'billable')}">
	                  	<check/>
	                  </col>
	                </table>
	                <widget rowspan="3">
	                	<AbsolutePanel style="Divider"/>
	                </widget>
	                <table key="analysisQATable" style="ScreenTableWithSides" title="" width="auto" maxRows="8" showScroll="ALWAYS">
                  		<col width="172" key="{meta:getAnalysisAubQaName()}" header="{resource:getString($constants,'analysisQAEvent')}">
                  		<label/>
	                  </col>
	                  <col width="90" key="{meta:getAnalysisQaTypeId()}" header="{resource:getString($constants,'type')}">
		                  <dropdown width="75px" popWidth="75px" field="Integer"/>
	                  </col>
	                  <col width="60" key="meta:getAnalysisQaIsBillable()" header="{resource:getString($constants,'billable')}">
	                  	<check/>
	                  </col>
                </table>
                </row>
                <row>
                <widget style="TableButtonFooter">
	                <HorizontalPanel>
	                  <appButton key="removeSampleQAButton" style="Button">
	                    <HorizontalPanel>
	                      <AbsolutePanel style="RemoveRowButtonImage" />
	                      <text>
	                        <xsl:value-of select="resource:getString($constants,'removeRow')" />
	                      </text>
	                    </HorizontalPanel>
	                  </appButton>
	                   <appButton key="sampleQAPicker" style="Button">
	                    <HorizontalPanel>
	                      <AbsolutePanel style="PickerButtonImage" />
	                      <text>
	                        <xsl:value-of select="resource:getString($constants,'qaEvents')" />
	                      </text>
	                    </HorizontalPanel>
	                  </appButton>
	                </HorizontalPanel>
	                </widget>
                <widget style="TableButtonFooter">
                <HorizontalPanel>
                  <appButton key="removeAnalysisQAButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style="RemoveRowButtonImage" />
                      <text>
                        <xsl:value-of select="resource:getString($constants,'removeRow')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                  <appButton key="analysisQAPicker" style="Button">
	                    <HorizontalPanel>
	                      <AbsolutePanel style="PickerButtonImage" />
	                      <text>
	                        <xsl:value-of select="resource:getString($constants,'qaEvents')" />
	                      </text>
	                    </HorizontalPanel>
	                  </appButton>
                </HorizontalPanel>
                </widget>
                </row>
                </TablePanel>       
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>           