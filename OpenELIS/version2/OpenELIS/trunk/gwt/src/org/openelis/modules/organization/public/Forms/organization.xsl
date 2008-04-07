<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.server.constants.UTFResource"
                xmlns:locale="xalan://java.util.Locale" 
                xmlns:organizationMeta="xalan://org.openelis.meta.OrganizationMeta" 
                xmlns:orgAddressMeta="xalan://org.openelis.meta.OrganizationAddressMeta"
                xmlns:orgNoteMeta="xalan://org.openelis.meta.OrganizationNoteMeta"
                xmlns:parentOrgMeta="xalan://org.openelis.meta.OrganizationParentOrganizationMeta"
                xmlns:orgContactMeta="xalan://org.openelis.meta.OrganizationContactMeta"
                xmlns:orgContactAddressMeta="xalan://org.openelis.meta.OrganizationContactAddressMeta"
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZOneColumn.xsl"/>

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.server.constants.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
  <xalan:component prefix="organizationMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrganizationMeta"/>
  </xalan:component>

  <xalan:component prefix="orgAddressMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrganizationAddressMeta"/>
  </xalan:component>
  
  <xalan:component prefix="parentOrgMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrganizationParentOrganizationMeta"/>
  </xalan:component>
  
    <xalan:component prefix="orgNoteMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrganizationNoteMeta"/>
  </xalan:component>
  
  <xalan:component prefix="orgContactMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrganizationContactMeta"/>
  </xalan:component>
  
  <xalan:component prefix="orgContactAddressMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrganizationContactAddressMeta"/>
  </xalan:component>
  
  <xsl:template match="doc"> 
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle('org.openelis.modules.main.server.constants.OpenELISConstants',locale:new(string($language)))"/>
<screen id="Organization" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<panel layout="horizontal" style="WhiteContentPanel" spacing="0" padding="0" xsi:type="Panel">
			<!--left table goes here -->
				<aToZ height="425px" width="100%" key="hideablePanel" visible="false" maxRows="19" title="{resource:getString($constants,'name')}" tablewidth="auto" colwidths="175">
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
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"id")'/>:</text>
										</widget>
										<widget>
										<textbox key="{organizationMeta:id()}" width="75px" tab="{organizationMeta:name()},{organizationMeta:isActive()}"/>
										</widget>
									</row>
									<row>								
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"name")'/>:</text>
										</widget>
										<widget>
										<textbox case="upper" key="{organizationMeta:name()}" width="225px" max="40" tab="{orgAddressMeta:multipleUnit()},{organizationMeta:name()}"/>
										</widget>text
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"city")'/>:</text>
										</widget>
										<widget colspan="3">
											<textbox case="upper" key="{orgAddressMeta:city()}" width="212px" max="30" tab="{orgAddressMeta:state()},{orgAddressMeta:streetAddress()}"/>
										</widget>		
									</row>
									<row>
									<panel layout="vertical" xsi:type="Panel" style="FormVerticalSpacing"/>
									</row>						
									<row>
									<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"aptSuite")'/>:</text>
										</widget>
										
										<widget>
											<textbox case="upper" key="{orgAddressMeta:multipleUnit()}" width="212px" max="30" tab="{orgAddressMeta:streetAddress()},{organizationMeta:name()}"/>
										</widget>
										<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"state")'/>:</text>
												</widget>
										<!--<panel layout="horizontal" xsi:type="Panel" padding="0" spacing="0">-->
												<widget>
													<autoDropdown key="{orgAddressMeta:state()}" case="upper" width="40px" popWidth="auto" tab="{orgAddressMeta:zipCode()},{orgAddressMeta:city()}">
													<widths>40</widths>
													</autoDropdown>
												</widget>
									<!--<panel layout="horizontal" width="24px" xsi:type="Panel"/>-->
											<widget>
													<text style="Prompt"><xsl:value-of select='resource:getString($constants,"zipcode")'/>:</text>
												</widget>
												<!--<panel layout="horizontal" width="2px" xsi:type="Panel"/>-->
												<widget>
													<maskedbox key="{orgAddressMeta:zipCode()}" width="70" mask="99999-9999" tab="{orgAddressMeta:country()},{orgAddressMeta:state()}"/>
												</widget>
									<!--</panel>									-->
									</row>	
									<row>
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"street")'/>:</text>
										</widget>
										<widget>
											<textbox case="upper" key="{orgAddressMeta:streetAddress()}" width="212px" max="30" tab="{orgAddressMeta:city()},{orgAddressMeta:multipleUnit()}"/>
										</widget>	
											<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"country")'/>:</text>
										</widget>
										<widget colspan="3">
												<autoDropdown key="{orgAddressMeta:country()}" case="mixed" width="175px" popWidth="auto" tab="{parentOrgMeta:name()},{orgAddressMeta:zipCode()}">
													<widths>175</widths>
												</autoDropdown>
										</widget>					
									</row>
								<row>
									<panel layout="vertical" xsi:type="Panel" style="FormVerticalSpacing"/>
								</row>
								<row>
									<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"parentOrganization")'/>:</text>
										</widget>
										<widget>
										<autoDropdown cat="parentOrg" key="{parentOrgMeta:name()}" case="upper" serviceUrl="OpenELISServlet?service=org.openelis.modules.organization.server.OrganizationService" width="225px" popWidth="225px" tab="{organizationMeta:isActive()},{orgAddressMeta:country()}">
										<headers>Name,Street,City,St</headers>
										<widths>180,110,100,20</widths>
										</autoDropdown>
										<query>
											<textbox case="upper" width="241px" tab="{organizationMeta:isActive()},{orgAddressMeta:country()}"/>
										</query>
										</widget>
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"active")'/>:</text>
										</widget>
										<widget colspan="3">
											<check key="{organizationMeta:isActive()}" tab="contactsTable,{parentOrgMeta:name()}"/>
										</widget>
								</row>
								</panel>
