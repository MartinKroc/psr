import java.util.*;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.*;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.*;

public class CarService {
    public static void main(String[] args) throws Exception {

        BasicAWSCredentials credentials = new BasicAWSCredentials("", "");
        AmazonDynamoDBClient instance = new AmazonDynamoDBClient(credentials).withRegion(Regions.US_EAST_1);
        DynamoDB dynamoDB = new DynamoDB(instance);

        Scanner in = new Scanner(System.in);
        int i;
        System.out.println("Serwis samochodowy. Proszę wybrać opcje");
        System.out.print("1. Pobierz dane\n2. Dodaj dane\n3. Usuń dane\n4. Aktualizuj dane\n5. Pobierz po kluczu\n6. Pobierz zapytaniem\n8.Nalicz rabaty (przetwarzanie)\n9. Wyjdź z programu");
        for (; ; ) {
            i = in.nextInt();
            switch (i) {
                case 1:
                    getServices(instance, dynamoDB);
                    break;
                case 2:
                    putService(instance, dynamoDB);
                    break;
                case 3:
                    removeService(instance, dynamoDB);
                    break;
                case 4:
                    updateService(instance, dynamoDB);
                    break;
                case 5:
                    getServiceByKey(instance, dynamoDB);
                    break;
                case 6:
                    getServiceByQuery(instance, dynamoDB);
                    break;
                case 7:
                    createTable(instance, dynamoDB);
                    break;
                case 8:
                    processTable(instance, dynamoDB);
                    break;
                case 9:
                    System.exit(0);
            }
        }
    }


    public static void getServices(AmazonDynamoDB instance, DynamoDB dynamoDB) {

        Table table = dynamoDB.getTable("Services2");

        ScanSpec scanSpec = new ScanSpec().withProjectionExpression("clientName, serviceCost, info.description, info.discount");

        try {
            ItemCollection<ScanOutcome> items = table.scan(scanSpec);

            Iterator<Item> iter = items.iterator();
            while (iter.hasNext()) {
                Item item = iter.next();
                System.out.println(item.toString());
            }

        }
        catch (Exception e) {
            System.err.println("Unable to scan the table:");
            System.err.println(e.getMessage());
        }

    }

