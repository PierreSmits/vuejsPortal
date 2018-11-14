package org.apache.ofbiz.widget.renderer.frontjs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.ofbiz.base.util.UtilGenerics;
import org.apache.ofbiz.base.util.UtilMisc;

public class FrontJsOutput {
    public static final String module = FrontJsOutput.class.getName();

	
	Map<String, Object> output = new HashMap<>();
    private List<Map<String, Object>> viewScreen;
    private Map<String, Object> viewEntities = new HashMap<>();
    
    // stack of screen, a screen is a list of element (see list of renderer method, each one is a element)
    private Stack<List<Map<String, Object>>> screensStack;

    // stack of entity which is currently used for store records
    // contain 3 field
    // - "primaryKeys"  list of fieldName which are pk in entityName. Used to build pkValue for storePointer
    // - "list" list of records (each record contain its stId)
    // - "entityName"
    private Stack<Map<String, Object>> entitiesStack;

    // use to build record, a map with content of fields (fieldName : value) for a row or a single form
    //   similar as a GenericValue but with fields define in the form
    // it's a stack because in a field I can have a sub-list about an other entities
    private Stack<Map<String, Object>> recordsStack;

    FrontJsOutput(String name) {
        viewScreen = new ArrayList<>();
        output.put("viewScreenName", name);
        output.put("viewScreen", viewScreen);
        output.put("viewEntities", viewEntities);
        screensStack = new Stack<>();
        entitiesStack = new Stack<>();
        recordsStack = new Stack<>();
        screensStack.push(viewScreen);
    }

    void putScreen(Map<String, Object> screen) {
        Map<String, Object> temp = new HashMap<>();
        String name = screen.keySet().toArray()[0].toString();
        temp.put("attributes", screen.get(name));
        temp.put("name", name);
        screen = temp;
        screensStack.peek().add(screen);
        if (screen.containsKey("attributes") && 
           ((Map<String, Object>) screen.get("attributes")).containsKey("data") && 
           ((Map<String, Object>) ((Map<String, Object>) screen.get("attributes")).get("data")).containsKey("action") && 
           ((Map<String, Object>) ((Map<String, Object>) screen.get("attributes")).get("data")).get("action").equals("PUT_RECORD")) {
            this.putRecord((ArrayList<Map<String, Object>>) ((Map<String, Object>) ((Map<String, Object>) screen.get("attributes")).get("data")).get("records"));
        }
    }
    void putScreen(String name, Map<String, Object> attributes) {
    	putScreen(name, attributes, null, null);
    }
    void putScreen(String name, Map<String, Object> attributes, String fieldName, String value) {
        Map<String, Object> screen = new HashMap<>();
        screen.put("attributes", attributes);
        screen.put("name", name);
        screensStack.peek().add(screen);
        if (fieldName != null && !recordsStack.empty()) {
            Map<String, String> stPointer = new HashMap<>();
            stPointer.put("stEntityName", (String) this.entitiesStack.peek().get("entityName"));
            stPointer.put("id", (String) recordsStack.peek().get("stId"));
            stPointer.put("field", fieldName);
            screen.put("stPointer", stPointer);
            screen.put("dataDebug", UtilMisc.toMap("action","PUT_RECORD", "key",fieldName, "value",value));
            this.putRecord(fieldName, value);
        }
    }

    void pushScreen(Map<String, Object> screen) {
        Map<String, Object> temp = new HashMap<>();
        String name = screen.keySet().toArray()[0].toString();
        temp.put("attributes", screen.get(name));
        temp.put("children", new ArrayList<Map<String, Object>>());
        temp.put("name", name.replace("Open", ""));
        screen = temp;
        screensStack.peek().add(screen);
        screensStack.push((ArrayList<Map<String, Object>>) screen.get("children"));
        if (screen.containsKey("attributes") && ((Map<String, Object>) screen.get("attributes")).containsKey("data") && ((Map<String, Object>) ((Map<String, Object>) screen.get("attributes")).get("data")).containsKey("action")) {
            if (((Map<String, Object>) ((Map<String, Object>) screen.get("attributes")).get("data")).get("action").equals("NEW_RECORD")) {
                this.newRecord();
            }
            if (((Map<String, Object>) ((Map<String, Object>) screen.get("attributes")).get("data")).get("action").equals("PUT_ENTITY")) {
                Map<String, Object> data = (Map<String, Object>) ((Map<String, Object>) screen.get("attributes")).get("data");
                this.putEntity((String) data.get("entityName"), (List<String>) data.get("primaryKeys"));
            }
        }
    }
    void pushScreen(String name, Map<String, Object> attributes, boolean newRecord, Map<String, Object> context) {
        Map<String, Object> screen = new HashMap<>();
        screen.put("name", name.replace("Open", ""));
        screen.put("attributes", attributes);

        List<Map<String, Object>> children = new ArrayList<Map<String, Object>>(); 
        screen.put("children", children);
        screensStack.peek().add(screen);
        screensStack.push( children);
        if (newRecord) {
        	this.newRecord(context);
        	screen.put("dataDebug", UtilMisc.toMap("action","NEW_RECORD"));
        }
    }

