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
  
    <screen id="AuxDataTab" name="{resource:getString($constants,'auxData')}">
      <VerticalPanel padding="0" spacing="0">
               <TablePanel padding="0" spacing="0">
                <row>
                <widget colspan="6">
	              <table key="auxValsTable" title="" width="auto" maxRows="6" showScroll="ALWAYS">
	                 <col width="85" header="{resource:getString($constants,'reportable')}">
	                 <check/>
	                 </col>
	                  <col width="300" header="{resource:getString($constants,'name')}">
	                    <label />
	                  </col>
	                  <col width="303" class="org.openelis.modules.sample.client.AuxTableColumn" header="{resource:getString($constants,'value')}">
	                  	<label/>
	                  </col>
	                </table>
	                </widget>
                </row>
                <row>
                <widget colspan="6" style="TableButtonFooter">
                <HorizontalPanel>
                  <appButton key="removeAuxButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style="RemoveRowButtonImage" />
                      <text>
                        <xsl:value-of select="resource:getString($constants,'removeRow')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                  <appButton key="addAuxButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style="PickerButtonImage" />
                      <text>
                        <xsl:value-of select="resource:getString($constants,'auxGroups')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                </HorizontalPanel>
                </widget>
                </row>
                </TablePanel>
                <TablePanel style="Form">
                <row>
	                <text style="Prompt">
	                	<xsl:value-of select="resource:getString($constants,'description')" />:
	              	</text>
	              	<widget colspan="3">
              			<textbox key="auxDesc" width="275px" style="ScreenTextboxDisplayOnly"/>
              		</widget>
	                <text style="Prompt">
	                	<xsl:value-of select="resource:getString($constants,'method')" />:
	              	</text>
              		<textbox key="auxMethod" width="125px" style="ScreenTextboxDisplayOnly"/>
              		<text style="Prompt">
	                	<xsl:value-of select="resource:getString($constants,'unit')" />:
	              	</text>
              		<textbox key="auxUnits" width="125px" style="ScreenTextboxDisplayOnly"/>
                </row>
                </TablePanel>
             </VerticalPanel>      
    </screen>
  </xsl:template>
</xsl:stylesheet>    