  var countrycode = document.getElementById("hidden_country").value;
  
    if(countrycode == 'CN' || countrycode == 'HK' ) { 	

    	document.getElementById("alipayimage").style.display = "inline";
    	
    }    
    else {
    	
    	showcreditcard();

    }
    
    function showcreditcard(){
    
    	document.getElementById("xiFrameHosted").style.display = "inline";
    	if(document.getElementById("xiFrameAlipay"))
    	{
    		document.getElementById("xiFrameAlipay").style.display = "none";
    	}

    }
    
	function getOrderData() {
	    	
		var hybrisdata = {
			amount: document.getElementById("hidden_amt").value
		};
		
		return hybrisdata;
	 }
	
	function showalipay() {
	      
        $('#cboxOverlay').show().css({'opacity': '0.7','cursor': 'pointer'});

      var alipayFrame = document.getElementById("xiFrameAlipay");
      
      var tabOrWindow =  window.open(alipayFrame.src, 
                'newwindow', 
                'width=1500,height=500,scrollbars=auto'); 
      tabOrWindow.focus(); 
      
      var timer = setInterval(function() { 
          if(tabOrWindow.closed) {
              clearInterval(timer);
              $('#cboxOverlay').removeAttr('style').hide();
          }
      }, 1000);
      
    }
      
    function handleAlipayResponse(tokenizedOrderNumber, cardNumber)
    {
            $('#cboxOverlay').removeAttr('style').hide();

             if( tokenizedOrderNumber != null) {

                document.getElementById("card_cardType").value = "009"; // Cardtype configured for Alipay
			    document.getElementById("card_accountNumber").value = tokenizedOrderNumber;
			    document.getElementById("card_cvNumber").value = "123";
			    document.getElementById("ExpiryMonth").value = "12";
			    var expiryYear = new Date().getFullYear();       
			    document.getElementById("ExpiryYear").value = expiryYear+1; // to always get a future year
			    var nameOnCard = document.getElementById("hidden_firstName").value + " " + document.getElementById("hidden_lastName").value;
			    document.getElementById("card_nameOnCard").value = nameOnCard; 
				   
			   var alipayOrderID = document.createElement("input");
			   alipayOrderID.setAttribute("type", "hidden");
			   alipayOrderID.setAttribute("name", "alipay_order_id");
			   alipayOrderID.setAttribute("value", cardNumber);
			   document.getElementById("silentOrderPostForm").appendChild(alipayOrderID);
			    
			    // submit the SOP form
			    document.getElementById("cmdSubmit").click();

	    	} 
			
    }
   		    