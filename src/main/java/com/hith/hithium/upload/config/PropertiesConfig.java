package com.hith.hithium.upload.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@Data
public class PropertiesConfig {

    @Value("${ticket.expire.time}")
    private Long ticketExpireTime;

    @Value("${batch.zip.export.path}")
    private String batchDownLoadPath;

    @Value("${word.to.pdf.path}")
    private String wordToPdfPath;

    @Value("${word.license}")
    private String wordLicense;

    @Value("${excel.to.pdf.path}")
    private String excelToPdfPath;

    @Value("${ppt.to.pdf.path}")
    private String pptToPdfPath;
}
