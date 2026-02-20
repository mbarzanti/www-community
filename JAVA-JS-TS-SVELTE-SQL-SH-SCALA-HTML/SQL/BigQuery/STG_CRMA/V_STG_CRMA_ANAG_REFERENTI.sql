SELECT * EXCEPT(_datepartition),
_datepartition AS partitiontime
FROM STG_CRMA.{env_prefix}_t_stg_crma_anag_referenti