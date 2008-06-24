<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                xmlns:testTrailerMeta="xalan://org.openelis.newmeta.TestTrailerMetaMap"
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
    <xalan:script lang="javaclass" src="xalan://org.openelis.newmeta.TestTrailerMetaMap"/>
  </xalan:component>

  <xsl:template match="doc"> 
    <xsl:variable name="meta" select="testTrailerMeta:new()"/>
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="TestTrailer" name="{resource:getString($constants,'trailerForTest')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<HorizontalPanel spacing="0" padding="0" style="WhiteContentPanel">
			<!--left table goes here -->
			<CollapsePanel key="collapsePanel">
			    <azTable width="100%" key="azTable" maxRows="11" title="{resource:getString($constants,'name')}" tablewidth="auto" colwidths="175">
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
						<textbox case="lower" key="{testTrailerMeta:getName($meta)}" max="60" width="150px" tab="{testTrailerMeta:getDescription($meta)},{testTrailerMeta:getText($meta)}"/>
					</row>
					<row>								
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"description")'/>:</text>
						<textbox case="mixed" key="{testTrailerMeta:getDescription($meta)}" max="60" width="300px" tab="{testTrailerMeta:getText($meta)},{testTrailerMeta:getName($meta)}"/>
					</row>
					<row>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"text")'/>:</text>
						<textarea key="{testTrailerMeta:getText($meta)}" width="300px" height="170px" tab="{testTrailerMeta:getName($meta)},{testTrailerMeta:getDescription($meta)}"/>
					</row>
				</TablePanel>
				</VerticalPanel>
			</VerticalPanel>
		</HorizontalPanel>
	</display>
	<rpc key="display">
  	<number key="{testTrailerMeta:getId($meta)}" type="integer" required="false"/>
  	<string key="{testTrailerMeta:getName($meta)}" max="20" required="true"/>
  	<string key="{testTrailerMeta:getDescription($meta)}" max="60" required="true"/>
  	<string key="{testTrailerMeta:getText($meta)}" required="true"/>
	</rpc>
	<rpc key="query">
 	<queryNumber key="{testTrailerMeta:getId($meta)}" type="integer" required="false"/>
  	<queryString key="{testTrailerMeta:getName($meta)}" max="20" required="true"/>
  	<queryString key="{testTrailerMeta:getDescription($meta)}" max="60" required="true"/>
  	<queryString key="{testTrailerMeta:getText($meta)}" required="true"/>

	</rpc>
	<rpc key="queryByLetter">
		<queryString key="{testTrailerMeta:getName($meta)}"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>