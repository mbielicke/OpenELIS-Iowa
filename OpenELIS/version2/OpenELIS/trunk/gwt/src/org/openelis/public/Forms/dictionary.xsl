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
<screen id="Dictionary" serviceUrl="OpenElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<panel layout="horizontal" spacing="5" xsi:type="Panel">
			<!--left table goes here -->
			
				
				
					
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
      <table maxRows = "20" rows = "0" width= "auto" key = "categoryTable" title="{resource:getString($constants,'dic_Categories')}">
       <headers><xsl:value-of select='resource:getString($constants,"dic_SystemName")'/></headers>
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
  
  
				<!-- end left table -->
				<panel layout="vertical" spacing="5" width="500px" xsi:type="Panel">
					<widget halign="center">
						<buttonPanel buttons="qaucbpn" key="buttons"/>
					</widget>
					<panel layout="horizontal" spacing="5" width="200px" xsi:type="Panel">
						<panel layout="vertical" spacing="5" width="200px" xsi:type="Panel">
							<!-- first vertical panel for org fields -->
							<panel key="secMod" layout="table" style="Form" width="200px" xsi:type="Table">							
								<row>
									<widget halign="right">
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"dic_SystemName")'/></text>
									</widget>
									<widget>
										<textbox case="mixed" key="sysName"/>
									</widget>
								</row>								
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"name")'/></text>
									</widget>
									<widget>
										<textbox case="mixed" key="name"/>
									</widget>
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"dic_Description")'/></text>
									</widget>
									<widget>
										<textbox case="mixed" key="desc" width="300px"/>
									</widget>
								</row>
								<row>								
									 <widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"dic_SectionName")'/></text>
									 </widget>
									 
									 <!--<panel layout="horizontal" spacing="5" width="150px" xsi:type="Panel">-->
									  <widget>
									  	<!--<textbox case="mixed" key="secName"/>-->
									  	<option key="secName" multi= "false" required= "true" onChange= "this"/>
									  </widget>
								      <!--<widget>
								 			<html key="lookupParentOrganizationHtml" onclick="this">&lt;img src=&quot;Images/lookupButtonIcon.png&quot;&gt;</html>
									  </widget>	-->									  
                                <!-- </panel> -->								   						
								</row>							  
							</panel>
						</panel>
					</panel>
					<panel height="25px" layout="vertical"  xsi:type="Panel"/>
					<panel layout="horizontal" spacing="5" xsi:type="Panel">
						<!-- start TAB 1 data table -->
						<widget>
							<table maxRows = "7" width = "auto" key="dictEntTable" autoAdd= "true" rows="1" title="{resource:getString($constants,'dic_Entries')}">
								<headers><xsl:value-of select='resource:getString($constants,"dic_Active")'/>,<xsl:value-of select='resource:getString($constants,"dic_SystemName")'/>,
								         <xsl:value-of select='resource:getString($constants,"dic_Abbr")'/>, <xsl:value-of select='resource:getString($constants,"dic_Entry")'/>,
								         <xsl:value-of select='resource:getString($constants,"dic_RelEntry")'/></headers>
								<widths>45,95,75,110,120</widths>
								<editors>
									<check/>									
									<textbox/>									
									<textbox/>									
									<textbox/>	
									<option>
									  <item value= " "> </item>
									  <item value= "1">Entry1</item>
									  <item value= "2">Entry2</item>
                             		  <item value= "3">Entry3</item>
			                          <item value= "4">Entry4</item>
   		                            </option>								
									<!--<lookupWImg onclick = "LookupListener" />-->
								</editors>
								<fields>
									<check/>																		
									<string/>									
									<string/>
									<string/>
									<string/>									
								</fields>
								<sorts>false,true,true,true,false</sorts>
								<filters>false,false,false,false,false</filters>
								<colAligns>center,left,center,right,left</colAligns>
							</table>
						  <query>
						   	<table width = "auto" maxRows = "7" rows="1" title="">
								<headers><xsl:value-of select='resource:getString($constants,"dic_Active")'/>,<xsl:value-of select='resource:getString($constants,"dic_SystemName")'/>,
								         <xsl:value-of select='resource:getString($constants,"dic_Abbr")'/>, <xsl:value-of select='resource:getString($constants,"dic_Entry")'/>,
								         <xsl:value-of select='resource:getString($constants,"dic_RelEntry")'/></headers>
								<widths>45,95,75,110,100</widths>
								<editors>
									<option multi="true" fromModel="true"/>									
									<textbox/>									
									<textbox/>									
									<textbox/>	
									<option multi="true" fromModel="true"/>									  																
								</editors>
								<fields>
									<queryOption multi="true" type="string">	
									 <item value= " "> </item>
									 <item value= "Y">Y</item>														
									 <item value= "N">N</item>																	
									</queryOption>
									<queryString/>									
									<queryString/>
									<queryString/>
									<queryOption multi="true" type="string">
									  <item value= " "> </item>
									  <item value= "1">Entry1</item>
									  <item value= "2">Entry2</item>
                             		  <item value= "3">Entry3</item>
			                          <item value= "4">Entry4</item>	                            									
									</queryOption>
								</fields>
								<sorts>false,true,true,true,false</sorts>
								<filters>false,false,false,false,false</filters>
								<colAligns>center,left,center,right,left</colAligns>
							</table>
						  </query>
						</widget>
					</panel>					
				</panel>
		</panel>
	</display>
	<rpc key = "display">
	 <number key="categoryId" type="integer" required="false"/>	
	 <string key="sysName" max="30" required="true"/>
	 <string key="name" max="50" required="true"/>
	 <string key="desc" max="60" required="false"/>
	 <table key="dictEntTable"/>	
	 <option key="secName" multi= "false" required= "false">
	  <item value= ""/>
      <item value= "1">Section1</item>
      <item value= "2">Section2</item>
      </option>
	</rpc>
	<rpc key = "query">
	 <queryNumber key="categoryId" type="integer" />		
	 <table key="dictEntTable"/>	
	 <queryString key="sysName"/>
	 <queryString key="name"/>
	 <queryString key="desc"/>
	 <queryOption key="secName" multi= "true" type= "string">
	  <item value= ""/>
      <item value= "1">Section1</item>
      <item value= "2">Section2</item>
     </queryOption> 
	</rpc>
</screen>
</xsl:template>
</xsl:stylesheet> 