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
<xsl:stylesheet xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  				xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  				xsi:noNamespaceSchemaLocation="http://openelis.uhl.uiowa.edu/schema/ScreenSchema.xsd"
  				xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd"
                xmlns:locale="xalan://java.util.Locale"
                extension-element-prefixes="resource"
                version="1.0">
  
  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  <xsl:template match="doc">
  <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
  <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
  <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
 <screen id="TestAnalytePicker" name="{resource:getString($constants,'testAnalyteSelection')}" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">			
		<VerticalPanel spacing="0">		          
		 
		 <widget>
							<table maxRows = "14" width = "auto" key="testAnalyteTable" title="" showScroll="ALWAYS">
								<col key="name" width="320" sort="false" header="{resource:getString($constants,'analyte')}">
                                	<label width="320"/>
                                </col>
                                <col key="include" width="70" sort="false" header="{resource:getString($constants,'include')}">
                                	<check/>
                                </col>
							</table>
							
	     </widget>						
							
		            <AbsolutePanel spacing="0" style="BottomButtonPanelContainer" align="center">
		             <HorizontalPanel>
                                <widget halign="center">
                                 <appButton key="okButton" style="ButtonPanelButton">
                                  <HorizontalPanel>
                                    <AbsolutePanel style="CommitButtonImage" />
                                    <widget>
                                      <text>
                                        <xsl:value-of select='resource:getString($constants,"ok")' />
                                      </text>
                                    </widget>
                                  </HorizontalPanel>
                                </appButton>
                              </widget>
                              <widget halign="center">
                                 <appButton key="cancelButton" style="ButtonPanelButton">
                                  <HorizontalPanel>
                                    <AbsolutePanel style="AbortButtonImage" />
                                    <widget>
                                      <text>
                                        <xsl:value-of select='resource:getString($constants,"cancel")' />
                                      </text>
                                    </widget>
                                 </HorizontalPanel>
                                </appButton>
                              </widget>
                     </HorizontalPanel>
                    </AbsolutePanel>
   </VerticalPanel>				
</screen>
  </xsl:template>
</xsl:stylesheet>
