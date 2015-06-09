-------------------------------------------------------------------------------
--
-- tables
--
-------------------------------------------------------------------------------

create table address
(
    id                        serial not null,
    multiple_unit             varchar(30),
    street_address            varchar(30),
    city                      varchar(30),
    state                     char(2),
    zip_code                  varchar(10),
    work_phone                varchar(21),
    home_phone                varchar(16),
    cell_phone                varchar(16),
    fax_phone                 varchar(16),
    email                     varchar(80),
    country                   varchar(30)
);

create table analysis
(
    id                        serial not null,
    sample_item_id            integer not null,
    revision                  integer not null,
    test_id                   integer not null,
    section_id                integer,
    panel_id                  integer,
    pre_analysis_id           integer,
    parent_analysis_id        integer,
    parent_result_id          integer,
    type_id                   integer,
    is_reportable             char(1) not null,
    unit_of_measure_id        integer,
    status_id                 integer not null,
    available_date            datetime year to second,
    started_date              datetime year to second,
    completed_date            datetime year to second,
    released_date             datetime year to second,
    printed_date              datetime year to second
);

create table analysis_qaevent
(
    id                        serial not null,
    analysis_id               integer not null,
    qaevent_id                integer not null,
    type_id                   integer not null,
    is_billable               char(1) not null
);

create table analysis_report_flags
(
    analysis_id               integer not null,
    notified_received         char(1),
    notified_released         char(1),
    billed_date               date,
    billed_analytes           smallint,
    billed_override           decimal(8,2)
);

create table analysis_user
(
    id                        serial not null,
    analysis_id               integer not null,
    system_user_id            integer not null,
    action_id                 integer
);

create table analyte
(
    id                        serial not null,
    name                      varchar(60) not null,
    is_active                 char(1) not null,
    parent_analyte_id         integer,
    external_id               varchar(20)
);

create table analyte_parameter
(
    id                        serial not null,
    reference_id              integer not null,
    reference_table_id        integer not null,
    analyte_id                integer not null,
    type_of_sample_id         integer,
    unit_of_measure_id        integer,
    active_begin              datetime year to minute not null,
    active_end                datetime year to minute not null,
    p1                        float,
    p2                        float,
    p3                        float
);

create table attachment
(
    id                        serial not null,
    created_date              datetime year to minute not null,
    type_id                   integer,
    section_id                integer,
    description               varchar(80),
    storage_reference         varchar(255) not null
);

create table attachment_item
(
    id                        serial not null,
    reference_id              integer not null,
    reference_table_id        integer not null,
    attachment_id             integer not null
);

create table aux_data
(
    id                        serial not null,
    sort_order                integer not null,
    aux_field_id              integer not null,
    reference_id              integer not null,
    reference_table_id        integer not null,
    is_reportable             char(1),
    type_id                   integer,
    value                     varchar(80)
);

create table aux_field
(
    id                        serial not null,
    aux_field_group_id        integer not null,
    sort_order                integer not null,
    analyte_id                integer not null,
    description               varchar(60),
    method_id                 integer,
    unit_of_measure_id        integer,
    is_required               char(1),
    is_active                 char(1),
    is_reportable             char(1),
    scriptlet_id              integer
);

create table aux_field_group
(
    id                        serial not null,
    name                      varchar(20) not null,
    description               varchar(60),
    is_active                 char(1) not null,
    active_begin              date not null,
    active_end                date not null
);

create table aux_field_value
(
    id                        serial not null,
    aux_field_id              integer not null,
    type_id                   integer not null,
    value                     varchar(80)
);

create table category
(
    id                        serial not null,
    system_name               varchar(30) not null,
    name                      varchar(50),
    description               varchar(60),
    section_id                integer,
    is_system                 char(1) not null
);

create table cron
(
    id                        serial not null,
    name                      varchar(30) not null,
    is_active                 char(1) not null,
    cron_tab                  varchar(30) not null,
    bean                      varchar(255) not null,
    method                    varchar(255) not null,
    parameters                varchar(255),
    last_run                  datetime year to second
);

create table dictionary
(
    id                        serial not null,
    category_id               integer not null,
    sort_order                integer not null,
    related_entry_id          integer,
    system_name               varchar(30),
    is_active                 char(1) not null,
    code                      varchar(10),
    entry                     varchar(255) not null
);

create table eorder
(
    id                        serial not null,
    entered_date              datetime year to second not null,
    paper_order_validator     varchar(40) not null,
    description               varchar(60)
);

create table eorder_body
(
    id                        serial not null,
    eorder_id                 integer not null,
    xml                       text
);

create table eorder_link
(
    id                        serial not null,
    eorder_id                 integer not null,
    reference                 varchar(40),
    sub_id                    varchar(20),
    name                      varchar(20) not null,
    value                     varchar(255)
);

create table event_log
(
    id                        serial not null,
    type_id                   integer not null,
    source                    varchar(60) not null,
    reference_table_id        integer,
    reference_id              integer,
    level_id                  integer not null,
    system_user_id            integer not null,
    timestamp                 datetime year to second not null,
    text                      text
);

create table exchange_criteria
(
    id                        serial not null,
    name                      varchar(20) not null,
    environment_id            integer,
    destination_uri           varchar(60),
    is_all_analyses_included  char(1) not null,
    query                     text
);

create table exchange_external_term
(
    id                        serial not null,
    exchange_local_term_id    integer not null,
    profile_id                integer not null,
    is_active                 char(1) not null,
    external_term             varchar(60) not null,
    external_description      varchar(255),
    external_coding_system    varchar(60),
    version                   varchar(60)
);

create table exchange_local_term
(
    id                        serial not null,
    reference_table_id        integer not null,
    reference_id              integer not null
);

create table exchange_profile
(
    id                        serial not null,
    exchange_criteria_id      integer not null,
    profile_id                integer not null,
    sort_order                integer not null
);

create table history
(
    id                        serial not null,
    reference_id              integer not null,
    reference_table_id        integer not null,
    timestamp                 datetime year to second not null,
    activity_id               integer not null,
    system_user_id            integer not null,
    changes                   text
);

create table instrument
(
    id                        serial not null,
    name                      varchar(20) not null,
    description               varchar(60),
    model_number              varchar(40),
    serial_number             varchar(40),
    type_id                   integer not null,
    location                  varchar(60) not null,
    is_active                 char(1) not null,
    active_begin              date,
    active_end                date,
    scriptlet_id              integer
);

