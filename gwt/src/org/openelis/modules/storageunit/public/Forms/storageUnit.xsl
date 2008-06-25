<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                xmlns:storageUnitMeta="xalan://org.openelis.meta.StorageUnitMetaMap"
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZTwoColumns.xsl"/>   
  
  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
 <xalan:component prefix="storageUnitMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.StorageUnitMetaMap"/>
  </xalan:component>

  <xsl:template match="doc"> 
    <xsl:variable name="meta" select="storageUnitMeta:new()"/>
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="Storage" name="{resource:getString($constants,'storageUnit')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<HorizontalPanel spacing="0" padding="0" style="WhiteContentPanel">
			<!--left table goes here -->
			<CollapsePanel key="collapsePanel" height="235px">
			    <azTable width="100%" key="azTable" maxRows="10" title="{resource:getString($constants,'description')}" tablewidth="auto" colwidths="175">
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
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"category")'/>:</text>
						<autoDropdown key="{storageUnitMeta:getCategory($meta)}" case="mixed" width="110px" tab="{storageUnitMeta:getDescription($meta)},{storageUnitMeta:getId($meta)}"/>
					</row>
					<row>								
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"description")'/>:</text>
						<textbox case="lower" key="{storageUnitMeta:getDescription($meta)}" max="60" width="300px" tab="{storageUnitMeta:getIsSingular($meta)},{storageUnitMeta:getCategory($meta)}"/>
					</row>
					<row>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"isSingular")'/>:</text>
						<check key="{storageUnitMeta:getIsSingular($meta)}" tab="{storageUnitMeta:getId($meta)},{storageUnitMeta:getDescription($meta)}"/>
					</row>
				</TablePanel>
				</VerticalPanel>
			</VerticalPanel>
		</HorizontalPanel>
	</display>
	<rpc key="display">
  	<number key="{storageUnitMeta:getId($meta)}" type="integer" required="false"/>
  	<dropdown key="{storageUnitMeta:getCategory($meta)}" required="false"/><!---->
  	<string key="{storageUnitMeta:getDescription($meta)}" max="60" required="false"/><!---->
  	<check key="{storageUnitMeta:getIsSingular($meta)}" required="false"/>
	</rpc>
	<rpc key="query">
 	<queryNumber key="{storageUnitMeta:getId($meta)}" type="integer" required="false"/>
 	<dropdown key="{storageUnitMeta:getCategory($meta)}" required="false"/>
  	<queryString key="{storageUnitMeta:getDescription($meta)}" required="false"/>
  	<queryCheck key="{storageUnitMeta:getIsSingular($meta)}" type="string" required="false"/>

	</rpc>
	<rpc key="queryByLetter">
		<queryString key="{storageUnitMeta:getDescription($meta)}"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>