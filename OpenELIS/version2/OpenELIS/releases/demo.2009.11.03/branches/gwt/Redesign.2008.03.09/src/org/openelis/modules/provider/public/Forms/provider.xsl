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
<screen id= "Provider" serviceUrl= "OpenElisService"
xsi:noNamespaceSchemaLocation= "file:///home/tschmidt/workspace/libraries/metadata/FormSchema.xsd"
xmlns:locale = "xalan:/java.util.Locale" xmlns:xalan= "http://xml.apache.org/xalan" xmlns:xsi= "http://www.w3.org/2001/XMLSchema-instance">
<display>
 <panel layout= "horizontal"  spacing= "0" padding= "0" style="WhiteContentPanel" xsi:type= "Panel">
  <aToZ height= "425px" width = "auto" key= "hideablePanel" visible= "false">
   <panel layout= "horizontal" xsi:type= "Panel" style="ScreenLeftPanel" spacing= "0">
    <xsl:if test="string($language)='en'">
		<xsl:call-template name="aToZLeftPanelButtons"/>
    </xsl:if>
      <table maxRows = "20" manager= "ProviderNamesTable" width= "auto" style="ScreenLeftTable"  key = "providersTable" title="" showError="false">
       <headers><xsl:value-of select='resource:getString($constants,"lastName")'/>,<xsl:value-of select='resource:getString($constants,"firstName")'/></headers>
							<widths>75,75</widths>
							<editors>
								<label/>
								<label/>								
							</editors>
							<fields>
								<string/>	
								<string/>							
							</fields>
							<sorts>false,false</sorts>
							<filters>false,false</filters>
     </table>
   </panel>
  </aToZ>
   <panel layout= "vertical" spacing= "0" xsi:type= "Panel">
   	<!--button panel code-->
	<xsl:call-template name="buttonPanelTemplate">
		<xsl:with-param name="buttonsParam">qpn|au|cb</xsl:with-param>
	</xsl:call-template>	
   
   <panel layout= "vertical"  height = "5px" xsi:type= "Panel"/>
   <panel key= "secMod" width = "450px" layout= "table"  style= "Form" xsi:type= "Table">
    <row>
    <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"id")'/></text>
     </widget>
     <widget> 
      <textbox  width= "50px" key = "providerId"  tab="lastName,npi"/>
     </widget>                 
    </row>
    <row>
      <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"lastName")'/></text>
     </widget>
     <widget width= "210px"> 
      <textbox key = "lastName" case = "upper" tab="firstName,npi"/>
     </widget>     
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"type")'/></text>
     </widget>
     <widget>
										<autoDropdown cat="providerType" key="providerType" case="mixed" serviceUrl="OpenELISServlet?service=org.openelis.modules.provider.server.ProviderService" width="80px"   fromModel="true" type="integer" tab="npi,middleName">
													<autoWidths>60</autoWidths>
													<autoEditors>
														<label/>
													</autoEditors>
													<autoFields>
														<string/>
													</autoFields>
										</autoDropdown>
												<query>
												  <autoDropdown cat="providerType" case="mixed" serviceUrl="OpenELISServlet?service=org.openelis.modules.provider.server.ProviderService" width="80px"  multiSelect="true" fromModel="true" type="integer" tab="npi,middleName">
													<autoWidths>60</autoWidths>
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
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"firstName")'/></text>
     </widget>
     <widget>
      <textbox key= "firstName" case = "upper"   width= "150px" tab="middleName,lastName"/>
     </widget>     
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"npi")'/></text>
     </widget>
     <widget>
      <textbox case= "mixed"   key= "npi" width= "80px" tab="lastName,providerType"/>
     </widget>
    </row>
    <row>
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"middleName")'/></text>
     </widget>
     <widget>
      <textbox key= "middleName" case = "upper" width= "150px" tab="providerType,firstName"/>
     </widget>
    </row>
   </panel>
   
   <panel layout= "vertical" height = "20px" xsi:type= "Panel"/>
    <panel height= "200px" key= "provTabPanel"  halign= "center" layout= "tab"  xsi:type= "Tab">
     <tab key= "tab1" text= "{resource:getString($constants,'locations')}">      
      <panel layout= "vertical" spacing= "0" xsi:type= "Panel">
       <widget halign= "center">
        <table width= "550px" cellHeight = "20" maxRows = "6" rows = "1" key= "providerAddressTable" manager = "ProviderAddressesTable" title= "" showError="false">
         <headers><xsl:value-of select='resource:getString($constants,"location")'/>,<xsl:value-of select='resource:getString($constants,"externalId")'/>,<xsl:value-of select='resource:getString($constants,"aptSuite")'/>,
				  <xsl:value-of select='resource:getString($constants,"address")'/>,<xsl:value-of select='resource:getString($constants,"city")'/>,
                  <xsl:value-of select='resource:getString($constants,"state")'/>, <xsl:value-of select='resource:getString($constants,"country")'/>,
                  <xsl:value-of select='resource:getString($constants,"zipcode")'/>,<xsl:value-of select='resource:getString($constants,"workNumber")'/>,<xsl:value-of select='resource:getString($constants,"homeNumber")'/>,
				  <xsl:value-of select='resource:getString($constants,"cellNumber")'/>,<xsl:value-of select='resource:getString($constants,"faxNumber")'/>,
				  <xsl:value-of select='resource:getString($constants,"email")'/></headers>
		 <widths>115,130,130,130,130,50,130,100,90,90,90,150,145</widths>
		 <editors>
		  <textbox case= "mixed"/>
		  <textbox case= "mixed"/>
		  <textbox case= "mixed"/>
		  <textbox case= "mixed"/>
		  <textbox case= "mixed"/>
		  <autoDropdown cat="state" key="state" case="upper" serviceUrl="OpenELISServlet?service=org.openelis.modules.provider.server.ProviderService" width="40px" popupHeight="80px" dropdown="true" fromModel = "true" type="string">
												<autoWidths>40</autoWidths>
												<autoEditors>
													<label/>
												</autoEditors>
												<autoFields>
													<string/>
												</autoFields>
												<autoItems>												 																																			
												</autoItems>
											</autoDropdown>		  
		    <autoDropdown cat="country" key="country" case="mixed" serviceUrl="OpenELISServlet?service=org.openelis.modules.provider.server.ProviderService" width="110px" popupHeight="80px" dropdown="true" fromModel = "true" type="string">
											<autoWidths>110</autoWidths>
											<autoEditors>
												<label/>
											</autoEditors>
											<autoFields>
											    <string/>
											</autoFields>
											<autoItems>												
											</autoItems>
			</autoDropdown>
			<textbox case= "mixed"/>
			<textbox case= "mixed"/>
			<textbox case= "mixed"/>
			<textbox case= "mixed"/>
			<textbox case= "mixed"/>
			<textbox case= "mixed"/>		 
		</editors>
		 <fields>
		  <string required = "true"/>
		  <string/>
		  <string/>
		  <string/>
		  <string/>
		  <string xml:space="preserve"> </string>
		  <string xml:space="preserve"> </string>
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
	     <table width= "550px" maxRows = "6" rows = "1" title = "" showError="false">
          <headers><xsl:value-of select='resource:getString($constants,"location")'/>,<xsl:value-of select='resource:getString($constants,"externalId")'/>,<xsl:value-of select='resource:getString($constants,"aptSuite")'/>,
				  <xsl:value-of select='resource:getString($constants,"address")'/>,<xsl:value-of select='resource:getString($constants,"city")'/>,
                  <xsl:value-of select='resource:getString($constants,"state")'/>, <xsl:value-of select='resource:getString($constants,"country")'/>,
                  <xsl:value-of select='resource:getString($constants,"zipcode")'/>,<xsl:value-of select='resource:getString($constants,"workNumber")'/>,<xsl:value-of select='resource:getString($constants,"homeNumber")'/>,
				  <xsl:value-of select='resource:getString($constants,"cellNumber")'/>,<xsl:value-of select='resource:getString($constants,"faxNumber")'/>,
				  <xsl:value-of select='resource:getString($constants,"email")'/></headers>
		  <widths>115,130,130,130,130,50,130,100,90,90,90,150,145</widths>
		  <editors>
		  <textbox case= "mixed"/>
		  <textbox case= "mixed"/>
		  <textbox case= "mixed"/>
		  <textbox case= "mixed"/>
		  <textbox case= "mixed"/>
		  <autoDropdown cat="state" key="state" case = "upper" serviceUrl="OpenELISServlet?service=org.openelis.modules.provider.server.ProviderService" width="40px" dropdown="true" fromModel = "true" multiSelect="true" type="string">
												<autoWidths>40</autoWidths>
												<autoEditors>
													<label/>
												</autoEditors>
												<autoFields>
													<string/>
												</autoFields>
												<autoItems>												 																																			
												</autoItems>
											</autoDropdown>
		  <autoDropdown cat="country" key="country" case = "mixed" serviceUrl="OpenELISServlet?service=org.openelis.modules.provider.server.ProviderService" width="110px" dropdown="true" fromModel = "true"  multiSelect="true" type="string">
											<autoWidths>110</autoWidths>
											<autoEditors>
												<label/>
											</autoEditors>
											<autoFields>
											    <string/>
											</autoFields>
											<autoItems>												
											</autoItems>
			</autoDropdown>		  
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
		  <collection type="string"/>
		  <collection type="string"/>	    		  	
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
	  <widget halign="left" style="WhiteContentPanel">
									<appButton action="removeRow" onclick="this" key="removeAddressButton">
									<panel xsi:type="Panel" layout="horizontal">
              						<panel xsi:type="Absolute" layout="absolute" style="RemoveRowButtonImage"/>
						              <widget>
                						<text><xsl:value-of select='resource:getString($constants,"removeRow")'/></text>
							              </widget>
							              </panel>
						            </appButton>
						            </widget> 	  	  
	</panel> 									
  </tab>
  
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
 </panel>
</display>
							  
<rpc key= "display">	
  <number key="providerId" type="integer" required="false"/>				
  <string key="lastName" max="30" required="true"/>
  <string key="firstName" max="20" required="false"/> 
  <string key="npi" max="20" required="false"/>
  <string key="middleName" max="20" required="false"/>	
  <table key="providerAddressTable"/>						      		       
  <string key="usersSubject" max="60" required="false"/>
  <string key="usersNote" required="false"/>
  <number key="providerTypeId" type="integer" required = "true"/>
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
  <collection key="providerType" type="integer" />	   
    
</rpc>

<rpc key="queryByLetter">
  <queryString key="lastName"/>
</rpc> 
</screen>
</xsl:template>
</xsl:stylesheet>     