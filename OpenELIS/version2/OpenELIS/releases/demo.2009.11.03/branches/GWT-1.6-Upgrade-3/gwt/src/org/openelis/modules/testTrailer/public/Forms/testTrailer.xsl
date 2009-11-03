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
                xmlns:testTrailerMeta="xalan://org.openelis.metamap.TestTrailerMetaMap"
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZTwoColumns.xsl"/>   
  
  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
 <xalan:component prefix="testTrailerMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TestTrailerMetaMap"/>
  </xalan:component>

  <xsl:template match="doc"> 
    <xsl:variable name="meta" select="testTrailerMeta:new()"/>
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="TestTrailer" name="{resource:getString($constants,'trailerForTest')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<HorizontalPanel spacing="0" padding="0">
		<!--left table goes here -->
			<CollapsePanel key="collapsePanel" height="225px" style="LeftSidePanel">
			   <!--
			    <azTable colwidths="175" key="azTable" maxRows="9" tablewidth="auto" headers="{resource:getString($constants,'name')}" width="100%">
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
			<VerticalPanel spacing="0" padding="0" style="WhiteContentPanel" width="620px">
				<TablePanel style="Form">
					<row>
						<HorizontalPanel style="FormVerticalSpacing"/>
					</row>
					<row>								
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"name")'/>:</text>
						<textbox case="lower" key="{testTrailerMeta:getName($meta)}" max="60" width="150px" tab="{testTrailerMeta:getDescription($meta)},{testTrailerMeta:getText($meta)}"/>
					</row>
					<row>								
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"description")'/>:</text>
						<textbox case="mixed" key="{testTrailerMeta:getDescription($meta)}" max="60" width="300px" tab="{testTrailerMeta:getText($meta)},{testTrailerMeta:getName($meta)}"/>
					</row>
					<row>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"text")'/>:</text>
						<textarea key="{testTrailerMeta:getText($meta)}" width="300px" height="145px" tab="{testTrailerMeta:getName($meta)},{testTrailerMeta:getDescription($meta)}"/>
					</row>
				</TablePanel>
				</VerticalPanel>
			</VerticalPanel>
		</HorizontalPanel>
	</display>
	<rpc key="display">
  	<integer key="{testTrailerMeta:getId($meta)}" required="false"/>
  	<string key="{testTrailerMeta:getName($meta)}" max="20" required="true"/>
  	<string key="{testTrailerMeta:getDescription($meta)}" max="60" required="true"/>
  	<string key="{testTrailerMeta:getText($meta)}" required="true"/>
	</rpc>
	<!--
	<rpc key="query">
 	<queryNumber key="{testTrailerMeta:getId($meta)}" type="integer" required="false"/>
  	<queryString key="{testTrailerMeta:getName($meta)}" max="20" required="true"/>
  	<queryString key="{testTrailerMeta:getDescription($meta)}" max="60" required="true"/>
  	<queryString key="{testTrailerMeta:getText($meta)}" required="true"/>

	</rpc>
	<rpc key="queryByLetter">
		<queryString key="{testTrailerMeta:getName($meta)}"/>
	</rpc>
	-->
</screen>
  </xsl:template>
</xsl:stylesheet>
