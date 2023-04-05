/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.paymetric.commands;


import de.hybris.platform.payment.AdapterException;
import de.hybris.platform.payment.commands.Command;
import de.hybris.platform.paymetric.constants.PaymetricConstants;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;

import com.paymetric.xslt.CXmlHelper;
import com.primesys.xipayws.CTimer;
import com.primesys.xipayws.CXiPay;
import com.primesys.xipayws.XiPay30WSStub.ITransactionHeader;


/**
 * THIS ABSTRACT CLASS IS A BASE CLASS FOR ALL THE COMMANDS. EVERY PAYMETRIC COMMAND EXTENDS THIS ABSTRACT CLASS AND
 * IMPLEMENTS THE COMMAND SPECIFIC ABSTRACT METHODS. BASED ON THE INSTANTIATED COMMAND OBJECT, RESPECTIVE COMMAND
 * SPECIFIC METHODS WILL GET EXECUTED AT RUN TIME. IT HAS MADE USE OF "POLYMORPHISM" CONCEPT OF OOP
 */
public abstract class AbstractCommand<T, U> implements Command<T, U>
{
	protected static AtomicBoolean isLogging;
	protected static AtomicBoolean isProfiling;

	/**
	 * THIS PUBLIC CONSTRUCTOR IS RESPONSIBLE FOR LOADING THE HYBRIS CONFIGURATION VALUES AT SERVER STARTUP
	 */
	public AbstractCommand()
	{
		if (isLogging == null)
		{
			isLogging = new AtomicBoolean(PaymetricConstants.getLoggingEnabled());
			isProfiling = new AtomicBoolean(PaymetricConstants.getProfilingEnabled());
			CXiPay.setLogging(isLogging.get());
			CXiPay.setProfiling(isProfiling.get());
			PaymetricConstants.loadXIConfig();

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Pre-allocate XiPay objects?
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (PaymetricConstants.getM_xiPay() == null)
			{
				final ArrayList<CXiPay> xiQueue = new ArrayList<CXiPay>();
				for (int xiCount = 0; xiCount < PaymetricConstants.getObjectCount(); xiCount++)
				{
					xiQueue.add(PaymetricConstants.getXiPayInstance("MerchantID", "VI;MC", "USD;CAD"));
				}
				while (xiQueue.size() > 0)
				{
					PaymetricConstants.setXiPayInstance(xiQueue.remove(0));
				}
			}
		}
	}

	public abstract Logger getLogger();

	protected abstract void loadXslt();

	protected abstract ITransactionHeader[] translateRequest(final T request);

	protected abstract ITransactionHeader[] callXiPay(final CXiPay xiPay, final ITransactionHeader[] xiTran);

	protected abstract U translateResponse(final ITransactionHeader[] xiTran);


	/**
	 * public method to create new Element node
	 */
	public Element element(final String name, final String value)
	{
		return new Element(name).setText(value);
	}

	public Element element(final String name)
	{
		return new Element(name);
	}

