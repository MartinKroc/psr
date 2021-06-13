package pl.kielce.tu.neo4j.zad1;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.neo4j.driver.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.neo4j.driver.Values.NULL;
import static org.neo4j.driver.internal.types.InternalTypeSystem.TYPE_SYSTEM;

public class TransportCompany {
    public static Result createJourney(Transaction transaction, int id, String from, String to, String driverName, int passengersNumber) {
        String command = "CREATE (:Journey {id:$id, from:$from, to:$to, driverName:$driverName, passengersNumber:$passengersNumber})";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", id);
        parameters.put("from", from);
        parameters.put("to", to);
        parameters.put("driverName", driverName);
        parameters.put("passengersNumber", passengersNumber);
        return transaction.run(command, parameters);
    }

    public static Result createVehicle(Transaction transaction, String vinN) {
        String command = "CREATE (:Vehicle {vin:$vinNumber})";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("vinNumber", vinN);
        return transaction.run(command, parameters);
    }

    public static Result createRelationship(Transaction transaction, String driverN, String vinN) {
        String command =
                "MATCH (j:Journey),(v:Vehicle) " +
                        "WHERE j.driverName = $driverN AND v.vin = $vinN "
                        + "CREATE (j)-[r:JEST_KIEROWCA]->(v)" +
                        "RETURN type(r)";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("driverN", driverN);
        parameters.put("vinN", vinN);
        return transaction.run(command, parameters);
    }

    public static Result readAllNodes(Transaction transaction) {
        String command =
                "MATCH (n)" +
                        "RETURN n";
        Result result = transaction.run(command);
        while (result.hasNext()) {
            org.neo4j.driver.Record record = result.next();
            List<Pair<String, Value>> fields = record.fields();
            for (Pair<String, Value> field : fields)
                printField(field);
        }
        return result;
    }

    public static Result readAllRealtionships(Transaction transaction) {
        String command =
                "MATCH ()-[r]->()" +
                        "RETURN r;";
        System.out.println("Executing: " + command);
        Result result = transaction.run(command);
        while (result.hasNext()) {
            org.neo4j.driver.Record record = result.next();
            List<Pair<String, Value>> fields = record.fields();
            for (Pair<String, Value> field : fields)
                printField(field);
        }
        return result;
    }

    public static void printField(Pair<String, Value> field) {
        System.out.println("field = " + field);
        Value value = field.value();
        if (TYPE_SYSTEM.NODE().isTypeOf(value))
            printNode(field.value().asNode());
        else if (TYPE_SYSTEM.RELATIONSHIP().isTypeOf(value))
            printRelationship(field.value().asRelationship());
        else
            throw new RuntimeException();
    }

    public static void printNode(Node node) {
        System.out.println("id = " + node.id());
        System.out.println("labels = " + " : " + node.labels());
        System.out.println("asMap = " + node.asMap());
    }

    public static void printRelationship(Relationship relationship) {
        System.out.println("id = " + relationship.id());
        System.out.println("type = " + relationship.type());
        System.out.println("startNodeId = " + relationship.startNodeId());
        System.out.println("endNodeId = " + relationship.endNodeId());
        System.out.println("asMap = " + relationship.asMap());
    }

    public static Result deleteEverything(Transaction transaction) {
        String command = "MATCH (n) DETACH DELETE n";
        System.out.println("Executing: " + command);
        return transaction.run(command);
    }

    public static Result deleteJourney(Transaction transaction, int id) {
        String command = "MATCH (j:Journey {id:$idN}) OPTIONAL MATCH (j)-[r]-() DELETE r,j";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("idN", id);
        return transaction.run(command, parameters);
    }

    public static Result updateJourney(Transaction transaction, int idUp, int passengersNumberUp) {
        String command = "MATCH (j {id:$id}) SET j.passengersNumber=$passengersNumber RETURN j.name, j.passengersNumber";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", idUp);
        parameters.put("passengersNumber", passengersNumberUp);
        System.out.println("Executing: " + command);
        return transaction.run(command, parameters);
    }

    public static Result selectJourney(Transaction transaction, int idd) {
        String command = "MATCH (j {id:$id}) RETURN j";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", idd);
        Result result = transaction.run(command, parameters);
        while (result.hasNext()) {
            org.neo4j.driver.Record record = result.next();
            List<Pair<String, Value>> fields = record.fields();
            for (Pair<String, Value> field : fields)
                printField(field);
        }
        return result;
    }

