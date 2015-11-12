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
	    <meta name="viewport" content="width=device-width"/>
	    <title>OpenELIS Authentication Service</title>
	    <script>	   
	    	var mql = window.matchMedia("(orientation: portrait)");

			// If there are matches, we're in portrait
			if(mql.matches) {  
				//window.alert("started in portrait");
			} else {  
				document.getElementById("textDiv").setAttribute("style","float:left;");
			}

			// Add a media query change listener
			mql.addListener(function(m) {
				if(m.matches) {
					//window.alert("changed to portrait");
				}
				else {
					document.getElementById("textDiv").setAttribute("style","float:left;");
				}
			});
			
	        function focusLogin() {
	            document.forms.login.username.focus();
	        }
	    </script>
        <style type="text/css">
            body {
                background : white;
            }
            .inputfield {
                width : 150px;
                border : 1px solid grey;
                padding-left : 5px;
                background : white;
                color : #333;
            }
            .prompt {
                color : grey;
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
	<div style="float:left;">
	<div id="textDiv">
                        <ul style="color:#333;" class="prompt">
                        	<li>Use your SHL assigned username to login to OpenELIS.</li>
                        	<li>Most usernames are in firstname.lastname format and are lowercase.</li>
                            <li>This is a secure site. Your ID and password are encrypted as they are sent for authorization.</li>
                            <li>If a password is forgotten, lost, or for some reason you cannot log in to your account, please call 319/335-4358 and we will be able to reset your password.</li>
                        </ul>
                    </div>
		<form method="post" name="login" autocomplete="off" action="j_security_check"
                    margin="0" onsubmit="document.forms.login.j_username.value=document.forms.login.username.value+';{session};{locale};{ip}';">
          <input type="hidden" name="j_username"/>
		<table cellspacing="0" cellpadding="0" style="padding-left:15%">
			<tr>
				<td>
			        <table width="100%">
            			<tr>
                			<td class="prompt" align="right">
                    			Username:
                			</td>
			                <td align="left">
                    			<input type="text" name="username" class="inputfield"
                        			onChange="javascript:this.value=this.value.toLowerCase();"/>
			                </td>
            			</tr>
            			<tr>
                			<td class="prompt" align="right">
                    			Password:
                			</td>
                			<td align="left">
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
			</tr>
		</table>
	</form>
	</div>
	</body>
</html>
</xsl:template>

</xsl:stylesheet>
