package com.vichumeats.controller;

import com.vichumeats.dto.*;
import com.vichumeats.entity.*;
import com.vichumeats.repository.*;
import com.vichumeats.service.ShopService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class StoreController {
  private final ShopService shop;
  private final DeliveryAreaRepository areas;
  private final CustomerOrderRepository orders;

  @SuppressWarnings("unchecked")
  private Map<Long, Integer> cart(HttpSession s) {
    if (s.getAttribute("cart") == null) s.setAttribute("cart", new LinkedHashMap<Long, Integer>());
    return (Map<Long, Integer>) s.getAttribute("cart");
  }

  @ModelAttribute
  void common(Model m, HttpSession s) {
    m.addAttribute("business", shop.settings());
    m.addAttribute("cartCount", cart(s).values().stream().mapToInt(Integer::intValue).sum());
  }

  @GetMapping("/api/cart")
  @ResponseBody
  Map<String, Object> cartState(HttpSession s) {
    return Map.of(
        "items",
        new LinkedHashMap<>(cart(s)),
        "authoritative",
        Boolean.TRUE.equals(s.getAttribute("cartTouched")));
  }

  @PostMapping("/api/cart/restore")
  @ResponseBody
  Map<String, Object> restore(@RequestBody Map<Long, Integer> saved, HttpSession s) {
    if (!Boolean.TRUE.equals(s.getAttribute("cartTouched"))) {
      shop.lines(saved);
      s.setAttribute("cart", new LinkedHashMap<>(saved));
      s.setAttribute("cartTouched", true);
    }
    return cartState(s);
  }

  @GetMapping("/")
  String home(Model m) {
    m.addAttribute("products", shop.products());
    return "home";
  }

  @GetMapping("/shop")
  String products(@RequestParam(defaultValue = "") String category, Model m) {
    m.addAttribute(
        "products",
        shop.products().stream()
            .filter(p -> category.isBlank() || p.getCategory().equalsIgnoreCase(category))
            .toList());
    return "shop";
  }

  @GetMapping("/farm")
  String farm() {
    return "farm";
  }

  @GetMapping("/contact")
  String contact(@RequestParam(defaultValue = "") String product, Model m) {
    m.addAttribute("interest", product);
    return "contact";
  }

  @GetMapping("/privacy")
  String privacy() {
    return "privacy";
  }

  @GetMapping("/login")
  String login() {
    return "login";
  }

  @PostMapping("/cart/add")
  String add(
      @RequestParam Long variantId,
      @RequestParam(defaultValue = "1") int quantity,
      HttpSession s,
      RedirectAttributes r) {
    Map<Long, Integer> next = new LinkedHashMap<>(cart(s));
    next.merge(variantId, quantity, Integer::sum);
    shop.lines(next);
    s.setAttribute("cart", next);
    s.setAttribute("cartTouched", true);
    r.addFlashAttribute("message", "Added to your cart.");
    return "redirect:/cart";
  }

  @PostMapping("/cart/update")
  String update(@RequestParam Long variantId, @RequestParam int quantity, HttpSession s) {
    if (quantity == 0) {
      cart(s).remove(variantId);
      s.setAttribute("cartTouched", true);
    } else {
      Map<Long, Integer> next = new LinkedHashMap<>(cart(s));
      next.put(variantId, quantity);
      shop.lines(next);
      s.setAttribute("cart", next);
      s.setAttribute("cartTouched", true);
    }
    return "redirect:/cart";
  }

  @GetMapping("/cart")
  String cartPage(Model m, HttpSession s) {
    try {
      var l = shop.lines(cart(s));
      m.addAttribute("lines", l);
      m.addAttribute("subtotal", shop.subtotal(l));
    } catch (IllegalArgumentException e) {
      m.addAttribute("cartError", e.getMessage());
      m.addAttribute("rawCart", new LinkedHashMap<>(cart(s)));
    }
    return "cart";
  }

  @GetMapping("/checkout")
  String checkout(Model m, HttpSession s) {
    var l = shop.lines(cart(s));
    if (l.isEmpty()) return "redirect:/cart";
    String token = UUID.randomUUID().toString();
    s.setAttribute("checkoutToken", token);
    s.setAttribute("reviewedSubtotal", shop.subtotal(l));
    CheckoutForm f = new CheckoutForm();
    f.setToken(token);
    m.addAttribute("form", f);
    checkoutModel(m, s);
    return "checkout";
  }

  private void checkoutModel(Model m, HttpSession s) {
    var l = shop.lines(cart(s));
    m.addAttribute("lines", l);
    m.addAttribute("subtotal", shop.subtotal(l));
    m.addAttribute("areas", areas.findAll().stream().filter(DeliveryArea::isActive).toList());
  }

  @PostMapping("/checkout")
  String checkoutPost(
      @Valid @ModelAttribute("form") CheckoutForm f, BindingResult errors, HttpSession s, Model m) {
    if (errors.hasErrors()) {
      checkoutModel(m, s);
      return "checkout";
    }
    try {
      synchronized (s) {
        CustomerOrder o =
            shop.place(
                f,
                cart(s),
                (String) s.getAttribute("checkoutToken"),
                (BigDecimal) s.getAttribute("reviewedSubtotal"));
        cart(s).clear();
        s.setAttribute("cartTouched", true);
        s.setAttribute("lastOrder", o.getId());
        return "redirect:/confirmation";
      }
    } catch (IllegalArgumentException e) {
      errors.reject("order", e.getMessage());
      checkoutModel(m, s);
      s.setAttribute("reviewedSubtotal", m.getAttribute("subtotal"));
      return "checkout";
    }
  }

  @GetMapping("/confirmation")
  String confirmation(HttpSession s, Model m) {
    Long id = (Long) s.getAttribute("lastOrder");
    if (id == null) return "redirect:/";
    CustomerOrder o = orders.findById(id).orElseThrow();
    m.addAttribute("order", o);
    String phone = shop.settings().getWhatsapp();
    if (!phone.isBlank()) {
      String summary =
          "Order request "
              + o.getReference()
              + "\n"
              + o.getItems().stream()
                  .map(
                      i -> i.getProductName() + " " + i.getVariantLabel() + " x " + i.getQuantity())
                  .reduce("", (a, b) -> a + b + "\n")
              + "Requested total INR "
              + o.getTotal();
      m.addAttribute(
          "whatsappUrl",
          "https://wa.me/" + phone + "?text=" + URLEncoder.encode(summary, StandardCharsets.UTF_8));
    }
    return "confirmation";
  }

  @GetMapping("/sheep")
  String sheep(Model m, HttpSession s) {
    SheepForm f = new SheepForm();
    String token = UUID.randomUUID().toString();
    s.setAttribute("sheepToken", token);
    f.setToken(token);
    m.addAttribute("form", f);
    return "sheep";
  }

  @PostMapping("/sheep")
  String sheepPost(
      @Valid @ModelAttribute("form") SheepForm f, BindingResult errors, HttpSession s, Model m) {
    if (errors.hasErrors()) return "sheep";
    try {
      synchronized (s) {
        var e = shop.enquire(f, (String) s.getAttribute("sheepToken"));
        s.setAttribute("sheepReference", e.getReference());
        return "redirect:/sheep/received";
      }
    } catch (IllegalArgumentException e) {
      errors.reject("enquiry", e.getMessage());
      return "sheep";
    }
  }

  @GetMapping("/sheep/received")
  String sheepReceived(Model m, HttpSession s) {
    if (s.getAttribute("sheepReference") == null) return "redirect:/sheep";
    m.addAttribute("reference", s.getAttribute("sheepReference"));
    return "received";
  }
}
