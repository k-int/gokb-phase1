package com.k_int.hibernate;

import org.hibernate.dialect.PostgreSQL81Dialect;

public class PgSQLCustomDialect extends PostgreSQL81Dialect {
 
  public PgSQLCustomDialect() {
    registerFunction("textSearch", new PgFullTextSearchFunction());
  }
}
