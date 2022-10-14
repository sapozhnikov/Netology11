import netscape.javascript.JSObject;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Arrays;
import java.util.Map;

public class Basket {
    private String[] products;
    private int[] prices;

    private int goodsCount;
    private int[] itemsInCart;
    private int sumProducts;

    public Basket(String[] products, int[] prices) {
        this.products = products;
        this.prices = prices;

        goodsCount = products.length;
        itemsInCart = new int[goodsCount];
    }

    public void addToCart(int productNum, int amount) {
        sumProducts += prices[productNum] * amount;
        itemsInCart[productNum] += amount;
    }

    public void printCart() {
        System.out.println("Ваша корзина:");
        for (int i = 0; i < goodsCount; i++) {
            if (itemsInCart[i] != 0) {
                System.out.println(products[i] + " " +
                        itemsInCart[i] + " шт " +
                        prices[i] + " руб/шт " +
                        itemsInCart[i] * prices[i] + " руб в сумме");
            }
        }
        System.out.println("Итого " + sumProducts + " руб");
    }

    public void saveTxt(File textFile) {
        try (PrintWriter writer = new PrintWriter(textFile)) {
            for (String product : products) {
                writer.print(product);
                writer.print(" ");
            }
            writer.println();

            for (int price : prices) {
                writer.print(price);
                writer.write(" ");
            }
            writer.println();

            for (int item : itemsInCart) {
                writer.print(item);
                writer.write(" ");
            }
            writer.println();
        } catch (IOException e) {
            System.out.println("Can't open file for writing");
        }
    }

    public void saveJson(){
        try (FileWriter file = new FileWriter("basket.json")){
            JSONObject obj = new JSONObject();
            for (int i = 0; i < prices.length; i++) {
                if (itemsInCart[i] != 0){
                    obj.put(i, itemsInCart[i]);
                }
            }
            file.write(obj.toString());
            file.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void SetCart(int[] cart) {
        this.itemsInCart = cart;
        sumProducts = 0;
        for (int i = 0; i < prices.length; i++) {
            sumProducts += prices[i] * itemsInCart[i];
        }
    }

    public static Basket loadFromTxtFile(File textFile) {
        String[] products = null;
        int[] prices = null;
        int[] itemsInCart = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
            String fileLine = reader.readLine();
            products = fileLine.split(" ");

            fileLine = reader.readLine();
            prices = Arrays.stream(fileLine.split(" ")).mapToInt(Integer::parseInt).toArray();

            fileLine = reader.readLine();
            itemsInCart = Arrays.stream(fileLine.split(" ")).mapToInt(Integer::parseInt).toArray();

            Basket basket = new Basket(products, prices);
            basket.SetCart(itemsInCart);

            return basket;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadFromJson(File textFile) {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(textFile));
            JSONObject jsonObject = (JSONObject) obj;
            for (Object product : jsonObject.keySet()){
                int productNum = Integer.parseInt(product.toString());
                int amount = Integer.parseInt(jsonObject.get(product).toString());
                addToCart(productNum, amount);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
