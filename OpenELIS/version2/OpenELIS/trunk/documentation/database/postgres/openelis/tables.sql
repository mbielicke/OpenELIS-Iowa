-------------------------------------------------------------------------------
--
-- tables
--
-------------------------------------------------------------------------------

create table address
(
    id                             serial not null,
    multiple_unit                  varchar(30),
    street_address                 varchar(30),
    city                           varchar(30),
    state                          char(2),
    zip_code                       varchar(10),
    work_phone                     varchar(21),
    home_phone                     varchar(16),
    cell_phone                     varchar(16),
    fax_phone                      varchar(16),
    email                          varchar(80),
    country                        varchar(30)
);

create table analysis
(
    id                             serial not null,
    sample_item_id                 integer not null,
    revision                       integer not null,
    test_id                        integer not null,
    section_id                     integer,
    panel_id                       integer,
    pre_analysis_id                integer,
    parent_analysis_id             integer,
    parent_result_id               integer,
    type_id                        integer,
    is_reportable                  char(1) not null,
    unit_of_measure_id             integer,
    status_id                      integer not null,
    available_date                 timestamp,
    started_date                   timestamp,
    completed_date                 timestamp,
    released_date                  timestamp,
    printed_date                   timestamp
);

create table analysis_qaevent
(
    id                             serial not null,
    analysis_id                    integer not null,
    qaevent_id                     integer not null,
    type_id                        integer not null,
    is_billable                    char(1) not null
);

create table analysis_report_flags
(
    analysis_id                    integer not null,
    notified_received              char(1),
    notified_released              char(1),
    billed_date                    date,
    billed_analytes                smallint,
    billed_override                decimal(8,2)
);

create table analysis_user
(
    id                             serial not null,
    analysis_id                    integer not null,
    system_user_id                 integer not null,
    action_id                      integer
);

create table analyte
(
    id                             serial not null,
    name                           varchar(60) not null,
    is_active                      char(1) not null,
    parent_analyte_id              integer,
    external_id                    varchar(20)
);

create table analyte_parameter
(
    id                             serial not null,
    reference_id                   integer not null,
    reference_table_id             integer not null,
    analyte_id                     integer not null,
    type_of_sample_id              integer,
    unit_of_measure_id             integer,
    active_begin                   timestamp not null,
    active_end                     timestamp not null,
    p1                             float,
    p2                             float,
    p3                             float
);

create table attachment
(
    id                             serial not null,
    created_date                   timestamp not null,
    type_id                        integer,
    section_id                     integer,
    description                    varchar(80),
    storage_reference              varchar(255) not null
);

create table attachment_issue
(
    id                             serial not null,
    attachment_id                  integer not null,
    timestamp                      timestamp not null,
    system_user_id                 integer not null,
    text                           varchar(255) not null
);

create table attachment_item
(
    id                             serial not null,
    reference_id                   integer not null,
    reference_table_id             integer not null,
    attachment_id                  integer not null
);

create table aux_data
(
    id                             serial not null,
    sort_order                     integer not null,
    aux_field_id                   integer not null,
    reference_id                   integer not null,
    reference_table_id             integer not null,
    is_reportable                  char(1),
    type_id                        integer,
    value                          varchar(80)
);

create table aux_field
(
    id                             serial not null,
    aux_field_group_id             integer not null,
    sort_order                     integer not null,
    analyte_id                     integer not null,
    description                    varchar(60),
    method_id                      integer,
    unit_of_measure_id             integer,
    is_required                    char(1),
    is_active                      char(1),
    is_reportable                  char(1),
    scriptlet_id                   integer
);

create table aux_field_group
(
    id                             serial not null,
    name                           varchar(20) not null,
    description                    varchar(60),
    is_active                      char(1) not null,
    active_begin                   date not null,
    active_end                     date not null
);

create table aux_field_value
(
    id                             serial not null,
    aux_field_id                   integer not null,
    type_id                        integer not null,
    value                          varchar(80)
);

create table category
(
    id                             serial not null,
    system_name                    varchar(30) not null,
    name                           varchar(50),
    description                    varchar(60),
    section_id                     integer,
    is_system                      char(1) not null
);

create table cron
(
    id                             serial not null,
    name                           varchar(30) not null,
    is_active                      char(1) not null,
    cron_tab                       varchar(30) not null,
    bean                           varchar(255) not null,
    method                         varchar(255) not null,
    parameters                     varchar(255),
    last_run                       timestamp
);

