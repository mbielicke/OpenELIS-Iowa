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
                xmlns:meta="xalan://org.openelis.metamap.AuxFieldGroupMetaMap"
                xmlns:auxField="xalan://org.openelis.metamap.AuxFieldMetaMap"
                xmlns:auxFieldValue="xalan://org.openelis.metamap.AuxFieldValueMetaMap"                       
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
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.AuxFieldGroupMetaMap"/>
	</xalan:component>
	<xalan:component prefix="auxField">
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.AuxFieldMetaMap"/>
	</xalan:component>
	<xalan:component prefix="auxFieldValue">
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.AuxFieldValueMetaMap"/>
	</xalan:component>	
	
	<xsl:template match="doc">
	   <xsl:variable name="auxfg" select="meta:new()"/>	
	   <xsl:variable name="auxf" select="meta:getAuxField($auxfg)"/>
	   <xsl:variable name="auxfv" select="auxField:getAuxFieldValue($auxf)"/>
		<xsl:variable name="language">
		<xsl:value-of select="locale"/>
		</xsl:variable>
		<xsl:variable name="props">
			<xsl:value-of select="props"/>
		</xsl:variable>
		<xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
		<screen id="Auxiliary" name="{resource:getString($constants,'auxiliaryPrompt')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
			<display>
				<HorizontalPanel padding="0" spacing="0" style="WhiteContentPanel">
					<!--left table goes here -->
					<CollapsePanel key="collapsePanel" height="480px">
					<resultsTable key="azTable" height="480px" width="100%">
				   	 <buttonPanel key="atozButtons">
	    			   <xsl:call-template name="aToZLeftPanelButtons"/>		
		    		 </buttonPanel>
		    		 <table maxRows="25" width="auto">
		    		   <headers><xsl:value-of select="resource:getString($constants,'groupName')"/></headers>
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
						<HorizontalPanel padding="0" spacing="0">
						<VerticalPanel width = "10px"/>
						 <VerticalPanel>						
								
				  <VerticalPanel height = "10px"/>						   							    																			
							<VerticalPanel>
							<TablePanel style="Form">
								<row>
								  <text style="Prompt"><xsl:value-of select="resource:getString($constants,'groupName')"/>:</text>
								  <widget colspan = "5">
							       <textbox  key="{meta:getName($auxfg)}"  width="145px"/>
							      </widget> 
								</row> 	
								<row>
								  <text style="Prompt"><xsl:value-of select="resource:getString($constants,'description')"/>:</text>
									<widget colspan = "5">
										<textbox  key="{meta:getDescription($auxfg)}" max="60" width="425px"/>
									</widget>									
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"active")'/>:</text>
									<check key="{meta:getIsActive($auxfg)}" />																	
								 <text style="Prompt"><xsl:value-of select='resource:getString($constants,"beginDate")'/>:</text>
								 <calendar key="{meta:getActiveBegin($auxfg)}"  onChange="this" begin="0" end="2" width = "80px"/>																									 
								 <text style="Prompt"><xsl:value-of select='resource:getString($constants,"endDate")'/>:</text>
								 <calendar key="{meta:getActiveEnd($auxfg)}"  onChange="this" begin="0" end="2" width = "80px"/>
								</row>
					          </TablePanel>			
							 <HorizontalPanel> 							 
							  <widget>
                                <table key="auxFieldTable" title="" targets = "auxFieldTable" manager = "this" drop = "default" drag = "default" width="600px" showError="false" showScroll="ALWAYS" maxRows="10">                                
                                 <headers>                                 
                                  <xsl:value-of select="resource:getString($constants,'analyte')"/>,
                                  <xsl:value-of select="resource:getString($constants,'method')"/>,
								  <xsl:value-of select="resource:getString($constants,'unit')"/>,
                                  <xsl:value-of select="resource:getString($constants,'active')"/>,
								  <xsl:value-of select="resource:getString($constants,'required')"/>,
								  <xsl:value-of select="resource:getString($constants,'auxReportable')"/>,																					  								 
								  <xsl:value-of select="resource:getString($constants,'description')"/>,
								  <xsl:value-of select="resource:getString($constants,'scriptlet')"/>
                                 </headers>
                                 <widths>250,70,50,60,70,60,200,150</widths>                                 
                                   <editors>
                                    <autoComplete cat="analyte" serviceUrl="OpenELISServlet?service=org.openelis.modules.auxiliary.server.AuxiliaryService" case="mixed" width="300px">												
										 <widths>300</widths>
									</autoComplete>
									<autoComplete cat="method" serviceUrl="OpenELISServlet?service=org.openelis.modules.auxiliary.server.AuxiliaryService" case="mixed" width="300px">												
										 <widths>100</widths>
									</autoComplete>
                                    <dropdown case="mixed" width = "80px" type = "integer"/>
									<check/>
                                    <check/> 
                                    <check/>									                                   
                                    <textbox/>
                                    <dropdown case="mixed" required = "true" width = "80px" type = "integer"/>   
                                   </editors>
                                   <fields>
                                    <dropdown key="{auxField:getAnalyteId($auxf)}" required = "true"/>
                                    <dropdown key="{auxField:getMethodId($auxf)}"/>
                                    <dropdown key="{auxField:getUnitOfMeasureId($auxf)}"/>
                                    <check key = "{auxField:getIsActive($auxf)}"/>
                                    <check key = "{auxField:getIsRequired($auxf)}"/>
                                    <check key = "{auxField:getIsReportable($auxf)}"/>                                                                                                              
                                    <string key="{auxField:getDescription($auxf)}"/> 
                                    <dropdown key="{auxField:getScriptletId($auxf)}" />                                                       
                                   </fields>
                                   <sorts>false,false,false,false,false,false,false,false</sorts>
								   <filters>false,false,false,false,false,false,false,false</filters>
                                   <colAligns>left,left,left,center,center,center,left,left</colAligns>
                                </table>
                              </widget> 
                              <HorizontalPanel width = "10px"/>
                             </HorizontalPanel>                              						 							
								<widget halign="center">
											<appButton action="removeIdentifierRow" key="removeAuxFieldRowButton" onclick="this" style="Button">
												<HorizontalPanel>
													<AbsolutePanel style="RemoveRowButtonImage"/>
													<text><xsl:value-of select="resource:getString($constants,'removeRow')"/></text>
												</HorizontalPanel>
											</appButton>
								</widget> 							 																															 						   							  
							<HorizontalPanel>
							  <widget valign="top">
							    <table key="auxFieldValueTable" manager="this" maxRows="5" showError="false" showScroll="ALWAYS" title="" width="600px">
												<headers>		 										    
													<xsl:value-of select="resource:getString($constants,'type')"/>,													
													<xsl:value-of select="resource:getString($constants,'value')"/>																																																																												
												</headers>
												<widths>100,493</widths>
												<editors>
												    <dropdown case="mixed" width="100px"/>													
													<textbox/>																																			
												</editors>
												<fields>											    
												    <dropdown key="{auxFieldValue:getTypeId($auxfv)}" type="integer" required="true"/>																										
													<string key="{auxFieldValue:getValue($auxfv)}" required="false"/>																																	
												</fields>
												<sorts>false,false</sorts>
												<filters>false,false</filters>
												<colAligns>left,left</colAligns>
								 </table>
							  </widget>
							 <HorizontalPanel width = "10px"/>                             
							</HorizontalPanel>     
                                     <HorizontalPanel>    
                                       <widget>
							            <HorizontalPanel width = "7px"/>
							           </widget>                                       
                                       <widget  align="center">
									       <appButton action="removeRow" onclick="this" style="Button" key="removeAuxFieldValueRowButton">
									        <HorizontalPanel>
													<AbsolutePanel style="RemoveRowButtonImage"/>
													<text><xsl:value-of select="resource:getString($constants,'removeRow')"/></text>
												</HorizontalPanel>
						                 </appButton>
						              </widget>
						              <HorizontalPanel width = "350px"/>
						              <widget  align="center">
								       <appButton action="dictionaryLookUp" onclick="this" key="dictionaryLookUpButton" style="Button">
										<HorizontalPanel>
              							  <AbsolutePanel style="ButtonPanelButton"/>						              
                						    <text><xsl:value-of select='resource:getString($constants,"dictionary")'/></text>							             
							              </HorizontalPanel>
						                </appButton>
						               </widget>
						             </HorizontalPanel>  							        															                   		
						</VerticalPanel>			 					   												   							  								 					    			
					 </VerticalPanel>
						<HorizontalPanel width = "10px"/>
					  </HorizontalPanel>			
					</VerticalPanel>					
				</HorizontalPanel>
			</display>
			<rpc key="display">	
			 <integer key="{meta:getId($auxfg)}" required="false" />
			 <string key="{meta:getName($auxfg)}" required = "true"/>
			 <string key="{meta:getDescription($auxfg)}" required = "false"/>
			 <date key="{meta:getActiveBegin($auxfg)}" begin="0" end="2" required = "true"/>	
			 <date key="{meta:getActiveEnd($auxfg)}" begin="0" end="2" required = "true"/>	
			 <check key = "{meta:getIsActive($auxfg)}" required = "true"/>	 
	         <table key = "auxFieldTable"/>
			 <table key = "auxFieldValueTable"/>					 										
		    </rpc>
		</screen>
  </xsl:template>
</xsl:stylesheet>