create table instrument_log
(
    id                        serial not null,
    instrument_id             integer not null,
    type_id                   integer not null,
    worksheet_id              integer,
    event_begin               datetime year to second not null,
    event_end                 datetime year to second,
    text                      text
);

create table inventory_adjustment
(
    id                        serial not null,
    description               varchar(60),
    system_user_id            integer not null,
    adjustment_date           datetime year to minute not null
);

create table inventory_component
(
    id                        serial not null,
    inventory_item_id         integer not null,
    component_id              integer not null,
    quantity                  integer not null
);

create table inventory_item
(
    id                        serial not null,
    name                      varchar(30) not null,
    description               varchar(60),
    category_id               integer,
    store_id                  integer not null,
    quantity_min_level        integer not null,
    quantity_max_level        integer,
    quantity_to_reorder       integer not null,
    dispensed_units_id        integer,
    is_reorder_auto           char(1) not null,
    is_lot_maintained         char(1) not null,
    is_serial_maintained      char(1) not null,
    is_active                 char(1) not null,
    is_bulk                   char(1) not null,
    is_not_for_sale           char(1) not null,
    is_sub_assembly           char(1) not null,
    is_labor                  char(1) not null,
    is_not_inventoried        char(1) not null,
    product_uri               varchar(80),
    average_lead_time         integer,
    average_cost              float,
    average_daily_use         integer,
    parent_inventory_item_id  integer,
    parent_ratio              integer
);

create table inventory_location
(
    id                        serial not null,
    inventory_item_id         integer not null,
    lot_number                varchar(20),
    storage_location_id       integer not null,
    quantity_onhand           integer not null,
    expiration_date           date
);

create table inventory_receipt
(
    id                        serial not null,
    inventory_item_id         integer not null,
    iorder_item_id            integer,
    organization_id           integer,
    received_date             datetime year to second not null,
    quantity_received         integer not null,
    unit_cost                 float,
    qc_reference              varchar(20),
    external_reference        varchar(20),
    upc                       varchar(15)
);

create table inventory_receipt_iorder_item
(
    id                        serial not null,
    inventory_receipt_id      integer not null,
    iorder_item_id            integer not null
);

create table inventory_x_adjust
(
    id                        serial not null,
    inventory_adjustment_id   integer not null,
    inventory_location_id     integer not null,
    quantity                  integer not null,
    physical_count            integer not null
);

create table inventory_x_put
(
    id                        serial not null,
    inventory_receipt_id      integer not null,
    inventory_location_id     integer not null,
    quantity                  integer not null
);

create table inventory_x_use
(
    id                        serial not null,
    inventory_location_id     integer not null,
    iorder_item_id            integer not null,
    quantity                  integer not null
);

create table iorder
(
    id                        serial not null,
    parent_iorder_id          integer,
    description               varchar(60),
    status_id                 integer not null,
    ordered_date              date not null,
    needed_in_days            integer,
    requested_by              varchar(30),
    cost_center_id            integer,
    organization_id           integer,
    organization_attention    varchar(30),
    type                      char(1) not null,
    external_order_number     varchar(20),
    ship_from_id              integer,
    number_of_forms           integer
);

create table iorder_container
(
    id                        serial not null,
    iorder_id                 integer not null,
    container_id              integer not null,
    item_sequence             integer not null,
    type_of_sample_id         integer
);

create table iorder_item
(
    id                        serial not null,
    iorder_id                 integer not null,
    inventory_item_id         integer not null,
    quantity                  integer not null,
    catalog_number            varchar(30),
    unit_cost                 float
);

create table iorder_organization
(
    id                        serial not null,
    iorder_id                 integer not null,
    organization_id           integer not null,
    organization_attention    varchar(30),
    type_id                   integer not null
);

create table iorder_recurrence
(
    id                        serial not null,
    iorder_id                 integer not null,
    is_active                 char(1) not null,
    active_begin              date,
    active_end                date,
    frequency                 smallint,
    unit_id                   integer
);

create table iorder_test
(
    id                        serial not null,
    iorder_id                 integer not null,
    item_sequence             integer not null,
    sort_order                integer not null,
    test_id                   integer not null
);

create table iorder_test_analyte
(
    id                        serial not null,
    iorder_test_id            integer not null,
    analyte_id                integer not null
);

create table label
(
    id                        serial not null,
    name                      varchar(30) not null,
    description               varchar(60),
    printer_type_id           integer not null,
    scriptlet_id              integer
);

create table lock
(
    reference_table_id        integer not null,
    reference_id              integer not null,
    expires                   int8 not null,
    system_user_id            integer not null,
    session_id                varchar(80)
);

create table method
(
    id                        serial not null,
    name                      varchar(20) not null,
    description               varchar(60),
    reporting_description     varchar(60),
    is_active                 char(1) not null,
    active_begin              date not null,
    active_end                date not null
);

create table note
(
    id                        serial not null,
    reference_id              integer not null,
    reference_table_id        integer not null,
    timestamp                 datetime year to second not null,
    is_external               char(1) not null,
    system_user_id            integer not null,
    subject                   varchar(60),
    text                      text
);

create table organization
(
    id                        serial not null,
    parent_organization_id    integer,
    name                      varchar(40) not null,
    is_active                 char(1) not null,
    address_id                integer
);

create table organization_contact
(
    id                        serial not null,
    organization_id           integer not null,
    contact_type_id           integer not null,
    name                      varchar(30) not null,
    address_id                integer
);

create table organization_parameter
(
    id                        serial not null,
    organization_id           integer not null,
    type_id                   integer not null,
    value                     varchar(80)
);

create table panel
(
    id                        serial not null,
    name                      varchar(20) not null,
    description               varchar(60)
);

create table panel_item
(
    id                        serial not null,
    panel_id                  integer not null,
    type                      char(1) not null,
    sort_order                integer not null,
    name                      varchar(20) not null,
    method_name               varchar(20)
);

create table patient
(
    id                        serial not null,
    last_name                 varchar(30),
    first_name                varchar(20),
    middle_name               varchar(20),
    address_id                integer,
    birth_date                date,
    birth_time                datetime hour to minute,
    gender_id                 integer,
    race_id                   integer,
    ethnicity_id              integer,
    national_id               varchar(20)
);