    public static void putService(AmazonDynamoDBClient instance, DynamoDB dynamoDB) {

        Table table = dynamoDB.getTable("Services2");
        System.out.println("Podaj imie klienta");
        Scanner serviceIn = new Scanner(System.in);
        String clientName = serviceIn.next();
        System.out.println("Podaj rodzaj usługi");
        String description = serviceIn.next();
        System.out.println("Podaj koszt usługi");
        int serviceCost = serviceIn.nextInt();

        final Map<String, Object> infoMap = new HashMap<String, Object>();
        infoMap.put("description", description);

        try {
            System.out.println("Adding a new item...");
            PutItemOutcome outcome = table
                    .putItem(new Item().withPrimaryKey("serviceCost", serviceCost, "clientName", clientName).withMap("info", infoMap));

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());

        }
        catch (Exception e) {
            System.err.println("Unable to add item: " + serviceCost + " " + clientName);
            System.err.println(e.getMessage());
        }
    }

    public static void removeService(AmazonDynamoDB instance, DynamoDB dynamoDB) {

        Table table = dynamoDB.getTable("Services2");

        Scanner inToGet = new Scanner(System.in);

        System.out.println("Podaj imie klienta");
        String clientName = inToGet.next();
        System.out.println("Podaj koszt usługi");
        int serviceCost = inToGet.nextInt();

        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey(new PrimaryKey("serviceCost", serviceCost, "clientName", clientName));

        // Conditional delete (we expect this to fail)

        try {
            System.out.println("Attempting a conditional delete...");
            table.deleteItem(deleteItemSpec);
            System.out.println("DeleteItem succeeded");
        }
        catch (Exception e) {
            System.err.println("Unable to delete item: " + serviceCost + " " + clientName);
            System.err.println(e.getMessage());
        }

    }

    public static void updateService(AmazonDynamoDB instance, DynamoDB dynamoDB) {

        Table table = dynamoDB.getTable("Services2");

        Scanner inToGet = new Scanner(System.in);

        System.out.println("Podaj imie klienta");
        String clientName = inToGet.next();
        System.out.println("Podaj koszt usługi");
        int serviceCost = inToGet.nextInt();
        System.out.println("Podaj nowy opis");
        String des = inToGet.next();

        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("serviceCost", serviceCost, "clientName", clientName)
                .withUpdateExpression("set info.description = :r")
                .withValueMap(new ValueMap().withString(":r", des))
                .withReturnValues(ReturnValue.UPDATED_NEW);

        try {
            System.out.println("Updating the item...");
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
            System.out.println("UpdateItem succeeded:\n" + outcome.getItem().toJSONPretty());

        }
        catch (Exception e) {
            System.err.println("Unable to update item: " + serviceCost + " " + clientName);
            System.err.println(e.getMessage());
        }
    }

    public static void getServiceByKey(AmazonDynamoDB instance, DynamoDB dynamoDB) {

        Table table = dynamoDB.getTable("Services2");

        Scanner inToGet = new Scanner(System.in);

        System.out.println("Podaj imie klienta");
        String clientName = inToGet.next();
        System.out.println("Podaj koszt usługi");
        int serviceCost = inToGet.nextInt();

        GetItemSpec spec = new GetItemSpec().withPrimaryKey("serviceCost", serviceCost, "clientName", clientName);

        try {
            System.out.println("Attempting to read the item...");
            Item outcome = table.getItem(spec);
            System.out.println("GetItem succeeded: " + outcome);

        }
        catch (Exception e) {
            System.err.println("Unable to read item: " + serviceCost + " " + clientName);
            System.err.println(e.getMessage());
        }
    }

    public static void getServiceByQuery(AmazonDynamoDBClient instance, DynamoDB dynamoDB) {
        Table table = dynamoDB.getTable("Services2");

        HashMap<String, String> nameMap = new HashMap<String, String>();
        nameMap.put("#sc", "serviceCost");

        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":yyy", 100);

        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("#sc = :yyy").withNameMap(nameMap)
                .withValueMap(valueMap);

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;

        try {
            System.out.println("Zlecenia napraw z ceną równą 100 zł");
            items = table.query(querySpec);

            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                System.out.println(item.getNumber("serviceCost") + "zł, klient: " + item.getString("clientName"));
            }

        }
        catch (Exception e) {
            System.err.println("Unable to query");
            System.err.println(e.getMessage());
        }
    }

    public static void processTable(AmazonDynamoDBClient instance, DynamoDB dynamoDB) {
        Table table = dynamoDB.getTable("Services2");

        HashMap<String, String> nameMap = new HashMap<String, String>();
        nameMap.put("#sc", "serviceCost");

        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":yyy", 100);

        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("#sc = :yyy").withNameMap(nameMap)
                .withValueMap(valueMap);

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;

        try {
            System.out.println("Osoby obdarzone rabatem na następne zamówienie");
            items = table.query(querySpec);

            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();

                UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("serviceCost", item.getNumber("serviceCost"), "clientName", item.getString("clientName"))
                        .withUpdateExpression("set info.discount = :r")
                        .withValueMap(new ValueMap().withNumber(":r", 10))
                        .withReturnValues(ReturnValue.UPDATED_NEW);

                try {
                    System.out.println("Updating the item...");
                    UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
                    System.out.println("UpdateItem succeeded:\n" + outcome.getItem().toJSONPretty());

                }
                catch (Exception e) {
                    System.err.println("Unable to update item: " + item.getNumber("serviceCost") + " " + item.getString("clientName"));
                    System.err.println(e.getMessage());
                }

                System.out.println(item.getNumber("serviceCost") + "zł, klient: " + item.getString("clientName"));
            }

        }
        catch (Exception e) {
            System.err.println("Unable to query");
            System.err.println(e.getMessage());
        }
    }
    public static void createTable(AmazonDynamoDBClient instance,DynamoDB dynamoDB) {
        String tableName = "Services2";
        try {
            System.out.println("Attempting to create table; please wait...");
            Table table = dynamoDB.createTable(tableName,
                    Arrays.asList(new KeySchemaElement("serviceCost", KeyType.HASH), // Partition
                            // key
                            new KeySchemaElement("clientName", KeyType.RANGE)), // Sort key
                    Arrays.asList(new AttributeDefinition("serviceCost", ScalarAttributeType.N),
                            new AttributeDefinition("clientName", ScalarAttributeType.S)),
                    new ProvisionedThroughput(10L, 10L));
            table.waitForActive();
            System.out.println("Success.  Table status: " + table.getDescription().getTableStatus());
        }
        catch (Exception e) {
            System.err.println("Unable to create table: ");
            System.err.println(e.getMessage());
        }
    }
}
