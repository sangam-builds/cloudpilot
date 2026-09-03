-- ====================================================================
-- CloudPilot Seed Data
-- ====================================================================

-- 1. Insert Teams
INSERT INTO teams (id, name, description) VALUES
(1, 'Payments & Billing', 'Handles checkout failures, invoice disputes, double charges, and refund requests'),
(2, 'Technical Support', 'Resolves system errors, API integrations, bugs, outages, and configuration issues'),
(3, 'Shipping & Logistics', 'Tracks shipments, customs delays, warehouse fulfillment, and damaged deliveries'),
(4, 'Account & Security', 'Assists with 2FA resets, password lockouts, SSO configurations, and fraud detection'),
(5, 'Returns & Refunds', 'Processes return merchandise authorization (RMA), store credit, and return inspections')
ON CONFLICT (id) DO NOTHING;

-- 2. Insert Agents
INSERT INTO agents (id, name, email, team_id, skill_tags, rating, is_available, current_workload) VALUES
(1, 'Alex Mercer', 'alex.mercer@cloudpilot.io', 1, 'refunds,stripe,invoicing,chargebacks', 4.95, true, 2),
(2, 'Sarah Connor', 'sarah.connor@cloudpilot.io', 1, 'tax,wire-transfers,subscriptions', 4.80, true, 1),
(3, 'David Chen', 'david.chen@cloudpilot.io', 2, 'api,oauth,cloud,database,performance', 4.90, true, 3),
(4, 'Elena Rostova', 'elena.rostova@cloudpilot.io', 2, 'frontend,react,mobile-app,websocket', 4.75, true, 1),
(5, 'Marcus Vance', 'marcus.vance@cloudpilot.io', 3, 'fedex,dhl,customs,international-freight', 4.88, true, 2),
(6, 'Priya Sharma', 'priya.sharma@cloudpilot.io', 3, 'warehousing,tracking,same-day-delivery', 4.92, true, 0),
(7, 'Liam O''Connor', 'liam.oconnor@cloudpilot.io', 4, 'sso,okta,saml,2fa,security-audit', 4.98, true, 1),
(8, 'Yuki Tanaka', 'yuki.tanaka@cloudpilot.io', 4, 'access-control,gdpr,compliance,phishing', 4.85, false, 4),
(9, 'Chloe Bennett', 'chloe.bennett@cloudpilot.io', 5, 'rma,damaged-goods,warranty,exchanges', 4.70, true, 2),
(10, 'Jamal Williams', 'jamal.williams@cloudpilot.io', 5, 'restocking,store-credit,appraisals', 4.89, true, 1)
ON CONFLICT (id) DO NOTHING;

-- 3. Insert Customers
INSERT INTO customers (id, name, email, phone) VALUES
(1, 'Acme Corporation (John Doe)', 'john.doe@acme.com', '+1-555-0101'),
(2, 'TechCorp Global (Jane Smith)', 'jane.smith@techcorp.io', '+1-555-0102'),
(3, 'Omni Logistics (Robert Green)', 'robert@omnilogistics.com', '+1-555-0103'),
(4, 'Nova Financials (Emily White)', 'emily.white@novafin.com', '+1-555-0104'),
(5, 'Apex Cloud Solutions (Michael Brown)', 'mbrown@apexcloud.org', '+1-555-0105'),
(6, 'Vanguard Retailers (Sophia Davis)', 'sophia.d@vanguardretail.com', '+1-555-0106'),
(7, 'Starlight Media (Daniel Miller)', 'dmiller@starlightmedia.co', '+1-555-0107'),
(8, 'Zenith BioTech (Olivia Wilson)', 'olivia.w@zenithbio.io', '+1-555-0108'),
(9, 'Helios Energy (Lucas Moore)', 'lucas@heliosenergy.com', '+1-555-0109'),
(10, 'Quantum Robotics (Ava Taylor)', 'ava.t@quantumrobotics.ai', '+1-555-0110'),
(11, 'Atlas Dynamics (Ethan Thomas)', 'ethan@atlasdynamics.net', '+1-555-0111'),
(12, 'Summit Ventures (Isabella Jackson)', 'isabella@summitvc.com', '+1-555-0112')
ON CONFLICT (id) DO NOTHING;

