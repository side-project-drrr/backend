package com.drrr.util;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleaner {

    @PersistenceContext
    private EntityManager entityManager;

    private List<MappingInformation> tableNames;


    @Transactional
    public void clear() {
        this.afterPropertiesSet();
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        for (var mappingInformation : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + mappingInformation.tableName()).executeUpdate();
            entityManager
                    .createNativeQuery(
                            "ALTER TABLE " + mappingInformation.tableName() + " ALTER COLUMN " + mappingInformation.idName() + " RESTART WITH 1")
                    .executeUpdate();
        }

        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

    private void afterPropertiesSet() {
        if (tableNames != null) {
            return;
        }
        this.tableNames = entityManager.getMetamodel()
                .getEntities().stream()
                .filter(e -> e.getJavaType().getAnnotation(Entity.class) != null)
                .map(e -> new MappingInformation(
                        e.getJavaType().getAnnotation(Table.class).name().toUpperCase(),
                        "id"
                ))
                .toList();
    }
}

record MappingInformation(String tableName, String idName) {

}