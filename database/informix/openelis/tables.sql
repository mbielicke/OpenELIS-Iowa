create table "dba".address
(
    id                        serial not null,
    multiple_unit             varchar(30,1),
    street_address            varchar(30,1),
    city                      varchar(30,1),
    state                     char(2),
    zip_code                  varchar(10,1),
    work_phone                varchar(21,1),
    home_phone                varchar(16,1),
    cell_phone                varchar(16,1),
    fax_phone                 varchar(16,1),
    email                     varchar(80,1),
    country                   varchar(30,1)
);

create table "dba".analysis
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
    printed_date              datetime year to second,
    primary                   key (id)
);

create table "dba".analysis_qaevent
(
    id                        serial not null,
    analysis_id               integer not null,
    qaevent_id                integer not null,
    type_id                   integer not null,
    is_billable               char(1) not null,
    primary                   key (id)
);

create table "dba".analysis_report_flags
(
    analysis_id               integer not null,
    notified_received         char(1),
    notified_released         char(1),
    billed_date               date,
    billed_analytes           smallint,
    billed_override           decimal(8,2)
);

create table "dba".analysis_user
(
    id                        serial not null,
    analysis_id               integer not null,
    system_user_id            integer not null,
    action_id                 integer,
    primary                   key (id)
);

create table "dba".analyte
(
    id                        serial not null,
    name                      char(60) not null,
    is_active                 char(1) not null,
    parent_analyte_id         integer,
    external_id               varchar(20,1),
    primary                   key (id)
);

create table "dba".analyte_parameter
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

create table "dba".attachment
(
    id                        serial not null,
    created_date              datetime year to minute not null,
    type_id                   integer,
    section_id                integer,
    description               varchar(80),
    storage_reference         varchar(255) not null,
    primary                   key (id)
);

create table "dba".attachment_item
(
    id                        serial not null,
    reference_id              integer not null,
    reference_table_id        integer not null,
    attachment_id             integer not null,
    primary                   key (id)
);

create table "dba".aux_data
(
    id                        serial not null,
    sort_order                integer not null,
    aux_field_id              integer not null,
    reference_id              integer not null,
    reference_table_id        integer not null,
    is_reportable             char(1),
    type_id                   integer,
    value                     varchar(80,1),
    primary                   key (id)
);

create table "dba".aux_field
(
    id                        serial not null,
    aux_field_group_id        integer not null,
    sort_order                integer not null,
    analyte_id                integer not null,
    description               varchar(60,1),
    method_id                 integer,
    unit_of_measure_id        integer,
    is_required               char(1),
    is_active                 char(1),
    is_reportable             char(1),
    scriptlet_id              integer,
    primary                   key (id)
);

create table "dba".aux_field_group
(
    id                        serial not null,
    name                      char(20) not null,
    description               varchar(60,1),
    is_active                 char(1) not null,
    active_begin              date not null,
    active_end                date not null
);

create table "dba".aux_field_value
(
    id                        serial not null,
    aux_field_id              integer not null,
    type_id                   integer not null,
    value                     varchar(80,1),
    primary                   key (id)
);

create table "dba".case
(
    id                        serial not null,
    created_date              datetime year to second not null,
    patient_id                integer,
    next_of_kin_id            integer,
    case_patient_id           integer,
    case_next_of_kin_id       integer,
    organization_id           integer,
    completed_date            datetime year to second,
    is_finalized              char(1)
);

create table "dba".case_analysis
(
    id                        serial not null,
    case_id                   integer not null,
    accession_number          integer not null,
    organization_id           integer not null,
    test_id                   integer not null,
    status_id                 integer not null,
    collection_date           datetime year to second,
    completed_date            datetime year to second,
    condition_id              integer
);

create table "dba".case_contact
(
    id                        serial not null,
    source_reference_id       varchar(30),
    source_reference          integer not null,
    last_name                 varchar(30),
    first_name                varchar(30),
    type_id                   integer not null,
    npi                       varchar(10)
);

create table "dba".case_contact_location
(
    id                        serial not null,
    case_contact_id           integer not null,
    location                  varchar(50),
    address_id                integer not null
);

