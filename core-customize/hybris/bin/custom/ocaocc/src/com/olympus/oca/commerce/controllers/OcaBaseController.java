package com.olympus.oca.commerce.controllers;

import com.google.common.collect.Lists;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.BusinessException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;

import javax.annotation.Resource;

import de.hybris.platform.webservicescommons.util.YSanitizer;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


public class OcaBaseController
{

	protected static final String DEFAULT_PAGE_SIZE = "20";
	protected static final String DEFAULT_CURRENT_PAGE = "0";
	protected static final String DEFAULT_FIELD_SET = FieldSetLevelHelper.DEFAULT_LEVEL;
	protected static final String HEADER_TOTAL_COUNT = "X-Total-Count";

	private static final Logger LOG = Logger.getLogger(OcaBaseController.class);

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	@Resource(name = "ocaB2BUnitValidator")
	private Validator ocaB2BUnitValidator;

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	protected void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}

	public void validateB2bUnitId(final String b2bUnitId)
	{
		final Errors errors = new BeanPropertyBindingResult(b2bUnitId, "B2B Unit Id");
		ocaB2BUnitValidator.validate(b2bUnitId, errors);
		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}
	}

	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	@ExceptionHandler(
			{ AmbiguousIdentifierException.class })
	public ErrorListWsDTO handleAmbiguousIdentifierException(final Exception ex)
	{
		return handleErrorInternal(AmbiguousIdentifierException.class.getSimpleName(), ex.getMessage());
	}

	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	@ExceptionHandler(
			{ BusinessException.class })
	public ErrorListWsDTO handleBusinessException(final Exception ex)
	{
		return handleErrorInternal(BusinessException.class.getSimpleName(), ex.getMessage());
	}


	protected ErrorListWsDTO handleErrorInternal(final String type, final String message)
	{
		final ErrorListWsDTO errorListDto = new ErrorListWsDTO();
		final ErrorWsDTO error = new ErrorWsDTO();
		error.setType(type.replace("Exception", "Error"));
		error.setMessage(sanitize(message));
		errorListDto.setErrors(Lists.newArrayList(error));
		return errorListDto;
	}

	protected static String sanitize(final String input)
	{
		return YSanitizer.sanitize(input);
	}
}
