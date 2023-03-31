import React, { useEffect, useState } from "react";
import Box from "@mui/material/Box";
import { getCheckout, getPoNumberData, getShippingAddressList,setSelectedAddressData, fetchCheckout } from "../../store/reducers/checkoutSlice";
import { useDispatch, useSelector } from "react-redux";
import OutlinedInput from '@mui/material/OutlinedInput';
import MenuItem from "@mui/material/MenuItem";
import FormControl from "@mui/material/FormControl";
import Select from "@mui/material/Select";
import './shippingAddress.scss'

const ShippingAddress = ({getpoNumberHandler, getAttnToSuiteHandler})=>{
    const { poSuccess, poFailure, poLoading} = useSelector(getCheckout);
    const { setShippingAddressSuccess, setShippingAddressLoading,setShippingAddressFailure } = useSelector(getCheckout);
    const {getShipAddressListSuccess, getShipAddressListLoading, getShipAddressListFailure} = useSelector(getCheckout);
    const {checkoutData, checkoutLoading} = useSelector(getCheckout);
    const [isShown, setIsShown] = useState();
    const [poNumber, setPoNumber] = useState();
    const [selectedOption, setSelectedOption] = useState();
    const [selectedFormattedAddress, setSelectedFormattedAddress] = useState();
    const [formattedAddress, setFormattedAddress] = useState();
    const [companyName, setCompanyName] = useState();
    const [checkoutFlag, setCheckoutFlag] = useState();
    const [addressList, setAddressList] = useState([{'id':123, 'formattedAddress':'address'}]);
    const [width, setWidth] = useState(25);
    const [attnToSuite, setAttnToSuite] = useState("")
    const dispatch = useDispatch();
    
    const handleClick = event =>{
        setIsShown(current => !current);
        dispatch(getShippingAddressList());
    }

    const handleCancel = event =>{
        setIsShown(current => !current);
    }

    useEffect(() => {
        if(poSuccess?.purchaseOrder?.purchaseOrderNumber){
         setPoNumber(poSuccess?.purchaseOrder?.purchaseOrderNumber)
        }
     }, [poSuccess]);

     useEffect(() => {
        if(getShipAddressListSuccess?.getAddresses?.shippingaddresses){
            setAddressList(getShipAddressListSuccess?.getAddresses?.shippingaddresses)
        }else{
            setAddressList([]);
        }
     }, [getShipAddressListSuccess]);

    useEffect(() => {
        if(checkoutData){
            setCompanyName(checkoutData?.data?.checkoutSummary?.shipping_addresses[0]?.company);
            setFormattedAddress(checkoutData?.data?.checkoutSummary?.shipping_addresses[0]?.formattedAddress);
        }
     }, [checkoutData]);

     useEffect(() => {
        if(setShippingAddressSuccess && checkoutFlag){
            dispatch(fetchCheckout());
            setCheckoutFlag(false);
            setIsShown(current => !current);
        }
     }, [setShippingAddressSuccess]);

     useEffect(() => {
        if (poNumber) {
            getpoNumberHandler(poNumber);
        }
      }, [poNumber]);

    const setPoNumberValue = (event) =>{
           setPoNumber(event.target.value);
           if(event.target.value.length >= 20){
            setWidth(event.target.value.length + 2);
           }
    }

    useEffect(()=> {
        if (attnToSuite) {
            getAttnToSuiteHandler(attnToSuite)
        }
    }, [attnToSuite])

    const setAttnToSuiteValue = (event) =>{
        setAttnToSuite(event.target.value);
    }

    const handleChange = (event) => {
        setSelectedOption(event.target.value.id);
        setSelectedFormattedAddress(event.target.value.formattedAddress);
    }

    const getPoNumber = (event) => {
            dispatch(getPoNumberData());
        };
    const setSelectedAddress = (event) => {
        setCheckoutFlag(true);
        const data = {
            id: selectedOption
        }
        dispatch(setSelectedAddressData(data));
    }

    return(
    <div className={`ShippingAddress`}>  
    <Box>
        <div className={`ShippingAddress__content`}>
            <div className="ShippingAddress__title">
                <span className="ShippingAddress__title-content">Shipping Address</span>
            <span className="ShippingAddress__title-link">
            {!isShown ? (<a class="ShippingAddress__change-address hyperlink" onClick={handleClick}>Change Address</a>) : ''}
            </span>
            </div>
            <div className="ShippingAddress__default-address">
                    <div className="ShippingAddress__company-name"><strong>{companyName}</strong></div>
                    <span>{formattedAddress}</span>
            </div>
            <div className="shippingAddress__loader">
        </div>
            {!isShown ?(<div>
                <div className="ShippingAddress__attn">
                    <span><strong>Attn to, Suite#</strong></span>
                    <div>
                    <input type="text" 
                           maxLength={40}
                           placeholder="Example placeholder text"
                           value={attnToSuite}
                           onChange={setAttnToSuiteValue}
                           class="ShippingAddress__input-suite input-fields"></input>
                        </div>
                </div>
                
                <div className="ShippingAddress__po">
                    <span className="required-field"><strong>Your Purchase Order # :</strong></span>
                    <div>
                        <input  required 
                                type="text"
                                style={{ width: width +'ch'}}
                                maxLength={35}
                                placeholder="PO Number"
                                class="ShippingAddress__input-po input-fields"
                                value={poNumber}
                                onChange={setPoNumberValue}></input>
                        <a class="hyperlink" onClick={getPoNumber}>Auto generate PO number</a>
                    </div>
                </div>
            </div>): isShown ?(
            <div>
                <div className="ShippingAddress__select">
                    <div className="pb-1 required-field"><strong>Change Shipping Address:</strong></div>
                                    <FormControl sx={{ m: 1, width: 300, mt: 3 }}>
                                        <Select
                                            displayEmpty
                                            onChange={handleChange}
                                            input={<OutlinedInput />}
                                            renderValue={() => {
                                                if (!selectedOption) {
                                                    return 'Select-one';
                                                } else {
                                                    return selectedFormattedAddress;
                                                }
                                            }}
                                            inputProps={{ 'aria-label': 'Without label' }}
                                        >
                                            <MenuItem style={{whiteSpace: 'normal'}} disabled value="">
                                                <em>Select-one</em>
                                            </MenuItem>
                                            {
                                                addressList.map((opt, i) => <MenuItem style={{whiteSpace: 'normal'}}  value={opt}>{opt?.formattedAddress}</MenuItem>)
                                            }
                                        </Select>
                                    </FormControl>
                </div>
                <div className="ShippingAddress__btnCont">
                    <button className="ShippingAddress__btnCont-confirm" disabled={!selectedOption} onClick={setSelectedAddress}>Confirm</button>
                    <button className="ShippingAddress__btnCont-cancel" onClick={handleCancel}>Cancel</button>
                </div>
            </div>) : '' }
        </div>
        </Box>
    </div> 
    );
            }
export default ShippingAddress;