create table patient_relation
(
    id                        serial not null,
    relation_id               integer not null,
    patient_id                integer not null,
    related_patient_id        integer not null
);

create table preferences
(
    system_user_id            integer not null,
    text                      text
);

create table project
(
    id                        serial not null,
    name                      varchar(20) not null,
    description               varchar(60),
    started_date              date,
    completed_date            date,
    is_active                 char(1) not null,
    reference_to              varchar(20),
    owner_id                  integer not null,
    scriptlet_id              integer
);

create table project_parameter
(
    id                        serial not null,
    project_id                integer not null,
    parameter                 varchar(80) not null,
    operation_id              integer not null,
    value                     varchar(255) not null
);

create table provider
(
    id                        serial not null,
    reference_id              varchar(40),
    reference_source_id       integer,
    last_name                 varchar(30),
    first_name                varchar(20),
    middle_name               varchar(20),
    type_id                   integer,
    npi                       varchar(10)
);

create table provider_location
(
    id                        serial not null,
    location                  varchar(50) not null,
    external_id               varchar(10),
    provider_id               integer not null,
    address_id                integer
);

create table pws
(
    id                        serial not null,
    tinwsys_is_number         integer not null,
    number0                   char(12) not null,
    alternate_st_num          char(5),
    name                      varchar(40),
    activity_status_cd        char(1),
    d_prin_city_svd_nm        varchar(40),
    d_prin_cnty_svd_nm        varchar(40),
    d_population_count        integer,
    d_pws_st_type_cd          char(4),
    activity_rsn_txt          varchar(255),
    start_day                 integer,
    start_month               integer,
    end_day                   integer,
    end_month                 integer,
    eff_begin_dt              date,
    eff_end_dt                date
);

create table pws_address
(
    tinwslec_is_number        integer not null,
    tinlgent_is_number        integer not null,
    tinwsys_is_number         integer not null,
    type_code                 char(3),
    active_ind_cd             char(1),
    name                      varchar(40),
    addr_line_one_txt         varchar(40),
    addr_line_two_txt         varchar(40),
    address_city_name         varchar(40),
    address_state_code        char(2),
    address_zip_code          char(10),
    state_fips_code           char(2),
    phone_number              char(12)
);

create table pws_facility
(
    tinwsf_is_number          integer not null,
    tsasmppt_is_number        integer not null,
    tinwsys_is_number         integer not null,
    name                      varchar(40),
    type_code                 char(2),
    st_asgn_ident_cd          char(12),
    activity_status_cd        char(1),
    water_type_code           char(3),
    availability_code         char(1),
    identification_cd         char(11),
    description_text          varchar(20),
    source_type_code          char(2)
);

create table pws_monitor
(
    tiamrtask_is_number       integer not null,
    tinwsys_is_number         integer not null,
    st_asgn_ident_cd          char(12),
    name                      varchar(40),
    tiaanlgp_tiaanlyt_name    varchar(64),
    number_samples            integer,
    comp_begin_date           date,
    comp_end_date             date,
    frequency_name            varchar(25),
    period_name               varchar(20)
);

create table pws_violation
(
    id                        serial not null,
    tinwsys_is_number         integer not null,
    st_asgn_ident_cd          char(12) not null,
    series                    varchar(64) not null,
    violation_date            date not null,
    sample_id                 integer
);

create table qaevent
(
    id                        serial not null,
    name                      varchar(20) not null,
    description               varchar(60),
    test_id                   integer,
    type_id                   integer not null,
    is_billable               char(1) not null,
    reporting_sequence        integer,
    reporting_text            text not null
);

create table qc
(
    id                        serial not null,
    name                      varchar(30) not null,
    type_id                   integer,
    inventory_item_id         integer,
    source                    varchar(30) not null,
    is_active                 char(1) not null
);

create table qc_analyte
(
    id                        serial not null,
    qc_id                     integer not null,
    sort_order                integer not null,
    analyte_id                integer not null,
    type_id                   integer not null,
    value                     varchar(80),
    is_trendable              char(1)
);

create table qc_lot
(
    id                        serial not null,
    qc_id                     integer not null,
    lot_number                varchar(30) not null,
    location_id               integer,
    prepared_date             datetime year to second not null,
    prepared_volume           float,
    prepared_unit_id          integer,
    prepared_by_id            integer,
    usable_date               datetime year to second not null,
    expire_date               datetime year to second not null,
    is_active                 char(1) not null
);

create table result
(
    id                        serial not null,
    analysis_id               integer not null,
    test_analyte_id           integer not null,
    test_result_id            integer,
    is_column                 char(1) not null,
    sort_order                integer not null,
    is_reportable             char(1) not null,
    analyte_id                integer not null,
    type_id                   integer,
    value                     varchar(80)
);

create table sample
(
    id                        serial not null,
    next_item_sequence        integer,
    domain                    char(1) not null,
    accession_number          integer not null,
    revision                  integer not null,
    order_id                  integer,
    entered_date              datetime year to second not null,
    received_date             datetime year to second,
    received_by_id            integer,
    collection_date           date,
    collection_time           datetime hour to second,
    status_id                 integer not null,
    package_id                integer,
    client_reference          varchar(20),
    released_date             datetime year to second
);

create table sample_animal
(
    id                        serial not null,
    sample_id                 integer not null,
    animal_common_name_id     integer,
    animal_scientific_name_id integer,
    collector                 varchar(40),
    collector_phone           varchar(21),
    sampling_location         varchar(40),
    address_id                integer
);

create table sample_clinical
(
    id                        serial not null,
    sample_id                 integer not null,
    patient_id                integer,
    provider_id               integer,
    provider_phone            varchar(21)
);

create table sample_environmental
(
    id                        serial not null,
    sample_id                 integer not null,
    is_hazardous              char(1) not null,
    priority                  integer,
    description               varchar(40),
    collector                 varchar(40),
    collector_phone           varchar(21),
    location                  varchar(40),
    location_address_id       integer
);

create table sample_item
(
    id                        serial not null,
    sample_id                 integer not null,
    sample_item_id            integer,
    item_sequence             integer not null,
    type_of_sample_id         integer,
    source_of_sample_id       integer,
    source_other              varchar(40),
    container_id              integer,
    container_reference       varchar(40),
    quantity                  float,
    unit_of_measure_id        integer
);