create table dictionary
(
    id                             serial not null,
    category_id                    integer not null,
    sort_order                     integer not null,
    related_entry_id               integer,
    system_name                    varchar(30),
    is_active                      char(1) not null,
    code                           varchar(10),
    entry                          varchar(255) not null
);

create table eorder
(
    id                             serial not null,
    entered_date                   timestamp not null,
    paper_order_validator          varchar(40) not null,
    description                    varchar(60)
);

create table eorder_body
(
    id                             serial not null,
    eorder_id                      integer not null,
    xml                            text
);

create table eorder_link
(
    id                             serial not null,
    eorder_id                      integer not null,
    reference                      varchar(40),
    sub_id                         varchar(20),
    name                           varchar(20) not null,
    value                          varchar(255)
);

create table event_log
(
    id                             serial not null,
    type_id                        integer not null,
    source                         varchar(60) not null,
    reference_table_id             integer,
    reference_id                   integer,
    level_id                       integer not null,
    system_user_id                 integer not null,
    timestamp                      timestamp not null,
    text                           text
);

create table exchange_criteria
(
    id                             serial not null,
    name                           varchar(20) not null,
    environment_id                 integer,
    destination_uri                varchar(60),
    is_all_analyses_included       char(1) not null,
    query                          text
);

create table exchange_external_term
(
    id                             serial not null,
    exchange_local_term_id         integer not null,
    profile_id                     integer not null,
    is_active                      char(1) not null,
    external_term                  varchar(60) not null,
    external_description           varchar(255),
    external_coding_system         varchar(60),
    version                        varchar(60)
);

create table exchange_local_term
(
    id                             serial not null,
    reference_table_id             integer not null,
    reference_id                   integer not null
);

create table exchange_profile
(
    id                             serial not null,
    exchange_criteria_id           integer not null,
    profile_id                     integer not null,
    sort_order                     integer not null
);

create table history
(
    id                             serial not null,
    reference_id                   integer not null,
    reference_table_id             integer not null,
    timestamp                      timestamp not null,
    activity_id                    integer not null,
    system_user_id                 integer not null,
    changes                        text
);

create table instrument
(
    id                             serial not null,
    name                           varchar(20) not null,
    description                    varchar(60),
    model_number                   varchar(40),
    serial_number                  varchar(40),
    type_id                        integer not null,
    location                       varchar(60) not null,
    is_active                      char(1) not null,
    active_begin                   date,
    active_end                     date,
    scriptlet_id                   integer
);

create table instrument_log
(
    id                             serial not null,
    instrument_id                  integer not null,
    type_id                        integer not null,
    worksheet_id                   integer,
    event_begin                    timestamp not null,
    event_end                      timestamp,
    text                           text
);

create table inventory_adjustment
(
    id                             serial not null,
    description                    varchar(60),
    system_user_id                 integer not null,
    adjustment_date                timestamp not null
);

create table inventory_component
(
    id                             serial not null,
    inventory_item_id              integer not null,
    component_id                   integer not null,
    quantity                       integer not null
);

create table inventory_item
(
    id                             serial not null,
    name                           varchar(30) not null,
    description                    varchar(60),
    category_id                    integer,
    store_id                       integer not null,
    quantity_min_level             integer not null,
    quantity_max_level             integer,
    quantity_to_reorder            integer not null,
    dispensed_units_id             integer,
    is_reorder_auto                char(1) not null,
    is_lot_maintained              char(1) not null,
    is_serial_maintained           char(1) not null,
    is_active                      char(1) not null,
    is_bulk                        char(1) not null,
    is_not_for_sale                char(1) not null,
    is_sub_assembly                char(1) not null,
    is_labor                       char(1) not null,
    is_not_inventoried             char(1) not null,
    product_uri                    varchar(80),
    average_lead_time              integer,
    average_cost                   float,
    average_daily_use              integer,
    parent_inventory_item_id       integer,
    parent_ratio                   integer
);

create table inventory_location
(
    id                             serial not null,
    inventory_item_id              integer not null,
    lot_number                     varchar(20),
    storage_location_id            integer not null,
    quantity_onhand                integer not null,
    expiration_date                date
);

