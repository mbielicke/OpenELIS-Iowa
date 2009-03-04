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
                xmlns:meta="xalan://org.openelis.metamap.ProviderMetaMap"
                xmlns:note="xalan://org.openelis.meta.NotetMeta"
                xmlns:location="xalan://org.openelis.metamap.ProviderAddressMetaMap"
                xmlns:addr="xalan://org.openelis.meta.AddressMeta"
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
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.ProviderMetaMap"/>
  </xalan:component>    
  <xalan:component prefix="note">
	 <xalan:script lang="javaclass" src="xalan://org.openelis.meta.NotetMeta"/>
  </xalan:component>  
  <xalan:component prefix="location">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.ProviderAddressMetaMap"/>
  </xalan:component>  
  <xalan:component prefix="addr">
		<xalan:script lang="javaclass" src="xalan://org.openelis.meta.AddressMeta"/>
	</xalan:component>
  <xsl:template match="doc"> 
    <xsl:variable name="pro" select="meta:new()"/>	
	<xsl:variable name="loc" select="meta:getProviderAddress($pro)"/>	
	<xsl:variable name="note" select="meta:getNote($pro)"/>
	<xsl:variable name="locAddr" select="location:getAddress($loc)"/> 
    <xsl:variable name="language">
     <xsl:value-of select="locale"/>
    </xsl:variable>
    <xsl:variable name="props">
     <xsl:value-of select="props"/>
    </xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id= "Provider" name = "{resource:getString($constants,'provider')}" serviceUrl= "OpenElisService" xmlns:xsi= "http://www.w3.org/2001/XMLSchema-instance">
