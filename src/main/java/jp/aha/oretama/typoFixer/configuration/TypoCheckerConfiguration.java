package jp.aha.oretama.typoFixer.configuration;

import jp.aha.oretama.typoFixer.language.CodingEnglish;
import jp.aha.oretama.typoFixer.service.BaseDictionaryService;
import org.languagetool.JLanguageTool;
import org.languagetool.rules.Rule;
import org.languagetool.rules.spelling.SpellingCheckRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author aha-oretama
 */
@Configuration
public class TypoCheckerConfiguration {

    @Bean
    @RequestScope
    public JLanguageTool jLanguageTool(BaseDictionaryService dictionaryService) {
        JLanguageTool jLanguageTool = new JLanguageTool(new CodingEnglish());
        // Make all rules disable except SpellingCheckRule.
        List<Rule> allActiveRules = jLanguageTool.getAllActiveRules();
        List<String> ruleIds = allActiveRules.stream().filter(rule -> !(rule instanceof SpellingCheckRule)).map(Rule::getId).collect(Collectors.toList());
        jLanguageTool.disableRules(ruleIds);

        // Set accept phrase
        for (Rule rule : jLanguageTool.getAllActiveRules()) {
            if (rule instanceof SpellingCheckRule) {
                ((SpellingCheckRule) rule).addIgnoreTokens(dictionaryService.getDictionaries());
            }
        }
        return jLanguageTool;
    }
}