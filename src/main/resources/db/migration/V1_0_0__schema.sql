CREATE SEQUENCE account_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


CREATE TABLE account (
    id integer DEFAULT nextval('account_seq'::regclass) NOT NULL,
    name character varying(100)
);


--
-- Name: billable_charge_seq; Type: SEQUENCE; Schema: public; Owner: datapopulator
--

CREATE SEQUENCE billable_charge_seq
    START WITH 101
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: billable_charge; Type: TABLE; Schema: public; Owner: datapopulator; Tablespace: 
--

CREATE TABLE billable_charge (
    id integer DEFAULT nextval('billable_charge_seq'::regclass) NOT NULL,
    account_id integer NOT NULL,
    amount money NOT NULL
);



--
-- Name: invoice_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE invoice_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: invoice; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE invoice (
    id integer DEFAULT nextval('invoice_seq'::regclass) NOT NULL,
    amount money NOT NULL,
    account_id integer NOT NULL
);


--
-- Name: invoice_item_seq; Type: SEQUENCE; Schema: public; Owner: datapopulator
--

CREATE SEQUENCE invoice_item_seq
    START WITH 101
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;




--
-- Name: invoice_item; Type: TABLE; Schema: public; Owner: datapopulator; Tablespace: 
--

CREATE TABLE invoice_item (
    id integer DEFAULT nextval('invoice_item_seq'::regclass) NOT NULL,
    account_id integer NOT NULL,
    amount money NOT NULL,
    invoice_id integer NOT NULL,
    billable_charge_id integer NOT NULL
);



--
-- Name: invoice_status; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE invoice_status (
    id integer NOT NULL,
    name character varying(20) NOT NULL,
    description text
);



--
-- Name: payment_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE payment_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;



--
-- Name: payment; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE payment (
    id integer DEFAULT nextval('payment_seq'::regclass) NOT NULL,
    invoice_id integer NOT NULL,
    amount money NOT NULL,
    status integer
);



--
-- Name: payment_status; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE payment_status (
    id integer NOT NULL,
    name character varying(20) NOT NULL,
    description text
);



--
-- Name: refund_seq; Type: SEQUENCE; Schema: public; Owner: datapopulator
--

CREATE SEQUENCE refund_seq
    START WITH 100
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;



--
-- Name: refund; Type: TABLE; Schema: public; Owner: datapopulator; Tablespace: 
--

CREATE TABLE refund (
    id integer DEFAULT nextval('refund_seq'::regclass) NOT NULL,
    account_id integer NOT NULL,
    amount money NOT NULL,
    payment_id integer NOT NULL
);


--
-- Name: account_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY account
    ADD CONSTRAINT account_pkey PRIMARY KEY (id);


--
-- Name: billable_charge_pkey; Type: CONSTRAINT; Schema: public; Owner: datapopulator; Tablespace:
--

ALTER TABLE ONLY billable_charge
    ADD CONSTRAINT billable_charge_pkey PRIMARY KEY (id);


--
-- Name: invoice_item_pkey; Type: CONSTRAINT; Schema: public; Owner: datapopulator; Tablespace:
--

ALTER TABLE ONLY invoice_item
    ADD CONSTRAINT invoice_item_pkey PRIMARY KEY (id);


--
-- Name: invoice_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY invoice
    ADD CONSTRAINT invoice_pkey PRIMARY KEY (id);


--
-- Name: invoice_status_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY invoice_status
    ADD CONSTRAINT invoice_status_pkey PRIMARY KEY (id);


--
-- Name: payment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY payment
    ADD CONSTRAINT payment_pkey PRIMARY KEY (id);


--
-- Name: payment_status_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY payment_status
    ADD CONSTRAINT payment_status_pkey PRIMARY KEY (id);


--
-- Name: refund_pkey; Type: CONSTRAINT; Schema: public; Owner: datapopulator; Tablespace:
--

ALTER TABLE ONLY refund
    ADD CONSTRAINT refund_pkey PRIMARY KEY (id);


--
-- Name: billable_charge_account_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: datapopulator
--

ALTER TABLE ONLY billable_charge
    ADD CONSTRAINT billable_charge_account_id_fkey FOREIGN KEY (account_id) REFERENCES account(id);


--
-- Name: invoice_account_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY invoice
    ADD CONSTRAINT invoice_account_id_fkey FOREIGN KEY (account_id) REFERENCES account(id);


--
-- Name: invoice_item_account_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: datapopulator
--

ALTER TABLE ONLY invoice_item
    ADD CONSTRAINT invoice_item_account_id_fkey FOREIGN KEY (account_id) REFERENCES account(id);


--
-- Name: invoice_item_billable_charge_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: datapopulator
--

ALTER TABLE ONLY invoice_item
    ADD CONSTRAINT invoice_item_billable_charge_id_fkey FOREIGN KEY (billable_charge_id) REFERENCES billable_charge(id);


--
-- Name: invoice_item_invoice_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: datapopulator
--

ALTER TABLE ONLY invoice_item
    ADD CONSTRAINT invoice_item_invoice_id_fkey FOREIGN KEY (invoice_id) REFERENCES invoice(id);


--
-- Name: payment_invoice_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY payment
    ADD CONSTRAINT payment_invoice_id_fkey FOREIGN KEY (invoice_id) REFERENCES invoice(id);


--
-- Name: payment_status_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY payment
    ADD CONSTRAINT payment_status_fkey FOREIGN KEY (status) REFERENCES payment_status(id);


--
-- Name: refund_account_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: datapopulator
--

ALTER TABLE ONLY refund
    ADD CONSTRAINT refund_account_id_fkey FOREIGN KEY (account_id) REFERENCES account(id);


--
-- Name: refund_payment_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: datapopulator
--

ALTER TABLE ONLY refund
    ADD CONSTRAINT refund_payment_id_fkey FOREIGN KEY (payment_id) REFERENCES payment(id);