-- 4. Insert Orders
INSERT INTO orders (id, customer_id, amount, status, created_at) VALUES
(1, 1, 12500.00, 'COMPLETED', NOW() - INTERVAL '45 days'),
(2, 1, 4500.00, 'COMPLETED', NOW() - INTERVAL '15 days'),
(3, 1, 7800.00, 'PROCESSING', NOW() - INTERVAL '2 days'),
(4, 2, 28900.00, 'COMPLETED', NOW() - INTERVAL '90 days'),
(5, 2, 15400.00, 'COMPLETED', NOW() - INTERVAL '30 days'),
(6, 3, 5300.00, 'COMPLETED', NOW() - INTERVAL '60 days'),
(7, 3, 6200.00, 'COMPLETED', NOW() - INTERVAL '20 days'),
(8, 4, 45000.00, 'COMPLETED', NOW() - INTERVAL '120 days'),
(9, 4, 32000.00, 'COMPLETED', NOW() - INTERVAL '40 days'),
(10, 5, 8900.00, 'COMPLETED', NOW() - INTERVAL '50 days'),
(11, 5, 11200.00, 'COMPLETED', NOW() - INTERVAL '10 days'),
(12, 6, 3400.00, 'REFUNDED', NOW() - INTERVAL '35 days'),
(13, 6, 4200.00, 'COMPLETED', NOW() - INTERVAL '5 days'),
(14, 7, 19500.00, 'COMPLETED', NOW() - INTERVAL '80 days'),
(15, 8, 87000.00, 'COMPLETED', NOW() - INTERVAL '100 days'),
(16, 9, 22400.00, 'COMPLETED', NOW() - INTERVAL '70 days'),
(17, 10, 64000.00, 'COMPLETED', NOW() - INTERVAL '25 days'),
(18, 11, 14300.00, 'COMPLETED', NOW() - INTERVAL '18 days'),
(19, 12, 9800.00, 'COMPLETED', NOW() - INTERVAL '12 days'),
(20, 2, 1999.00, 'COMPLETED', NOW() - INTERVAL '1 day'),
(21, 4, 15000.00, 'PROCESSING', NOW() - INTERVAL '6 hours'),
(22, 8, 45000.00, 'COMPLETED', NOW() - INTERVAL '14 days')
ON CONFLICT (id) DO NOTHING;