create table "dba".case_patient
(
    id                        serial not null,
    last_name                 varchar(30),
    first_name                varchar(20),
    maiden_name               varchar(30),
    address_id                integer,
    birth_date                date,
    birth_time                datetime hour to second,
    gender_id                 integer,
    race_id                   integer,
    ethnicity_id              integer,
    national_id               varchar(20)
);

create table "dba".case_provider
(
    id                        serial not null,
    case_id                   integer not null,
    case_contact_id           integer not null,
    type_id                   integer
);

create table "dba".case_result
(
    id                        serial not null,
    case_analysis_id          integer not null,
    test_analyte_id           integer not null,
    test_result_id            integer not null,
    row                       smallint not null,
    col                       smallint not null,
    is_reportable             char(1),
    analyte_id                integer not null,
    type_id                   integer not null,
    value                     varchar(80)
);

create table "dba".case_tag
(
    id                        serial not null,
    case_id                   integer not null,
    type_id                   integer not null,
    system_user_id            integer not null,
    created_date              datetime year to second not null,
    remind_date               datetime year to second not null,
    completed_date            datetime year to second,
    note                      text
);

create table "dba".case_user
(
    id                        serial not null,
    case_id                   integer not null,
    system_user_id            integer,
    section_id                integer,
    action_id                 integer
);

create table "dba".category
(
    id                        serial not null,
    system_name               varchar(30,1) not null,
    name                      varchar(50,1),
    description               varchar(60,1),
    section_id                integer,
    is_system                 char(1) not null
);

create table "dba".cron
(
    id                        serial not null,
    name                      char(30) not null,
    is_active                 char(1) not null,
    cron_tab                  char(30) not null,
    bean                      varchar(255) not null,
    method                    varchar(255) not null,
    parameters                varchar(255),
    last_run                  datetime year to second
);

create table "dba".dictionary
(
    id                        serial not null,
    category_id               integer not null,
    sort_order                integer not null,
    related_entry_id          integer,
    system_name               varchar(30,1),
    is_active                 char(1) not null,
    code                      varchar(10,1),
    entry                     varchar(255,1) not null,
    primary                   key (id)
);

create table "dba".eorder
(
    id                        serial not null,
    entered_date              datetime year to second not null,
    paper_order_validator     varchar(40) not null,
    description               varchar(60),
    primary                   key (id)
);

create table "dba".eorder_body
(
    id                        serial not null,
    eorder_id                 integer not null,
    xml                       text,
    primary                   key (id)
);

create table "dba".eorder_link
(
    id                        serial not null,
    eorder_id                 integer not null,
    reference                 varchar(40),
    sub_id                    varchar(20),
    name                      varchar(20) not null,
    value                     varchar(255),
    primary                   key (id)
);

create table "dba".event_log
(
    id                        serial not null,
    type_id                   integer not null,
    source                    varchar(60,1) not null,
    reference_table_id        integer,
    reference_id              integer,
    level_id                  integer not null,
    system_user_id            integer not null,
    timestamp                 datetime year to second not null,
    text                      text
);

create table "dba".exchange_criteria
(
    id                        serial not null,
    name                      char(20) not null,
    environment_id            integer,
    destination_uri           varchar(60),
    is_all_analyses_included  char(1) not null,
    query                     text
);

create table "dba".exchange_external_term
(
    id                        serial not null,
    exchange_local_term_id    integer not null,
    profile_id                integer not null,
    is_active                 char(1) not null,
    external_term             varchar(60,1) not null,
    external_description      varchar(255),
    external_coding_system    varchar(60,1),
    version                   varchar(60,1)
);

create table "dba".exchange_local_term
(
    id                        serial not null,
    reference_table_id        integer not null,
    reference_id              integer not null
);

create table "dba".exchange_profile
(
    id                        serial not null,
    exchange_criteria_id      integer not null,
    profile_id                integer not null,
    sort_order                integer not null
);

create table "dba".history
(
    id                        serial not null,
    reference_id              integer not null,
    reference_table_id        integer not null,
    timestamp                 datetime year to second not null,
    activity_id               integer not null,
    system_user_id            integer not null,
    changes                   text,
    primary                   key (id)
);

create table "dba".instrument
(
    id                        serial not null,
    name                      char(20) not null,
    description               varchar(60,1),
    model_number              varchar(40,1),
    serial_number             varchar(40,1),
    type_id                   integer not null,
    location                  char(60) not null,
    is_active                 char(1) not null,
    active_begin              date,
    active_end                date,
    scriptlet_id              integer,
    primary                   key (id)
);

