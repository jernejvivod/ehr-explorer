package com.github.jernejvivod.ehrexplorer.core.test.service;

import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.github.jernejvivod.ehrexplorer.common.exception.ValidationCoreException;
import com.github.jernejvivod.ehrexplorer.core.service.StatsService;
import com.github.jernejvivod.ehrexplorer.core.test.ACoreTest;
import com.github.jernejvivod.ehrexplorerprocessorapi.v1.model.EntityStatsDto;
import com.github.jernejvivod.ehrexplorerprocessorapi.v1.model.PropertyStatsDto;

class StatsServiceTest extends ACoreTest
{
    @Inject
    private StatsService statsService;

    @Test
    void testTableStatsWrongTableName()
    {
        Assertions.assertThrows(ValidationCoreException.class, () -> statsService.tableStats("Wrong"));
    }

    @Test
    void testTableStats()
    {
        String entityName = "AdmissionsEntity";
        EntityStatsDto res = statsService.tableStats(entityName);

        testAdmissionsEntityPropertyStats(res, entityName, 58976, 19, 25332, 75);
    }

    @Test
    @Disabled("very time consuming when running on MIMIC-III")
    void test()
    {
        List<EntityStatsDto> res = statsService.allStats();

        String entityName = "AdmissionsEntity";
        EntityStatsDto EntityStatsDto = res.stream().filter(t -> t.getEntityName().equals("AdmissionsEntity")).findAny().orElseGet(() -> Assertions.fail("entity 'AdmissionsEntity' not found"));
        testAdmissionsEntityPropertyStats(EntityStatsDto, entityName, 58976, 19, 25332, 75);
    }

    private void testAdmissionsEntityPropertyStats(EntityStatsDto EntityStatsDto, String entityName, long expectedNumEntries, int expectedNumColumns, long expectedNumNull, long expectedNumUnique)
    {
        Assertions.assertEquals(entityName, EntityStatsDto.getEntityName());
        Assertions.assertEquals(expectedNumEntries, EntityStatsDto.getNumEntries());
        Assertions.assertEquals(expectedNumColumns, EntityStatsDto.getPropertyStats().size());

        PropertyStatsDto languagePropertyStats = EntityStatsDto.getPropertyStats()
                .stream()
                .filter(s -> s.getPropertyName().equals("language"))
                .findAny()
                .orElseGet(() -> Assertions.fail("property 'language' not found"));

        Assertions.assertEquals(expectedNumNull, languagePropertyStats.getNumNull());
        Assertions.assertEquals(expectedNumUnique, languagePropertyStats.getNumUnique());
    }
}
