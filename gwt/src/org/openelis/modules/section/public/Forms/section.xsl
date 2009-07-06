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
                xmlns:meta="xalan://org.openelis.metamap.SectionMetaMap"   
                xmlns:org="xalan://org.openelis.meta.OrganizationMeta"         
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
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.SectionMetaMap"/>
	</xalan:component>
	<xalan:component prefix="org">
		<xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrganizationMeta"/>
	</xalan:component>
	
	<xsl:template match="doc">	
	   <xsl:variable name="sect" select="meta:new()"/>
	   <xsl:variable name="o" select="meta:getOrganization($sect)"/>
		<xsl:variable name="language">
		<xsl:value-of select="locale"/>
		</xsl:variable>
		<xsl:variable name="props">
			<xsl:value-of select="props"/>
		</xsl:variable>
		<xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
		<screen id="Section" name="{resource:getString($constants,'labSection')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
			<display>
				<HorizontalPanel padding="0" spacing="0">
					<!--left table goes here -->
					<CollapsePanel key="collapsePanel" height="225px" style="LeftSidePanel">

					   <resultsTable width="100%" key="azTable" showError="false">
					       <buttonPanel key="atozButtons">
								<xsl:call-template name="aToZLeftPanelButtons"/>
						   </buttonPanel>
						   <table maxRows="9" width="auto">
						     <headers><xsl:value-of select="resource:getString($constants,'name')"/></headers>
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
					<VerticalPanel spacing="0"  padding="0">
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
						 <VerticalPanel spacing="0" padding="0" height="235px" width="620px" style="WhiteContentPanel">
							<TablePanel style="Form"> 
								<row>
								  <text style="Prompt"><xsl:value-of select="resource:getString($constants,'name')"/>:</text>
								  <widget colspan = "6">	
									<textbox key="{meta:getName($sect)}" tab = "{meta:getDescription($sect)},{meta:getParentSectionId($sect)}" case = "lower" max="20" width="145px" />																									    								   
								  </widget>
								</row>
								<row>
								    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'description')"/>:</text>
									<widget colspan = "6">
										<textbox  key="{meta:getDescription($sect)}" tab = "{meta:getIsExternal($sect)},{meta:getName($sect)}" case = "mixed" max="60" width="425px" />
									</widget>
								</row>																	
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"external")'/>:</text>
									<check key="{meta:getIsExternal($sect)}" tab = "{org:getName($o)},{meta:getDescription($sect)}" />																										
								</row>		
								<row>
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'organization')"/>:</text>
									<widget>
										<autoComplete case="upper" cat="organization" key="{org:getName($o)}" tab = "{meta:getParentSectionId($sect)},{meta:getIsExternal($sect)}" serviceUrl="OpenELISServlet?service=org.openelis.modules.section.server.SectionService" width="285px">
								       		<headers><xsl:value-of select="resource:getString($constants,'name')"/>,<xsl:value-of select="resource:getString($constants,'street')"/>,<xsl:value-of select="resource:getString($constants,'city')"/>,<xsl:value-of select="resource:getString($constants,'st')"/></headers>
										    <widths>180,110,100,20</widths>
									  	</autoComplete>
									</widget>									
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'parentSection')"/>:</text>
								    <dropdown key="{meta:getParentSectionId($sect)}" tab = "{meta:getName($sect)},{org:getName($o)}" width="180px" />									
								</row>			
						     </TablePanel>			                                                       					        
							</VerticalPanel>						    	
						</VerticalPanel>   								
				</HorizontalPanel>
			</display>
			<rpc key="display">			 
			 <integer key="{meta:getId($sect)}" required="false"/>
			 <string key="{meta:getName($sect)}" max = "20" required="true" />
			 <check key="{meta:getIsExternal($sect)}" required="true"/>			 
			 <string key="{meta:getDescription($sect)}" max="60" required="false"/>			 
			 <dropdown key="{org:getName($o)}" case="lower" required="false" type="integer"/>
			 <dropdown key="{meta:getParentSectionId($sect)}" case="lower" required="false" type="integer"/>			
		  </rpc>
		</screen>
  </xsl:template>
</xsl:stylesheet>