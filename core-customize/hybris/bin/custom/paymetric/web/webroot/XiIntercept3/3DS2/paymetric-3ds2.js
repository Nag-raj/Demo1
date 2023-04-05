function render3dsPopup(e){
     
     
     if (!e) {
         return;
     }
     
     if (!e.HasPassed) {
         return;
     }

     if ($("#post_frame").length > 0) {
         return;
     } 
     
     var myiframe = document.createElement("iframe");
     myiframe.setAttribute("id","post_frame");
     myiframe.setAttribute("style","width:450px;height:480px;");
     myiframe.setAttribute("scrolling","yes");
     myiframe.setAttribute("frameborder","0");
     
     document.body.appendChild(myiframe);
     
     var framebody = $('#post_frame').contents().find('body');
     
     var context = e.UrlsAndPayloadsFor3Ds;
      
     var version = parseFloat(context.ThreeDSVersion).toFixed(0); 
      
     if(version < 2)
    {
     
      
      var myform = document.createElement("form");
         myform.setAttribute("method", "post");
         myform.setAttribute("action", context.CentinelAcsUrl);
         myform.setAttribute("target", "view");
         
         var hiddenField1 = document.createElement("input"); 
         hiddenField1.setAttribute("type", "hidden");
         hiddenField1.setAttribute("name", "PaReq");
         hiddenField1.setAttribute("value", context.CentinelPayload);
         myform.appendChild(hiddenField1);
         
         var hiddenField2 = document.createElement("input"); 
         hiddenField2.setAttribute("type", "hidden");
         hiddenField2.setAttribute("name", "TermUrl");
         hiddenField2.setAttribute("value", context.CentinelTermUrl);
         myform.appendChild(hiddenField2);
         
         var hiddenField3 = document.createElement("input"); 
         hiddenField3.setAttribute("type", "hidden");
         hiddenField3.setAttribute("name", "MD");
         hiddenField3.setAttribute("value", context.PaymetricPayload);
         myform.appendChild(hiddenField3);
    
         framebody.append(myform); 
      
      
    }
     else
    {
     var myform = document.createElement("form");
          myform.setAttribute("method", "post");
          myform.setAttribute("action", context.StepUpUrl);
          myform.setAttribute("target", "view");
          
          var hiddenField1 = document.createElement("input"); 
          hiddenField1.setAttribute("type", "hidden");
          hiddenField1.setAttribute("name", "JWT");
          hiddenField1.setAttribute("value", context.JWT);
          myform.appendChild(hiddenField1);
          
          var hiddenField2 = document.createElement("input"); 
          hiddenField2.setAttribute("type", "hidden");
          hiddenField2.setAttribute("name", "MD");
          hiddenField2.setAttribute("value", context.PaymetricPayload);
          myform.appendChild(hiddenField2);
          
          framebody.append(myform);
    }
     var left = (screen.width/2)-(1000/2);
     var top = (screen.height/2)-(600/2);
     
        $('#cboxOverlay').show().css({'opacity':'0.7', 'cursor':'pointer'});
 
	    var tab3DSWindow =   window.open('', 'view',"width=1000,height=600,top="+top+",left="+left+",toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,copyhistory=no");
	    tab3DSWindow.focus(); 
	    
	    var timer = setInterval(function() { 
	        if(tab3DSWindow.closed) {
	            clearInterval(timer);
	            $('#cboxOverlay').removeAttr('style').hide();
	        }
	    }, 1000);

     myform.submit();
     
     
}


function getResponseValue(inputStr) {
	
	var defaultVal = '';
	
	var result = (typeof(inputStr) != "undefined" && inputStr != null)? inputStr : defaultVal;
	return result;
}