	protected String readXslt(final String xsltName)
	{
		final CTimer timer = (isProfiling.get() ? new CTimer() : null);
		final String strWhere = PaymetricConstants.getWhere("readXslt");
		final String strXSL = "/paymetric/XSL/";
		final Logger LOG = getLogger();
		InputStream iXslt = null;
		Document doc = null;
		Document lib = null;
		Element root = null;
		List<Element> nodes = null;
		final ArrayList<Element> toAdd = new ArrayList<Element>();
		final ArrayList<Element> toRemove = new ArrayList<Element>();
		String libName = null;
		String result = null;
		int iBegin = 0;


		try
		{
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Load the Paymetric Hybris XSLT for the command
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			iXslt = AbstractCommand.class.getResourceAsStream(strXSL + xsltName);
			doc = CXmlHelper.DocumentFromStream(iXslt, LOG);
			root = doc.getRootElement();

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Load any custom XSLT template(s)
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			nodes = root.getChildren();
			for (final Element element : nodes)
			{
				if ("import".equalsIgnoreCase(element.getName()))
				{
					libName = element.getAttributeValue("href");
					if ((iBegin = libName.lastIndexOf('/')) == -1)
					{
						iBegin = 0;
					}
					else
					{
						iBegin++;
					}

					libName = libName.substring(iBegin);
					iXslt = AbstractCommand.class.getResourceAsStream(strXSL + libName);
					lib = CXmlHelper.DocumentFromStream(iXslt, LOG);
					toAdd.add(lib.getRootElement());
					toRemove.add(element);
				}
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Add load XSLT libraries
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			for (final Element element : toAdd)
			{
				root.addContent(element.cloneContent());
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Remove "import" elements
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			for (final Element element : toRemove)
			{
				root.removeContent(element);
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Get the XSLT as a string and remove unwanted comments
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			result = CXmlHelper.DocumentToString(doc, LOG);
			if ((iBegin = result.indexOf("<!-- Stylus Studio meta-information")) != -1)
			{
				result = result.substring(0, iBegin);
			}
		}
		catch (final Exception ex)
		{
			PaymetricConstants.forcedLog(LOG, strWhere, "Exception", ex);
		}
		finally
		{
			PaymetricConstants.forcedLog(LOG, strWhere, "Merged XSLT - " + strXSL + xsltName + "\n" + result);
			if (timer != null)
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "Duration=" + timer.getElapsedTimeString());
			}
		}

		return result;
	}

	/**
	 * public perform method for all commands
	 */
	@Override
	public U perform(final T request)
	{
		final String strWhere = PaymetricConstants.getWhere("perform");
		CTimer timer = null;
		ITransactionHeader[] xiTranRQ = null;
		ITransactionHeader[] xiTranRS = null;
		CXiPay xiPay = null;
		U response = null;
		final Logger LOG = getLogger();


		try
		{
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Get the latest configuration values for Profiling, Logging, XiIntercept and XSLT
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			isProfiling.set(PaymetricConstants.getProfilingEnabled());
			timer = (isProfiling.get() ? new CTimer() : null);
			isLogging.set(PaymetricConstants.getLoggingEnabled());
			CXiPay.setLogging(isLogging.get());
			CXiPay.setProfiling(isProfiling.get());
			PaymetricConstants.loadXIConfig();
			loadXslt();

			if (isLogging.get())
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "Hybris REQUEST\n" + CXmlHelper.toXml(request, LOG));
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Translate the Hybris Request to Paymetric Request
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			xiTranRQ = translateRequest(request);
			if (isLogging.get())
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "XiPay REQUEST\n" + CXmlHelper.toXml(xiTranRQ, LOG));
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Acquire an XiPay instance
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			xiPay = PaymetricConstants.getXiPayInstance(xiTranRQ[0].getMerchantID(), xiTranRQ[0].getCardType(),
					xiTranRQ[0].getCurrencyKey());

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Call to XiPay WebService to perform the requested payment operation
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (xiPay != null)
			{
				xiTranRS = callXiPay(xiPay, xiTranRQ);
				if (isLogging.get())
				{
					PaymetricConstants.forcedLog(LOG, strWhere, "XiPay RESPONSE\n" + CXmlHelper.toXml(xiTranRS, LOG));
				}
			}
			else
			{
				xiTranRS = xiTranRQ;
				for (int index = 0; index < xiTranRS.length; index++)
				{
					xiTranRS[index].setStatusCode(-1);
					xiTranRS[index].setMessage("XiPay communication failure");
				}
			}

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Translate Paymetric response to hybris interpretable response
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			response = translateResponse(xiTranRS);
		}
		catch (final Exception ex)
		{
			PaymetricConstants.forcedLog(LOG, strWhere, "Exception", ex);
			throw new AdapterException(ex.getMessage(), ex);
		}
		finally
		{
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Release the XiPay instance
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			PaymetricConstants.setXiPayInstance(xiPay);

			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// END
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (isLogging.get() && response != null)
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "Hybris RESPONSE\n" + CXmlHelper.toXml(response, LOG));
			}
			if (timer != null)
			{
				PaymetricConstants.forcedLog(LOG, strWhere, "Duration=" + timer.getElapsedTimeString());
			}
		}

		return response;
	}
}
