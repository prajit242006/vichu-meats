package com.vichumeats;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.vichumeats.dto.*;
import com.vichumeats.entity.*;
import com.vichumeats.repository.*;
import com.vichumeats.service.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
      "app.demo=true",
      "app.uploads=./target/test-uploads"
    })
@AutoConfigureMockMvc
class StoreIntegrationTest {
  @Autowired MockMvc mvc;
  @Autowired ShopService service;
  @Autowired VariantRepository variants;
  @Autowired ProductRepository products;
  @Autowired CustomerOrderRepository orders;
  @Autowired SheepEnquiryRepository sheep;
  @Autowired ImageService images;
  Variant milk;

  @BeforeEach
  void prepare() {
    milk =
        variants.findAll().stream()
            .filter(v -> "100 ml".equals(v.getLabel()))
            .findFirst()
            .orElseThrow();
    milk.setAvailable(true);
    milk.setPrice(new BigDecimal("12.50"));
    milk.setDepositEnabled(false);
    milk = variants.save(milk);
  }

  CheckoutForm form(String token) {
    var f = new CheckoutForm();
    f.setCustomerName("Test Customer");
    f.setPhone("9000000000");
    f.setRequestedDate(LocalDate.now().plusDays(1));
    f.setToken(token);
    return f;
  }

  @Test
  void milkSizesAndMoneySnapshotsAndDuplicates() {
    assertThat(variants.findAll()).extracting(Variant::getLabel).contains("100 ml", "200 ml");
    String token = UUID.randomUUID().toString();
    var f = form(token);
    var cart = Map.of(milk.getId(), 3);
    assertThat(service.subtotal(service.lines(cart))).isEqualByComparingTo("37.50");
    long before = orders.count();
    var first = service.place(f, cart, token, new BigDecimal("37.50"));
    var repeat = service.place(f, Map.of(), token, new BigDecimal("37.50"));
    assertThat(first.getId()).isEqualTo(repeat.getId());
    assertThat(orders.count()).isEqualTo(before + 1);
    milk.setPrice(new BigDecimal("20.00"));
    milk = variants.save(milk);
    var saved = orders.findById(first.getId()).orElseThrow();
    assertThat(saved.getItems().get(0).getUnitPrice()).isEqualByComparingTo("12.50");
    assertThat(saved.getTotal()).isEqualByComparingTo("37.50");
  }

  @Test
  void unavailableAndMissingPriceAndPriceChangeAreRejected() {
    var c = Map.of(milk.getId(), 1);
    milk.setAvailable(false);
    milk = variants.save(milk);
    assertThatThrownBy(() -> service.lines(c)).isInstanceOf(IllegalArgumentException.class);
    milk.setAvailable(true);
    milk.setPrice(null);
    milk = variants.save(milk);
    assertThatThrownBy(() -> service.lines(c)).hasMessageContaining("Contact for price");
    milk.setPrice(new BigDecimal("13"));
    milk = variants.save(milk);
    String token = UUID.randomUUID().toString();
    assertThatThrownBy(() -> service.place(form(token), c, token, new BigDecimal("12.50")))
        .hasMessageContaining("Prices changed");
  }

  @Test
  void badQuantityAndTokenRejected() {
    assertThatThrownBy(() -> service.lines(Map.of(milk.getId(), 101)))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(
            () ->
                service.place(form("one"), Map.of(milk.getId(), 1), "two", new BigDecimal("12.50")))
        .hasMessageContaining("expired");
  }

  @Test
  void disabledDeliveryRejected() {
    var f = form(UUID.randomUUID().toString());
    f.setFulfilment("Delivery");
    assertThatThrownBy(
            () -> service.place(f, Map.of(milk.getId(), 1), f.getToken(), new BigDecimal("12.50")))
        .hasMessageContaining("Delivery");
  }

  @Test
  void sheepSavedOnce() {
    var f = new SheepForm();
    f.setCustomerName("Test Enquiry");
    f.setPhone("9000000000");
    f.setPreferredDate(LocalDate.now().plusDays(2));
    f.setWeightRange("Owner to advise");
    f.setToken(UUID.randomUUID().toString());
    long n = sheep.count();
    var a = service.enquire(f, f.getToken());
    var b = service.enquire(f, f.getToken());
    assertThat(a.getId()).isEqualTo(b.getId());
    assertThat(sheep.count()).isEqualTo(n + 1);
    assertThat(a.getReference()).startsWith("VS-");
  }

