
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                version="1.0">

<xsl:output method="html" encoding="UTF-8" indent="yes"/>

<xsl:template match="login">

<html>
<head>
    <title>OpenELIS Authentication Service</title>
    <script>
   
        function focusLogin() {
            document.login_form.username.focus();
        }
        
    </script>
    <style type="text/css">
        .inputfield
        {
           border:solid 1px black;
            padding-left: 5px;
            background:transparent; 
        }
        .inputbackground
        {
            background-image: url("signInBg.jpg");
            background-repeat:no-repeat;
            width:433px;
            height:312px;
        }
        .submit
        {
            background: url("signIn.gif") no-repeat;
            border-width:0px;
            width: 75px;
            height: 20px;
            color:#486095;
            cursor:pointer;
        }
        .spinnerIcon {width:16px; height:16px;background:transparent url("OSXspinnerGIF.gif") no-repeat scroll center;}
    </style>
</head>

<body onLoad="focusLogin()">

    <table align="center" id="table" style="position:absolute;left:28%;top:28%;">
        <tr>
            <td>
                <form method="post" id="login" autocomplete="off" action="j_security_check" onsubmit="document.forms['login'].style.display='none';document.forms['login'].elements['j_username'].value+=';{session};{locale}';" > 
                    <center>
                        <table border="0" cellspacing="5" align='center' class="inputbackground">
                            <tr><td colspan='2' style="height: 130px;"></td></tr>
                            <tr><td align="right" width='140'>
                                <font face="Arial,Helvetica">Username:</font></td>
                                <td align="left"><input type="text" name="j_username" class="inputfield" value='' onChange="javascript:this.value=this.value.toLowerCase();"/></td>
                            </tr>
                            <tr><td align="right" class="ptext"><font face="Arial,Helvetica">Password</font></td> 
                                <td align="left"><input type="password" name="j_password" class="inputfield"/></td>
                            </tr>
                            <tr><td align="right"></td>
                                <td align="left" height="100px" valign="top"><font face="Arial,Helvetica" color="#486095"><input type="submit" value="Signin" border="0" class="submit"/></font></td>
                            </tr>

                        </table>
                    </center>
               </form>
            </td>
        </tr>
        <tr id="credentials" style="display:none;">
          <td>
            <table>
              <tr>
                <td>
                  <font face="Arial,Helvetica">Checking Credentials...</font>
                </td>
                <td>
                  <div class="spinnerIcon"/>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        

 		 <tr>
 		     <td style="color:#ac0000;"><font face="Arial,Helvetica"><xsl:value-of select="error"/></font></td>
		  </tr>

		
    </table>
</body>
</html>
</xsl:template>


</xsl:stylesheet>
