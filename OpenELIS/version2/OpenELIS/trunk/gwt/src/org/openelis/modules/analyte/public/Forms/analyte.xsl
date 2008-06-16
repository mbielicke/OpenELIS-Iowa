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
		<panel layout="horizontal" spacing="0" padding="0" style="WhiteContentPanel" xsi:type="Panel">
			<!--left table goes here -->
			    <aToZ height="260px" width="100%" key="hideablePanel" maxRows="10" title="{resource:getString($constants,'name')}" tablewidth="auto" colwidths="175">
					 <buttonPanel key="atozButtons">
	    			   <xsl:call-template name="aToZLeftPanelButtons"/>		
		    		 </buttonPanel>
				</aToZ>
			<panel layout="vertical" spacing="0" xsi:type="Panel">
				<!--button panel code-->
		<panel xsi:type="Absolute" layout="absolute" spacing="0" style="ButtonPanelContainer">
			<widget>
    			<buttonPanel key="buttons">
    			<xsl:call-template name="queryButton"/>
    			<xsl:call-template name="previousButton"/>
    			<xsl:call-template name="nextButton"/>
    			<xsl:call-template name="buttonPanelDivider"/>
    			<xsl:call-template name="addButton"/>
    			<xsl:call-template name="updateButton"/>
    			<xsl:call-template name="deleteButton"/>
    			<xsl:call-template name="buttonPanelDivider"/>
    			<xsl:call-template name="commitButton"/>
    			<xsl:call-template name="abortButton"/>
				</buttonPanel>
 			</widget>
		</panel>
		<!--end button panel-->
					<panel layout="vertical" xsi:type="Panel">
							<panel key="secMod2" layout="table" style="Form" xsi:type="Table">
								<row>
									<panel layout="horizontal" xsi:type="Panel" style="FormVerticalSpacing"/>
								</row>
								<row>								
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"name")'/>:</text>
									</widget>
									<widget>
										<textbox case="mixed" key="{meta:getName($meta)}" max="60" width="350px" tab="{meta:getAnalyteGroupId($meta)},{meta:getIsActive($meta)}"/>
									</widget>
								</row>
								<row>								
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"analyteGroup")'/>:</text>
									</widget>
									<widget>
										<textbox case="mixed" key="{meta:getAnalyteGroupId($meta)}" width="200px" tab="{parentMeta:getName($parentMeta)},{meta:getName($meta)}"/>
									</widget>
								</row>
								<row>								
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"parentAnalyte")'/>:</text>
									</widget>
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
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"externalId")'/>:</text>
									</widget>
									<widget>
										<textbox case="mixed" key="{meta:getExternalId($meta)}" max="20" width="150px" tab="{meta:getIsActive($meta)},{parentMeta:getName($parentMeta)}"/>
									</widget>
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"active")'/>:</text>
									</widget>
									<widget>
										<check key="{meta:getIsActive($meta)}" tab="{meta:getName($meta)},{meta:getExternalId($meta)}"/>
									</widget>
								</row>
							</panel>
				</panel>
			</panel>
		</panel>
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