create table "dba".instrument_log
(
    id                        serial not null,
    instrument_id             integer not null,
    type_id                   integer not null,
    worksheet_id              integer,
    event_begin               datetime year to second not null,
    event_end                 datetime year to second,
    text                      text,
    primary                   key (id)
);

create table "dba".inventory_adjustment
(
    id                        serial not null,
    description               varchar(60,1),
    system_user_id            integer not null,
    adjustment_date           datetime year to minute not null
);

create table "dba".inventory_component
(
    id                        serial not null,
    inventory_item_id         integer not null,
    component_id              integer not null,
    quantity                  integer not null,
    primary                   key (id)
);

create table "dba".inventory_item
(
    id                        serial not null,
    name                      char(30) not null,
    description               varchar(60,1),
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
    parent_ratio              integer,
    primary                   key (id)
);

create table "dba".inventory_location
(
    id                        serial not null,
    inventory_item_id         integer not null,
    lot_number                varchar(20,1),
    storage_location_id       integer not null,
    quantity_onhand           integer not null,
    expiration_date           date,
    primary                   key (id)
);

create table "dba".inventory_receipt
(
    id                        serial not null,
    inventory_item_id         integer not null,
    order_item_id             integer,
    organization_id           integer,
    received_date             datetime year to second not null,
    quantity_received         integer not null,
    unit_cost                 float,
    qc_reference              varchar(20,1),
    external_reference        varchar(20,1),
    upc                       varchar(15,1),
    primary                   key (id)
);

create table "dba".inventory_receipt_order_item
(
    id                        serial not null,
    inventory_receipt_id      integer not null,
    order_item_id             integer not null
);

create table "dba".inventory_x_adjust
(
    id                        serial not null,
    inventory_adjustment_id   integer not null,
    inventory_location_id     integer not null,
    quantity                  integer not null,
    physical_count            integer not null
);

create table "dba".inventory_x_put
(
    id                        serial not null,
    inventory_receipt_id      integer not null,
    inventory_location_id     integer not null,
    quantity                  integer not null
);

create table "dba".inventory_x_use
(
    id                        serial not null,
    inventory_location_id     integer not null,
    order_item_id             integer not null,
    quantity                  integer not null
);

create table "dba".label
(
    id                        serial not null,
    name                      varchar(30,1) not null,
    description               varchar(60,1),
    printer_type_id           integer not null,
    scriptlet_id              integer,
    primary                   key (id)
);

create table "dba".lock
(
    reference_table_id        integer not null,
    reference_id              integer not null,
    expires                   int8 not null,
    system_user_id            integer not null,
    session_id                varchar(80)
);

create table "dba".method
(
    id                        serial not null,
    name                      char(20) not null,
    description               varchar(60,1),
    reporting_description     varchar(60,1),
    is_active                 char(1) not null,
    active_begin              date not null,
    active_end                date not null,
    primary                   key (id)
);

create table "dba".note
(
    id                        serial not null,
    reference_id              integer not null,
    reference_table_id        integer not null,
    timestamp                 datetime year to second not null,
    is_external               char(1) not null,
    system_user_id            integer not null,
    subject                   varchar(60,1),
    text                      text,
    primary                   key (id)
);

create table "dba".order
(
    id                        serial not null,
    parent_order_id           integer,
    description               varchar(60,1),
    status_id                 integer not null,
    ordered_date              date not null,
    needed_in_days            integer,
    requested_by              varchar(30,1),
    cost_center_id            integer,
    organization_id           integer,
    organization_attention    varchar(30,1),
    type                      char(1) not null,
    external_order_number     varchar(20,1),
    ship_from_id              integer,
    number_of_forms           integer,
    primary                   key (id)
);

create table "dba".order_container
(
    id                        serial not null,
    order_id                  integer not null,
    container_id              integer not null,
    item_sequence             integer not null,
    type_of_sample_id         integer
);

create table "dba".order_item
(
    id                        serial not null,
    order_id                  integer not null,
    inventory_item_id         integer not null,
    quantity                  integer not null,
    catalog_number            varchar(30,1),
    unit_cost                 float,
    primary                   key (id)
);

