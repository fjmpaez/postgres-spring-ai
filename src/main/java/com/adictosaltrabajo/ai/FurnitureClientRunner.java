package com.adictosaltrabajo.ai;

import com.adictosaltrabajo.ai.adapter.llm.FurnitureCatalog;
import com.adictosaltrabajo.ai.model.Furniture;
import com.adictosaltrabajo.ai.model.FurnitureRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Scanner;

@Component
public class FurnitureClientRunner implements ApplicationRunner {

    private final ChatModel chatModel;
    private final FurnitureRepository furnitureRepository;

    public FurnitureClientRunner(ChatModel chatModel, FurnitureRepository furnitureRepository) {
        this.chatModel = chatModel;
        this.furnitureRepository = furnitureRepository;
    }


    String prompt = """
            Generate a list of 10 random furniture for an e-commerce website.
            """;

    @Override
    public void run(ApplicationArguments args) {
        if (furnitureRepository.count() <= 0) {
            for (int i = 0; i < 10; i++) {
                loadDataFromLLMIfNoData();
            }
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("*************************************************");
            System.out.print("What kind of furniture you like (or exit)?: ");
            String furnitureDescription = scanner.nextLine();
            System.out.println();
            if ("exit".equalsIgnoreCase(furnitureDescription)) {
                break;
            }


            System.out.print("Top price (leave empty if no top)?: ");
            String requestedTopPrice = scanner.nextLine();

            Double topPriceBD = 0.0;

            if (!requestedTopPrice.isBlank()) {
                try {
                    topPriceBD = Double.parseDouble(requestedTopPrice);
                } catch (NumberFormatException e) {
                    topPriceBD = 0.0;
                }
            }

            System.out.println("Searching for furniture similar to: " + furnitureDescription);
            furnitureRepository.findBySimilarity(furnitureDescription, topPriceBD, 5).forEach(System.out::println);
            System.out.println("*************************************************");

        }
    }

    private void loadDataFromLLMIfNoData() {
        try {
            System.out.println("Requesting a furniture catalog to: " + chatModel.getClass().getSimpleName());
            FurnitureCatalog furnitureCatalog = ChatClient.create(chatModel)
                    .prompt().advisors(new SimpleLoggerAdvisor()).user(prompt).call().entity(FurnitureCatalog.class);

            furnitureCatalog.furnitureList().forEach(furniture -> {
                System.out.println(furniture);
                furnitureRepository.save(
                        Furniture.builder()
                                .name(furniture.name()).description(furniture.description())
                                .type(furniture.type()).style(furniture.style())
                                .material(furniture.material()).color(furniture.color())
                                .width(furniture.width()).height(furniture.height()).depth(furniture.depth())
                                .price(furniture.price())
                                .build());
            });
            System.out.println("Furniture loaded");
        } catch (Exception e) {
            System.out.println("Error loading furniture: " + e.getMessage());
        }
    }
}
