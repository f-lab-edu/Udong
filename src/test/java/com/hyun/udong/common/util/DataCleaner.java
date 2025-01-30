package com.hyun.udong.common.util;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataCleaner {

    private static final String FIND_TABLES_QUERY = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'PUBLIC' AND table_name NOT IN ('CITY', 'COUNTRY')";

    private final List<String> tableNames = new ArrayList<>();

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void findDatabaseTableNames() {
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
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        for (String tableName : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE " + tableName + " ALTER COLUMN " + getIdColumnName(tableName) + " RESTART WITH 1").executeUpdate();
        }
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

    private String getIdColumnName(String tableName) {
        return tableName.toUpperCase() + "_ID";
    }
}
