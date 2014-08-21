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
                xmlns:meta="xalan://org.openelis.metamap.OrganizationMetaMap" 
                xmlns:addr="xalan://org.openelis.meta.AddressMeta"
                xmlns:contact="xalan://org.openelis.metamap.OrganizationContactMetaMap"
                xmlns:note="xalan://org.openelis.meta.NoteMeta"
                xmlns:parent="xalan://org.openelis.meta.OrganizationMeta"
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

		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.OrganizationMetaMap"/>
	</xalan:component>
	<xalan:component prefix="addr">
		<xalan:script lang="javaclass" src="xalan://org.openelis.meta.AddressMeta"/>
	</xalan:component>
	<xalan:component prefix="contact">
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.OrganizationContactMetaMap"/>
	</xalan:component>
	<xalan:component prefix="note">

		<xalan:script lang="javaclass" src="xalan://org.openelis.meta.NoteMeta"/>
	</xalan:component>
	<xalan:component prefix="parent">
		<xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrganizationMeta"/>
	</xalan:component>
	<xsl:template match="doc">
		<xsl:variable name="org" select="meta:new()"/>
		<xsl:variable name="addr" select="meta:getAddress($org)"/>
		<xsl:variable name="cont" select="meta:getOrganizationContact($org)"/>

		<xsl:variable name="parent" select="meta:getParentOrganization($org)"/>
		<xsl:variable name="note" select="meta:getNote($org)"/>
		<xsl:variable name="contAddr" select="contact:getAddress($cont)"/>
		<xsl:variable name="language">
			<xsl:value-of select="locale"/>
		</xsl:variable>
		<xsl:variable name="props">
			<xsl:value-of select="props"/>
		</xsl:variable>

		<xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
		<screen id="Organization" name="{resource:getString($constants,'organization')}" serviceUrl="TestService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
			<display>
				<HorizontalPanel padding="0" spacing="0">
					<!--left table goes here -->
					<CollapsePanel key="collapsePanel" height="440px" style="LeftSidePanel">
					    <!--
						<azTable colwidths="175" height="425px" key="azTable" maxRows="18" tablewidth="auto" headers="{resource:getString($constants,'name')}" width="100%">
							<buttonPanel key="atozButtons">
								<xsl:call-template name="aToZLeftPanelButtons"/>

							</buttonPanel>
						</azTable>
					    -->
					    <resultsTable height="425px" width="100%" key="azTable">
					       <buttonPanel key="atozButtons">
								<xsl:call-template name="aToZLeftPanelButtons"/>
						   </buttonPanel>
						   <table maxRows="18" width="auto">
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
						<VerticalPanel spacing="0" padding="0" style="WhiteContentPanel">
							<TablePanel style="Form">
								<row>
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'id')"/>:</text>
									<textbox key="{meta:getId($org)}" tab="{meta:getName($org)},{meta:getIsActive($org)}" width="75px"/>
								</row>

								<row>
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'name')"/>:</text>
									<textbox case="upper" key="{meta:getName($org)}" max="40" tab="{addr:getMultipleUnit($addr)},{meta:getId($org)}" width="225px"/>
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'city')"/>:</text>
									<widget colspan="3">
										<textbox case="upper" key="{addr:getCity($addr)}" max="30" tab="{addr:getState($addr)},{addr:getStreetAddress($addr)}" width="212px"/>
									</widget>
								</row>

								<row>
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'aptSuite')"/>:</text>
									<textbox case="upper" key="{addr:getMultipleUnit($addr)}" max="30" tab="{addr:getStreetAddress($addr)},{meta:getName($org)}" width="212px"/>
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'state')"/>:</text>
									<dropdown case="upper" key="{addr:getState($addr)}" tab="{addr:getZipCode($addr)},{addr:getCity($addr)}" width="40px"/>
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'zipcode')"/>:</text>
									<maskedbox key="{addr:getZipCode($addr)}" mask="99999-9999" tab="{addr:getCountry($addr)},{addr:getState($addr)}" width="70"/>

								</row>
								<row>
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'address')"/>:</text>
									<textbox case="upper" key="{addr:getStreetAddress($addr)}" max="30" tab="{addr:getCity($addr)},{addr:getMultipleUnit($addr)}" width="212px"/>
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'country')"/>:</text>
									<widget colspan="3">
										<dropdown case="mixed" key="{addr:getCountry($addr)}" tab="{parent:getName($parent)},{addr:getZipCode($addr)}" width="175px"/>
									</widget>

								</row>
								<row>
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'parentOrganization')"/>:</text>
									<widget>
									<autoComplete case="upper" cat="parentOrg" key="{parent:getName($parent)}" serviceUrl="OpenELISServlet?service=org.openelis.modules.organization.server.OrganizationService" tab="{meta:getIsActive($org)},{addr:getCountry($addr)}" width="241px">
										<headers>Name,Street,City,St</headers>
										<widths>180,110,100,20</widths>

									</autoComplete>
									<query>
										<textbox case="upper" tab="{meta:getIsActive($org)},{addr:getCountry($addr)}" width="241px"/>
									</query>
									</widget>
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'active')"/>:</text>
									<widget colspan="3">
										<check key="{meta:getIsActive($org)}" tab="contactsTable,{parent:getName($parent)}"/>

									</widget>
								</row>
							</TablePanel>
							<!-- TAB PANEL -->
							<TabPanel key="orgTabPanel">
								<!-- TAB 1 -->
								<tab key="contactsTab" text="{resource:getString($constants,'contact')}">
									<VerticalPanel  width="610px" padding="0" spacing="0">
										<widget valign="top">
											<table key="contactsTable" maxRows="9" showError="false" showScroll="ALWAYS" title="" width="574px" tab="{meta:getId($org)},{meta:getIsActive($org)}">
												<headers>
													<xsl:value-of select="resource:getString($constants,'type')"/>,<xsl:value-of select="resource:getString($constants,'contactName')"/>,<xsl:value-of select="resource:getString($constants,'aptSuite')"/>,<xsl:value-of select="resource:getString($constants,'address')"/>,<xsl:value-of select="resource:getString($constants,'city')"/>,<xsl:value-of select="resource:getString($constants,'state')"/>,<xsl:value-of select="resource:getString($constants,'zipcode')"/>,<xsl:value-of select="resource:getString($constants,'country')"/>,<xsl:value-of select="resource:getString($constants,'workNumber')"/>,<xsl:value-of select="resource:getString($constants,'homeNumber')"/>,<xsl:value-of select="resource:getString($constants,'cellNumber')"/>,<xsl:value-of select="resource:getString($constants,'faxNumber')"/>,<xsl:value-of select="resource:getString($constants,'email')"/>

												</headers>
												<widths>106,130,130,130,130,56,68,126,100,90,90,90,150</widths>
												<editors>
													<dropdown case="mixed" width="90px"/>
													<textbox case="upper"/>
													<textbox case="upper"/>
													<textbox case="upper"/>
													<textbox case="upper"/>

													<dropdown case="upper" width="40px"/>
													<textbox case="mixed"/>
													<dropdown case="mixed" width="110px"/>
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
											<!--
											<query>
												<queryTable maxRows="9" showError="false" title="" width="574px" showScroll="ALWAYS">
													<headers>
														<xsl:value-of select="resource:getString($constants,'type')"/>,<xsl:value-of select="resource:getString($constants,'contactName')"/>,<xsl:value-of select="resource:getString($constants,'aptSuite')"/>,<xsl:value-of select="resource:getString($constants,'address')"/>,<xsl:value-of select="resource:getString($constants,'city')"/>,<xsl:value-of select="resource:getString($constants,'state')"/>,<xsl:value-of select="resource:getString($constants,'zipcode')"/>,<xsl:value-of select="resource:getString($constants,'country')"/>,<xsl:value-of select="resource:getString($constants,'workNumber')"/>,<xsl:value-of select="resource:getString($constants,'homeNumber')"/>,<xsl:value-of select="resource:getString($constants,'cellNumber')"/>,<xsl:value-of select="resource:getString($constants,'faxNumber')"/>,<xsl:value-of select="resource:getString($constants,'email')"/>

													</headers>
													<widths>106,130,130,130,130,56,68,126,100,90,90,90,150</widths>
													<editors>
														<dropdown case="mixed" multiSelect="true" width="90px"/>
														<textbox case="upper"/>
														<textbox case="upper"/>
														<textbox case="upper"/>
														<textbox case="upper"/>

														<dropdown case="upper" multiSelect="true" width="40px"/>
														<textbox case="mixed"/>
														<dropdown case="mixed" multiSelect="true" width="110px"/>
														<textbox case="mixed"/>
														<textbox case="mixed"/>
														<textbox case="mixed"/>
														<textbox case="mixed"/>
														<textbox case="mixed"/>
													</editors>

													<fields>
														<xsl:value-of select="contact:getContactTypeId($cont)"/>,<xsl:value-of select="contact:getName($cont)"/>,<xsl:value-of select="addr:getMultipleUnit($contAddr)"/>,<xsl:value-of select="addr:getStreetAddress($contAddr)"/>,<xsl:value-of select="addr:getCity($contAddr)"/>,<xsl:value-of select="addr:getState($contAddr)"/>,<xsl:value-of select="addr:getZipCode($contAddr)"/>,<xsl:value-of select="addr:getCountry($contAddr)"/>,<xsl:value-of select="addr:getWorkPhone($contAddr)"/>,<xsl:value-of select="addr:getHomePhone($contAddr)"/>,<xsl:value-of select="addr:getCellPhone($contAddr)"/>,<xsl:value-of select="addr:getFaxPhone($contAddr)"/>,<xsl:value-of select="addr:getEmail($contAddr)"/>
													</fields>

													
												</queryTable>
											</query>
											-->
										</widget>
										<widget halign="center" style="WhiteContentPanel">
											<appButton action="removeRow" key="removeContactButton" onclick="this" style="Button">
												<HorizontalPanel>
													<AbsolutePanel style="RemoveRowButtonImage"/>
													<text><xsl:value-of select="resource:getString($constants,'removeRow')"/></text>
												</HorizontalPanel>

											</appButton>
										</widget>
									</VerticalPanel>
									<!-- END TAB 1 -->
								</tab>
								<!-- START TAB 2 -->
								<tab text="{resource:getString($constants,'identifier')}">
									<VerticalPanel padding="0" spacing="0">
									<widget valign="top">
										<table key="identifierstsTable" maxRows="9" showError="false" showScroll="ALWAYS" title="" width="auto">
												<headers>
													<xsl:value-of select="resource:getString($constants,'identifier')"/>,<xsl:value-of select="resource:getString($constants,'value')"/>												</headers>
												<widths>267,298</widths>
												<editors>
													<textbox case="mixed"/>
													<textbox case="mixed"/>

												</editors>
												<fields>
													<string key="{contact:getName($cont)}" required="true"/>
													<string key="{addr:getMultipleUnit($contAddr)}" required="true"/>
												</fields>
												<sorts>true,true</sorts>
												<filters>false,false</filters>
												<colAligns>left,left</colAligns>

											</table>
											<!--
											<query>
												<queryTable maxRows="9" showError="false" title="" width="592px">
													<headers>
														<xsl:value-of select="resource:getString($constants,'type')"/>,<xsl:value-of select="resource:getString($constants,'contactName')"/>,<xsl:value-of select="resource:getString($constants,'aptSuite')"/>,<xsl:value-of select="resource:getString($constants,'address')"/>,<xsl:value-of select="resource:getString($constants,'city')"/>,<xsl:value-of select="resource:getString($constants,'state')"/>,<xsl:value-of select="resource:getString($constants,'zipcode')"/>,<xsl:value-of select="resource:getString($constants,'country')"/>,<xsl:value-of select="resource:getString($constants,'workNumber')"/>,<xsl:value-of select="resource:getString($constants,'homeNumber')"/>,<xsl:value-of select="resource:getString($constants,'cellNumber')"/>,<xsl:value-of select="resource:getString($constants,'faxNumber')"/>,<xsl:value-of select="resource:getString($constants,'email')"/>

													</headers>
													<widths>106,130,130,130,130,56,68,126,100,90,90,90,150</widths>
													<editors>
														<dropdown case="mixed" multiSelect="true" width="90px"/>
														<textbox case="upper"/>
														<textbox case="upper"/>
														<textbox case="upper"/>
														<textbox case="upper"/>

														<dropdown case="upper" multiSelect="true" width="40px"/>
														<textbox case="mixed"/>
														<dropdown case="mixed" multiSelect="true" width="110px"/>
														<textbox case="mixed"/>
														<textbox case="mixed"/>
														<textbox case="mixed"/>
														<textbox case="mixed"/>
														<textbox case="mixed"/>
													</editors>

													<fields>
														<xsl:value-of select="contact:getContactTypeId($cont)"/>,<xsl:value-of select="contact:getName($cont)"/>,<xsl:value-of select="addr:getMultipleUnit($contAddr)"/>,<xsl:value-of select="addr:getStreetAddress($contAddr)"/>,<xsl:value-of select="addr:getCity($contAddr)"/>,<xsl:value-of select="addr:getState($contAddr)"/>,<xsl:value-of select="addr:getZipCode($contAddr)"/>,<xsl:value-of select="addr:getCountry($contAddr)"/>
													</fields>
													<xsl:value-of select="addr:getWorkPhone($contAddr)"/>,
   													<xsl:value-of select="addr:getHomePhone($contAddr)"/>,
													<xsl:value-of select="addr:getCellPhone($contAddr)"/>,
													<xsl:value-of select="addr:getFaxPhone($contAddr)"/>,
   													<xsl:value-of select="addr:getEmail($contAddr)"/>

												</queryTable>
											</query>
											-->
										</widget>
										<widget halign="center" style="WhiteContentPanel">
											<appButton action="removeIdentifierRow" key="removeIdentifierButton" onclick="this" style="Button">
												<HorizontalPanel>
													<AbsolutePanel style="RemoveRowButtonImage"/>
													<text><xsl:value-of select="resource:getString($constants,'removeRow')"/></text>
												</HorizontalPanel>

											</appButton>
										</widget>
										<HorizontalPanel height="18px"/>
									</VerticalPanel>
								</tab>
								<!-- END TAB 2 -->
								<!-- start TAB 3 -->
								<tab key="notesTab" text="{resource:getString($constants,'note')}">
									<VerticalPanel height="164px" key="secMod3" padding="0" spacing="0" width="100%">

										<TablePanel key="noteFormPanel" layout="table" padding="0" spacing="0" style="Form" xsi:type="Table">
											<row>
												<text style="Prompt"><xsl:value-of select="resource:getString($constants,'subject')"/>:</text>
												<textbox case="mixed" key="{note:getSubject($note)}" max="60" showError="false" tab="{note:getText($note)},{note:getText($note)}" width="429px"/>
												<appButton action="standardNote" key="standardNoteButton" onclick="this" style="Button">
													<HorizontalPanel>
														<AbsolutePanel style="StandardNoteButtonImage"/>
														<text><xsl:value-of select="resource:getString($constants,'standardNote')"/></text>
													</HorizontalPanel>

												</appButton>
											</row>
											<row>
												<text style="Prompt"><xsl:value-of select="resource:getString($constants,'note')"/>:</text>
												<widget colspan="2">
													<textarea case="mixed" height="48px" key="{note:getText($note)}" showError="false" tab="{note:getSubject($note)},{note:getSubject($note)}" width="545px"/>
												</widget>
											</row>
											<row>

											    <widget>
													<html key="spacer" xml:space="preserve"> </html>
											    </widget>
												<widget colspan="2">
													<HorizontalPanel style="notesPanelContainer">
														<VerticalPanel height="178px" key="notesPanel" layout="vertical" onclick="this" overflowX="auto" overflowY="scroll" style="NotesPanel" valign="top" width="545px" xsi:type="Panel"></VerticalPanel>
													</HorizontalPanel>
												</widget>

											</row>
										</TablePanel>
									</VerticalPanel>
								</tab>
							</TabPanel>
						</VerticalPanel>
					</VerticalPanel>
				</HorizontalPanel>
			</display>

			<rpc key="display">
				<integer key="{meta:getId($org)}" required="false" />
				<integer key="{meta:getAddressId($org)}" required="false"/>
				<string key="{meta:getName($org)}" max="40" required="true"/>
				<string key="{addr:getStreetAddress($addr)}" max="30" required="true"/>
				<string key="{addr:getMultipleUnit($addr)}" max="30" required="false"/>
				<string key="{addr:getCity($addr)}" max="30" required="true"/>
				<string key="{addr:getZipCode($addr)}" max="10" required="true"/>
				<check key="{meta:getIsActive($org)}" required="false"/>

				<dropdown key="{parent:getName($parent)}" required="false"/>
				<dropdown key="{addr:getState($addr)}" required="false"/>
				<dropdown key="{addr:getCountry($addr)}" required="true"/>
				<rpc key="contacts">
				  <table key="contactsTable"/>
				</rpc>
				<rpc key="notes">
				  <string key="{note:getSubject($note)}" max="60" required="false"/>
				  <string key="{note:getText($note)}" required="false"/>

				  <string key="notesPanel"/>
				</rpc>
				<string key="orgTabPanel" reset="false">contactsTab</string>
			</rpc>
			<!--
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
            -->
		</screen>
  </xsl:template>
</xsl:stylesheet>
