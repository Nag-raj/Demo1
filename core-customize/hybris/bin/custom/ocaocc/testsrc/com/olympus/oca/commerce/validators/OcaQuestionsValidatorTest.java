package com.olympus.oca.commerce.validators;

import com.olympus.oca.commerce.dto.order.HeavyOrderQuestionsCartWsDTO;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.validation.Errors;


import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OcaQuestionsValidatorTest {

    private static String NAME = "name";

    private static String EMAIL = "email";

    private static String PHONE_NUMBER = "phoneNumber";

    private static String LARGE_TRUCK_ENTRY = "largeTruckEntry";

    private static String LIFT_AVAILABLE = "liftAvailable";

    private static String ORDER_DELIVERED_INSIDE = "orderDeliveredInside";
    private static String LOADING_DOCK = "loadingDock";

    private static String TRUCK_SIZE = "truckSize";

    @Mock
    private Errors errors;

    @Mock
    private HeavyOrderQuestionsCartWsDTO questionnaire;

    @InjectMocks
    private OcaQuestionsValidator ocaQuestionsValidator;

    @Test
    public void testValidateErrorsNameEmpty()
    {
        when(questionnaire.getName()).thenReturn(null);
        ocaQuestionsValidator.validate(questionnaire,errors);
        verify(errors).rejectValue(NAME,"questionnaire.nameIsEmpty","name should not be empty");
    }

    @Test
    public void testValidateErrorsEmailEmpty()
    {
        when(questionnaire.getEmail()).thenReturn(null);
        ocaQuestionsValidator.validate(questionnaire,errors);
        verify(errors).rejectValue(EMAIL,"questionnaire.emailIsEmpty","email should not be empty");
    }

    @Test
    public void testValidateErrorsPhoneNumberEmpty()
    {
        when(questionnaire.getPhoneNumber()).thenReturn(null);
        ocaQuestionsValidator.validate(questionnaire,errors);
        verify(errors).rejectValue(PHONE_NUMBER,"questionnaire.phoneNumberIsEmpty","phoneNumber should not be empty");
    }

    @Test
    public void testValidateErrorsLargeTruckEmpty()
    {
        when(questionnaire.getLargeTruckEntry()).thenReturn(null);
        ocaQuestionsValidator.validate(questionnaire,errors);
        verify(errors).rejectValue(LARGE_TRUCK_ENTRY,"questionnaire.largeTruckEntry","large Truck Entry possible :  Select yes or no");
    }

    @Test
    public void testValidateErrorsLiftAvailableEmpty()
    {
        when(questionnaire.getLiftAvailable()).thenReturn(null);
        ocaQuestionsValidator.validate(questionnaire,errors);
        verify(errors).rejectValue(LIFT_AVAILABLE,"questionnaire.liftAvailable","lift Available: Select yes or no");
    }

    @Test
    public void testValidateErrorsOrderDeliveredInsideEmpty()
    {
        when(questionnaire.getOrderDeliveredInside()).thenReturn(null);
        ocaQuestionsValidator.validate(questionnaire,errors);
        verify(errors).rejectValue(ORDER_DELIVERED_INSIDE,"questionnaire.orderDeliveredInside","Order delivered:  Select yes or no");
    }

    @Test
    public void testValidateErrorsLoadingDockEmpty()
    {
        when(questionnaire.getLoadingDock()).thenReturn(null);
        ocaQuestionsValidator.validate(questionnaire,errors);
        verify(errors).rejectValue(LOADING_DOCK,"questionnaire.loadingDock","loading dock available :  Select yes or no");
    }

    @Test
    public void testValidateErrorsTruckSizeEmpty()
    {
        when(questionnaire.getTruckSize()).thenReturn(null);
        ocaQuestionsValidator.validate(questionnaire,errors);
        verify(errors).rejectValue(TRUCK_SIZE,"questionnaire.truckSize","please enter the truck size");
    }
    @Before
    public void setUp() {
        when(questionnaire.getName()).thenReturn(NAME);
        when(questionnaire.getEmail()).thenReturn(EMAIL);
        when(questionnaire.getLiftAvailable()).thenReturn(Boolean.TRUE);
        when(questionnaire.getLargeTruckEntry()).thenReturn(Boolean.TRUE);
        when(questionnaire.getLoadingDock()).thenReturn(Boolean.TRUE);
        when(questionnaire.getPhoneNumber()).thenReturn(PHONE_NUMBER);
        when(questionnaire.getTruckSize()).thenReturn(TRUCK_SIZE);
    }

}
