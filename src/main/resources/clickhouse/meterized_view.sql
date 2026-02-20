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
