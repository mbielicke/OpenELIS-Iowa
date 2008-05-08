<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                xmlns:analyteMeta="xalan://org.openelis.meta.AnalyteMeta"
                xmlns:parentAnalyteMeta="xalan://org.openelis.meta.AnalyteParentAnalyteMeta"
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZTwoColumns.xsl"/>   
  
  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
  <xalan:component prefix="analyteMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.AnalyteMeta"/>
  </xalan:component>
  
  <xalan:component prefix="parentAnalyteMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.AnalyteParentAnalyteMeta"/>
  </xalan:component>

  <xsl:template match="doc"> 
      <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="Analyte" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
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
										<textbox case="mixed" key="{analyteMeta:name()}" max="60" width="350px" tab="{analyteMeta:analyteGroup()},{analyteMeta:isActive()}"/>
									</widget>
								</row>
								<row>								
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"analyteGroup")'/>:</text>
									</widget>
									<widget>
										<textbox case="mixed" key="{analyteMeta:analyteGroup()}" width="200px" tab="{parentAnalyteMeta:name()},{analyteMeta:name()}"/>
									</widget>
								</row>
								<row>								
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"parentAnalyte")'/>:</text>
									</widget>
									<widget>
									<autoDropdown cat="parentAnalyte" key="{parentAnalyteMeta:name()}" case="mixed" serviceUrl="OpenELISServlet?service=org.openelis.modules.analyte.server.AnalyteService" width="184px" popWidth="198px" tab="{analyteMeta:externalId()},{analyteMeta:analyteGroup()}">
										<headers>Name</headers>
										<widths>194</widths>
										</autoDropdown>
										<query>
											<textbox case="mixed" width="200px" tab="{analyteMeta:externalId()},{analyteMeta:analyteGroup()}"/>
										</query>
									</widget>
								</row>
								<row>								
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"externalId")'/>:</text>
									</widget>
									<widget>
										<textbox case="mixed" key="{analyteMeta:externalId()}" max="20" width="150px" tab="{analyteMeta:isActive()},{parentAnalyteMeta:name()}"/>
									</widget>
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"active")'/>:</text>
									</widget>
									<widget>
										<check key="{analyteMeta:isActive()}" tab="{analyteMeta:name()},{analyteMeta:externalId()}"/>
									</widget>
								</row>
							</panel>
				</panel>
			</panel>
		</panel>
	</display>
	<rpc key="display">
  	<number key="{analyteMeta:id()}" type="integer" required="false"/>
  	<string key="{analyteMeta:name()}" required="true"/>
  	<number key="{analyteMeta:analyteGroup()}" type="integer" required="false"/>
  	<dropdown key="{parentAnalyteMeta:name()}" type="integer" required="false"/>
  	<string key="{analyteMeta:externalId()}" required="false"/>
  	<check key="{analyteMeta:isActive()}" required="false"/>
	</rpc>
	<rpc key="query">
  	<queryNumber key="{analyteMeta:id()}" type="integer" required="false"/>
  	<queryString key="{analyteMeta:name()}" required="false"/>
  	<queryNumber key="{analyteMeta:analyteGroup()}" type="integer" required="false"/>
  	<queryString key="{parentAnalyteMeta:name()}" required="false"/>
  	<queryString key="{analyteMeta:externalId()}" required="false"/>
  	<queryCheck key="{analyteMeta:isActive()}" required="false"/>
	</rpc>
	<rpc key="queryByLetter">
	<queryString key="{analyteMeta:name()}"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>