<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale" 
                xmlns:inventoryItemMeta="xalan://org.openelis.meta.InventoryItemMeta" 
                xmlns:inventoryComponentMeta="xalan://org.openelis.meta.InventoryComponentMeta"
                xmlns:inventoryLocationMeta="xalan://org.openelis.meta.InventoryLocationMeta"
                xmlns:inventoryItemNoteMeta="xalan://org.openelis.meta.InventoryItemNoteMeta"
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZOneColumn.xsl"/>

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
  <xalan:component prefix="inventoryItemMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryItemMeta"/>
  </xalan:component>

  <xalan:component prefix="inventoryComponentMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryComponentMeta"/>
  </xalan:component>
  
  <xalan:component prefix="inventoryLocationMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryLocationMeta"/>
  </xalan:component>
  
    <xalan:component prefix="inventoryItemNoteMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryItemNoteMeta"/>
  </xalan:component>
  
  <xsl:template match="doc"> 
      <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="KitOrders" name="Kit Orders" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
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
    			<!--<xsl:call-template name="buttonPanelDivider"/>
    			<xsl:call-template name="optionsButton">
    			<xsl:with-param name="language"><xsl:value-of select="language"/></xsl:with-param>
    			</xsl:call-template>-->
				</buttonPanel>
 			</widget>
		</panel>
		<!--end button panel-->

					<panel layout="vertical" xsi:type="Panel">
						<panel key="a" layout="vertical" xsi:type="Panel">
							<panel key="secMod2" layout="table" style="Form" xsi:type="Table">
								<row>
									<widget>
											<text style="Prompt"><xsl:value-of select='resource:getString($constants,"orderNum")'/>:</text>
									</widget>
									<widget>
										<textbox case="lower" key="{inventoryItemMeta:name()}" width="75px" max="20" tab="{inventoryItemMeta:description()},{inventoryItemMeta:isLabor()}"/>
									</widget>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"orderDate")'/>:</text>
									</widget>
									<widget>
										<textbox key="{inventoryItemMeta:id()}" width="75px"/>
									</widget>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"status")'/>:</text>
									</widget>
									<widget>
										<autoDropdown key="{inventoryItemMeta:purchasedUnit()}" case="lower" width="90px" popWidth="auto" tab="{inventoryItemMeta:dispensedUnit()},{inventoryItemMeta:quantityMaxLevel()}">
													<widths>167</widths>
										</autoDropdown>
									</widget>
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"requestedBy")'/>:</text>
									</widget>
									<widget colspan="3">
										<textbox key="{inventoryItemMeta:id()}" width="255px"/>
									</widget>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"neededDays")'/>:</text>
									</widget>
									<widget>
										<textbox key="{inventoryItemMeta:id()}" width="75px"/>
									</widget>
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"costCenter")'/>:</text>
									</widget>
									<widget colspan="5">
										<autoDropdown key="{inventoryItemMeta:purchasedUnit()}" case="lower" width="284px" popWidth="auto" tab="{inventoryItemMeta:dispensedUnit()},{inventoryItemMeta:quantityMaxLevel()}">
													<widths>167</widths>
										</autoDropdown>
									</widget>
								</row>
								</panel>
								<panel layout="horizontal" xsi:type="Panel">
								<panel layout="vertical" style="Form" xsi:type="Panel">
								<titledPanel key="borderedPanel">
								<legend><text style="LegendTitle"><xsl:value-of select='resource:getString($constants,"storeVendor")'/></text></legend>
								<content>
								<panel layout="table" style="Form" xsi:type="Table">
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"vendorOrder")'/>:</text>
									</widget>
									<widget colspan="3">
										<check key="{inventoryItemMeta:isActive()}" tab="{inventoryItemMeta:isReorderAuto()},{inventoryItemMeta:dispensedUnit()}"/>
									</widget>
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"vendor")'/>:</text>
									</widget>
									<widget colspan="3">
										<autoDropdown key="{inventoryItemMeta:purchasedUnit()}" case="lower" width="172px" popWidth="auto" tab="{inventoryItemMeta:dispensedUnit()},{inventoryItemMeta:quantityMaxLevel()}">
													<widths>167</widths>
										</autoDropdown>
									</widget>
								</row>
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
								<legend><text style="LegendTitle"><xsl:value-of select='resource:getString($constants,"shipTo")'/></text></legend>
								<content><panel layout="table" style="Form" xsi:type="Table">
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"organization")'/>:</text>
									</widget>
									<widget colspan="3">
										<autoDropdown key="{inventoryItemMeta:purchasedUnit()}" case="lower" width="172px" popWidth="auto" tab="{inventoryItemMeta:dispensedUnit()},{inventoryItemMeta:quantityMaxLevel()}">
													<widths>167</widths>
										</autoDropdown>
									</widget>
								</row>
								<row>
									<!-- empty row-->
									<widget colspan="5">
										<panel layout="vertical" height="19" spacing="0" xsi:type="Panel"/>
									</widget>
								</row>
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
								</panel>
								
								</panel>
