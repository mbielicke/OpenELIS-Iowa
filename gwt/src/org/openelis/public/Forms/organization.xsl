<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.server.constants.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                extension-element-prefixes="resource"
                version="1.0">
                
  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.server.constants.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>

  <xsl:template match="doc"> 
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle('org.openelis.client.main.constants.OpenELISConstants',locale:new(string($language)))"/>
<screen id="Organization" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display focus="orgName">
		<panel layout="horizontal" spacing="0" padding="0" xsi:type="Panel">
			<!--left table goes here -->
				<aToZ height="425px" width="100%" key="hideablePanel" visible="false" onclick="this">
				 <panel layout="horizontal" style="ScreenLeftPanel" xsi:type="Panel" spacing="0">
				 <xsl:if test="string($language)='en'">
			<panel layout="vertical" xsi:type="Panel" spacing="0" style="AtoZ">
				<widget>
            <html key="a" onclick="this">&lt;a class='navIndex'&gt;A&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="b" onclick="this">&lt;a class='navIndex'&gt;B&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="c" onclick="this">&lt;a class='navIndex'&gt;C&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="d" onclick="this">&lt;a class='navIndex'&gt;D&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="e" onclick="this">&lt;a class='navIndex'&gt;E&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="f" onclick="this">&lt;a class='navIndex'&gt;F&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="g" onclick="this">&lt;a class='navIndex'&gt;G&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="h" onclick="this">&lt;a class='navIndex'&gt;H&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="i" onclick="this">&lt;a class='navIndex'&gt;I&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="j" onclick="this">&lt;a class='navIndex'&gt;J&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="k" onclick="this">&lt;a class='navIndex'&gt;K&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="l" onclick="this">&lt;a class='navIndex'&gt;L&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="m" onclick="this">&lt;a class='navIndex'&gt;M&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="n" onclick="this">&lt;a class='navIndex'&gt;N&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="o" onclick="this">&lt;a class='navIndex'&gt;O&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="p" onclick="this">&lt;a class='navIndex'&gt;P&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="q" onclick="this">&lt;a class='navIndex'&gt;Q&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="r" onclick="this">&lt;a class='navIndex'&gt;R&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="s" onclick="this">&lt;a class='navIndex'&gt;S&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="t" onclick="this">&lt;a class='navIndex'&gt;T&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="u" onclick="this">&lt;a class='navIndex'&gt;U&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="v" onclick="this">&lt;a class='navIndex'&gt;V&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="w" onclick="this">&lt;a class='navIndex'&gt;W&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="x" onclick="this">&lt;a class='navIndex'&gt;X&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="y" onclick="this">&lt;a class='navIndex'&gt;Y&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="z" onclick="this">&lt;a class='navIndex'&gt;Z&lt;/a&gt;</html>
          </widget>
          </panel>
		</xsl:if>
		<xsl:if test="string($language)='cn'">

		</xsl:if>	
		<xsl:if test="string($language)='fa'">

		</xsl:if>
		<xsl:if test="string($language)='sp'">
			<panel layout="vertical" xsi:type="Panel" cellpadding="0" cellspacing="0">
			<widget>
            <html key="a" onclick="this">&lt;a class='navIndex'&gt;A&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="b" onclick="this">&lt;a class='navIndex'&gt;B&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="c" onclick="this">&lt;a class='navIndex'&gt;C&lt;/a&gt;</html>
          </widget>
           <widget>
            <html key="ch" onclick="this">&lt;a class='navIndex'&gt;CH&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="d" onclick="this">&lt;a class='navIndex'&gt;D&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="e" onclick="this">&lt;a class='navIndex'&gt;E&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="f" onclick="this">&lt;a class='navIndex'&gt;F&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="g" onclick="this">&lt;a class='navIndex'&gt;G&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="h" onclick="this">&lt;a class='navIndex'&gt;H&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="i" onclick="this">&lt;a class='navIndex'&gt;I&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="j" onclick="this">&lt;a class='navIndex'&gt;J&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="k" onclick="this">&lt;a class='navIndex'&gt;K&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="l" onclick="this">&lt;a class='navIndex'&gt;L&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="ll" onclick="this">&lt;a class='navIndex'&gt;LL&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="m" onclick="this">&lt;a class='navIndex'&gt;M&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="n" onclick="this">&lt;a class='navIndex'&gt;N&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="ñ" onclick="this">&lt;a class='navIndex'&gt;Ñ&lt;/a&gt;</html>
          </widget>          
          <widget>
            <html key="o" onclick="this">&lt;a class='navIndex'&gt;O&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="p" onclick="this">&lt;a class='navIndex'&gt;P&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="q" onclick="this">&lt;a class='navIndex'&gt;Q&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="r" onclick="this">&lt;a class='navIndex'&gt;R&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="s" onclick="this">&lt;a class='navIndex'&gt;S&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="t" onclick="this">&lt;a class='navIndex'&gt;T&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="u" onclick="this">&lt;a class='navIndex'&gt;U&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="v" onclick="this">&lt;a class='navIndex'&gt;V&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="w" onclick="this">&lt;a class='navIndex'&gt;W&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="x" onclick="this">&lt;a class='navIndex'&gt;X&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="y" onclick="this">&lt;a class='navIndex'&gt;Y&lt;/a&gt;</html>
          </widget>
          <widget>
            <html key="z" onclick="this">&lt;a class='navIndex'&gt;Z&lt;/a&gt;</html>
          </widget>
          </panel>
		</xsl:if>
				<table manager="OrganizationNameTable" width="auto" style="ScreenLeftTable" key="organizationsTable" maxRows="21" title="{resource:getString($constants,'organizations')}">
				<headers><xsl:value-of select='resource:getString($constants,"name")'/></headers>
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
			<panel layout="vertical" spacing="0" width="600px" xsi:type="Panel">
				<widget>
					

          <buttonPanel key="buttons">
            <appButton action="query" toggle="true">
              <widget>
                <text>Query</text>
              </widget>
            </appButton>
            <html>&lt;div
