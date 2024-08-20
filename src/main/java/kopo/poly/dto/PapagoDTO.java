package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class PapagoDTO {

    private String langCode;
    private String text;

    private String translatedText;
    private String srcLangType;
    private String traLangType;

    private Map<String, Object> message;
}
