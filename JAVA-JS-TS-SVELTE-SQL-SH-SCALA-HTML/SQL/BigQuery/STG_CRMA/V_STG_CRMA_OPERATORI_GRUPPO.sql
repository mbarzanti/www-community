SELECT * EXCEPT(_datepartition),
_datepartition as partitiontime
FROM STG_CRMA.{env_prefix}_t_stg_crma_operatori_gruppo