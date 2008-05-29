<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                xmlns:storageUnitMeta="xalan://org.openelis.meta.StorageUnitMeta"
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZTwoColumns.xsl"/>   
  
  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
 <xalan:component prefix="StorageUnitMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.StorageUnitMeta"/>
  </xalan:component>

  <xsl:template match="doc"> 
      <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="Storage" name="{resource:getString($constants,'storageUnit')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<panel layout="horizontal" spacing="0" padding="0" style="WhiteContentPanel" xsi:type="Panel">
			<!--left table goes here -->
			    <aToZ height="240px" width="100%" key="hideablePanel" maxRows="10" title="{resource:getString($constants,'description')}" tablewidth="auto" colwidths="175">
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
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"category")'/>:</text>
									</widget>
									<widget>
										<autoDropdown key="{storageUnitMeta:category()}" case="mixed" width="110px" tab="{storageUnitMeta:description()},{storageUnitMeta:id()}"/>
									</widget>
								</row>
								<row>								
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"description")'/>:</text>
									</widget>
									<widget>
										<textbox case="lower" key="{storageUnitMeta:description()}" max="60" width="300px" tab="{storageUnitMeta:isSingular()},{storageUnitMeta:category()}"/>
									</widget>
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"isSingular")'/>:</text>
									</widget>
									<widget>
										<check key="{storageUnitMeta:isSingular()}" tab="{storageUnitMeta:id()},{storageUnitMeta:description()}"/>
									</widget>
								</row>
							</panel>
				</panel>
			</panel>
		</panel>
	</display>
	<rpc key="display">
  	<number key="{storageUnitMeta:id()}" type="integer" required="false"/>
  	<dropdown key="{storageUnitMeta:category()}" required="false"/><!---->
  	<string key="{storageUnitMeta:description()}" max="60" required="false"/><!---->
  	<check key="{storageUnitMeta:isSingular()}" required="false"/>
	</rpc>
	<rpc key="query">
 	<queryNumber key="{storageUnitMeta:id()}" type="integer" required="false"/>
 	<dropdown key="{storageUnitMeta:category()}" required="false"/>
  	<queryString key="{storageUnitMeta:description()}" required="false"/>
  	<queryCheck key="{storageUnitMeta:isSingular()}" type="string" required="false"/>

	</rpc>
	<rpc key="queryByLetter">
		<queryString key="{storageUnitMeta:description()}"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>