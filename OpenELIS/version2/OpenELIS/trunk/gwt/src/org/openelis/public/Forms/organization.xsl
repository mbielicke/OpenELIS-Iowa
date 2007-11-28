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
	<display>
		<panel layout="horizontal" spacing="0" xsi:type="Panel">
			<!--left table goes here -->
				<aToZ height="425px" width="auto" key="hideablePanel" visible="false" onclick="this">
				 <panel layout="horizontal" xsi:type="Panel" spacing="5">
				 <xsl:if test="string($language)='en'">
			<panel layout="vertical" xsi:type="Panel" spacing="1">
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
			<panel layout="vertical" xsi:type="Panel">
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
				 
				<table height="425px" manager="OrganizationNameTable" serviceUrl="OrganizationScreen" width="auto" key="organizationsTable" title="{resource:getString($constants,'organizations')}">
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
			<panel layout="vertical" spacing="2" width="600px" xsi:type="Panel">
				<widget halign="center">
					<buttonPanel buttons="qacubnp" key="buttons"/>
				</widget>
				<panel key="formDeck" layout="deck" xsi:type="Deck" align="left">
					<deck>
					<panel layout="vertical" width="600px" xsi:type="Panel">
							<panel key="secMod2" layout="table" style="FormBorderless" width="225px" xsi:type="Table">
							<row>
									<panel layout="horizontal" xsi:type="Panel" height="10px"/>
							</row>
									<row>
									<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"organizationId")'/></text>
										</widget>
										<widget>
										<textbox key="orgId" width="100px" tab="orgName,isActive"/>
										</widget>
									</row>
									<row>
									<panel layout="horizontal" width="1px" xsi:type="Panel"/>
									<widget>
									<error id="orgId"/>
									</widget>
									</row>
									<row>
								
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"name")'/></text>
										</widget>
										<widget colspan="3">
										<textbox case="upper" key="orgName" width="290px" tab="multUnit,orgName"/>
										</widget>

									</row>
									<row>
									<panel layout="horizontal" width="1px" xsi:type="Panel"/>
									<widget>
									<error id="orgName"/>
									</widget>
									</row>
									<row>
									<panel layout="vertical" height="10px" xsi:type="Panel"/>
									</row>
						
									<row>
									<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"aptSuite")'/></text>
										</widget>
										
										<widget>
											<textbox case="upper" key="multUnit" width="212px" tab="streetAddress,orgName"/>
										</widget>	
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"city")'/></text>
										</widget>
										<widget>
											<textbox case="upper" key="city" width="212px" tab="state,streetAddress"/>
										</widget>							
									</row>
									<row>
									<panel layout="horizontal" width="1px" xsi:type="Panel"/>
									<widget>
									<error id="multUnit"/>
									</widget>
									<panel layout="horizontal" width="1px" xsi:type="Panel"/>
									<widget>
									<error id="city"/>
									</widget>
									</row>
									
									<row>
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"street")'/></text>
										</widget>
										<widget>
											<textbox case="upper" key="streetAddress" width="212px" tab="city,multUnit"/>
										</widget>	
										<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"state")'/></text>
												</widget>
										<panel layout="horizontal" xsi:type="Panel" padding="0" spacing="0">
												
												<!--<panel layout="horizontal" width="1px" xsi:type="Panel"/> -->
												<widget>
													<option key="state" tab="zipCode,city"/>
												</widget>
									<panel layout="horizontal" width="37px" xsi:type="Panel"/>
											<widget>
													<text style="Prompt"><xsl:value-of select='resource:getString($constants,"zipcode")'/></text>
												</widget>
												<panel layout="horizontal" width="2px" xsi:type="Panel"/>
												<widget>
													<maskedbox key="zipCode" width="70" mask="99999-9999" tab="country,state"/>
													<!--<textbox case="upper" key="zipCode" width="70" tab="country,state"/> -->
												</widget>
									</panel>									
									</row>
									<row>
									<panel layout="horizontal" width="1px" xsi:type="Panel"/>
									<widget>
									<error id="streetAddress"/>
									</widget>
									<panel layout="horizontal" width="1px" xsi:type="Panel"/>
									<widget>
									<error id="state,zipCode"/>
									</widget>
									</row>
									<row>
									<panel layout="horizontal" width="1px" height="1px" xsi:type="Panel"/>
									<panel layout="horizontal" width="1px" height="1px" xsi:type="Panel"/>
									<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"country")'/></text>
										</widget>
										<widget>
											<option key="country" tab="parentOrg,zipCode"/>
										</widget>
										</row>
									<row>
									<panel layout="horizontal" width="1px" xsi:type="Panel"/>
									<widget>
									<error id="country"/>
									</widget>
									</row>
								<row>
									<panel layout="vertical" height="10px" xsi:type="Panel"/>
								</row>
								<row>
									<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"parentOrganization")'/></text>
										</widget>
										<widget>
										<auto cat="" case="upper" serviceUrl="OrganizationScreen" key="parentOrg" width="225px" popupWidth="350px" tab="isActive,country"/>
										<query>
											<textbox case="upper" width="225px" tab="isActive,country"/>
										</query>
										</widget>

								</row>
								<row>
									<widget>
									<error id="parentOrg"/>
									</widget>
									</row>
								
								<row>
										<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"active")'/></text>
										</widget>
										<widget>
											<check key="isActive" tab="contactsTable,parentOrg"/>
											<query>
										<option tab="contactsTable,parentOrg"/>
										</query>
										</widget>
								</row>
								<row>
									<widget>
									<error id="isActive"/>
									</widget>
									</row>
								</panel>
