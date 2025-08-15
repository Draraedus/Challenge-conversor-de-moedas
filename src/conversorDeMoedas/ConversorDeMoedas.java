package conversorDeMoedas;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class ConversorDeMoedas {
    private ArrayList<Moeda> moedas;
    final private Scanner scanner = new Scanner(System.in);
    final private String keyAPI = "dcf3012e09cad9ef3ad145a1";
    final private String urlAPI = String.format("https://v6.exchangerate-api.com/v6/%s/", keyAPI);
    final private Gson gson = new Gson();

    private String getJsonFromUrl(String endpoint) throws Exception {
        URI uri = new URI(endpoint);
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("HTTP error code: " + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            sb.append(output);
        }
        conn.disconnect();

        return sb.toString();
    }

    public void getDisponibleABV() throws Exception {
        String endpoint = urlAPI + "codes";
        String jsonResponse = getJsonFromUrl(endpoint);

        JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
        JsonArray codesArray = jsonObject.getAsJsonArray("supported_codes");

        ArrayList<Moeda> moedas = new ArrayList<>();
        for (int i = 0; i < codesArray.size(); i++) {
            JsonArray item = codesArray.get(i).getAsJsonArray();
            String code = item.get(0).getAsString();
            String name = item.get(1).getAsString();
            moedas.add(new Moeda(code, name));
        }
        this.moedas = moedas;
    }

    private double getConvertRatio(String moedaOrigem, String moedaDestino) throws Exception {
        String endpoint = urlAPI + "pair/" + moedaOrigem + "/" + moedaDestino;

        String jsonResponse = getJsonFromUrl(endpoint);

        JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
        return jsonObject.get("conversion_rate").getAsDouble();
    }


    private void printMoedasABV() {
        for (Moeda moeda : moedas) {
            System.out.printf(" [%s] %s%n", moeda.getAbreviacao(), moeda.getNome());
        }
    }

    private String viewAndSelectABV(){
        System.out.print("Escolha e digite a abreviação abaixo das moedas suportadas pela API");
        this.printMoedasABV();
        System.out.println("Digite X para Sair");
        System.out.print("\nDigite a abreviação da moeda base: ");

        return this.scanner.nextLine();
    }

    private double selectMoedaQuantity() {
        System.out.println("\nDigite a quantidade desejada que se deseja converter (EX.: 2.50):\n");
        String rawCommand = this.scanner.nextLine();

        double selectedQuantity;
        try {
            selectedQuantity = Double.parseDouble(rawCommand);
        } catch (NumberFormatException e) {
            return(-1);
        }

        return selectedQuantity;
    }

    private String viewAndSelectABVToConvert() {
        System.out.println("\nAgora, por último, escolha a abreviação que deseja converter o número selecionado para.\n");
        this.printMoedasABV();
        System.out.println("Digite -1 para Sair");
        System.out.println("Digite o indice escolhido:");

        return this.scanner.nextLine();
    }

    private void resultPrint(double result) {
        System.out.println("O valor convertido é:");
        System.out.printf("%.2f %n", result);
        System.out.println("Aperte enter para voltar ao menu de conversão.");
        scanner.nextLine();
    }

    public void menu() throws Exception {
        while (true) {
            String baseABV = this.viewAndSelectABV();
            if (baseABV.equals("X")) {break;}
            System.out.println("Moeda escolhida! Aperte enter para continuar.");
            scanner.nextLine();

            double moneyToConvert = this.selectMoedaQuantity();
            if (moneyToConvert == -1) {break;}
            System.out.println("Valor para conversão escolhido, aperte enter para continuar.");
            scanner.nextLine();

            String toConvertABV = this.viewAndSelectABVToConvert();
            if (toConvertABV.equals("X")){break;}
            System.out.println("Moeda escolhida! Aperte enter para continuar.");
            scanner.nextLine();

            double ratio = this.getConvertRatio(baseABV, toConvertABV);

            double result =  moneyToConvert * ratio;

            this.resultPrint(result);
        }
    }
}