<display>
 	<HorizontalPanel spacing="0" padding="0">
 					<CollapsePanel key="collapsePanel" height="440px" style="LeftSidePanel">
						<azTable maxRows="18" tablewidth="auto" key="azTable" title="" width="100%" colwidths ="88,87" headers = "{resource:getString($constants,'lastName')},{resource:getString($constants,'firstName')}">
							<buttonPanel key="atozButtons">
								<xsl:call-template name="aToZLeftPanelButtons"/>
							</buttonPanel>
						</azTable>
					</CollapsePanel>
   <VerticalPanel spacing= "0" padding="0">
   <!--button panel code-->
		<AbsolutePanel layout="absolute" spacing="0" style="ButtonPanelContainer">
			<widget>
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
 			</widget>
		</AbsolutePanel>
		<!--end button panel-->
   
   <VerticalPanel spacing="0" padding="0" style="WhiteContentPanel">
   <TablePanel style= "Form">
    <row>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"id")'/>:</text>
      <textbox  width= "50px"  key = "{meta:getId($pro)}"  tab="{meta:getLastName($pro)},{meta:getNpi($pro)}"/>                
    </row>
    <row>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"lastName")'/>:</text> 
      <textbox key = "{meta:getLastName($pro)}" max="30" width= "215px" case = "upper" tab="{meta:getFirstName($pro)},{meta:getId($pro)}"/>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"type")'/>:</text>
	  <dropdown key="{meta:getTypeId($pro)}" case="mixed" width="80px" tab="{meta:getNpi($pro)},{meta:getMiddleName($pro)}"/>							
    </row>
    <row>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"firstName")'/>:</text>
      <textbox key= "{meta:getFirstName($pro)}" max="20"  case = "upper"   width= "145px" tab="{meta:getMiddleName($pro)},{meta:getLastName($pro)}"/>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"npi")'/>:</text>
      <textbox case= "mixed"   key= "{meta:getNpi($pro)}" max="20"  width= "145px" tab="{meta:getId($pro)},{meta:getTypeId($pro)}"/>
    </row>
    <row>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"middleName")'/>:</text>
      <textbox key= "{meta:getMiddleName($pro)}" max="20" case = "upper" width= "145px" tab="{meta:getTypeId($pro)},{meta:getFirstName($pro)}"/>
    </row>
   </TablePanel>
   
    <TabPanel width = "640px" key= "provTabPanel"  halign="center">
     <tab key= "addressesTab" text= "{resource:getString($constants,'locations')}">      
      <VerticalPanel>
       <widget valign="top">
        <table width= "606px" maxRows = "10" key= "providerAddressTable" manager = "this" title= "" showError="false" showScroll="ALWAYS">
         <headers><xsl:value-of select='resource:getString($constants,"location")'/>,<xsl:value-of select='resource:getString($constants,"externalId")'/>,<xsl:value-of select='resource:getString($constants,"aptSuite")'/>,
				  <xsl:value-of select='resource:getString($constants,"address")'/>,<xsl:value-of select='resource:getString($constants,"city")'/>,
                  <xsl:value-of select='resource:getString($constants,"state")'/>, <xsl:value-of select='resource:getString($constants,"country")'/>,
                  <xsl:value-of select='resource:getString($constants,"zipcode")'/>,<xsl:value-of select='resource:getString($constants,"workNumber")'/>,<xsl:value-of select='resource:getString($constants,"homeNumber")'/>,
				  <xsl:value-of select='resource:getString($constants,"cellNumber")'/>,<xsl:value-of select='resource:getString($constants,"faxNumber")'/>,
				  <xsl:value-of select='resource:getString($constants,"email")'/></headers>
		 <widths>115,130,130,130,130,60,130,100,90,90,90,150,145</widths>
		 <editors>
		  <textbox case= "mixed" max="50"/>
		  <textbox case= "mixed" max="10"/>
		  <textbox case= "mixed" max="30"/>
		  <textbox case= "mixed" max="30"/>
		  <textbox case= "mixed" max="30"/>		  
			<dropdown  case="upper" width="45px"/>
			<dropdown case="mixed" width="110px"/>
			<textbox case= "mixed" max="10"/>
			<textbox case= "mixed" max="21"/>
			<textbox case= "mixed" max="16"/>
			<textbox case= "mixed" max="16"/>
			<textbox case= "mixed" max="16"/>
			<textbox case= "mixed" max="80"/>		 
		</editors>
		 <fields>
		  <string key = "{location:getLocation($loc)}" required = "true"/>
		  <string key="{location:getExternalId($loc)}"/>
		  <string key="{addr:getMultipleUnit($locAddr)}"/>
		  <string key="{addr:getStreetAddress($locAddr)}" required="true"/>
		  <string key="{addr:getCity($locAddr)}" required="true"/>
		  <dropdown key="{addr:getState($locAddr)}"/>		  
		  <dropdown key="{addr:getCountry($locAddr)}" required="true"/>
		  <string key="{addr:getZipCode($locAddr)}" required="true"/>
		  <string key="{addr:getWorkPhone($locAddr)}"/>
		  <string key="{addr:getHomePhone($locAddr)}"/>
		  <string key="{addr:getCellPhone($locAddr)}"/>
		  <string key="{addr:getFaxPhone($locAddr)}"/>
		  <string key="{addr:getEmail($locAddr)}"/>  
		</fields>
		  <sorts>true,true,true,true,true,true,true,true,true,true,true,true,true</sorts>
		  <filters>false,false,false,false,false,false,false,false,false,false,false,false,false</filters>
		  <colAligns>left,left,left,left,left,left,left,left,left,left,left,left,left</colAligns>
	    </table>
	    <query>
	     <queryTable width= "606px" maxRows = "10" title = "" showError="false" showScroll="ALWAYS">
          <headers><xsl:value-of select='resource:getString($constants,"location")'/>,<xsl:value-of select='resource:getString($constants,"externalId")'/>,<xsl:value-of select='resource:getString($constants,"aptSuite")'/>,
				  <xsl:value-of select='resource:getString($constants,"address")'/>,<xsl:value-of select='resource:getString($constants,"city")'/>,
                  <xsl:value-of select='resource:getString($constants,"state")'/>, <xsl:value-of select='resource:getString($constants,"country")'/>,
                  <xsl:value-of select='resource:getString($constants,"zipcode")'/>,<xsl:value-of select='resource:getString($constants,"workNumber")'/>,<xsl:value-of select='resource:getString($constants,"homeNumber")'/>,
				  <xsl:value-of select='resource:getString($constants,"cellNumber")'/>,<xsl:value-of select='resource:getString($constants,"faxNumber")'/>,
				  <xsl:value-of select='resource:getString($constants,"email")'/></headers>
		  <widths>115,130,130,130,130,60,130,100,90,90,90,150,145</widths>
		  <editors>
		  <textbox case= "mixed"/>
		  <textbox case= "mixed"/>
		  <textbox case= "mixed"/>
		  <textbox case= "mixed"/>
		  <textbox case= "mixed"/>		  
			<dropdown case="upper"  width="45px" multiSelect = "true"/>
			<dropdown case="mixed" width="110px" multiSelect = "true"/>
			<textbox case= "mixed"/>
			<textbox case= "mixed"/>
			<textbox case= "mixed"/>
			<textbox case= "mixed"/>
			<textbox case= "mixed"/>
			<textbox case= "mixed"/>		 
		</editors>
		<fields>
		  <xsl:value-of select='location:getLocation($loc)'/>,<xsl:value-of select='location:getExternalId($loc)'/>,<xsl:value-of select='addr:getMultipleUnit($locAddr)'/>,<xsl:value-of select='addr:getStreetAddress($locAddr)'/>,<xsl:value-of select='addr:getCity($locAddr)'/>,<xsl:value-of select='addr:getState($locAddr)'/>,<xsl:value-of select='addr:getCountry($locAddr)'/>,<xsl:value-of select='addr:getZipCode($locAddr)'/>,<xsl:value-of select='addr:getWorkPhone($locAddr)'/>,<xsl:value-of select='addr:getHomePhone($locAddr)'/>,<xsl:value-of select='addr:getCellPhone($locAddr)'/>,<xsl:value-of select='addr:getFaxPhone($locAddr)'/>,<xsl:value-of select='addr:getEmail($locAddr)'/>																				
		  </fields>
		  <sorts>true,true,true,true,true,true,true,true,true,true,true,true,true</sorts>
		  <filters>false,false,false,false,false,false,false,false,false,false,false,false,false</filters>
		  <colAligns>left,left,left,left,left,left,left,left,left,left,left,left,left</colAligns>
	    </queryTable>
	    </query>
	  </widget>							
							
	  <widget style="WhiteContentPanel" halign="center">
									<appButton action="removeRow" onclick="this" style="Button" key="removeAddressButton">
									<HorizontalPanel>
              						<AbsolutePanel style="RemoveRowButtonImage"/>
						              <widget>
                						<text><xsl:value-of select='resource:getString($constants,"removeRow")'/></text>
							              </widget>
							              </HorizontalPanel>
						            </appButton>
						            </widget> 	  	  
	</VerticalPanel> 									
  </tab>
  
  <tab key="notesTab" text="{resource:getString($constants,'note')}">
						<VerticalPanel key="secMod3" height="164px" xsi:type="Panel">
							<TablePanel key="noteFormPanel" style="Form" >
										<row>
										
												<text style="Prompt"><xsl:value-of select='resource:getString($constants,"subject")'/>:</text>
										<textbox case="mixed" key="{note:getSubject($note)}" width="420px" max="60" showError="false" tab="{note:getText($note)},{note:getText($note)}"/>
					
										<appButton action="standardNote" onclick="this" key="standardNoteButton" style="Button">
										<HorizontalPanel>
              							<AbsolutePanel style="StandardNoteButtonImage"/>
						              
                						<text><xsl:value-of select='resource:getString($constants,"standardNote")'/></text>
							             
							              </HorizontalPanel>
						            </appButton>
						            
										</row>
										<row>
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"note")'/>:</text>
										</widget>
										<widget colspan="2">
										<textarea width="549px" height="50px" case="mixed" key="{note:getText($note)}" showError="false" tab="{note:getSubject($note)},{note:getSubject($note)}"/>
										</widget>
										</row>
								 
							<row>								
								<html key="spacer" xml:space="preserve"> </html>
								<widget colspan="2">
								<HorizontalPanel style="notesPanelContainer">
								<VerticalPanel key="notesPanel" style="NotesPanel" valign="top" onclick="this" height="170px" width="549px"  overflowY="scroll">
								
								</VerticalPanel>
								</HorizontalPanel>
								</widget>
							</row>
						</TablePanel>
						</VerticalPanel>
					</tab>
   </TabPanel>   
  </VerticalPanel>
  <HorizontalPanel width = "10px"/>
  </VerticalPanel>
  </HorizontalPanel> 