create table sample_neonatal
(
    id                        serial not null,
    sample_id                 integer not null,
    patient_id                integer not null,
    birth_order               smallint,
    gestational_age           smallint,
    next_of_kin_id            integer,
    next_of_kin_relation_id   integer,
    is_repeat                 char(1),
    is_nicu                   char(1),
    feeding_id                integer,
    weight_sign               char(1),
    weight                    integer,
    is_transfused             char(1),
    transfusion_date          date,
    is_collection_valid       char(1),
    collection_age            integer,
    provider_id               integer,
    form_number               varchar(20,8)
);

create table sample_organization
(
    id                        serial not null,
    sample_id                 integer not null,
    organization_id           integer not null,
    organization_attention    varchar(30),
    type_id                   integer not null
);

create table sample_private_well
(
    id                        serial not null,
    sample_id                 integer not null,
    organization_id           integer,
    report_to_name            varchar(30),
    report_to_attention       varchar(30),
    report_to_address_id      integer,
    location                  varchar(40),
    location_address_id       integer,
    owner                     varchar(30),
    collector                 varchar(30),
    well_number               integer
);

create table sample_project
(
    id                        serial not null,
    sample_id                 integer not null,
    project_id                integer not null,
    is_permanent              char(1) not null
);

create table sample_pt
(
    id                        serial not null,
    sample_id                 integer not null,
    pt_provider_id            integer not null,
    series                    varchar(50) not null,
    due_date                  datetime year to minute,
    additional_domain         char(1)
);

create table sample_qaevent
(
    id                        serial not null,
    sample_id                 integer not null,
    qaevent_id                integer not null,
    type_id                   integer not null,
    is_billable               char(1) not null
);

create table sample_sdwis
(
    id                        serial not null,
    sample_id                 integer not null,
    pws_id                    integer not null,
    state_lab_id              smallint,
    facility_id               varchar(12),
    sample_type_id            integer,
    sample_category_id        integer,
    sample_point_id           varchar(11),
    priority                  integer,
    location                  varchar(40),
    collector                 varchar(20)
);

create table scriptlet
(
    id                        serial not null,
    name                      varchar(40) not null,
    bean                      varchar(255) not null,
    is_active                 char(1) not null,
    active_begin              date not null,
    active_end                date not null
);

create table section
(
    id                        serial not null,
    parent_section_id         integer,
    name                      varchar(20) not null,
    description               varchar(60),
    is_external               char(1) not null,
    organization_id           integer
);

create table section_parameter
(
    id                        serial not null,
    section_id                integer not null,
    type_id                   integer not null,
    value                     varchar(80)
);

create table shipping
(
    id                        serial not null,
    status_id                 integer not null,
    shipped_from_id           integer,
    shipped_to_id             integer,
    shipped_to_attention      varchar(30),
    processed_by              varchar(30),
    processed_date            datetime year to second,
    shipped_method_id         integer,
    shipped_date              datetime year to second,
    number_of_packages        integer,
    cost                      decimal(9,2)
);

create table shipping_item
(
    id                        serial not null,
    shipping_id               integer not null,
    reference_table_id        integer not null,
    reference_id              integer not null,
    quantity                  integer not null,
    description               varchar(80) not null
);

create table shipping_tracking
(
    id                        serial not null,
    shipping_id               integer not null,
    tracking_number           varchar(30) not null
);

create table standard_note
(
    id                        serial not null,
    name                      varchar(20) not null,
    description               varchar(60),
    type_id                   integer,
    text                      text
);

create table storage
(
    id                        serial not null,
    reference_id              integer,
    reference_table_id        integer,
    storage_location_id       integer,
    checkin                   datetime year to minute,
    checkout                  datetime year to minute,
    system_user_id            integer
);

create table storage_location
(
    id                        serial not null,
    sort_order                integer,
    name                      varchar(20),
    location                  varchar(80) not null,
    parent_storage_location_id integer,
    storage_unit_id           integer not null,
    is_available              char(1) not null
);

create table storage_unit
(
    id                        serial not null,
    category_id               integer not null,
    description               varchar(60) not null,
    is_singular               char(1) not null
);

create table system_variable
(
    id                        serial not null,
    name                      varchar(30) not null,
    value                     varchar(255)
);

create table test
(
    id                        serial not null,
    name                      varchar(20) not null,
    description               varchar(60),
    reporting_description     varchar(60),
    method_id                 integer not null,
    is_active                 char(1) not null,
    active_begin              date not null,
    active_end                date not null,
    is_reportable             char(1) not null,
    time_transit              integer,
    time_holding              integer,
    time_ta_average           integer,
    time_ta_warning           integer,
    time_ta_max               integer,
    label_id                  integer,
    label_qty                 integer,
    test_trailer_id           integer,
    scriptlet_id              integer,
    test_format_id            integer,
    revision_method_id        integer,
    reporting_method_id       integer,
    sorting_method_id         integer,
    reporting_sequence        integer
);

create table test_analyte
(
    id                        serial not null,
    test_id                   integer not null,
    sort_order                integer not null,
    row_group                 integer not null,
    is_column                 char(1) not null,
    analyte_id                integer not null,
    type_id                   integer not null,
    is_reportable             char(1) not null,
    result_group              integer,
    scriptlet_id              integer
);

create table test_prep
(
    id                        serial not null,
    test_id                   integer not null,
    prep_test_id              integer not null,
    is_optional               char(1)
);

create table test_reflex
(
    id                        serial not null,
    test_id                   integer not null,
    test_analyte_id           integer not null,
    test_result_id            integer not null,
    flags_id                  integer not null,
    add_test_id               integer not null
);

create table test_result
(
    id                        serial not null,
    test_id                   integer not null,
    result_group              integer not null,
    sort_order                integer not null,
    unit_of_measure_id        integer,
    type_id                   integer not null,
    value                     varchar(80),
    significant_digits        integer,
    rounding_method_id        integer,
    flags_id                  integer
);

create table test_section
(
    id                        serial not null,
    test_id                   integer not null,
    section_id                integer not null,
    flag_id                   integer
);

create table test_trailer
(
    id                        serial not null,
    name                      varchar(20) not null,
    description               varchar(60),
    text                      text
);

create table test_type_of_sample
(
    id                        serial not null,
    test_id                   integer not null,
    type_of_sample_id         integer not null,
    unit_of_measure_id        integer
);

create table test_worksheet
(
    id                        serial not null,
    test_id                   integer not null,
    subset_capacity           integer not null,
    total_capacity            integer not null,
    format_id                 integer not null,
    scriptlet_id              integer
);