    void popScreen(Map<String, Object> screen) {
        screensStack.pop();
        String name = screen.keySet().toArray()[0].toString();
        if (((Map<String, Object>) screen.get(name)).containsKey("data") && ((Map<String, Object>) ((Map<String, Object>) screen.get(name)).get("data")).containsKey("action")) {
            if (((Map<String, Object>) ((Map<String, Object>) screen.get(name)).get("data")).get("action").equals("STORE_RECORD")) {
                this.storeRecord();
            }
            if (((Map<String, Object>) ((Map<String, Object>) screen.get(name)).get("data")).get("action").equals("POP_ENTITY")) {
                this.popEntity();
            }
        }
    }


	private void putEntity(String entityName, List<String> primaryKeys) {
        Map<String, Object> entity;
        if (!viewEntities.containsKey(entityName)) {
            entity = new HashMap<>();
            entity.put("primaryKeys", primaryKeys);
            entity.put("list", new ArrayList<Map<String, Object>>());
            entity.put("entityName", entityName);
            viewEntities.put(entityName, entity);
        } else {
            entity = UtilGenerics.checkMap(viewEntities.get(entityName));
        }
        entitiesStack.push(entity);
    }

    private void popEntity() {
        entitiesStack.pop();
    }

    private void newRecord() {
        // currentRecord
        if (!entitiesStack.empty()) {
            recordsStack.push(new HashMap<>());
        }
    }
    private void newRecord(Map<String, Object> context) {
        if (!entitiesStack.empty()) {
        	// currentRecord
            Map<String, Object> record = new HashMap<>();
            // build stPointerId
            List<String> pkList = UtilGenerics.checkList(this.entitiesStack.peek().get("primaryKeys"));
            int i = 0;
            String pkey = "";
            do {
                pkey += context.get(pkList.get(i));
                i++;
            } while (i < pkList.size());
            record.put("stId", pkey);
            recordsStack.push(record);

            List<Map<String, Object>> entitiesStackPeekList = UtilGenerics.checkList(entitiesStack.peek().get("list"));
            entitiesStackPeekList.add(record);
        }
    }

    private void storeRecord() {
        if (!recordsStack.empty()) {
            recordsStack.pop();
        }
    }

    private void putRecord(String fieldName, String value) {
        if (!recordsStack.empty()) {
            recordsStack.peek().put(fieldName, value);
            //recordsStack.peek().put(record.get("key"), record.get("value"));
        }
    }
    private void putRecord(List<Map<String, Object>> records) {
    	if (!recordsStack.empty()) {
    		for (Map<String, Object> record : records) {
                recordsStack.peek().put((String) record.get("key"), record.get("value"));
            }
        }
    }

    public Map<String, Object> output() {
        return this.output;
    }

//    public Map<String, String> getRecordPointer() {
//    	return this.storePointer;
//    }

    public Map<String, Object> getRecordPointer(Map<String, Object> context) {
        if (!this.entitiesStack.empty()) {
            Map<String, Object> data = new HashMap<>();
            String entityName = (String) this.entitiesStack.peek().get("entityName");
            data.put("entity", entityName);
            List<String> primaryKeys = UtilGenerics.checkList(this.entitiesStack.peek().get("primaryKeys"));
            data.put("id", context.get(primaryKeys.get(0)));
            return data;
        } else {
            return null;
        }
    }
}