create table inventory_receipt
(
    id                             serial not null,
    inventory_item_id              integer not null,
    iorder_item_id                 integer,
    organization_id                integer,
    received_date                  timestamp not null,
    quantity_received              integer not null,
    unit_cost                      float,
    qc_reference                   varchar(20),
    external_reference             varchar(20),
    upc                            varchar(15)
);

create table inventory_receipt_iorder_item
(
    id                             serial not null,
    inventory_receipt_id           integer not null,
    iorder_item_id                 integer not null
);

create table inventory_x_adjust
(
    id                             serial not null,
    inventory_adjustment_id        integer not null,
    inventory_location_id          integer not null,
    quantity                       integer not null,
    physical_count                 integer not null
);

create table inventory_x_put
(
    id                             serial not null,
    inventory_receipt_id           integer not null,
    inventory_location_id          integer not null,
    quantity                       integer not null
);

create table inventory_x_use
(
    id                             serial not null,
    inventory_location_id          integer not null,
    iorder_item_id                 integer not null,
    quantity                       integer not null
);

create table iorder
(
    id                             serial not null,
    parent_iorder_id               integer,
    description                    varchar(60),
    status_id                      integer not null,
    ordered_date                   date not null,
    needed_in_days                 integer,
    requested_by                   varchar(30),
    cost_center_id                 integer,
    organization_id                integer,
    organization_attention         varchar(30),
    type                           char(1) not null,
    external_order_number          varchar(20),
    ship_from_id                   integer,
    number_of_forms                integer
);

create table iorder_container
(
    id                             serial not null,
    iorder_id                      integer not null,
    container_id                   integer not null,
    item_sequence                  integer not null,
    type_of_sample_id              integer
);

create table iorder_item
(
    id                             serial not null,
    iorder_id                      integer not null,
    inventory_item_id              integer not null,
    quantity                       integer not null,
    catalog_number                 varchar(30),
    unit_cost                      float
);

create table iorder_organization
(
    id                             serial not null,
    iorder_id                      integer not null,
    organization_id                integer not null,
    organization_attention         varchar(30),
    type_id                        integer not null
);

create table iorder_recurrence
(
    id                             serial not null,
    iorder_id                      integer not null,
    is_active                      char(1) not null,
    active_begin                   date,
    active_end                     date,
    frequency                      smallint,
    unit_id                        integer
);

create table iorder_test
(
    id                             serial not null,
    iorder_id                      integer not null,
    item_sequence                  integer not null,
    sort_order                     integer not null,
    test_id                        integer not null
);

create table iorder_test_analyte
(
    id                             serial not null,
    iorder_test_id                 integer not null,
    analyte_id                     integer not null
);

create table label
(
    id                             serial not null,
    name                           varchar(30) not null,
    description                    varchar(60),
    printer_type_id                integer not null,
    scriptlet_id                   integer
);

create table lock
(
    reference_table_id             integer not null,
    reference_id                   integer not null,
    expires                        int8 not null,
    system_user_id                 integer not null,
    session_id                     varchar(80)
);

create table method
(
    id                             serial not null,
    name                           varchar(20) not null,
    description                    varchar(60),
    reporting_description          varchar(60),
    is_active                      char(1) not null,
    active_begin                   date not null,
    active_end                     date not null
);

create table note
(
    id                             serial not null,
    reference_id                   integer not null,
    reference_table_id             integer not null,
    timestamp                      timestamp not null,
    is_external                    char(1) not null,
    system_user_id                 integer not null,
    subject                        varchar(60),
    text                           text
);

create table organization
(
    id                             serial not null,
    parent_organization_id         integer,
    name                           varchar(40) not null,
    is_active                      char(1) not null,
    address_id                     integer
);

create table organization_contact
(
    id                             serial not null,
    organization_id                integer not null,
    contact_type_id                integer not null,
    name                           varchar(30) not null,
    address_id                     integer
);

create table organization_parameter
(
    id                             serial not null,
    organization_id                integer not null,
    type_id                        integer not null,
    value                          varchar(80)
);

create table panel
(
    id                             serial not null,
    name                           varchar(20) not null,
    description                    varchar(60)
);

create table panel_item
(
    id                             serial not null,
    panel_id                       integer not null,
    type                           char(1) not null,
    sort_order                     integer not null,
    name                           varchar(20) not null,
    method_name                    varchar(20)
);