<!-- tabbed panel needs to go here -->
				<panel height="200px" key="orgTabPanel" halign="center" layout="tab" xsi:type="Tab">
					<!-- TAB 1 -->
					<tab key="tab1" text="{resource:getString($constants,'contact')}">
							<panel layout="vertical" spacing="0" padding="0" xsi:type="Panel">
							<widget valign="top">
								<table width="567px" key="contactsTable" manager="OrganizationContactsTable" maxRows="8" title="" showError="false">
										<headers><xsl:value-of select='resource:getString($constants,"type")'/>,<xsl:value-of select='resource:getString($constants,"contactName")'/>,<xsl:value-of select='resource:getString($constants,"aptSuite")'/>,
										<xsl:value-of select='resource:getString($constants,"address")'/>,<xsl:value-of select='resource:getString($constants,"city")'/>,
										<xsl:value-of select='resource:getString($constants,"state")'/>,<xsl:value-of select='resource:getString($constants,"zipcode")'/>,
										<xsl:value-of select='resource:getString($constants,"workNumber")'/>,<xsl:value-of select='resource:getString($constants,"homeNumber")'/>,
										<xsl:value-of select='resource:getString($constants,"cellNumber")'/>,<xsl:value-of select='resource:getString($constants,"faxNumber")'/>,
										<xsl:value-of select='resource:getString($constants,"email")'/>,<xsl:value-of select='resource:getString($constants,"country")'/></headers>
										<widths>106,130,130,130,130,56,68,100,90,90,90,150,126</widths>
										<editors>
											<autoDropdown case="mixed" width="90px" popWidth="auto">
											  <widths>90</widths>
											</autoDropdown>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<autoDropdown case="upper" width="40px" popWidth="auto">
												 <widths>40</widths>
											</autoDropdown>
										 	<textbox case="mixed"/>
										 	<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
											<autoDropdown case="mixed" width="110px" popWidth="auto">
											  <widths>110</widths>
											</autoDropdown>
										</editors>
										<fields>
											<dropdown/>
											<string required="true"/>
											<string/>
											<string/>
											<string/>
											<dropdown/>
											<string/>
											<string/>
											<string/>
											<string/>
											<string/>
											<string/>
											<dropdown/>
										</fields>
										<sorts>true,true,true,true,true,true,true,true,true,true,true,true,true</sorts>
										<filters>false,false ,false,false,false,false ,false,false,false,false ,false,false,false</filters>
										<colAligns>left,left,left,left,left,left,left,left,left,left,left,left,left</colAligns>
									</table>
									<query>
									<queryTable width="567px" title="" maxRows="8" showError="false">
										<headers><xsl:value-of select='resource:getString($constants,"type")'/>,<xsl:value-of select='resource:getString($constants,"contactName")'/>,<xsl:value-of select='resource:getString($constants,"aptSuite")'/>,
										<xsl:value-of select='resource:getString($constants,"address")'/>,<xsl:value-of select='resource:getString($constants,"city")'/>,
										<xsl:value-of select='resource:getString($constants,"state")'/>,<xsl:value-of select='resource:getString($constants,"zipcode")'/>,
										<xsl:value-of select='resource:getString($constants,"workNumber")'/>,<xsl:value-of select='resource:getString($constants,"homeNumber")'/>,
										<xsl:value-of select='resource:getString($constants,"cellNumber")'/>,<xsl:value-of select='resource:getString($constants,"faxNumber")'/>,
										<xsl:value-of select='resource:getString($constants,"email")'/>,<xsl:value-of select='resource:getString($constants,"country")'/></headers>
										<widths>106,130,130,130,130,56,68,100,90,90,90,150,126</widths>
										<editors>
											<autoDropdown case="mixed" width="90px" popWidth="auto" multiSelect="true">
											  <widths>90</widths>
											</autoDropdown>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<autoDropdown case="upper" width="40px" popWidth="auto" multiSelect="true" >
												<widths>40</widths>
											</autoDropdown>
										 	<textbox case="mixed"/>
										 	<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
										 	<autoDropdown case="mixed" width="110px" popWidth="auto" multiSelect="true">
											  <widths>110</widths>
											</autoDropdown>									 	
										</editors>
										<fields><xsl:value-of select='orgContactMeta:contactType()'/>,<xsl:value-of select='orgContactMeta:name()'/>,<xsl:value-of select='orgContactAddressMeta:multipleUnit()'/>,
										<xsl:value-of select='orgContactAddressMeta:streetAddress()'/>,<xsl:value-of select='orgContactAddressMeta:city()'/>,<xsl:value-of select='orgContactAddressMeta:state()'/>,
										<xsl:value-of select='orgContactAddressMeta:zipCode()'/>,<xsl:value-of select='orgContactAddressMeta:workPhone()'/>,<xsl:value-of select='orgContactAddressMeta:homePhone()'/>,
										<xsl:value-of select='orgContactAddressMeta:cellPhone()'/>,<xsl:value-of select='orgContactAddressMeta:faxPhone()'/>,<xsl:value-of select='orgContactAddressMeta:email()'/>,
										<xsl:value-of select='orgContactAddressMeta:country()'/></fields>
									</queryTable>
									</query>
								</widget>

									<widget style="WhiteContentPanel">
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
										<textbox case="mixed" key="{orgNoteMeta:subject()}" width="405px" max="60" showError="false"/>
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
										<textarea width="524px" height="50px" case="mixed" key="{orgNoteMeta:text()}" showError="false"/>
										</widget>
										</row>
								 
							<row>
								<widget>
								<html key="spacer" xml:space="preserve"> </html>
								</widget>
								<widget colspan="2">
								<panel style="notesPanelContainer" layout="horizontal" xsi:type="Panel">
								<panel key="notesPanel" style="NotesPanel" valign="top" onclick="this" height="145px" width="524px" layout="vertical" overflowX="auto" overflowY="scroll" xsi:type="Panel">
								
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
  	  <number key="{organizationMeta:id()}" type="integer" required="false"/>
      <number key="{organizationMeta:addressId()}" required="false" type="integer"/>
      <string key="{organizationMeta:name()}" max="40" required="true"/>
      <string key="{orgAddressMeta:streetAddress()}" max="30" required="true"/>
      <string key="{orgAddressMeta:multipleUnit()}" max="30" required="false"/>
      <string key="{orgAddressMeta:city()}" max="30" required="true"/>
      <string key="{orgAddressMeta:zipCode()}" max="10" required="true"/>
      <string key="{organizationMeta:isActive()}" required="false"/>
      <string key="{orgNoteMeta:subject()}" max="60" required="false"/>
      <string key="{orgNoteMeta:text()}" required="false"/>
      <dropdown key="{parentOrgMeta:name()}" type="integer" required="false"/> 
      <table key="contactsTable"/>
      <dropdown key="{orgAddressMeta:state()}" required="false"/>
      <dropdown key="{orgAddressMeta:country()}" required="true"/>
	</rpc>
	<rpc key="query">
      <queryNumber key="{organizationMeta:id()}" type="integer"/>
      <queryString key="{organizationMeta:name()}"/>
      <queryString key="{orgAddressMeta:streetAddress()}"/>
      <queryString key="{orgAddressMeta:multipleUnit()}" value="query"/>
      <queryString key="{orgAddressMeta:city()}"/>
      <queryString key="{orgAddressMeta:zipCode()}"/>
      <queryString key="{parentOrgMeta:name()}"/>
      <queryString key="{orgNoteMeta:subject()}"/>
      <queryString key="{orgNoteMeta:text()}"/>
      <dropdown key="{orgAddressMeta:state()}" required="false"/>
      <dropdown key="{orgAddressMeta:country()}" required="false"/>
      <queryString key="{organizationMeta:isActive()}" required="false"/>
      <dropdown key="{orgContactMeta:contactType()}" required="false"/>
	  <queryString key="{orgContactMeta:name()}" required="false"/>
	  <queryString key="{orgContactAddressMeta:multipleUnit()}" required="false"/>
	  <queryString key="{orgContactAddressMeta:streetAddress()}" required="false"/>
	  <queryString key="{orgContactAddressMeta:city()}" required="false"/>
	  <dropdown key="{orgContactAddressMeta:state()}" required="false"/>
	  <queryString key="{orgContactAddressMeta:zipCode()}" required="false"/>
      <queryString key="{orgContactAddressMeta:workPhone()}" required="false"/>
      <queryString key="{orgContactAddressMeta:homePhone()}" required="false"/>
      <queryString key="{orgContactAddressMeta:cellPhone()}" required="false"/>
	  <queryString key="{orgContactAddressMeta:faxPhone()}" required="false"/>
      <queryString key="{orgContactAddressMeta:email()}" required="false"/>
	  <dropdown key="{orgContactAddressMeta:country()}" required="false"/>
	</rpc>
	<rpc key="queryByLetter">
		<queryString key="{organizationMeta:name()}"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>