package kopo.poly.service.impl;

import kopo.poly.dto.PapagoDTO;
import kopo.poly.service.INaverAPIService;
import kopo.poly.service.IPapagoService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class PapagoService implements IPapagoService {

    // OpenFeign 정의된 API 인터페이스 가져오기
    private final INaverAPIService naverAPIService;

    @Override
    public PapagoDTO detectLangs(PapagoDTO pDTO) {

        log.info(this.getClass().getName() + ".detectLangs Start!");

        val text = CmmUtil.nvl(pDTO.getText()); // 영작할 문장

        // PapagoAPI 호출하기
        // 결과  예 : {"langCode":"ko"}
        PapagoDTO rDTO = naverAPIService.detectLangs(text);

        // 언어 감지를 위한 원문 저장하기
        rDTO.setText(text);

        log.info(this.getClass().getName() + ".detectLangs End!");

        return rDTO;
    }

    @Override
    public PapagoDTO translate(PapagoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".translate Start!");

        // 언어 종류 찾기, 위에 개발한 public PapagoDTO detectLangs(PapagoDTO pDTO) 호출
        PapagoDTO rDTO = this.detectLangs(pDTO);

        // 찾은 언어 종류
        String langCode = CmmUtil.nvl(rDTO.getLangCode());

        log.info("langCode : " + langCode);

        var source = ""; // 원문 언어(한국어 : ko / 영어 : en)
        var target = ""; // 번역할 언어

        if (langCode.equals("ko")) {
            source = "ko";
            target = "en";

        } else if (langCode.equals("en")) {
            source = "en";
            target = "ko";

        } else {
            // 한국어와 영어가 아니면, 에러 발생시키기
            throw new Exception("한국어와 영어만 번역됩닌다.");
        }

        val text = CmmUtil.nvl(pDTO.getText()); // 번역할 문장

        rDTO = naverAPIService.translate(source, target, text);

        log.info("rDTO : " + rDTO.getMessage().get("result"));

        // 네이버 결과 데이터 구조는 Map구조에 Map 구조에 Map 구조로 3중 Map구조되어 있음
        val result = (Map<String, String>) rDTO.getMessage().get("result");

        val srcLangType = CmmUtil.nvl(result.get("srcLangType"));
        val traLangType = CmmUtil.nvl(result.get("tarLangType"));
        val translatedText = CmmUtil.nvl(result.get("translatedText"));

        log.info("srcLangType : " + srcLangType);
        log.info("traLangType : " + traLangType);
        log.info("translatedText : " + translatedText);

        // API 호출 결과를 기반으로 HTML에서 사용하기 쉽게 새롭게 데이터 구조 정의하기
        rDTO = PapagoDTO.builder().text(text).translatedText(translatedText)
                .srcLangType(srcLangType).traLangType(traLangType).build();

        log.info(this.getClass().getName() + ".translate End!");

        return rDTO;
    }

}