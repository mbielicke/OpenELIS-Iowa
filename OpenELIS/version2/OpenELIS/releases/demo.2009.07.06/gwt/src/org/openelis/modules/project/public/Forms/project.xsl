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
                xmlns:meta="xalan://org.openelis.metamap.ProjectMetaMap"   
                xmlns:prmtrMeta="xalan://org.openelis.metamap.ProjectParameterMetaMap"          
                xmlns:script="xalan://org.openelis.meta.ScriptletMeta"
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
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.ProjectMetaMap"/>
	</xalan:component>
	<xalan:component prefix="prmtrMeta">
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.ProjectParameterMetaMap"/>
	</xalan:component>
	<xalan:component prefix="script">
		<xalan:script lang="javaclass" src="xalan://org.openelis.meta.ScriptletMeta"/>
    </xalan:component>
	
	<xsl:template match="doc">	
	   <xsl:variable name="proj" select="meta:new()"/>
	   <xsl:variable name="scpt" select="meta:getScriptlet($proj)"/>
	   <xsl:variable name="prm" select="meta:getProjectParameter($proj)"/>
		<xsl:variable name="language">
		<xsl:value-of select="locale"/>
		</xsl:variable>
		<xsl:variable name="props">
			<xsl:value-of select="props"/>
		</xsl:variable>
		<xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
		<screen id="Project" name="{resource:getString($constants,'project')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
			<display>
				<HorizontalPanel padding="0" spacing="0">
					<!--left table goes here -->
					<CollapsePanel key="collapsePanel" height="450px" style="LeftSidePanel">

					   <resultsTable width="100%" key="azTable" showError="false">
					       <buttonPanel key="atozButtons">
								<xsl:call-template name="aToZLeftPanelButtons"/>
						   </buttonPanel>
						   <table maxRows="20" width="auto">
						     <headers><xsl:value-of select="resource:getString($constants,'name')"/></headers>
						     <widths>175</widths>
						     <editors>
						       <label/>
						     </editors>
						     <fields>
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
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'id')"/>:</text>
									<textbox  key="{meta:getId($proj)}" width="50px" tab = "{meta:getName($proj)},parameterTable" />
								</row> 
								<row>
								  <text style="Prompt"><xsl:value-of select="resource:getString($constants,'name')"/>:</text>
								  <widget colspan = "6">	
									<textbox key="{meta:getName($proj)}" case = "lower" max="20" width="145px" tab = "{meta:getDescription($proj)},{meta:getId($proj)}" />																									    								   
								  </widget>
								</row>
								<row>
								    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'description')"/>:</text>
									<widget colspan = "6">
										<textbox  key="{meta:getDescription($proj)}" case = "mixed" max="60" width="425px" tab = "{meta:getOwnerId($proj)},{meta:getName($proj)}"/>
									</widget>
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'owner')"/>:</text>
									<widget>
										<autoComplete case="mixed" cat="owner" key="{meta:getOwnerId($proj)}" tab = "{meta:getIsActive($proj)},{meta:getDescription($proj)}" serviceUrl="OpenELISServlet?service=org.openelis.modules.project.server.ProjectService" width="145px">
								       		<widths>145</widths>
									  	</autoComplete>
									</widget>									
								</row>																	
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"active")'/>:</text>
									<check key="{meta:getIsActive($proj)}" tab = "{meta:getStartedDate($proj)},{meta:getOwnerId($proj)}"/>
									
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'referenceTo')"/>:</text>
									<widget>
										<textbox  key="{meta:getReferenceTo($proj)}" tab = "{script:getName($scpt)},{meta:getCompletedDate($proj)}" case = "mixed" max="20" width="145px"/>
									</widget>									
								</row>		
								<row>
								 <text style="Prompt"><xsl:value-of select='resource:getString($constants,"beginDate")'/>:</text>
								 <calendar key="{meta:getStartedDate($proj)}" begin="0" end="2" width = "90px" tab = "{meta:getCompletedDate($proj)},{meta:getIsActive($proj)}"/>
								 <text style="Prompt"><xsl:value-of select="resource:getString($constants,'scriptlet')"/>:</text>								 																									 								
								 <autoComplete cat="scriptlet" key="{script:getName($scpt)}" tab = "parameterTable,{meta:getId($proj)}" serviceUrl="OpenELISServlet?service=org.openelis.modules.project.server.ProjectService" case="lower" width="180px">												
									<widths>180</widths>
								  </autoComplete>
								</row>	
								<row>
								 <text style="Prompt"><xsl:value-of select='resource:getString($constants,"endDate")'/>:</text>
								 <calendar key="{meta:getCompletedDate($proj)}" begin="0" end="2" width = "90px" tab = "{meta:getReferenceTo($proj)},{meta:getStartedDate($proj)}"/>								 
								</row>					
						     </TablePanel>
						     <VerticalPanel height = "5px"/>			 
							  <HorizontalPanel width = "635px">							 
							   <widget valign="top">
							    <table key="parameterTable" manager="this" maxRows="8" showError="false" showScroll="ALWAYS" title="" width = "605px" tab = "{meta:getId($proj)},{script:getName($scpt)}">
												<headers>
												    <xsl:value-of select="resource:getString($constants,'parameter')"/>,
													<xsl:value-of select="resource:getString($constants,'operation')"/>,													
													<xsl:value-of select="resource:getString($constants,'value')"/>																																						
												</headers>
												<widths>325,80,400</widths>
												<editors>
													<textbox cellKey="{prmtrMeta:getParameter($prm)}"/>
													<dropdown cellKey="{prmtrMeta:getOperationId($prm)}" case="mixed" width="140px"/>
													<textbox cellKey="{prmtrMeta:getValue($prm)}"/>																						
												</editors>												
												<sorts>false,false,false</sorts>
												<filters>false,false,false</filters>
												<colAligns>left,left,left</colAligns>
								</table>						
						       </widget>							     						     
                            </HorizontalPanel>                                                       
						           <TablePanel width = "635px" spacing="0" padding="0" style="TableFooter">
                                      <row>
                                        <widget  align="center" >
									     <appButton action="removeRow" onclick="this" style="Button" key="removeParameterButton" >
									       <HorizontalPanel>
              						         <AbsolutePanel style="RemoveRowButtonImage"/>
						                      <widget>
                						       <text><xsl:value-of select='resource:getString($constants,"removeRow")'/></text>
							                 </widget>
							               </HorizontalPanel>
						                 </appButton>
						                </widget>	
						              </row>  									 
						           </TablePanel>						        
							  </VerticalPanel>						    	
						</VerticalPanel>   								
				</HorizontalPanel>
			</display>
			<rpc key="display">
			 <integer key="{meta:getId($proj)}" required="false"/>
			 <string key="{meta:getName($proj)}" max = "20" required="true" />
			 <check key="{meta:getIsActive($proj)}" required="true"/>
			 <date key="{meta:getCompletedDate($proj)}" begin="0" end="2" required="false"/>
			 <date key="{meta:getStartedDate($proj)}" begin="0" end="2" required="false"/>
			 <string key="{meta:getDescription($proj)}" max="60" required="false"/>
			 <string key="{meta:getReferenceTo($proj)}" max = "20" required="false" />
			 <dropdown key="{meta:getOwnerId($proj)}" case="lower" required="true" type="integer"/>
			 <dropdown key="{script:getName($scpt)}" required="false" type="integer"/> 		 								 
		     <table key="parameterTable">
		     	<string key="{prmtrMeta:getParameter($prm)}" required="true"/>
				<dropdown key="{prmtrMeta:getOperationId($prm)}" case="mixed" required="true" type="integer"/>
				<string key="{prmtrMeta:getValue($prm)}" required="true"/>
		     </table>  
		  </rpc>
		</screen>
  </xsl:template>
</xsl:stylesheet>