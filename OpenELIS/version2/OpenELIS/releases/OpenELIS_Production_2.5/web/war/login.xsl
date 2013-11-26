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
                xmlns:random="xalan://java.util.Random"
                extension-element-prefixes="resource"
                version="1.0">

 <!-- 
   <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
  <xalan:component>
    <xalan:script lang="javaclass" src="xalan://java.util.Random"/>
  </xalan:component>
  
  -->

<xsl:output method="html" omit-xml-declaration="yes" encoding="UTF-8" indent="yes"/>

<xsl:template match="login">
<!-- 
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
    <xsl:variable name="rand" select="random:new()"/>
-->
<html>
	<head>
		<meta http-equiv="X-UA-Compatible" content="IE=8" />
	    <title>OpenELIS Authentication Service</title>
	    <script>	   
	        function focusLogin() {
	            document.forms.login.username.focus();
	        }
	    </script>
        <style type="text/css">
            body {
                background : url("signInBg.jpg") no-repeat 120px 82px #616161;
                width : 1024px;
            }
            .inputfield {
                width : 150px;
                border : 1px solid white;
                padding-left : 5px;
                background : white;
                color : #333;
            }
            .prompt {
                color : white;
                font-family : "Trebuchet MS", Arial, Helvetica, sans-serif;
                font-size : 13px;
            }
            .error {
                color:#ac0000;
                font-family : "Trebuchet MS", Arial, Helvetica, sans-serif;
                font-size : 13px;
                text-align:center;
                padding-top:32px;
                height:50px;
                
            }
            .submit {
                border : 1px solid #616161;
                color : #7D88DA;
                cursor : pointer;
                background : url("webbuttonbg.gif") repeat-x;
                height : 30px;
            }
		</style>
	</head>

	<body onLoad="focusLogin()">
		<form method="post" name="login" autocomplete="off" action="j_security_check"
                    margin="0" onsubmit="document.forms.login.j_username.value=document.forms.login.username.value+';{session};{locale};{ip}';">
          <input type="hidden" name="j_username"/>
		<table cellspacing="0" cellpadding="0">
			<tr>
				<td>
			        <table width="380px;" style="margin-top:408px;">
            			<tr>
                			<td class="prompt" align="right" width="300px">
                    			Username:<!-- <xsl:value-of select="resource:getString($constants,'username')"/> -->
                			</td>
			                <td align="right">
                    			<input type="text" name="username" class="inputfield"
                        			onChange="javascript:this.value=this.value.toLowerCase();"/>
			                </td>
            			</tr>
            			<tr>
                			<td class="prompt" align="right">
                    			Password:<!-- <xsl:value-of select="resource:getString($constants,'password')"/> -->
                			</td>
                			<td align="right">
                    			<input type="password" name="j_password" class="inputfield"/>
                			</td>
            			</tr>
			            <tr>
            			    <td/>
			                <td align="right">
            			        <input type="submit"
                        			value="Sign In" border="0"
                        			class="submit"/>
                			</td>
			            </tr>
            			<tr>
                			<td colspan="2">
                			    <div class="error">
                    			<xsl:value-of select="error"/>
                			    </div>
			                </td>
            			</tr>
        			</table>
				</td>
				<td>
					<div style="background:black;padding:8px;width:300px;margin:0px 0 0 240px;" class="prompt">Logging in:</div>
                    <div style="padding:8px;background:#EFEFEF;width:300px;margin:0 0 0 240px;">
                        <ul style="margin:0 0 0 12px;padding:0 0 0 8px;color:#333;" class="prompt">
                        	<li>Use your SHL assigned username to login to OpenELIS.</li>
                        	<li>Most usernames are in firstname.lastname format and are
                                lowercase.</li>
                            <li>This is a secure site. Your ID and password are encrypted as they are sent
                                for authorization.</li>
                        	<li>If you use a shared computer, please click LOGOUT and then exit the browser
                        	    after completing your session.</li>
                            <li>If a password is forgotten, lost, or for some reason you cannot log
                                in to your account, please call 319/335-4358 and we will be able
                                to reset your password.</li>
			                <li>Supported Browsers are: Firefox, Chrome, Safari, IE version 7 or later</li>
                        </ul>
                    </div>
				</td>
			</tr>
		</table>
	</form>
	</body>
</html>
</xsl:template>

</xsl:stylesheet>