create table patient
(
    id                             serial not null,
    last_name                      varchar(30),
    first_name                     varchar(20),
    middle_name                    varchar(20),
    address_id                     integer,
    birth_date                     date,
    birth_time                     time,
    gender_id                      integer,
    race_id                        integer,
    ethnicity_id                   integer,
    national_id                    varchar(20)
);

create table patient_relation
(
    id                             serial not null,
    relation_id                    integer not null,
    patient_id                     integer not null,
    related_patient_id             integer not null
);

create table preferences
(
    system_user_id                 integer not null,
    text                           text
);

create table project
(
    id                             serial not null,
    name                           varchar(20) not null,
    description                    varchar(60),
    started_date                   date,
    completed_date                 date,
    is_active                      char(1) not null,
    reference_to                   varchar(20),
    owner_id                       integer not null,
    scriptlet_id                   integer
);

create table project_parameter
(
    id                             serial not null,
    project_id                     integer not null,
    parameter                      varchar(80) not null,
    operation_id                   integer not null,
    value                          varchar(255) not null
);

create table provider
(
    id                             serial not null,
    reference_id                   varchar(40),
    reference_source_id            integer,
    last_name                      varchar(30),
    first_name                     varchar(20),
    middle_name                    varchar(20),
    type_id                        integer,
    npi                            varchar(10)
);

create table provider_location
(
    id                             serial not null,
    location                       varchar(50) not null,
    external_id                    varchar(10),
    provider_id                    integer not null,
    address_id                     integer
);

create table pws
(
    id                             serial not null,
    tinwsys_is_number              integer not null,
    number0                        char(12) not null,
    alternate_st_num               char(5),
    name                           varchar(40),
    activity_status_cd             char(1),
    d_prin_city_svd_nm             varchar(40),
    d_prin_cnty_svd_nm             varchar(40),
    d_population_count             integer,
    d_pws_st_type_cd               char(4),
    activity_rsn_txt               varchar(255),
    start_day                      integer,
    start_month                    integer,
    end_day                        integer,
    end_month                      integer,
    eff_begin_dt                   date,
    eff_end_dt                     date
);

create table pws_address
(
    tinwslec_is_number             integer not null,
    tinlgent_is_number             integer not null,
    tinwsys_is_number              integer not null,
    type_code                      char(3),
    active_ind_cd                  char(1),
    name                           varchar(40),
    addr_line_one_txt              varchar(40),
    addr_line_two_txt              varchar(40),
    address_city_name              varchar(40),
    address_state_code             char(2),
    address_zip_code               char(10),
    state_fips_code                char(2),
    phone_number                   char(12)
);

create table pws_facility
(
    tinwsf_is_number               integer not null,
    tsasmppt_is_number             integer not null,
    tinwsys_is_number              integer not null,
    name                           varchar(40),
    type_code                      char(2),
    st_asgn_ident_cd               char(12),
    activity_status_cd             char(1),
    water_type_code                char(3),
    availability_code              char(1),
    identification_cd              char(11),
    description_text               varchar(20),
    source_type_code               char(2)
);

create table pws_monitor
(
    tiamrtask_is_number            integer not null,
    tinwsys_is_number              integer not null,
    st_asgn_ident_cd               char(12),
    name                           varchar(40),
    tiaanlgp_tiaanlyt_name         varchar(64),
    number_samples                 integer,
    comp_begin_date                date,
    comp_end_date                  date,
    frequency_name                 varchar(25),
    period_name                    varchar(20)
);

create table pws_violation
(
    id                             serial not null,
    tinwsys_is_number              integer not null,
    st_asgn_ident_cd               char(12) not null,
    series                         varchar(64) not null,
    violation_date                 date not null,
    sample_id                      integer
);

create table qaevent
(
    id                             serial not null,
    name                           varchar(20) not null,
    description                    varchar(60),
    test_id                        integer,
    type_id                        integer not null,
    is_billable                    char(1) not null,
    reporting_sequence             integer,
    reporting_text                 text not null
);

create table qc
(
    id                             serial not null,
    name                           varchar(30) not null,
    type_id                        integer,
    inventory_item_id              integer,
    source                         varchar(30) not null,
    is_active                      char(1) not null
);

create table qc_analyte
(
    id                             serial not null,
    qc_id                          integer not null,
    sort_order                     integer not null,
    analyte_id                     integer not null,
    type_id                        integer not null,
    value                          varchar(80),
    is_trendable                   char(1)
);

