<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.server.constants.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZTwoColumns.xsl"/> 

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.server.constants.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>

  <xsl:template match="doc"> 
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle('org.openelis.modules.main.server.constants.OpenELISConstants',locale:new(string($language)))"/>
<screen id="Storage" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<panel layout="horizontal" spacing="0" padding="0" style="WhiteContentPanel" xsi:type="Panel">
			<!--left table goes here -->
				<aToZ height="290px" width="100%" key="hideablePanel" visible="false" maxRows="10" title='{resource:getString($constants,"name")}' tablewidth="auto" colwidths="175">
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
										<textbox case="mixed" key="standardNote.name" width="155px" max="20" tab="standardNote.description,id"/>
									</widget>
								</row>
								<row>								
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"description")'/>:</text>
									</widget>
									<widget>
										<textbox case="mixed" key="standardNote.description" width="300px" max="60" tab="standardNote.type,standardNote.name"/>
									</widget>
								</row>
								<row>								
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"type")'/>:</text>
									</widget>
									<widget>
										<autoDropdown key="standardNote.type" case="mixed" width="121px" popWidth="auto" tab="organization.address.zipCode,organization.address.city">
											<widths>100</widths>
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
										<textarea key="standardNote.text" width="300px" height="180px" tab="id,standardNote.type"/>
									</widget>
								</row>
								
							</panel>
				</panel>
			</panel>
		</panel>
	</display>
	<rpc key="display">
  	<number key="standardNote.id" type="integer" required="false"/>
  	<string key="standardNote.name" required="true" max="20"/>
  	<string key="standardNote.description" required="true" max="60"/>
  	<dropdown key="standardNote.type" required="true"/>
  	<string key="standardNote.text" required="true"/>
	</rpc>
	<rpc key="query">
 	<queryNumber key="standardNote.id" type="integer" required="false"/>
 	<queryString key="standardNote.name" type="string" required="false"/>
  	<queryString key="standardNote.description" required="false"/>
  	<dropdown key="standardNote.type" required="false"/>
	<queryString key="standardNote.text" required="false"/>
	</rpc>
	<rpc key="queryByLetter">
		<queryString key="standardNote.name"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>