create table test_worksheet_analyte
(
    id                        serial not null,
    test_id                   integer not null,
    test_analyte_id           integer not null,
    repeat                    integer not null,
    flag_id                   integer
);

create table test_worksheet_item
(
    id                        serial not null,
    test_worksheet_id         integer not null,
    sort_order                integer not null,
    position                  integer,
    type_id                   integer not null,
    qc_name                   varchar(30)
);

create table worksheet
(
    id                        serial not null,
    created_date              datetime year to minute not null,
    system_user_id            integer not null,
    status_id                 integer not null,
    format_id                 integer not null,
    subset_capacity           integer,
    related_worksheet_id      integer,
    instrument_id             integer,
    description               varchar(60)
);

create table worksheet_analysis
(
    id                        serial not null,
    worksheet_item_id         integer not null,
    accession_number          varchar(10) not null,
    analysis_id               integer,
    qc_lot_id                 integer,
    worksheet_analysis_id     integer,
    system_users              varchar(60),
    started_date              datetime year to second,
    completed_date            datetime year to second,
    from_other_id             integer,
    change_flags_id           integer
);

create table worksheet_item
(
    id                        serial not null,
    worksheet_id              integer not null,
    position                  integer
);

create table worksheet_qc_result
(
    id                        serial not null,
    worksheet_analysis_id     integer not null,
    sort_order                integer not null,
    qc_analyte_id             integer not null,
    value_1                   varchar(80),
    value_2                   varchar(80),
    value_3                   varchar(80),
    value_4                   varchar(80),
    value_5                   varchar(80),
    value_6                   varchar(80),
    value_7                   varchar(80),
    value_8                   varchar(80),
    value_9                   varchar(80),
    value_10                  varchar(80),
    value_11                  varchar(80),
    value_12                  varchar(80),
    value_13                  varchar(80),
    value_14                  varchar(80),
    value_15                  varchar(80),
    value_16                  varchar(80),
    value_17                  varchar(80),
    value_18                  varchar(80),
    value_19                  varchar(80),
    value_20                  varchar(80),
    value_21                  varchar(80),
    value_22                  varchar(80),
    value_23                  varchar(80),
    value_24                  varchar(80),
    value_25                  varchar(80),
    value_26                  varchar(80),
    value_27                  varchar(80),
    value_28                  varchar(80),
    value_29                  varchar(80),
    value_30                  varchar(80)
);

create table worksheet_reagent
(
    id                        serial not null,
    worksheet_id              integer not null,
    sort_order                integer not null,
    qc_lot_id                 integer not null
);

create table worksheet_result
(
    id                        serial not null,
    worksheet_analysis_id     integer not null,
    test_analyte_id           integer not null,
    result_row                integer not null,
    analyte_id                integer not null,
    value_1                   varchar(80),
    value_2                   varchar(80),
    value_3                   varchar(80),
    value_4                   varchar(80),
    value_5                   varchar(80),
    value_6                   varchar(80),
    value_7                   varchar(80),
    value_8                   varchar(80),
    value_9                   varchar(80),
    value_10                  varchar(80),
    value_11                  varchar(80),
    value_12                  varchar(80),
    value_13                  varchar(80),
    value_14                  varchar(80),
    value_15                  varchar(80),
    value_16                  varchar(80),
    value_17                  varchar(80),
    value_18                  varchar(80),
    value_19                  varchar(80),
    value_20                  varchar(80),
    value_21                  varchar(80),
    value_22                  varchar(80),
    value_23                  varchar(80),
    value_24                  varchar(80),
    value_25                  varchar(80),
    value_26                  varchar(80),
    value_27                  varchar(80),
    value_28                  varchar(80),
    value_29                  varchar(80),
    value_30                  varchar(80),
    change_flags_id           integer
);

-------------------------------------------------------------------------------
--
-- views
--
-------------------------------------------------------------------------------

