package org.inssg.backend.item;

import org.inssg.backend.item.Item;
import org.inssg.backend.item.ItemRepository;
import org.inssg.backend.item.ItemService;
import org.inssg.backend.util.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.inssg.backend.util.ApiDocumentUtils.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class ItemApiTest  extends AcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    Item 프로틴;
    Item 닭가슴살;

    @BeforeEach
     void beforeAll() {
         프로틴 = Item.create("프로틴", "https://unsplash.com/2", 50000);
         닭가슴살 = Item.create("닭가슴살", "https://unsplash.com/1", 30000);

    }
    //TODO: RestDocs 적용
    @Test
    @DisplayName("상품리스트 조회")
    @WithMockUser
    void 상품리스트조회() throws Exception {
        //given
        itemRepository.save(프로틴);
        itemRepository.save(닭가슴살);

        //when
        ResultActions actions = mockMvc.perform(get("/items")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()));
        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].name").value("프로틴"))
                .andExpect(jsonPath("$.[0].price").value(50000))
                .andExpect(jsonPath("$.[1].name").value("닭가슴살"))
                .andExpect(jsonPath("$.[1].price").value(30000))
                .andDo(document(
                                "get-itemList",
                                getRequestPreProcessor(),
                                getResponsePreProcessor(),
                                responseFields(
                                        List.of(
                                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("상품 아이디"),
                                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("상품 이름"),
                                                fieldWithPath("[].imgPath").type(JsonFieldType.STRING).description("상품 이미지 경로"),
                                                fieldWithPath("[].price").type(JsonFieldType.NUMBER).description("상품 가격")
                                        )
                                )

                        )
                );
    }

}
