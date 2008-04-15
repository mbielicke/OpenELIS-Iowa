<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.server.constants.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                xmlns:providerMeta="xalan://org.openelis.meta.ProviderMeta"
                xmlns:providerNoteMeta="xalan://org.openelis.meta.ProviderNoteMeta"
                xmlns:providerAddrMeta="xalan://org.openelis.meta.ProviderAddressMeta"
                xmlns:providerAddrAddrMeta="xalan://org.openelis.meta.ProviderAddressAddressMeta"
                extension-element-prefixes="resource"
                version="1.0">
                
<xsl:import href="aToZOneColumn.xsl"/> 
  
  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.server.constants.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
  <xalan:component prefix="providerMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.ProviderMeta"/>
  </xalan:component>  
  
  <xalan:component prefix="providerNoteMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.ProviderNoteMeta"/>
  </xalan:component> 
  
  <xalan:component prefix="providerAddrMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.ProviderAddressMeta"/>
  </xalan:component>
  
  <xalan:component prefix="providerAddrAddrMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.ProviderAddressAddressMeta"/>
  </xalan:component>

  <xsl:template match="doc"> 
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle('org.openelis.modules.main.server.constants.OpenELISConstants',locale:new(string($language)))"/>
<screen id= "Provider" serviceUrl= "OpenElisService"
xsi:noNamespaceSchemaLocation= "file:///home/tschmidt/workspace/libraries/metadata/FormSchema.xsd"
xmlns:locale = "xalan:/java.util.Locale" xmlns:xalan= "http://xml.apache.org/xalan" xmlns:xsi= "http://www.w3.org/2001/XMLSchema-instance">
<display>
 <panel layout= "horizontal"  spacing= "0" padding= "0" style="WhiteContentPanel" xsi:type= "Panel">
  <aToZ height="425px" width="100%" key="hideablePanel" maxRows="19" title = "" tablewidth="auto" headers = "{resource:getString($constants,'lastName')},{resource:getString($constants,'firstName')}" colwidths ="88,87">   
		 <buttonPanel key="atozButtons">
	       <xsl:call-template name="aToZLeftPanelButtons"/>		
		 </buttonPanel>   
  </aToZ>
   <panel layout= "vertical" spacing= "0" xsi:type= "Panel">
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
   
   <panel layout= "vertical"  height = "5px" xsi:type= "Panel"/>
   <panel layout="vertical" xsi:type="Panel">
   <panel key= "secMod" width = "450px" layout= "table"  style= "Form" xsi:type= "Table">
    <row>
    <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"id")'/>:</text>
     </widget>
     <widget> 
      <textbox  width= "50px"  key = "{providerMeta:id()}"  tab="{providerMeta:lastName()},{providerMeta:npi()}"/>
     </widget>                 
    </row>
    <row>
      <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"lastName")'/>:</text>
     </widget>
     <widget > 
      <textbox key = "{providerMeta:lastName()}" max="30" width= "215px" case = "upper" tab="{providerMeta:firstName()},{providerMeta:npi()}"/>
     </widget>     
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"type")'/>:</text>
     </widget>           
		<widget>
		  <autoDropdown key="{providerMeta:type()}" case="mixed" width="80px" popWidth="auto" tab="{providerMeta:npi()},{providerMeta:middleName()}">
			<widths>90</widths>
		  </autoDropdown>
		</widget>								
    </row>
    <row>
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"firstName")'/>:</text>
     </widget>
     <widget>
      <textbox key= "{providerMeta:firstName()}" max="20"  case = "upper"   width= "145px" tab="{providerMeta:middleName()},{providerMeta:lastName()}"/>
     </widget>     
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"npi")'/>:</text>
     </widget>
     <widget>
      <textbox case= "mixed"   key= "{providerMeta:npi()}" max="20"  width= "145px" tab="{providerMeta:lastName()},{providerMeta:type()}"/>
     </widget>
    </row>
    <row>
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"middleName")'/>:</text>
     </widget>
     <widget>
      <textbox key= "{providerMeta:middleName()}" max="20" case = "upper" width= "145px" tab="{providerMeta:type()},{providerMeta:firstName()}"/>
     </widget>
    </row>
   </panel>
   
    <panel height= "200px" key= "provTabPanel"  halign="center" layout= "tab"  xsi:type= "Tab">
     <tab key= "tab1" text= "{resource:getString($constants,'locations')}">      
      <panel layout= "vertical" spacing= "0" padding="0" xsi:type= "Panel">
       <widget valign="top">
        <table width= "574px" maxRows = "9" key= "providerAddressTable" manager = "ProviderAddressesTable" title= "" showError="false">
         <headers><xsl:value-of select='resource:getString($constants,"location")'/>,<xsl:value-of select='resource:getString($constants,"externalId")'/>,<xsl:value-of select='resource:getString($constants,"aptSuite")'/>,
				  <xsl:value-of select='resource:getString($constants,"address")'/>,<xsl:value-of select='resource:getString($constants,"city")'/>,
                  <xsl:value-of select='resource:getString($constants,"state")'/>, <xsl:value-of select='resource:getString($constants,"country")'/>,
                  <xsl:value-of select='resource:getString($constants,"zipcode")'/>,<xsl:value-of select='resource:getString($constants,"workNumber")'/>,<xsl:value-of select='resource:getString($constants,"homeNumber")'/>,
				  <xsl:value-of select='resource:getString($constants,"cellNumber")'/>,<xsl:value-of select='resource:getString($constants,"faxNumber")'/>,
				  <xsl:value-of select='resource:getString($constants,"email")'/></headers>
		 <widths>115,130,130,130,130,60,130,100,90,90,90,150,145</widths>
		 <editors>
		  <textbox case= "mixed" max="50" />
		  <textbox case= "mixed" max="10"/>
		  <textbox case= "mixed" max="30"/>
		  <textbox case= "mixed" max="30"/>
		  <textbox case= "mixed" max="30"/>		  
			<autoDropdown  case="upper" width="45px" popWidth="auto">
				<widths>37</widths>
			</autoDropdown>
			<autoDropdown case="mixed" width="110px" popWidth="auto">
				<widths>105</widths>
			</autoDropdown>
			<textbox case= "mixed" max="10"/>
			<textbox case= "mixed" max="21"/>
			<textbox case= "mixed" max="16"/>
			<textbox case= "mixed" max="16"/>
			<textbox case= "mixed" max="16"/>
			<textbox case= "mixed" max="80"/>		 
		</editors>
		 <fields>
		  <string required = "true"/>
		  <string/>
		  <string/>
		  <string/>
		  <string required = "true"/>		  		  
		  <dropdown/>
		  <dropdown/>
		  <string required = "true"/>
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
	     <queryTable width= "574px" maxRows = "9" title = "" showError="false">
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
			<autoDropdown case="upper"  width="45px" popWidth="auto" multiSelect = "true">
				<widths>37</widths>
			</autoDropdown>
			<autoDropdown case="mixed" width="110px" popWidth="auto" multiSelect = "true">
				<widths>105</widths>
			</autoDropdown>		  
			<textbox case= "mixed"/>
			<textbox case= "mixed"/>
			<textbox case= "mixed"/>
			<textbox case= "mixed"/>
			<textbox case= "mixed"/>
			<textbox case= "mixed"/>		 
		</editors>
		<fields>
		  <xsl:value-of select='providerAddrMeta:location()'/>,<xsl:value-of select='providerAddrMeta:externalId()'/>,
		  <xsl:value-of select='providerAddrAddrMeta:multipleUnit()'/>,<xsl:value-of select='providerAddrAddrMeta:streetAddress()'/>,
		  <xsl:value-of select='providerAddrAddrMeta:city()'/>,<xsl:value-of select='providerAddrAddrMeta:state()'/>,
		  <xsl:value-of select='providerAddrAddrMeta:country()'/>,<xsl:value-of select='providerAddrAddrMeta:zipCode()'/>,
		  <xsl:value-of select='providerAddrAddrMeta:workPhone()'/>,<xsl:value-of select='providerAddrAddrMeta:homePhone()'/>,
		  <xsl:value-of select='providerAddrAddrMeta:cellPhone()'/>,<xsl:value-of select='providerAddrAddrMeta:faxPhone()'/>,
		  <xsl:value-of select='providerAddrAddrMeta:email()'/>																				
		  </fields>
		  <sorts>true,true,true,true,true,true,true,true,true,true,true,true,true</sorts>
		  <filters>false,false,false,false,false,false,false,false,false,false,false,false,false</filters>
		  <colAligns>left,left,left,left,left,left,left,left,left,left,left,left,left</colAligns>
	    </queryTable>
	    </query>
	  </widget>							
							
	  <widget style="WhiteContentPanel" halign="center">
									<appButton action="removeRow" onclick="this" style="Button" key="removeAddressButton">
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
										<textbox case="mixed" key="{providerNoteMeta:subject()}" width="412px" max="60" showError="false"/>
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
										<textarea width="531px" height="61px" case="mixed" key="{providerNoteMeta:text()}" showError="false"/>
										</widget>
										</row>
								 
							<row>
								<widget>
								<html key="spacer" xml:space="preserve"> </html>
								</widget>
								<widget colspan="2">
								<panel style="notesPanelContainer" layout="horizontal" xsi:type="Panel">
								<panel key="notesPanel" style="NotesPanel" valign="top" onclick="this" height="145px" width="531px" layout="vertical" overflowX="auto" overflowY="scroll" xsi:type="Panel">
								
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
							  