create view analysis_view (sample_id, domain, accession_number, received_date,
                           collection_date, collection_time, entered_date,
                           primary_organization_name, todo_description, worksheet_description,
                           priority, test_id, test_name, method_name, time_ta_average,
                           time_holding, type_of_sample_id, analysis_id, analysis_status_id,
                           section_id, section_name, available_date, started_date,
                           completed_date, released_date, analysis_result_override,
                           unit_of_measure_id, worksheet_format_id)
                           AS
                           (
select s.id, s.domain, s.accession_number, s.received_date, s.collection_date,
       s.collection_time, s.entered_date,
       case
           when spw.organization_id is not null then spw_org.name
           when spw.report_to_name is not null then spw.report_to_name
           else o.name
       end,
       case 
           when s.domain = 'E' then
               case
                   when sen.priority is not null then '[pri]' || sen.priority || ' '
                   else ''
               end ||
               case
                   when p.name is not null then '[prj]' || trim(p.name) || ' '
                   else ''
               end ||
               case
                   when sen.location is not null then '[loc]' || trim(sen.location)
                   else ''
               end
           when s.domain = 'W' then
               case
                   when spw.owner is not null then '[own]' || trim(spw.owner)
                   else null
               end
           when s.domain = 'S' then
               case
                   when pws.name is not null then '[pws]' || trim(pws.name) || ' '
                   else ''
               end ||
               case
                   when ssd.facility_id is not null then '[fac]' || trim(ssd.facility_id) || ' '
                   else ''
               end ||
               case
                   when ssd.priority is not null then '[pri]' || ssd.priority
                   else ''
               end
           when s.domain = 'C' then
               case
                   when pat.last_name is not null then '[lst]' || trim(pat.last_name) || ' '
                   else ''
               end ||
               case
                   when pat.first_name is not null then '[fst]' || trim(pat.first_name) || ' '
                   else ''
               end ||
               case
                   when si.container_reference is not null then '[cnt]' || trim(si.container_reference)
                   else ''
               end
           when s.domain = 'P' then
               case
                   when ptp.entry is not null then '[ptp]' || trim(ptp.entry) || ' '
                   else ''
               end ||
               case
                   when spt.series is not null then '[ser]' || trim(spt.series) || ' '
                   else ''
               end ||
               case
                   when si.container_reference is not null then '[cnt]' || trim(si.container_reference)
                   else ''
               end
           else null
       end,
       case
           when s.domain = 'E' then
               case
                   when sen.location is not null then '[loc]' || trim(sen.location) || ' '
                   else ''
               end ||
               case
                   when o.name is not null then '[rpt]' || trim(o.name)
                   else ''
               end
           when s.domain = 'S' then
               case
                   when ssd.location is not null then '[loc]' || trim(ssd.location) || ' '
                   else ''
               end ||
               case
                   when o.name is not null then '[rpt]' || trim(o.name)
                   else ''
               end
           when s.domain = 'W' then
               case
                   when spw.location is not null then '[loc]' || trim(spw.location)
                   else null
               end
           when s.domain = 'C' then
               case
                   when pat.last_name is not null then '[lst]' || trim(pat.last_name) || ' '
                   else ''
               end ||
               case
                   when pat.first_name is not null then '[fst]' || trim(pat.first_name) || ' '
                   else ''
               end ||
               case
                   when si.container_reference is not null then '[cnt]' || trim(si.container_reference)
                   else ''
               end
           when s.domain = 'P' then
               case
                   when ptp.entry is not null then '[ptp]' || trim(ptp.entry) || ' '
                   else ''
               end ||
               case
                   when spt.series is not null then '[ser]' || trim(spt.series) || ' '
                   else ''
               end ||
               case
                   when si.container_reference is not null then '[cnt]' || trim(si.container_reference)
                   else ''
               end
           else null
       end,
       case
           when s.domain = 'E' then sen.priority
           when s.domain = 'S' then ssd.priority
           else null
       end,
       t.id, t.name, m.name, t.time_ta_average, t.time_holding, si.type_of_sample_id, 
       a.id, a.status_id, sec.id, sec.name, a.available_date, a.started_date, a.completed_date,
       a.released_date,
       case
           when sqa.id is not null or aqa.id is not null then 'Y'
           else 'N'
       end,
       a.unit_of_measure_id, tw.format_id
  from sample s
       join sample_item si on s.id = si.sample_id
       join analysis a on si.id = a.sample_item_id
       join test t on a.test_id = t.id
       join method m on t.method_id = m.id
       join section sec on a.section_id = sec.id
       left join sample_environmental sen on s.id = sen.sample_id
       left join sample_private_well spw on s.id = spw.sample_id
       left join organization spw_org on spw.organization_id = spw_org.id
       left join sample_sdwis ssd on s.id = ssd.sample_id
       left join pws on ssd.pws_id = pws.id
       left join sample_clinical scl on s.id = scl.sample_id
       left join patient pat on scl.patient_id = pat.id
       left join sample_pt spt on s.id = spt.sample_id
       left join dictionary ptp on spt.pt_provider_id = ptp.id
       left join sample_organization so on s.id = so.sample_id and
                                           so.type_id = (select dictionary.id
                                                           from dictionary
                                                          where dictionary.system_name = 'org_report_to')
       left join organization o on so.organization_id = o.id
       left join sample_project sp on s.id = sp.sample_id and
                                       sp.is_permanent = 'Y'
       left join project p on sp.project_id = p.id
       left join sample_qaevent sqa on s.id = sqa.sample_id and
                                              sqa.type_id = (select dictionary.id
                                                               from dictionary
                                                              where dictionary.system_name = 'qaevent_override')
       left join qaevent sq on sqa.qaevent_id = sq.id
       left join analysis_qaevent aqa on a.id = aqa.analysis_id and
                                         aqa.type_id = (select dictionary.id
                                                          from dictionary
                                                         where dictionary.system_name = 'qaevent_override')
       left join qaevent aq on aqa.qaevent_id = aq.id
       left join test_worksheet tw on tw.test_id = t.id);

create view sample_patient_view (sample_id, patient_id)
                                 AS
                                 (
select sn.sample_id, sn.patient_id
  from sample_neonatal sn
union
select sc.sample_id, sc.patient_id
  from sample_clinical sc);

create view sample_view (sample_id, domain, accession_number, sample_revision, received_date,
                         collection_date, collection_time, sample_status_id, client_reference,
                         sample_released_date, report_to_id, report_to_name, collector,
                         location, location_city, project_id, project_name, pws_number0,
                         pws_name, sdwis_facility_id, patient_last_name, patient_first_name,
                         patient_birth_date, provider_name, analysis_id, analysis_revision, analysis_is_reportable,
                         analysis_status_id, analysis_released_date, test_reporting_description,
                         method_reporting_description)
                         AS
                         (
select s.id, s.domain, s.accession_number, s.revision, s.received_date, s.collection_date,
       s.collection_time, s.status_id, s.client_reference, s.released_date,
       case
           when s.domain = 'W' then spwo.id
           else o.id
       end,
       case
           when s.domain = 'W' then spwo.name
           else o.name
       end,
       case 
           when s.domain = 'E' then sen.collector
           when s.domain = 'S' then ssd.collector
           when s.domain = 'W' then spw.collector
           else null
       end,
       case 
           when s.domain = 'E' then sen.location
           when s.domain = 'S' then ssd.location
           when s.domain = 'W' then spw.location
           else null
       end,
       case
           when s.domain = 'E' then ead.city
           when s.domain = 'W' then wad.city
           else null
       end,
       p.id, p.name, pws.number0, pws.name, ssd.facility_id,
       case 
           when s.domain = 'C' then cpa.last_name
           when s.domain = 'N' then npa.last_name
           else null
       end,
       case 
           when s.domain = 'C' then cpa.first_name
           when s.domain = 'N' then npa.first_name
           else null
       end,
       case 
           when s.domain = 'C' then cpa.birth_date
           when s.domain = 'N' then npa.birth_date
           else null
       end,
       case 
           when s.domain = 'C' then
               case
                   when pv.last_name is not null then trim(pv.last_name) || ', '
                   else ''
               end ||
               case
                   when pv.first_name is not null then trim(pv.first_name)
                   else ''
               end
           else null
       end,
       a.id, a.revision, a.is_reportable, a.status_id, a.released_date, t.reporting_description, m.reporting_description
from sample s
     join sample_item si on s.id = si.sample_id
     join analysis a on si.id = a.sample_item_id
     join test t on a.test_id = t.id
     join method m on t.method_id = m.id
     left join sample_environmental sen on s.id = sen.sample_id
     left join address ead on sen.location_address_id = ead.id
     left join sample_sdwis ssd on s.id = ssd.sample_id
     left join pws on ssd.pws_id = pws.id
     left join sample_clinical scl on s.id = scl.sample_id
     left join patient cpa on scl.patient_id = cpa.id
     left join provider pv on scl.provider_id = pv.id
     left join sample_neonatal snn on s.id = snn.sample_id
     left join patient npa on snn.patient_id = npa.id
     left join sample_private_well spw on s.id = spw.sample_id
     left join organization spwo on spw.organization_id = spwo.id
     left join address wad on spw.location_address_id = wad.id
     left join sample_organization so on s.id = so.sample_id and
                                         so.type_id = (select dictionary.id
                                                         from dictionary
                                                        where dictionary.system_name = 'org_report_to')
     left join organization o on so.organization_id = o.id
     left join sample_project sp on s.id = sp.sample_id and
                                    sp.is_permanent = 'Y'
     left join project p on sp.project_id = p.id);

