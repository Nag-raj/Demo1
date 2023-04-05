/**
 *
 */
package com.olympus.oca.commerce.fulfilmentprocess.actions.order;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;
import com.olympus.oca.commerce.integrations.order.service.SAPOrderIntegrationService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Resource;


public class OcaSubmitOrderToSapAction extends AbstractSimpleDecisionAction<OrderProcessModel>
{

	private static final Logger LOG = LogManager.getLogger(OcaSubmitOrderToSapAction.class);

	@Resource(name="orderIntegrationService")
	private SAPOrderIntegrationService sapOrderIntegrationService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Override
	public Transition executeAction(final OrderProcessModel orderProcessModel) throws RetryLaterException, Exception
	{
		AbstractOrderModel orderModel = orderProcessModel.getOrder();
		orderModel = sapOrderIntegrationService.submitOrderToSapBTP(orderModel);
		final Boolean isOrderSubmitted = StringUtils.isNotEmpty(orderModel.getErpOrderNumber());
		if(isOrderSubmitted){
			modelService.save(orderModel);
		}
		return isOrderSubmitted ? Transition.OK : Transition.NOK;
	}

}
