package com.pblues.sportsshop.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class WardListResponse {
    private Integer code;
    private String message;
    private List<WardResponse> data;

    @Data
    public static class WardResponse {
        private String WardCode;
        private Integer DistrictID;
        private String WardName;
        private List<String> NameExtension;
        private Boolean CanUpdateCOD;
        private Integer SupportType;
        private Integer PickType;
        private Integer DeliverType;
        private WhiteListClient WhiteListClient;
        private WhiteListWard WhiteListWard;
        private String GovernmentCode;
        private Integer Status;
        private String ReasonCode;
        private String ReasonMessage;
        private Object OnDates; // có thể null
        private Integer CreatedEmployee;
        private String CreatedSource;
        private String CreatedDate;
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
        public static class WhiteListWard {
            private Object From; // có thể null
            private Object To;   // có thể null
        }
    }
}
