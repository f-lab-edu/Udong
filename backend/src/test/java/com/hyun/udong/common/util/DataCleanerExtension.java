package com.hyun.udong.common.util;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class DataCleanerExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        DataCleaner dataCleaner = getDataCleaner(context);
        dataCleaner.clear();
    }

    private DataCleaner getDataCleaner(ExtensionContext context) {
        return SpringExtension.getApplicationContext(context).getBean(DataCleaner.class);
    }
}
