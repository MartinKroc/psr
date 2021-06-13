package Police;

import javax.persistence.*;
import java.util.*;

public class Department {

    public static void selectFromTable(EntityManager em) {
        TypedQuery<Crime> query =
                em.createQuery("SELECT c FROM Crime c", Crime.class);
        List<Crime> results = query.getResultList();
        for (Crime p : results) {
            System.out.println(p);
        }
    }

    public static void insertIntoTable(EntityManager em, String name, String crime, int penalty) {
        em.getTransaction().begin();
        Crime c = new Crime(name, crime, penalty);
        em.persist(c);
        em.getTransaction().commit();
    }

    public static void deleteFromTable(EntityManager em, int id) {
        TypedQuery<Crime> query =
                em.createQuery("SELECT c FROM Crime c", Crime.class);
        List<Crime> results = query.getResultList();
        em.getTransaction().begin();
        for (Crime p : results) {
            if(p.getId() == id) {
                em.remove(p);
            }
        }
        em.getTransaction().commit();
    }

    public static void updateIntoTable(EntityManager em, int newVal, int id) {
        TypedQuery<Crime> query =
                em.createQuery("SELECT c FROM Crime c", Crime.class);
        List<Crime> results = query.getResultList();
        em.getTransaction().begin();
        for (Crime p : results) {
            if(p.getId() == id) {
                p.setPenalty(newVal);
            }
        }
        em.getTransaction().commit();
    }

    public static void selectFromTableByQuery(EntityManager em) {
        TypedQuery<Crime> query =
                em.createQuery("SELECT c FROM Crime c WHERE c.penalty>25", Crime.class);
        List<Crime> results = query.getResultList();
        for (Crime p : results) {
            System.out.println(p);
        }
    }

    public static void selectFromTableById(EntityManager em, int id) {
        TypedQuery<Crime> query =
                em.createQuery("SELECT c FROM Crime c WHERE c.id=" + id, Crime.class);
        List<Crime> results = query.getResultList();
        for (Crime p : results) {
            System.out.println(p);
        }
    }

    public static void processCrimes(EntityManager em) {
        Query q2 = em.createQuery("SELECT AVG(c.penalty) FROM Crime c");
        System.out.println("Srednia wysokosc kary: " + q2.getSingleResult());
    }

    public static void main(String[] args) {
        // Open a database connection
        // (create a new database if it doesn't exist yet):
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("$objectdb/db/crimes.odb");
        EntityManager em = emf.createEntityManager();

        // init crimes
        em.getTransaction().begin();
        Crime p1 = new Crime("Michal", "Kradziez", 45);
        em.persist(p1);
        Crime p2 = new Crime("Iwona", "Napad", 34);
        em.persist(p2);
        Crime p3 = new Crime("Karol", "Grozby karalne", 2);
        em.persist(p3);
        em.getTransaction().commit();

        //ui
        Scanner in = new Scanner(System.in);
        int i;

        System.out.println("Komenda policji. Proszę wybrać opcje");
        for(;;) {
            System.out.print("1. Pobierz dane\n2. Dodaj dane\n3. Usuń dane\n4. Aktualizuj dane\n5. Pobierz po kluczu\n6. Pobierz zapytaniem\n7. Przetwarzanie\n9. Wyjdź z programu");
            i = in.nextInt();
            switch(i) {
                case 1:
                    selectFromTable(em);
                    break;
                case 2:
                    System.out.println("imie wieznia: ");
                    String name = in.next();
                    System.out.println("przestepstwo: ");
                    String crime = in.next();
                    System.out.println("wysokosc kary: ");
                    int penalty = in.nextInt();
                    insertIntoTable(em, name, crime, penalty);
                    break;
                case 3:
                    System.out.println("podaj id wieznia: ");
                    int id = in.nextInt();
                    deleteFromTable(em, id);
                    break;
                case 4:
                    System.out.println("podaj id wieznia: ");
                    int idUp = in.nextInt();
                    System.out.println("nowa wysokosc kary: ");
                    int penaltyUp = in.nextInt();
                    updateIntoTable(em, penaltyUp, idUp);
                    break;
                case 5:
                    System.out.println("podaj id wieznia: ");
                    int idSel = in.nextInt();
                    selectFromTableById(em, idSel);
                    break;
                case 6:
                    selectFromTableByQuery(em);
                    break;
                case 7:
                    System.out.println("przetwarzanie. Srednia wysokosc kary ");
                    processCrimes(em);
                    break;
                case 9:
                    //delete all
                    TypedQuery<Crime> query =
                            em.createQuery("SELECT c FROM Crime c", Crime.class);
                    List<Crime> results = query.getResultList();

                    em.getTransaction().begin();
                    for (Crime c : results) {
                        em.remove(c); // delete entity
                    }
                    em.getTransaction().commit();

                    //end connection
                    em.close();
                    emf.close();
                    System.exit(0);
            }
        }
    }
}
