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
<screen id= "Provider" serviceUrl= "OpenElisService"
xsi:noNamespaceSchemaLocation= "file:///home/tschmidt/workspace/libraries/metadata/FormSchema.xsd"
xmlns:locale = "xalan:/java.util.Locale" xmlns:xalan= "http://xml.apache.org/xalan" xmlns:xsi= "http://www.w3.org/2001/XMLSchema-instance">
<display>
 <panel layout= "horizontal" width = "400px" spacing= "0" padding= "0" xsi:type= "Panel">
  <aToZ height= "425px" width = "auto" key= "hideablePanel" visible= "false" onclick= "this">
   <panel layout= "horizontal" xsi:type= "Panel" spacing= "0">
    <xsl:if test="string($language)='en'">
     <panel layout= "vertical" xsi:type= "Panel" spacing = "0"> 
      <widget> 
       <html key= "a" onclick= "this">&lt;a class='navIndex'&gt;A&lt;/a&gt;</html> 
      </widget>
      <widget>
       <html key= "b" onclick= "this">&lt;a class='navIndex'&gt;B&lt;/a&gt;</html> 
      </widget>
      <widget>
        <html key= "c" onclick= "this">&lt;a class='navIndex'&gt;C&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "d" onclick= "this">&lt;a class='navIndex'&gt;D&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "e" onclick= "this">&lt;a class='navIndex'&gt;E&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key = "f" onclick = "this">&lt;a class='navIndex'&gt;F&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "g" onclick = "this">&lt;a class='navIndex'&gt;G&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "h" onclick = "this">&lt;a class='navIndex'&gt;H&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "i" onclick = "this">&lt;a class='navIndex'&gt;I&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "j" onclick = "this">&lt;a  class='navIndex'&gt;J&lt;/a&gt;</html>
      </widget>
      <widget>
        <html key= "k" onclick = "this">&lt;a class='navIndex'&gt;K&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "l" onclick= "this">&lt;a class='navIndex'&gt;L&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "m" onclick= "this">&lt;a class='navIndex'&gt;M&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "n" onclick= "this">&lt;a class='navIndex'&gt;N&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "o" onclick= "this">&lt;a class='navIndex'&gt;O&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "p" onclick= "this">&lt;a class='navIndex'&gt;P&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "q" onclick= "this">&lt;a class='navIndex'&gt;Q&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "r" onclick= "this">&lt;a class='navIndex'&gt;R&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "s" onclick= "this">&lt;a class='navIndex'&gt;S&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "t" onclick= "this">&lt;a class='navIndex'&gt;T&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "u" onclick= "this">&lt;a class='navIndex'&gt;U&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "v" onclick= "this">&lt;a class='navIndex'&gt;V&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "w" onclick= "this">&lt;a class='navIndex'&gt;W&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "x" onclick= "this">&lt;a class='navIndex'&gt;X&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "y" onclick= "this">&lt;a class='navIndex'&gt;Y&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "z" onclick= "this">&lt;a class='navIndex'&gt;Z&lt;/a&gt;</html>
      </widget>
    </panel>
    </xsl:if>
    
    <panel layout= "vertical" width = "175px" xsi:type= "Panel" >
      <table maxRows = "20" rows = "0" manager= "ProviderNamesTable" serviceUrl= "ProviderServlet" width= "auto" key = "providersTable" title="{resource:getString($constants,'providers')}">
       <headers><xsl:value-of select='resource:getString($constants,"name")'/></headers>
							<widths>150</widths>
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
   </panel>
  </aToZ>
  
  <panel layout= "vertical" xsi:type = "Panel">
   <panel layout= "vertical" spacing= "2" xsi:type= "Panel">
    <widget halign= "center">
      <buttonPanel buttons= "qaucbpn" key= "buttons"/>
    </widget>
    <widget halign= "center">
     <appMessage key= "message"/>
    </widget>
   </panel>
   
   <panel key= "secMod" layout= "table" width= "450px" style= "Form" xsi:type= "Table">
    <row>
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"provider_LastName")'/></text>
     </widget>
     <widget width= "210px"> 
      <textbox key = "lastName" case = "upper" />
     </widget>
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"provider_Type")'/></text>
     </widget>
     <widget>
      <option key= "providerType"  multi= "false" required= "true" onChange= "this"/>
     </widget>
    <!-- multi= "false" required= "true" onChange= "this"-->
    </row>
    <row>
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"provider_FirstName")'/></text>
     </widget>
     <widget>
      <textbox key= "firstName" case = "upper"   width= "150px"/>
     </widget>
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"provider_NPI")'/></text>
     </widget>
     <widget>
      <textbox case= "mixed"   key= "npi" width= "80px"/>
     </widget>
    </row>
    <row>
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"provider_MiddleName")'/></text>
     </widget>
     <widget>
      <textbox key= "middleName" case = "upper" width= "150px"/>
     </widget>
    </row>
   </panel>
   
   <panel layout= "vertical" height = "20px" xsi:type= "Panel"/>
    <panel height= "200px" key= "orgTabPanel" halign= "center" layout= "tab" width= "600px" xsi:type= "Tab">
     <tab key= "tab1" text= "{resource:getString($constants,'provider_Locations')}">
      <panel layout= "vertical" spacing= "0" xsi:type= "Panel">
       <widget halign= "center">
        <table width= "auto" maxRows = "6" rows = "1" key= "providerAddressTable" manager = "ProviderAddressesTable" autoAdd= "true" title= "{resource:getString($constants,'provider_Locations')}">
         <headers><xsl:value-of select='resource:getString($constants,"provider_Location")'/>,<xsl:value-of select='resource:getString($constants,"provider_ExternalId")'/>,<xsl:value-of select='resource:getString($constants,"aptSuite")'/>,
				  <xsl:value-of select='resource:getString($constants,"address")'/>,<xsl:value-of select='resource:getString($constants,"city")'/>,
                  <xsl:value-of select='resource:getString($constants,"state")'/>, <xsl:value-of select='resource:getString($constants,"country")'/>,
                  <xsl:value-of select='resource:getString($constants,"zipcode")'/>,<xsl:value-of select='resource:getString($constants,"workNumber")'/>,<xsl:value-of select='resource:getString($constants,"homeNumber")'/>,
				  <xsl:value-of select='resource:getString($constants,"cellNumber")'/>,<xsl:value-of select='resource:getString($constants,"faxNumber")'/>,
				  <xsl:value-of select='resource:getString($constants,"email")'/></headers>
		 <widths>115,130,130,130,130,50,68,100,90,90,90,150,145</widths>
		 <editors>
		  <textbox case= "upper"/>
		  <textbox case= "mixed"/>
		  <textbox case= "upper"/>
		  <textbox case= "upper"/>
		  <textbox case= "upper"/>
		  <option>
		   <item value= ""/>
		   <item value= "AL">AL</item>
		   <item value= "AK">AK</item>
		   <item value= "AZ">AZ</item>
		   <item value= "AR">AR</item>
		   <item value= "CA">CA</item>
		   <item value= "CO">CO</item>
		   <item value= "CT">CT</item>
		   <item value= "DE">DE</item>
		   <item value= "FL">FL</item>
		   <item value= "GA">GA</item>
		   <item value= "HI">HI</item>
		   <item value= "ID">ID</item>
		   <item value= "IL">IL</item>
		   <item value= "IN">IN</item>
		   <item value= "IA">IA</item>
		   <item value= "KS">KS</item>
		   <item value= "KY">KY</item>
		   <item value= "LA">LA</item>
		   <item value= "ME">ME</item>
		   <item value= "MD">MD</item>
		   <item value= "MA">MA</item>
		   <item value= "MI">MI</item>
		   <item value= "MN">MN</item>
		   <item value= "MS">MS</item>
		   <item value= "MO">MO</item>
		   <item value= "MT">MT</item>
		   <item value= "NE">NE</item>
		   <item value= "NV">NV</item>
		   <item value= "NJ">NJ</item>
		   <item value= "NH">NH</item>
		   <item value= "NM">NM</item>
		   <item value= "NY">NY</item>
		   <item value= "NC">NC</item>
		   <item value= "ND">ND</item>
		   <item value= "OH">OH</item>
		   <item value= "OK">OK</item>
		   <item value= "OR">OR</item>
		   <item value= "PA">PA</item>
		   <item value= "RI">RI</item>
		   <item value= "SC">SC</item>
		   <item value= "SD">SD</item>
		   <item value= "TN">TN</item>
		   <item value= "TX">TX</item>
		   <item value= "UT">UT</item>
		   <item value= "VT">VT</item>
		   <item value= "VA">VA</item>
		   <item value= "WA">WA</item>
		   <item value= "WV">WV</item>
		   <item value= "WI">WI</item>
		   <item value= "WY">WY</item>
		  </option>
		  <option>
			<item value= ""/>
			<item value= "UnitedStates">UNITED STATES</item>
			<item value= "AAAA">AAAA</item>
			<item value= "BBBB">BBBB</item>
			<item value= "CCCC">CCCC</item>
		 </option>
			<textbox case= "mixed"/>
			<textbox case= "mixed"/>
			<textbox case= "mixed"/>
			<textbox case= "mixed"/>
			<textbox case= "mixed"/>
			<textbox case= "mixed"/>		 
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
		  <string/>
		  </fields>
		  <sorts>true,true,true,true,true,true,true,true,true,true,true,true,true</sorts>
		  <filters>false,false,false,false,false,false,false,false,false,false,false,false,false</filters>
		  <colAligns>left,left,left,left,left,left,left,left,left,left,left,left,left</colAligns>
	    </table>
	    <query>
	     <table width= "auto" maxRows = "6" rows = "1" title= " ">
          <headers><xsl:value-of select='resource:getString($constants,"provider_Location")'/>,<xsl:value-of select='resource:getString($constants,"provider_ExternalId")'/>,<xsl:value-of select='resource:getString($constants,"aptSuite")'/>,
				  <xsl:value-of select='resource:getString($constants,"address")'/>,<xsl:value-of select='resource:getString($constants,"city")'/>,
                  <xsl:value-of select='resource:getString($constants,"state")'/>, <xsl:value-of select='resource:getString($constants,"country")'/>,
                  <xsl:value-of select='resource:getString($constants,"zipcode")'/>,<xsl:value-of select='resource:getString($constants,"workNumber")'/>,<xsl:value-of select='resource:getString($constants,"homeNumber")'/>,
				  <xsl:value-of select='resource:getString($constants,"cellNumber")'/>,<xsl:value-of select='resource:getString($constants,"faxNumber")'/>,
				  <xsl:value-of select='resource:getString($constants,"email")'/></headers>
		  <widths>115,130,130,130,130,50,68,100,90,90,90,150,145</widths>
		  <editors>
		  <textbox case= "upper"/>
		  <textbox case= "mixed"/>
		  <textbox case= "upper"/>
		  <textbox case= "upper"/>
		  <textbox case= "upper"/>
		  <option multi = "true" fromModel = "true"/>		   
		  <option multi="true" fromModel="true">			
		  </option>
			<textbox case= "mixed"/>
			<textbox case= "mixed"/>
			<textbox case= "mixed"/>
			<textbox case= "mixed"/>
			<textbox case= "mixed"/>
			<textbox case= "mixed"/>		 
		</editors>
		<fields>
		  <queryString/>
		  <queryString/>
		  <queryString/>
		  <queryString/>
		  <queryString/>
		  <queryOption multi="true" type="string">
		   <item value= ""/>
		   <item value= "AL">AL</item>
		   <item value= "AK">AK</item>
		   <item value= "AZ">AZ</item>
		   <item value= "AR">AR</item>
		   <item value= "CA">CA</item>
		   <item value= "CO">CO</item>
		   <item value= "CT">CT</item>
		   <item value= "DE">DE</item>
		   <item value= "FL">FL</item>
		   <item value= "GA">GA</item>
		   <item value= "HI">HI</item>
		   <item value= "ID">ID</item>
		   <item value= "IL">IL</item>
		   <item value= "IN">IN</item>
		   <item value= "IA">IA</item>
		   <item value= "KS">KS</item>
		   <item value= "KY">KY</item>
		   <item value= "LA">LA</item>
		   <item value= "ME">ME</item>
		   <item value= "MD">MD</item>
		   <item value= "MA">MA</item>
		   <item value= "MI">MI</item>
		   <item value= "MN">MN</item>
		   <item value= "MS">MS</item>
		   <item value= "MO">MO</item>
		   <item value= "MT">MT</item>
		   <item value= "NE">NE</item>
		   <item value= "NV">NV</item>
		   <item value= "NJ">NJ</item>
		   <item value= "NH">NH</item>
		   <item value= "NM">NM</item>
		   <item value= "NY">NY</item>
		   <item value= "NC">NC</item>
		   <item value= "ND">ND</item>
		   <item value= "OH">OH</item>
		   <item value= "OK">OK</item>
		   <item value= "OR">OR</item>
		   <item value= "PA">PA</item>
		   <item value= "RI">RI</item>
		   <item value= "SC">SC</item>
		   <item value= "SD">SD</item>
		   <item value= "TN">TN</item>
		   <item value= "TX">TX</item>
		   <item value= "UT">UT</item>
		   <item value= "VT">VT</item>
		   <item value= "VA">VA</item>
		   <item value= "WA">WA</item>
		   <item value= "WV">WV</item>
		   <item value= "WI">WI</item>
		   <item value= "WY">WY</item>
		  </queryOption>
		  <queryOption multi="true" type="string">
		    <item value= ""/>
			<item value= "UnitedStates">UNITED STATES</item>
			<item value= "AAAA">AAAA</item>
			<item value= "BBBB">BBBB</item>
			<item value= "CCCC">CCCC</item>
		  </queryOption>	
		  <queryString/>
		  <queryString/>
		  <queryString/>
		  <queryString/>
		  <queryString/>
		  <queryString/>
		  </fields>
		  <sorts>true,true,true,true,true,true,true,true,true,true,true,true,true</sorts>
		  <filters>false,false,false,false,false,false,false,false,false,false,false,false,false</filters>
		  <colAligns>left,left,left,left,left,left,left,left,left,left,left,left,left</colAligns>
	    </table>
	    </query>
	  </widget>
	  		 
	  <panel layout= "horizontal" xsi:type= "Panel" height= "5px"/>
		<button halign= "center" onclick = "this" key= "removeAddressButton" style= "ScreenButtonPanel" html= "&lt;img src=&quot;Images/deleteButtonIcon.png&quot;&gt; {resource:getString($constants,'provider_RemAddress')}"/>
	</panel>									
  </tab>
  
  <tab key="noteTab" text="{resource:getString($constants,'note')}">
						<panel key="secMod3" layout="vertical" width="100%" height="164px" spacing="0" padding="0" xsi:type="Panel">

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
                 
							 	<pagedTree key="notesTree" vertical="true" width="585px" height="100px" itemsPerPage="1000" title="Provider Notes"/>

							</widget>    

						</panel>
					</tab>
   </panel>
  </panel>
 </panel>