<!-- tabbed panel needs to go here -->
				<panel height="200px" key="orgTabPanel" halign="center" layout="tab" xsi:type="Tab">
					<!-- TAB 1 (Components) -->
					<tab key="tab1" text="{resource:getString($constants,'items')}">
							<panel layout="vertical" spacing="0" padding="0" xsi:type="Panel" overflow="hidden">
							<widget valign="top">
								<table width="auto" key="componentsTable" maxRows="9" title="" showError="false" showScroll="true">
										<headers><xsl:value-of select='resource:getString($constants,"quantity")'/>,<xsl:value-of select='resource:getString($constants,"component")'/>,
										<xsl:value-of select='resource:getString($constants,"location")'/></headers>
										<widths>80,324,160</widths>
										<editors>
											<autoDropdown case="mixed" width="90px" popWidth="auto">
											  <widths>100</widths>
											</autoDropdown>
											<textbox case="upper"/>
											<textbox case="upper"/>
										</editors>
										<fields>
											<dropdown key="zxcvzdcvsdcvsd" required="true"/>
											<string key="dvsvsdvsdvzdv " required="true"/>
											<string key="zvsdvsdvzdvzxc"/>
										</fields>
										<sorts>true,true,true</sorts>
										<filters>false,false,false</filters>
										<colAligns>left,left,left</colAligns>
									</table>
									<query>
									<queryTable width="auto" title="" maxRows="9" showError="false">
										<headers><xsl:value-of select='resource:getString($constants,"component")'/>,<xsl:value-of select='resource:getString($constants,"description")'/>,
										<xsl:value-of select='resource:getString($constants,"quantity")'/></headers>
										<widths>125,353,104</widths>
										<editors>
											<autoDropdown case="mixed" width="90px" popWidth="auto" multiSelect="true">
											  <widths>90</widths>
											</autoDropdown>
											<textbox case="upper"/>
											<textbox case="upper"/>
										</editors>
										<fields>test1,test2,test3
										</fields>										
									</queryTable>
									</query>
								</widget>

									<widget style="WhiteContentPanel" halign="center">
									<appButton action="removeComponentRow" onclick="this" style="Button" key="removeComponentButton">
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
					<tab key="tab4" text="Tab 2">
						<panel height="229px" width="610px" layout="vertical" xsi:type="Panel"/>
					</tab>
					<tab key="tab4" text="Tab 3">
						<panel height="229px" width="610px" layout="vertical" xsi:type="Panel"/>
					</tab>
					<tab key="tab4" text="Tab 4">
						<panel height="229px" width="610px" layout="vertical" xsi:type="Panel"/>
					</tab>
					
				</panel>
				</panel>
			</panel>
		</panel>
	</display>
	<rpc key="display">
  	  <number key="{inventoryItemMeta:id()}" type="integer" required="false"/>
      <string key="{inventoryItemMeta:name()}" max="20" required="true"/>
      <string key="{inventoryItemMeta:description()}" max="60" required="true"/>
      <dropdown key="{inventoryItemMeta:store()}" type="integer" required="true"/> 
      <dropdown key="{inventoryItemMeta:category()}" type="integer" required="true"/> 
      <number key="{inventoryItemMeta:quantityMinLevel()}" type="integer" required="true"/>
      <number key="{inventoryItemMeta:quantityMaxLevel()}" type="integer" required="true"/>
      <number key="{inventoryItemMeta:quantityToReorder()}" type="integer" required="true"/>
      <dropdown key="{inventoryItemMeta:purchasedUnit()}" type="integer" required="true"/> 
      <dropdown key="{inventoryItemMeta:dispensedUnit()}" type="integer" required="true"/> 
      <number key="{inventoryItemMeta:averageLeadTime()}" type="integer" required="false"/>
      <number key="{inventoryItemMeta:averageCost()}" type="double" required="false"/>
      <number key="{inventoryItemMeta:averageDailyUse()}" type="integer" required="false"/>
      <table key="componentsTable"/>
      <table key="locQuantitiesTable"/>
      <check key="{inventoryItemMeta:isActive()}" required="false"/>
      <check key="{inventoryItemMeta:isReorderAuto()}" required="false"/>
      <check key="{inventoryItemMeta:isLotMaintained()}" required="false"/>
      <check key="{inventoryItemMeta:isSerialRequired()}" required="false"/>locsController
      <check key="{inventoryItemMeta:isBulk()}" required="false"/>
      <check key="{inventoryItemMeta:isNotForSale()}" required="false"/>
      <check key="{inventoryItemMeta:isSubAssembly()}" required="false"/>
      <check key="{inventoryItemMeta:isLabor()}" required="false"/>
      <string key="{inventoryItemNoteMeta:subject()}" max="60" required="false"/>
      <string key="{inventoryItemNoteMeta:text()}" required="false"/>
      
	</rpc>
	<rpc key="query">
      <queryNumber key="{inventoryItemMeta:id()}" type="integer"/>
      <string key="{inventoryItemMeta:name()}"/>
      <string key="{inventoryItemMeta:description()}"/>
      <dropdown key="{inventoryItemMeta:store()}" type="integer"/> 
      <dropdown key="{inventoryItemMeta:category()}" type="integer"/> 
      <number key="{inventoryItemMeta:quantityMinLevel()}" type="integer"/>
      <number key="{inventoryItemMeta:quantityMaxLevel()}" type="integer"/>
      <number key="{inventoryItemMeta:quantityToReorder()}" type="integer"/>
      <dropdown key="{inventoryItemMeta:purchasedUnit()}" type="integer"/> 
      <dropdown key="{inventoryItemMeta:dispensedUnit()}" type="integer"/> 
      <number key="{inventoryItemMeta:averageLeadTime()}" type="integer"/>
      <number key="{inventoryItemMeta:averageCost()}" type="double"/>
      <number key="{inventoryItemMeta:averageDailyUse()}" type="integer"/>
<!--      <table key="componentsTable"/>-->
<!--      <table key="locQuantitiesTable"/>-->
      <check key="{inventoryItemMeta:isActive()}"/>
      <check key="{inventoryItemMeta:isReorderAuto()}"/>
      <check key="{inventoryItemMeta:isLotMaintained()}"/>
      <check key="{inventoryItemMeta:isSerialRequired()}"/>
      <check key="{inventoryItemMeta:isBulk()}"/>
      <check key="{inventoryItemMeta:isNotForSale()}"/>
      <check key="{inventoryItemMeta:isSubAssembly()}"/>
      <check key="{inventoryItemMeta:isLabor()}"/>
      <string key="{inventoryItemNoteMeta:subject()}"/>
      <!--<string key="text" required="false"/>-->
      
      <dropdown key="test1"/>
      <queryString key="test2"/>
      <queryNumber key="test3" type="integer"/>
	</rpc>
	<rpc key="queryByLetter">
		<queryString key="{inventoryItemMeta:name()}"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>