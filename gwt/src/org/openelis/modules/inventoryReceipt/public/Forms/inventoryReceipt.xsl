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
<screen id="InventoryReceipt" name="Inventory Receipt" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<HorizontalPanel style="WhiteContentPanel" spacing="0" padding="0">
			<VerticalPanel spacing="0">
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
			<VerticalPanel>
				<VerticalPanel spacing="0" padding="0" overflow="hidden">
					<widget valign="top">
						<table width="auto" key="contactsTable" manager="OrganizationContactsTable" maxRows="10" title="" showError="false" showScroll="true">
							<headers>Order #,Date Rec,UPC,Inventory Item,Organization,Qty,Cost,QC,Ext Reference</headers>
							<widths>60,70,85,140,155,30,50,80,100</widths>										<editors>
								<textbox case="upper"/>
								<textbox case="upper"/>
								<textbox case="upper"/>
								<textbox case="upper"/>
								<textbox case="upper"/>
								<textbox case="upper"/>
								<textbox case="upper"/>
								<textbox case="upper"/>
								<textbox case="upper"/>
							</editors>
							<fields>
								<string key="{organizationMeta:getId()}" required="false"/>
								<string key="{organizationMeta:getId()}" required="false"/>
								<string key="{organizationMeta:getId()}" required="false"/>
								<string key="{organizationMeta:getId()}" required="false"/>
								<string key="{organizationMeta:getId()}" required="false"/>
								<string key="{organizationMeta:getId()}" required="false"/>
								<string key="{organizationMeta:getId()}" required="false"/>
								<string key="{organizationMeta:getId()}" required="false"/>
								<string key="{organizationMeta:getId()}" required="false"/>
							</fields>
							<sorts>true,true,true,true,true,true,true,true,true</sorts>
							<filters>false,false,false,false,false,false,false,false,false</filters>
							<colAligns>left,left,left,left,left,left,left,left,left</colAligns>
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
								<fields><xsl:value-of select='organizationMeta:getId()'/>,<xsl:value-of select='organizationMeta:getId()'/>,<xsl:value-of select='organizationMeta:getId()'/>,
								<xsl:value-of select='organizationMeta:getId()'/>,<xsl:value-of select='organizationMeta:getId()'/>,<xsl:value-of select='organizationMeta:getId()'/>,
								<xsl:value-of select='organizationMeta:getId()'/>,<xsl:value-of select='organizationMeta:getId()'/></fields>,<xsl:value-of select='organizationMeta:getId()'/>,
								<xsl:value-of select='organizationMeta:getId()'/>,	<xsl:value-of select='organizationMeta:getId()'/>,<xsl:value-of select='organizationMeta:getId()'/>,
								<xsl:value-of select='organizationMeta:getId()'/>										
							</queryTable>
							</query>
						</widget>
						<widget style="WhiteContentPanel" halign="center">									
							<appButton action="removeRow" onclick="this" style="Button" key="removeContactButton">
								<HorizontalPanel>
              						<AbsolutePanel style="RemoveRowButtonImage"/>
                						<text><xsl:value-of select='resource:getString($constants,"removeRow")'/></text>
							              </HorizontalPanel>
						            </appButton>
						            </widget>
							</VerticalPanel>
						
				</VerticalPanel>
			
			<HorizontalPanel>
				<VerticalPanel style="Form">
					<titledPanel key="borderedPanel">
						<legend><text style="LegendTitle">Organization Address</text></legend>
							<content>
								<TablePanel style="Form">
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"aptSuite")'/>:</text>
									<widget colspan="3">
										<textbox case="upper" key="city" width="180px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true" tab="??,??"/>
									</widget>		
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"address")'/>:</text>
									<widget colspan="3">
										<textbox case="upper" key="city" width="180px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true" tab="??,??"/>
									</widget>		
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"city")'/>:</text>
									<widget colspan="3">
										<textbox case="upper" key="city" width="180px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true" tab="?,??"/>
									</widget>		
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"state")'/>:</text>
									<textbox case="upper" key="city" width="30px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true" tab="?,??"/>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"zipcode")'/>:</text>
									<textbox case="upper" key="city" width="60px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true" tab="?,??"/>
								</row>
							</TablePanel>
						</content>
						</titledPanel>
					</VerticalPanel>
					<VerticalPanel style="Form">
						<titledPanel key="borderedPanel">
							<legend><text style="LegendTitle">Item Information</text></legend>
								<content>
								<TablePanel style="Form">
								<row>
									<text style="Prompt">Description:</text>
									<widget colspan="2">
										<textbox case="upper" key="city" width="195px" max="30" style="ScreenTextboxDisplayOnly" tab="??,??"/>
									</widget>
									<widget valign="middle">
										<check key="abc" tab="??,??"><text style="CheckboxPrompt">Add To Existing</text></check>
									</widget>
								</row>
								<row>
									<text style="Prompt">Store:</text>
									<textbox case="upper" key="city" width="115px" max="30" style="ScreenTextboxDisplayOnly" tab="??,??"/>
									<text style="Prompt">Location:</text>
									<autoDropdown key="store" case="mixed" width="160px" tab="??,??"/>
								</row>
								<row>
									<text style="Prompt">Purchased Units:</text>
									<textbox case="upper" key="city" width="90px" max="30" style="ScreenTextboxDisplayOnly" alwaysDisabled="true" tab="??,??"/>
									<text style="Prompt">Lot #:</text>
									<textbox case="upper" key="city" width="100px" max="30" tab="??,??"/>
								</row>
								<row>
									<widget colspan="2">
										<VerticalPanel/>
									</widget>
									<text style="Prompt">Exp Date:</text>
									<calendar key="" begin="0" end="2"/>							
								</row>
								</TablePanel>
								</content>
								</titledPanel>
								</VerticalPanel>
								</HorizontalPanel>
								
				</VerticalPanel>				
		</HorizontalPanel>
	</display>
	<rpc key="display">
  	  <number key="{organizationMeta:getId()}" type="integer" required="false"/>
      <number key="{organizationMeta:getId()}" required="false" type="integer"/>
      <string key="{organizationMeta:getId()}" max="40" required="true"/>
      <string key="{organizationMeta:getId()}" max="30" required="true"/>
      <string key="{organizationMeta:getId()}" max="30" required="false"/>
      <string key="{organizationMeta:getId()}" max="30" required="true"/>
      <string key="{organizationMeta:getId()}" max="10" required="true"/>
      <check key="{organizationMeta:getId()}" required="false"/>
      <string key="{organizationMeta:getId()}" max="60" required="false"/>
      <string key="{organizationMeta:getId()}" required="false"/>
      <dropdown key="{organizationMeta:getId()}" type="integer" required="false"/> 
      <dropdown key="{organizationMeta:getId()}" required="false"/>
      <dropdown key="{organizationMeta:getId()}" required="true"/>
      <table key="contactsTable"/>
	</rpc>
	<rpc key="query">
      <queryNumber key="{organizationMeta:getId()}" type="integer"/>
      <queryString key="{organizationMeta:getId()}"/>
      <queryString key="{organizationMeta:getId()}"/>
      <queryString key="{organizationMeta:getId()}" value="query"/>
      <queryString key="{organizationMeta:getId()}"/>
      <queryString key="{organizationMeta:getId()}"/>
      <queryString key="{organizationMeta:getId()}"/>
      <queryString key="{organizationMeta:getId()}"/>
      <queryString key="{organizationMeta:getId()}"/>
      <dropdown key="{organizationMeta:getId()}" required="false"/>
      <dropdown key="{organizationMeta:getId()}" required="false"/>
      <queryCheck key="{organizationMeta:getId()}" required="false"/>
      <table key="contactsTable"/>
      <dropdown key="{organizationMeta:getId()}" required="false"/>
	  <queryString key="{organizationMeta:getId()}" required="false"/>
	  <queryString key="{organizationMeta:getId()}" required="false"/>
	  <queryString key="{organizationMeta:getId()}" required="false"/>
	  <queryString key="{organizationMeta:getId()}" required="false"/>
	  <dropdown key="{organizationMeta:getId()}" required="false"/>
	  <queryString key="{organizationMeta:getId()}" required="false"/>
      <queryString key="{organizationMeta:getId()}" required="false"/>
      <queryString key="{organizationMeta:getId()}" required="false"/>
      <queryString key="{organizationMeta:getId()}" required="false"/>
	  <queryString key="{organizationMeta:getId()}" required="false"/>
      <queryString key="{organizationMeta:getId()}" required="false"/>
	  <dropdown key="{organizationMeta:getId()}" required="false"/>
	</rpc>
	<rpc key="queryByLetter">
		<queryString key="{organizationMeta:getId()}"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>