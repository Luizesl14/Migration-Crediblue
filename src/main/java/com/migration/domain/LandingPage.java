package com.migration.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LandingPage {

    private Integer id;
    private String link;
    private String title;
    private String descriptionMetatag;
    private String codeGoogleTagManager;
    private TemplateStyle templateStyle;
    private Media logo;
    private Media favicon;
}
