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
                xmlns:meta="xalan://org.openelis.metamap.MethodMetaMap"             
                extension-element-prefixes="resource"
                version="1.0">
	<xsl:import href="aToZTwoColumns.xsl"/>
	<xalan:component prefix="resource">
		<xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
	</xalan:component>
	<xalan:component prefix="locale">
		<xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
	</xalan:component>
	<xalan:component prefix="meta">
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.MethodMetaMap"/>
	</xalan:component>
	
	<xsl:template match="doc">	
	   <xsl:variable name="method" select="meta:new()"/>
		<xsl:variable name="language">
		<xsl:value-of select="locale"/>
		</xsl:variable>
		<xsl:variable name="props">
			<xsl:value-of select="props"/>
		</xsl:variable>
		<xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
		<screen id="Test" name="{resource:getString($constants,'method')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
			<display>
				<HorizontalPanel padding="0" spacing="0">
					<!--left table goes here -->
					<CollapsePanel key="collapsePanel" height="235px" style="LeftSidePanel">
					<!--
						<azTable colwidths="175"  key="azTable" maxRows="9" tablewidth="auto" headers="{resource:getString($constants,'method')}" width="100%">
							<buttonPanel key="atozButtons">
								<xsl:call-template name="aToZLeftPanelButtons"/>
							</buttonPanel>
						</azTable>
					-->
					   <resultsTable width="100%" key="azTable">
					       <buttonPanel key="atozButtons">
								<xsl:call-template name="aToZLeftPanelButtons"/>
						   </buttonPanel>
						   <table maxRows="9" width="auto">
						     <headers><xsl:value-of select="resource:getString($constants,'method')"/></headers>
						     <widths>175</widths>
						     <editors>
						       <label/>
						     </editors>
						     <fields>
						       <string/>
						     </fields>
						   </table>
					   </resultsTable>
					</CollapsePanel>					
					<VerticalPanel spacing="0" padding="0">
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
								<xsl:call-template name="deleteButton">
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
						 <VerticalPanel spacing="0" padding="0" height="235px" style="WhiteContentPanel">
							<TablePanel style="Form">
								<!--<row>
								   <text style="Prompt"><xsl:value-of select="resource:getString($constants,'id')"/>:</text>
								   <widget colspan = "6">	
									<textbox  key="{meta:getId($method)}" tab="{meta:getName($method)},{meta:getActiveEnd($method)}" width="50px"/>
								   </widget>
								</row> 	-->
								<row>
								  <text style="Prompt"><xsl:value-of select="resource:getString($constants,'name')"/>:</text>
								  <widget colspan = "6">	
									<textbox key="{meta:getName($method)}" case = "mixed" tab="{meta:getDescription($method)},{meta:getActiveEnd($method)}" max="20" width="145px"/>																									    								   
								  </widget>
								</row>
								<row>
								    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'description')"/>:</text>
									<widget colspan = "6">
										<textbox  key="{meta:getDescription($method)}" tab="{meta:getReportingDescription($method)},{meta:getName($method)}" case = "mixed" max="60" width="425px"/>
									</widget>
								</row>
								<row>
								 <text style="Prompt"><xsl:value-of select="resource:getString($constants,'reportDescription')"/>:</text>
									<widget colspan = "6">
										<textbox key="{meta:getReportingDescription($method)}" tab="{meta:getIsActive($method)},{meta:getDescription($method)}" case = "mixed" max="60" width="425px"/>
									</widget>
								</row>	
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"active")'/>:</text>
									<check key="{meta:getIsActive($method)}" tab="{meta:getActiveBegin($method)},{meta:getReportingDescription($method)}"/>									
								</row>		
								<row>
								 <text style="Prompt"><xsl:value-of select='resource:getString($constants,"beginDate")'/>:</text>
								 <calendar key="{meta:getActiveBegin($method)}" tab="{meta:getActiveEnd($method)},{meta:getIsActive($method)}" onChange="this" begin="0" end="2" width = "70px"/>																	
								 </row>
								 <row>
								 <text style="Prompt"><xsl:value-of select='resource:getString($constants,"endDate")'/>:</text>
								 <calendar key="{meta:getActiveEnd($method)}" tab="{meta:getName($method)},{meta:getActiveBegin($method)}" onChange="this" begin="0" end="2" width = "70px"/>
								</row>						
						     </TablePanel>						    	
						</VerticalPanel>   			
					</VerticalPanel>					
				</HorizontalPanel>
			</display>
			<rpc key="display">
			 <integer key="{meta:getId($method)}" required="false"/>
			 <string key="{meta:getName($method)}" max = "20" required="true" />
			 <check key="{meta:getIsActive($method)}" required="true"/>
			 <date key="{meta:getActiveBegin($method)}" begin="0" end="2" required="true"/>
			 <date key="{meta:getActiveEnd($method)}" begin="0" end="2" required="true"/>
			 <string key="{meta:getDescription($method)}" max="60" required="false"/>
			 <string key="{meta:getReportingDescription($method)}" max="60" required="false"/>			 			 								 
		  </rpc>
		  <!--
		  <rpc key="query">
			 <queryInteger key="{meta:getId($method)}"/>
			 <queryString key="{meta:getName($method)}" />			 
			 <queryString key="{meta:getDescription($method)}"/>
			 <queryString key="{meta:getReportingDescription($method)}" />					
			 <queryCheck key="{meta:getIsActive($method)}" />							
			</rpc>
			<rpc key="queryByLetter">
			 <queryString key="{meta:getName($method)}"/>				
			</rpc>
			-->
		</screen>
  </xsl:template>
</xsl:stylesheet>

