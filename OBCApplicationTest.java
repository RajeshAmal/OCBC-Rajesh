package com.ocbc;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.*;
import org.springframework.beans.factory.*;
import org.springframework.mock.web.*;

public class OBCApplicationTest {
	
	private MockMvc mockMvcl
	
	@Autowired
	WebApplicationContext wac;
	
	@Test
	public void getLoginPersonAmount1() throws Exception {
		mockMvc.perform(get("/LoginController").param("type","loginPersonBalanace").param("username","Bob")).andExpect(status(),is(200));
	}
	
	@Test
	public void getLoginPersonAmount2() throws Exception {
		mockMvc.perform(get("/LoginController").param("type","loginPersonBalanace").param("username","Alice")).andExpect(status(),is(200));
	}
	
	@Test
	public void transferTheAmount1() throws Exception {
		mockMvc.perform(get("/LoginController").param("type","transfer").param("username","Bob").param("balance","80").param("payTo","Alice").param("amount","50")).andExpect(status(),is(200));
	}
	
	@Test
	public void transferTheAmount2() throws Exception {
		mockMvc.perform(get("/LoginController").param("type","transfer").param("username","Bob").param("balance","30").param("payTo","Alice").param("amount","30")).andExpect(status(),is(200));
	}
	
	@Test
	public void transferTheAmount2() throws Exception {
		mockMvc.perform(get("/LoginController").param("type","transfer").param("username","Bob").param("balance","0").param("payTo","Bob").param("amount","50")).andExpect(status(),is(200));
	}

	@Test
	public void transferTheAmount2() throws Exception {
		mockMvc.perform(get("/LoginController").param("type","transfer").param("username","Alice").param("balance","80").param("payTo","Bob").param("amount","30")).andExpect(status(),is(200));
	}
}
