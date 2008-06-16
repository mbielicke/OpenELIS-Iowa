<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale" 
                xmlns:meta="xalan://org.openelis.newmeta.OrganizationMetaMap"
                xmlns:addr="xalan://org.openelis.newmeta.AddressMeta"
                xmlns:contact="xalan://org.openelis.newmeta.OrganizationContactMetaMap"
                xmlns:note="xalan://org.openelis.newmeta.NoteMeta"
                xmlns:parent="xalan://org.openelis.newmeta.OrganizationMeta" 
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
    <xalan:script lang="javaclass" src="xalan://org.openelis.newmeta.OrganizationMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="addr">
    <xalan:script lang="javaclass" src="xalan://org.openelis.newmeta.AddressMeta"/>
  </xalan:component>
  
  <xalan:component prefix="contact">
    <xalan:script lang="javaclass" src="xalan://org.openelis.newmeta.OrganizationContactMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="note">
    <xalan:script lang="javaclass" src="xalan://org.openelis.newmeta.NotetMeta"/>
  </xalan:component>
  
  <xalan:component prefix="parent">
    <xalan:script lang="javaclass" src="xalan://org.openelis.newmeta.OrganizationMeta"/>
  </xalan:component>  
  
  <xsl:template match="doc"> 
    <xsl:variable name="org" select="meta:new()"/>
    <xsl:variable name="addr" select="meta:getAddress($org)"/>
    <xsl:variable name="cont" select="meta:getOrganizationContact($org)"/>
    <xsl:variable name="parent" select="meta:getParentOrganization($org)"/>
    <xsl:variable name="note" select="meta:getNote($org)"/>
    <xsl:variable name="contAddr" select="contact:getAddress($cont)"/>
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="Organization" name="{resource:getString($constants,'organization')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<panel layout="horizontal" style="WhiteContentPanel" spacing="0" padding="0" xsi:type="Panel">
			<!--left table goes here -->
			  <panel layout="collapse" key="collapsePanel">
				<azTable height="425px" width="100%" key="azTable" maxRows="19" title="{resource:getString($constants,'name')}" tablewidth="auto" colwidths="175">
    				 <buttonPanel key="atozButtons">
	    			   <xsl:call-template name="aToZLeftPanelButtons"/>		
		    		 </buttonPanel>
				</azTable>
			  </panel>
			<panel layout="vertical" spacing="0" xsi:type="Panel">
		<!--button panel code-->
		<panel xsi:type="Absolute" layout="absolute" spacing="0" style="ButtonPanelContainer">
			<widget>
    			<buttonPanel key="buttons">
    			<xsl:call-template name="queryButton">
    				<xsl:with-param name="language"><xsl:value-of select="language"/></xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="previousButton">
    			<xsl:with-param name="language"><xsl:value-of select="language"/></xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="nextButton">
    			<xsl:with-param name="language"><xsl:value-of select="language"/></xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="buttonPanelDivider"/>
    			<xsl:call-template name="addButton">
    			<xsl:with-param name="language"><xsl:value-of select="language"/></xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="updateButton">
    			<xsl:with-param name="language"><xsl:value-of select="language"/></xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="buttonPanelDivider"/>
    			<xsl:call-template name="commitButton">
    			<xsl:with-param name="language"><xsl:value-of select="language"/></xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="abortButton">
    			<xsl:with-param name="language"><xsl:value-of select="language"/></xsl:with-param>
    			</xsl:call-template>
				</buttonPanel>
 			</widget>
		</panel>
		<!--end button panel-->
		
					<panel layout="vertical" xsi:type="Panel">
							<panel key="secMod2" layout="table" style="Form" xsi:type="Table">
									<row>
									<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"id")'/>:</text>
										</widget>
										<widget>
										<textbox key="{meta:getId($org)}" width="75px" tab="{meta:getName($org)},{meta:getIsActive($org)}"/>
										</widget>
									</row>
									<row>								
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"name")'/>:</text>
										</widget>
										<widget>
										<textbox case="upper" key="{meta:getName($org)}" width="225px" max="40" tab="{addr:getMultipleUnit($addr)},{meta:getId($org)}"/>
										</widget>
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"city")'/>:</text>
										</widget>
										<widget colspan="3">
											<textbox case="upper" key="{addr:getCity($addr)}" width="212px" max="30" tab="{addr:getState($addr)},{addr:getStreetAddress($addr)}"/>
										</widget>		
									</row>				
									<row>
									<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"aptSuite")'/>:</text>
										</widget>
										
										<widget>
											<textbox case="upper" key="{addr:getMultipleUnit($addr)}" width="212px" max="30" tab="{addr:getStreetAddress($addr)},{meta:getName($org)}"/>
										</widget>
										<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"state")'/>:</text>
												</widget>
												<widget>
													<autoDropdown key="{addr:getState($addr)}" case="upper" width="40px" tab="{addr:getZipCode($addr)},{addr:getCity($addr)}"/>
												</widget>
											<widget>
													<text style="Prompt"><xsl:value-of select='resource:getString($constants,"zipcode")'/>:</text>
												</widget>
												<widget>
													<maskedbox key="{addr:getZipCode($addr)}" width="70" mask="99999-9999" tab="{addr:getCountry($addr)},{addr:getState($addr)}"/>
												</widget>
									</row>	
									<row>
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"address")'/>:</text>
										</widget>
										<widget>
											<textbox case="upper" key="{addr:getStreetAddress($addr)}" width="212px" max="30" tab="{addr:getCity($addr)},{addr:getMultipleUnit($addr)}"/>
										</widget>	
											<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"country")'/>:</text>
										</widget>
										<widget colspan="3">
												<autoDropdown key="{addr:getCountry($addr)}" case="mixed" width="175px" tab="{parent:getName($parent)},{addr:getZipCode($addr)}"/>
										</widget>					
									</row>
								<row>
									<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"parentOrganization")'/>:</text>
										</widget>
										<widget>
										<autoDropdown cat="parentOrg" key="{parent:getName($parent)}" case="upper" serviceUrl="OpenELISServlet?service=org.openelis.modules.organization.server.OrganizationService" width="225px" tab="{meta:getIsActive($org)},{addr:getCountry($addr)}">
										<headers>Name,Street,City,St</headers>
										<widths>180,110,100,20</widths>
										</autoDropdown>
										<query>
											<textbox case="upper" width="241px" tab="{meta:getIsActive($org)},{addr:getCountry($addr)}"/>
										</query>
										</widget>
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"active")'/>:</text>
										</widget>
										<widget colspan="3">
											<check key="{meta:getIsActive($org)}" tab="{meta:getId($org)},{parent:getName($parent)}"/>
										</widget>
								</row>
								</panel>
