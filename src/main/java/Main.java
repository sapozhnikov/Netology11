import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String[] products = {"Хлеб", "Яблоки", "Молоко", "Чипсы", "Шоколад"};
        int[] prices = {50, 150, 90, 190, 110};
        final String basketFileName = "basket.json";
        ClientLog log = new ClientLog();

        Basket basket;
        File basketFile = new File(basketFileName);
        if (basketFile.exists() && !basketFile.isDirectory()) {
            //restore from file
            basket = new Basket(products, prices);
            basket.loadFromJson(basketFile);
        } else {
            //an empty one
            basket = new Basket(products, prices);
        }

        System.out.println("Список возможных товаров для покупки");
        for (int i = 0; i < products.length; i++) {
            System.out.println((i + 1) + ". " + products[i] + " " +
                    prices[i] + " руб/шт");
        }


        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Выберите товар и количество или введите `end`");
            String input = scanner.nextLine();
            if (input.equals("end")) {
                break;
            }
            String[] userData = input.split(" ");
            if (userData.length != 2) {
                System.out.println("Неверное количество данных");
                continue;
            }
            int productNumber;
            int productCount;
            try {
                productNumber = Integer.parseInt(userData[0]) - 1;
                if ((productNumber < 0) || (productNumber + 1 > prices.length)) {
                    System.out.println("Неверный номер продукта");
                    continue;
                }
                productCount = Integer.parseInt(userData[1]);
                if (productCount < 0) {
                    System.out.println("Неверное количество продукта");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат числа");
                continue;
            }
            basket.addToCart(productNumber, productCount);
            log.log(productNumber, productCount);
            basket.saveJson();
        }

        basket.printCart();
        File logFile = new File("log.csv");
        log.exportAsCSV(logFile);
    }
}
