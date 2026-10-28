package com.vichumeats.service;

import com.vichumeats.dto.*;
import com.vichumeats.entity.*;
import com.vichumeats.repository.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShopService {
  private final ProductRepository products;
  private final VariantRepository variants;
  private final CustomerOrderRepository orders;
  private final SheepEnquiryRepository sheep;
  private final BusinessSettingsRepository settings;
  private final DeliveryAreaRepository areas;

  public BusinessSettings settings() {
    return settings.findById(1L).orElseGet(BusinessSettings::new);
  }

  public List<Product> products() {
    return products.findAll().stream().filter(Product::isActive).toList();
  }

  public record Line(
      Long variantId,
      String name,
      String label,
      String unit,
      int quantity,
      BigDecimal price,
      BigDecimal deposit,
      BigDecimal total) {}

  public List<Line> lines(Map<Long, Integer> cart) {
    List<Line> lines = new ArrayList<>();
    if (cart.size() > 40) throw new IllegalArgumentException("Cart is too large.");
    for (var entry : new TreeMap<>(cart).entrySet()) {
      Variant v =
          variants
              .findById(entry.getKey())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "A cart option no longer exists. Remove it and try again."));
      check(v, entry.getValue());
      BigDecimal d = v.isDepositEnabled() ? v.getDeposit() : BigDecimal.ZERO;
      lines.add(
          new Line(
              v.getId(),
              v.getProduct().getName(),
              v.getLabel(),
              v.getUnit(),
              entry.getValue(),
              v.getPrice(),
              d,
              v.getPrice().add(d).multiply(BigDecimal.valueOf(entry.getValue()))));
    }
    return lines;
  }

  private void check(Variant v, int q) {
    if (!v.isAvailable() || !v.getProduct().isActive())
      throw new IllegalArgumentException("A product is unavailable. Update your cart.");
    if (v.getPrice() == null || v.getPrice().signum() <= 0)
      throw new IllegalArgumentException("Contact for price. This option cannot be ordered yet.");
    if (q < 1 || q > 100) throw new IllegalArgumentException("Quantity must be between 1 and 100.");
  }

  public BigDecimal subtotal(List<Line> l) {
    return l.stream().map(Line::total).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  @Transactional
  public synchronized CustomerOrder place(
      CheckoutForm f, Map<Long, Integer> cart, String sessionToken, BigDecimal reviewedSubtotal) {
    if (!Objects.equals(f.getToken(), sessionToken))
      throw new IllegalArgumentException("This checkout has expired. Open the cart again.");
    var existing = orders.findBySubmissionKey(f.getToken());
    if (existing.isPresent()) return existing.get();
    if (cart.isEmpty()) throw new IllegalArgumentException("Your cart is empty.");
    for (Long id : new TreeSet<>(cart.keySet()))
      variants.lockById(id).orElseThrow(() -> new IllegalArgumentException("Product removed."));
    List<Line> lines = lines(cart);
    BigDecimal sub = subtotal(lines);
    if (reviewedSubtotal == null || sub.compareTo(reviewedSubtotal) != 0)
      throw new IllegalArgumentException(
          "Prices changed. Review your cart before submitting again.");
    BusinessSettings b = settings();
    BigDecimal fee = BigDecimal.ZERO;
    DeliveryArea area = null;
    if ("Delivery".equals(f.getFulfilment())) {
      if (!b.isDeliveryEnabled()) throw new IllegalArgumentException("Delivery is not enabled.");
      if (f.getAddress() == null || f.getAddress().isBlank())
        throw new IllegalArgumentException("Delivery address is required.");
      if (f.getAreaId() == null)
        throw new IllegalArgumentException("Choose a supported delivery area.");
      area =
          areas
              .findById(f.getAreaId())
              .filter(DeliveryArea::isActive)
              .orElseThrow(() -> new IllegalArgumentException("Unsupported delivery area."));
      fee = area.getFee();
    }
    if (!"Not selected".equals(f.getPayment())
        && !(b.isCashEnabled() && "Cash".equals(f.getPayment()))
        && !(b.isUpiEnabled() && "UPI on confirmation".equals(f.getPayment())))
      throw new IllegalArgumentException("Payment option is not enabled.");
    if (f.getRequestedDate().isBefore(LocalDate.now(ZoneId.of("Asia/Kolkata"))))
      throw new IllegalArgumentException("Choose today or a future date.");
    CustomerOrder o = new CustomerOrder();
    o.setReference("VM-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
    o.setSubmissionKey(f.getToken());
    o.setCustomerName(f.getCustomerName());
    o.setPhone(f.getPhone());
    o.setFulfilment(f.getFulfilment());
    o.setAddress(area == null ? null : f.getAddress());
    o.setDeliveryArea(area == null ? null : area.getName());
    o.setRequestedDate(f.getRequestedDate());
    o.setNotes(f.getNotes());
    o.setPayment(f.getPayment());
    o.setSubtotal(sub);
    o.setDeliveryFee(fee);
    o.setTotal(sub.add(fee));
    for (Line l : lines) {
      OrderItem i = new OrderItem();
      i.setProductName(l.name());
      i.setVariantLabel(l.label());
      i.setUnit(l.unit());
      i.setQuantity(l.quantity());
      i.setUnitPrice(l.price());
      i.setDeposit(l.deposit());
      i.setLineTotal(l.total());
      o.getItems().add(i);
    }
    o.setDemo(
        lines.stream()
            .anyMatch(l -> variants.findById(l.variantId()).orElseThrow().getProduct().isDemo()));
    return orders.save(o);
  }

  @Transactional
  public synchronized SheepEnquiry enquire(SheepForm f, String token) {
    if (!Objects.equals(token, f.getToken()))
      throw new IllegalArgumentException("This form expired. Please open it again.");
    var old = sheep.findBySubmissionKey(token);
    if (old.isPresent()) return old.get();
    SheepEnquiry e = new SheepEnquiry();
    e.setReference("VS-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
    e.setSubmissionKey(token);
    e.setCustomerName(f.getCustomerName());
    e.setPhone(f.getPhone());
    e.setPreferredDate(f.getPreferredDate());
    e.setFestival(f.getFestival());
    e.setSheepCount(f.getSheepCount());
    e.setWeightRange(f.getWeightRange());
    e.setNotes(f.getNotes());
    e.setPricingBasis(settings().getSheepBasis());
    return sheep.save(e);
  }
}
