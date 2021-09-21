package bgu.spl.mics.application;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.io.*;

import bgu.spl.mics.application.passiveObjects.Pair;
import java.io.Serializable;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */

public class BookStoreRunner implements Serializable{
    public static void main(String[] args) {

        ArrayList<Thread> Threads=new ArrayList<Thread>();
        JsonParser parser = new JsonParser();
        try
        {
          //  Object obj = parser.parse(new FileReader("/users/studs/bsc/2019/romid/Downloads/bookstore/src/input1.json"));
           Object obj = parser.parse(new FileReader(args[0]));
            JsonObject jsonObj = (JsonObject) obj;
            // init inventory
            JsonArray initialInventoryArray = (JsonArray) jsonObj.get("initialInventory");
            Inventory inventory = Inventory.getInstance();
            Iterator<JsonElement> iteratorInventory = initialInventoryArray.iterator();
            BookInventoryInfo[] booksToInsert = new BookInventoryInfo[initialInventoryArray.size()];
            int i=0;
            while(iteratorInventory.hasNext())
            {
                JsonElement elemnt = iteratorInventory.next();
                JsonObject bookInfo = elemnt.getAsJsonObject();
                booksToInsert[i] = new BookInventoryInfo(bookInfo.get("bookTitle").getAsString(), bookInfo.get("amount").getAsInt(), bookInfo.get("price").getAsInt());
                i++;
            }
            inventory.load(booksToInsert);
            //initialResources
            JsonArray arr = (JsonArray) jsonObj.get("initialResources");
            ResourcesHolder resourcesHolder = ResourcesHolder.getInstance();
            Iterator<JsonElement> iter = arr.iterator();
            JsonArray initialResourcesArray = iter.next().getAsJsonObject().get("vehicles").getAsJsonArray();
            Iterator<JsonElement> iteratorresourcesHolder = initialResourcesArray.iterator();
            DeliveryVehicle[] vehiclesToInsert = new DeliveryVehicle[initialResourcesArray.size()];
            int j=0;
            while (iteratorresourcesHolder.hasNext())
            {
                JsonElement elemnt2 = iteratorresourcesHolder.next();
                JsonObject vehicle = elemnt2.getAsJsonObject();
                vehiclesToInsert[j] = new DeliveryVehicle(vehicle.get("license").getAsInt(), vehicle.get("speed").getAsInt());
                j++;
            }
            resourcesHolder.load(vehiclesToInsert);

            //initialServices
            JsonObject servicesObj = (JsonObject) jsonObj.get("services");
            int sellingNum = servicesObj.get("selling").getAsInt();
            for (int k=1; k<=sellingNum; k++){
                SellingService sellingService = new SellingService("selling" + k);
                Thread t = new Thread(sellingService);
                t.start();
                Threads.add(t);
            }
            int inventoryNum = servicesObj.get("inventoryService").getAsInt();
            for (int k=1; k<=inventoryNum; k++){
                InventoryService inventoryService = new InventoryService("inventoryService" + k);
                Thread t = new Thread(inventoryService);
                t.start();
                Threads.add(t);
            }
            int logisticsNum = servicesObj.get("logistics").getAsInt();
            for (int k=1; k<=logisticsNum; k++){
                LogisticsService logisticsService = new LogisticsService("logistics" + k);
                Thread t = new Thread(logisticsService);
                t.start();
                Threads.add(t);
            }
            int resourcesServiceNum = servicesObj.get("resourcesService").getAsInt();
            for (int k=1; k<=resourcesServiceNum; k++){
                ResourceService resourceService = new ResourceService("resourcesService" + k);
                Thread t = new Thread(resourceService);
                t.start();
                Threads.add(t);
            }
            // Customers
            HashMap<Integer,Customer> customerHashMap = new HashMap<>();
            JsonArray customersArray = (JsonArray)servicesObj.get("customers").getAsJsonArray();
            Iterator<JsonElement> customerIterator = customersArray.iterator();
            while (customerIterator.hasNext())
            {
                JsonObject customerObj = customerIterator.next().getAsJsonObject();
                int id = customerObj.get("id").getAsInt();
                String name = customerObj.get("name").getAsString();
                String address = customerObj.get("address").getAsString();
                int distance = customerObj.get("distance").getAsInt();
                int number = customerObj.get("creditCard").getAsJsonObject().get("number").getAsInt();
                int amount = customerObj.get("creditCard").getAsJsonObject().get("amount").getAsInt();
                Customer customer = new Customer(name, id, address, distance, number, amount);
                customerHashMap.put(id, customer);
                List<Pair> orderSchedule = new LinkedList<>();
                JsonArray OrderArray = customerObj.get("orderSchedule").getAsJsonArray();
                Iterator<JsonElement> OrederIterator = OrderArray.iterator();
                while (OrederIterator.hasNext())
                {
                    JsonObject ord = OrederIterator.next().getAsJsonObject();
                    Pair p = new Pair(ord.get("tick").getAsInt(),ord.get("bookTitle").getAsString());
                    orderSchedule.add(p);
                }
                APIService apiService = new APIService(customer, orderSchedule);
                Thread t = new Thread(apiService);
                t.start();
                Threads.add(t);


            }
            Thread.sleep(1000); //give time for the services to init
            JsonObject timeObj = servicesObj.getAsJsonObject("time");


            if(ThreadsCounter.getInstance().getThreadsInit().get()==Threads.size()) {
                TimeService timeService = new TimeService(timeObj.get("speed").getAsInt(), timeObj.get("duration").getAsInt());


                Thread t2 = new Thread(timeService);
                t2.start();
                Threads.add(t2);
            }
            for(Thread t:Threads){
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //start print the customer hashmap
            try {
             //   FileOutputStream fileOut = new FileOutputStream("customer");
                FileOutputStream fileOut = new FileOutputStream(args[1]);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(customerHashMap);
                out.close();
                fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //end print the customer hashmap
            // start print money register
            MoneyRegister moneyRegister = MoneyRegister.getInstance();
            try {
              //  FileOutputStream fileOut = new FileOutputStream("money");
                FileOutputStream fileOut = new FileOutputStream(args[4]);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(moneyRegister);
                out.close();
                fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // end print money register
            // start print inventory and recepites
          //  inventory.printInventoryToFile("inventori");
           inventory.printInventoryToFile(args[2]);
         //   moneyRegister.printOrderReceipts("reciptes");
            moneyRegister.printOrderReceipts(args[3]);
            // end print inventory and recepites

            // check..............................................................................................................
/*
            try
            {
                // Reading the object from a file
                FileInputStream file = new FileInputStream("customer");
                ObjectInputStream in = new ObjectInputStream(file);

                // Method for deserialization of object
                HashMap<Integer,Customer> customerHashMapCheck = new HashMap<>();
                customerHashMapCheck = (HashMap<Integer,Customer> )in.readObject();

                in.close();
                file.close();

                System.out.println("Object has been deserialized ");
                for(Map.Entry<Integer, Customer> entry : customerHashMapCheck.entrySet()) {
                    System.out.println("ID : " + entry.getKey());
                    System.out.println(entry.getValue().toString());

                }
            }

            catch(IOException ex)
            {
                System.out.println("IOException is caught");
            }

            catch(ClassNotFoundException ex)
            {
                System.out.println("ClassNotFoundException is caught");
            }


            try
            {
                // Reading the object from a file
                FileInputStream file = new FileInputStream("money");
                ObjectInputStream in = new ObjectInputStream(file);

                // Method for deserialization of object
                MoneyRegister mon = (MoneyRegister)in.readObject();
                System.out.println(mon.toString());
                in.close();
                file.close();

            }

            catch(IOException ex)
            {
                System.out.println("IOException is caught");
            }

            catch(ClassNotFoundException ex)
            {
                System.out.println("ClassNotFoundException is caught");
            }


            try
            {
                // Reading the object from a file
                FileInputStream file = new FileInputStream("inventori");
                ObjectInputStream in = new ObjectInputStream(file);

                // Method for deserialization of object
                HashMap<String,Integer> output = (HashMap<String,Integer>)in.readObject();
                for(Map.Entry<String,Integer> entry : output.entrySet()) {
                    System.out.println("Book Name : " + entry.getKey());
                    System.out.println("Book Amount" + entry.getValue());
                }
                in.close();
                file.close();
            }

            catch(IOException ex)
            {
                System.out.println("IOException is caught");
            }

            catch(ClassNotFoundException ex)
            {
                System.out.println("ClassNotFoundException is caught");
            }


            try
            {
                // Reading the object from a file
                FileInputStream file = new FileInputStream("reciptes");
                ObjectInputStream in = new ObjectInputStream(file);

                // Method for deserialization of object
                List<OrderReceipt> ret = (List<OrderReceipt>)in.readObject();

                in.close();
                file.close();

            }

            catch(IOException ex)
            {
                System.out.println("IOException is caught");
            }

            catch(ClassNotFoundException ex)
            {
                System.out.println("ClassNotFoundException is caught");
            }

            // check..............................................................................................................
           */
        }

        catch (FileNotFoundException e) {e.printStackTrace();}
        catch (IOException e) {}
        catch (Exception e) {e.printStackTrace();}

    }
}
