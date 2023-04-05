package com.olympus.oca.commerce.facades.b2bcustomer.converters.populator;

import com.olympus.oca.commerce.core.model.CRMCustomerAccountInviteModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.user.data.CustomerActivationData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.servicelayer.internal.model.impl.LocaleProvider;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.servicelayer.model.ModelContextUtils;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OcaB2BCustomerAccountInvitePopulatorTest extends TestCase {

    @InjectMocks
    private OcaB2BCustomerAccountInvitePopulator ocaB2BCustomerAccountInvitePopulator;
    @Mock
    private LocaleProvider localeProvider;

    @Before
    public void setup(){
        ocaB2BCustomerAccountInvitePopulator = new OcaB2BCustomerAccountInvitePopulator();

    }
    @Test
    public void testPopulate(){
        CRMCustomerAccountInviteModel source = new CRMCustomerAccountInviteModel();
        CustomerActivationData target = new CustomerActivationData();
        TitleModel titleModel = new TitleModel();
        localeProvider = Mockito.mock(LocaleProvider.class);
        final Locale locale = new Locale("EN");
        ((ItemModelContextImpl) (ModelContextUtils.getItemModelContext(titleModel))).setLocaleProvider(localeProvider);
        Mockito.when(localeProvider.getCurrentDataLocale()).thenReturn(locale);
        source.setName("shaun");
        source.setEmail("shaun@gmail.com");
        source.setCustomerID("customer1");
        source.setUid("customer1_uid");
        source.setContactID("6732819036");
        titleModel.setName("Mr.");
        source.setTitle(titleModel);

        B2BUnitModel group1 = new B2BUnitModel();
        ((ItemModelContextImpl) (ModelContextUtils.getItemModelContext(group1))).setLocaleProvider(localeProvider);
        group1.setLocName("group1");
        group1.setUid("group1");

        B2BUnitModel group2 = new B2BUnitModel();
        ((ItemModelContextImpl) (ModelContextUtils.getItemModelContext(group2))).setLocaleProvider(localeProvider);
        group2.setLocName("group2");
        group2.setUid("group2");

        final List<PrincipalGroupModel> groups = new ArrayList<>();
        groups.add(group1);
        groups.add(group2);


        source.setGroups(groups);
        source.setDefaultB2BUnit(group2);

        ocaB2BCustomerAccountInvitePopulator.populate(source,target);

        Assert.assertEquals("shaun",target.getName());
        Assert.assertEquals("shaun@gmail.com",target.getEmailId());
        Assert.assertEquals("customer1",target.getCustomerId());
        Assert.assertEquals("customer1_uid",target.getUid());
        Assert.assertEquals("6732819036",target.getContactID());
        Assert.assertEquals("Mr.",target.getTitle());
        Assert.assertEquals("group1",target.getAccountsInfo().get(0).getAccountId());
        Assert.assertTrue(target.getAccountsInfo().get(1).getIsDefault());
    }

}