create table qc_lot
(
    id                             serial not null,
    qc_id                          integer not null,
    lot_number                     varchar(30) not null,
    location_id                    integer,
    prepared_date                  timestamp not null,
    prepared_volume                float,
    prepared_unit_id               integer,
    prepared_by_id                 integer,
    usable_date                    timestamp not null,
    expire_date                    timestamp not null,
    is_active                      char(1) not null
);

create table result
(
    id                             serial not null,
    analysis_id                    integer not null,
    test_analyte_id                integer not null,
    test_result_id                 integer,
    is_column                      char(1) not null,
    sort_order                     integer not null,
    is_reportable                  char(1) not null,
    analyte_id                     integer not null,
    type_id                        integer,
    value                          varchar(80)
);

create table sample
(
    id                             serial not null,
    next_item_sequence             integer,
    domain                         char(1) not null,
    accession_number               integer not null,
    revision                       integer not null,
    order_id                       integer,
    entered_date                   timestamp not null,
    received_date                  timestamp,
    received_by_id                 integer,
    collection_date                date,
    collection_time                time,
    status_id                      integer not null,
    package_id                     integer,
    client_reference               varchar(20),
    released_date                  timestamp
);

create table sample_animal
(
    id                             serial not null,
    sample_id                      integer not null,
    animal_common_name_id          integer,
    animal_scientific_name_id      integer,
    collector                      varchar(40),
    collector_phone                varchar(21),
    sampling_location              varchar(40),
    address_id                     integer
);

create table sample_clinical
(
    id                             serial not null,
    sample_id                      integer not null,
    patient_id                     integer,
    provider_id                    integer,
    provider_phone                 varchar(21)
);

create table sample_environmental
(
    id                             serial not null,
    sample_id                      integer not null,
    is_hazardous                   char(1) not null,
    priority                       integer,
    description                    varchar(40),
    collector                      varchar(40),
    collector_phone                varchar(21),
    location                       varchar(40),
    location_address_id            integer
);

create table sample_item
(
    id                             serial not null,
    sample_id                      integer not null,
    sample_item_id                 integer,
    item_sequence                  integer not null,
    type_of_sample_id              integer,
    source_of_sample_id            integer,
    source_other                   varchar(40),
    container_id                   integer,
    container_reference            varchar(40),
    quantity                       float,
    unit_of_measure_id             integer
);

create table sample_neonatal
(
    id                             serial not null,
    sample_id                      integer not null,
    patient_id                     integer not null,
    birth_order                    smallint,
    gestational_age                smallint,
    next_of_kin_id                 integer,
    next_of_kin_relation_id        integer,
    is_repeat                      char(1),
    is_nicu                        char(1),
    feeding_id                     integer,
    weight_sign                    char(1),
    weight                         integer,
    is_transfused                  char(1),
    transfusion_date               date,
    is_collection_valid            char(1),
    collection_age                 integer,
    provider_id                    integer,
    form_number                    varchar(20)
);

create table sample_organization
(
    id                             serial not null,
    sample_id                      integer not null,
    organization_id                integer not null,
    organization_attention         varchar(30),
    type_id                        integer not null
);

create table sample_private_well
(
    id                             serial not null,
    sample_id                      integer not null,
    organization_id                integer,
    report_to_name                 varchar(30),
    report_to_attention            varchar(30),
    report_to_address_id           integer,
    location                       varchar(40),
    location_address_id            integer,
    owner                          varchar(30),
    collector                      varchar(30),
    well_number                    integer
);

create table sample_project
(
    id                             serial not null,
    sample_id                      integer not null,
    project_id                     integer not null,
    is_permanent                   char(1) not null
);

create table sample_pt
(
    id                             serial not null,
    sample_id                      integer not null,
    pt_provider_id                 integer not null,
    series                         varchar(50) not null,
    due_date                       timestamp,
    additional_domain              char(1)
);

create table sample_qaevent
(
    id                             serial not null,
    sample_id                      integer not null,
    qaevent_id                     integer not null,
    type_id                        integer not null,
    is_billable                    char(1) not null
);

