<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                xmlns:fn="http://www.w3.org/2005/xpath-functions" 
                extension-element-prefixes="resource"
                version="1.0">

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
       <xsl:variable name="language"><xsl:value-of select="doc/locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="doc/props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
  <xsl:template match="doc">
   <xsl:variable name="modules"><xsl:value-of select="modules"/></xsl:variable>
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
				    <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">preference</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
   				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:if test="contains($modules,'Favorites')">
				      <xsl:call-template name="menuItem">
      				    <xsl:with-param name="key">FavoritesMenu</xsl:with-param>
				        <xsl:with-param name="label">favoritesMenu</xsl:with-param>
				        <xsl:with-param name="enabled">true</xsl:with-param>
				        <xsl:with-param name="class">FavoritesMenu</xsl:with-param>
       				    <xsl:with-param name="args"></xsl:with-param>
				      </xsl:call-template>
				    </xsl:if>
				    <xsl:call-template name="menuItem">
    				  <xsl:with-param name="key">Logout</xsl:with-param>
				      <xsl:with-param name="label">logout</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="class">Logout</xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				  </menuPanel>
		    </menuItem>
		    <menuItem>
		      <menuDisplay>					
					<label style="topMenuBarItem" text="{resource:getString($constants,'edit')}" hover="Hover"/>
			  </menuDisplay>
	            <menuPanel style="topMenuContainer" layout="vertical" position="below">
				    <xsl:call-template name="menuItem">
    				  <xsl:with-param name="key">Cut</xsl:with-param>
				      <xsl:with-param name="label">cut</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
   				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>	            
				    <xsl:call-template name="menuItem">
    				  <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">copy</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
     				  <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">paste</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
			    </menuPanel>
			</menuItem>
    	    <menuItem>
    	      <menuDisplay>
					<label style="topMenuBarItem" text="{resource:getString($constants,'sample')}" hover="Hover" />
		      </menuDisplay>
				<menuPanel layout="vertical" style="topMenuContainer" position="below">
					<xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">fullLogin</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
    				  <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">quickEntry</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">secondEntry</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
    				  <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">tracking</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				  <widget>
					<html>&lt;hr/&gt;</html>
				  </widget>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">project</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:if test="contains($modules,'Provider')">				  
				      <xsl:call-template name="menuItem">
				        <xsl:with-param name="key">Provider</xsl:with-param>
				        <xsl:with-param name="label">provider</xsl:with-param>
				        <xsl:with-param name="enabled">true</xsl:with-param>
				        <xsl:with-param name="class">ProviderScreen</xsl:with-param>
      				    <xsl:with-param name="args"></xsl:with-param>
				      </xsl:call-template>
				    </xsl:if>
				    <xsl:if test="contains($modules,'Organization')">
				      <xsl:call-template name="menuItem">
				        <xsl:with-param name="key">Organization</xsl:with-param>
				        <xsl:with-param name="label">organization</xsl:with-param>
				        <xsl:with-param name="enabled">true</xsl:with-param>
				        <xsl:with-param name="class">OrganizationScreen</xsl:with-param>
				        <xsl:with-param name="args"></xsl:with-param>
				      </xsl:call-template>
				    </xsl:if>
				</menuPanel>
		    </menuItem>
	        <menuItem>
	          <menuDisplay>
					<label style="topMenuBarItem" text="{resource:getString($constants,'analysis')}" hover="Hover"/>
		      </menuDisplay>
				<menuPanel layout="vertical" style="topMenuContainer" position="below">
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">worksheetCreation</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>				
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">worksheetCompletion</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
    				  <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">addOrCancel</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">reviewAndRelease</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">toDo</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">labelFor</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">storage</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">QC</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				</menuPanel>
			</menuItem>
	        <menuItem>  
	          <menuDisplay>
					<label style="topMenuBarItem" text="{resource:getString($constants,'inventoryOrder')}" hover="Hover" />
			  </menuDisplay>
				<menuPanel layout="vertical" style="topMenuContainer" position="below">
				    <xsl:call-template name="menuItem">
	 				  <xsl:with-param name="key">InternalOrder</xsl:with-param>
				      <xsl:with-param name="label">order</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="class">OrderScreen</xsl:with-param>
				      <xsl:with-param name="args">internal</xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key">ExternalOrder</xsl:with-param>
				      <xsl:with-param name="label">order</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="class">OrderScreen</xsl:with-param>
				      <xsl:with-param name="args">external</xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key">KitOrder</xsl:with-param>
				      <xsl:with-param name="label">order</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="class">OrderScreen</xsl:with-param>
				      <xsl:with-param name="args">kits</xsl:with-param>
				    </xsl:call-template>	
				    <xsl:if test="contains($modules,'Inventory')">			
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key">Inventory</xsl:with-param>
				      <xsl:with-param name="label">inventory</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="class">InventoryScreen</xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    </xsl:if>
				</menuPanel>
		    </menuItem>
	        <menuItem>
	          <menuDisplay>
					<label style="topMenuBarItem" text="{resource:getString($constants,'instrument')}" hover="Hover"/>
			  </menuDisplay>
				<menuPanel layout="vertical" style="topMenuContainer" position="below">
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">instrument</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>				
				</menuPanel>
		    </menuItem>
	        <menuItem>
	          <menuDisplay>
					<label style="topMenuBarItem" text="{resource:getString($constants,'maintenance')}" hover="Hover" />
			  </menuDisplay>
				<menuPanel layout="vertical" style="topMenuContainer" position="below">
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">test</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>				      
				    </xsl:call-template>				
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">method</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">panel</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:if test="contains($modules,'QAEvent')">
				      <xsl:call-template name="menuItem">
				        <xsl:with-param name="key">QAEvent</xsl:with-param>
				        <xsl:with-param name="label">QAEvent</xsl:with-param>
				        <xsl:with-param name="enabled">true</xsl:with-param>
				        <xsl:with-param name="class">QAEventScreen</xsl:with-param>
				        <xsl:with-param name="args"></xsl:with-param>
				      </xsl:call-template>
				    </xsl:if>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">labSection</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
			    <widget>
				   <html>&lt;hr/&gt;</html>
				</widget>
				    <xsl:if test="contains($modules,'Analyte')">
					  <xsl:call-template name="menuItem">
				        <xsl:with-param name="key">Analyte</xsl:with-param>
				        <xsl:with-param name="label">analyte</xsl:with-param>
				        <xsl:with-param name="enabled">true</xsl:with-param>
				        <xsl:with-param name="class">AnalyteScreen</xsl:with-param>
				        <xsl:with-param name="args"></xsl:with-param>
				      </xsl:call-template>
				    </xsl:if>
				    <xsl:if test="contains($modules,'Dictionary')">
				      <xsl:call-template name="menuItem">
				        <xsl:with-param name="key">Dictionary</xsl:with-param>
				        <xsl:with-param name="label">dictionary</xsl:with-param>
				        <xsl:with-param name="enabled">true</xsl:with-param>
				        <xsl:with-param name="class">DictionaryScreen</xsl:with-param>
      				    <xsl:with-param name="args"></xsl:with-param>
				      </xsl:call-template>
				    </xsl:if>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">auxiliaryPrompt</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
			    <widget>
					<html>&lt;hr/&gt;</html>
			    </widget>
			        <xsl:if test="contains($modules,'Label')">
				      <xsl:call-template name="menuItem">
				        <xsl:with-param name="key">Label</xsl:with-param>
				        <xsl:with-param name="label">label</xsl:with-param>
				        <xsl:with-param name="enabled">true</xsl:with-param>
				        <xsl:with-param name="class">LabelScreen</xsl:with-param>
				        <xsl:with-param name="args"></xsl:with-param>
				      </xsl:call-template>
				    </xsl:if>
				    <xsl:if test="contains($modules,'StandardNote')">			    
				      <xsl:call-template name="menuItem">
				        <xsl:with-param name="key">StandardNote</xsl:with-param>
				        <xsl:with-param name="label">standardNote</xsl:with-param>
				        <xsl:with-param name="enabled">true</xsl:with-param>
				        <xsl:with-param name="class">StandardNoteScreen</xsl:with-param>
				        <xsl:with-param name="args"></xsl:with-param>
				      </xsl:call-template>
				    </xsl:if>
				    <xsl:if test="contains($modules,'TestTrailer')">
				      <xsl:call-template name="menuItem">
				        <xsl:with-param name="key"></xsl:with-param>
				        <xsl:with-param name="label">trailerForTest</xsl:with-param>
				        <xsl:with-param name="enabled">true</xsl:with-param>
				        <xsl:with-param name="class">TestTrailerScreen</xsl:with-param>
				        <xsl:with-param name="args"></xsl:with-param>
				      </xsl:call-template>
				    </xsl:if>
			    <widget>
					<html>&lt;hr/&gt;</html>
			    </widget>
			        <xsl:if test="contains($modules,'StorageUnit')">
			    	  <xsl:call-template name="menuItem">
				        <xsl:with-param name="key">StorageUnit</xsl:with-param>
				        <xsl:with-param name="label">storageUnit</xsl:with-param>
				        <xsl:with-param name="enabled">true</xsl:with-param>
				        <xsl:with-param name="class">StorageUnitScreen</xsl:with-param>
				        <xsl:with-param name="args"></xsl:with-param>
				      </xsl:call-template>
				    </xsl:if>
				    <xsl:if test="contains($modules,'Storage')">
				      <xsl:call-template name="menuItem">
				        <xsl:with-param name="key">StorageLocation</xsl:with-param>
				        <xsl:with-param name="label">storageLocation</xsl:with-param>
				        <xsl:with-param name="enabled">true</xsl:with-param>
				        <xsl:with-param name="class">StorageLocationScreen</xsl:with-param>
				        <xsl:with-param name="args"></xsl:with-param>
				      </xsl:call-template>
				    </xsl:if>
				<widget>
					<html>&lt;hr/&gt;</html>
				</widget>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">instrument</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				<widget>
					<html>&lt;hr/&gt;</html>
				</widget>
					<xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">scriptlet</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:if test="contains($modules,'SystemVariable')">
				      <xsl:call-template name="menuItem">
				        <xsl:with-param name="key">SystemVariable</xsl:with-param>
				        <xsl:with-param name="label">systemVariable</xsl:with-param>
				        <xsl:with-param name="enabled">true</xsl:with-param>
				        <xsl:with-param name="class">SystemVariableScreen</xsl:with-param>
				        <xsl:with-param name="args"></xsl:with-param>
				      </xsl:call-template>
				    </xsl:if>
				</menuPanel>
		    </menuItem>
	        <menuItem>
	          <menuDisplay>
					<label style="topMenuBarItem" text="{resource:getString($constants,'report')}" hover="Hover"/>
			  </menuDisplay>
				<menuPanel layout="vertical" style="topMenuContainer" position="below">
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">finalReport</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>				
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">sampleDataExport</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">loginLabel</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
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
				            <xsl:with-param name="key"></xsl:with-param>
				            <xsl:with-param name="label">organization</xsl:with-param>
				            <xsl:with-param name="enabled">false</xsl:with-param>
				            <xsl:with-param name="class"></xsl:with-param>
				            <xsl:with-param name="args"></xsl:with-param>
				          </xsl:call-template>						  
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">test</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">QAEvent</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
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
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">QAByOrganization</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">testCountByFacility</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">turnaround</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">positiveTestCount</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
							</menuPanel>
					 </menuItem>
				</menuPanel>
		    </menuItem>
        </menuPanel>
       </panel>
       <panel layout="horizontal" xsi:type="Panel">
          <panel layout="vertical" xsi:type="Panel" key="favoritesPanel" visible="false" width="220px">
            <panel layout="horizontal" xsi:type="Panel" style="Header" width="100%" height="20px">
              <widget>
                <text style="ScreenWindowLabel">Favorites</text>
              </widget>
              <widget halign="right">
                <appButton action="editFavorites" key="EditFavorites" onclick="this">
	              <panel layout="absolute" xsi:type="Absolute" style="EditSettings"/>
	            </appButton>
	          </widget>
            </panel>
	      </panel>
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
  	<xsl:param name="key"/>
    <xsl:param name="label"/>
    <xsl:param name="class"/>
    <xsl:param name="args"/>
    <xsl:param name="enabled"/>
    <xsl:variable name="descrip"><xsl:value-of select="$label"/>Description</xsl:variable>
  	<menuItem key="{$key}" style="TopMenuRowContainer" enabled="{$enabled}"  
	          hover="Hover"
	          icon="{$label}Icon"
	   		  label="{resource:getString($constants,$label)}"
	          description="{resource:getString($constants,$descrip)}" 
	          class="{$class}"
			  args="{$args}"
	          onClick="this"/>
  </xsl:template>
</xsl:stylesheet>