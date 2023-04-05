package com.olympus.oca.commerce.core.product.interceptors;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.mediaweb.assertions.assertj.Assertions;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class OcaProductPrepareInterceptorTest {
    @InjectMocks
    private final OcaProductPrepareInterceptor ocaProductPrepareInterceptor = new OcaProductPrepareInterceptor();

    @Mock
    private InterceptorContext ctx;


    public void setup(ProductModel product)
    {
        when(ctx.isModified(product,ProductModel.DISTRIBUTIONCHAINSTATUS)).thenReturn(true);
    }

    @Test
    public void testGhostProducts() throws InterceptorException {
        final ProductModel product = new ProductModel();

        setup(product);
        //ghost product testing - group1 - case 1 - positive testing
        product.setDistributionChainStatus("01");

        ocaProductPrepareInterceptor.onPrepare(product, ctx);

        assertThat(Boolean.FALSE).isEqualTo(product.isPurchaseEnabled());
        assertThat(Boolean.FALSE).isEqualTo(product.isSearchEnabled());

        //ghost product testing - group1 - case 2 - negative testing
        product.setDistributionChainStatus("09");
        product.setMaterialGroup4("A01");

        ocaProductPrepareInterceptor.onPrepare(product, ctx);

        assertThat(Boolean.FALSE).isEqualTo(product.isPurchaseEnabled());
        assertThat(Boolean.TRUE).isEqualTo(product.isSearchEnabled());

        //ghost product testing - group2 - case 1 - positive testing
        product.setDistributionChainStatus("02");
        product.setMaterialGroup4("A04");

        ocaProductPrepareInterceptor.onPrepare(product, ctx);

        assertThat(Boolean.FALSE).isEqualTo(product.isPurchaseEnabled());
        assertThat(Boolean.FALSE).isEqualTo(product.isSearchEnabled());

    }

    @Test
    public void testTemporaryPurchasableProducts() throws InterceptorException {
        final ProductModel product = new ProductModel();

        setup(product);
        //temporary purchasable product testing - case 1 - positive testing
        product.setDistributionChainStatus("09");
        product.setMaterialGroup4("A01");

        ocaProductPrepareInterceptor.onPrepare(product, ctx);

        assertThat(Boolean.TRUE).isEqualTo(product.isSearchEnabled());
        assertThat(Boolean.FALSE).isEqualTo(product.isPurchaseEnabled());

        //temporary purchasable product testing - case 2 - negative testing
        product.setDistributionChainStatus("09");
        product.setMaterialGroup4("A04");

        ocaProductPrepareInterceptor.onPrepare(product, ctx);

        assertThat(Boolean.FALSE).isEqualTo(product.isSearchEnabled());
        assertThat(Boolean.FALSE).isEqualTo(product.isPurchaseEnabled());

        //temporary purchasable product testing- case 3 - negative testing
        product.setDistributionChainStatus("01");
        product.setMaterialGroup4("A18");

        ocaProductPrepareInterceptor.onPrepare(product, ctx);

        assertThat(Boolean.FALSE).isEqualTo(product.isSearchEnabled());
        assertThat(Boolean.FALSE).isEqualTo(product.isPurchaseEnabled());

    }

    @Test
    public void testCapitalProducts() throws InterceptorException {
        ProductModel product = new ProductModel();
        setup(product);
        //capital product testing - case 1 - positive testing
        product.setDistributionChainStatus("09");
        product.setMaterialGroup4("A18");
        product.setMaterialGroup("9000");

        ocaProductPrepareInterceptor.onPrepare(product, ctx);

        assertThat(Boolean.TRUE).isEqualTo(product.isSearchEnabled());
        assertThat(Boolean.FALSE).isEqualTo(product.isPurchaseEnabled());

        //capital product testing - case 2 - positive testing
        product.setDistributionChainStatus("07");
        product.setMaterialGroup4("A18");
        product.setMaterialGroup("9002");

        ocaProductPrepareInterceptor.onPrepare(product, ctx);

        assertThat(Boolean.TRUE).isEqualTo(product.isSearchEnabled());
        assertThat(Boolean.FALSE).isEqualTo(product.isPurchaseEnabled());

        //capital product testing- case 3 - negative testing
        product.setDistributionChainStatus("01");
        product.setMaterialGroup4("A04");
        product.setMaterialGroup("9000");

        ocaProductPrepareInterceptor.onPrepare(product, ctx);

        assertThat(Boolean.FALSE).isEqualTo(product.isSearchEnabled());
        assertThat(Boolean.FALSE).isEqualTo(product.isPurchaseEnabled());

        //capital product testing- case 4 - negative testing
        product = new ProductModel();
        product.setDistributionChainStatus("01");
        product.setMaterialGroup4("A01");
        product.setMaterialGroup("9002");

        ocaProductPrepareInterceptor.onPrepare(product, ctx);

        assertThat(Boolean.FALSE).isEqualTo(product.isPurchaseEnabled());
        assertThat(Boolean.FALSE).isEqualTo(product.isSearchEnabled());

    }

    @Test
    public void testPurchasableProducts() throws InterceptorException {
        ProductModel product = new ProductModel();
        setup(product);
        // purchasable product testing - case 1 - positive testing
        product.setDistributionChainStatus("07");
        product.setMaterialGroup4("A01");

        ocaProductPrepareInterceptor.onPrepare(product, ctx);

        assertThat(Boolean.TRUE).isEqualTo(product.isSearchEnabled());
        assertThat(Boolean.TRUE).isEqualTo(product.isPurchaseEnabled());

        // purchasable product testing - case 2 - positive testing
        product.setDistributionChainStatus("29");
        product.setMaterialGroup4(null);

        ocaProductPrepareInterceptor.onPrepare(product, ctx);

        assertThat(Boolean.TRUE).isEqualTo(product.isSearchEnabled());
        assertThat(Boolean.TRUE).isEqualTo(product.isPurchaseEnabled());


        // purchasable product testing- case 3 - negative testing
        product.setDistributionChainStatus("09");
        product.setMaterialGroup4("A01");

        ocaProductPrepareInterceptor.onPrepare(product, ctx);

        assertThat(Boolean.TRUE).isEqualTo(product.isSearchEnabled());
        assertThat(Boolean.FALSE).isEqualTo(product.isPurchaseEnabled());

        // purchasable product testing- case 4 - negative testing
        product.setDistributionChainStatus("07");
        product.setMaterialGroup4("A04");

        ocaProductPrepareInterceptor.onPrepare(product, ctx);

        assertThat(Boolean.FALSE).isEqualTo(product.isSearchEnabled());
        assertThat(Boolean.FALSE).isEqualTo(product.isPurchaseEnabled());

    }

}
