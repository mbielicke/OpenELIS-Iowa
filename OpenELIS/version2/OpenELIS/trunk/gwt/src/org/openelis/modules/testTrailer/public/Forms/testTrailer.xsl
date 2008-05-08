<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                xmlns:testTrailerMeta="xalan://org.openelis.meta.TestTrailerMeta"
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
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.TestTrailerMeta"/>
  </xalan:component>

  <xsl:template match="doc"> 
      <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="TestTrailer" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
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
										<textbox case="lower" key="{testTrailerMeta:name()}" max="60" width="150px" tab="{testTrailerMeta:description()},{testTrailerMeta:text()}"/>
									</widget>
								</row>
								<row>								
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"description")'/>:</text>
									</widget>
									<widget>
										<textbox case="mixed" key="{testTrailerMeta:description()}" max="60" width="300px" tab="{testTrailerMeta:text()},{testTrailerMeta:name()}"/>
									</widget>
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"text")'/>:</text>
									</widget>
									<widget>
										<textarea key="{testTrailerMeta:text()}" width="300px" height="170px" tab="{testTrailerMeta:name()},{testTrailerMeta:description()}"/>
									</widget>
								</row>
							</panel>
				</panel>
			</panel>
		</panel>
	</display>
	<rpc key="display">
  	<number key="{testTrailerMeta:id()}" type="integer" required="false"/>
  	<string key="{testTrailerMeta:name()}" max="20" required="true"/>
  	<string key="{testTrailerMeta:description()}" max="60" required="true"/>
  	<string key="{testTrailerMeta:text()}" required="true"/>
	</rpc>
	<rpc key="query">
 	<queryNumber key="{testTrailerMeta:id()}" type="integer" required="false"/>
  	<queryString key="{testTrailerMeta:name()}" max="20" required="true"/>
  	<queryString key="{testTrailerMeta:description()}" max="60" required="true"/>
  	<queryString key="{testTrailerMeta:text()}" required="true"/>

	</rpc>
	<rpc key="queryByLetter">
		<queryString key="{testTrailerMeta:name()}"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>