  @Test
  void customerPagesAndSecurity() throws Exception {
    for (String p :
        List.of(
            "/",
            "/shop",
            "/shop?category=Milk",
            "/farm",
            "/contact",
            "/privacy",
            "/cart",
            "/sheep",
            "/login",
            "/images/milk.svg",
            "/css/style.css",
            "/js/site.js")) mvc.perform(get(p)).andExpect(status().isOk());
    mvc.perform(get("/admin/orders")).andExpect(status().is3xxRedirection());
    mvc.perform(post("/cart/add").param("variantId", milk.getId().toString()))
        .andExpect(status().isForbidden());
    mvc.perform(get("/h2-console")).andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void adminTemplatesRender() throws Exception {
    for (String p :
        List.of(
            "/admin",
            "/admin/products",
            "/admin/products/edit",
            "/admin/variants/edit?productId=" + milk.getProduct().getId(),
            "/admin/orders",
            "/admin/sheep",
            "/admin/settings")) mvc.perform(get(p)).andExpect(status().isOk());
  }

  @Test
  void checkoutFlowAndBeanValidation() throws Exception {
    MockHttpSession s = new MockHttpSession();
    mvc.perform(
            post("/cart/add")
                .with(csrf())
                .session(s)
                .param("variantId", milk.getId().toString())
                .param("quantity", "2"))
        .andExpect(status().is3xxRedirection());
    mvc.perform(get("/checkout").session(s)).andExpect(status().isOk());
    String token = (String) s.getAttribute("checkoutToken");
    mvc.perform(
            post("/checkout")
                .with(csrf())
                .session(s)
                .param("customerName", "")
                .param("phone", "bad")
                .param("requestedDate", LocalDate.now().plusDays(1).toString())
                .param("token", token))
        .andExpect(status().isOk())
        .andExpect(model().attributeHasFieldErrors("form", "customerName", "phone"));
    mvc.perform(
            post("/checkout")
                .with(csrf())
                .session(s)
                .param("customerName", "Test Checkout")
                .param("phone", "9000000000")
                .param("fulfilment", "Pickup")
                .param("payment", "Not selected")
                .param("requestedDate", LocalDate.now().plusDays(1).toString())
                .param("token", token))
        .andExpect(redirectedUrl("/confirmation"));
    mvc.perform(get("/confirmation").session(s))
        .andExpect(status().isOk())
        .andExpect(
            content().string(org.hamcrest.Matchers.containsString("Order request received")));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void adminDetailsRender() throws Exception {
    String token = UUID.randomUUID().toString();
    var o = service.place(form(token), Map.of(milk.getId(), 1), token, new BigDecimal("12.50"));
    mvc.perform(get("/admin/orders/" + o.getId())).andExpect(status().isOk());
    var f = new SheepForm();
    f.setCustomerName("Test");
    f.setPhone("9000000000");
    f.setPreferredDate(LocalDate.now().plusDays(1));
    f.setWeightRange("unsure");
    f.setToken(UUID.randomUUID().toString());
    service.enquire(f, f.getToken());
    mvc.perform(get("/admin/sheep")).andExpect(status().isOk());
  }

  @Test
  void invalidImageRejected() {
    assertThatThrownBy(
            () ->
                images.save(
                    new MockMultipartFile(
                        "image", "evil.svg", "image/svg+xml", "<svg/>".getBytes())))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void browserCartRestoresOnlyIdentifiersAndQuantities() throws Exception {
    MockHttpSession session = new MockHttpSession();
    mvc.perform(
            post("/api/cart/restore")
                .with(csrf())
                .session(session)
                .contentType("application/json")
                .content("{\"" + milk.getId() + "\":2}"))
        .andExpect(status().isOk());
    mvc.perform(get("/api/cart").session(session))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items." + milk.getId()).value(2))
        .andExpect(jsonPath("$.authoritative").value(true));
  }

  @Test
  void loginAttemptsAreThrottledBeforeAuthentication() throws Exception {
    for (int i = 0; i < 15; i++)
      mvc.perform(
              post("/login").with(csrf()).param("username", "missing").param("password", "invalid"))
          .andExpect(status().is3xxRedirection());
    mvc.perform(
            post("/login").with(csrf()).param("username", "missing").param("password", "invalid"))
        .andExpect(status().isTooManyRequests());
  }

  @Test
  void enabledDepositUsesDecimalArithmetic() {
    milk.setDepositEnabled(true);
    milk.setDeposit(new BigDecimal("5.25"));
    milk = variants.save(milk);
    assertThat(service.subtotal(service.lines(Map.of(milk.getId(), 3))))
        .isEqualByComparingTo("53.25");
  }
}
