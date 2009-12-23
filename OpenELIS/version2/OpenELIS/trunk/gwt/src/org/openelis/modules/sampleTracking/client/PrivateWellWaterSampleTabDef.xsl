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
  
    <screen id="envDomain" name="Well Water Info">
          <VerticalPanel width="98%" style="subform">
            <text style="FormTitle">
					Sample Info</text>
					<TablePanel style="Form" width="100%">
							<row>
								<text style="Prompt">Sampling Point:</text>
								<textbox key="samplingpoint" width="120px" field="String"/>	
								<text style="Prompt">Collector:</text>
								<textbox key="collector" width="200px" field="String"/>	
							</row>
							<row>
								<text style="Prompt">Owner:</text>
								<textbox key="owner" tab="??,??" width="200px" field="String"/>
								<text style="Prompt">Depth:</text>
								<textbox key="depth" width="80px" field="String"/>	
							</row>
							</TablePanel>
	                  	<VerticalPanel style="subform">
	                  	<text style="FormTitle">Results Reported To</text>
	                  <TablePanel style="Form">
                 		<row>
							<text style="Prompt">Name:</text>
							<widget colspan="3">
								<textbox key="name" tab="??,??" width="180px"/>		
							</widget>
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"aptSuite")'/>:</text>
							<widget colspan="3">
								<textbox case="UPPER" key="1" width="180px" max="30" field="String"/>
							</widget>		
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"address")'/>:</text>
							<widget colspan="3">
								<textbox case="UPPER" key="2" width="180px" max="30" field="String"/>
							</widget>		
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"city")'/>:</text>
							<widget colspan="3">
								<textbox case="UPPER" key="3" width="180px" max="30" field="String"/>
							</widget>		
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"state")'/>:</text>
							<dropdown case="UPPER" key="4" width="40px" tab="??,??" field="String"/>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"zipcode")'/>:</text>
							<textbox case="UPPER" key="5" width="73px" max="30" field="String"/>
						</row>
						<row>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"country")'/>:</text>
							<widget colspan="3">
								<dropdown key="6" width="180px" tab="??,??" field="String"/>
							</widget>
						</row>
					</TablePanel>
					</VerticalPanel>
			 </VerticalPanel>
					
    </screen>
</xsl:template>
</xsl:stylesheet>