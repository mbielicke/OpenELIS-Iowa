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
  
      <xsl:variable name="language"><xsl:value-of select="doc/locale"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle('org.openelis.modules.main.server.constants.OpenELISConstants',locale:new(string($language)))"/>

  <xsl:template match="doc">

<screen id="main" serviceUrl="OpenELISService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display  constants="OpenELISConstants">
		<panel layout="vertical" style="AppBackground" sizeToWindow="true" xsi:type="Panel">
		<panel layout="absolute" style="topMenuBar">
		<menuPanel layout="horizontal" xsi:type="Panel" style="topBarItemHolder" spacing="0" padding="0">
		    <menuItem>
		        <menuDisplay>
			    	  <label style="topMenuBarItem" text="{resource:getString($constants,'application')}" hover="Hover"/>
				</menuDisplay>
				  <menuPanel style="topMenuContainer" layout="vertical" xsi:type="Panel" position="below">
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">preference</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">favoritesMenu</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">logout</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
				  </menuPanel>
		    </menuItem>
		    <menuItem>
		      <menuDisplay>					
					<label style="topMenuBarItem" text="{resource:getString($constants,'edit')}" hover="Hover"/>
			  </menuDisplay>
	            <menuPanel style="topMenuContainer" layout="vertical" position="below">
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">cut</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>	            
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">copy</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">paste</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
			    </menuPanel>
			</menuItem>
    	    <menuItem>
    	      <menuDisplay>
					<label style="topMenuBarItem" text="{resource:getString($constants,'sample')}" hover="Hover" />
		      </menuDisplay>
				<menuPanel layout="vertical" style="topMenuContainer" position="below">
					<xsl:call-template name="menuItem">
				      <xsl:with-param name="label">fullLogin</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">quickEntry</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">secondEntry</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">tracking</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
				  <widget>
					<html>&lt;hr/&gt;</html>
				  </widget>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">project</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>				  
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">provider</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="value">ProviderScreen</xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">organization</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="value">OrganizationScreen</xsl:with-param>
				    </xsl:call-template>
				</menuPanel>
		    </menuItem>
	        <menuItem>
	          <menuDisplay>
					<label style="topMenuBarItem" text="{resource:getString($constants,'analysis')}" hover="Hover"/>
		      </menuDisplay>
				<menuPanel layout="vertical" style="topMenuContainer" position="below">
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">worksheetCreation</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>				
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">worksheetCompletion</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">addOrCancel</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">reviewAndRelease</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">toDo</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">labelFor</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">storage</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">QC</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
				</menuPanel>
			</menuItem>
	        <menuItem>  
	          <menuDisplay>
					<label style="topMenuBarItem" text="{resource:getString($constants,'inventoryOrder')}" hover="Hover" />
			  </menuDisplay>
				<menuPanel layout="vertical" style="topMenuContainer" position="below">
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">order</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>				
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">inventory</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
				</menuPanel>
		    </menuItem>
	        <menuItem>
	          <menuDisplay>
					<label style="topMenuBarItem" text="{resource:getString($constants,'instrument')}" hover="Hover"/>
			  </menuDisplay>
				<menuPanel layout="vertical" style="topMenuContainer" position="below">
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">instrument</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>				
				</menuPanel>
		    </menuItem>
	        <menuItem>
	          <menuDisplay>
					<label style="topMenuBarItem" text="{resource:getString($constants,'maintenance')}" hover="Hover" />
			  </menuDisplay>
				<menuPanel layout="vertical" style="topMenuContainer" position="below">
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">test</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>				
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">method</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">panel</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">QAEvent</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="value">QAEventScreen</xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">labSection</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
			    <widget>
				   <html>&lt;hr/&gt;</html>
				</widget>
					<xsl:call-template name="menuItem">
				      <xsl:with-param name="label">analyte</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="value">AnalyteScreen</xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">dictionary</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="value">DictionaryScreen</xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">auxiliaryPrompt</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
			    <widget>
					<html>&lt;hr/&gt;</html>
			    </widget>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">barcodeLabel</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>			    
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">standardNote</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="value">StandardNoteScreen</xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">trailerForTest</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="value">TestTrailerScreen</xsl:with-param>
				    </xsl:call-template>
			    <widget>
					<html>&lt;hr/&gt;</html>
			    </widget>
			    	<xsl:call-template name="menuItem">
				      <xsl:with-param name="label">storageUnit</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="value">StorageUnitScreen</xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">storageLocation</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="value">StorageLocationScreen</xsl:with-param>
				    </xsl:call-template>
				<widget>
					<html>&lt;hr/&gt;</html>
				</widget>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">instrument</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
				<widget>
					<html>&lt;hr/&gt;</html>
				</widget>
					<xsl:call-template name="menuItem">
				      <xsl:with-param name="label">scriptlet</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">systemVariable</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="value">SystemVariableScreen</xsl:with-param>
				    </xsl:call-template>
				</menuPanel>
		    </menuItem>
	        <menuItem>
	          <menuDisplay>
					<label style="topMenuBarItem" text="{resource:getString($constants,'report')}" hover="Hover"/>
			  </menuDisplay>
				<menuPanel layout="vertical" style="topMenuContainer" position="below">
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">finalReport</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>				
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">sampleDataExport</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">loginLabel</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
					<widget>
						<html>&lt;hr/&gt;</html>
					</widget>
					<menuItem style="TopMenuRowContainer" 
						      hover="Hover"
						      icon="referenceIcon"
						      label="{resource:getString($constants,'reference')}"
						      description="">					
						  <menuPanel layout="vertical" style="topMenuContainer" position="side">
				          <xsl:call-template name="menuItem">
				            <xsl:with-param name="label">organization</xsl:with-param>
				            <xsl:with-param name="enabled">false</xsl:with-param>
				            <xsl:with-param name="value"></xsl:with-param>
				          </xsl:call-template>						  
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">test</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">QAEvent</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
						  </menuPanel>
					</menuItem>
					<menuItem style="TopMenuRowContainer" 
						      hover="Hover"
						      icon="summaryIcon"
						      label="{resource:getString($constants,'summary')}"
						      description="">
							<menuPanel layout="vertical" style="topMenuContainer" position="side">
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">QAByOrganization</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">testCountByFacility</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">turnaround</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="label">positiveTestCount</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="value"></xsl:with-param>
				    </xsl:call-template>
							</menuPanel>
					 </menuItem>
				</menuPanel>
		    </menuItem>
        </menuPanel>
       </panel>
       <panel layout="horizontal" xsi:type="Panel">
         	<menuPanel layout="vertical" style="topMenuContainer" key="favoritesMenu" width="220px">
	         <menuItem style="TopMenuRowContainer" enabled="true"
	                   hover="Hover"
	                   icon="favoritesIcon"
	                   label="Edit Favorites"
	                   value="FavoritesScreen"
	                   description=""
	                   onClick="this"/>
	  		<xsl:for-each select="favorite">
			    <xsl:variable name="label"><xsl:value-of select="@label"/></xsl:variable>
			    <xsl:variable name="value"><xsl:value-of select="@value"/></xsl:variable>
		        <menuItem style="TopMenuRowContainer" enabled="true"  
			              hover="Hover"
				          icon="{$label}Icon"
		        		  label="{resource:getString($constants,$label)}"
				          description=""
				          value="{$value}"
				          onClick="this"/>
			    </xsl:for-each>
			</menuPanel>
		    	<widget>
			    	<winbrowser key="browser" sizeToWindow="true"/>
		    	</widget>
		</panel>
	</panel>
	</display>
	<rpc key="display"/>
	<rpc key="query"/>
</screen>
  </xsl:template>
  
  
  <xsl:template name="menuItem">
    <xsl:param name="label"/>
    <xsl:param name="value"/>
    <xsl:param name="enabled"/>
    <xsl:variable name="descrip"><xsl:value-of select="$label"/>Description</xsl:variable>
  	<menuItem style="TopMenuRowContainer" enabled="{$enabled}"  
	          hover="Hover"
	          icon="{$label}Icon"
	   		  label="{resource:getString($constants,$label)}"
	          description="{resource:getString($constants,$descrip)}" 
	          value="{$value}"
	          onClick="this"/>
  </xsl:template>
</xsl:stylesheet>