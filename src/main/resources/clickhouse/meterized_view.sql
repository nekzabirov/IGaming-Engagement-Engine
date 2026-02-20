-- Materialized view: aggregates player_spin inserts into daily totals
-- Target table stores pre-aggregated place/settle amounts per player per day

CREATE MATERIALIZED VIEW IF NOT EXISTS player_total_spin_info_mv
TO player_total_spin_info
AS
SELECT
    player_id,
    toDate(created_at) AS date,
    (place_real_amount + place_bonus_amount) AS placeAmount,
    (settle_real_amount + settle_bonus_amount) AS settleAmount,
    if(freespin_id IS NOT NULL, 0, place_real_amount) AS realPlaceAmount,
    if(freespin_id IS NOT NULL, 0, settle_real_amount) AS realSettleAmount
FROM player_spin;

-- Materialized view: aggregates player_invoice inserts into daily totals
-- Target table stores pre-aggregated deposit/withdraw amounts and counts per player per day

CREATE MATERIALIZED VIEW IF NOT EXISTS player_invoice_total_mv
TO player_invoice_total
AS
SELECT
    player_id,
    toDate(created_at) AS date,
    if(type = 'DEPOSIT', amount, 0) AS depositAmount,
    if(type = 'PAYOUT', amount, 0) AS withdrawAmount,
    if(type = 'DEPOSIT', 1, 0) AS depositCount,
    if(type = 'PAYOUT', 1, 0) AS withdrawCount,
    tax_amount AS taxAmount,
    fee_amount AS feesAmount
FROM player_invoice;
