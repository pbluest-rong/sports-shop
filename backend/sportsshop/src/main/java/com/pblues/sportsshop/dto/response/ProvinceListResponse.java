package com.pblues.sportsshop.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ProvinceListResponse {
    private Integer code;
    private String message;
    private List<ProvinceResponse> data;

    @Data
    public static class ProvinceResponse {
        private Integer ProvinceID;
        private String ProvinceName;
        private Integer CountryID;
        private String Code;
        private List<String> NameExtension;
        private Integer IsEnable;
        private Integer RegionID;
        private Integer RegionCPN;
        private Long UpdatedBy;
        private String CreatedAt;
        private String UpdatedAt;
        private Integer AreaID;
        private Boolean CanUpdateCOD;
        private Integer Status;
        private Integer UpdatedEmployee;
        private String UpdatedSource;
        private String UpdatedDate;
    }
}