</display>
							  
<rpc key= "display">	
  <number key="providerId" type="integer" required="false"/>				
  <string key="lastName" max="30" required="false"/>
  <string key="firstName" max="20" required="false"/> 
  <string key="npi" max="20" required="false"/>
  <string key="middleName" max="20" required="false"/>	
  <table key="providerAddressTable"/>						      		     
  <option key= "providerType"   multi= "false" required= "false"/>
  <string key="usersSubject" max="60" required="false"/>
  <string key="usersNote" required="false"/>
  <tree key="notesTree"/>
  <!--   <item value= ""/>
    <item value= "118">Nurse</item>
	<item value= "119">Physician</item>
  </option> -->
</rpc>
					   
<rpc key= "query">     
  <queryNumber key="providerId" type="integer" />				
  <queryString key="lastName"  />
  <queryString key="firstName"  /> 
  <queryString key="npi" />
  <queryString key="middleName" />	  
  <queryString key="usersSubject" />
  <queryString key="usersNote" />                           
  <table key="providerAddressTable"/>	
  <!--<queryString key= "providerType" max = "255" required="false"/>-->
  <queryOption key= "providerType" multi= "true"  type= "string">
  <item value= ""/>
  <item value= "118">Nurse</item>
  <item value= "119">Physician</item>
   <!--<item value= "3">TECHNICIAN</item>
   <item value= "4">MAIN CONTACT</item>
   <item value= "5">SECOND CONTACT</item>
   <item value= "6">DOCTOR</item>-->
  </queryOption> 
  <tree key="notesTree"/>
</rpc>

<rpc key="queryByLetter">
  <queryString key="lastName"/>
</rpc> 
</screen>
</xsl:template>
</xsl:stylesheet>     