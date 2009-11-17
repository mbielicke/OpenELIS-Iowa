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
  <xsl:import href="IMPORT/aToZTwoColumns.xsl"/>
  
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
 <screen id="DictionaryEntryPicker" name="{resource:getString($constants,'dictionaryEntrySelection')}" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">	
		<VerticalPanel >
		<HorizontalPanel spacing="0">
			<!--left table goes here -->
			 <buttonGroup height = "100%" key='atozButtons'>
                <xsl:call-template name='aToZLeftPanelButtons' />
              </buttonGroup>	
              						
		<VerticalPanel style="WhiteContentPanel">
		<HorizontalPanel>	 
         <TablePanel style = "Form"> 
         <row>
          <text style="Prompt"><xsl:value-of select="resource:getString($constants,'selectCategory')"/>:</text>
          <widget colspan = "3">	 		
		   <dropdown key="category" popWidth = "253" field="Integer" width="253" />				  
		  </widget> 
		 </row> 
		<row>
		  <text style="Prompt"><xsl:value-of select="resource:getString($constants,'enterSearch')"/>:</text>		  
		   <textbox key="findTextBox" width="253" />		   	  
		</row>
	   </TablePanel>	
	   <VerticalPanel>
		<VerticalPanel height = "28px"/>
		 <widget halign="center">
                               <appButton action="find" style="Button" key="findButton" >
								<HorizontalPanel>
            						<AbsolutePanel style="FindButtonImage"/>
                						<text>
                						  <xsl:value-of select='resource:getString($constants,"search")'/>
                					    </text>
								</HorizontalPanel>
		   						</appButton>	
		   					  </widget>	
		 </VerticalPanel>	
		</HorizontalPanel> 
		<VerticalPanel spacing="0">
		 <widget>
							<table maxRows = "6" width = "auto" key="dictEntTable" multiSelect = "true" title="" showScroll="ALWAYS" style="ScreenTableWithSides">
								<col key="entry" width="250" sort="false" header="{resource:getString($constants,'entry')}">
                                	<label width="250"/>
                                </col>
								<col key="category" width="176" sort="false" header="{resource:getString($constants,'category')}">
                                	<label width="176"/>
                                </col>
							</table>
							
	     </widget>	
	     <widget>
	     	<VerticalPanel height = "15"/>
	     </widget>
	     </VerticalPanel>		     
   </VerticalPanel>	   
  </HorizontalPanel>  
  <VerticalPanel spacing="0" width="503px">								
		            <AbsolutePanel spacing="0" style="BottomButtonPanelContainer" align="center">
		             <HorizontalPanel>
                                <xsl:call-template name="okButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="cancelButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>                              
                     </HorizontalPanel>
                    </AbsolutePanel>		            
    </VerticalPanel>
  </VerticalPanel>					
</screen>
  </xsl:template>
</xsl:stylesheet>					
					
					