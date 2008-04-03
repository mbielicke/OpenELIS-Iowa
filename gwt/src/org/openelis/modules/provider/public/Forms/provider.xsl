<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.server.constants.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                xmlns:providerMeta="xalan://org.openelis.meta.ProviderMeta"
                xmlns:providerNoteMeta="xalan://org.openelis.meta.ProviderNoteMeta"
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

  <xsl:template match="doc"> 
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle('org.openelis.modules.main.server.constants.OpenELISConstants',locale:new(string($language)))"/>
<screen id= "Provider" serviceUrl= "OpenElisService"
xsi:noNamespaceSchemaLocation= "file:///home/tschmidt/workspace/libraries/metadata/FormSchema.xsd"
xmlns:locale = "xalan:/java.util.Locale" xmlns:xalan= "http://xml.apache.org/xalan" xmlns:xsi= "http://www.w3.org/2001/XMLSchema-instance">
<display>
 <panel layout= "horizontal"  spacing= "0" padding= "0" style="WhiteContentPanel" xsi:type= "Panel">
  <aToZ height="425px" width="100%" key="hideablePanel" visible="false" maxRows="19" title = "" tablewidth="auto" headers = "{resource:getString($constants,'lastName')},{resource:getString($constants,'firstName')}" colwidths ="75,75">
   <!--<panel layout= "horizontal" xsi:type= "Panel" style="ScreenLeftPanel" spacing= "0">-->
    <xsl:if test="string($language)='en'">
		 <buttonPanel key="atozButtons">
	       <xsl:call-template name="aToZLeftPanelButtons"/>		
		 </buttonPanel>
    </xsl:if>     
   <!--</panel>-->
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
   <panel key= "secMod" width = "450px" layout= "table"  style= "Form" xsi:type= "Table">
    <row>
    <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"id")'/></text>
     </widget>
     <widget> 
      <textbox  width= "50px" key = "{providerMeta:id()}"  tab="{providerMeta:lastName()},{providerMeta:npi()}"/>
     </widget>                 
    </row>
    <row>
      <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"lastName")'/></text>
     </widget>
     <widget width= "210px"> 
      <textbox key = "{providerMeta:lastName()}" case = "upper" tab="{providerMeta:firstName()},{providerMeta:npi()}"/>
     </widget>     
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"type")'/></text>
     </widget>
    <!-- <widget>
										<autoDropdown cat="providerType" key="providerType" case="mixed" serviceUrl="OpenELISServlet?service=org.openelis.modules.provider.server.ProviderService" width="80px"  multiSelect="false" fromModel="true" type="integer" tab="npi,middleName">
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
										</widget> -->         
		<widget>
		  <autoDropdown key="{providerMeta:type()}" case="mixed" width="80px" popWidth="auto" tab="{providerMeta:npi()},{providerMeta:middleName()}">
			<widths>40</widths>
		  </autoDropdown>
		</widget>								
    </row>
    <row>
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"firstName")'/></text>
     </widget>
     <widget>
      <textbox key= "{providerMeta:firstName()}" case = "upper"   width= "150px" tab="{providerMeta:middleName},{providerMeta:lastName()}"/>
     </widget>     
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"npi")'/></text>
     </widget>
     <widget>
      <textbox case= "mixed"   key= "{providerMeta:npi()}" width= "80px" tab="{providerMeta:lastName()},{providerMeta:type()}"/>
     </widget>
    </row>
    <row>
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"middleName")'/></text>
     </widget>
     <widget>
      <textbox key= "{providerMeta:middleName}" case = "upper" width= "150px" tab="{providerMeta:type()},{providerMeta:firstName()}"/>
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
		  <!--<autoDropdown cat="state" key="state" case="upper" serviceUrl="OpenELISServlet?service=org.openelis.modules.provider.server.ProviderService" width="40px"  popupHeight="80px" dropdown="true" fromModel = "true" type="string">
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
			</autoDropdown>-->
			<autoDropdown  case="upper" width="40px" popWidth="auto">
				<widths>40</widths>
			</autoDropdown>
			<autoDropdown case="mixed" width="110px" popWidth="auto">
				<widths>110</widths>
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
		  <!--<autoDropdown cat="state" key="state" case = "upper" serviceUrl="OpenELISServlet?service=org.openelis.modules.provider.server.ProviderService" width="40px" dropdown="true" fromModel = "true" multiSelect="true" type="string">
												<autoWidths>40</autoWidths>
												<autoEditors>
													<label/>
												</autoEditors>
												<autoFields>
													<string/>
												</autoFields>												
											</autoDropdown>
		  <autoDropdown cat="country" key="country" case = "mixed" serviceUrl="OpenELISServlet?service=org.openelis.modules.provider.server.ProviderService" width="110px" dropdown="true" fromModel = "true"  multiSelect="true" type="string">
											<autoWidths>110</autoWidths>
											<autoEditors>
												<label/>
											</autoEditors>
											<autoFields>
											    <string/>
											</autoFields>											
			</autoDropdown>-->
			<autoDropdown case="upper"  width="40px" popWidth="auto" multiSelect = "true">
				<widths>40</widths>
			</autoDropdown>
			<autoDropdown case="mixed" width="110px" popWidth="auto" multiSelect = "true">
				<widths>110</widths>
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
		  <dropdown/>
		  <dropdown/>		  	    		  	
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
										<textbox case="mixed" key="{providerNoteMeta:subject()}" width="405px" max="60"/>
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
										<textarea width="524px" height="50px" case="mixed" key="{providerNoteMeta:text()}"/>
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
  <number key="{providerMeta:id()}" type="integer" required="false"/>				
  <string key="{providerMeta:lastName()}" max="30" required="true"/>
  <string key="{providerMeta:firstName()}" max="20" required="false"/> 
  <string key="{providerMeta:npi()}" max="20" required="false"/>
  <string key="{providerMeta:middleName()}" max="20" required="false"/>	
  <table key="providerAddressTable"/>						      		       
  <string key="{providerNoteMeta:subject()}" max="60" required="false"/>
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
  <table key="providerAddressTable"/>
  <dropdown key="{providerMeta:type()}" type="integer" required = "false"/>    
</rpc>

<rpc key="queryByLetter">
  <queryString key="{providerMeta:lastName()}"/>
</rpc> 
</screen>
</xsl:template>
</xsl:stylesheet>     