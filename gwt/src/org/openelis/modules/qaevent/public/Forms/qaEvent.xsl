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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                xmlns:meta="xalan://org.openelis.metamap.QaEventMetaMap"
                xmlns:testMeta="xalan://org.openelis.metamap.QaEventTestMetaMap"
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZOneColumn.xsl"/>

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
  <xalan:component prefix="meta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.QaEventMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="testMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.QaEventTestMetaMap"/>
  </xalan:component>

  <xsl:template match="doc"> 
    <xsl:variable name="qae" select="meta:new()"/>
    <xsl:variable name="test" select="meta:getTest($qae)"/>
    <xsl:variable name="method" select="testMeta:getMethod($test)"/>
    <xsl:variable name="language">
     <xsl:value-of select="locale"/>
    </xsl:variable>
    <xsl:variable name="props">
     <xsl:value-of select="props"/>
    </xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id= "QAEvents" name="{resource:getString($constants,'QAEvent')}" serviceUrl= "OpenElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
<display>
		 <HorizontalPanel spacing= "0" padding= "0">  
 					<CollapsePanel key="collapsePanel" style="LeftSidePanel">
						<azTable colwidths ="100,65,65" height="425px" key="azTable" maxRows="19" tablewidth="auto" title="" width="100%" headers = "{resource:getString($constants,'name')},{resource:getString($constants,'test')},{resource:getString($constants,'method')}">
							<buttonPanel key="atozButtons">
								<xsl:call-template name="aToZLeftPanelButtons"/>
							</buttonPanel>
						</azTable>
					</CollapsePanel>
  		<VerticalPanel padding="0" spacing="0">
   <!--button panel code-->
		<AbsolutePanel spacing="0" style="ButtonPanelContainer">
						<buttonPanel key="buttons">
								<xsl:call-template name="queryButton">
									<xsl:with-param name="language">
										<xsl:value-of select="language"/>
									</xsl:with-param>
								</xsl:call-template>
								<xsl:call-template name="previousButton">
									<xsl:with-param name="language">
										<xsl:value-of select="language"/>
									</xsl:with-param>
								</xsl:call-template>
								<xsl:call-template name="nextButton">
									<xsl:with-param name="language">
										<xsl:value-of select="language"/>
									</xsl:with-param>
								</xsl:call-template>
								<xsl:call-template name="buttonPanelDivider"/>
								<xsl:call-template name="addButton">
									<xsl:with-param name="language">
										<xsl:value-of select="language"/>
									</xsl:with-param>
								</xsl:call-template>
								<xsl:call-template name="updateButton">
									<xsl:with-param name="language">
										<xsl:value-of select="language"/>
									</xsl:with-param>
								</xsl:call-template>
								<xsl:call-template name="buttonPanelDivider"/>
								<xsl:call-template name="commitButton">
									<xsl:with-param name="language">
										<xsl:value-of select="language"/>
									</xsl:with-param>
								</xsl:call-template>
								<xsl:call-template name="abortButton">
									<xsl:with-param name="language">
										<xsl:value-of select="language"/>
									</xsl:with-param>
								</xsl:call-template>
							</buttonPanel> 			
		</AbsolutePanel>
		<!--end button panel-->
   
  <VerticalPanel spacing="0" padding="0" style="WhiteContentPanel">
  <TablePanel style="Form">
    <row>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"name")'/>:</text>
      <textbox key = "{meta:getName($qae)}" max = "20" width= "145px" case = "lower" tab="{meta:getDescription($qae)},{meta:getReportingText($qae)}"/>
     </row>     
     <row>     
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"description")'/>:</text>
      <textbox case= "mixed" max = "60"  key= "{meta:getDescription($qae)}" width= "425px" tab="{meta:getTypeId($qae)},{meta:getName($qae)}"/>
    </row>
     <row>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"type")'/>:</text> 		
	  <dropdown key="{meta:getTypeId($qae)}" width = "120px" case="mixed" tab="{meta:getTestId($qae)},{meta:getDescription($qae)}"/>									        
     </row>
     <row>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"test")'/>:</text>
	  <dropdown key="{meta:getTestId($qae)}" width = "140px"  case="mixed" tab="{meta:getIsBillable($qae)},{meta:getTypeId($qae)}"/>
	  </row>		
	  						
      <row>           
        <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"billable")'/>:</text>
        <check key= "{meta:getIsBillable($qae)}" tab="{meta:getReportingSequence($qae)},{meta:getTestId($qae)}"/>
      </row> 
      <row>       
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"sequence")'/>:</text>   
      <textbox key= "{meta:getReportingSequence($qae)}" width= "40px" tab="{meta:getReportingText($qae)},{meta:getIsBillable($qae)}"/> 
     </row>
			
	   <row>
	    <widget valign = "top"> 
		   <text style="Prompt"><xsl:value-of select='resource:getString($constants,"text")'/>:</text>
		</widget>
	    <widget halign = "center">
		  <textarea width="425px" height="250px" case="mixed" key="{meta:getReportingText($qae)}" tab="{meta:getName($qae)},{meta:getReportingSequence($qae)}"/>
	    </widget> 
	   </row>								          
                                 					                         
   </TablePanel>            
                            
						
  </VerticalPanel>
 </VerticalPanel>
 </HorizontalPanel>
</display>
							  
<rpc key= "display">
 <integer key="{meta:getId($qae)}" type="integer" required = "false" />
 <string key="{meta:getName($qae)}" max = "20" required = "true"/>
 <integer key="{meta:getReportingSequence($qae)}"  type="integer" required = "false" />
 <string key="{meta:getDescription($qae)}" max = "60" required = "false" /> 	 
 <check key= "{meta:getIsBillable($qae)}" required = "false" />
 <string key="{meta:getReportingText($qae)}" required = "true"/>
 <dropdown key="{meta:getTestId($qae)}" type="integer" required = "false" />
 <dropdown key="{meta:getTypeId($qae)}" type="integer" required = "true"/>
</rpc>
					   
<rpc key= "query">     
 <queryString key="{meta:getName($qae)}" />
 <queryInteger key="{meta:getReportingSequence($qae)}" type="integer" />
 <queryString key="{meta:getDescription($qae)}"  /> 	
 <dropdown key="{meta:getTypeId($qae)}" type="integer"/> 
 <dropdown key="{meta:getTestId($qae)}" type="integer"/>
 <queryCheck key="{meta:getIsBillable($qae)}"/>
 <queryString key="{meta:getReportingText($qae)}"/>
</rpc>

<rpc key= "queryByLetter">     
 <queryString key="{meta:getName($qae)}"/>
</rpc>
 
</screen>
</xsl:template>
</xsl:stylesheet> 