<!-- tabbed panel needs to go here -->
				<panel height="200px" key="orgTabPanel" halign="center" layout="tab" xsi:type="Tab">
					<!-- TAB 1 -->
					<tab key="tab1" text="{resource:getString($constants,'contact')}">
							<panel layout="vertical" spacing="0" padding="0" xsi:type="Panel" overflow="hidden">
							<widget valign="top">
								<table width="574px" key="contactsTable" manager="OrganizationContactsTable" maxRows="8" title="" showError="false" showScroll="true">
										<headers><xsl:value-of select='resource:getString($constants,"type")'/>,<xsl:value-of select='resource:getString($constants,"contactName")'/>,<xsl:value-of select='resource:getString($constants,"aptSuite")'/>,
										<xsl:value-of select='resource:getString($constants,"address")'/>,<xsl:value-of select='resource:getString($constants,"city")'/>,
										<xsl:value-of select='resource:getString($constants,"state")'/>,<xsl:value-of select='resource:getString($constants,"zipcode")'/>,
										<xsl:value-of select='resource:getString($constants,"country")'/>,<xsl:value-of select='resource:getString($constants,"workNumber")'/>,
										<xsl:value-of select='resource:getString($constants,"homeNumber")'/>,<xsl:value-of select='resource:getString($constants,"cellNumber")'/>,
										<xsl:value-of select='resource:getString($constants,"faxNumber")'/>,<xsl:value-of select='resource:getString($constants,"email")'/></headers>
										<widths>106,130,130,130,130,56,68,126,100,90,90,90,150</widths>
										<editors>
											<autoDropdown case="mixed" width="90px"/>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<autoDropdown case="upper" width="40px"/>
										 	<textbox case="mixed"/>
										 	<autoDropdown case="mixed" width="110px"/>
										 	<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
										</editors>
										<fields>
											<dropdown key="{contact:getContactTypeId($cont)}" required="true"/>
											<string key="{contact:getName($cont)}" required="true"/>
											<string key="{addr:getMultipleUnit($contAddr)}"/>
											<string key="{addr:getStreetAddress($contAddr)}" required="true"/>
											<string key="{addr:getCity($contAddr)}" required="true"/>
											<dropdown key="{addr:getState($contAddr)}"/>
											<string key="{addr:getZipCode($contAddr)}" required="true"/>
											<dropdown key="{addr:getCountry($contAddr)}" required="true"/>
											<string key="{addr:getWorkPhone($contAddr)}"/>
											<string key="{addr:getHomePhone($contAddr)}"/>
											<string key="{addr:getCellPhone($contAddr)}"/>
											<string key="{addr:getFaxPhone($contAddr)}"/>
											<string key="{addr:getEmail($contAddr)}"/>
										</fields>
										<sorts>true,true,true,true,true,true,true,true,true,true,true,true,true</sorts>
										<filters>false,false ,false,false,false,false ,false,false,false,false ,false,false,false</filters>
										<colAligns>left,left,left,left,left,left,left,left,left,left,left,left,left</colAligns>
									</table>
									<query>
									<queryTable width="592px" title="" maxRows="8" showError="false">
										<headers><xsl:value-of select='resource:getString($constants,"type")'/>,<xsl:value-of select='resource:getString($constants,"contactName")'/>,
										<xsl:value-of select='resource:getString($constants,"aptSuite")'/>,<xsl:value-of select='resource:getString($constants,"address")'/>,
										<xsl:value-of select='resource:getString($constants,"city")'/>,<xsl:value-of select='resource:getString($constants,"state")'/>,
										<xsl:value-of select='resource:getString($constants,"zipcode")'/>,<xsl:value-of select='resource:getString($constants,"country")'/>,
										<xsl:value-of select='resource:getString($constants,"workNumber")'/>,<xsl:value-of select='resource:getString($constants,"homeNumber")'/>,
										<xsl:value-of select='resource:getString($constants,"cellNumber")'/>,<xsl:value-of select='resource:getString($constants,"faxNumber")'/>,
										<xsl:value-of select='resource:getString($constants,"email")'/></headers>
										<widths>106,130,130,130,130,56,68,126,100,90,90,90,150</widths>
										<editors>
											<autoDropdown case="mixed" width="90px" multiSelect="true"/>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<autoDropdown case="upper" width="40px" multiSelect="true"/>
										 	<textbox case="mixed"/>
										 	<autoDropdown case="mixed" width="110px" multiSelect="true"/>
										 	<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>		 	
										</editors>
										<fields><xsl:value-of select='contact:getContactTypeId($cont)'/>,<xsl:value-of select='contact:getName($cont)'/>,<xsl:value-of select='addr:getMultipleUnit($contAddr)'/>,
										<xsl:value-of select='addr:getStreetAddress($contAddr)'/>,<xsl:value-of select='addr:getCity($contAddr)'/>,<xsl:value-of select='addr:getState($contAddr)'/>,
										<xsl:value-of select='addr:getZipCode($contAddr)'/>,<xsl:value-of select='addr:getCountry($contAddr)'/></fields>,<xsl:value-of select='addr:getWorkPhone($contAddr)'/>,
										<xsl:value-of select='addr:getHomePhone($contAddr)'/>,	<xsl:value-of select='addr:getCellPhone($contAddr)'/>,<xsl:value-of select='addr:getFaxPhone($contAddr)'/>,
										<xsl:value-of select='addr:getEmail($contAddr)'/>										
									</queryTable>
									</query>
								</widget>
								<widget style="WhiteContentPanel" halign="center">									
									<appButton action="removeRow" onclick="this" style="Button" key="removeContactButton">
									<panel xsi:type="Panel" layout="horizontal">
              						<panel xsi:type="Absolute" layout="absolute" style="RemoveRowButtonImage"/>
						              <widget>
                						<text><xsl:value-of select='resource:getString($constants,"removeRow")'/></text>
							              </widget>
							              </panel>
						            </appButton>
						            </widget>
							</panel>
								<!-- end TAB 1 data table -->
						<!-- END TAB 1 -->
					</tab>			
					<!-- start TAB 2 -->
					<tab key="noteTab" text="{resource:getString($constants,'note')}">
						<panel key="secMod3" layout="vertical" width="100%" height="164px" spacing="0" padding="0" xsi:type="Panel">
							<panel key="noteFormPanel" layout="table" style="Form" xsi:type="Table" padding="0" spacing="0">
										<row>
										<widget>
												<text style="Prompt"><xsl:value-of select='resource:getString($constants,"subject")'/></text>
										</widget>
										<widget>
										<textbox case="mixed" key="{note:getSubject($note)}" width="435px" max="60" showError="false" tab="{note:getText($note)},{note:getText($note)}"/>
										</widget>
										<widget>
										<appButton action="standardNote" onclick="this" key="standardNoteButton" style="Button">
										<panel xsi:type="Panel" layout="horizontal">
              							<panel xsi:type="Absolute" layout="absolute" style="StandardNoteButtonImage"/>
						              <widget>
                						<text><xsl:value-of select='resource:getString($constants,"standardNote")'/></text>
							              </widget>
							              </panel>
						            </appButton>
						            </widget>
										</row>
										<row>
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"note")'/></text>
										</widget>
										<widget colspan="2">
										<textarea width="549px" height="50px" case="mixed" key="{note:getText($note)}" showError="false" tab="{note:getSubject($note)},{note:getSubject($note)}"/>										
										</widget>
										</row>
								 
							<row>
								<widget>
								<html key="spacer" xml:space="preserve"> </html>
								</widget>
								<widget colspan="2">
								<panel style="notesPanelContainer" layout="horizontal" xsi:type="Panel">
								<panel key="notesPanel" style="NotesPanel" valign="top" onclick="this" height="137px" width="549px" layout="vertical" overflowX="auto" overflowY="scroll" xsi:type="Panel">								
								</panel>
								</panel>
								</widget>
							</row>
						</panel>
						</panel>
					</tab>
					
				</panel>
				</panel>
			</panel>
		</panel>
	</display>
	<rpc key="display">
  	  <number key="{meta:getId($org)}" type="integer" required="false"/>
      <number key="{meta:getAddressId($org)}" required="false" type="integer"/>
      <string key="{meta:getName($org)}" max="40" required="true"/>
      <string key="{addr:getStreetAddress($addr)}" max="30" required="true"/>
      <string key="{addr:getMultipleUnit($addr)}" max="30" required="false"/>
      <string key="{addr:getCity($addr)}" max="30" required="true"/>
      <string key="{addr:getZipCode($addr)}" max="10" required="true"/>
      <check key="{meta:getIsActive($org)}" required="false"/>
      <string key="{note:getSubject($note)}" max="60" required="false"/>
      <string key="{note:getText($note)}" required="false"/>
      <dropdown key="{parent:getName($parent)}" type="integer" required="false"/> 
      <dropdown key="{addr:getState($addr)}" required="false"/>
      <dropdown key="{addr:getCountry($addr)}" required="true"/>
      <table key="contactsTable"/>
	</rpc>
	<rpc key="query">
      <queryNumber key="{meta:getId($org)}" type="integer"/>
      <queryString key="{meta:getName($org)}"/>
      <queryString key="{addr:getStreetAddress($addr)}"/>
      <queryString key="{addr:getMultipleUnit($addr)}" value="query"/>
      <queryString key="{addr:getCity($addr)}"/>
      <queryString key="{addr:getZipCode($addr)}"/>
      <queryString key="{parent:getName($parent)}"/>
      <queryString key="{note:getSubject($note)}"/>
      <queryString key="{note:getText($note)}"/>
      <dropdown key="{addr:getState($addr)}" required="false"/>
      <dropdown key="{addr:getCountry($addr)}" required="false"/>
      <queryCheck key="{meta:getIsActive($org)}" required="false"/>
      <table key="contactsTable"/>
      <dropdown key="{contact:getContactTypeId($cont)}" required="false"/>
	  <queryString key="{contact:getName($cont)}" required="false"/>
	  <queryString key="{addr:getMultipleUnit($contAddr)}" required="false"/>
	  <queryString key="{addr:getStreetAddress($contAddr)}" required="false"/>
	  <queryString key="{addr:getCity($contAddr)}" required="false"/>
	  <dropdown key="{addr:getState($contAddr)}" required="false"/>
	  <queryString key="{addr:getZipCode($contAddr)}" required="false"/>
      <queryString key="{addr:getWorkPhone($contAddr)}" required="false"/>
      <queryString key="{addr:getHomePhone($contAddr)}" required="false"/>
      <queryString key="{addr:getCellPhone($contAddr)}" required="false"/>
	  <queryString key="{addr:getFaxPhone($contAddr)}" required="false"/>
      <queryString key="{addr:getEmail($contAddr)}" required="false"/>
	  <dropdown key="{addr:getCountry($contAddr)}" required="false"/>
	</rpc>
	<rpc key="queryByLetter">
		<queryString key="{meta:getName($org)}"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>