create view test_analyte_view (id, test_id, test_name, method_id, method_name,
                               test_is_active, test_active_begin, test_active_end,
                               row_test_analyte_id, row_analyte_id, row_analyte_name,
                               col_analyte_id, col_analyte_name)
                               AS
select ca.id, t.id, t.name, m.id, m.name,
       t.is_active, t.active_begin, t.active_end,
       ra.id, ra.analyte_id, raa.name,
       ca.analyte_id, caa.name
  from test t
       join method m on m.id = t.method_id
       join test_analyte ra on ra.test_id = t.id
       join test_analyte ca on ca.test_id = t.id
       join analyte raa on raa.id = ra.analyte_id
       join analyte caa on caa.id = ca.analyte_id
 where ra.test_id = ca.test_id and
       ra.sort_order = (select max(ta.sort_order) from test_analyte ta
                         where ta.test_id = t.id and
                               ta.row_group = ca.row_group and
                               ta.is_column = 'N' and
                               ta.sort_order <= ca.sort_order)
order by t.name, m.name, ra.sort_order, ca.sort_order;

create view todo_sample_view (sample_id, domain, accession_number, received_date,
                              collection_date, collection_time, primary_organization_name,
                              description, sample_status_id, sample_result_override)
                              AS
                              (
select s.id, s.domain, s.accession_number, s.received_date, s.collection_date,
       s.collection_time,
       case
           when spw.organization_id is not null then spw_org.name
           when spw.report_to_name is not null then spw.report_to_name
           else o.name
       end,
       case 
           when s.domain = 'E' then
               case
                   when sen.priority is not null then '[pri]' || sen.priority || ' '
                   else ''
               end ||
               case
                   when p.name is not null then '[prj]' || trim(p.name) || ' '
                   else ''
               end ||
               case
                   when sen.location is not null then '[loc]' || trim(sen.location)
                   else ''
               end
            when s.domain = 'W' then
                case
                    when spw.owner is not null then '[own]' || trim(spw.owner)
                    else null
                end
            when s.domain = 'S' then
                case
                    when pws.name is not null then '[pws]' || trim(pws.name) || ' '
                    else ''
                end ||
                case
                    when ssd.facility_id is not null then ' [fac]' || trim(ssd.facility_id) || ' '
                    else ''
                end ||
                case
                    when ssd.priority is not null then ' [pri]' || ssd.priority
                    else ''
                end
            when s.domain = 'C' then
                case
                    when pat.last_name is not null then '[lst]' || trim(pat.last_name) || ' '
                    else ''
                end ||
                case
                    when pat.first_name is not null then '[fst]' || trim(pat.first_name) || ' '
                    else ''
                end ||
                case
                    when si.container_reference is not null then '[cnt]' || trim(si.container_reference)
                    else ''
                end
            when s.domain = 'P' then
                case
                    when ptp.entry is not null then '[ptp]' || trim(ptp.entry) || ' '
                    else ''
                end ||
                case
                    when spt.series is not null then '[ser]' || trim(spt.series) || ' '
                    else ''
                end ||
                case
                    when si.container_reference is not null then '[cnt]' || trim(si.container_reference)
                    else ''
                end
            else null
       end,
       s.status_id,
       case
           when sqa.id is not null or
                (s.id in (select sample_id
                            from sample_item
                                 join analysis on analysis.sample_item_id = sample_item.id
                                 join analysis_qaevent on analysis_qaevent.analysis_id = analysis.id and
                                                          analysis_qaevent.type_id = (select dictionary.id from dictionary
                                                                                       where dictionary.system_name = 'qaevent_override')
                           where sample_item.sample_id = s.id)) then "Y"
           else "N"
       end
 from sample s
      join sample_item si on s.id = si.sample_id
      left join sample_environmental sen on s.id = sen.sample_id
      left join sample_private_well spw on s.id = spw.sample_id
      left join organization spw_org on spw.organization_id = spw_org.id
      left join sample_sdwis ssd on s.id = ssd.sample_id
      left join pws on ssd.pws_id = pws.id
      left join sample_clinical scl on s.id = scl.sample_id
      left join patient pat on scl.patient_id = pat.id
      left join sample_pt spt on s.id = spt.sample_id
      left join dictionary ptp on spt.pt_provider_id = ptp.id
      left join sample_organization so on s.id = so.sample_id and
                                          so.type_id = (select dictionary.id
                                                          from dictionary
                                                         where dictionary.system_name = 'org_report_to')
      left join organization o on so.organization_id = o.id
      left join sample_project sp on s.id = sp.sample_id and
                                            sp.is_permanent = 'Y'
      left join project p on sp.project_id = p.id
      left join sample_qaevent sqa on s.id = sqa.sample_id and
                                             sqa.type_id = (select dictionary.id 
                                                              from dictionary
                                                             where dictionary.system_name = 'qaevent_override')
      left join qaevent sq on sqa.qaevent_id = sq.id);

