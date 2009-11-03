<!--
Exhibit A - UIRF Open-source Based Public Software License.

The contents of this file are subject to the UIRF Open-source Based
Public Software License(the "License"); you may not use this file except
in compliance with the License. You may obtain a copy of the License at
openelis.uhl.uiowa.edu

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.

The Original Code is OpenELIS code.

The Initial Developer of the Original Code is The University of Iowa.
Portions created by The University of Iowa are Copyright 2006-2008. All
Rights Reserved.

Contributor(s): ______________________________________.

Alternatively, the contents of this file marked
"Separately-Licensed" may be used under the terms of a UIRF Software
license ("UIRF Software License"), in which case the provisions of a
UIRF Software License are applicable instead of those above. 
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                xmlns:security="xalan://org.openelis.gwt.common.SecurityUtil"
                xmlns:service="xalan://org.openelis.gwt.server.ServiceUtils"
                xmlns:so="xalan://org.openelis.gwt.common.SecurityModule"
                xmlns:fn="http://www.w3.org/2005/xpath-functions" 
                extension-element-prefixes="resource"
                version="1.0">

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
  <xalan:component prefix="security">
    <xalan:script lang="javaclass" src="xalan://org.openelis.gwt.common.SecurityUtil"/>
  </xalan:component>
  
  <xalan:component prefix="service">
    <xalan:script lang="javaclass" src="xalan://org.openelis.gwt.server.ServiceUtils"/>
  </xalan:component>
  
  <xalan:component prefix="so">
    <xalan:script lang="javaclass" src="xalan://org.openelis.gwt.common.SecurityModule"/>
  </xalan:component>
  
       <xsl:variable name="language"><xsl:value-of select="doc/locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="doc/props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
  <xsl:template match="doc">
   <xsl:variable name="security" select="service:getSecurity()"/>
<screen id="main" serviceUrl="OpenELISService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display  constants="OpenELISConstants">
		<VerticalPanel style="AppBackground" sizeToWindow="true">
		<AbsolutePanel style="topMenuBar">
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
				      <xsl:call-template name="menuItem">
      				    <xsl:with-param name="key">FavoritesMenu</xsl:with-param>
				        <xsl:with-param name="label">favoritesMenu</xsl:with-param>
				        <xsl:with-param name="enabled">true</xsl:with-param>
				        <xsl:with-param name="class">FavoritesMenu</xsl:with-param>
       				    <xsl:with-param name="args"></xsl:with-param>
				      </xsl:call-template>
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
<!--					<xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">fullLogin</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
    				  <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template> -->
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">quickEntry</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
<!--				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">secondEntry</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
    				  <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template> -->
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">tracking</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
					<html>&lt;hr/&gt;</html>
					<!--
					enviromentalSampleLogin = Enviromental Sample Login
enviromentalSampleLoginDescription = Description...
clinicalSampleLogin = Clinical Sample Login
clinicalSampleLoginDescription = Description...
animalSampleLogin = Animal Sample Login
newbornScreeningSampleLogin = Newborn Screening Sample Login
newbornScreeningSampleDescription = Description...
sampleManagement = Sample Management
sampleManagementDescription = Description...
-->
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key">EnviromentalSampleLogin</xsl:with-param>
				      <xsl:with-param name="label">enviromentalSampleLogin</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="class">EnviromentalSampleLoginScreen</xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key">ClinicalSampleLogin</xsl:with-param>
				      <xsl:with-param name="label">clinicalSampleLogin</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="class">ClinicalSampleLoginScreen</xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key">AnimalSampleLogin</xsl:with-param>
				      <xsl:with-param name="label">animalSampleLogin</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="class">AnimalSampleLoginScreen</xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key">NewbornScreeningSampleLogin</xsl:with-param>
				      <xsl:with-param name="label">newbornScreeningSampleLogin</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="class">NewbornScreeningSampleLoginScreen</xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key">PTSampleLogin</xsl:with-param>
				      <xsl:with-param name="label">ptSampleLogin</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="class">PTSampleLoginScreen</xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key">SDWISSampleLogin</xsl:with-param>
				      <xsl:with-param name="label">sdwisSampleLogin</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="class">SDWISSampleLoginScreen</xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				   	<xsl:call-template name="menuItem">
				      <xsl:with-param name="key">PrivateWellWaterSampleLogin</xsl:with-param>
				      <xsl:with-param name="label">privateWellWaterSampleLogin</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="class">PrivateWellWaterSampleLoginScreen</xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <!--
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key">SampleManagement</xsl:with-param>
				      <xsl:with-param name="label">sampleManagement</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="class">SampleManagementScreen</xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
