package com.hyun.udong.common.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.metamodel.SingularAttribute;

@Component
public class DataCleaner {

    private static final String[] EXCLUDED_TABLES = {"city", "country", "udong_tags"};
    private static final String FIND_TABLES_QUERY = """
                SELECT table_name 
                FROM information_schema.tables 
                WHERE table_schema = 'public' 
                AND table_name NOT IN (%s)
            """.formatted(String.join(", ", wrapWithQuotes(EXCLUDED_TABLES)));
    private final List<String> tableNames = new ArrayList<>();

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void findDatabaseTableNames() {
        System.out.println("FIND_TABLES_QUERY: " + FIND_TABLES_QUERY);
        List<Object> tableInfos = entityManager.createNativeQuery(FIND_TABLES_QUERY).getResultList();
        for (Object tableInfo : tableInfos) {
            tableNames.add((String) tableInfo);
        }
    }

    @Transactional
    public void clear() {
        entityManager.clear();
        truncate();
    }

    public void truncate() {
        String dbProductName = getDatabaseProductName();
        if (dbProductName.contains("H2")) {
            entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
            for (String tableName : tableNames) {
                entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
                entityManager.createNativeQuery("ALTER TABLE " + tableName + " ALTER COLUMN " + getIdColumnName(tableName) + " RESTART WITH 1").executeUpdate();
            }
            entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
        } else if (dbProductName.contains("PostgreSQL")) {
            for (String tableName : tableNames) {
                entityManager.createNativeQuery("TRUNCATE TABLE " + tableName + " CASCADE").executeUpdate();
                String sequenceName = tableName.toLowerCase() + "_" + getIdColumnName(tableName).toLowerCase() + "_seq";
                entityManager.createNativeQuery("ALTER SEQUENCE " + sequenceName + " RESTART WITH 1").executeUpdate();
            }
        }
    }

    private String getDatabaseProductName() {
        try {
            org.hibernate.Session session = entityManager.unwrap(org.hibernate.Session.class);
            return session.doReturningWork(connection -> connection.getMetaData().getDatabaseProductName());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get database product name", e);
        }
    }

    private String getIdColumnName(String tableName) {
        Metamodel metamodel = entityManager.getMetamodel();
        for (EntityType<?> entityType : metamodel.getEntities()) {
            Table table = entityType.getJavaType().getAnnotation(Table.class);
            if (table != null && table.name().equalsIgnoreCase(tableName)) {
                // PK 컬럼명 추출
                Class<?> idType = entityType.getIdType().getJavaType();
                SingularAttribute<?, ?> idAttr = entityType.getId(idType);
                try {
                    Column column = entityType.getJavaType().getDeclaredField(idAttr.getName()).getAnnotation(Column.class);
                    if (column != null && !column.name().isEmpty()) {
                        return column.name();
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
                return idAttr.getName();
            }
        }
        // 못 찾으면 기본 규칙
        return tableName.toLowerCase() + "_id";
    }

    private static String[] wrapWithQuotes(String[] tables) {
        String[] wrappedTables = new String[tables.length];
        for (int i = 0; i < tables.length; i++) {
            wrappedTables[i] = "'" + tables[i] + "'";
        }
        return wrappedTables;
    }
}