create table "dba".order_organization
(
    id                        serial not null,
    order_id                  integer not null,
    organization_id           integer not null,
    organization_attention    varchar(30),
    type_id                   integer not null
);

create table "dba".order_recurrence
(
    id                        serial not null,
    order_id                  integer not null,
    is_active                 char(1) not null,
    active_begin              date,
    active_end                date,
    frequency                 smallint,
    unit_id                   integer
);

create table "dba".order_test
(
    id                        serial not null,
    order_id                  integer not null,
    item_sequence             integer not null,
    sort_order                integer not null,
    test_id                   integer not null
);

create table "dba".order_test_analyte
(
    id                        serial not null,
    order_test_id             integer not null,
    analyte_id                integer not null
);

create table "dba".organization
(
    id                        serial not null,
    parent_organization_id    integer,
    name                      char(40) not null,
    is_active                 char(1) not null,
    address_id                integer,
    primary                   key (id)
);

create table "dba".organization_contact
(
    id                        serial not null,
    organization_id           integer not null,
    contact_type_id           integer not null,
    name                      varchar(30,1) not null,
    address_id                integer,
    primary                   key (id)
);

create table "dba".organization_parameter
(
    id                        serial not null,
    organization_id           integer not null,
    type_id                   integer not null,
    value                     varchar(80,1)
);

create table "dba".panel
(
    id                        serial not null,
    name                      char(20) not null,
    description               varchar(60,1),
    primary                   key (id)
);

create table "dba".panel_item
(
    id                        serial not null,
    panel_id                  integer not null,
    type                      char(1) not null,
    sort_order                integer not null,
    name                      char(20) not null,
    method_name               char(20),
    primary                   key (id)
);

create table "dba".patient
(
    id                        serial not null,
    last_name                 char(30),
    first_name                char(20),
    middle_name               char(20),
    address_id                integer,
    birth_date                date,
    birth_time                datetime hour to minute,
    gender_id                 integer,
    race_id                   integer,
    ethnicity_id              integer,
    national_id               varchar(20),
    primary                   key (id)
);

create table "dba".patient_relation
(
    id                        serial not null,
    relation_id               integer not null,
    patient_id                integer not null,
    related_patient_id        integer not null,
    primary                   key (id)
);

create table "dba".preferences
(
    system_user_id            integer not null,
    text                      text
);

create table "dba".project
(
    id                        serial not null,
    name                      char(20) not null,
    description               varchar(60,1),
    started_date              date,
    completed_date            date,
    is_active                 char(1) not null,
    reference_to              varchar(20,1),
    owner_id                  integer not null,
    scriptlet_id              integer,
    primary                   key (id)
);

create table "dba".project_parameter
(
    id                        serial not null,
    project_id                integer not null,
    parameter                 varchar(80,1) not null,
    operation_id              integer not null,
    value                     varchar(255,1) not null,
    primary                   key (id)
);

create table "dba".provider
(
    id                        serial not null,
    last_name                 char(30),
    first_name                char(20),
    middle_name               char(20),
    type_id                   integer,
    npi                       varchar(10,1),
    reference_id              varchar(40),
    reference_source_id       integer,
    primary                   key (id)
);

create table "dba".provider_location
(
    id                        serial not null,
    location                  varchar(50,10) not null,
    external_id               varchar(10,1),
    provider_id               integer not null,
    address_id                integer,
    primary                   key (id)
);

create table "dba".pws
(
    id                        serial not null,
    tinwsys_is_number         integer not null,
    number0                   char(12) not null,
    alternate_st_num          char(5),
    name                      varchar(40,1),
    activity_status_cd        char(1),
    d_prin_city_svd_nm        varchar(40,1),
    d_prin_cnty_svd_nm        varchar(40,1),
    d_population_count        integer,
    d_pws_st_type_cd          char(4),
    activity_rsn_txt          varchar(255,1),
    start_day                 integer,
    start_month               integer,
    end_day                   integer,
    end_month                 integer,
    eff_begin_dt              date,
    eff_end_dt                date,
    primary                   key (id)
);

