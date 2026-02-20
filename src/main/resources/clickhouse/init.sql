-- ClickHouse schema init for crm-engine
-- All tables use ReplacingMergeTree with _version for upsert semantics

CREATE TABLE IF NOT EXISTS player_details (
    id String,
    username Nullable(String),
    email Nullable(String),
    phone Nullable(String),
    email_confirmed Bool,
    phone_confirmed Bool,
    status String,
    first_name Nullable(String),
    last_name Nullable(String),
    middle_name Nullable(String),
    birth_date Nullable(Date),
    country Nullable(String),
    locale Nullable(String),
    personal_number Nullable(String),
    is_verified Bool,
    gender Nullable(String),
    address Nullable(String),
    affiliate_tag Nullable(String),
    registered_at DateTime64(3),
    _version UInt64 DEFAULT toUnixTimestamp64Milli(now64(3))
) ENGINE = ReplacingMergeTree(_version)
ORDER BY id;

CREATE TABLE IF NOT EXISTS player_bonus (
    id String,
    identity String,
    player_id String,
    status String,
    amount Int64,
    payout_amount Int64,
    _version UInt64 DEFAULT toUnixTimestamp64Milli(now64(3))
) ENGINE = ReplacingMergeTree(_version)
ORDER BY id;

CREATE TABLE IF NOT EXISTS player_spin (
    id String,
    player_id String,
    freespin_id Nullable(String),
    spin_currency String,
    game String,
    place_real_amount Int64,
    settle_real_amount Int64,
    place_bonus_amount Int64,
    settle_bonus_amount Int64,
    created_at DateTime64(3),
    _version UInt64 DEFAULT toUnixTimestamp64Milli(now64(3))
) ENGINE = ReplacingMergeTree(_version)
ORDER BY (id, player_id, game);

CREATE TABLE IF NOT EXISTS player_freespin (
    id String,
    identity String,
    player_id String,
    game String,
    status String,
    payout_real_amount Int64,
    _version UInt64 DEFAULT toUnixTimestamp64Milli(now64(3))
) ENGINE = ReplacingMergeTree(_version)
ORDER BY (id, player_id);

CREATE TABLE IF NOT EXISTS player_invoice (
    id String,
    player_id String,
    type String,
    status String,
    transaction_currency String,
    amount Int64,
    transaction_amount Int64,
    tax_amount Int64,
    fee_amount Int64,
    created_at DateTime64(3),
    _version UInt64 DEFAULT toUnixTimestamp64Milli(now64(3))
) ENGINE = ReplacingMergeTree(_version)
ORDER BY id;