style="width:1px;height:20px;background:grey"/&gt;</html>
 <appButton action="prev" toggle="true">
              <widget>
                <text>Previous</text>
              </widget>
            </appButton>
            <html>&lt;div
style="width:1px;height:20px;background:grey"/&gt;</html>
 <appButton action="next" toggle="true">
              <widget>
                <text>Next</text>
              </widget>
            </appButton>
            <html>&lt;div
style="width:1px;height:20px;background:grey"/&gt;</html>
            <appButton action="add" toggle="true">
              <widget>
                <text>Add</text>
              </widget>
            </appButton>
            <html>&lt;div
style="width:1px;height:20px;background:grey"/&gt;</html>
            <appButton action="update" toggle="true">
              <widget>
                <text>Update</text>
              </widget>
            </appButton>
            <html>&lt;div
style="width:1px;height:20px;background:grey"/&gt;</html>
            <appButton action="commit">
              <widget>
                <text>Commit</text>
              </widget>
            </appButton>
            <html>&lt;div
style="width:1px;height:20px;background:grey"/&gt;</html>
            <appButton action="abort">
              <widget>
                <text>Abort</text>
              </widget>
            </appButton>
          </buttonPanel>
				</widget>
				<panel key="formDeck" layout="deck" xsi:type="Deck" align="left">
					<deck>
					<panel layout="vertical" width="600px" xsi:type="Panel">
							<panel key="secMod2" layout="table" style="FormBorderless" width="225px" xsi:type="Table">
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
										<textbox case="upper" key="orgName" width="225px" tab="multUnit,orgName"/>
										</widget>
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"city")'/>:</text>
										</widget>
										<widget>
											<textbox case="upper" key="city" width="212px" tab="state,streetAddress"/>
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
											<textbox case="upper" key="multUnit" width="212px" tab="streetAddress,orgName"/>
										</widget>
										<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"state")'/>:</text>
												</widget>
										<panel layout="horizontal" xsi:type="Panel" padding="0" spacing="0">
												<widget>
													<!--<option key="state" tab="zipCode,city"/>-->
													<autoDropdown cat="state" key="state" case="upper" serviceUrl="OrganizationServlet" width="40px" dropdown="true" type="string" fromModel="true" tab="country,city">
													<autoWidths>40</autoWidths>
													<autoEditors>
														<label/>
													</autoEditors>
													<autoFields>
														<string/>
													</autoFields>
													</autoDropdown>
													<query>
													<!--<option tab="country,city"/>-->
													<autoDropdown cat="state" case="upper" serviceUrl="OrganizationServlet" width="40px" dropdown="true" type="string" fromModel="true" multiSelect="true" tab="country,city">
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
									<panel layout="horizontal" width="24px" xsi:type="Panel"/>
											<widget>
													<text style="Prompt"><xsl:value-of select='resource:getString($constants,"zipcode")'/>:</text>
												</widget>
												<panel layout="horizontal" width="2px" xsi:type="Panel"/>
												<widget>
													<maskedbox key="zipCode" width="70" mask="99999-9999" tab="country,state"/>
													<!--<textbox case="upper" key="zipCode" width="70" tab="country,state"/> -->
												</widget>
									</panel>									
									</row>									
									<row>
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"street")'/>:</text>
										</widget>
										<widget>
											<textbox case="upper" key="streetAddress" width="212px" tab="city,multUnit"/>
										</widget>	
											<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"country")'/>:</text>
										</widget>
										<widget>
												<autoDropdown cat="country" key="country" case="upper" serviceUrl="OrganizationServlet" width="196px" dropdown="true" fromModel="true" type="string" tab="parentOrg,zipCode">
													<autoWidths>175</autoWidths>
													<autoEditors>
														<label/>
													</autoEditors>
													<autoFields>
														<string/>
													</autoFields>
												</autoDropdown>
											<query>
													<autoDropdown cat="country" case="upper" serviceUrl="OrganizationServlet" width="196px" dropdown="true" type="string" fromModel="true" multiSelect="true" tab="parentOrg,zipCode">
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
										<auto cat="parentOrg" case="upper" serviceUrl="OrganizationServlet" key="parentOrg" width="225px" type="integer" popupHeight="150px" tab="isActive,country">
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
										<widget>
											<check key="isActive" tab="contactsTable,parentOrg"/>
											<query>
											<autoDropdown cat="isActive" case="upper" serviceUrl="OrganizationServlet" width="40px" dropdown="true" type="string" multiSelect="true" tab="contactsTable,parentOrg">
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
										<!--<option tab="contactsTable,parentOrg"/>-->
										</query>
										</widget>
								</row>
								</panel>
