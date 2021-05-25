import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.query.Predicates;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;

public class CarServiceClient {
    final private static Random r = new Random(System.currentTimeMillis());
    public static void main(String[] args) throws UnknownHostException {
        HazelcastInstance instance = HazelcastClient.newHazelcastClient();
        Scanner in = new Scanner(System.in);
        int i;
        System.out.println("Serwis samochodowy. Proszę wybrać opcje");
        System.out.print("1. Pobierz dane\n2. Dodaj dane\n3. Usuń dane\n4. Aktualizuj dane\n5. Pobierz po kluczu\n6. Pobierz zapytaniem\n9. Wyjdź z programu");
        for(;;) {
            i = in.nextInt();
            switch(i) {
                case 1:
                    getServices(instance);
                    break;
                case 2:
                    putService(instance);
                    break;
                case 3:
                    removeService(instance);
                    break;
                case 4:
                    updateService(instance);
                    break;
                case 5:
                    getServiceByKey(instance);
                    break;
                case 6:
                    getServiceByQuery(instance);
                    break;
                case 9:
                    instance.shutdown();
                    System.exit(0);
            }
        }
    }

    private static void getServiceByQuery(HazelcastInstance instance) {
        IMap<Long, Service> services = instance.getMap( "services" );
        Predicate<?,?> costPredicate = Predicates.greaterThan("serviceCost",100);
        Collection<Service> costs = services.values(Predicates.and(costPredicate));
        for(Service s : costs) {
            System.out.println(s);
        }
    }

    private static void getServiceByKey(HazelcastInstance instance) {
        IMap<Long, Service> services = instance.getMap( "services" );
        System.out.println("Podaj klucz");
        Scanner inToUpdate = new Scanner(System.in);
        long key = (long) inToUpdate.nextInt();
        for(Map.Entry<Long, Service> e : services.entrySet()){
            if(e.getKey() == key) {
                System.out.println(e.getKey() + " => " + e.getValue());
            }
        }
    }

    private static void updateService(HazelcastInstance instance) {
        IMap<Long, Service> services = instance.getMap( "services" );
        System.out.println("Podaj klucz usugi jaką chcesz aktualizować");
        Scanner inToUpdate = new Scanner(System.in);
        long key = (long) inToUpdate.nextInt();
        System.out.println("Podaj nowe imie");
        String name = inToUpdate.next();
        System.out.println("Podaj nowy koszt");
        String cost = inToUpdate.next();
        System.out.println("Podaj nowy opis usługi");
        String des = inToUpdate.next();
        services.executeOnKey(key, new ServiceEntryProcessor(
                new Service(name,Integer.parseInt(cost),des)
        ));
        //services.executeOnEntries(new ServiceEntryProcessor());
    }

    public static void putService(HazelcastInstance instance) {
        IMap<Long, Service> services = instance.getMap( "services" );
        System.out.println("Podaj imie klienta");
        Scanner serviceIn = new Scanner(System.in);
        String name = serviceIn.next();
        System.out.println("Podaj rodzaj usługi");
        String des = serviceIn.next();
        System.out.println("Podaj koszt usługi");
        String cost = serviceIn.next();

        Long key1 = (long) Math.abs(r.nextInt());
        Service service = new Service(name, Integer.parseInt(cost), des);
        services.put(key1, service);
    }

    public static void getServices(HazelcastInstance instance) {
        IMap<Long, Service> services = instance.getMap( "services" );
        System.out.println("List of services: ");
        for(Map.Entry<Long, Service> e : services.entrySet()){
            System.out.println(e.getKey() + " => " + e.getValue());
        }
    }

    public static void removeService(HazelcastInstance instance) {
        IMap<Long, Service> services = instance.getMap( "services" );
        System.out.println("Podaj klucz usługi do usunięcia");
        Scanner remSerIn = new Scanner(System.in);
        long key = (long) remSerIn.nextInt();
        services.remove(key);
    }
}