function add3ds2Elements(CardinalResp){
	
	

     if(CardinalResp.cmpi_authenticate != null && CardinalResp.cmpi_lookup != null)
     {
     
          var cavv = document.createElement("input");
          cavv.setAttribute("type", "hidden");
          cavv.setAttribute("name", "card_cavv");          
          cavv.setAttribute("value",getResponseValue(CardinalResp.cmpi_authenticate.Cavv));
          document.getElementById("silentOrderPostForm").appendChild(cavv);
             
          if(typeof(CardinalResp.cmpi_authenticate.DSTransactionId) !== 'undefined'	)
          {    
	          var DSTranID = document.createElement("input");
	          DSTranID.setAttribute("type", "hidden");
	          DSTranID.setAttribute("name", "card_dstranid");
	          DSTranID.setAttribute("value",getResponseValue(CardinalResp.cmpi_authenticate.DSTransactionId)); 
	          document.getElementById("silentOrderPostForm").appendChild(DSTranID);
          }           
          
          if(typeof(CardinalResp.cmpi_authenticate.Xid) !== 'undefined'	)
          {
	           var XID = document.createElement("input");
	           XID.setAttribute("type", "hidden");
	           XID.setAttribute("name", "card_xid");
	           XID.setAttribute("value",getResponseValue(CardinalResp.cmpi_authenticate.Xid));
	           document.getElementById("silentOrderPostForm").appendChild(XID);
          }
          
          var parEsStatus = document.createElement("input");
          parEsStatus.setAttribute("type", "hidden");
          parEsStatus.setAttribute("name", "card_parEsStatus");
          parEsStatus.setAttribute("value",getResponseValue(CardinalResp.cmpi_authenticate.PAResStatus));
          document.getElementById("silentOrderPostForm").appendChild(parEsStatus);
          
           var eciFlag = document.createElement("input");
          eciFlag.setAttribute("type", "hidden");
          eciFlag.setAttribute("name", "card_eciFlag");
          eciFlag.setAttribute("value",Number(getResponseValue(CardinalResp.cmpi_authenticate.EciFlag)));
          document.getElementById("silentOrderPostForm").appendChild(eciFlag);
          
           var signatureFlag = document.createElement("input");
          signatureFlag.setAttribute("type", "hidden");
          signatureFlag.setAttribute("name", "card_signatureVerification");
          signatureFlag.setAttribute("value",getResponseValue(CardinalResp.cmpi_authenticate.SignatureVerification));
          document.getElementById("silentOrderPostForm").appendChild(signatureFlag);
          
           var enrolledFlag = document.createElement("input");
          enrolledFlag.setAttribute("type", "hidden");
          enrolledFlag.setAttribute("name", "card_enrolled");
          enrolledFlag.setAttribute("value",getResponseValue(CardinalResp.cmpi_lookup.Enrolled));
          document.getElementById("silentOrderPostForm").appendChild(enrolledFlag);
          
           var cardinalTransID_lookup = document.createElement("input");
          cardinalTransID_lookup.setAttribute("type", "hidden");
          cardinalTransID_lookup.setAttribute("name", "cardinal_TransID");
          cardinalTransID_lookup.setAttribute("value",getResponseValue(CardinalResp.cmpi_lookup.TransactionID));
          document.getElementById("silentOrderPostForm").appendChild(cardinalTransID_lookup);
          
          var cardinalThreeDSVersion_lookup = document.createElement("input");
          cardinalThreeDSVersion_lookup.setAttribute("type", "hidden");
          cardinalThreeDSVersion_lookup.setAttribute("name", "card_ThreeDSVersion");
          cardinalThreeDSVersion_lookup.setAttribute("value",getResponseValue(CardinalResp.cmpi_lookup.ThreeDSVersion));
          document.getElementById("silentOrderPostForm").appendChild(cardinalThreeDSVersion_lookup);
     }
     
     if(CardinalResp.cmpi_lookup != null && CardinalResp.cmpi_authenticate == null)
     {
                   
           var cavv_lookup = document.createElement("input");
          cavv_lookup.setAttribute("type", "hidden");
          cavv_lookup.setAttribute("name", "card_cavv");
          cavv_lookup.setAttribute("value",getResponseValue(CardinalResp.cmpi_lookup.Cavv));
          document.getElementById("silentOrderPostForm").appendChild(cavv_lookup);             
               
                    
          if(typeof(CardinalResp.cmpi_lookup.DSTransactionId) !== 'undefined')
          {
        	  var DSTranID_lookup = document.createElement("input");
              DSTranID_lookup.setAttribute("type", "hidden");
              DSTranID_lookup.setAttribute("name", "card_dstranid");
              DSTranID_lookup.setAttribute("value",getResponseValue(CardinalResp.cmpi_lookup.DSTransactionId));
              document.getElementById("silentOrderPostForm").appendChild(DSTranID_lookup);
          }           
           
           if(typeof(CardinalResp.cmpi_lookup.Xid) !== 'undefined')
           {
        	   var XID_lookup = document.createElement("input");
               XID_lookup.setAttribute("type", "hidden");
               XID_lookup.setAttribute("name", "card_xid");
               XID_lookup.setAttribute("value",getResponseValue(CardinalResp.cmpi_lookup.Xid));
               document.getElementById("silentOrderPostForm").appendChild(XID_lookup);
           }
          
           //PAYER AUTH RESULT STATUS - PARESTATUS
          var parEsStatus_lookup = document.createElement("input");
          parEsStatus_lookup.setAttribute("type", "hidden");
          parEsStatus_lookup.setAttribute("name", "card_parEsStatus");
          parEsStatus_lookup.setAttribute("value",getResponseValue(CardinalResp.cmpi_lookup.PAResStatus));
          document.getElementById("silentOrderPostForm").appendChild(parEsStatus_lookup);
                    
          //ECI FLAG
           var eciFlag_lookup = document.createElement("input");
          eciFlag_lookup.setAttribute("type", "hidden");
          eciFlag_lookup.setAttribute("name", "card_eciFlag");
          eciFlag_lookup.setAttribute("value",Number(getResponseValue(CardinalResp.cmpi_lookup.EciFlag)));
          document.getElementById("silentOrderPostForm").appendChild(eciFlag_lookup);
          
          // SIGNATURE VERIFICATION
           var signatureFlag_lookup = document.createElement("input");
          signatureFlag_lookup.setAttribute("type", "hidden");
          signatureFlag_lookup.setAttribute("name", "card_signatureVerification");
          signatureFlag_lookup.setAttribute("value",getResponseValue(CardinalResp.cmpi_lookup.SignatureVerification));
          document.getElementById("silentOrderPostForm").appendChild(signatureFlag_lookup);
          
           var enrolledFlag_lookup = document.createElement("input");
          enrolledFlag_lookup.setAttribute("type", "hidden");
          enrolledFlag_lookup.setAttribute("name", "card_enrolled");
          enrolledFlag_lookup.setAttribute("value",getResponseValue(CardinalResp.cmpi_lookup.Enrolled));
          document.getElementById("silentOrderPostForm").appendChild(enrolledFlag_lookup);
          
           var cardinalTransID_lookup = document.createElement("input");
          cardinalTransID_lookup.setAttribute("type", "hidden");
          cardinalTransID_lookup.setAttribute("name", "cardinal_TransID");
          cardinalTransID_lookup.setAttribute("value",getResponseValue(CardinalResp.cmpi_lookup.TransactionID));
          document.getElementById("silentOrderPostForm").appendChild(cardinalTransID_lookup);
          
          var cardinalThreeDSVersion_lookup = document.createElement("input");
          cardinalThreeDSVersion_lookup.setAttribute("type", "hidden");
          cardinalThreeDSVersion_lookup.setAttribute("name", "card_ThreeDSVersion");
          cardinalThreeDSVersion_lookup.setAttribute("value",getResponseValue(CardinalResp.cmpi_lookup.ThreeDSVersion));
          document.getElementById("silentOrderPostForm").appendChild(cardinalThreeDSVersion_lookup);
     }
}

