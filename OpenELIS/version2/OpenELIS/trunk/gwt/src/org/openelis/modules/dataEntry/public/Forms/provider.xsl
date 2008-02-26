<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.server.constants.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                extension-element-prefixes="resource"
                version="1.0">
               <xsl:import href="aToZOneColumn.xsl"/> 
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
      <table maxRows = "20" manager= "ProviderNamesTable" width= "auto" style="ScreenLeftTable"  key = "providersTable" title="">
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
   		<panel xsi:type="Absolute" layout="absolute" spacing="0" style="ButtonPanelContainer">
    <widget>						
		<buttonPanel key="buttons">
      <appButton action="query" toggle="true">
              <panel xsi:type="Panel" layout="horizontal">
              <panel xsi:type="Absolute" layout="absolute" style="QueryButtonImage"/>
              <widget>
                <text>Query</text>
              </widget>
              </panel>
            </appButton>
 <appButton action="prev" toggle="true">
              <panel xsi:type="Panel" layout="horizontal">
              <panel xsi:type="Absolute" layout="absolute" style="PreviousButtonImage"/>
              <widget>
                <text>Previous</text>
              </widget>
              </panel>
            </appButton>
 <appButton action="next" toggle="true">
              <panel xsi:type="Panel" layout="horizontal">
              <panel xsi:type="Absolute" layout="absolute" style="NextButtonImage"/>
              <widget>
                <text>Next</text>
              </widget>
              </panel>
            </appButton>
            <panel xsi:type="Absolute" layout="absolute" style="ButtonDivider"/>
            <appButton action="add" toggle="true">
              <panel xsi:type="Panel" layout="horizontal">
              <panel xsi:type="Absolute" layout="absolute" style="AddButtonImage"/>
              <widget>
                <text>Add</text>
              </widget>
              </panel>
            </appButton>
            <appButton action="update" toggle="true">
              <panel xsi:type="Panel" layout="horizontal">
              <panel xsi:type="Absolute" layout="absolute" style="UpdateButtonImage"/>
              <widget>
                <text>Update</text>
              </widget>
              </panel>
            </appButton>
            <panel xsi:type="Absolute" layout="absolute" style="ButtonDivider"/>
            <appButton action="commit">
              <panel xsi:type="Panel" layout="horizontal">
              <panel xsi:type="Absolute" layout="absolute" style="CommitButtonImage"/>
              <widget>
                <text>Commit</text>
              </widget>
              </panel>
            </appButton>
            <appButton action="abort">
              <panel xsi:type="Panel" layout="horizontal">
              <panel xsi:type="Absolute" layout="absolute" style="AbortButtonImage"/>
              <widget>
                <text>Abort</text>
              </widget>
              </panel>
            </appButton>
            <panel xsi:type="Absolute" layout="absolute" style="ButtonSpacer"/>
          </buttonPanel>						
	</widget>
					
   </panel>
   
   <panel key= "secMod" layout= "table" width= "450px" style= "Form" xsi:type= "Table">
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
										<autoDropdown cat="providerType" key="providerType" case="mixed" serviceUrl="ProviderServlet" width="80px"   fromModel="true" type="integer" tab="npi,middleName">
													<autoWidths>60</autoWidths>
													<autoEditors>
														<label/>
													</autoEditors>
													<autoFields>
														<string/>
													</autoFields>
										</autoDropdown>
												<query>
												  <autoDropdown cat="providerType" case="mixed" serviceUrl="ProviderServlet" width="80px"  multiSelect="true" fromModel="true" type="integer" tab="npi,middleName">
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
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"provider_NPI")'/></text>
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
    <panel height= "200px" key= "orgTabPanel" halign= "center" layout= "tab" width= "600px" xsi:type= "Tab">
     <tab key= "tab1" text= "{resource:getString($constants,'provider_Locations')}">      
      <panel layout= "vertical" spacing= "0" xsi:type= "Panel">
       <widget halign= "center">
        <table width= "550px" maxRows = "9" rows = "1" key= "providerAddressTable" manager = "ProviderAddressesTable" title= "">
         <headers><xsl:value-of select='resource:getString($constants,"provider_Location")'/>,<xsl:value-of select='resource:getString($constants,"provider_ExternalId")'/>,<xsl:value-of select='resource:getString($constants,"aptSuite")'/>,
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
		  <autoDropdown cat="state" key="state" case="upper" serviceUrl="ProviderServlet" width="40px" popupHeight="80px" dropdown="true" fromModel = "true" type="string">
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
		    <autoDropdown cat="country" key="country" case="mixed" serviceUrl="ProviderServlet" width="110px" popupHeight="80px" dropdown="true" fromModel = "true" type="string">
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
	     <table width= "550px" maxRows = "9" rows = "1" title = "">
          <headers><xsl:value-of select='resource:getString($constants,"provider_Location")'/>,<xsl:value-of select='resource:getString($constants,"provider_ExternalId")'/>,<xsl:value-of select='resource:getString($constants,"aptSuite")'/>,
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
		  <autoDropdown cat="state" key="state" case = "upper" serviceUrl="ProviderServlet" width="40px" dropdown="true" fromModel = "true" multiSelect="true" type="string">
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
		  <autoDropdown cat="country" key="country" case = "mixed" serviceUrl="ProviderServlet" width="110px" dropdown="true" fromModel = "true"  multiSelect="true" type="string">
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
	  <widget halign="right" style="WhiteContentPanel">
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

									<panel layout="vertical" height="3px" xsi:type="Panel"/>
							<panel key="noteFormPanel" layout="table" style="FormBorderless" width="160px" xsi:type="Table" padding="0" spacing="0">
										<row>
										<widget>
												<text style="Prompt"><xsl:value-of select='resource:getString($constants,"subject")'/></text>
										</widget>
										<widget>
										<textbox case="mixed" key="usersSubject" width="400px" max="60"/>
										</widget>
										<widget>
										<appButton action="standardNote" onclick="this" key="standardNoteButton">
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
										<textarea width="510px" height="50px" case="mixed" key="usersNote"/>
										</widget>
										</row>
								</panel> 

							<panel layout="vertical" height="2px" xsi:type="Panel"/>
							<panel key="notesPanel" valign="top" layout="vertical" width="100%" xsi:type="Panel"/>	
							 <!--<widget valign="top">
                 
							  <pagedTree key="notesTree" vertical="true" width="400px" height="100px" itemsPerPage="1000" title="Provider Notes"/>
                               
							</widget> -->    

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
  <model key = "notesModel"/>
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