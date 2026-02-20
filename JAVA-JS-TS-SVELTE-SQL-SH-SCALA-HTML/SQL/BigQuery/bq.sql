SELECT
     foo,
     bar,
     sum(baz) AS sum_value
 FROM fake_table
 GROUP BY
     foo, 2; -- VIOLAZ presenti sia nomi che numeri

SELECT
     foo,
     bar
 FROM fake_table
 ORDER BY
     1, bar;  -- VIOLAZ presenti sia nomi che numeri

SELECT
     foo,
     bar,
     sum(baz) AS sum_value
 FROM fake_table
 GROUP BY
     foo, bar; --OK solo nomi
SELECT
     foo,
     bar
 FROM fake_table
 ORDER BY
     1, 2;  -- OK solo numeri


SELECT
  1 AS customer_id,
  KEYS.NEW_KEYSET('AEAD_AES128_GCM_SIV') AS keyset,  -- VIOLAZ
  b'jaguar' AS favorite_animal;


SELECT
    a, b
FROM foo
ORDER BY a, b DESC; – VIOLAZ: 2 colonne ma una sola DESCE

SELECT
    a, b
FROM foo
ORDER BY a ASC, b DESC; --OK


SELECT
     foo.col1,
     bar.col2
 FROM foo
 RIGHT JOIN bar
     ON foo.bar_id = bar.id;  -- VIOLAZ


SELECT
    a
FROM foo
WHERE a = NULL; -- VIOLAZ

SELECT
    a
FROM foo
WHERE a IS NULL; -- OK


--VIOLAZ
SELECT ifnull(foo, 0) AS bar,   
FROM baz;  
--VIOLAZ

SELECT nvl(foo, 0) AS bar,
FROM baz;
--OK
SELECT coalesce(foo, 0) AS bar,
FROM baz;


SELECT a, b FROM table_1
UNION
SELECT a, b FROM table_2; -- VIOLAZ

SELECT a, b FROM table_1
UNION DISTINCT
SELECT a, b FROM table_2; -- OK


SELECT DISTINCT
    a
FROM foo
GROUP BY a;   -- VIOLAZ


SELECT
    col AS col – VIOLAZ l’alias ha lo stesso nome della colonna
FROM table;


SELECT
    COUNT(o.customer_id) as order_amount,
    c.name
FROM orders as o
JOIN customers as c on o.id = c.user_id;  -- VIOLAZ c’è la AS nella JOIN

SELECT
    COUNT(orders.customer_id) as order_amount,
    customers.name
FROM orders
JOIN customers on orders.id = customers.user_id;  --OK


SELECT
    a
FROM foo AS zoo  -- VIOLAZ l’alias zoo non viene usato nella query
JOIN
    foo AS previous_foo
    ON foo.id = previous_foo.id;

SELECT
    zoo.a
FROM foo AS zoo; – OK zoo viene usato


SELECT
    a as foo,
    b as foo
FROM tbl; –VIOLAZ l’alias foo è usato da due differenti colonne

SELECT
    a as foo,
    b as bar
FROM tbl; --OK gli alias sono unici

SELECT * EXCEPT(_datepartition),
_datepartition as partitiontime
FROM STG_CRMA.{env_prefix}_t_stg_crma_anag_contratti;
SELECT col_a a FROM foo; -- noqa: disable=AL02

SELECT
    voo.a
FROM foo voo; – VIOLAZ voo è un alias implicito

SELECT
    voo.a
FROM foo AS voo; – OK c’è la AS

SELECT
    a alias_col – VIOLAZ alias_col è un alias implicito
FROM foo;

SELECT
    a AS alias_col – OK c’è la AS
FROM foo;

SELECT
    sum(a),  -- VIOLAZ non c’è la AS
    sum(b)  -- VIOLAZ non c’è la AS
FROM foo;

SELECT
    sum(a) AS a_sum, – OK c’è la AS
    sum(b) AS b_sum  – OK c’è la AS
FROM foo;

SELECT
    t.a,
    t.b
FROM foo AS t, bar AS t;  –VIOLAZ l’alias t è usato da due differenti tabelle

SELECT
    f.a,
    b.b
FROM foo AS f, bar AS b;  --OK gli alias sono unici


