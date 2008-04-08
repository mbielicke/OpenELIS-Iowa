<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.server.constants.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                xmlns:storageUnitMeta="xalan://org.openelis.meta.StorageUnitMeta"
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZTwoColumnsNum.xsl"/>   
  
  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.server.constants.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
 <xalan:component prefix="StorageUnitMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.StorageUnitMeta"/>
  </xalan:component>

  <xsl:template match="doc"> 
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle('org.openelis.modules.main.server.constants.OpenELISConstants',locale:new(string($language)))"/>
<screen id="Storage" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<panel layout="horizontal" spacing="0" padding="0" style="WhiteContentPanel" xsi:type="Panel">
			<!--left table goes here -->
			    <aToZ height="260px" width="100%" key="hideablePanel" visible="false" maxRows="10" title="{resource:getString($constants,'name')}" tablewidth="auto" colwidths="175">
					 <xsl:if test="string($language)='en'">
					 <buttonPanel key="atozButtons">
	    			   <xsl:call-template name="aToZLeftPanelButtons"/>		
		    		 </buttonPanel>
					</xsl:if>
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
										<textbox case="mixed" key="name" max="60" width="350px" tab="{storageUnitMeta:isSingular()},{storageUnitMeta:category()}"/>
									</widget>
								</row>
								<row>								
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"analyteGroup")'/>:</text>
									</widget>
									<widget>
										<textbox case="mixed" key="analyteGroup" width="200px" tab="parentAnalyte,name"/>
									</widget>
								</row>
								<row>								
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"parentAnalyte")'/>:</text>
									</widget>
									<widget>
										<textbox case="mixed" key="parentAnalyte" width="200px" tab="externalId,analyteGroup"/>
									</widget>
								</row>
								<row>								
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"externalId")'/>:</text>
									</widget>
									<widget>
										<textbox case="mixed" key="externalId" max="20" width="150px" tab="isActive,parentAnalyte"/>
									</widget>
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"active")'/>:</text>
									</widget>
									<widget>
										<check key="isActive" tab="name,externalId"/>
									</widget>
								</row>
							</panel>
				</panel>
			</panel>
		</panel>
	</display>
	<rpc key="display">
  	<number key="id" type="integer" required="false"/>
  	<string key="name" required="true"/>
  	<number key="analyteGroup" type="integer" required="true"/>
  	<number key="parentAnalyte" type="integer" required="false"/>
  	<string key="externalId" required="false"/>
  	<string key="isActive" required="false"/>
	</rpc>
	<rpc key="query">
  	<queryNumber key="id" type="integer" required="false"/>
  	<queryString key="name" required="false"/>
  	<queryNumber key="analyteGroup" type="integer" required="false"/>
  	<queryNumber key="parentAnalyte" type="integer" required="false"/>
  	<queryString key="externalId" required="false"/>
  	<queryString key="isActive" required="false"/>
	</rpc>
	<rpc key="queryByLetter">
	<queryString key="name"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>