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
                xmlns:meta="xalan://org.openelis.metamap.AnalyteMetaMap"
                xmlns:parentMeta="xalan://org.openelis.meta.AnalyteMeta"
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
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.AnalyteMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="parentMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.AnalyteMeta"/>
  </xalan:component>

  <xsl:template match="doc"> 
    <xsl:variable name="meta" select="meta:new()"/>
    <xsl:variable name="parentMeta" select="meta:getParentAnalyte($meta)"/>
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="Analyte" name="{resource:getString($constants,'analyte')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<HorizontalPanel spacing="0" padding="0">
			<!--left table goes here -->
			<CollapsePanel key="collapsePanel" height="235px" style="LeftSidePanel">
			   <!-- <azTable width="auto" key="azTable" maxRows="9" headers="{resource:getString($constants,'name')}" tablewidth="auto" colwidths="175">
					 <buttonPanel key="atozButtons">
	    			   <xsl:call-template name="aToZLeftPanelButtons"/>		
		    		 </buttonPanel>
				</azTable>
				-->
				<resultsTable key="azTable" height="235px" width="100%" showError="false">
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
			<VerticalPanel spacing="0">
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
			<VerticalPanel spacing="0" padding="0" style="WhiteContentPanel" height="235px" width="600px">
				<TablePanel style="Form">
					<row>
						<HorizontalPanel style="FormVerticalSpacing"/>
					</row>
					<row>								
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"name")'/>:</text>
						<textbox case="mixed" key="{meta:getName($meta)}" max="60" width="350px" tab="{parentMeta:getName($parentMeta)},{meta:getIsActive($meta)}"/>
					</row>
					<row>								
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"parentAnalyte")'/>:</text>
						<widget>	
							<autoComplete cat="parentAnalyte" key="{parentMeta:getName($parentMeta)}" case="mixed" serviceUrl="OpenELISServlet?service=org.openelis.modules.analyte.server.AnalyteService" width="184px" tab="{meta:getExternalId($meta)},{meta:getName($meta)}">
								<headers>Name</headers>
								<widths>194</widths>
							</autoComplete>
							<query>
								<textbox case="mixed" width="200px" tab="{meta:getExternalId($meta)},{meta:getName($meta)}"/>
							</query>
						</widget>
					</row>
					<row>								
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"externalId")'/>:</text>
						<textbox case="mixed" key="{meta:getExternalId($meta)}" max="20" width="150px" tab="{meta:getIsActive($meta)},{parentMeta:getName($parentMeta)}"/>
					</row>
					<row>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"active")'/>:</text>
						<check key="{meta:getIsActive($meta)}" tab="{meta:getName($meta)},{meta:getExternalId($meta)}"/>
					</row>
				</TablePanel>
				</VerticalPanel>
			</VerticalPanel>
		</HorizontalPanel>
	</display>
	<rpc key="display">
  	<integer key="{meta:getId($meta)}" required="false"/>
  	<string key="{meta:getName($meta)}" required="true"/>
  	<dropdown key="{parentMeta:getName($parentMeta)}" type="integer" required="false"/>
  	<string key="{meta:getExternalId($meta)}" required="false"/>
  	<check key="{meta:getIsActive($meta)}" required="false"/>
	</rpc>
	<!--
	<rpc key="query">
  	<queryNumber key="{meta:getId($meta)}" type="integer" required="false"/>
  	<queryString key="{meta:getName($meta)}" required="false"/>
  	<queryNumber key="{meta:getAnalyteGroupId($meta)}" type="integer" required="false"/>
  	<queryString key="{parentMeta:getName($parentMeta)}" required="false"/>
  	<queryString key="{meta:getExternalId($meta)}" required="false"/>
  	<queryCheck key="{meta:getIsActive($meta)}" required="false"/>
	</rpc>
	<rpc key="queryByLetter">
	<queryString key="{meta:getName($meta)}"/>
	</rpc>
	-->
</screen>
  </xsl:template>
</xsl:stylesheet>