</display>
							  
<rpc key= "display">	
  <integer key="{meta:getId($pro)}" type="integer" required="false"/>				
  <string key="{meta:getLastName($pro)}"  required = "true"/>
  <string key="{meta:getFirstName($pro)}"  required="false"/> 
  <string key="{meta:getNpi($pro)}" required="false"/>
  <string key="{meta:getMiddleName($pro)}"  required="false"/>	
  <dropdown key="{meta:getTypeId($pro)}" type="integer" required = "true"/>
   <rpc key = "addresses">
    <table key="providerAddressTable"/>						      		       
   </rpc>
   <rpc key = "notes"> 
    <string key="{note:getSubject($note)}" required="false"/>
    <string key="{note:getText($note)}" required="false"/>
    <string key="notesPanel"/>
   </rpc>
   <string key="provTabPanel" reset="false">addressesTab</string>
</rpc>
					   
<rpc key= "query">     
  <queryInteger key="{meta:getId($pro)}" type="integer" />				
  <queryString key="{meta:getLastName($pro)}" />
  <queryString key="{meta:getFirstName($pro)}" /> 
  <queryString key="{meta:getNpi($pro)}" />
  <queryString key="{meta:getMiddleName($pro)}" />	  
  <queryString key="{note:getSubject($note)}" />
  <queryString key="{note:getText($note)}" /> 
  <dropdown key="{meta:getTypeId($pro)}" type="integer" required = "false"/>                            
  <queryString key="{location:getLocation($loc)}" required="false"/>
  <queryString key="{location:getExternalId($loc)}" required="false"/>
  <queryString key="{addr:getMultipleUnit($locAddr)}" required="false"/>
  <queryString key="{addr:getStreetAddress($locAddr)}" required="false"/>
  <queryString key="{addr:getCity($locAddr)}" required="false"/>
  <dropdown key="{addr:getState($locAddr)}" required="false"/>
  <queryString key="{addr:getZipCode($locAddr)}" required="false"/>
  <queryString key="{addr:getWorkPhone($locAddr)}" required="false"/>
  <queryString key="{addr:getHomePhone($locAddr)}" required="false"/>
  <queryString key="{addr:getCellPhone($locAddr)}" required="false"/>
  <queryString key="{addr:getFaxPhone($locAddr)}" required="false"/>
  <queryString key="{addr:getEmail($locAddr)}" required="false"/>
  <dropdown key="{addr:getCountry($locAddr)}" required="false"/>
  <model key="providerAddressTable"/>
</rpc>

<rpc key="queryByLetter">
  <queryString key="{meta:getLastName($pro)}"/>
</rpc> 
</screen>
</xsl:template>
</xsl:stylesheet>     