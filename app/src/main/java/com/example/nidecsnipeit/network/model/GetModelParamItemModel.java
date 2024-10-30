package com.example.nidecsnipeit.network.model;

import android.net.Uri;

public class GetModelParamItemModel {
    private String limit;
    private String offset;
    private String order;
    private String start_date;
    private String end_date;

    public GetModelParamItemModel(Builder builder) {

        this.limit = builder.limit;
        this.offset = builder.offset;
        this.start_date = builder.start_date;
        this.end_date = builder.end_date;
        this.order = builder.order;
    }

    public static class Builder {
        private String limit;
        private String offset;
        private String order;
        private String start_date;
        private String end_date;


        public GetModelParamItemModel build() {
            return new GetModelParamItemModel(this);

        }

        public Builder setLimit(String limit) {
            this.limit = limit;
            return this;
        }

        public Builder setOffset(String offset) {
            this.offset = offset;
            return this;
        }

        public Builder setOrder(String order) {
            this.order = order;
            return this;
        }

        public Builder setStartDate(String startDate) {
            this.start_date = startDate;
            return this;
        }

        public Builder setEndDate(String endDate) {
            this.end_date = endDate;
            return this;
        }
    }
}
