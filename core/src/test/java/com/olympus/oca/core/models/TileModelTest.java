package com.olympus.oca.core.models;

import com.olympus.oca.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import junitx.util.PrivateAccessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class TileModelTest {

	private final AemContext context = AppAemContext.newAemContext();

	@InjectMocks
	TileModel tileModel =new TileModel();

	@BeforeEach
	public void setup() throws Exception {
		PrivateAccessor.setField(tileModel, "tileText", "tileText");
		PrivateAccessor.setField(tileModel,"iconPath", "/content/iconPath");
		PrivateAccessor.setField(tileModel, "linkPath", "/content/linkPath");
	}

	@Test
	void test() {
		assertEquals("tileText", tileModel.getTileText());
		assertEquals("/content/iconPath", tileModel.getIconPath());
		assertEquals("/content/linkPath", tileModel.getLinkPath());
	}

}