<!-- tabbed panel needs to go here -->
				<panel height="200px" key="orgTabPanel" halign="center" layout="tab" width="600px" xsi:type="Tab">
					<!-- TAB 1 -->
					<tab key="tab1" text="{resource:getString($constants,'contact')}">
							<panel layout="vertical" spacing="0" padding="0" xsi:type="Panel">
							<widget valign="top">
								<table width="567px" key="contactsTable" manager="OrganizationContactsTable" maxRows="7" title="">
										<headers><xsl:value-of select='resource:getString($constants,"type")'/>,<xsl:value-of select='resource:getString($constants,"contactName")'/>,<xsl:value-of select='resource:getString($constants,"aptSuite")'/>,
										<xsl:value-of select='resource:getString($constants,"address")'/>,<xsl:value-of select='resource:getString($constants,"city")'/>,
										<xsl:value-of select='resource:getString($constants,"state")'/>,<xsl:value-of select='resource:getString($constants,"zipcode")'/>,
										<xsl:value-of select='resource:getString($constants,"workNumber")'/>,<xsl:value-of select='resource:getString($constants,"homeNumber")'/>,
										<xsl:value-of select='resource:getString($constants,"cellNumber")'/>,<xsl:value-of select='resource:getString($constants,"faxNumber")'/>,
										<xsl:value-of select='resource:getString($constants,"email")'/>,<xsl:value-of select='resource:getString($constants,"country")'/></headers>
										<widths>106,130,130,130,130,56,68,100,90,90,90,150,126</widths>
										<editors>
											<autoDropdown cat="contactType" key="contactType" case="upper" serviceUrl="OrganizationServlet" width="90px" fromModel="true" dropdown="true" type="integer">
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
											<autoDropdown cat="contactState" key="contactState" case="upper" serviceUrl="OrganizationServlet" width="40px" fromModel="true" dropdown="true" type="string">
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
											<autoDropdown cat="contactCountry" key="contactCountry" case="upper" serviceUrl="OrganizationServlet" width="110px" fromModel="true" dropdown="true" type="string">
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
											<number type="integer">0</number>
											<string/>
											<string/>
											<string/>
											<string/>
											<string xml:space="preserve"> </string>
											<string/>
											<string/>
											<string/>
											<string/>
											<string/>
											<string/>
											<string xml:space="preserve"> </string>
										</fields>
										<sorts>true,true,true,true,true,true,true,true,true,true,true,true,true</sorts>
										<filters>false,false ,false,false,false,false ,false,false,false,false ,false,false,false</filters>
										<colAligns>left,left,left,left,left,left,left,left,left,left,left,left,left</colAligns>
									</table>
									<query>
									<table width="567px" rows="1" title="" maxRows="7">
										<headers><xsl:value-of select='resource:getString($constants,"type")'/>,<xsl:value-of select='resource:getString($constants,"contactName")'/>,<xsl:value-of select='resource:getString($constants,"aptSuite")'/>,
										<xsl:value-of select='resource:getString($constants,"address")'/>,<xsl:value-of select='resource:getString($constants,"city")'/>,
										<xsl:value-of select='resource:getString($constants,"state")'/>,<xsl:value-of select='resource:getString($constants,"zipcode")'/>,
										<xsl:value-of select='resource:getString($constants,"workNumber")'/>,<xsl:value-of select='resource:getString($constants,"homeNumber")'/>,
										<xsl:value-of select='resource:getString($constants,"cellNumber")'/>,<xsl:value-of select='resource:getString($constants,"faxNumber")'/>,
										<xsl:value-of select='resource:getString($constants,"email")'/>,<xsl:value-of select='resource:getString($constants,"country")'/></headers>
										<widths>106,130,130,130,130,56,68,100,90,90,90,150,126</widths>
										<editors>
											<autoDropdown cat="contactType" key="contactType" case="upper" serviceUrl="OrganizationServlet" width="90px" fromModel="true" dropdown="true" multiSelect="true" type="integer">
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
											<autoDropdown cat="contactState" key="contactState" case="upper" serviceUrl="OrganizationServlet" width="40px" fromModel="true" dropdown="true" multiSelect="true" type="string">
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
										 	<autoDropdown cat="contactCountry" key="contactCountry" case="upper" serviceUrl="OrganizationServlet" width="110px" fromModel="true" dropdown="true" multiSelect="true" type="string">
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
								<panel layout="horizontal" xsi:type="Panel" height="5px"/>
																<!--&lt;img src=&quot;Images/deleteButtonIcon.png&quot;&gt; -->
									<widget halign="right">
									<appButton action="removeContact" onclick="this" key="removeContactButton">
						              <widget>
                						<text><xsl:value-of select='resource:getString($constants,"removeRow")'/></text>
							              </widget>
						            </appButton>
						            </widget>
							</panel>
								<!-- end TAB 1 data table -->
						<!-- END TAB 1 -->
					</tab>			
					<!-- start TAB 2 -->
					<tab key="noteTab" text="{resource:getString($constants,'note')}">
						<panel key="secMod3" layout="vertical" width="100%" height="164px" spacing="0" padding="0" xsi:type="Panel">

									<panel layout="vertical" height="3px" xsi:type="Panel"/>
							<panel key="noteFormPanel" layout="table" style="FormBorderless" width="160px" xsi:type="Table" padding="0" spacing="0">
										<row>
										<widget>
												<text style="Prompt"><xsl:value-of select='resource:getString($constants,"subject")'/></text>
										</widget>
										<widget>
										<textbox case="mixed" key="usersSubject" width="510px" max="60"/>
										</widget>
										</row>
										<row>
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"note")'/></text>
										</widget>
										<widget>
										<textarea width="510px" height="50px" case="mixed" key="usersNote"/>
										</widget>
										</row>
								</panel> 

							<panel key="notesPanel" valign="top" layout="vertical" width="100%" xsi:type="Panel">
							
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
  <string key="multUnit" max="30" required="false" value="test"/>
  <string key="city" max="30" rkeyequired="true"/>
  <string key="zipCode" max="10" required="true"/>
  <string key="action" max="20" required="false"/>
  <check key="isActive" required="false"/>

  <string key="usersSubject" max="60" required="false"/>
  <string key="usersNote" required="false"/>
  <number key="parentOrgId" type="integer" required="false"/> 
  <table key="contactsTable"/>
  <model key="notesModel"/>
  <number key="id" required="false" type="integer"/>
  <string key="stateId" required="true"/>
  <string key="countryId" required="true"/>
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
  <tree key="notesTree"/>
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