    public static Result queryJourney(Transaction transaction, String dn) {
        String command = "MATCH (j:Journey) WHERE j.from=$from RETURN j";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("from", dn);
        Result result = transaction.run(command, parameters);
        while (result.hasNext()) {
            org.neo4j.driver.Record record = result.next();
            List<Pair<String, Value>> fields = record.fields();
            for (Pair<String, Value> field : fields)
                printField(field);
        }
        return result;
    }

    public static Result sumOfPassengers(Transaction transaction) {
        int sumOfPas = 0;
        Value temp;
        String command =
                "MATCH (j)" +
                        "RETURN j";
        Result result = transaction.run(command);
        while (result.hasNext()) {
            org.neo4j.driver.Record record = result.next();
            List<Pair<String, Value>> fields = record.fields();
            for (Pair<String, Value> field : fields) {
                temp = field.value();
                if(temp.get("passengersNumber")!=NULL) {
                    sumOfPas = sumOfPas + temp.get("passengersNumber").asInt();
                }
            }
        }
        System.out.println("Suma wszystkich pasażerów: " + sumOfPas);
        return result;
    }

    public static void main(String[] args) throws Exception {
        try (Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "neo4jpassword"));
             Session session = driver.session()) {

            session.writeTransaction(tx -> deleteEverything(tx));

            //init values

            session.writeTransaction(tx -> createJourney(tx, 1, "Kielce", "Sandomierz", "Michal", 56));
            session.writeTransaction(tx -> createJourney(tx, 2, "Miechow", "Radoszyce", "Michal", 22));
            session.writeTransaction(tx -> createJourney(tx, 3, "Kielce", "Michalow", "Krzysztof", 77));
            session.writeTransaction(tx -> createVehicle(tx, "543542343647547"));
            session.writeTransaction(tx -> createRelationship(tx, "Krzysztof", "543542343647547"));

            //end

            Scanner in = new Scanner(System.in);
            int i;
            int nextId = 4;
            System.out.println("Firma transportowa. Proszę wybrać opcje");
            for(;;) {
                System.out.print("1. Pobierz dane\n2. Dodaj dane\n3. Usuń dane\n4. Aktualizuj dane\n5. Pobierz po kluczu\n6. Pobierz zapytaniem\n7. Przetwarzanie\n9. Wyjdź z programu");
                i = in.nextInt();
                switch(i) {
                    case 1:
                        session.writeTransaction(tx -> readAllNodes(tx));
                        break;
                    case 2:
                        System.out.println("miejsce rozpoczęcia podróży: ");
                        String from = in.next();
                        System.out.println("miejsce zakończenia podróży: ");
                        String to = in.next();
                        System.out.println("imie kierowcy: ");
                        String driverName = in.next();
                        System.out.println("liczba pasazerow: ");
                        int passengersNumber = in.nextInt();
                        int finalNextId = nextId;
                        session.writeTransaction(tx -> createJourney(tx, finalNextId, from, to, driverName, passengersNumber));
                        nextId++;
                        break;
                    case 3:
                        System.out.println("podaj id podrozy do usuniecia: ");
                        int id = in.nextInt();
                        session.writeTransaction(tx -> deleteJourney(tx, id));
                        break;
                    case 4:
                        System.out.println("podaj id podrozy: ");
                        int idUp = in.nextInt();
                        System.out.println("nowa liczba pasażerów: ");
                        int passengersNumberUp = in.nextInt();
                        session.writeTransaction(tx -> updateJourney(tx, idUp, passengersNumberUp));
                        break;
                    case 5:
                        System.out.println("podaj id podróży: ");
                        int idSel = in.nextInt();
                        session.writeTransaction(tx -> selectJourney(tx, idSel));
                        break;
                    case 6:
                        System.out.println("podaj miasto z którego był odjazd: ");
                        String driverQ = in.next();
                        session.writeTransaction(tx -> queryJourney(tx, driverQ));
                        break;
                    case 7:
                        System.out.println("przetwarzanie. Suma pasażerów");
                        session.writeTransaction(tx -> sumOfPassengers(tx));
                        break;
                    case 9:
                        System.exit(0);
                }
            }

        }
    }
}
