/*
 * Copyright (C) 2024-2025 Objectos Software LTDA.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
module com.h2database {
  //requires java.compiler;
  //requires jdk.net;
  requires org.slf4j;

  //requires transitive java.desktop;
  //requires transitive java.instrument;
  //requires transitive java.logging;
  //requires transitive java.management;
  requires transitive java.naming;
  //requires transitive java.scripting;
  requires transitive java.sql;
  //requires transitive java.transaction.xa;
  //requires transitive java.xml;

  exports org.h2;
  //exports org.h2.api;
  //exports org.h2.bnf;
  //exports org.h2.bnf.context;
  //exports org.h2.command;
  //exports org.h2.command.ddl;
  //exports org.h2.command.dml;
  //exports org.h2.command.query;
  //exports org.h2.compress;
  //exports org.h2.constraint;
  //exports org.h2.engine;
  //exports org.h2.expression;
  //exports org.h2.expression.aggregate;
  //exports org.h2.expression.analysis;
  //exports org.h2.expression.condition;
  //exports org.h2.expression.function;
  //exports org.h2.expression.function.table;
  //exports org.h2.fulltext;
  //exports org.h2.index;
  //exports org.h2.jdbc;
  //exports org.h2.jdbc.meta;
  exports org.h2.jdbcx;
  //exports org.h2.jmx;
  //exports org.h2.message;
  //exports org.h2.mode;
  //exports org.h2.mvstore;
  //exports org.h2.mvstore.cache;
  //exports org.h2.mvstore.db;
  //exports org.h2.mvstore.rtree;
  //exports org.h2.mvstore.tx;
  //exports org.h2.mvstore.type;
  //exports org.h2.result;
  //exports org.h2.schema;
  //exports org.h2.security;
  //exports org.h2.security.auth;
  //exports org.h2.security.auth.impl;
  //exports org.h2.server;
  //exports org.h2.server.pg;
  //exports org.h2.server.web;
  //exports org.h2.store;
  //exports org.h2.store.fs;
  //exports org.h2.store.fs.async;
  //exports org.h2.store.fs.disk;
  //exports org.h2.store.fs.encrypt;
  //exports org.h2.store.fs.mem;
  //exports org.h2.store.fs.niomapped;
  //exports org.h2.store.fs.niomem;
  //exports org.h2.store.fs.rec;
  //exports org.h2.store.fs.retry;
  //exports org.h2.store.fs.split;
  //exports org.h2.store.fs.zip;
  //exports org.h2.table;
  exports org.h2.tools;
  //exports org.h2.util;
  //exports org.h2.util.geometry;
  //exports org.h2.util.json;
  //exports org.h2.value;
  //exports org.h2.value.lob;

  provides java.sql.Driver with
      org.h2.Driver;
}