create table sample_sdwis
(
    id                             serial not null,
    sample_id                      integer not null,
    pws_id                         integer not null,
    state_lab_id                   smallint,
    facility_id                    varchar(12),
    sample_type_id                 integer,
    sample_category_id             integer,
    sample_point_id                varchar(11),
    priority                       integer,
    location                       varchar(40),
    collector                      varchar(20)
);

create table scriptlet
(
    id                             serial not null,
    name                           varchar(40) not null,
    bean                           varchar(255) not null,
    is_active                      char(1) not null,
    active_begin                   date not null,
    active_end                     date not null
);

create table section
(
    id                             serial not null,
    parent_section_id              integer,
    name                           varchar(20) not null,
    description                    varchar(60),
    is_external                    char(1) not null,
    organization_id                integer
);

create table section_parameter
(
    id                             serial not null,
    section_id                     integer not null,
    type_id                        integer not null,
    value                          varchar(80)
);

create table shipping
(
    id                             serial not null,
    status_id                      integer not null,
    shipped_from_id                integer,
    shipped_to_id                  integer,
    shipped_to_attention           varchar(30),
    processed_by                   varchar(30),
    processed_date                 timestamp,
    shipped_method_id              integer,
    shipped_date                   timestamp,
    number_of_packages             integer,
    cost                           decimal(9,2)
);

create table shipping_item
(
    id                             serial not null,
    shipping_id                    integer not null,
    reference_table_id             integer not null,
    reference_id                   integer not null,
    quantity                       integer not null,
    description                    varchar(80) not null
);

create table shipping_tracking
(
    id                             serial not null,
    shipping_id                    integer not null,
    tracking_number                varchar(30) not null
);

create table standard_note
(
    id                             serial not null,
    name                           varchar(20) not null,
    description                    varchar(60),
    type_id                        integer,
    text                           text
);

create table storage
(
    id                             serial not null,
    reference_id                   integer,
    reference_table_id             integer,
    storage_location_id            integer,
    checkin                        timestamp,
    checkout                       timestamp,
    system_user_id                 integer
);

create table storage_location
(
    id                             serial not null,
    sort_order                     integer,
    name                           varchar(20),
    location                       varchar(80) not null,
    parent_storage_location_id     integer,
    storage_unit_id                integer not null,
    is_available                   char(1) not null
);

create table storage_unit
(
    id                             serial not null,
    category_id                    integer not null,
    description                    varchar(60) not null,
    is_singular                    char(1) not null
);

create table system_variable
(
    id                             serial not null,
    name                           varchar(30) not null,
    value                          varchar(255)
);

create table test
(
    id                             serial not null,
    name                           varchar(20) not null,
    description                    varchar(60),
    reporting_description          varchar(60),
    method_id                      integer not null,
    is_active                      char(1) not null,
    active_begin                   date not null,
    active_end                     date not null,
    is_reportable                  char(1) not null,
    time_transit                   integer,
    time_holding                   integer,
    time_ta_average                integer,
    time_ta_warning                integer,
    time_ta_max                    integer,
    label_id                       integer,
    label_qty                      integer,
    test_trailer_id                integer,
    scriptlet_id                   integer,
    test_format_id                 integer,
    revision_method_id             integer,
    reporting_method_id            integer,
    sorting_method_id              integer,
    reporting_sequence             integer
);

create table test_analyte
(
    id                             serial not null,
    test_id                        integer not null,
    sort_order                     integer not null,
    row_group                      integer not null,
    is_column                      char(1) not null,
    analyte_id                     integer not null,
    type_id                        integer not null,
    is_reportable                  char(1) not null,
    result_group                   integer,
    scriptlet_id                   integer
);

create table test_prep
(
    id                             serial not null,
    test_id                        integer not null,
    prep_test_id                   integer not null,
    is_optional                    char(1)
);

create table test_reflex
(
    id                             serial not null,
    test_id                        integer not null,
    test_analyte_id                integer not null,
    test_result_id                 integer not null,
    flags_id                       integer not null,
    add_test_id                    integer not null
);

create table test_result
(
    id                             serial not null,
    test_id                        integer not null,
    result_group                   integer not null,
    sort_order                     integer not null,
    unit_of_measure_id             integer,
    type_id                        integer not null,
    value                          varchar(80),
    significant_digits             integer,
    rounding_method_id             integer,
    flags_id                       integer
);

create table test_section
(
    id                             serial not null,
    test_id                        integer not null,
    section_id                     integer not null,
    flag_id                        integer
);