-- 5. Insert Tickets
INSERT INTO tickets (id, customer_id, subject, description, category, priority, sentiment, status, assigned_agent_id, sla_deadline, risk_status, created_at) VALUES
(1, 1, 'Double charge on March invoice #INV-9481', 'We noticed our corporate credit card was billed twice ($7,800 x 2) for our monthly subscription. Please reverse the duplicate charge immediately.', 'Payments', 'HIGH', 'FRUSTRATED', 'IN_PROGRESS', 1, NOW() + INTERVAL '1 hour', 'AT_RISK', NOW() - INTERVAL '1 hour'),
(2, 2, 'API Rate limit 429 errors during batch sync', 'Our ETL pipeline is receiving unexpected 429 Too Many Requests errors even though we are within our contracted 5,000 req/min quota.', 'Technical Support', 'HIGH', 'NEGATIVE', 'ASSIGNED', 3, NOW() + INTERVAL '45 minutes', 'AT_RISK', NOW() - INTERVAL '75 minutes'),
(3, 3, 'Pallet delivery delayed at Frankfurt customs', 'Tracking ID #DHL-884919 is held at Frankfurt airport hub due to missing commercial invoice declaration.', 'Shipping & Logistics', 'HIGH', 'NEGATIVE', 'IN_PROGRESS', 5, NOW() + INTERVAL '3 hours', 'ON_TRACK', NOW() - INTERVAL '30 minutes'),
(4, 4, 'Need SSO SAML certificate renewal before expiry', 'Our Okta enterprise certificate expires in 72 hours. We require CloudPilot SSO assertion metadata updated.', 'Account & Security', 'MEDIUM', 'NEUTRAL', 'ASSIGNED', 7, NOW() + INTERVAL '6 hours', 'ON_TRACK', NOW() - INTERVAL '2 hours'),
(5, 5, 'Request for refund on unused annual license seats', 'Due to team downsizing we would like to adjust our seat count from 50 to 30 and obtain a pro-rated credit.', 'Returns & Refunds', 'LOW', 'NEUTRAL', 'NEW', NULL, NOW() + INTERVAL '22 hours', 'ON_TRACK', NOW() - INTERVAL '2 hours'),
(6, 6, 'Damaged packaging on hardware shipment #ORD-991', 'The server chassis arrived with dented outer casing. Requesting RMA and expedited replacement unit.', 'Returns & Refunds', 'HIGH', 'NEGATIVE', 'ASSIGNED', 9, NOW() - INTERVAL '30 minutes', 'BREACHED', NOW() - INTERVAL '3 hours'),
(7, 7, 'Webhook event signatures failing HMAC validation', 'Our receiver is rejecting payload signatures with invalid digest matching secret key.', 'Technical Support', 'MEDIUM', 'NEUTRAL', 'IN_PROGRESS', 4, NOW() + INTERVAL '5 hours', 'ON_TRACK', NOW() - INTERVAL '3 hours'),
(8, 8, 'Requesting SOC2 Type II compliance audit report', 'Our security governance team requires your latest SOC2 compliance packet for our annual vendor review.', 'Account & Security', 'LOW', 'POSITIVE', 'RESOLVED', 7, NOW() - INTERVAL '10 hours', 'ON_TRACK', NOW() - INTERVAL '18 hours'),
(9, 9, 'Billing currency conversion discrepancy', 'The invoice issued in EUR used an incorrect spot exchange rate compared to European Central Bank rate.', 'Payments', 'MEDIUM', 'NEUTRAL', 'RESOLVED', 2, NOW() - INTERVAL '4 hours', 'ON_TRACK', NOW() - INTERVAL '12 hours'),
(10, 10, 'Urgent: Production cluster timeout on EU-West region', 'All microservice calls to eu-west-1 gateway endpoint are throwing 504 Gateway Timeout errors.', 'Technical Support', 'HIGH', 'FRUSTRATED', 'IN_PROGRESS', 3, NOW() + INTERVAL '20 minutes', 'AT_RISK', NOW() - INTERVAL '100 minutes'),
(11, 11, 'Tracking status not updating for order #ORD-848', 'The tracking portal shows label created for 4 days without carrier scan.', 'Shipping & Logistics', 'MEDIUM', 'NEGATIVE', 'NEW', NULL, NOW() + INTERVAL '7 hours', 'ON_TRACK', NOW() - INTERVAL '1 hour'),
(12, 12, 'Adding 10 new developer seats to workspace', 'We need to upgrade our tier and assign licenses to our newly onboarded engineering cohort.', 'Payments', 'LOW', 'POSITIVE', 'RESOLVED', 1, NOW() - INTERVAL '20 hours', 'ON_TRACK', NOW() - INTERVAL '24 hours')
ON CONFLICT (id) DO NOTHING;

-- 6. Insert Ticket History
INSERT INTO ticket_history (ticket_id, from_status, to_status, changed_by, changed_at) VALUES
(1, 'NEW', 'ASSIGNED', 'System Auto-Assigner', NOW() - INTERVAL '55 minutes'),
(1, 'ASSIGNED', 'IN_PROGRESS', 'Alex Mercer', NOW() - INTERVAL '40 minutes'),
(2, 'NEW', 'ASSIGNED', 'System Auto-Assigner', NOW() - INTERVAL '70 minutes'),
(6, 'NEW', 'ASSIGNED', 'System Auto-Assigner', NOW() - INTERVAL '2 hours 50 minutes'),
(8, 'NEW', 'ASSIGNED', 'System Auto-Assigner', NOW() - INTERVAL '17 hours'),
(8, 'ASSIGNED', 'RESOLVED', 'Liam O''Connor', NOW() - INTERVAL '10 hours'),
(9, 'NEW', 'ASSIGNED', 'System Auto-Assigner', NOW() - INTERVAL '11 hours'),
(9, 'ASSIGNED', 'RESOLVED', 'Sarah Connor', NOW() - INTERVAL '4 hours')
ON CONFLICT DO NOTHING;

