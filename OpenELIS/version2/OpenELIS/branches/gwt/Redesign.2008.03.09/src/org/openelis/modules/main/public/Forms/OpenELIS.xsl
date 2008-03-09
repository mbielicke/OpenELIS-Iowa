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
    <xsl:variable name="constants" select="resource:getBundle('org.openelis.modules.main.server.constants.OpenELISConstants',locale:new(string($language)))"/>
<screen id="main" serviceUrl="OpenELISService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display  constants="OpenELISConstants">
		<panel height="100%" layout="vertical" style="AppBackground" width="100%" xsi:type="Panel">
		<menuPanel key="topMenu" layout="horizontal" style="topMenuBar" xsi:type="Panel">
		    <menuItem>
		        <menuDisplay>
		      	  <widget>
			    	  <label key="application" style="topMenuBarItem" text="{resource:getString($constants,'application')}" onClick="this" hover="Hover"/>
				   </widget>
				</menuDisplay>
				<menuPopupPanel key="applicationPanel" autoHide="true" hidden="true" popup="this">
				  <menuPanel key="applicationPanelTable" style="topMenuContainer" layout="vertical" xsi:type="Panel" spacing="0" padding="0">
				    <menuItem key="preference" style="TopMenuRowContainer" hover="Hover" 
				              onPanelClick="this" 
				              icon="preferenceIcon" 
				              label="{resource:getString($constants,'preference')}" 
				              description="{resource:getString($constants,'preferenceDescription')}"/>
					<menuItem key="favorites" style="TopMenuRowContainer" hover="Hover" 
					          onPanelClick="this" 
					          icon="favoritesMenuIcon"
					          label="{resource:getString($constants,'favoritesMenu')}"
					          description="{resource:getString($constants,'favoritesMenuDescription')}"/>
					<menuItem key="logoutRow" style="TopMenuRowContainer" hover="Hover" 
					          onPanelClick="this"
					          icon="logoutIcon"
					          label="{resource:getString($constants,'logout')}"
					          description="{resource:getString($constants,'logoutDescription')}"/>
				  </menuPanel>
			   </menuPopupPanel>
		    </menuItem>
		    <widget>
				<html style="topMenuBarSpacer" xml:space="preserve"> </html>
		    </widget>
		    <menuItem>
		      <menuDisplay>
   				<widget>
					<label key="edit" style="topMenuBarItem" text="{resource:getString($constants,'edit')}" onClick="this" hover="Hover"/>
				</widget>
			  </menuDisplay>
			  <menuPopupPanel key="editPanel" autoHide="true" hidden="true" popup="this">
	            <menuPanel key="applicationPanelTable" style="topMenuContainer" layout="vertical" xsi:type="Panel" spacing="0" padding="0">
				  <menuItem key="cutRow" style="TopMenuRowContainer" hover="Hover" 
				            onPanelClick="this"
				            icon="cutIcon"
				            label="{resource:getString($constants,'cut')}"
				            description=""/>
				  <menuItem key="copyRow" style="TopMenuRowContainer" hover="Hover" 
				            onPanelClick="this"
				            icon="copyIcon"
				            label="{resource:getString($constants,'copy')}"
				            description=""/>
	              <menuItem key="pasteRow" style="TopMenuRowContainer" hover="Hover" 
	                        onPanelClick="this"
	                        icon="pasteIcon"
	                        label="{resource:getString($constants,'paste')}"
	                        description=""/>
			    </menuPanel>
			  </menuPopupPanel>
			</menuItem>
			<widget>
	            <html style="topMenuBarSpacer" xml:space="preserve"> </html>
    	    </widget>
    	    <menuItem>
    	      <menuDisplay>
				<widget>
					<label key="sample" style="topMenuBarItem" text="{resource:getString($constants,'sample')}" onClick="this" hover="Hover"/>
				</widget>
		      </menuDisplay>
			  <menuPopupPanel key="samplePanel" autoHide="true" hidden="true" popup="this">
				<menuPanel key="samplePanelTable" layout="vertical" style="topMenuContainer" xsi:type="Panel" padding="0" spacing="0">
				  <menuItem key="fullLoginRow" style="TopMenuRowContainer" hover="Hover" 
				            onPanelClick="this" 
				            icon="fullLoginIcon"
				            label="{resource:getString($constants,'fullLogin')}"
				            description="{resource:getString($constants,'fullLoginDescription')}"/>
                  <menuItem key="quickEntryRow" style="TopMenuRowContainer" hover="Hover"
                            onPanelClick="this"
                            icon="quickEntryIcon"
                            label="{resource:getString($constants,'quickEntry')}"
                            description="{resource:getString($constants,'quickEntryDescription')}"/>
				  <menuItem key="secondEntryRow" style="TopMenuRowContainer" hover="Hover"
				            onPanelClick="this"
				            icon="secondEntryIcon"
				            label="{resource:getString($constants,'secondEntry')}"
				            description="{resource:getString($constants,'secondEntryDescription')}"/>
				  <menuItem key="trackingRow" style="TopMenuRowContainer" onPanelClick="this" 
				            hover="Hover"
				            icon="trackingIcon"
				            label="{resource:getString($constants,'tracking')}"
				            description="{resource:getString($constants,'trackingDescription')}"/>
				  <widget>
					<html key="spacer">&lt;hr/&gt;</html>
				  </widget>
				  <menuItem key="projectRow" style="TopMenuRowContainer" onPanelClick="this" 
				            hover="Hover"
				            icon="projectIcon"
				            label="{resource:getString($constants,'project')}"
				            description="{resource:getString($constants,'projectDescription')}"/>
                  <menuItem key="providerRow" style="TopMenuRowContainer" onPanelClick="this" 
                            hover="Hover"
                            icon="providerIcon"
                            label="{resource:getString($constants,'provider')}"
                            description="{resource:getString($constants,'providerDescription')}"/>
                  <menuItem key="organizationRow" style="TopMenuRowContainer" onPanelClick="this" 
                            hover="Hover"
                            icon="organizationIcon"
                            label="{resource:getString($constants,'organization')}"
                            description="{resource:getString($constants,'organizationDescription')}"/>
				</menuPanel>
			  </menuPopupPanel>
		    </menuItem>
			<widget>
            	<html style="topMenuBarSpacer" xml:space="preserve"> </html>
	        </widget>
	        <menuItem>
	          <menuDisplay>
				<widget>
					<label key="analysis" style="topMenuBarItem" text="{resource:getString($constants,'analysis')}" onClick="this" hover="Hover"/>
				</widget>
		      </menuDisplay>
			  <menuPopupPanel key="analysisPanel" autoHide="true" hidden="true" popup="this">
				<menuPanel key="analysisPanelTable" layout="vertical" style="topMenuContainer" xsi:type="Panel" padding="0" spacing="0">
				  <menuItem key="worksheetCreationRow" style="TopMenuRowContainer" onPanelClick="this" 
				            hover="Hover"
				            icon="worksheetCreationIcon"
				            label="{resource:getString($constants,'worksheetCreation')}"
				            description="{resource:getString($constants,'worksheetCreationDescription')}"/>
                  <menuItem key="worksheetCompletionRow" style="TopMenuRowContainer" onPanelClick="this" 
                            hover="Hover"
                            icon="worksheetCompletionIcon"
                            label="{resource:getString($constants,'worksheetCompletion')}"
                            description="{resource:getString($constants,'worksheetCompletionDescription')}"/>
                  <menuItem key="addOrCancelRow" style="TopMenuRowContainer" onPanelClick="this" 
                            hover="Hover"
                            icon="addOrCancelIcon"
                            label="{resource:getString($constants,'addOrCancel')}"
                            description="{resource:getString($constants,'addOrCancelDescription')}"/>
                  <menuItem key="reviewAndReleaseRow" style="TopMenuRowContainer" onPanelClick="this" 
                            hover="Hover"
                            icon="reviewAndReleaseIcon"
                            label="{resource:getString($constants,'reviewAndRelease')}"
                            description="{resource:getString($constants,'reviewAndReleaseDescription')}"/>
                  <menuItem key="toDoRow" style="TopMenuRowContainer" onPanelClick="this" 
                            hover="Hover"
                            icon="toDoIcon"
                            label="{resource:getString($constants,'toDo')}"
                            description="{resource:getString($constants,'toDoDescription')}"/>
				  <menuItem key="labelForRow" style="TopMenuRowContainer" onPanelClick="this" 
				            hover="Hover"
				            icon="labelForIcon"
				            label="{resource:getString($constants,'labelFor')}"
				            description="{resource:getString($constants,'labelForDescription')}"/>
				  <menuItem key="storageRow" style="TopMenuRowContainer" onPanelClick="this" 
				            hover="Hover"
				            icon="storageIcon"
				            label="{resource:getString($constants,'storage')}"
				            description="{resource:getString($constants,'storageDescription')}"/>
				  <menuItem key="QCRow" style="TopMenuRowContainer" onPanelClick="this" 
				            hover="Hover"
				            icon="QCIcon"
				            label="{resource:getString($constants,'QC')}"
				            description="{resource:getString($constants,'QCDescription')}"/>
				</menuPanel>
              </menuPopupPanel>
			</menuItem>
			<widget>
            	<html style="topMenuBarSpacer" xml:space="preserve"> </html>
	        </widget>
	        <menuItem>  
	          <menuDisplay>
				<widget>
					<label key="inventoryOrder" style="topMenuBarItem" text="{resource:getString($constants,'inventoryOrder')}" onClick="this" hover="Hover"/>
				</widget>
			  </menuDisplay>
			  <menuPopupPanel key="inventoryOrderPanel" autoHide="true" hidden="true" popup="this">
				<menuPanel key="inventoryOrderPanelTable" layout="vertical" style="topMenuContainer" xsi:type="Panel" padding="0" spacing="0">
				<menuItem key="orderRow" style="TopMenuRowContainer" onPanelClick="this" 
				          hover="Hover"
				          icon="orderIcon"
				          label="{resource:getString($constants,'order')}"
				          description="{resource:getString($constants,'orderDescription')}"/>
				<menuItem key="inventoryRow" style="TopMenuRowContainer" onPanelClick="this" 
				          hover="Hover"
				          icon="inventoryIcon"
				          label="{resource:getString($constants,'inventory')}"
				          description="{resource:getString($constants,'inventoryDescription')}"/>
				</menuPanel>
			  </menuPopupPanel>
		    </menuItem>
		    <widget>
            	<html style="topMenuBarSpacer" xml:space="preserve"> </html>
	        </widget>
	        <menuItem>
	          <menuDisplay>
				<widget>
					<label key="instrument" style="topMenuBarItem" text="{resource:getString($constants,'instrument')}" onClick="this" hover="Hover"/>
				</widget>
			  </menuDisplay>
              <menuPopupPanel key="instrumentPanel" autoHide="true" hidden="true" popup="this">
				<menuPanel key="instrumentTable" layout="vertical" style="topMenuContainer" xsi:type="Panel" padding="0" spacing="0">
				<menuItem key="instrumentRow" style="TopMenuRowContainer" onPanelClick="this" 
				          hover="Hover"
				          icon="instrumentIcon"
				          label="{resource:getString($constants,'instrument')}"
				          description="{resource:getString($constants,'instrumentDescription')}"/>
				</menuPanel>
			  </menuPopupPanel>
		    </menuItem>
			<widget>
            	<html style="topMenuBarSpacer" xml:space="preserve"> </html>
	        </widget>
	        <menuItem>
	          <menuDisplay>
				<widget>
					<label key="maintenance" style="topMenuBarItem" text="{resource:getString($constants,'maintenance')}" onClick="this" hover="Hover"/>
				</widget>
			  </menuDisplay>
			  <menuPopupPanel key="maintenancePanel" autoHide="true" hidden="true" popup="this">
				<menuPanel key="maintenancePanelTable" layout="vertical" style="topMenuContainer" xsi:type="Panel" padding="0" spacing="0">
				<menuItem key="testRow" style="TopMenuRowContainer" onPanelClick="this" 
				          hover="Hover"
				          icon="testIcon"
				          label="{resource:getString($constants,'test')}"
				          description="{resource:getString($constants,'testDescription')}"/>
				<menuItem key="methodRow" style="TopMenuRowContainer" onPanelClick="this" 
				          hover="Hover"
				          icon="methodIcon"
				          label="{resource:getString($constants,'method')}"
				          description="{resource:getString($constants,'methodDescription')}"/>
 				<menuItem key="panelRow" style="TopMenuRowContainer" onPanelClick="this" 
 				          hover="Hover"
 				          icon="panelIcon"
 				          label="{resource:getString($constants,'panel')}"
 				          description="{resource:getString($constants,'panelDescription')}"/>
				<menuItem key="QAEventRow" style="TopMenuRowContainer" onPanelClick="this" 
				          hover="Hover"
				          icon="QAEventIcon"
				          label="{resource:getString($constants,'QAEvent')}"
				          description="{resource:getString($constants,'QAEventDescription')}"/>
 				<menuItem key="labSectionRow" style="TopMenuRowContainer" onPanelClick="this" 
 				          hover="Hover"
 				          icon="labelSectionIcon"
 				          label="{resource:getString($constants,'labSection')}"
 				          description="{resource:getString($constants,'labSectionDescription')}"/>
			    <widget>
				   <html key="spacer">&lt;hr/&gt;</html>
				</widget>
				<menuItem key="analyteRow" style="TopMenuRowContainer" onPanelClick="this" 
				          hover="Hover"
				          icon="analyteIcon"
				          label="{resource:getString($constants,'analyte')}"
				          description="{resource:getString($constants,'analyteDescription')}"/>
				<menuItem key="dictionaryRow" style="TopMenuRowContainer" onPanelClick="this" 
				          hover="Hover"
				          icon="dictionaryIcon"
				          label="{resource:getString($constants,'dictionary')}"
				          description="{resource:getString($constants,'dictionaryDescription')}"/>
				<menuItem key="auxiliaryPromptRow" style="TopMenuRowContainer" onPanelClick="this" 
				          hover="Hover"
				          icon="auxiliaryPromptIcon"
				          label="{resource:getString($constants,'auxiliaryPrompt')}"
				          description="{resource:getString($constants,'auxiliaryPromptDescription')}"/>
			    <widget>
					<html key="spacer">&lt;hr/&gt;</html>
			    </widget>
			    <menuItem key="barcodeLabelRow" style="TopMenuRowContainer" onPanelClick="this" 
			              hover="Hover"
			              icon="barcodeLabelIcon"
			              label="{resource:getString($constants,'barcodeLabel')}"
			              description="{resource:getString($constants,'barcodeLabelDescription')"/>
				<menuItem key="standardNoteRow" style="TopMenuRowContainer" onPanelClick="this" 
				          hover="Hover"
				          icon="standardNoteIcon"
				          label="{resource:getString($constants,'standardNote')}"
				          description="{resource:getString($constants,'standardNoteDescription')}"/>
				<menuItem key="trailerForTestRow" style="TopMenuRowContainer" onPanelClick="this" 
				          hover="Hover"
				          icon="trailerForTestIcon"
				          label="{resource:getString($constants,'trailerForTest')}"
				          description="{resource:getString($constants,'trailerForTestDescription')}"/>
			    <widget>
					<html key="spacer">&lt;hr/&gt;</html>
			    </widget>
			    <menuItem key="storageUnitRow" style="TopMenuRowContainer" onPanelClick="this" 
			              hover="Hover"
			              icon="storageUnitIcon"
			              label="{resource:getString($constants,'storageUnit')}"
			              description="{resource:getString($constants,'storageUnitDescription')}"/>
				<menuItem key="storageLocationRow" style="TopMenuRowContainer" onPanelClick="this" 
				          hover="Hover"
				          icon="storageLocationIcon"
				          label="{resource:getString($constants,'storageLocation')}"
				          description="{resource:getString($constants,'storageLocationDescription')}"/>
				<widget>
					<html key="spacer">&lt;hr/&gt;</html>
				</widget>
				<menuItem key="instrumentMaintRow" style="TopMenuRowContainer" onPanelClick="this" 
				          hover="Hover"
				          icon="instrumentIcon"
				          label="{resource:getString($constants,'instrument')}"
				          description="{resource:getString($constants,'instrumentMainDescription')}"/>
				<widget>
					<html key="spacer">&lt;hr/&gt;</html>
				</widget>
			    <menuItem key="scriptletRow" style="TopMenuRowContainer" onPanelClick="this" 
			              hover="Hover"
			              icon="scriptletIcon"
			              label="{resource:getString($constants,'scriptlet')}"
			              description="{resource:getString($constants,'scriptletDescription')}"/>
				<menuItem key="systemVariableRow" style="TopMenuRowContainer" onPanelClick="this" 
				          hover="Hover"
				          icon="systemVariableIcon"
				          label="{resource:getString($constants,'systemVariable')}"
				          description="{resource:getString($constants,'systemVariableDescription')}"/>
				</menuPanel>
			  </menuPopupPanel>
		    </menuItem>
			<widget>
            	<html style="topMenuBarSpacer" xml:space="preserve"> </html>
	        </widget>
	        <menuItem>
	          <menuDisplay>
				<widget>
					<label key="report" style="topMenuBarItem" text="{resource:getString($constants,'report')}" onClick="this" hover="Hover"/>
				</widget>
			  </menuDisplay>
			  <menuPopupPanel key="reportPanel" autoHide="true" hidden="true" popup="this">
				<menuPanel key="reportPanelTable" layout="vertical" style="topMenuContainer" xsi:type="Panel" padding="0" spacing="0">
				    <menuItem key="finalReportRow" style="TopMenuRowContainer" onPanelClick="this" 
				          hover="Hover"
				          icon="finalReportIcon"
				          label="{resource:getString($constants,'finalReport')}"
				          description=""/>
					<menuItem key="sampleDataExportRow" style="TopMenuRowContainer" onPanelClick="this" 
					          hover="Hover"
					          icon="sampleDataExportIcon"
					          label="{resource:getString($constants,'sampleDataExport')}"
					          description=""/>
					<menuItem key="loginLabelRow" style="TopMenuRowContainer" onPanelClick="this" 
					          hover="Hover"
					          icon="loginLabelIcon"
					          label="{resource:getString($constants,'loginLabel')}"
					          description=""/>
					<widget>
						<html key="spacer">&lt;hr/&gt;</html>
					</widget>
					<menuItem key="referenceRow" style="TopMenuRowContainer" onPanelClick="this" 
					          hover="Hover"
					          icon="referenceIcon"
					          label="{resource:getString($constants,'reference')}"
					          description="">
					   <menuPopupPanel key="reportReferencePanel" autoHide="true" hidden="true" popup="this">
						  <menuPanel key="reportReferencePanelTable" layout="vertical" style="topMenuContainer" xsi:type="Panel" padding="0" spacing="0">
							<menuItem key="organizationReportRow" style="TopMenuRowContainer" 
							          onPanelClick="this" 
							          hover="Hover"
							          icon="organizationReportIcon"
							          label="{resource:getString($constants,'organization')}"
							          description=""/>

							<menuItem key="testReportRow" style="TopMenuRowContainer" 
							          onPanelClick="this" 
							          hover="Hover"
							          icon="testReportIcon"
							          label="{resource:getString($constants,'test')}"
							          description=""/>
							<menuItem key="qaEventReportRow" style="TopMenuRowContainer" 
							          onPanelClick="this" 
							          hover="Hover"
							          icon="qaEventReportIcon"
							          label="{resource:getString($constants,'QAEvent')}"
							          description=""/>
						  </menuPanel>
					    </menuPopupPanel>
					</menuItem>
					<menuItem key="summaryRow" style="TopMenuRowContainer" 
						      onPanelClick="this" 
						      hover="Hover"
						      icon="summaryIcon"
						      label="{resource:getString($constants,'summary')}"
						      description="">
						<menuPopupPanel key="reportSummaryPanel" autoHide="true" hidden="true" popup="this">
							<menuPanel key="reportSummaryPanelTable" layout="vertical" style="topMenuContainer" xsi:type="Panel" padding="0" spacing="0">
								<menuItem key="QAByOrganizationRow" style="TopMenuRowContainer" 
								          onPanelClick="this" 
								          hover="Hover"
								          icon="QAByOrganizationIcon"
								          label="{resource:getString($constants,'QAByOrganization')}"
								          description=""/>

								<menuItem key="testCountByFacilityRow" style="TopMenuRowContainer" 
								          onPanelClick="this" 
								          hover="Hover"
								          icon="sampleDataExportIcon"
								          label="{resource:getString($constants,'testCountByFacility')}"
								          description=""/>
								<menuItem key="6" style="TopMenuRowContainer" 
								          onPanelClick="this" 
								          hover="Hover"
								          icon="loginLabelIcon"
								          label="{resource:getString($constants,'turnaround')}"
								          description=""/>
								<menuItem key="7" style="TopMenuRowContainer" 
								          onPanelClick="this" 
								          hover="Hover"
								          icon="referenceIcon"
								          label="{resource:getString($constants,'positiveTestCount')}"
								          description=""/>
							</menuPanel>
						</menuPopupPanel>
					 </menuItem>
				</menuPanel>
			  </menuPopupPanel>
		    </menuItem>
        </menuPanel>
	   <panel layout="vertical" width="100%" xsi:type="Panel">
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
</xsl:stylesheet>