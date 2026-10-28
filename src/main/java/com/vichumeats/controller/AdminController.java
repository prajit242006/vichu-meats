package com.vichumeats.controller;

import com.vichumeats.dto.*;
import com.vichumeats.entity.*;
import com.vichumeats.repository.*;
import com.vichumeats.service.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
  private final ProductRepository products;
  private final VariantRepository variants;
  private final CustomerOrderRepository orders;
  private final SheepEnquiryRepository sheep;
  private final BusinessSettingsRepository settings;
  private final DeliveryAreaRepository areas;
  private final ImageService images;
  private static final List<String> ORDER_STATUSES =
      List.of("Requested", "Confirmed", "Preparing", "Ready", "Completed", "Cancelled");

  @GetMapping
  String dashboard(Model m) {
    var all = orders.findAll();
    m.addAttribute("orderCount", all.size());
    m.addAttribute("completed", orders.countByStatus("Completed"));
    m.addAttribute("requested", orders.countByStatus("Requested"));
    m.addAttribute("sheepCount", sheep.count());
    m.addAttribute(
        "completedTotal",
        all.stream()
            .filter(o -> o.getStatus().equals("Completed") && !o.isDemo())
            .map(CustomerOrder::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
    return "admin/dashboard";
  }

  @GetMapping("/products")
  String products(Model m) {
    m.addAttribute("products", products.findAll());
    return "admin/products";
  }

  @GetMapping("/products/edit")
  String product(@RequestParam(required = false) Long id, Model m) {
    ProductForm f = new ProductForm();
    if (id != null) {
      Product p = products.findById(id).orElseThrow();
      f.setId(id);
      f.setName(p.getName());
      f.setCategory(p.getCategory());
      f.setDescription(p.getDescription());
      f.setActive(p.isActive());
      f.setDemo(p.isDemo());
    }
    m.addAttribute("form", f);
    return "admin/product";
  }

  @PostMapping("/products/save")
  String saveProduct(
      @Valid @ModelAttribute("form") ProductForm f,
      BindingResult e,
      @RequestParam(required = false) MultipartFile image)
      throws Exception {
    if (e.hasErrors()) return "admin/product";
    Product p = f.getId() == null ? new Product() : products.findById(f.getId()).orElseThrow();
    p.setName(f.getName());
    p.setCategory(f.getCategory());
    p.setDescription(f.getDescription());
    p.setActive(f.isActive());
    p.setDemo(f.isDemo());
    if (image != null && !image.isEmpty()) p.setImage(images.save(image));
    products.save(p);
    return "redirect:/admin/products";
  }

  @GetMapping("/variants/edit")
  String variant(@RequestParam Long productId, @RequestParam(required = false) Long id, Model m) {
    VariantForm f = new VariantForm();
    f.setProductId(productId);
    if (id != null) {
      Variant v = variants.findById(id).orElseThrow();
      f.setId(id);
      f.setProductId(v.getProduct().getId());
      f.setLabel(v.getLabel());
      f.setUnit(v.getUnit());
      f.setPrice(v.getPrice());
      f.setDeposit(v.getDeposit());
      f.setAvailable(v.isAvailable());
      f.setDepositEnabled(v.isDepositEnabled());
    }
    m.addAttribute("form", f);
    return "admin/variant";
  }

  @PostMapping("/variants/save")
  String saveVariant(@Valid @ModelAttribute("form") VariantForm f, BindingResult e) {
    if (e.hasErrors()) return "admin/variant";
    Variant v = f.getId() == null ? new Variant() : variants.findById(f.getId()).orElseThrow();
    v.setProduct(products.findById(f.getProductId()).orElseThrow());
    v.setLabel(f.getLabel());
    v.setUnit(f.getUnit());
    v.setPrice(f.getPrice());
    v.setDeposit(f.getDeposit());
    v.setAvailable(f.isAvailable());
    v.setDepositEnabled(f.isDepositEnabled());
    variants.save(v);
    return "redirect:/admin/products";
  }

  @GetMapping("/orders")
  String orders(Model m) {
    m.addAttribute(
        "orders", orders.findAll(org.springframework.data.domain.Sort.by("id").descending()));
    return "admin/orders";
  }

  @GetMapping("/orders/{id}")
  String order(@PathVariable Long id, Model m) {
    m.addAttribute("order", orders.findById(id).orElseThrow());
    m.addAttribute("statuses", ORDER_STATUSES);
    return "admin/order";
  }

  @PostMapping("/orders/{id}/status")
  String orderStatus(@PathVariable Long id, @RequestParam String status) {
    if (!ORDER_STATUSES.contains(status)) throw new IllegalArgumentException("Invalid status.");
    CustomerOrder o = orders.findById(id).orElseThrow();
    o.setStatus(status);
    orders.save(o);
    return "redirect:/admin/orders/" + id;
  }

  @GetMapping("/sheep")
  String sheep(Model m) {
    m.addAttribute(
        "enquiries", sheep.findAll(org.springframework.data.domain.Sort.by("id").descending()));
    return "admin/sheep";
  }

  @PostMapping("/sheep/{id}")
  String quote(
      @PathVariable Long id,
      @RequestParam String status,
      @RequestParam(required = false) BigDecimal estimatedPrice,
      @RequestParam String pricingBasis,
      @RequestParam String ownerNotes) {
    if (!List.of("Requested", "Contacted", "Quoted", "Closed", "Cancelled").contains(status)
        || pricingBasis.isBlank()
        || pricingBasis.length() > 120
        || ownerNotes.length() > 1500
        || estimatedPrice != null
            && (estimatedPrice.signum() < 0
                || estimatedPrice.scale() > 2
                || estimatedPrice.compareTo(new BigDecimal("99999999")) > 0))
      throw new IllegalArgumentException("Invalid quote details.");
    var e = sheep.findById(id).orElseThrow();
    e.setStatus(status);
    e.setEstimatedPrice(estimatedPrice);
    e.setPricingBasis(pricingBasis);
    e.setOwnerNotes(ownerNotes);
    sheep.save(e);
    return "redirect:/admin/sheep";
  }

  @GetMapping("/settings")
  String settings(Model m) {
    BusinessSettings b = settings.findById(1L).orElseThrow();
    SettingsForm f = new SettingsForm();
    org.springframework.beans.BeanUtils.copyProperties(b, f);
    m.addAttribute("form", f);
    m.addAttribute("areas", areas.findAll());
    return "admin/settings";
  }

  @PostMapping("/settings")
  String settingsSave(
      @Valid @ModelAttribute("form") SettingsForm f,
      BindingResult e,
      @RequestParam(required = false) MultipartFile image,
      Model m)
      throws Exception {
    if (e.hasErrors()) {
      m.addAttribute("areas", areas.findAll());
      return "admin/settings";
    }
    BusinessSettings b = settings.findById(1L).orElseThrow();
    org.springframework.beans.BeanUtils.copyProperties(f, b);
    if (image != null && !image.isEmpty()) b.setFarmImage(images.save(image));
    settings.save(b);
    return "redirect:/admin/settings";
  }

  @PostMapping("/areas")
  String area(
      @RequestParam(required = false) Long id,
      @RequestParam String name,
      @RequestParam BigDecimal fee,
      @RequestParam(defaultValue = "false") boolean active) {
    if (name.isBlank()
        || name.length() > 120
        || fee.signum() < 0
        || fee.scale() > 2
        || fee.compareTo(new BigDecimal("99999")) > 0)
      throw new IllegalArgumentException("Invalid delivery area or fee.");
    DeliveryArea a = id == null ? new DeliveryArea() : areas.findById(id).orElseThrow();
    a.setName(name);
    a.setFee(fee);
    a.setActive(active);
    areas.save(a);
    return "redirect:/admin/settings";
  }
}
