package xyz.hardliner.offside.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import xyz.hardliner.offside.SecurityConfig;
import xyz.hardliner.offside.domain.Client;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(WebServiceController.class)
@ContextConfiguration(classes = {SecurityConfig.class})
@ComponentScan(basePackages = "xyz.hardliner.offside")
public class WebServiceControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private ConsumptionHandler handler;

	/**
	 * Test for denying unauthorized user.
	 */
	@Test
	public void testUnauthorized() throws Exception {
		this.mockMvc.perform(get("/consumption").param("id", "1"))
				.andExpect(status().isUnauthorized());
	}

	/**
	 * Test for success user authorization.
	 */
	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void testBasicAuth() throws Exception {
		this.mockMvc.perform(get("/consumption)").param("id", "1"))
				.andExpect(authenticated().withRoles("USER"));
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void newClientTest() throws Exception {
		when(handler.newClient()).thenReturn(new Client());
		this.mockMvc.perform(post("/client")).andExpect(status().isOk());
		verify(handler, times(1)).newClient();
		verifyNoMoreInteractions(handler);
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void getActualConsumptionTest() throws Exception {
		when(handler.actualConsumption(anyLong())).thenReturn(Collections.emptyList());
		this.mockMvc.perform(get("/consumption").param("id", "1")).andExpect(status().isOk());
		verify(handler, times(1)).actualConsumption(eq(1L));
		verifyNoMoreInteractions(handler);
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void getHistoryOfConsumptionTest() throws Exception {
		when(handler.historyOfConsumption(anyLong(), anyString())).thenReturn(Collections.emptyList());
		this.mockMvc.perform(get("/consumption")
				.param("id", "1")
				.param("type", "gas"))
				.andExpect(status().isOk());
		verify(handler, times(1)).historyOfConsumption(eq(1L), eq("gas"));
		verifyNoMoreInteractions(handler);
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void addConsumptionTest() throws Exception {
		this.mockMvc.perform(post("/consumption")
				.param("id", "1")
				.param("type", "gas")
				.param("value", "60.5"))
				.andExpect(status().isOk());
		verify(handler, times(1))
				.addConsumption(eq(1L), eq("gas"), eq(BigDecimal.valueOf(60.5)));
		verifyNoMoreInteractions(handler);
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void exceptionTest() throws Exception {
		doThrow(new IllegalArgumentException("Test")).when(handler)
				.addConsumption(anyLong(), anyString(), any(BigDecimal.class));
		this.mockMvc.perform(post("/consumption")
				.param("id", "1")
				.param("type", "gas")
				.param("value", "60.5"))
				.andExpect(status().isBadRequest());
		verify(handler, times(1))
				.addConsumption(eq(1L), eq("gas"), eq(BigDecimal.valueOf(60.5)));
		verifyNoMoreInteractions(handler);
	}
}