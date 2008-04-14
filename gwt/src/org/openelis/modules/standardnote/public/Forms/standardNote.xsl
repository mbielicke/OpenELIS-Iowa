<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.server.constants.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                xmlns:standardNoteMeta="xalan://org.openelis.meta.StandardNoteMeta" 
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZTwoColumns.xsl"/> 

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.server.constants.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>

  <xalan:component prefix="standardNoteMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.StandardNoteMeta"/>
  </xalan:component>
  
  <xsl:template match="doc"> 
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle('org.openelis.modules.main.server.constants.OpenELISConstants',locale:new(string($language)))"/>
<screen id="Storage" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<panel layout="horizontal" spacing="0" padding="0" style="WhiteContentPanel" xsi:type="Panel">
			<!--left table goes here -->
				<aToZ height="290px" width="100%" key="hideablePanel" visible="false" maxRows="10" title='{resource:getString($constants,"name")}' tablewidth="auto" colwidths="175">
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
										<textbox case="mixed" key="{standardNoteMeta:name()}" width="155px" max="20" tab="{standardNoteMeta:description()},{standardNoteMeta:text()}"/>
									</widget>
								</row>
								<row>								
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"description")'/>:</text>
									</widget>
									<widget>
										<textbox case="mixed" key="{standardNoteMeta:description()}" width="300px" max="60" tab="{standardNoteMeta:type()},{standardNoteMeta:name()}"/>
									</widget>
								</row>
								<row>								
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"type")'/>:</text>
									</widget>
									<widget>
										<autoDropdown key="{standardNoteMeta:type()}" case="mixed" width="121px" popWidth="auto" tab="{standardNoteMeta:text()},{standardNoteMeta:description()}">
											<widths>131</widths>
										</autoDropdown>
									</widget>
								</row>
								<row>
								  <widget>
								    <text style="Prompt">Test Date</text>
								  </widget>
								  <widget>
				                    <calendar begin="0" end="2"/>
							      </widget>
							    </row>	    
								<row>								
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"text")'/>:</text>
									</widget>
									<widget>
										<textarea key="{standardNoteMeta:text()}" width="300px" height="180px" tab="{standardNoteMeta:name()},{standardNoteMeta:type()}"/>
									</widget>
								</row>
								
							</panel>
				</panel>
			</panel>
		</panel>
	</display>
	<rpc key="display">
  	<number key="{standardNoteMeta:id()}" type="integer" required="false"/>
  	<string key="{standardNoteMeta:name()}" required="true" max="20"/>
  	<string key="{standardNoteMeta:description()}" required="true" max="60"/>
  	<dropdown key="{standardNoteMeta:type()}" required="true"/>
  	<string key="{standardNoteMeta:text()}" required="true"/>
	</rpc>
	<rpc key="query">
 	<queryNumber key="{standardNoteMeta:id()}" type="integer" required="false"/>
 	<queryString key="{standardNoteMeta:name()}" type="string" required="false"/>
  	<queryString key="{standardNoteMeta:description()}" required="false"/>
  	<dropdown key="{standardNoteMeta:type()}" required="false"/>
	<queryString key="{standardNoteMeta:text()}" required="false"/>
	</rpc>
	<rpc key="queryByLetter">
		<queryString key="{standardNoteMeta:name()}"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>