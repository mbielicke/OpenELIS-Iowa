<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.server.constants.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZOneColumn.xsl"/>
<xsl:import href="buttonPanel.xsl"/>

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.server.constants.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>

  <xsl:template match="doc"> 
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle('org.openelis.modules.main.server.constants.OpenELISConstants',locale:new(string($language)))"/>
<screen id="Organization" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<panel layout="horizontal" style="WhiteContentPanel" spacing="0" padding="0" xsi:type="Panel">
			<!--left table goes here -->
				<aToZ height="425px" width="100%" key="hideablePanel" visible="false">
				 <panel layout="horizontal" style="ScreenLeftPanel" xsi:type="Panel" spacing="0">
				 <xsl:if test="string($language)='en'">
				<xsl:call-template name="aToZLeftPanelButtons"/>		
		</xsl:if>
				<table manager="OrganizationNameTable" width="auto" style="ScreenLeftTable" key="organizationsTable" maxRows="19" title="Name" showError="false">
							<widths>175</widths>
							<editors>
								<label/>
							</editors>
							<fields>
								<string/>
							</fields>
							<sorts>false</sorts>
							<filters>false</filters>
				</table>
				</panel>
				</aToZ>
			<panel layout="vertical" spacing="0" xsi:type="Panel">
		<!--button panel code-->
		<xsl:call-template name="buttonPanelTemplate">
		<xsl:with-param name="buttonsParam">qpn|au|cb</xsl:with-param>
		</xsl:call-template>

				<panel key="formDeck" layout="deck" xsi:type="Deck" align="left">
					<deck>
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
										<textbox key="orgId" width="75px" tab="orgName,isActive"/>
										</widget>
									</row>
									<row>								
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"name")'/>:</text>
										</widget>
										<widget>
										<textbox case="upper" key="orgName" width="225px" max="40" tab="multUnit,orgName"/>
										</widget>text
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"city")'/>:</text>
										</widget>
										<widget colspan="3">
											<textbox case="upper" key="city" width="212px" max="30" tab="state,streetAddress"/>
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
											<textbox case="upper" key="multUnit" width="212px" max="30" tab="streetAddress,orgName"/>
										</widget>
										<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"state")'/>:</text>
												</widget>
										<!--<panel layout="horizontal" xsi:type="Panel" padding="0" spacing="0">-->
												<widget>
													<autoDropdown cat="state" key="state" case="upper" serviceUrl="OpenELISServlet?service=org.openelis.modules.organization.server.OrganizationService" width="40px" dropdown="true" type="string" fromModel="true" tab="zipCode,city">
													<autoWidths>40</autoWidths>
													<autoEditors>
														<label/>
													</autoEditors>
													<autoFields>
														<string/>
													</autoFields>
													</autoDropdown>
													<query>
													<autoDropdown cat="state" case="upper" serviceUrl="OpenELISServlet?service=org.openelis.modules.organization.server.OrganizationService" width="40px" dropdown="true" type="string" fromModel="true" multiSelect="true" tab="zipCode,city">
													<autoWidths>40</autoWidths>
													<autoEditors>
														<label/>
													</autoEditors>
													<autoFields>
														<string/>
													</autoFields>
													</autoDropdown>
												</query>
												</widget>
									<!--<panel layout="horizontal" width="24px" xsi:type="Panel"/>-->
											<widget>
													<text style="Prompt"><xsl:value-of select='resource:getString($constants,"zipcode")'/>:</text>
												</widget>
												<!--<panel layout="horizontal" width="2px" xsi:type="Panel"/>-->
												<widget>
													<maskedbox key="zipCode" width="70" mask="99999-9999" tab="country,state"/>
												</widget>
									<!--</panel>									-->
									</row>									
									<row>
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"street")'/>:</text>
										</widget>
										<widget>
											<textbox case="upper" key="streetAddress" width="212px" max="30" tab="city,multUnit"/>
										</widget>	
											<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"country")'/>:</text>
										</widget>
										<widget colspan="3">
												<autoDropdown cat="country" key="country" case="mixed" serviceUrl="OpenELISServlet?service=org.openelis.modules.organization.server.OrganizationService" width="196px" dropdown="true" fromModel="true" type="string" tab="parentOrg,zipCode">
													<autoWidths>175</autoWidths>
													<autoEditors>
														<label/>
													</autoEditors>
													<autoFields>
														<string/>
													</autoFields>
												</autoDropdown>
											<query>
													<autoDropdown cat="country" case="mixed" serviceUrl="OpenELISServlet?service=org.openelis.modules.organization.server.OrganizationService" width="196px" dropdown="true" type="string" fromModel="true" multiSelect="true" tab="parentOrg,zipCode">
													<autoWidths>175</autoWidths>
													<autoEditors>
														<label/>
													</autoEditors>
													<autoFields>
														<string/>
													</autoFields>
													</autoDropdown>
											</query>
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
										<auto cat="parentOrg" case="upper" serviceUrl="OpenELISServlet?service=org.openelis.modules.organization.server.OrganizationService" key="parentOrg" width="225px" type="integer" popupHeight="150px" tab="isActive,country">
										<autoHeaders>Name,Street,City,St</autoHeaders>
										<autoWidths>180,110,100,20</autoWidths>
										<autoEditors>
											<label/>
											<label/>
											<label/>
											<label/>
										</autoEditors>
										<autoFields>
											<string/>
											<string/>
											<string/>
											<string/>
										</autoFields>
										</auto>
										<query>
											<textbox case="upper" width="225px" tab="isActive,country"/>
										</query>
										</widget>

										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"active")'/>:</text>
										</widget>
										<widget colspan="3">
											<check key="isActive" tab="contactsTable,parentOrg"/>
											<query>
											<autoDropdown cat="isActive" case="upper" serviceUrl="OpenELISServlet?service=org.openelis.modules.organization.server.OrganizationService" width="40px" dropdown="true" type="string" multiSelect="true" tab="contactsTable,parentOrg">
													<autoWidths>19</autoWidths>
													<autoEditors>
														<label/>
													</autoEditors>
													<autoFields>
														<string/>
													</autoFields>
													<autoItems>
													<item value=""> </item>
													<item value="Y">Y</item>
													<item value="N">N</item>
													</autoItems>
													</autoDropdown>
										</query>
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
											<autoDropdown cat="contactType" case="mixed" serviceUrl="OpenELISServlet?service=org.openelis.modules.organization.server.OrganizationService" width="90px" fromModel="true" dropdown="true" type="integer">
											<autoWidths>90</autoWidths>
											<autoEditors>
												<label/>
											</autoEditors>
											<autoFields>
											    <string/>
											</autoFields>
											</autoDropdown>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<autoDropdown cat="contactState" case="upper" serviceUrl="OpenELISServlet?service=org.openelis.modules.organization.server.OrganizationService" width="40px" fromModel="true" dropdown="true" type="string">
												<autoWidths>40</autoWidths>
												<autoEditors>
													<label/>
												</autoEditors>
												<autoFields>
													<string/>
												</autoFields>
											</autoDropdown>
										 	<textbox case="mixed"/>
										 	<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
											<autoDropdown cat="contactCountry" case="upper" serviceUrl="OpenELISServlet?service=org.openelis.modules.organization.server.OrganizationService" width="110px" fromModel="true" dropdown="true" type="string">
											<autoWidths>110</autoWidths>
											<autoEditors>
												<label/>
											</autoEditors>
											<autoFields>
											    <string/>
											</autoFields>
											</autoDropdown>
										</editors>
										<fields>
											<collection type="integer"/>
											<string required="true"/>
											<string/>
											<string/>
											<string/>
											<collection type="string"/>
											<string/>
											<string/>
											<string/>
											<string/>
											<string/>
											<string/>
											<collection type="string"/>
										</fields>
										<sorts>true,true,true,true,true,true,true,true,true,true,true,true,true</sorts>
										<filters>false,false ,false,false,false,false ,false,false,false,false ,false,false,false</filters>
										<colAligns>left,left,left,left,left,left,left,left,left,left,left,left,left</colAligns>
									</table>
									<query>
									<table width="567px" rows="1" title="" maxRows="8" showError="false">
										<headers><xsl:value-of select='resource:getString($constants,"type")'/>,<xsl:value-of select='resource:getString($constants,"contactName")'/>,<xsl:value-of select='resource:getString($constants,"aptSuite")'/>,
										<xsl:value-of select='resource:getString($constants,"address")'/>,<xsl:value-of select='resource:getString($constants,"city")'/>,
										<xsl:value-of select='resource:getString($constants,"state")'/>,<xsl:value-of select='resource:getString($constants,"zipcode")'/>,
										<xsl:value-of select='resource:getString($constants,"workNumber")'/>,<xsl:value-of select='resource:getString($constants,"homeNumber")'/>,
										<xsl:value-of select='resource:getString($constants,"cellNumber")'/>,<xsl:value-of select='resource:getString($constants,"faxNumber")'/>,
										<xsl:value-of select='resource:getString($constants,"email")'/>,<xsl:value-of select='resource:getString($constants,"country")'/></headers>
										<widths>106,130,130,130,130,56,68,100,90,90,90,150,126</widths>
										<editors>
											<autoDropdown cat="contactType" case="upper" serviceUrl="OpenELISServlet?service=org.openelis.modules.organization.server.OrganizationService" width="90px" fromModel="true" dropdown="true" multiSelect="true" type="integer">
											<autoWidths>90</autoWidths>
											<autoEditors>
												<label/>
											</autoEditors>
											<autoFields>
											    <string/>
											</autoFields>
											</autoDropdown>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<autoDropdown cat="contactState" case="upper" serviceUrl="OpenELISServlet?service=org.openelis.modules.organization.server.OrganizationServlet" width="40px" fromModel="true" dropdown="true" multiSelect="true" type="string">
												<autoWidths>40</autoWidths>
												<autoEditors>
													<label/>
												</autoEditors>
												<autoFields>
													<string/>
												</autoFields>
											</autoDropdown>
										 	<textbox case="mixed"/>
										 	<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
										 	<autoDropdown cat="contactCountry" case="upper" serviceUrl="OpenELISServlet?service=org.openelis.modules.organization.server.OrganizationService" width="110px" fromModel="true" dropdown="true" multiSelect="true" type="string">
											<autoWidths>110</autoWidths>
											<autoEditors>
												<label/>
											</autoEditors>
											<autoFields>
											    <string/>
											</autoFields>
											</autoDropdown>									 	
										</editors>
										<fields>
											<collection type="integer"/>
											<queryString/>
											<queryString/>
											<queryString/>
											<queryString/>
											<collection type="string"/>
											<queryString/>
											<queryString/>
											<queryString/>
											<queryString/>
											<queryString/>
											<queryString/>
											<collection type="string"/>
										</fields>
										<sorts>true,true,true,true,true,true,true,true,true,true,true,true,true</sorts>
										<filters>false,false ,false,false,false,false ,false,false,false,false ,false,false,false</filters>
										<colAligns>left,left,left,left,left,left,left,left,left,left,left,left,left</colAligns>
									</table>
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
										<textbox case="mixed" key="usersSubject" width="405px" max="60"/>
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
										<textarea width="524px" height="50px" case="mixed" key="usersNote"/>
										</widget>
										</row>
								 
							<row>
								<widget>
								<html key="spacer" xml:space="preserve"> </html>
								</widget>
								<widget colspan="2">
								<panel style="notesPanelContainer" layout="horizontal" xsi:type="Panel">
								<panel key="notesPanel" style="NotesPanel" valign="top" onclick="this" height="127px" width="524px" layout="vertical" overflowX="auto" overflowY="scroll" xsi:type="Panel">
								
								</panel>
								</panel>
								</widget>
							</row>
						</panel>
						</panel>
					</tab>
				</panel>
				</panel>
					</deck> 
				</panel>
			</panel>
		</panel>
	</display>
	<rpc key="display">
  <number key="orgId" type="integer" required="false"/>
  <number key="addressId" required="false" type="integer"/>
  <string key="orgName" max="40" required="true"/>
  <string key="streetAddress" max="30" required="true"/>
  <string key="multUnit" max="30" required="false"/>
  <string key="city" max="30" required="true"/>
  <string key="zipCode" max="10" required="true"/>
  <string key="action" max="20" required="false"/>
  <check key="isActive" required="false"/>

  <string key="usersSubject" max="60" required="false"/>
  <string key="usersNote" required="false"/>
  <number key="parentOrgId" type="integer" required="true"/> 
  <table key="contactsTable"/>
  <number key="id" required="false" type="integer"/>
  <collection key="state" type="string" required="true"/>
  <collection key="country" type="string" required="true"/>
	</rpc>
	<rpc key="query">
  <queryNumber key="orgId" type="integer"/>
  <queryNumber key="addressId" type="integer"/>
  <queryString key="orgName"/>
  <queryString key="streetAddress"/>
  <queryString key="multUnit" value="query"/>
  <queryString key="city"/>
  <queryString key="zipCode"/>
  <queryString key="action"/>
  <queryString key="parentOrg"/>
  <queryString key="usersSubject"/>
  <queryString key="usersNote"/>
  <table key="contactsTable"/>
  <queryNumber key="id" type="integer"/>
  <collection key="state" type="string" required="false"/>
  <model key="stateModel"/>
  <collection key="country" type="string" required="false"/>
  <model key="countryModel"/>
  <collection key="isActive" type="string" required="false"/>
	</rpc>
	<rpc key="queryByLetter">
		<queryString key="orgName"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>