-- 7. Insert FAQs (Knowledge Base for RAG)
INSERT INTO faqs (id, category, question, answer) VALUES
(1, 'Payments', 'How do I request a refund for an accidental duplicate transaction?', 'To request a duplicate charge refund, navigate to Billing > Invoices, locate the transaction ID, and select Request Refund. Our automated billing engine will verify the authorization and credit your payment method within 3–5 business days.'),
(2, 'Payments', 'Which payment methods and currencies are supported?', 'CloudPilot supports Visa, MasterCard, American Express, ACH direct debits, and SEPA wire transfers in USD, EUR, GBP, CAD, and JPY.'),
(3, 'Payments', 'How do I update my organization tax ID or VAT exemption certificate?', 'Go to Settings > Billing Profile > Tax Configuration. Upload your VAT / GST registration certificate for review. Verification takes up to 24 hours.'),
(4, 'Technical Support', 'What should I do if I encounter HTTP 429 Too Many Requests?', 'HTTP 429 indicates rate limiting. Verify your subscription tier burst limits. Implement exponential backoff retry algorithms with jitter in your client SDK, or contact support to request a quota increase.'),
(5, 'Technical Support', 'How do I verify webhook cryptographic signatures?', 'Each webhook request includes an X-CloudPilot-Signature header containing an HMAC-SHA256 digest computed using your webhook signing secret. Compute the digest over the raw request body and compare using constant-time comparison.'),
(6, 'Technical Support', 'What are the IP addresses for CloudPilot outbound webhook egress?', 'Our outbound webhook traffic originates from 54.210.12.0/24 and 52.88.45.0/24. Ensure your corporate firewall allowlists these CIDR blocks on port 443.'),
(7, 'Shipping & Logistics', 'How do I track international shipments and customs clearance status?', 'Real-time tracking is available in the Logistics Portal under Deliveries. Detailed customs clearance status, commercial invoices, and carrier waybills can be downloaded directly from the shipment summary.'),
(8, 'Shipping & Logistics', 'What is the procedure for reporting damaged or missing packages?', 'Notify CloudPilot Support within 48 hours of delivery scan. Provide high-resolution photos of external packaging, shipping label, and damaged contents to initiate an immediate carrier claim and expedited replacement.'),
(9, 'Account & Security', 'How do I configure Single Sign-On (SSO) with Okta, Azure AD, or Google Workspace?', 'Navigate to Admin Console > Security > SSO. CloudPilot supports SAML 2.0 and OpenID Connect (OIDC). Download our SP metadata XML, configure your Identity Provider, and upload your IdP certificate.'),
(10, 'Account & Security', 'What happens if a user is locked out after multiple failed 2FA attempts?', 'Workspace administrators can issue a temporary one-time bypass token from Admin > Users > Security Settings. Users can also authenticate via emergency recovery codes generated during 2FA setup.'),
(11, 'Returns & Refunds', 'What is the Return Merchandise Authorization (RMA) process for hardware?', 'Submit an RMA request via the Support Center with the hardware serial number. Upon review, a prepaid return shipping label and packing slip will be issued. Replacements are dispatched upon receipt scan.'),
(12, 'Returns & Refunds', 'Are digital software licenses refundable?', 'Digital license subscriptions can be cancelled for a full prorated refund within 14 days of purchase. After 14 days, cancellation takes effect at the end of the current billing cycle.')
ON CONFLICT (id) DO NOTHING;

-- 8. Insert Audit Logs
INSERT INTO audit_logs (actor_id, actor_role, action, entity_type, entity_id, details) VALUES
('system', 'SYSTEM', 'SEED_INITIALIZED', 'DATABASE', 'ALL', '{"message": "Seed data loaded successfully with initial teams, agents, and tickets"}'),
('admin-1', 'ADMIN', 'UPDATE_SLA_POLICY', 'POLICY', 'SLA-CORE', '{"high_sla_hours": 2, "med_sla_hours": 8, "low_sla_hours": 24}'),
('agent-1', 'AGENT', 'STATUS_CHANGE', 'TICKET', '1', '{"from": "ASSIGNED", "to": "IN_PROGRESS"}'),
('agent-7', 'AGENT', 'RESOLVE_TICKET', 'TICKET', '8', '{"resolution": "Attached SOC2 Type II compliance audit PDF package"}')
ON CONFLICT DO NOTHING;

-- Reset sequence IDs for PostgreSQL
SELECT setval('customers_id_seq', (SELECT COALESCE(MAX(id), 1) FROM customers));
SELECT setval('orders_id_seq', (SELECT COALESCE(MAX(id), 1) FROM orders));
SELECT setval('teams_id_seq', (SELECT COALESCE(MAX(id), 1) FROM teams));
SELECT setval('agents_id_seq', (SELECT COALESCE(MAX(id), 1) FROM agents));
SELECT setval('tickets_id_seq', (SELECT COALESCE(MAX(id), 1) FROM tickets));
SELECT setval('faqs_id_seq', (SELECT COALESCE(MAX(id), 1) FROM faqs));
SELECT setval('ticket_history_id_seq', (SELECT COALESCE(MAX(id), 1) FROM ticket_history));
SELECT setval('audit_logs_id_seq', (SELECT COALESCE(MAX(id), 1) FROM audit_logs));