create table "dba".pws_address
(
    tinwslec_is_number        integer not null,
    tinlgent_is_number        integer not null,
    tinwsys_is_number         integer not null,
    type_code                 char(3),
    active_ind_cd             char(1),
    name                      varchar(40,1),
    addr_line_one_txt         varchar(40,1),
    addr_line_two_txt         varchar(40,1),
    address_city_name         varchar(40,1),
    address_state_code        char(2),
    address_zip_code          char(10),
    state_fips_code           char(2),
    phone_number              char(12)
);

create table "dba".pws_facility
(
    tinwsf_is_number          integer not null,
    tsasmppt_is_number        integer,
    tinwsys_is_number         integer not null,
    name                      varchar(40,1),
    type_code                 char(2),
    st_asgn_ident_cd          char(12),
    activity_status_cd        char(1),
    water_type_code           char(3),
    availability_code         char(1),
    identification_cd         char(11),
    description_text          varchar(20,1),
    source_type_code          char(2)
);

create table "dba".pws_monitor
(
    tiamrtask_is_number       integer,
    tinwsys_is_number         integer not null,
    st_asgn_ident_cd          char(12),
    name                      varchar(40,1),
    tiaanlgp_tiaanlyt_name    varchar(64,1),
    number_samples            integer,
    comp_begin_date           date,
    comp_end_date             date,
    frequency_name            varchar(25,1),
    period_name               varchar(20,1)
);

create table "dba".pws_violation
(
    id                        serial not null,
    tinwsys_is_number         integer not null,
    st_asgn_ident_cd          char(12) not null,
    series                    varchar(64) not null,
    violation_date            date not null,
    sample_id                 integer
);

create table "dba".qaevent
(
    id                        serial not null,
    name                      char(20) not null,
    description               varchar(60,1),
    test_id                   integer,
    type_id                   integer not null,
    is_billable               char(1) not null,
    reporting_sequence        integer,
    reporting_text            text not null,
    primary                   key (id)
);

create table "dba".qc
(
    id                        serial not null,
    name                      char(30) not null,
    type_id                   integer,
    inventory_item_id         integer,
    source                    varchar(30,1) not null,
    is_active                 char(1) not null,
    primary                   key (id)
);

create table "dba".qc_analyte
(
    id                        serial not null,
    qc_id                     integer not null,
    sort_order                integer not null,
    analyte_id                integer not null,
    type_id                   integer not null,
    value                     varchar(80,1),
    is_trendable              char(1),
    primary                   key (id)
);

create table "dba".qc_lot
(
    id                        serial not null,
    qc_id                     integer not null,
    lot_number                varchar(30,1) not null,
    location_id               integer,
    prepared_date             datetime year to second not null,
    prepared_volume           float,
    prepared_unit_id          integer,
    prepared_by_id            integer,
    usable_date               datetime year to second not null,
    expire_date               datetime year to second not null,
    is_active                 char(1) not null
);

create table "dba".result
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
    value                     varchar(80,1)
);

create table "dba".sample
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
    collection_date           datetime year to second,
    collection_time           datetime hour to second,
    status_id                 integer not null,
    package_id                integer,
    client_reference          char(20),
    released_date             datetime year to second,
    primary                   key (id)
);

create table "dba".sample_animal
(
    id                        serial not null,
    sample_id                 integer not null,
    animal_common_name_id     integer,
    animal_scientific_name_id integer,
    collector                 varchar(40,1),
    collector_phone           varchar(21,1),
    sampling_location         varchar(40,1),
    address_id                integer,
    primary                   key (id)
);

create table "dba".sample_clinical
(
    id                        serial not null,
    sample_id                 integer not null,
    patient_id                integer,
    provider_id               integer,
    provider_phone            varchar(21,1),
    primary                   key (id)
);

create table "dba".sample_environmental
(
    id                        serial not null,
    sample_id                 integer not null,
    is_hazardous              char(1) not null,
    priority                  integer,
    description               varchar(40,1),
    collector                 varchar(40,1),
    collector_phone           varchar(21,1),
    location                  varchar(40,1),
    location_address_id       integer,
    primary                   key (id)
);

create table "dba".sample_item
(
    id                        serial not null,
    sample_id                 integer not null,
    sample_item_id            integer,
    item_sequence             integer not null,
    type_of_sample_id         integer,
    source_of_sample_id       integer,
    source_other              varchar(40,1),
    container_id              integer,
    container_reference       varchar(40),
    quantity                  float,
    unit_of_measure_id        integer,
    primary                   key (id)
);

