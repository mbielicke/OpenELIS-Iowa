<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                xmlns:meta="xalan://org.openelis.newmeta.AnalyteMetaMap"
                xmlns:parentMeta="xalan://org.openelis.newmeta.AnalyteMeta"
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
    <xalan:script lang="javaclass" src="xalan://org.openelis.newmeta.AnalyteMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="parentMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.newmeta.AnalyteMeta"/>
  </xalan:component>

  <xsl:template match="doc"> 
    <xsl:variable name="meta" select="meta:new()"/>
    <xsl:variable name="parentMeta" select="meta:getParentAnalyte($meta)"/>
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="Analyte" name="{resource:getString($constants,'analyte')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<HorizontalPanel spacing="0" padding="0" style="WhiteContentPanel">
			<!--left table goes here -->
			<CollapsePanel key="collapsePanel" height="235px">
			    <azTable width="auto" key="azTable" maxRows="10" title="{resource:getString($constants,'name')}" tablewidth="auto" colwidths="175">
					 <buttonPanel key="atozButtons">
	    			   <xsl:call-template name="aToZLeftPanelButtons"/>		
		    		 </buttonPanel>
				</azTable>
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
			<VerticalPanel>
				<TablePanel style="Form">
					<row>
						<HorizontalPanel style="FormVerticalSpacing"/>
					</row>
					<row>								
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"name")'/>:</text>
						<textbox case="mixed" key="{meta:getName($meta)}" max="60" width="350px" tab="{meta:getAnalyteGroupId($meta)},{meta:getIsActive($meta)}"/>
					</row>
					<row>								
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"analyteGroup")'/>:</text>
						<textbox case="mixed" key="{meta:getAnalyteGroupId($meta)}" width="200px" tab="{parentMeta:getName($parentMeta)},{meta:getName($meta)}"/>
					</row>
					<row>								
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"parentAnalyte")'/>:</text>
						<widget>	
							<autoDropdown cat="parentAnalyte" key="{parentMeta:getName($parentMeta)}" case="mixed" serviceUrl="OpenELISServlet?service=org.openelis.modules.analyte.server.AnalyteService" width="184px" tab="{meta:getExternalId($meta)},{meta:getAnalyteGroupId($meta)}">
								<headers>Name</headers>
								<widths>194</widths>
							</autoDropdown>
							<query>
								<textbox case="mixed" width="200px" tab="{meta:getExternalId($meta)},{meta:getAnalyteGroupId($meta)}"/>
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
  	<number key="{meta:getId($meta)}" type="integer" required="false"/>
  	<string key="{meta:getName($meta)}" required="true"/>
  	<number key="{meta:getAnalyteGroupId($meta)}" type="integer" required="false"/>
  	<dropdown key="{parentMeta:getName($parentMeta)}" type="integer" required="false"/>
  	<string key="{meta:getExternalId($meta)}" required="false"/>
  	<check key="{meta:getIsActive($meta)}" required="false"/>
	</rpc>
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
</screen>
  </xsl:template>
</xsl:stylesheet>