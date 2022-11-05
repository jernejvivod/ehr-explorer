package si.jernej.mexplorer.core.manager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Metamodel;
import javax.ws.rs.InternalServerErrorException;

import org.apache.commons.lang3.StringUtils;

import si.jernej.mexplorer.core.util.EntityUtils;

@Dependent
public class MimicEntityManager
{
    @PersistenceContext
    private EntityManager em;

    public Metamodel getMetamodel()
    {
        return em.getMetamodel();
    }

    // TODO redundant select — ID is already in e
    public Stream<Object[]> getEntitiesAndIds(String idPropertyName, String rootEntityName, List<Long> ids)
    {
        final String query = """
                SELECT e, e.%1$s FROM %2$s e
                WHERE e.%1$s IN (:ids)
                """
                .formatted(idPropertyName, rootEntityName);

        return em.createQuery(query, Object[].class)
                .setParameter("ids", ids)
                .getResultStream();
    }

    public record ClinicalTextExtractionQueryResult<T>(
            T clinicalTextEntityId,
            String clinicalText,
            List<LocalDateTime> dateTimeColumnValues
    )
    {
    }

    /**
     * Map clinical text data for a set of root entity IDs.
     *
     * @param rootEntityIds root entity IDs for which to compute the clinical text data
     * @param foreignKeyPath foreign key path from the root entity to the clinical text eny
     * @param rootEntityIdPropertyName ID property name of root entity
     * @param endEntityIdPropertyName ID property name of clinical text entity
     * @param textPropertyName name of clinical text entity property representing the clinical text
     * @param dateTimePropertiesNames list of names of clinical text entity's date/time properties
     * @param <T> root entity ID type
     * @param <S> clinical text entity ID type
     */
    @SuppressWarnings("unchecked")
    public <T, S> Map<T, List<ClinicalTextExtractionQueryResult<S>>> mapRootEntityIdsToClinicalText(
            Set<?> rootEntityIds,
            List<String> foreignKeyPath,
            String rootEntityIdPropertyName,
            String endEntityIdPropertyName,
            String textPropertyName,
            List<String> dateTimePropertiesNames
    )
    {
        // pool of available query variables
        String[] queryVarsPool = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
        List<String> queryVars = Arrays.stream(queryVarsPool).limit(foreignKeyPath.size()).toList();

        // construct query template arguments list
        List<String> queryTemplateArgs = new ArrayList<>(5 + dateTimePropertiesNames.size() * 2 + 1 + (foreignKeyPath.size() - 1) * 3 + dateTimePropertiesNames.size() * 2);

        // add query template arguments for selecting root entity IDs and clinical text entity texts
        queryTemplateArgs.add(rootEntityIdPropertyName);
        queryTemplateArgs.add(queryVars.get(queryVars.size() - 1));
        queryTemplateArgs.add(endEntityIdPropertyName);
        queryTemplateArgs.add(queryVars.get(queryVars.size() - 1));
        queryTemplateArgs.add(textPropertyName);

        // add query template arguments for selecting clinical entity datetime columns values
        for (String dateTimePropertiesName : dateTimePropertiesNames)
        {
            queryTemplateArgs.add(queryVars.get(queryVars.size() - 1));
            queryTemplateArgs.add(dateTimePropertiesName);
        }

        // add query template argument for root entity table from which to select
        queryTemplateArgs.add(foreignKeyPath.get(0));

        // add query template parameters for JOINs
        boolean[] foreignKeyPathIsSingularMask = foreignKeyPathToIsSingularMask(foreignKeyPath);
        for (int i = 0; i < queryVars.size() - 1; i++)
        {
            queryTemplateArgs.add(queryVars.get(i));
            queryTemplateArgs.add(EntityUtils.entityNameToPropertyName(foreignKeyPath.get(i + 1), foreignKeyPathIsSingularMask[i]));
            queryTemplateArgs.add(queryVars.get(i + 1));
        }

        // add query template parameters for ORDER BY
        for (String dateTimePropertiesName : dateTimePropertiesNames)
        {
            queryTemplateArgs.add(queryVars.get(queryVars.size() - 1));
            queryTemplateArgs.add(dateTimePropertiesName);
        }

        // construct dynamic query
        final String dynamicQuery = ("SELECT a.%s, %s.%s, %s.%s" +                 // SELECT root entity IDs, clinical text entity ids, text from clinical text entity
                ", %s.%s".repeat(dateTimePropertiesNames.size()) +                 // SELECT clinical text entity datetime column values
                " FROM %s a" +                                                     // FROM root entity table
                " JOIN %s.%s %s".repeat(foreignKeyPath.size() - 1) +               // construct foreign key path using JOINs
                " WHERE a.%1$s IN (:ids)" +                                        // for specified root entity IDs
                (dateTimePropertiesNames.isEmpty() ? "" : " ORDER BY ") +          // order by specified clinical text entity datetime column values
                StringUtils.repeat("%s.%s", ", ", dateTimePropertiesNames.size()))
                .formatted(queryTemplateArgs.toArray());

        return em.createQuery(dynamicQuery, Object[].class)
                .setParameter("ids", rootEntityIds)
                .getResultStream()
                .collect(Collectors.groupingBy(e -> (T) e[0], Collectors.mapping(e -> new ClinicalTextExtractionQueryResult<>((S) e[1], (String) e[2], Arrays.stream(e).skip(3).map(LocalDateTime.class::cast).toList()), Collectors.toList())));
    }

    /**
     * Get array of boolean values indicating whether a property representing a linked entity on a foreign-key path is singular or not.
     * The resulting boolean array has one less element that the provided foreign-key path (the first element is associated with the second element on the foreign-path key).
     *
     * @param foreignKeyPath foreign-key path
     */
    public boolean[] foreignKeyPathToIsSingularMask(List<String> foreignKeyPath)
    {
        // get name of package containing the entity classes
        Metamodel metamodel = em.getMetamodel();
        final String entityPackageName = em.getMetamodel().getEntities().iterator().next().getJavaType().getPackageName();

        boolean[] res = new boolean[foreignKeyPath.size() - 1];
        for (int i = 0; i < foreignKeyPath.size() - 1; i++)
        {
            final int fpIdx = i;  // final index
            try
            {
                Stream<String> singularAttributeNames = metamodel.entity(Class.forName(entityPackageName + "." + foreignKeyPath.get(fpIdx)))
                        .getSingularAttributes()
                        .stream()
                        .map(Attribute::getName);

                // check if next entity on foreign key path present as linked entity (singular attribute)
                res[i] = singularAttributeNames.anyMatch(a -> a.equals(EntityUtils.entityNameToPropertyName(foreignKeyPath.get(fpIdx + 1), true)));
            }
            catch (ClassNotFoundException e)
            {
                throw new InternalServerErrorException("Invalid entity name specified");
            }
        }
        return res;
    }
}
