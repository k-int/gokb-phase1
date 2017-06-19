package com.k_int.hibernate;

import com.k_int.hibernate.PgFullTextSearchFunction;

// Converting from Grails3/Hibernate5 to Grails2/Hibernate3, no org.hibernate.dialect.PostgreSQL81Dialect
// import org.hibernate.dialect.PostgreSQL81Dialect;

import org.hibernate.dialect.PostgreSQLDialect;

public class PgSQLCustomDialect extends PostgreSQLDialect {
 
  public PgSQLCustomDialect() {
    registerFunction("textSearch", new PgFullTextSearchFunction());
  }
}