create table "dba".sample_neonatal
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

create table "dba".sample_organization
(
    id                        serial not null,
    sample_id                 integer not null,
    organization_id           integer not null,
    organization_attention    varchar(30,1),
    type_id                   integer not null,
    primary                   key (id)
);

create table "dba".sample_private_well
(
    id                        serial not null,
    sample_id                 integer not null,
    organization_id           integer,
    report_to_name            varchar(30,1),
    report_to_attention       varchar(30,1),
    report_to_address_id      integer,
    location                  varchar(40,1),
    location_address_id       integer,
    owner                     varchar(30,1),
    collector                 varchar(30,1),
    well_number               integer
);

create table "dba".sample_project
(
    id                        serial not null,
    sample_id                 integer not null,
    project_id                integer not null,
    is_permanent              char(1) not null,
    primary                   key (id)
);

create table "dba".sample_pt
(
    id                        serial not null,
    sample_id                 integer not null,
    pt_provider_id            integer not null,
    series                    varchar(50) not null,
    due_date                  datetime year to minute,
    additional_domain         char(1)
);

create table "dba".sample_qaevent
(
    id                        serial not null,
    sample_id                 integer not null,
    qaevent_id                integer not null,
    type_id                   integer not null,
    is_billable               char(1) not null
);

create table "dba".sample_sdwis
(
    id                        serial not null,
    sample_id                 integer not null,
    pws_id                    integer not null,
    state_lab_id              smallint,
    facility_id               char(12),
    sample_type_id            integer,
    sample_category_id        integer,
    sample_point_id           char(11),
    priority                  integer,
    location                  varchar(40,1),
    collector                 char(20)
);

create table "dba".scriptlet
(
    id                        serial not null,
    name                      varchar(40) not null,
    bean                      varchar(255) not null,
    is_active                 char(1) not null,
    active_begin              date not null,
    active_end                date not null
);

create table "dba".section
(
    id                        serial not null,
    parent_section_id         integer,
    name                      char(20) not null,
    description               varchar(60,1),
    is_external               char(1) not null,
    organization_id           integer,
    primary                   key (id)
);

create table "dba".section_parameter
(
    id                        serial not null,
    section_id                integer not null,
    type_id                   integer not null,
    value                     varchar(80,1)
);

create table "dba".shipping
(
    id                        serial not null,
    status_id                 integer not null,
    shipped_from_id           integer,
    shipped_to_id             integer,
    shipped_to_attention      varchar(30,1),
    processed_by              varchar(30,1),
    processed_date            datetime year to second,
    shipped_method_id         integer,
    shipped_date              datetime year to second,
    number_of_packages        integer,
    cost                      decimal(9,2)
);

create table "dba".shipping_item
(
    id                        serial not null,
    shipping_id               integer not null,
    reference_table_id        integer not null,
    reference_id              integer not null,
    quantity                  integer not null,
    description               varchar(80,1) not null
);

create table "dba".shipping_tracking
(
    id                        serial not null,
    shipping_id               integer not null,
    tracking_number           varchar(30,1) not null
);

create table "dba".standard_note
(
    id                        serial not null,
    name                      char(20) not null,
    description               varchar(60,1),
    type_id                   integer,
    text                      text
);

create table "dba".storage
(
    id                        serial not null,
    reference_id              integer,
    reference_table_id        integer,
    storage_location_id       integer,
    checkin                   datetime year to minute,
    checkout                  datetime year to minute,
    system_user_id            integer
);

create table "dba".storage_location
(
    id                        serial not null,
    sort_order                integer,
    name                      char(20),
    location                  varchar(80,1) not null,
    parent_storage_location_id integer,
    storage_unit_id           integer not null,
    is_available              char(1) not null,
    primary                   key (id)
);

create table "dba".storage_unit
(
    id                        serial not null,
    category_id               integer not null,
    description               varchar(60,1) not null,
    is_singular               char(1) not null,
    primary                   key (id)
);

create table "dba".system_variable
(
    id                        serial not null,
    name                      varchar(30,1) not null,
    value                     varchar(255,1),
    primary                   key (id)
);

