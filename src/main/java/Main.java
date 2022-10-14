import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String[] products = {"Хлеб", "Яблоки", "Молоко", "Чипсы", "Шоколад"};
        int[] prices = {50, 150, 90, 190, 110};
        //final String basketFileName = "basket.json";
        ClientLog log = new ClientLog();

        //read shop.xml
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(new File("shop.xml"));
            doc.getDocumentElement().normalize();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }

        boolean configLoadEnabled;
        String configLoadFileName;
        String configLoadFormat;
        boolean configSaveEnabled;
        String configSaveFileName;
        String configSaveFormat;
        boolean configLogEnabled;
        String configLogFileName;

        try {
            XPathExpression xp = XPathFactory.newInstance().newXPath().compile("//config/load/enabled");
            configLoadEnabled = Boolean.parseBoolean(xp.evaluate(doc));
            xp = XPathFactory.newInstance().newXPath().compile("//config/load/fileName");
            configLoadFileName = xp.evaluate(doc);
            xp = XPathFactory.newInstance().newXPath().compile("//config/load/format");
            configLoadFormat = xp.evaluate(doc);

            xp = XPathFactory.newInstance().newXPath().compile("//config/save/enabled");
            configSaveEnabled = Boolean.parseBoolean(xp.evaluate(doc));
            xp = XPathFactory.newInstance().newXPath().compile("//config/save/fileName");
            configSaveFileName = xp.evaluate(doc);
            xp = XPathFactory.newInstance().newXPath().compile("//config/save/format");
            configSaveFormat = xp.evaluate(doc);

            xp = XPathFactory.newInstance().newXPath().compile("//config/log/enabled");
            configLogEnabled = Boolean.parseBoolean(xp.evaluate(doc));
            xp = XPathFactory.newInstance().newXPath().compile("//config/log/fileName");
            configLogFileName = xp.evaluate(doc);

        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }

        Basket basket;
        File basketFile = new File(configLoadFileName);
        if (configLoadEnabled && basketFile.exists() && !basketFile.isDirectory()) {
            //restore from file
            if (configLoadFormat.equals("json")) {
                basket = new Basket(products, prices);
                basket.loadFromJson(basketFile);
            }
            else {
                basket = Basket.loadFromTxtFile(basketFile);
            }
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
            if (configSaveEnabled) {
                if (configSaveFormat.equals("json")) {
                    basket.saveJson(new File(configSaveFileName));
                } else if (configSaveFormat.equals("csv")) {
                    basket.saveTxt(new File(configSaveFileName));
                }
            }
        }

        basket.printCart();
        if (configLogEnabled) {
            File logFile = new File(configLogFileName);
            log.exportAsCSV(logFile);
        }
    }
}
