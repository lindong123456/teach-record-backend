package com.teachrecord.web.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class StudentViewJacksonTest {

    @Test
    void serializesParentPasswordWhenPresent() throws Exception {
        ObjectMapper om = new ObjectMapper();
        StudentDtos.StudentView v =
                new StudentDtos.StudentView(1L, "n", "u", BigDecimal.TEN, "P3", "secretPlain");
        String json = om.writeValueAsString(v);
        assertThat(json).contains("\"parentPassword\":\"secretPlain\"");
        assertThat(json).contains("\"gradeLevel\":\"P3\"");
    }

    @Test
    void alwaysIncludesParentPasswordKeyWhenNull() throws Exception {
        ObjectMapper om = new ObjectMapper();
        StudentDtos.StudentView v =
                new StudentDtos.StudentView(1L, "n", "u", BigDecimal.TEN, "P1", null);
        String json = om.writeValueAsString(v);
        assertThat(json).contains("\"parentPassword\":null");
        assertThat(json).contains("\"gradeLevel\":\"P1\"");
    }
}
