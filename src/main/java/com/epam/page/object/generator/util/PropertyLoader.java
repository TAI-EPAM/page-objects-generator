package com.epam.page.object.generator.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyLoader {

    private String propertyFile;

    private final static Logger logger = LoggerFactory.getLogger(PropertyLoader.class);

    public PropertyLoader(String propertyFile) {
        this.propertyFile = propertyFile;
    }

    public SearchRuleGroupsScheme getMapWithScheme() {
        Map<String, Schema> schemeMap = new HashMap<>();

        logger.info("Start reading list of schemes...");
        try (InputStream propertyStream = PropertyLoader.class.getResourceAsStream(propertyFile)) {
            JSONObject jsonObject = new JSONObject(
                new JSONTokener(propertyStream));

            JSONArray typeGroups = jsonObject.getJSONArray("typeGroups");
            for (int i = 0; i < typeGroups.length(); i++) {

                JSONObject group = typeGroups.getJSONObject(i);
                String groupName = group.getString("name");
                logger.debug("Read '" + groupName + "' group...");

                String schemaPath = group.getString("schema");

                try (InputStream schemaStream = PropertyLoader.class
                    .getResourceAsStream(schemaPath)) {
                    JSONObject jsonSchema = new JSONObject(
                        new JSONTokener(schemaStream));
                    Schema schema = SchemaLoader.load(jsonSchema);
                    schemeMap.put(groupName, schema);
                    logger.debug("Add schema = '" + schemaPath + "' for '" + groupName + "' group");
                } catch (NullPointerException e) {
                    logger.error("Schema = '" + schemaPath + "' doesn't exist!", e);
                    throw new NullPointerException("Schema = '" + schemaPath + "' doesn't exist!");
                } catch (IOException e) {
                    logger.error("Failed reading schema = '" + schemaPath + "'!", e);
                    throw new NullPointerException("Failed reading schema = '" + schemaPath + "'!");
                }
            }
        } catch (NullPointerException e) {
            logger.error("Property file = '" + propertyFile + "' doesn't exist!", e);
            throw new NullPointerException("Property file = '" + propertyFile + "' doesn't exist!");
        } catch (IOException e) {
            logger.error("Failed reading property file = '" + propertyFile + "'!", e);
            throw new NullPointerException(
                "Failed reading property file = '" + propertyFile + "'!");
        }
        logger.info("Finish reading list of schemes\n");

        return new SearchRuleGroupsScheme(schemeMap);
    }

    public SearchRuleGroups getSearchRuleGroupList() {
        List<SearchRuleGroup> searchRuleGroups = new ArrayList<>();

        logger.info("Start reading list of group types...");
        try (InputStream propertyStream = PropertyLoader.class.getResourceAsStream(propertyFile)) {
            JSONObject jsonObject = new JSONObject(
                new JSONTokener(propertyStream));

            JSONArray typeGroups = jsonObject.getJSONArray("typeGroups");
            for (int i = 0; i < typeGroups.length(); i++) {
                JSONObject group = typeGroups.getJSONObject(i);

                String groupName = group.getString("name");
                logger.debug("Read '" + groupName + "' group...");
                List<SearchRuleType> searchRuleTypes = new ArrayList<>();
                JSONArray types = group.getJSONArray("searchRuleTypes");
                for (int j = 0; j < types.length(); j++) {
                    searchRuleTypes
                        .add(SearchRuleType.getSearchRuleTypeByString(types.getString(j)));
                    logger
                        .debug(
                            "Add type = '" + types.getString(j) + "' in '" + groupName + "' group");
                }
                searchRuleGroups.add(new SearchRuleGroup(groupName, searchRuleTypes));
                logger.debug("Finish read group types - '" + groupName + "'\n");
            }
        } catch (NullPointerException e) {
            logger.error("Property file = '" + propertyFile + "' doesn't exist!", e);
            throw new NullPointerException("Property file = '" + propertyFile + "' doesn't exist!");
        } catch (IOException e) {
            logger.error("Failed reading property file = '" + propertyFile + "'!", e);
            throw new NullPointerException(
                "Failed reading property file = '" + propertyFile + "'!");
        }
        logger.info("Finish reading list of group types\n");

        return new SearchRuleGroups(searchRuleGroups);
    }
}