<!-- tabbed panel needs to go here -->
				<panel height="200px" key="orgTabPanel" halign="center" layout="tab" width="600px" xsi:type="Tab">
					<!-- TAB 1 -->
					<tab key="tab1" text="{resource:getString($constants,'contact')}">
							<panel layout="vertical" spacing="5" xsi:type="Panel">
							<widget halign="center">
								<table width="575px" height="135px" key="contactsTable" manager="OrganizationContactsTable" autoAdd="true" title="">
										<headers><xsl:value-of select='resource:getString($constants,"contactName")'/>,<xsl:value-of select='resource:getString($constants,"aptSuite")'/>,
										<xsl:value-of select='resource:getString($constants,"address")'/>,<xsl:value-of select='resource:getString($constants,"city")'/>,
										<xsl:value-of select='resource:getString($constants,"state")'/>,<xsl:value-of select='resource:getString($constants,"zipcode")'/>,
										<xsl:value-of select='resource:getString($constants,"workNumber")'/>,<xsl:value-of select='resource:getString($constants,"homeNumber")'/>,
										<xsl:value-of select='resource:getString($constants,"cellNumber")'/>,<xsl:value-of select='resource:getString($constants,"faxNumber")'/>,
										<xsl:value-of select='resource:getString($constants,"email")'/>,<xsl:value-of select='resource:getString($constants,"country")'/></headers>
										<widths>130,130,130,130,50,68,100,90,90,90,150,145</widths>
										<editors>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<option>
											<item value=" "> </item>
											<item value="AL">AL</item>
											<item value="AK">AK</item>
											<item value="AZ">AZ</item>
											<item value="AR">AR</item>
											<item value="CA">CA</item>
											<item value="CO">CO</item>
											<item value="CT">CT</item>
											<item value="DE">DE</item>
											<item value="FL">FL</item>
											<item value="GA">GA</item>
											<item value="HI">HI</item>
											<item value="ID">ID</item>
											<item value="IL">IL</item>
											<item value="IN">IN</item>
											<item value="IA">IA</item>
											<item value="KS">KS</item>
											<item value="KY">KY</item>
											<item value="LA">LA</item>
											<item value="ME">ME</item>
											<item value="MD">MD</item>
											<item value="MA">MA</item>
											<item value="MI">MI</item>
											<item value="MN">MN</item>
											<item value="MS">MS</item>
											<item value="MO">MO</item>
											<item value="MT">MT</item>
											<item value="NE">NE</item>
											<item value="NV">NV</item>
											<item value="NJ">NJ</item>
											<item value="NH">NH</item>
											<item value="NM">NM</item>
											<item value="NY">NY</item>
											<item value="NC">NC</item>
											<item value="ND">ND</item>
											<item value="OH">OH</item>
											<item value="OK">OK</item>
											<item value="OR">OR</item>
											<item value="PA">PA</item>
											<item value="RI">RI</item>
											<item value="SC">SC</item>
											<item value="SD">SD</item>
											<item value="TN">TN</item>
											<item value="TX">TX</item>
											<item value="UT">UT</item>
											<item value="VT">VT</item>
											<item value="VA">VA</item>
											<item value="WA">WA</item>
											<item value="WV">WV</item>
											<item value="WI">WI</item>
											<item value="WY">WY</item>											
											</option>
										 	<textbox case="mixed"/>
										 	<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
										 	<option>
										 	<item value=" "> </item>
											<item value="United States">United States</item>
											</option>
										</editors>
										<fields>
											<string/>
											<string/>
											<string/>
											<string/>
											<string/>
											<string/>
											<string/>
											<string/>
											<string/>
											<string/>
											<string/>
											<string/>
										</fields>
										<sorts>true,true,true,true,true,true,true,true,true,true,true,true</sorts>
										<filters>false ,false,false,false,false ,false,false,false,false ,false,false,false</filters>
										<colAligns>left,left,left,left,left,left,left,left,left,left,left,left</colAligns>
									</table>
									<query>
									<table width="575px" height="135px" rows="1" title="">
										<headers><xsl:value-of select='resource:getString($constants,"contactName")'/>,<xsl:value-of select='resource:getString($constants,"aptSuite")'/>,
										<xsl:value-of select='resource:getString($constants,"address")'/>,<xsl:value-of select='resource:getString($constants,"city")'/>,
										<xsl:value-of select='resource:getString($constants,"state")'/>,<xsl:value-of select='resource:getString($constants,"zipcode")'/>,
										<xsl:value-of select='resource:getString($constants,"workNumber")'/>,<xsl:value-of select='resource:getString($constants,"homeNumber")'/>,
										<xsl:value-of select='resource:getString($constants,"cellNumber")'/>,<xsl:value-of select='resource:getString($constants,"faxNumber")'/>,
										<xsl:value-of select='resource:getString($constants,"email")'/>,<xsl:value-of select='resource:getString($constants,"country")'/></headers>
										<widths>130,130,130,130,50,68,100,90,90,90,150,145</widths>
										<editors>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<option>
											<item value=" "> </item>
											<item value="AL">AL</item>
											<item value="AK">AK</item>
											<item value="AZ">AZ</item>
											<item value="AR">AR</item>
											<item value="CA">CA</item>
											<item value="CO">CO</item>
											<item value="CT">CT</item>
											<item value="DE">DE</item>
											<item value="FL">FL</item>
											<item value="GA">GA</item>
											<item value="HI">HI</item>
											<item value="ID">ID</item>
											<item value="IL">IL</item>
											<item value="IN">IN</item>
											<item value="IA">IA</item>
											<item value="KS">KS</item>
											<item value="KY">KY</item>
											<item value="LA">LA</item>
											<item value="ME">ME</item>
											<item value="MD">MD</item>
											<item value="MA">MA</item>
											<item value="MI">MI</item>
											<item value="MN">MN</item>
											<item value="MS">MS</item>
											<item value="MO">MO</item>
											<item value="MT">MT</item>
											<item value="NE">NE</item>
											<item value="NV">NV</item>
											<item value="NJ">NJ</item>
											<item value="NH">NH</item>
											<item value="NM">NM</item>
											<item value="NY">NY</item>
											<item value="NC">NC</item>
											<item value="ND">ND</item>
											<item value="OH">OH</item>
											<item value="OK">OK</item>
											<item value="OR">OR</item>
											<item value="PA">PA</item>
											<item value="RI">RI</item>
											<item value="SC">SC</item>
											<item value="SD">SD</item>
											<item value="TN">TN</item>
											<item value="TX">TX</item>
											<item value="UT">UT</item>
											<item value="VT">VT</item>
											<item value="VA">VA</item>
											<item value="WA">WA</item>
											<item value="WV">WV</item>
											<item value="WI">WI</item>
											<item value="WY">WY</item>											
											</option>
										 	<textbox case="mixed"/>
										 	<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
										 	<option>
										 	<item value=" "> </item>
											<item value="United States">United States</item>
											</option>
										</editors>
										<fields>
											<queryString/>
											<queryString/>
											<queryString/>
											<queryString/>
											<queryOption multi="true" type="string"/>
											<queryString/>
											<queryString/>
											<queryString/>
											<queryString/>
											<queryString/>
											<queryString/>
											<queryOption multi="true" type="string"/>
										</fields>
										<sorts>true,true,true,true,true,true,true,true,true,true,true,true</sorts>
										<filters>false ,false,false,false,false ,false,false,false,false ,false,false,false</filters>
										<colAligns>left,left,left,left,left,left,left,left,left,left,left,left</colAligns>
									</table>
									</query>
								</widget>
								<panel layout="horizontal" xsi:type="Panel" height="5px"/>
								<button halign="center" onclick="this" key="removeContactButton" style="ScreenButtonPanel" html="&lt;img src=&quot;Images/deleteButtonIcon.png&quot;&gt; {resource:getString($constants,'removeContact')}"/>
							</panel>
								<!-- end TAB 1 data table -->
						<!-- END TAB 1 -->
					</tab>			
					<!-- start TAB 2 -->
					<tab key="noteTab" text="{resource:getString($constants,'note')}">
						<panel key="secMod3" layout="vertical" width="100%" height="200px" spacing="0" padding="0" xsi:type="Panel">

									<panel layout="vertical" height="3px" xsi:type="Panel"/>
							<panel key="noteFormPanel" layout="table" style="FormBorderless" width="160px" xsi:type="Table" padding="0" spacing="0">
										<row>
										<widget>
												<text style="Prompt"><xsl:value-of select='resource:getString($constants,"subject")'/></text>
										</widget>
										<widget>
										<textbox case="mixed" key="usersSubject" width="510px"/>
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

							<panel layout="vertical" height="2px" xsi:type="Panel"/>	
							 <widget valign="top">
                 
							 	<pagedTree key="notesTree" vertical="true" width="596px" height="107px" itemsPerPage="1000" title="abc"/>

							</widget>    

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
  <string key="city" max="30" required="true"/>
  <string key="zipCode" max="10" required="true"/>
  <string key="action" max="20" required="false"/>
  <check key="isActive" required="false"/>
  <string key="usersSubject" max="60" required="false"/>
  <string key="usersNote" required="false"/>
  <string key="parentOrgText" required="false" max="40"/>
  <number key="parentOrgId" type="integer" required="false"/> 
  <table key="contactsTable"/>
  <number key="id" required="false" type="integer"/>
		<option key="state" multi="false" required="true">
			<item value=" "> </item>
			<item value="AL">AL</item>
			<item value="AK">AK</item>
			<item value="AZ">AZ</item>
			<item value="AR">AR</item>
			<item value="CA">CA</item>
			<item value="CO">CO</item>
			<item value="CT">CT</item>
			<item value="DE">DE</item>
			<item value="FL">FL</item>
			<item value="GA">GA</item>
			<item value="HI">HI</item>
			<item value="ID">ID</item>
			<item value="IL">IL</item>
			<item value="IN">IN</item>
			<item value="IA">IA</item>
			<item value="KS">KS</item>
			<item value="KY">KY</item>
			<item value="LA">LA</item>
			<item value="ME">ME</item>
			<item value="MD">MD</item>
			<item value="MA">MA</item>
			<item value="MI">MI</item>
			<item value="MN">MN</item>
			<item value="MS">MS</item>
			<item value="MO">MO</item>
			<item value="MT">MT</item>
			<item value="NE">NE</item>
			<item value="NV">NV</item>
			<item value="NJ">NJ</item>
			<item value="NH">NH</item>
			<item value="NM">NM</item>
			<item value="NY">NY</item>
			<item value="NC">NC</item>
			<item value="ND">ND</item>
			<item value="OH">OH</item>
			<item value="OK">OK</item>
			<item value="OR">OR</item>
			<item value="PA">PA</item>
			<item value="RI">RI</item>
			<item value="SC">SC</item>
			<item value="SD">SD</item>
			<item value="TN">TN</item>
			<item value="TX">TX</item>
			<item value="UT">UT</item>
			<item value="VT">VT</item>
			<item value="VA">VA</item>
			<item value="WA">WA</item>
			<item value="WV">WV</item>
			<item value="WI">WI</item>
			<item value="WY">WY</item>	
		</option>
		<option key="country" multi="false" required="true">
		 	<item value=" "> </item>
			<item value="United States">United States</item>
			<item value="2">AAAA</item>
		</option>
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
  <queryOption key="isActive" type="string" multi="true">
  	<item value=" "> </item>
  	<item value="Y">Y</item>
  	<item value="N">N</item>
  </queryOption>

  <queryString key="usersSubject"/>
  <queryString key="usersNote"/>
 <table key="contactsTable"/>
  <queryNumber key="id" type="integer"/>
  <queryOption key="state" multi="true" type="string">
			<item value=" "> </item>
			<item value="AL">AL</item>
			<item value="AK">AK</item>
			<item value="AZ">AZ</item>
			<item value="AR">AR</item>
			<item value="CA">CA</item>
			<item value="CO">CO</item>
			<item value="CT">CT</item>
			<item value="DE">DE</item>
			<item value="FL">FL</item>
			<item value="GA">GA</item>
			<item value="HI">HI</item>
			<item value="ID">ID</item>
			<item value="IL">IL</item>
			<item value="IN">IN</item>
			<item value="IA">IA</item>
			<item value="KS">KS</item>
			<item value="KY">KY</item>
			<item value="LA">LA</item>
			<item value="ME">ME</item>
			<item value="MD">MD</item>
			<item value="MA">MA</item>
			<item value="MI">MI</item>
			<item value="MN">MN</item>
			<item value="MS">MS</item>
			<item value="MO">MO</item>
			<item value="MT">MT</item>
			<item value="NE">NE</item>
			<item value="NV">NV</item>
			<item value="NJ">NJ</item>
			<item value="NH">NH</item>
			<item value="NM">NM</item>
			<item value="NY">NY</item>
			<item value="NC">NC</item>
			<item value="ND">ND</item>
			<item value="OH">OH</item>
			<item value="OK">OK</item>
			<item value="OR">OR</item>
			<item value="PA">PA</item>
			<item value="RI">RI</item>
			<item value="SC">SC</item>
			<item value="SD">SD</item>
			<item value="TN">TN</item>
			<item value="TX">TX</item>
			<item value="UT">UT</item>
			<item value="VT">VT</item>
			<item value="VA">VA</item>
			<item value="WA">WA</item>
			<item value="WV">WV</item>
			<item value="WI">WI</item>
			<item value="WY">WY</item>	
		</queryOption>
		<queryOption key="country" multi="true" type="string">
		 	<item value=" "> </item>
			<item value="United States">United States</item>
			<item value="AAAAA">AAAA</item>
		</queryOption>
	</rpc>
	<rpc key="queryByLetter">
		<queryString key="orgName"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>