/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2019 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.paymetric.cronjobs;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.PaymetricScheduleBatchCronJobModel;
import de.hybris.platform.paymetric.constants.PaymetricConstants;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymetric.xslt.CXmlHelper;
import com.primesys.xipayws.CTimer;
import com.primesys.xipayws.CXiPay;
import com.primesys.xipayws.XiPay30WSStub.ITransactionHeader;


/**
 *
 */
public class PaymetricScheduleBatchJob extends AbstractJobPerformable<PaymetricScheduleBatchCronJobModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(PaymetricScheduleBatchJob.class);

	@Override
	public PerformResult perform(final PaymetricScheduleBatchCronJobModel arg0)
	{
		final CTimer timer = (PaymetricConstants.getProfilingEnabled() ? new CTimer() : null);
		final String strWhere = PaymetricConstants.getWhere("perform");
		final Calendar now = Calendar.getInstance();
		SimpleDateFormat sdFmt = null;
		ITransactionHeader xiTranRQ = null;
		ITransactionHeader xiTranRS = null;
		CXiPay xiPay = null;
		String batchID = null;
		PerformResult result = null;
		int countSuccess = 0;

		try
		{
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Acquire an XiPay instance using dummy values
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			xiPay = PaymetricConstants.getXiPayInstance("MerchantID", "VI;MC;AX", "USD;JPY");

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Determine the date to be use as part of the batch id
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (PaymetricConstants.getAutoCaptureInterval("cronjob").equalsIgnoreCase("hourly"))
			{
				sdFmt = new SimpleDateFormat("yyyy-MM-dd.HH");
				now.add(Calendar.HOUR_OF_DAY, -1);
			}
			else
			{
				sdFmt = new SimpleDateFormat("yyyy-MM-dd");
				now.add(Calendar.DAY_OF_MONTH, -1);
			}
			batchID = sdFmt.format(now.getTime());

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Set the proper Batch ID for Auto-Ship Orders
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			xiTranRQ = new ITransactionHeader();
			xiTranRQ.setBatchID("auto_" + batchID);

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Schedule the batch for settlement (funding) with XiPay
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (PaymetricConstants.getLoggingEnabled())
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "XiPay REQUEST\n" + CXmlHelper.toXml(xiTranRQ, LOG));
			}
			xiTranRS = xiPay.ScheduleBatch(xiTranRQ);
			if (xiTranRS.getStatusCode() == 210)
			{
				countSuccess++;
			}
			if (PaymetricConstants.getLoggingEnabled())
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "XiPay RESPONSE\n" + CXmlHelper.toXml(xiTranRS, LOG));
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Set the proper Batch ID for Store-Front (adhoc) Orders
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			xiTranRQ = new ITransactionHeader();
			xiTranRQ.setBatchID("adhoc_" + batchID);

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Schedule the batch for settlement (funding) with XiPay
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (PaymetricConstants.getLoggingEnabled())
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "XiPay REQUEST\n" + CXmlHelper.toXml(xiTranRQ, LOG));
			}
			xiTranRS = xiPay.ScheduleBatch(xiTranRQ);
			if (xiTranRS.getStatusCode() == 210)
			{
				countSuccess++;
			}
			if (PaymetricConstants.getLoggingEnabled())
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "XiPay RESPONSE\n" + CXmlHelper.toXml(xiTranRS, LOG));
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Set the proper Batch ID for Store-Front (adhoc) Orders
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			xiTranRQ = new ITransactionHeader();
			xiTranRQ.setBatchID("apm_" + batchID);

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Schedule the batch for settlement (funding) with XiPay
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (PaymetricConstants.getLoggingEnabled())
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "XiPay REQUEST\n" + CXmlHelper.toXml(xiTranRQ, LOG));
			}
			xiTranRS = xiPay.ScheduleBatch(xiTranRQ);
			if (xiTranRS.getStatusCode() == 210)
			{
				countSuccess++;
			}
			if (PaymetricConstants.getLoggingEnabled())
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "XiPay RESPONSE\n" + CXmlHelper.toXml(xiTranRS, LOG));
			}

			result = new PerformResult((countSuccess > 0 ? CronJobResult.SUCCESS : CronJobResult.FAILURE), CronJobStatus.FINISHED);
		}
		catch (final Exception ex)
		{
			PaymetricConstants.forcedLog(LOG, strWhere, "Exception", ex);
			result = new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
		}
		finally
		{
			if (timer != null)
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "Duration=" + timer.getElapsedTimeString());
			}
		}

		return result;
	}
}
