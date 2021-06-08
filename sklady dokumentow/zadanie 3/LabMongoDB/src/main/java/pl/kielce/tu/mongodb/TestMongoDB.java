package pl.kielce.tu.mongodb;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.elemMatch;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Updates.inc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class TestMongoDB {
	public static void main(String[] args) {

		String user = "student01";
		String password = "student01";
		String host = "localhost";
		int port = 27017;
		String database = "database01";

		String clientURI = "mongodb://" + user + ":" + password + "@" + host + ":" + port + "/" + database;
		MongoClientURI uri = new MongoClientURI(clientURI);

		MongoClient mongoClient = new MongoClient(uri);

		MongoDatabase db = mongoClient.getDatabase(database);

		//db.getCollection("animals").drop();
		db.getCollection("employees").drop();

		MongoCollection<Document> emp = db.getCollection("employees");
		MongoCollection<Document> c = db.getCollection("animals");
		if(emp.countDocuments() == 0) {
			List<Document> documents = new ArrayList<Document>();
			for (int i = 0; i < 5; i++)
				documents.add(new Document("_id", 10 + i).append("name", "Pracownik" + i).append("salary", 1800).append("animals", i+10));
			emp.insertMany(documents);
		}

		Scanner in = new Scanner(System.in);
		int i;
		System.out.println("Skład dokumentów ZOO. Proszę wybrać opcje");
		for (; ; ) {
			System.out.print("1. Pobierz zwierzęta\n2. Dodaj zwierze\n3. Usuń zwierze\n4. Aktualizuj dane o zwierzęciu\n5. Pobierz zwierze po kluczu\n6. Pobierz zwierzęta zapytaniem\n7. Przetwarzanie na pracownikach\n9. Wyjdź z programu");
			i = in.nextInt();
			switch (i) {
				case 1:
					getAnimals(db);
					break;
				case 2:
					putAnimal(db);
					break;
				case 3:
					removeAnimal(db);
					break;
				case 4:
					updateAnimal(db);
					break;
				case 5:
					getAnimalByKey(db);
					break;
				case 6:
					getAnimalByQuery(db);
					break;
				case 7:
					processData(db);
					break;
				case 9:
					mongoClient.close();
					System.exit(0);
			}
		}

	}

	private static void processData(MongoDatabase db) {
		MongoCollection<Document> emp = db.getCollection("employees");

		for (Document doc : emp.find()) {
			int temp = (int) doc.get("animals");
			if(temp >= 12) {
				emp.updateOne(eq("_id", doc.get("_id")), new Document("$set", new Document("salary", 2000)));
			}
		}
		for (Document doc : emp.find())
			System.out.println(doc.toJson());
	}

	private static void getAnimalByQuery(MongoDatabase db) {
		MongoCollection<Document> collection = db.getCollection("animals");

		System.out.println("Wyszukaj zwierzęta po gatunku");
		Scanner serviceIn = new Scanner(System.in);
		String fil = serviceIn.next();

		for (Document d : collection.find(or(
				eq("type", fil))))
		System.out.println(d.toJson());
	}

	private static void getAnimalByKey(MongoDatabase db) {
		MongoCollection<Document> collection = db.getCollection("animals");

		System.out.println("Podaj ID zwierzęcia");
		Scanner serviceIn = new Scanner(System.in);
		long itemToShow = serviceIn.nextInt();

		Document myDoc = collection.find(eq("_id", itemToShow)).first();
		System.out.println(myDoc.toJson());

	}

	private static void updateAnimal(MongoDatabase db) {
		MongoCollection<Document> collection = db.getCollection("animals");

		System.out.println("Podaj ID zwierzęcia");
		Scanner serviceIn = new Scanner(System.in);
		long itemToUpdate = serviceIn.nextInt();
		System.out.println("Podaj nową wage");
		String weightUpdate = serviceIn.next();

		collection.updateOne(eq("_id", itemToUpdate), new Document("$set", new Document("weight", weightUpdate)));
		getAnimals(db);
	}

	private static void removeAnimal(MongoDatabase db) {
		MongoCollection<Document> collection = db.getCollection("animals");
		System.out.println("Podaj ID zwierzęcia");
		Scanner serviceIn = new Scanner(System.in);
		long itemToDel = serviceIn.nextInt();
		collection.deleteOne(eq("_id", itemToDel));
		getAnimals(db);
	}

	private static void putAnimal(MongoDatabase db) {
		MongoCollection<Document> collection = db.getCollection("animals");
		//long lastId = collection.countDocuments();
		long lastId;
		if(collection.countDocuments() == 0) {
			lastId = 1;
		}
		else {
			FindIterable<Document> last= collection.find().sort(Sorts.descending("_id")).limit(1);
			lastId = (long) last.iterator().next().get("_id");
		}
		System.out.println("Podaj nazwe zwierzecia");
		Scanner serviceIn = new Scanner(System.in);
		String name = serviceIn.next();
		System.out.println("Podaj gatunek");
		String type = serviceIn.next();
		System.out.println("Podaj wage zwierzecia");
		String w = serviceIn.next();

		Document animal = new Document("_id", lastId + 1)
				.append("name", name)
				.append("type", type)
				.append("weight", w);
		collection.insertOne(animal);
		getAnimals(db);
	}

	private static void getAnimals(MongoDatabase db) {
		MongoCollection<Document> collection = db.getCollection("animals");
		System.out.println("Wszystkie zwierzęta");
		for (Document doc : collection.find())
			System.out.println(doc.toJson());
	}
}