<rpc key= "display">	
  <number key="{providerMeta:id()}" type="integer" required="false"/>				
  <string key="{providerMeta:lastName()}"  required="true"/>
  <string key="{providerMeta:firstName()}"  required="false"/> 
  <string key="{providerMeta:npi()}" required="false"/>
  <string key="{providerMeta:middleName()}"  required="false"/>	
  <table key="providerAddressTable"/>						      		       
  <string key="{providerNoteMeta:subject()}" required="false"/>
  <string key="{providerNoteMeta:text()}" required="false"/>
  <dropdown key="{providerMeta:type()}" type="integer" required = "true"/>
</rpc>
					   
<rpc key= "query">     
  <queryNumber key="{providerMeta:id()}" type="integer" />				
  <queryString key="{providerMeta:lastName()}" />
  <queryString key="{providerMeta:firstName()}" /> 
  <queryString key="{providerMeta:npi()}" />
  <queryString key="{providerMeta:middleName()}" />	  
  <queryString key="{providerNoteMeta:subject()}" />
  <queryString key="{providerNoteMeta:text()}" /> 
  <dropdown key="{providerMeta:type()}" type="integer" required = "false"/>                            
  <queryString key="{providerAddrMeta:location()}" required="false"/>
	  <queryString key="{providerAddrMeta:externalId()}" required="false"/>
	  <queryString key="{providerAddrAddrMeta:multipleUnit()}" required="false"/>
	  <queryString key="{providerAddrAddrMeta:streetAddress()}" required="false"/>
	  <queryString key="{providerAddrAddrMeta:city()}" required="false"/>
	  <dropdown key="{providerAddrAddrMeta:state()}" required="false"/>
	  <dropdown key="{providerAddrAddrMeta:country()}" required="false"/>
	  <queryString key="{providerAddrAddrMeta:zipCode()}" required="false"/>
      <queryString key="{providerAddrAddrMeta:workPhone()}" required="false"/>
      <queryString key="{providerAddrAddrMeta:homePhone()}" required="false"/>
      <queryString key="{providerAddrAddrMeta:cellPhone()}" required="false"/>
	  <queryString key="{providerAddrAddrMeta:faxPhone()}" required="false"/>
      <queryString key="{providerAddrAddrMeta:email()}" required="false"/>	 
</rpc>

<rpc key="queryByLetter">
  <queryString key="{providerMeta:lastName()}"/>
</rpc> 
</screen>
</xsl:template>
</xsl:stylesheet>     