create view worksheet_analysis_view (id, worksheet_item_id, postition, worksheet_id,
                                     format_id, worksheet_description, accession_number,
                                     analysis_id, qc_lot_id, qc_id, worksheet_analysis_id,
                                     system_users, started_date, completed_date,
                                     from_other_id, change_flags_id, description,
                                     test_id, test_name, method_name, time_ta_average,
                                     time_holding, section_name, unit_of_measure_id,
                                     unit_of_measure, analysis_status_id, analysis_status,
                                     collection_date, collection_time, received_date,
                                     priority)
                                     AS
                                     (
select wa.id, wi.id, wi.position, w.id, w.format_id, w.description,
       case 
           when wa.analysis_id is not null then s.accession_number::char(10)
           when wa.qc_lot_id is not null then
               case
                   when wa.from_other_id is not null then wi2.worksheet_id || '.' || wi2.position
                   else wi.worksheet_id || '.' || wi.position
               end
           else null
       end,
       wa.analysis_id, wa.qc_lot_id, q.id, wa.worksheet_analysis_id, wa.system_users,
       wa.started_date, wa.completed_date, wa.from_other_id, wa.change_flags_id,
       case
           when wa.analysis_id is not null then
               case 
                   when s.domain = 'E' then
                       case
                           when sen.location is not null then '[loc]' || trim(sen.location) || ' '
                           else ''
                       end ||
                       case
                           when o.name is not null then '[rpt]' || trim(o.name)
                           else ''
                       end
                   when s.domain = 'S' then
                       case
                           when ssd.location is not null then '[loc]' || trim(ssd.location) || ' '
                           else ''
                       end ||
                       case
                           when o.name is not null then '[rpt]' || trim(o.name)
                           else ''
                       end
                   when s.domain = 'W' then
                       case
                           when spw.location is not null then '[loc]' || trim(spw.location) || ' '
                           else ''
                       end ||
                       case
                           when o.name is not null then '[rpt]' || trim(o.name)
                           else ''
                       end
                   when s.domain = 'C' then
                       case
                           when pat.last_name is not null then '[lst]' || trim(pat.last_name) || ' '
                           else ''
                       end ||
                       case
                           when pat.first_name is not null then '[fst]' || trim(pat.first_name) || ' '
                           else ''
                       end ||
                       case
                           when si.container_reference is not null then '[cnt]' || trim(si.container_reference)
                           else ''
                       end
                   when s.domain = 'P' then
                       case
                           when ptp.entry is not null then '[ptp]' || trim(ptp.entry) || ' '
                           else ''
                       end ||
                       case
                           when spt.series is not null then '[ser]' || trim(spt.series) || ' '
                           else ''
                       end ||
                       case
                           when si.container_reference is not null then '[cnt]' || trim(si.container_reference)
                           else ''
                       end
                   else null
               end
           when wa.qc_lot_id is not null then trim(q.name) || ' (' || ql.lot_number || ')'
           else null
       end,
       t.id, t.name, m.name, t.time_ta_average, t.time_holding, sec.name, a.unit_of_measure_id,
       uom.entry, a.status_id, ans.entry, s.collection_date, s.collection_time, s.received_date,
       case
           when s.domain = 'E' then sen.priority
           when s.domain = 'S' then ssd.priority
           else null::integer
       end
  from worksheet_analysis wa
       join worksheet_item wi on wa.worksheet_item_id = wi.id
       join worksheet w on wi.worksheet_id = w.id
       left join analysis a on wa.analysis_id = a.id
       left join test t on a.test_id = t.id
       left join method m on t.method_id = m.id
       left join section sec on a.section_id = sec.id
       left join sample_item si on a.sample_item_id = si.id
       left join sample s on si.sample_id = s.id
       left join sample_environmental sen on s.id = sen.sample_id
       left join sample_private_well spw on s.id = spw.sample_id
       left join organization spw_org on spw.organization_id = spw_org.id
       left join sample_sdwis ssd on s.id = ssd.sample_id
       left join pws on ssd.pws_id = pws.id
       left join sample_clinical scl on s.id = scl.sample_id
       left join patient pat on scl.patient_id = pat.id
       left join sample_pt spt on s.id = spt.sample_id
       left join dictionary ptp on spt.pt_provider_id = ptp.id
       left join sample_organization so on s.id = so.sample_id and
                                           so.type_id = (select dictionary.id
                                                           from dictionary
                                                          where dictionary.system_name = 'org_report_to')
       left join organization o on so.organization_id = o.id
       left join dictionary uom on a.unit_of_measure_id = uom.id
       left join dictionary ans on a.status_id = ans.id
       left join qc_lot ql on wa.qc_lot_id = ql.id
       left join qc q on ql.qc_id = q.id
       left join worksheet_analysis wa2 on wa.from_other_id = wa2.id
       left join worksheet_item wi2 on wa2.worksheet_item_id = wi2.id);

create view worksheet_qc_result_view (id, worksheet_analysis_id, sort_order, value_1,
                                      value_2, value_3, value_4, value_5, value_6,
                                      value_7, value_8, value_9, value_10, value_11,
                                      value_12, value_13, value_14, value_15, value_16,
                                      value_17, value_18, value_19, value_20, value_21,
                                      value_22, value_23, value_24, value_25, value_26,
                                      value_27, value_28, value_29, value_30, qc_name,
                                      qc_type_id, source, lot_number, location_id,
                                      prepared_date, prepared_volume, prepared_unit_id,
                                      prepared_by_id, usable_date, expire_date,
                                      analyte_id, qc_analyte_type_id, expected_value,
                                      format_id)
                                      AS
                                      (
select wqr.id, wqr.worksheet_analysis_id, wqr.sort_order, wqr.value_1, wqr.value_2,
       wqr.value_3, wqr.value_4, wqr.value_5, wqr.value_6, wqr.value_7, wqr.value_8,
       wqr.value_9, wqr.value_10, wqr.value_11, wqr.value_12, wqr.value_13, wqr.value_14,
       wqr.value_15, wqr.value_16, wqr.value_17, wqr.value_18, wqr.value_19, wqr.value_20,
       wqr.value_21, wqr.value_22, wqr.value_23, wqr.value_24, wqr.value_25, wqr.value_26,
       wqr.value_27, wqr.value_28, wqr.value_29, wqr.value_30, q.name, q.type_id,
       q.source, ql.lot_number, ql.location_id, ql.prepared_date, ql.prepared_volume,
       ql.prepared_unit_id, ql.prepared_by_id, ql.usable_date, ql.expire_date, qa.analyte_id,
       qa.type_id, qa.value, w.format_id
  from worksheet_qc_result wqr
       join worksheet_analysis wa on wqr.worksheet_analysis_id = wa.id
       join worksheet_item wi on wa.worksheet_item_id = wi.id
       join worksheet w on wi.worksheet_id = w.id
       join qc_analyte qa on wqr.qc_analyte_id = qa.id
       join qc_lot ql on wa.qc_lot_id = ql.id
       join qc q on ql.qc_id = q.id);