create table test_trailer
(
    id                             serial not null,
    name                           varchar(20) not null,
    description                    varchar(60),
    text                           text
);

create table test_type_of_sample
(
    id                             serial not null,
    test_id                        integer not null,
    type_of_sample_id              integer not null,
    unit_of_measure_id             integer
);

create table test_worksheet
(
    id                             serial not null,
    test_id                        integer not null,
    subset_capacity                integer not null,
    total_capacity                 integer not null,
    format_id                      integer not null,
    scriptlet_id                   integer
);

create table test_worksheet_analyte
(
    id                             serial not null,
    test_id                        integer not null,
    test_analyte_id                integer not null,
    repeat                         integer not null,
    flag_id                        integer
);

create table test_worksheet_item
(
    id                             serial not null,
    test_worksheet_id              integer not null,
    sort_order                     integer not null,
    position                       integer,
    type_id                        integer not null,
    qc_name                        varchar(30)
);

create table worksheet
(
    id                             serial not null,
    created_date                   timestamp not null,
    system_user_id                 integer not null,
    status_id                      integer not null,
    format_id                      integer not null,
    subset_capacity                integer,
    related_worksheet_id           integer,
    instrument_id                  integer,
    description                    varchar(60)
);

create table worksheet_analysis
(
    id                             serial not null,
    worksheet_item_id              integer not null,
    accession_number               varchar(10) not null,
    analysis_id                    integer,
    qc_lot_id                      integer,
    worksheet_analysis_id          integer,
    system_users                   varchar(60),
    started_date                   timestamp,
    completed_date                 timestamp,
    from_other_id                  integer,
    change_flags_id                integer
);

create table worksheet_item
(
    id                             serial not null,
    worksheet_id                   integer not null,
    position                       integer
);

create table worksheet_qc_result
(
    id                             serial not null,
    worksheet_analysis_id          integer not null,
    sort_order                     integer not null,
    qc_analyte_id                  integer not null,
    value_1                        varchar(80),
    value_2                        varchar(80),
    value_3                        varchar(80),
    value_4                        varchar(80),
    value_5                        varchar(80),
    value_6                        varchar(80),
    value_7                        varchar(80),
    value_8                        varchar(80),
    value_9                        varchar(80),
    value_10                       varchar(80),
    value_11                       varchar(80),
    value_12                       varchar(80),
    value_13                       varchar(80),
    value_14                       varchar(80),
    value_15                       varchar(80),
    value_16                       varchar(80),
    value_17                       varchar(80),
    value_18                       varchar(80),
    value_19                       varchar(80),
    value_20                       varchar(80),
    value_21                       varchar(80),
    value_22                       varchar(80),
    value_23                       varchar(80),
    value_24                       varchar(80),
    value_25                       varchar(80),
    value_26                       varchar(80),
    value_27                       varchar(80),
    value_28                       varchar(80),
    value_29                       varchar(80),
    value_30                       varchar(80)
);

create table worksheet_reagent
(
    id                             serial not null,
    worksheet_id                   integer not null,
    sort_order                     integer not null,
    qc_lot_id                      integer not null
);

create table worksheet_result
(
    id                             serial not null,
    worksheet_analysis_id          integer not null,
    test_analyte_id                integer not null,
    result_row                     integer not null,
    analyte_id                     integer not null,
    value_1                        varchar(80),
    value_2                        varchar(80),
    value_3                        varchar(80),
    value_4                        varchar(80),
    value_5                        varchar(80),
    value_6                        varchar(80),
    value_7                        varchar(80),
    value_8                        varchar(80),
    value_9                        varchar(80),
    value_10                       varchar(80),
    value_11                       varchar(80),
    value_12                       varchar(80),
    value_13                       varchar(80),
    value_14                       varchar(80),
    value_15                       varchar(80),
    value_16                       varchar(80),
    value_17                       varchar(80),
    value_18                       varchar(80),
    value_19                       varchar(80),
    value_20                       varchar(80),
    value_21                       varchar(80),
    value_22                       varchar(80),
    value_23                       varchar(80),
    value_24                       varchar(80),
    value_25                       varchar(80),
    value_26                       varchar(80),
    value_27                       varchar(80),
    value_28                       varchar(80),
    value_29                       varchar(80),
    value_30                       varchar(80),
    change_flags_id                integer
);