create table "dba".test
(
    id                        serial not null,
    name                      char(20) not null,
    description               varchar(60,1),
    reporting_description     varchar(60,1),
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
    reporting_sequence        integer,
    primary                   key (id)
);

create table "dba".test_analyte
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

create table "dba".test_prep
(
    id                        serial not null,
    test_id                   integer not null,
    prep_test_id              integer not null,
    is_optional               char(1)
);

create table "dba".test_reflex
(
    id                        serial not null,
    test_id                   integer not null,
    test_analyte_id           integer not null,
    test_result_id            integer not null,
    flags_id                  integer not null,
    add_test_id               integer not null,
    primary                   key (id)
);

create table "dba".test_result
(
    id                        serial not null,
    test_id                   integer not null,
    result_group              integer not null,
    sort_order                integer not null,
    unit_of_measure_id        integer,
    type_id                   integer not null,
    value                     varchar(80,1),
    significant_digits        integer,
    rounding_method_id        integer,
    flags_id                  integer
);

create table "dba".test_section
(
    id                        serial not null,
    test_id                   integer not null,
    section_id                integer not null,
    flag_id                   integer
);

create table "dba".test_trailer
(
    id                        serial not null,
    name                      char(20) not null,
    description               varchar(60,1),
    text                      text,
    primary                   key (id)
);

create table "dba".test_type_of_sample
(
    id                        serial not null,
    test_id                   integer not null,
    type_of_sample_id         integer not null,
    unit_of_measure_id        integer
);

create table "dba".test_worksheet
(
    id                        serial not null,
    test_id                   integer not null,
    subset_capacity           integer not null,
    total_capacity            integer not null,
    format_id                 integer not null,
    scriptlet_id              integer,
    primary                   key (id)
);

create table "dba".test_worksheet_analyte
(
    id                        serial not null,
    test_id                   integer not null,
    test_analyte_id           integer not null,
    repeat                    integer not null,
    flag_id                   integer
);

create table "dba".test_worksheet_item
(
    id                        serial not null,
    test_worksheet_id         integer not null,
    sort_order                integer not null,
    position                  integer,
    type_id                   integer not null,
    qc_name                   varchar(30,1),
    primary                   key (id)
);

create table "dba".worksheet
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

create table "dba".worksheet_analysis
(
    id                        serial not null,
    worksheet_item_id         integer not null,
    accession_number          varchar(10,1),
    analysis_id               integer,
    qc_lot_id                 integer,
    worksheet_analysis_id     integer,
    system_users              varchar(60),
    started_date              datetime year to second,
    completed_date            datetime year to second,
    from_other_id             integer,
    change_flags_id           integer,
    primary                   key (id)
);

create table "dba".worksheet_item
(
    id                        serial not null,
    worksheet_id              integer not null,
    position                  integer
);

create table "dba".worksheet_qc_result
(
    id                        serial not null,
    worksheet_analysis_id     integer not null,
    sort_order                integer not null,
    qc_analyte_id             integer not null,
    value_1                   varchar(80,1),
    value_2                   varchar(80,1),
    value_3                   varchar(80,1),
    value_4                   varchar(80,1),
    value_5                   varchar(80,1),
    value_6                   varchar(80,1),
    value_7                   varchar(80,1),
    value_8                   varchar(80,1),
    value_9                   varchar(80,1),
    value_10                  varchar(80,1),
    value_11                  varchar(80,1),
    value_12                  varchar(80,1),
    value_13                  varchar(80,1),
    value_14                  varchar(80,1),
    value_15                  varchar(80,1),
    value_16                  varchar(80,1),
    value_17                  varchar(80,1),
    value_18                  varchar(80,1),
    value_19                  varchar(80,1),
    value_20                  varchar(80,1),
    value_21                  varchar(80,1),
    value_22                  varchar(80,1),
    value_23                  varchar(80,1),
    value_24                  varchar(80,1),
    value_25                  varchar(80,1),
    value_26                  varchar(80,1),
    value_27                  varchar(80,1),
    value_28                  varchar(80,1),
    value_29                  varchar(80,1),
    value_30                  varchar(80,1)
);

create table "dba".worksheet_reagent
(
    id                        serial not null,
    worksheet_id              integer not null,
    sort_order                integer not null,
    qc_lot_id                 integer not null
);

create table "dba".worksheet_result
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