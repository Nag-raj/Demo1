/**
 *
 */
package com.olympus.oca.commerce.fulfilmentprocess.actions.order;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.task.RetryLaterException;
import com.olympus.oca.commerce.fulfilmentprocess.event.OrderSubmissionNotificationEvent;

import javax.annotation.Resource;


public class OcaSendOrderCompletedNotificationAction extends AbstractProceduralAction<OrderProcessModel>
{

	@Resource(name="eventService")
	private EventService eventService;

	@Override
	public void executeAction(final OrderProcessModel orderProcessModel) throws RetryLaterException, Exception
	{
		OrderModel orderModel = orderProcessModel.getOrder();
		final B2BCustomerModel currentCustomer = (B2BCustomerModel) orderModel.getUser();
		OrderSubmissionNotificationEvent ev = new OrderSubmissionNotificationEvent();
		final BaseStoreModel currentStore = orderModel.getStore();
		ev.setBaseStore(currentStore);
		ev.setSite(currentStore.getCmsSites().stream().findFirst().get());
		ev.setLanguage(currentStore.getDefaultLanguage());
		ev.setCurrency(currentStore.getDefaultCurrency());
		ev.setCustomer(currentCustomer);
		eventService.publishEvent(ev);
	}

}
