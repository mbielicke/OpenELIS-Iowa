<!--
Exhibit A - UIRF Open-source Based Public Software License.

The contents of this file are subject to the UIRF Open-source Based
Public Software License(the "License"); you may not use this file except
in compliance with the License. You may obtain a copy of the License at
openelis.uhl.uiowa.edu

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.

The Original Code is OpenELIS code.

The Initial Developer of the Original Code is The University of Iowa.
Portions created by The University of Iowa are Copyright 2006-2008. All
Rights Reserved.

Contributor(s): ______________________________________.

Alternatively, the contents of this file marked
"Separately-Licensed" may be used under the terms of a UIRF Software
license ("UIRF Software License"), in which case the provisions of a
UIRF Software License are applicable instead of those above. 
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"                
                xmlns:locale="xalan://java.util.Locale"
                xmlns:meta="xalan://org.openelis.metamap.InstrumentMetaMap"    
                xmlns:instLog="xalan://org.openelis.metamap.InstrumentLogMetaMap"       
                xmlns:script="xalan://org.openelis.meta.ScriptletMeta"
                xmlns:analyte="xalan://org.openelis.meta.AnalyteMeta"
                extension-element-prefixes="resource"
                version="1.0">
	<xsl:import href="aToZOneColumn.xsl"/>
	<xalan:component prefix="resource">
		<xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
	</xalan:component>
	<xalan:component prefix="locale">
		<xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
	</xalan:component>
	<xalan:component prefix="meta">
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.InstrumentMetaMap"/>
	</xalan:component>
	<xalan:component prefix="instLog">
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.InstrumentLogMetaMap"/>
	</xalan:component>
	<xalan:component prefix="script">
		<xalan:script lang="javaclass" src="xalan://org.openelis.meta.ScriptletMeta"/>
    </xalan:component>
	
	<xsl:template match="doc">	
	   <xsl:variable name="inst" select="meta:new()"/>
	   <xsl:variable name="scpt" select="meta:getScriptlet($inst)"/>
	   <xsl:variable name="il" select="meta:getInstrumentLog($inst)"/>
		<xsl:variable name="language">
		<xsl:value-of select="locale"/>
		</xsl:variable>
		<xsl:variable name="props">
			<xsl:value-of select="props"/>
		</xsl:variable>
		<xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
		<screen id="Project" name="{resource:getString($constants,'instrument')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
			<display>
				<HorizontalPanel padding="0" spacing="0">
					<!--left table goes here -->
					<CollapsePanel key="collapsePanel" height="450px" style="LeftSidePanel">

					   <resultsTable width="100%" key="azTable" showError="false">
					       <buttonPanel key="atozButtons">
								<xsl:call-template name="aToZLeftPanelButtons"/>
						   </buttonPanel>
						   <table maxRows="20" width="auto">
						     <headers><xsl:value-of select="resource:getString($constants,'name')"/>,<xsl:value-of select="resource:getString($constants,'serialNumber')"/></headers>
						     <widths>75,100</widths>
						     <editors>
						       <label/>
						       <label/>
						     </editors>
						     <fields>
						       <string/>
						       <string/>
						     </fields>
						   </table>
					   </resultsTable>
					</CollapsePanel>					
					<VerticalPanel spacing="0" padding="0">
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
						 <VerticalPanel spacing="0" padding="0" height="235px" width="620px" style="WhiteContentPanel">
							<TablePanel style="Form">
								<row>
								  <text style="Prompt"><xsl:value-of select="resource:getString($constants,'name')"/>:</text>
								  <widget>	
									<textbox key="{meta:getName($inst)}" case = "lower" max="20" width="145px" tab = "{meta:getDescription($inst)},{meta:getActiveEnd($inst)}" />																									    								   
								  </widget>
								</row>
								<row>
								    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'description')"/>:</text>
									<widget colspan = "6">
										<textbox  key="{meta:getDescription($inst)}" case = "mixed" max="60" width="425px" tab = "{meta:getModelNumber($inst)},{meta:getName($inst)}"/>
									</widget>
								</row>
								<row>
     							 <text style="Prompt"><xsl:value-of select="resource:getString($constants,'modelNumber')"/>:</text>
									<widget>
										<textbox  key="{meta:getModelNumber($inst)}" case = "mixed" max="40" width="285px" tab = "{meta:getSerialNumber($inst)},{meta:getDescription($inst)}"/>
									</widget>
									<!-- <text style="Prompt"><xsl:value-of select='resource:getString($constants,"active")'/>:</text>
								    <check key="{meta:getIsActive($inst)}" tab = "{meta:getActiveBegin($inst)},{script:getName($scpt)}"/>-->
								</row>
								<row>
     							 <text style="Prompt"><xsl:value-of select="resource:getString($constants,'serialNumber')"/>:</text>
									<widget>
										<textbox  key="{meta:getSerialNumber($inst)}" case = "mixed" max="40" width="285px" tab = "{meta:getTypeId($inst)},{meta:getModelNumber($inst)}"/>
									</widget>
									<!-- <text style="Prompt"><xsl:value-of select='resource:getString($constants,"beginDate")'/>:</text>
								    <calendar key="{meta:getActiveBegin($inst)}" tab = "{meta:getActiveEnd($inst)},{meta:getIsActive($inst)}" begin="0" end="4" width = "140px" />-->
								</row>
																	
								<row>
      							 <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"type")'/>:</text> 		
	                             <dropdown key="{meta:getTypeId($inst)}" width = "150px" case="mixed" tab = "{meta:getLocation($inst)},{meta:getSerialNumber($inst)}"/>									        
	
								 <text style="Prompt"><xsl:value-of select='resource:getString($constants,"active")'/>:</text>
								 <check key="{meta:getIsActive($inst)}" tab = "{meta:getActiveBegin($inst)},{script:getName($scpt)}"/> 
								 <!-- <text style="Prompt"><xsl:value-of select='resource:getString($constants,"endDate")'/>:</text>
								 <calendar key="{meta:getActiveEnd($inst)}" tab = "{meta:getName($inst)},{meta:getActiveBegin($inst)}" begin="0" end="4" width = "140px" />-->																	
								</row>		
								<row>
								 <text style="Prompt"><xsl:value-of select="resource:getString($constants,'location')"/>:</text>
									<widget>
										<textbox  key="{meta:getLocation($inst)}" tab = "{script:getName($scpt)},{meta:getTypeId($inst)}" case = "mixed" max="60" width="425px"/>
									</widget>
								 <text style="Prompt"><xsl:value-of select='resource:getString($constants,"beginDate")'/>:</text>
								 <calendar key="{meta:getActiveBegin($inst)}" tab = "{meta:getActiveEnd($inst)},{meta:getIsActive($inst)}" begin="0" end="4" width = "90px" />								
								</row>	
								<row>
								 <text style="Prompt"><xsl:value-of select="resource:getString($constants,'scriptlet')"/>:</text>								 																									 								
								 <autoComplete cat="scriptlet" key="{script:getName($scpt)}" tab = "{meta:getIsActive($inst)},{meta:getLocation($inst)}" serviceUrl="OpenELISServlet?service=org.openelis.modules.instrument.server.InstrumentService" case="lower" width="180px">												
									<widths>180</widths>
								  </autoComplete>
								 <text style="Prompt"><xsl:value-of select='resource:getString($constants,"endDate")'/>:</text>
								 <calendar key="{meta:getActiveEnd($inst)}" tab = "{meta:getName($inst)},{meta:getActiveBegin($inst)}" begin="0" end="4" width = "90px" />								 
								</row>	                                 			
						    </TablePanel>						     
     					      <VerticalPanel width= "665px">
						       <widget valign="top">
						        <table width= "635" maxRows = "8" key= "logTable" manager = "this" title= "" showError="false" showScroll="ALWAYS">
							         <headers><xsl:value-of select='resource:getString($constants,"type")'/>,
							                  <xsl:value-of select='resource:getString($constants,"worksheet")'/>,
							                  <xsl:value-of select='resource:getString($constants,"beginDate")'/>,
							                  <xsl:value-of select='resource:getString($constants,"endDate")'/>,
											  <xsl:value-of select='resource:getString($constants,"note")'/></headers>
									 <widths>120,100,140,140,400</widths>
									 <editors>
									    <dropdown cellKey ="{instLog:getTypeId($il)}" case="mixed" width="155px"/>
									    <textbox cellKey = "{instLog:getWorksheetId($il)}" />																																  										  	
									  	<calendar cellKey = "{instLog:getEventBegin($il)}" begin="0" end="4"/>
									  	<calendar cellKey = "{instLog:getEventEnd($il)}" begin="0" end="4"/>
								  		<textbox cellKey="{instLog:getText($il)}" case= "mixed" />								  				
									</editors>
									<sorts>false,false,false,false,false</sorts>
									<filters>false,false,false,false,false</filters>
									<colAligns>left,left,left,left,left</colAligns>
							    </table>
							  </widget>											
							  <widget style="WhiteContentPanel" halign="center">
								<appButton action="removeRow" onclick="this" style="Button" key="removeEntryButton">
								<HorizontalPanel>
	            						<AbsolutePanel style="RemoveRowButtonImage"/>
					              <widget>
	              						<text><xsl:value-of select='resource:getString($constants,"removeRow")'/></text>
						              </widget>
						              </HorizontalPanel>
					            </appButton>
					          </widget> 	 					            						    						             	 
							</VerticalPanel>     					   
						<VerticalPanel height = "5px"/>			 						        
					 </VerticalPanel>						    	
					</VerticalPanel>   								
				</HorizontalPanel>
			</display>
			<rpc key="display">
			 <integer key="{meta:getId($inst)}" required="false"/>
			 <string key="{meta:getName($inst)}" max = "20" required="true" />
			 <string key="{meta:getLocation($inst)}" max = "60" required="true" />
			 <string key="{meta:getSerialNumber($inst)}" max = "40" required = "true" />
			 <string key="{meta:getModelNumber($inst)}" max = "40"  />			 
			 <check key="{meta:getIsActive($inst)}" required="true"/>
			 <date key="{meta:getActiveBegin($inst)}" begin="0" end="2" />
			 <date key="{meta:getActiveEnd($inst)}" begin="0" end="2" />
			 <string key="{meta:getDescription($inst)}" max="60" required="false"/>
			 <dropdown key="{meta:getTypeId($inst)}" case="lower" required="true" type="integer"/>			
			 <dropdown key="{script:getName($scpt)}"  type="integer"/> 	
			 <table key="logTable">
			 	<dropdown key ="{instLog:getTypeId($il)}" type = "integer" case="mixed" required = "true"/>
				<integer key ="{instLog:getWorksheetId($il)}" />									  	
				<date key = "{instLog:getEventBegin($il)}" begin="0" end="4" required = "true"/>
				<date key = "{instLog:getEventEnd($il)}" begin="0" end="4"/>
				<string key="{instLog:getText($il)}" case= "mixed" />
			 </table> 								 
		  </rpc>
		</screen>
  </xsl:template>
</xsl:stylesheet>