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
    <xsl:variable name="constants" select="resource:getBundle('org.openelis.modules.main.client.constants.OpenELISConstants',locale:new(string($language)))"/>
<screen id="Storage" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<panel layout="horizontal" spacing="0" padding="0" xsi:type="Panel">
			<!--left table goes here -->
				<aToZ height="75px" width="100%" key="hideablePanel" visible="false" onclick="this">
				 <panel layout="horizontal" style="ScreenLeftPanel" xsi:type="Panel" spacing="0">
				 <xsl:if test="string($language)='en'">
		    <panel layout="horizontal" xsi:type="Panel" spacing="0" padding="0">
			<panel layout="vertical" xsi:type="Panel" spacing="0">
			<widget>
            <html key="#" onclick="this">&lt;a class='navIndex'&gt;#&lt;/a&gt;</html>
          </widget>
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
          </panel>
          <panel layout="vertical" xsi:type="Panel" spacing="0">
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
				<table manager="StorageUnitDescTable" width="auto" style="ScreenLeftTable" key="StorageUnitTable" maxRows="10" title="{resource:getString($constants,'storageUnits')}">
				<headers><xsl:value-of select='resource:getString($constants,"description")'/></headers>
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
			<panel layout="vertical" spacing="0" width="400px" xsi:type="Panel">
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
            <appButton action="delete" toggle="true">
              <widget>
                <text>Delete</text>
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
					<panel layout="vertical" width="400px" xsi:type="Panel">
							<panel key="secMod2" layout="table" style="FormBorderless" width="225px" xsi:type="Table">
								<row>
									<panel layout="horizontal" xsi:type="Panel" style="FormVerticalSpacing"/>
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"id")'/>:</text>
									</widget>
									<widget>
										<textbox key="id" width="75px" tab="orgName,isActive"/>
									</widget>
								</row>
								<row>								
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"category")'/>:</text>
									</widget>
									<widget>
										<autoDropdown key="category" cat="category" case="mixed" serviceUrl="StorageUnitServlet" width="110px" dropdown="true" type="string" fromModel="true" tab="contactsTable,parentOrg">
											<autoWidths>89</autoWidths>
											<autoEditors>
												<label/>
											</autoEditors>
											<autoFields>
												<string/>
											</autoFields>
										</autoDropdown>
										<query>
										<autoDropdown cat="category" case="mixed" serviceUrl="StorageUnitServlet" width="110px" dropdown="true" type="string" fromModel="true" multiSelect="true" tab="contactsTable,parentOrg">
											<autoWidths>89</autoWidths>
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
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"description")'/>:</text>
									</widget>
									<widget>
										<textbox case="mixed" key="description" max="60" width="300px" tab="multUnit,orgName"/>
									</widget>
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"isSingular")'/>:</text>
									</widget>
									<widget>
										<check key="isSingular" tab="contactsTable,parentOrg"/>
										<query>
											<autoDropdown cat="isSingular" case="upper" serviceUrl="StorageUnitServlet" width="40px" dropdown="true" type="string" multiSelect="true" tab="contactsTable,parentOrg">
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
										</query>
									</widget>
								</row>
							</panel>
				</panel>
					</deck> 
				</panel>
			</panel>
		</panel>
	</display>
	<rpc key="display">
  	<number key="id" type="integer" required="false"/>
  	<string key="categoryId" required="true"/>
  	<string key="description" max="60" required="true"/>
  	<check key="isSingular" required="false"/>
	</rpc>
	<rpc key="query">
 	<queryNumber key="id" type="integer" required="false"/>
 	<collection key="category" type="string" required="false"/>
  	<queryString key="description" required="true"/>
  	<collection key="isSingular" type="string" required="false"/>

	</rpc>
	<rpc key="queryByLetter">
		<queryString key="description"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>