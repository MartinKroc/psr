package zad4;

import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import io.dgraph.DgraphClient;
import io.dgraph.DgraphGrpc;
import io.dgraph.DgraphGrpc.DgraphStub;
import io.dgraph.DgraphProto;
import io.dgraph.DgraphProto.Mutation;
import io.dgraph.DgraphProto.Operation;
import io.dgraph.DgraphProto.Response;
import io.dgraph.Transaction;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class PoliceDep {
    private static final String TEST_HOSTNAME = "localhost";
    private static final int TEST_PORT = 9080;

    private static DgraphClient createDgraphClient(boolean withAuthHeader) {
        ManagedChannel channel =
                ManagedChannelBuilder.forAddress(TEST_HOSTNAME, TEST_PORT).usePlaintext().build();
        DgraphStub stub = DgraphGrpc.newStub(channel);

        if (withAuthHeader) {
            Metadata metadata = new Metadata();
            metadata.put(
                    Metadata.Key.of("auth-token", Metadata.ASCII_STRING_MARSHALLER), "the-auth-token-value");
            stub = MetadataUtils.attachHeaders(stub, metadata);
        }

        return new DgraphClient(stub);
    }

    public static void getJourneys(DgraphClient dgraphClient, String i) {
        Gson gson = new Gson(); // For JSON encode/decode
        String query =
                "query all($a: string){\n"
                        + "  all(func: eq(id, $a)) {\n"
                        + "    "
                        + "id\n"
                        + "uid\n"
                        + "name\n"
                        + "from\n"
                        + "to\n"
                        + "driver\n"
                        + "pasnum\n"
                        + "clickCount\n"
                        + "  }\n"
                        + "}\n";
        Map<String, String> vars = Collections.singletonMap("$a", i);
        Response res = dgraphClient.newTransaction().queryWithVars(query, vars);

        // Deserialize
        Journeys ppl = gson.fromJson(res.getJson().toStringUtf8(), Journeys.class);

        // Print results
        System.out.printf("journeys found: %d\n", ppl.all.size());
        ppl.all.forEach(j -> System.out.println(j.toString()));
        System.out.printf("Response: %s", res.getJson().toStringUtf8());
    }

    public static void getJourneysByDriver(DgraphClient dgraphClient, String dn) {
        Gson gson = new Gson(); // For JSON encode/decode
        String query =
                "query all($a: string){\n"
                        + "  all(func: eq(driver, $a)) {\n"
                        + "    "
                        + "id\n"
                        + "uid\n"
                        + "name\n"
                        + "from\n"
                        + "to\n"
                        + "driver\n"
                        + "pasnum\n"
                        + "clickCount\n"
                        + "  }\n"
                        + "}\n";
        Map<String, String> vars = Collections.singletonMap("$a", dn);
        Response res = dgraphClient.newTransaction().queryWithVars(query, vars);

        // Deserialize
        Journeys ppl = gson.fromJson(res.getJson().toStringUtf8(), Journeys.class);

        // Print results
        System.out.printf("journeys found: %d\n", ppl.all.size());
        ppl.all.forEach(j -> {
            System.out.println(j.toString());
        });
        System.out.printf("Response: %s", res.getJson().toStringUtf8());
    }

    public static void getAllJourneys(DgraphClient dgraphClient) {
        Gson gson = new Gson(); // For JSON encode/decode
        String query =
                "query all($a: string){\n"
                        + "  all(func: has(driver)) {\n"
                        + "    "
                        + "id\n"
                        + "uid\n"
                        + "name\n"
                        + "from\n"
                        + "to\n"
                        + "driver\n"
                        + "pasnum\n"
                        + "clickCount\n"
                        + "  }\n"
                        + "}\n";
        //Map<String, String> vars = Collections.singletonMap("$a", dn);
        Response res = dgraphClient.newTransaction().query(query);

        // Deserialize
        Journeys ppl = gson.fromJson(res.getJson().toStringUtf8(), Journeys.class);

        // Print results
        System.out.printf("journeys found: %d\n", ppl.all.size());
        ppl.all.forEach(j -> {
            System.out.println(j.toString());
        });
        System.out.printf("Response: %s", res.getJson().toStringUtf8());
    }

    public static void createJourney(int idn, DgraphClient dgraphClient, String from, String to, String dn, int num) {
        Gson gson = new Gson(); // For JSON encode/decode
        Transaction txn = dgraphClient.newTransaction();
        try {
            // Create data
            Journey p = new Journey();
            p.id = String.valueOf(idn);
            p.from = from;
            p.to = to;
            p.driver = dn;
            p.pasnum = num;

            // Serialize it
            String json = gson.toJson(p);

            // Run mutation
            Mutation mutation =
                    Mutation.newBuilder().setSetJson(ByteString.copyFromUtf8(json.toString())).build();
            txn.mutate(mutation);
            txn.commit();

        } finally {
            txn.discard();
        }
    }

    public static void deleteJourney(DgraphClient dgraphClient, String uuid, String id) {
        Gson gson = new Gson(); // For JSON encode/decode
        Transaction txn = dgraphClient.newTransaction();
        try {
            // Create data
            DelJourney p = new DelJourney();
            p.uid = id;
            // Serialize it
            //String json = gson.toJson(p);
            //System.out.println(json);
            String json =
                    "" +
                            "  <"+ uuid+ ">" + " <id> " + "\"" + id + "\" ." +
                            "";
            // Run mutation
            DgraphProto.Mutation mutation = DgraphProto.Mutation.newBuilder()
                    .setDelNquads(ByteString.copyFromUtf8(json)).build();
            System.out.println(json);
/*            Mutation mutation =
                    Mutation.newBuilder().setDeleteJson(ByteString.copyFromUtf8(json.toString())).build();*/
            txn.mutate(mutation);
            txn.commit();

        } finally {
            txn.discard();
        }
    }

    public static void updateJourney(DgraphClient dgraphClient, String uuid, String newname) {
        Gson gson = new Gson(); // For JSON encode/decode
        Transaction txn = dgraphClient.newTransaction();
        try {
            String json =
                    "" +
                            "  <"+ uuid+ ">" + " <driver> " + "\"" + newname + "\" ." +
                            "";
            // Run mutation
            DgraphProto.Mutation mutation = DgraphProto.Mutation.newBuilder()
                    .setSetNquads(ByteString.copyFromUtf8(json)).build();
            System.out.println(json);
            txn.mutate(mutation);
            txn.commit();

        } finally {
            txn.discard();
        }
    }

    public static void processing(DgraphClient dgraphClient) {
        Gson gson = new Gson(); // For JSON encode/decode
        String query =
                "query all($a: string){\n"
                        + "  all(func: eq(driver, $a)) {\n"
                        + "    "
                        + "id\n"
                        + "uid\n"
                        + "name\n"
                        + "from\n"
                        + "to\n"
                        + "driver\n"
                        + "pasnum\n"
                        + "clickCount\n"
                        + "  }\n"
                        + "}\n";
        Map<String, String> vars = Collections.singletonMap("$a", "Michal");
        Response res = dgraphClient.newTransaction().queryWithVars(query, vars);

        // Deserialize
        Journeys ppl = gson.fromJson(res.getJson().toStringUtf8(), Journeys.class);

        // Print results
        System.out.printf("journeys found: %d\n", ppl.all.size());
        ppl.all.forEach(j -> {
            System.out.println(j.toString());
            System.out.println("processing...");
            updateJourney(dgraphClient,j.uid,"Krzysztof");
        });
    }

    public static void main(final String[] args) {
        org.apache.log4j.BasicConfigurator.configure();
        DgraphClient dgraphClient = createDgraphClient(false);

        // Initialize
        dgraphClient.alter(Operation.newBuilder().setDropAll(true).build());

        // Set schema
        String schema = "id: string @index(exact) .";
        Operation operation = Operation.newBuilder().setSchema(schema).build();
        dgraphClient.alter(operation);
        String schema2 = "driver: string @index(exact) .";
        Operation operation2 = Operation.newBuilder().setSchema(schema2).build();
        dgraphClient.alter(operation2);
        String schema3 = "uuid: [uid] .";
        Operation operation3 = Operation.newBuilder().setSchema(schema3).build();
        dgraphClient.alter(operation3);

        //init values
        createJourney(1, dgraphClient, "Kielce","Sandomierz","Michal",44);
        createJourney(2, dgraphClient, "Krakow","Kielce","Karol",444);
        createJourney(3, dgraphClient, "Sun","Gdansk","Michal",2);

        Scanner in = new Scanner(System.in);
        int i;
        int nextId = 4;
        System.out.println("Firma transportowa. Proszę wybrać opcje");
        for(;;) {
            System.out.print("\n1. Pobierz dane\n2. Dodaj dane\n3. Usuń dane\n4. Aktualizuj dane\n5. Pobierz po kluczu\n6. Pobierz zapytaniem\n7. Przetwarzanie\n9. Wyjdź z programu");
            i = in.nextInt();
            switch(i) {
                case 1:
                    getAllJourneys(dgraphClient);
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
                    int nextid = nextId;
                    createJourney(nextid, dgraphClient, from,to,driverName,passengersNumber);
                    nextId++;
                    break;
                case 3:
                    System.out.println("podaj uid podrozy do usuniecia: ");
                    String id = in.next();
                    System.out.println("podaj id podrozy do usuniecia: ");
                    String idf = in.next();
                    deleteJourney(dgraphClient, id, idf);
                    break;
                case 4:
                    System.out.println("podaj uid podrozy: ");
                    String idUp = in.next();
                    System.out.println("nowimie kierowcy: ");
                    String drivernewname = in.next();
                    updateJourney(dgraphClient, idUp, drivernewname);
                    break;
                case 5:
                    System.out.println("podaj id podróży: ");
                    String idSel = in.next();
                    getJourneys(dgraphClient, idSel);
                    break;
                case 6:
                    System.out.println("podaj kierowce: ");
                    String driverQ = in.next();
                    getJourneysByDriver(dgraphClient, driverQ);
                    break;
                case 7:
                    System.out.println("przetwarzanie. Suma pasażerów");
                    processing(dgraphClient);
                    break;
                case 9:
                    System.exit(0);
            }
        }
    }

    static class Journey {
        String uuid;
        String id;
        String from;
        String to;
        String driver;
        String uid;
        int pasnum;

        Journey() {}

        @Override
        public String toString() {
            return "id: " + this.id + " z: " + this.from + " do: " + this.to + " kierowca: " + driver + " pasazerow: " + pasnum + " uid: " + uid;
        }
    }

    static class DelJourney {
        String uid;

        DelJourney() {}

    }

    static class Journeys {
        List<Journey> all;

        Journeys() {}
    }
}
