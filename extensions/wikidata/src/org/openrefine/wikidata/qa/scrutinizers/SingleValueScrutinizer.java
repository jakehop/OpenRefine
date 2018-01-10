package org.openrefine.wikidata.qa.scrutinizers;

import java.util.HashSet;
import java.util.Set;

import org.openrefine.wikidata.qa.QAWarning;
import org.openrefine.wikidata.schema.ItemUpdate;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

/**
 * For now this scrutinizer only checks for uniqueness at
 * the item level (it ignores qualifiers and references).
 * @author antonin
 *
 */
public class SingleValueScrutinizer extends ItemEditScrutinizer {

    @Override
    public void scrutinize(ItemUpdate update) {
        Set<PropertyIdValue> seenSingleProperties = new HashSet<>();
        
        for(Statement statement : update.getAddedStatements()) {
            PropertyIdValue pid = statement.getClaim().getMainSnak().getPropertyId();
            if (seenSingleProperties.contains(pid)) {
                
                QAWarning issue = new QAWarning(
                        "single-valued-property-added-more-than-once",
                        pid.getId(),
                        QAWarning.Severity.WARNING,
                        1);
                issue.setProperty("property_entity", pid);
                issue.setProperty("example_entity", update.getItemId());
                addIssue(issue);
            } else if (_fetcher.hasSingleValue(pid)) {
                seenSingleProperties.add(pid);
            }
        }
    }

}