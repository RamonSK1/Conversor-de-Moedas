import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class ConversorDeMoeda {
    private static final String apiChave = "26e530099d204b98c50a6ae1";
    private static final String link = "https://v6.exchangerate-api.com/v6/" + apiChave + "/latest/USD";

    public static JsonObject obterMoedas() throws IOException, InterruptedException {
        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest requisitar = HttpRequest.newBuilder()
                .uri(URI.create(link))
                .build();

        HttpResponse<String> response = cliente.send(requisitar,HttpResponse.BodyHandlers.ofString());

        Gson gaison = new Gson();
        JsonObject object = gaison.fromJson(response.body(), JsonObject.class);
        return  object.getAsJsonObject("conversion_rates");

    }

    public static double cambio(String moedaO, String moedaD, JsonObject taxaDeConversao) {
        double taxaO = taxaDeConversao.get(moedaO).getAsDouble();
        double taxaD = taxaDeConversao.get(moedaD).getAsDouble();

        return taxaD / taxaO;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        JsonObject taxasDeConversao;

        try {
            taxasDeConversao = obterMoedas();
        } catch (IOException | InterruptedException e){
            System.out.println("Ocorreu um erro: "+ e.getMessage());
            return;
        }

        Set<Map.Entry<String, JsonElement>> moedas = taxasDeConversao.entrySet();

        while (true){
            try {
                System.out.println("Conversao de Moedas");
                System.out.println("1. Converter moeda");
                System.out.println("2. Lista de moedas");
                System.out.println("3. Sair");
                int opcao = scanner.nextInt();

                if (opcao == 1) {
                    System.out.println("Digite o valor: ");
                    double valor = scanner.nextDouble();

                    System.out.println("Qual a Sua moeda? ");
                    String moedaOriginal = scanner.next().toUpperCase();

                    System.out.println("Para qual moeda converter? ");
                    String moedaDeConversao = scanner.next().toUpperCase();

                    if (!taxasDeConversao.has(moedaOriginal) || !taxasDeConversao.has(moedaDeConversao)) {
                        System.out.println("Nao tem essa moeda na lista, tente novamente");
                    }

                    double taxaDeC = cambio(moedaOriginal, moedaDeConversao, taxasDeConversao);
                    double quantiaFinal = valor * taxaDeC;
                    System.out.printf("%.2f %s = %.2f %s%n", valor, moedaOriginal, quantiaFinal, moedaDeConversao);


                } else if (opcao == 2) {
                    System.out.println("Lista de Moedas: ");
                    for (Map.Entry<String, com.google.gson.JsonElement> moeda : moedas) {
                        System.out.println(moeda.getKey() + " ");
                    }
                    System.out.println();

                } else if (opcao == 3) {
                    System.out.println("Fechando o conversor");
                    return;
                } else {
                    System.out.println("Opção invalida");
                }
            } catch (Exception e){
                System.out.println("Erro: " + e.getMessage());
                System.out.println("Aperte uma das 3 opções anteriores novamente");
                scanner.next();
            }

        }

    }
}