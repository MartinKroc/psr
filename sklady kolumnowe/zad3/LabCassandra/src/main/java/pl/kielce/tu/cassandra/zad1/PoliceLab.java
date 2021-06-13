package pl.kielce.tu.cassandra.zad1;

import com.datastax.oss.driver.api.core.CqlSession;

import java.util.Scanner;

public class PoliceLab {
    public static void main(String[] args) {
        try (CqlSession session = CqlSession.builder().build()) {
            KeySpaceMan keyspaceManager = new KeySpaceMan(session, "police");
            keyspaceManager.dropKeyspace();
            keyspaceManager.selectKeyspaces();
            keyspaceManager.createKeyspace();
            keyspaceManager.useKeyspace();

            CriminalsOperations criminalsOperations = new CriminalsOperations(session);
            criminalsOperations.createTable();
            int nexId = 1;
            Scanner in = new Scanner(System.in);
            int i;

            System.out.println("Komenda policji. Proszę wybrać opcje");
            for(;;) {
                System.out.print("1. Pobierz dane\n2. Dodaj dane\n3. Usuń dane\n4. Aktualizuj dane\n5. Pobierz po kluczu\n6. Pobierz zapytaniem\n7. Przetwarzanie\n9. Wyjdź z programu");
                i = in.nextInt();
                switch(i) {
                    case 1:
                        criminalsOperations.selectFromTable();
                        break;
                    case 2:
                        System.out.println("imie wieznia: ");
                        String name = in.next();
                        System.out.println("przestepstwo: ");
                        String crime = in.next();
                        System.out.println("wysokosc kary: ");
                        int penalty = in.nextInt();
                        criminalsOperations.insertIntoTable(nexId, name, crime, penalty);
                        nexId++;
                        break;
                    case 3:
                        System.out.println("podaj id wieznia: ");
                        int id = in.nextInt();
                        criminalsOperations.deleteFromTable(id);
                        break;
                    case 4:
                        System.out.println("podaj id wieznia: ");
                        int idUp = in.nextInt();
                        System.out.println("nowa wysokosc kary: ");
                        int penaltyUp = in.nextInt();
                        criminalsOperations.updateIntoTable(penaltyUp, idUp);
                        break;
                    case 5:
                        System.out.println("podaj id wieznia: ");
                        int idSel = in.nextInt();
                        criminalsOperations.selectFromTableById(idSel);
                        break;
                    case 6:
                        System.out.println("podaj przestepstwo: ");
                        String crimeQ = in.next();
                        criminalsOperations.selectFromTableByQuery(crimeQ);
                        break;
                    case 7:
                        System.out.println("przetwarzanie. Gdy wiezien ma przestepstwo kradziezy naliczana jest kara = 5: ");
                        criminalsOperations.processCrimes();
                        break;
                    case 9:
                        criminalsOperations.dropTable();
                        System.exit(0);
                }
            }

        }
    }
}
