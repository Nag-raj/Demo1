		
        var CURRENCY_CONST="USD";
        var INTENT_CONST = "order";
        var PAYMENT_CONST = "paypal";
       
        var param1=document.getElementById("parameter1").value;
        var strEnv = document.getElementById("env_val").value;
        var strSandboxClientID="";
        var strProductionClientID="";
        
        /**************READING THE CLIENT-ID VALUE FROM LOCAL.PROPERTIES FILE***************/
        var client_id = document.getElementById("client_id").value;
        var allClientId = client_id.split(";");
        var strTemp="";
        
        if(allClientId.length != 1){
	       
        	for (i = 0; i < allClientId.length; i++) {
	            
	        	
	        	strTemp=allClientId[i].split(",");
	        	
	        	if(strTemp[0]==param1){
	        		client_id=strTemp[1];
	        	}
	        }
        }
        /**************END READING CLIENT-ID VALUE FROM LOCAL.PROPERTIES FILE***************/
        
                
        /***************READING THE EMAIL-ID VALUE FROM LOCAL.PROPERTIES FILE***************/
		var strMerchantEmail =  document.getElementById("merchant_email").value;
		var allEmailIds = strMerchantEmail.split(";");
        strTemp="";
        
        if(allEmailIds.length != 1){
	       
        	for (i = 0; i < allEmailIds.length; i++) {
	            
	        	
	        	strTemp=allEmailIds[i].split(",");
	        	
	        	if(strTemp[0]==param1){
	        		strMerchantEmail=strTemp[1];
	        	}
	        }
        }
        /***************END READING EMAIL-ID VALUE FROM LOCAL.PROPERTIES FILE***************/
        
        if(strEnv.toUpperCase()==="SANDBOX"){
        	strSandboxClientID=client_id;
        }else if(strEnv.toUpperCase()==="PRODUCTION"){
        	strProductionClientID=client_id;
        }
        
		var strAmt = roundN(document.getElementById("hidden_amt").value,2); //Restrict to 2 decimal places only
		
		
        try{
			
		 paypal.Button.render({
   
        	env: strEnv, // sandbox | production
			
            style: {
                size: 'small',
                color: 'silver',
                shape: 'rect',
                label: 'checkout'
                
            },
            
            client: {
                
                // TI CLIENT ID:
               	sandbox:strSandboxClientID,
                production:strProductionClientID
            },

            // Show the buyer a 'Pay Now' button in the checkout flow
            commit: true,

            // payment() is called when the button is clicked
            payment: function(data, actions) {
					
            	return actions.payment.create({
                    payment: {
                    		
                    	intent : INTENT_CONST,
                    	
                    	payer : {
                    		  payment_method: PAYMENT_CONST
                    	},
                    	
                        transactions: [
                            {
                                amount: { total: strAmt, currency: CURRENCY_CONST },
                                payee : { email: strMerchantEmail }
                                
                            }
                        ]
                    } 
                }); 
                  
            },

            // onAuthorize() is called when the buyer approves the payment
            onAuthorize: function(data, actions) {
            	
            	try
            	{
            	
					// AFTER USER CLICKS ON PAY NOW BUTTON, PAYPAL EXECUTES THE ORDER AND RETURNS THE DATA.
                	return actions.payment.execute().then(function(data) {
                	
                	//get the paypal order id
                	var resp_id=data.transactions[0].related_resources[0].order.id;
                    
                	
	                	if (typeof(resp_id) != "undefined")
	                	{
	
	                	               	
			                	// setting form values             
			                    if(document.getElementById("card_accountNumber"))
			                    {
			                 	   document.getElementById("card_accountNumber").value = resp_id;
			                    }
			                    else
			                    {
			                 	   var cardNumber = document.createElement("input");
			                 	   cardNumber.setAttribute("type", "hidden");
			                 	   cardNumber.setAttribute("name", "card_accountNumber");
			                 	   cardNumber.setAttribute("value", resp_id);
			             		   document.getElementById("silentOrderPostForm").appendChild(cardNumber);
			             		   
			                    }
			                 	
			                    document.getElementById("card_cardType").value = "008"; //PAYPAL CODE 008
			                    document.getElementById("card_cvNumber").value = "999";
			                    document.getElementById("ExpiryMonth").value = "12";
			                
			                             
			                    //CREATE HIDDEN EXPIRATION MONTH ELEMENT
								createHiddenElements(resp_id); 
			                    
			                    // DO TOKENIZATION AND SUBMIT THE FORM
			                    tokenizeOrderNumber(resp_id);
	                	}
	                	else
	                	{
	                		alert("Could not connect to PayPal \nPlease try later !!");
	                		window.close();
	                	}
                	
                	
                	}); // end actions.payment.execute method
                          
            	} // end try
            	catch(err){
            		
            		alert(err.message);
            	}
                      
               
                
            } // end onAuthorize

        }, '#paypal-button-container');
        
		}//end try block
		catch(err){
			alert(err.message);
		}
    
        

        
       function createHiddenElements(resp_id)
       {
    	   
    	     
		   var cardExpYear = document.createElement("input");
		   cardExpYear.setAttribute("type", "hidden");
		   cardExpYear.setAttribute("name", "card_expirationYear");
		   cardExpYear.setAttribute("value", "2099");
		   document.getElementById("silentOrderPostForm").appendChild(cardExpYear);
		   
		   var paypalOrderID = document.createElement("input");
		   paypalOrderID.setAttribute("type", "hidden");
		   paypalOrderID.setAttribute("name", "paypal_order_id");
		   paypalOrderID.setAttribute("value", resp_id);
		   document.getElementById("silentOrderPostForm").appendChild(paypalOrderID);
          	
    	   
       }
       
       function tokenizeOrderNumber(resp_id)
       {
   		
   		var frame = window.frames['xiFrame'];
   		document.getElementById('xiFrame').contentWindow.passOrderNumber(resp_id);
   		
   	   }
       
      function showToken(args)
      {
   	
    	 // ASSIGN TOKENIZED FORMAT OF THE PAYPAL ORDER ID TO CARD_ACCOUNTNUMBER AND THEN SUBMIT THE FORM
    	   var paypaltoken = document.createElement("input");
           paypaltoken.setAttribute("type", "hidden");
           paypaltoken.setAttribute("name", "card_token");
           paypaltoken.setAttribute("value", args);
           document.getElementById("silentOrderPostForm").appendChild(paypaltoken);   		
   		   document.getElementById("cmdSubmit").click();
   		
   	 }
      
     function roundN(num,n)
     {
    	  return parseFloat(Math.round(num * 100) / 100).toFixed(n);
     }

     
    