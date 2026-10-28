# Owner guide

## Before real customers use the site

Confirm your phone/WhatsApp, business hours, product photos, product descriptions, actual prices and units. Confirm chicken weight options and egg pack quantities. The starter egg pack and chicken option are preview examples, not claims about the real business.

Use only authorised photos. Product uploads and the farm photo accept JPG/PNG under 3 MB and at most 4000 × 4000 pixels. The server validates the file and re-encodes it as PNG under a random filename. Old images remain available for backup; they are not automatically deleted. Logo replacement is in `templates/fragments.html` and `static/images/mark.svg`. Image sources and placeholder status are documented in `static/images/SOURCES.md`.

Never add unapproved “100% pure milk” text, certifications, awards, customer reviews, health claims or invented delivery promises. The supplied site makes none of these claims.

## Products and options

Products → Edit product controls name, category, description, image, visibility and preview label. Hiding a product removes it from the customer catalogue and blocks it during checkout. Keep the preview label enabled until the owner reviews that product.

Add/Edit option controls the size or pack, unit, exact option price, availability and optional glass-container deposit. Leave price blank to show Contact for price. Deposits are disabled by default. When the owner enables a deposit, it is shown separately per item and included in the request total. Document the return/refund terms in your product description before enabling it. There is no automatic deposit refund workflow.

Use separate 500 g / 1 kg chicken options or 6 / 12 egg packs as needed. Quantities count the chosen sized option. No fractional number of bottles or packs is accepted.

## Orders

An order is a request until you confirm it. Open Orders → reference to review contact information, requested date, fulfilment, payment preference and historical product/price snapshots. Statuses are Requested, Confirmed, Preparing, Ready, Completed and Cancelled. Print summary opens the browser print dialog with a simplified layout. Status updates do not send messages.

When prices change before submission, customers must review and submit again. Unavailable or unpriced options are blocked. Repeated submissions of the same checkout token return the saved request. A new checkout starts a new request; customers should keep their reference to avoid placing a second intentional request.

Dashboard completed value counts completed non-preview orders, not collected revenue or payment receipts. There is no payment gateway or payment ledger.

## Delivery and payment

Delivery is disabled initially. Add supported areas and explicit fees under Settings, then enable delivery. Only active areas are offered; an address is required only for delivery. These area labels are customer-selected. The owner must verify the supplied address lies in the chosen area before confirmation; no postcode/geographic validation is performed.

Cash and UPI on confirmation are optional owner-enabled preferences. No UPI ID is invented or used for automatic collection. Actual payment arrangements happen directly with the owner.

## Whole sheep

Review preferred date, occasion, sheep count, preferred weight and notes. Confirm what the quoted weight means. Update pricing basis only after you decide whether it is live weight or another basis. Enter a manually calculated **Estimated price** and notes. No automated weight-rate computation, processing, slaughtering or transport promise is included. Contact the customer yourself; saving a quote does not send it.

## Contact and privacy

Blank phone, WhatsApp, hours or social fields stay hidden. WhatsApp uses country code and digits only. Social links accept Instagram/Facebook HTTPS profile URLs. The optional post-order WhatsApp button opens a message draft only; the customer must press Send. The database save is independent of WhatsApp.

Keep customer data private. Back up data and images together with the application stopped. Determine a record retention policy, and arrange deletion/export procedures before public use. This version has no self-service customer account, automated retention or bulk export. Admin access is required to inspect personal records. Do not put runtime data or credentials in GitHub.

The application binds only to this computer by default. Public hosting must be reviewed separately, including appropriate business obligations and security settings. No deployment has been performed.
