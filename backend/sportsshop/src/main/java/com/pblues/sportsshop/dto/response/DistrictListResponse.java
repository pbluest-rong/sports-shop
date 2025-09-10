package com.pblues.sportsshop.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class DistrictListResponse {
    private Integer code;
    private String message;
    private List<DistrictResponse> data;

    @Data
    public static class DistrictResponse {
        private Integer DistrictID;
        private Integer ProvinceID;
        private String DistrictName;
        private String Code;
        private Integer Type;
        private Integer SupportType;
        private List<String> NameExtension;
        private Integer IsEnable;
        private Long UpdatedBy;
        private String CreatedAt;
        private String UpdatedAt;
        private Boolean CanUpdateCOD;
        private Integer Status;
        private Integer PickType;
        private Integer DeliverType;
        private WhiteListClient WhiteListClient;
        private WhiteListDistrict WhiteListDistrict;
        private String GovernmentCode;
        private String ReasonCode;
        private String ReasonMessage;
        private Object OnDates; // kiểu này GHN trả null -> để Object hoặc String
        private Integer UpdatedEmployee;
        private String UpdatedSource;
        private String UpdatedDate;

        @Data
        public static class WhiteListClient {
            private List<String> From;
            private List<String> To;
            private List<String> Return;
        }

        @Data
        public static class WhiteListDistrict {
            private Object From; // có thể null
            private Object To;   // có thể null
        }
    }
}