-->
					<html>&lt;hr/&gt;</html>

				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">project</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:if test="security:hasModule($security,'provider','SELECT')">				  
				      <xsl:call-template name="menuItem">
				        <xsl:with-param name="key">Provider</xsl:with-param>
				        <xsl:with-param name="label">provider</xsl:with-param>
				        <xsl:with-param name="enabled">true</xsl:with-param>
				        <xsl:with-param name="class">ProviderScreen</xsl:with-param>
      				    <xsl:with-param name="args"></xsl:with-param>
				      </xsl:call-template>
				    </xsl:if>
				    <xsl:if test="security:hasModule($security,'organization','SELECT')">
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
				      <xsl:with-param name="label">internalOrder</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="class">OrderScreen</xsl:with-param>
				      <xsl:with-param name="args">internal</xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key">ExternalOrder</xsl:with-param>
				      <xsl:with-param name="label">vendorOrder</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="class">OrderScreen</xsl:with-param>
				      <xsl:with-param name="args">external</xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key">KitOrder</xsl:with-param>
				      <xsl:with-param name="label">kitOrder</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="class">OrderScreen</xsl:with-param>
				      <xsl:with-param name="args">kits</xsl:with-param>
				    </xsl:call-template>	
				    <html>&lt;hr/&gt;</html>
   				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key">FillOrder</xsl:with-param>
				      <xsl:with-param name="label">fillOrder</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="class">FillOrderScreen</xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>	
  				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key">Shipping</xsl:with-param>
				      <xsl:with-param name="label">shipping</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="class">ShippingScreen</xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>	
				    <html>&lt;hr/&gt;</html>
	   				<xsl:call-template name="menuItem">
				      <xsl:with-param name="key">BuildKits</xsl:with-param>
				      <xsl:with-param name="label">buildKits</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="class">BuildKitsScreen</xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>	
				    <html>&lt;hr/&gt;</html>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key">InventoryReceipt</xsl:with-param>
				      <xsl:with-param name="label">inventoryReceipt</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="class">InventoryReceiptScreen</xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>	
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key">InventoryAdjustment</xsl:with-param>
				      <xsl:with-param name="label">inventoryAdjustment</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="class">InventoryAdjustmentScreen</xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>	
				    <xsl:if test="security:hasModule($security,'inventory','SELECT')">			
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key">InventoryItem</xsl:with-param>
				      <xsl:with-param name="label">inventoryItem</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="class">InventoryItemScreen</xsl:with-param>
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
				      <xsl:with-param name="key">Test</xsl:with-param>
				      <xsl:with-param name="label">test</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="class">TestScreen</xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>				      
				    </xsl:call-template>				
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key">Method</xsl:with-param>
				      <xsl:with-param name="label">method</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="class">MethodScreen</xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key">Panel</xsl:with-param>
				      <xsl:with-param name="label">panel</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="class">PanelScreen</xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:if test="security:hasModule($security,'qaevent','SELECT')">
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
				   <html>&lt;hr/&gt;</html>
				    <xsl:if test="security:hasModule($security,'analyte','SELECT')">
					  <xsl:call-template name="menuItem">
				        <xsl:with-param name="key">Analyte</xsl:with-param>
				        <xsl:with-param name="label">analyte</xsl:with-param>
				        <xsl:with-param name="enabled">true</xsl:with-param>
				        <xsl:with-param name="class">AnalyteScreen</xsl:with-param>
				        <xsl:with-param name="args"></xsl:with-param>
				      </xsl:call-template>
				    </xsl:if>
				    <xsl:if test="security:hasModule($security,'dictionary','SELECT')">
				      <xsl:call-template name="menuItem">
				        <xsl:with-param name="key">Dictionary</xsl:with-param>
				        <xsl:with-param name="label">dictionary</xsl:with-param>
				        <xsl:with-param name="enabled">true</xsl:with-param>
				        <xsl:with-param name="class">DictionaryScreen</xsl:with-param>
      				    <xsl:with-param name="args"></xsl:with-param>
				      </xsl:call-template>
				    </xsl:if>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key">AuxiliaryPrompt</xsl:with-param>
				      <xsl:with-param name="label">auxiliaryPrompt</xsl:with-param>
				      <xsl:with-param name="enabled">true</xsl:with-param>
				      <xsl:with-param name="class">AuxiliaryScreen</xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
					<html>&lt;hr/&gt;</html>
			        <xsl:if test="security:hasModule($security,'label','SELECT')">
				      <xsl:call-template name="menuItem">
				        <xsl:with-param name="key">Label</xsl:with-param>
				        <xsl:with-param name="label">label</xsl:with-param>
				        <xsl:with-param name="enabled">true</xsl:with-param>
				        <xsl:with-param name="class">LabelScreen</xsl:with-param>
				        <xsl:with-param name="args"></xsl:with-param>
				      </xsl:call-template>
				    </xsl:if>
				    <xsl:if test="security:hasModule($security,'standardnote','SELECT')">			    
				      <xsl:call-template name="menuItem">
				        <xsl:with-param name="key">StandardNote</xsl:with-param>
				        <xsl:with-param name="label">standardNote</xsl:with-param>
				        <xsl:with-param name="enabled">true</xsl:with-param>
				        <xsl:with-param name="class">StandardNoteScreen</xsl:with-param>
				        <xsl:with-param name="args"></xsl:with-param>
				      </xsl:call-template>
				    </xsl:if>
				    <xsl:if test="security:hasModule($security,'testtrailer','SELECT')">
				      <xsl:call-template name="menuItem">
				        <xsl:with-param name="key"></xsl:with-param>
				        <xsl:with-param name="label">trailerForTest</xsl:with-param>
				        <xsl:with-param name="enabled">true</xsl:with-param>
				        <xsl:with-param name="class">TestTrailerScreen</xsl:with-param>
				        <xsl:with-param name="args"></xsl:with-param>
				      </xsl:call-template>
				    </xsl:if>
					<html>&lt;hr/&gt;</html>
			        <xsl:if test="security:hasModule($security,'storageunit','SELECT')">
			    	  <xsl:call-template name="menuItem">
				        <xsl:with-param name="key">StorageUnit</xsl:with-param>
				        <xsl:with-param name="label">storageUnit</xsl:with-param>
				        <xsl:with-param name="enabled">true</xsl:with-param>
				        <xsl:with-param name="class">StorageUnitScreen</xsl:with-param>
				        <xsl:with-param name="args"></xsl:with-param>
				      </xsl:call-template>
				    </xsl:if>
				    <xsl:if test="security:hasModule($security,'storagelocation','SELECT')">
				      <xsl:call-template name="menuItem">
				        <xsl:with-param name="key">StorageLocation</xsl:with-param>
				        <xsl:with-param name="label">storageLocation</xsl:with-param>
				        <xsl:with-param name="enabled">true</xsl:with-param>
				        <xsl:with-param name="class">StorageLocationScreen</xsl:with-param>
				        <xsl:with-param name="args"></xsl:with-param>
				      </xsl:call-template>
				    </xsl:if>
					<html>&lt;hr/&gt;</html>
				    <xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">instrument</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
					<html>&lt;hr/&gt;</html>
					<xsl:call-template name="menuItem">
				      <xsl:with-param name="key"></xsl:with-param>
				      <xsl:with-param name="label">scriptlet</xsl:with-param>
				      <xsl:with-param name="enabled">false</xsl:with-param>
				      <xsl:with-param name="class"></xsl:with-param>
				      <xsl:with-param name="args"></xsl:with-param>
				    </xsl:call-template>
				    <xsl:if test="security:hasModule($security,'systemvariable','SELECT')">
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
						<html>&lt;hr/&gt;</html>
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
       </AbsolutePanel>
       <HorizontalPanel>
          <VerticalPanel key="favoritesPanel" visible="false" width="220px">
            <HorizontalPanel style="FavHeader" width="100%" height="20px">
                <text style="ScreenWindowLabel">Favorites</text>
              <widget halign="right">
                <appButton action="editFavorites" key="EditFavorites" onclick="this">
	              <AbsolutePanel style="EditSettings"/>
	            </appButton>
	          </widget>
            </HorizontalPanel>
	      </VerticalPanel>
    	<winbrowser key="browser" sizeToWindow="true"/>
		</HorizontalPanel>
	</VerticalPanel>
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
