<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
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
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
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
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="Organization" name="Inventory Adjustment" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<panel layout="horizontal" style="WhiteContentPanel" spacing="0" padding="0" xsi:type="Panel">
			<panel layout="vertical" spacing="0" xsi:type="Panel">
		<!--button panel code-->
		<panel xsi:type="Absolute" layout="absolute" spacing="0" style="ButtonPanelContainer">
			<widget>
    			<buttonPanel key="buttons">
    			<xsl:call-template name="queryButton">
    				<xsl:with-param name="language"><xsl:value-of select="language"/></xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="previousButton">
    			<xsl:with-param name="language"><xsl:value-of select="language"/></xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="nextButton">
    			<xsl:with-param name="language"><xsl:value-of select="language"/></xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="buttonPanelDivider"/>
    			<xsl:call-template name="addButton">
    			<xsl:with-param name="language"><xsl:value-of select="language"/></xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="updateButton">
    			<xsl:with-param name="language"><xsl:value-of select="language"/></xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="buttonPanelDivider"/>
    			<xsl:call-template name="commitButton">
    			<xsl:with-param name="language"><xsl:value-of select="language"/></xsl:with-param>
    			</xsl:call-template>
    			<xsl:call-template name="abortButton">
    			<xsl:with-param name="language"><xsl:value-of select="language"/></xsl:with-param>
    			</xsl:call-template>
				</buttonPanel>
 			</widget>
		</panel>
		<!--end button panel-->
		
					<panel layout="vertical" xsi:type="Panel">
							<panel layout="table" style="Form" xsi:type="Table">
							<row>
								<widget>
									<text style="Prompt">Adjustment #:</text>
								</widget>
								<widget>
									<textbox case="lower" key="aa" width="75px" max="20" tab="??,??"/>
								</widget>
								<widget>
										<text style="Prompt">Description:</text>
								</widget>
								<widget>
									<textbox case="lower" key="ss" width="350px" max="20" tab="??,??"/>
								</widget>
							</row>
							<row>
								<widget>
									<text style="Prompt">Adj Date:</text>
								</widget>
								<widget>
									<textbox case="lower" key="aa" width="85px" max="20" tab="??,??"/>
								</widget>
								<widget>
										<text style="Prompt">User:</text>
								</widget>
								<widget>
									<textbox case="lower" key="aa" width="125px" max="20" tab="??,??"/>
								</widget>
							</row>
							</panel>
							<panel layout="vertical" spacing="0" padding="0" xsi:type="Panel" overflow="hidden">
							<widget valign="top">
								<table width="auto" key="contactsTable" manager="OrganizationContactsTable" maxRows="10" title="" showError="false" showScroll="true">
										<headers>Inventory Item, Store, Storage Location, On Hand, Phys Count, Adj Quan</headers>
										<widths>170,170,170,70,70,70</widths>										<editors>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<textbox case="upper"/>
										</editors>
										<fields>
											<string key="{orgContactMeta:name()}" required="false"/>
											<string key="{orgContactMeta:name()}" required="false"/>
											<string key="{orgContactMeta:name()}" required="false"/>
											<string key="{orgContactMeta:name()}" required="false"/>
											<string key="{orgContactMeta:name()}" required="false"/>
											<string key="{orgContactMeta:name()}" required="false"/>
										</fields>
										<sorts>true,true,true,true,true,true</sorts>
										<filters>false,false,false,false,false,false</filters>
										<colAligns>left,left,left,left,left,left</colAligns>
									</table>
									<query>
									<queryTable width="592px" title="" maxRows="8" showError="false">
										<headers><xsl:value-of select='resource:getString($constants,"type")'/>,<xsl:value-of select='resource:getString($constants,"contactName")'/>,
										<xsl:value-of select='resource:getString($constants,"aptSuite")'/>,<xsl:value-of select='resource:getString($constants,"address")'/>,
										<xsl:value-of select='resource:getString($constants,"city")'/>,<xsl:value-of select='resource:getString($constants,"state")'/>,
										<xsl:value-of select='resource:getString($constants,"zipcode")'/>,<xsl:value-of select='resource:getString($constants,"country")'/>,
										<xsl:value-of select='resource:getString($constants,"workNumber")'/>,<xsl:value-of select='resource:getString($constants,"homeNumber")'/>,
										<xsl:value-of select='resource:getString($constants,"cellNumber")'/>,<xsl:value-of select='resource:getString($constants,"faxNumber")'/>,
										<xsl:value-of select='resource:getString($constants,"email")'/></headers>
										<widths>106,130,130,130,130,56,68,126,100,90,90,90,150</widths>
										<editors>
											<autoDropdown case="mixed" width="90px" multiSelect="true"/>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<textbox case="upper"/>
											<autoDropdown case="upper" width="40px" multiSelect="true"/>
										 	<textbox case="mixed"/>
										 	<autoDropdown case="mixed" width="110px" multiSelect="true"/>
										 	<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>
											<textbox case="mixed"/>		 	
										</editors>
										<fields><xsl:value-of select='orgContactMeta:contactType()'/>,<xsl:value-of select='orgContactMeta:name()'/>,<xsl:value-of select='orgContactAddressMeta:multipleUnit()'/>,
										<xsl:value-of select='orgContactAddressMeta:streetAddress()'/>,<xsl:value-of select='orgContactAddressMeta:city()'/>,<xsl:value-of select='orgContactAddressMeta:state()'/>,
										<xsl:value-of select='orgContactAddressMeta:zipCode()'/>,<xsl:value-of select='orgContactAddressMeta:country()'/></fields>,<xsl:value-of select='orgContactAddressMeta:workPhone()'/>,
										<xsl:value-of select='orgContactAddressMeta:homePhone()'/>,	<xsl:value-of select='orgContactAddressMeta:cellPhone()'/>,<xsl:value-of select='orgContactAddressMeta:faxPhone()'/>,
										<xsl:value-of select='orgContactAddressMeta:email()'/>										
									</queryTable>
									</query>
								</widget>
								<widget style="WhiteContentPanel" halign="center">									
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
						
				</panel>
			
			<!--<panel layout="horizontal" xsi:type="Panel">
								<panel layout="vertical" style="Form" xsi:type="Panel">
								<titledPanel key="borderedPanel">
								<legend><text style="LegendTitle">Organization Address</text></legend>
								<content>
								<panel layout="table" style="Form" xsi:type="Table">
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"aptSuite")'/>:</text>
									</widget>
									<widget colspan="3">
										<textbox case="upper" key="city" width="180px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true" tab="??,??"/>
									</widget>		
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"address")'/>:</text>
									</widget>
									<widget colspan="3">
										<textbox case="upper" key="city" width="180px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true" tab="??,??"/>
									</widget>		
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"city")'/>:</text>
									</widget>
									<widget colspan="3">
										<textbox case="upper" key="city" width="180px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true" tab="?,??"/>
									</widget>		
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"state")'/>:</text>
									</widget>
									<widget>
										<textbox case="upper" key="city" width="30px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true" tab="?,??"/>
									</widget>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"zipcode")'/>:</text>
									</widget>
									<widget>
										<textbox case="upper" key="city" width="60px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true" tab="?,??"/>
									</widget>		
								</row>
								</panel>
								</content>
								</titledPanel>
								</panel>
								<panel layout="vertical" style="Form" xsi:type="Panel">
								<titledPanel key="borderedPanel">
								<legend><text style="LegendTitle">Item Information</text></legend>
								<content><panel layout="table" style="Form" xsi:type="Table">
								
								<row>
									<widget>
										<text style="Prompt">Description:</text>
									</widget>
									<widget colspan="2">
										<textbox case="upper" key="city" width="195px" max="30" style="ScreenTextboxDisplayOnly" tab="??,??"/>
									</widget>	
									<widget valign="middle">
										<check key="abc" tab="??,??"><text style="CheckboxPrompt">Add To Existing</text></check>
									</widget>
								--><!--
								</row>
								<row>
									<widget>
										<text style="Prompt">Store:</text>
									</widget>
									<widget>
										<textbox case="upper" key="city" width="115px" max="30" style="ScreenTextboxDisplayOnly" tab="??,??"/>
									</widget>	
									<widget>
										<text style="Prompt">Location:</text>
									</widget>
									<widget>
										<autoDropdown key="store" case="mixed" width="160px" tab="??,??"/>
									</widget>		
								</row>
								<row>
									<widget>
										<text style="Prompt">Purchased Units:</text>
									</widget>
									<widget>
										<textbox case="upper" key="city" width="90px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true" tab="??,??"/>
									</widget>	
									<widget>
										<text style="Prompt">Lot #:</text>
									</widget>	
									<widget>
										<textbox case="upper" key="city" width="100px" max="30" tab="??,??"/>
									</widget>	
									
								</row>
								<row>
									<widget colspan="2">
										<panel layout="vertical" xsi:type="Panel"/>
									</widget>
									<widget>
										<text style="Prompt">Exp Date:</text>
									</widget>	
									<widget>
										<calendar key="" begin="0" end="2"/>
									</widget>	
									
								</row>
								</panel>
								</content>
								</titledPanel>
								</panel>
								</panel>-->
								
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
      <check key="{organizationMeta:isActive()}" required="false"/>
      <string key="{orgNoteMeta:subject()}" max="60" required="false"/>
      <string key="{orgNoteMeta:text()}" required="false"/>
      <dropdown key="{parentOrgMeta:name()}" type="integer" required="false"/> 
      <dropdown key="{orgAddressMeta:state()}" required="false"/>
      <dropdown key="{orgAddressMeta:country()}" required="true"/>
      <table key="contactsTable"/>
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
      <queryCheck key="{organizationMeta:isActive()}" required="false"/>
      <table key="contactsTable"/>
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